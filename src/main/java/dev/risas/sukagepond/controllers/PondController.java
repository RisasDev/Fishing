package dev.risas.sukagepond.controllers;

import dev.risas.sukagepond.SukagePond;
import dev.risas.sukagepond.models.PondItem;
import dev.risas.sukagepond.models.webhook.WebhookEvent;
import dev.risas.sukagepond.tasks.PondEventTask;
import dev.risas.sukagepond.tasks.PondStartTask;
import dev.risas.sukagepond.utilities.JavaUtil;
import dev.risas.sukagepond.utilities.SerializeUtil;
import dev.risas.sukagepond.utilities.chat.ChatUtil;
import dev.risas.sukagepond.utilities.file.FileConfig;
import dev.risas.sukagepond.utilities.item.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

@Getter @Setter
public class PondController {

    private final SukagePond plugin;
    private final FileConfig configFile;

    private final ItemStack locationWand;
    private String duration;
    private int playersNeeded;

    private final List<Location> locations;
    private final Map<String, PondItem> items;
    private PondStartTask pondStartTask;
    private PondEventTask pondEventTask;

    public PondController(SukagePond plugin) {
        this.plugin = plugin;
        this.configFile = plugin.getConfigFile();
        this.locationWand = new ItemBuilder(Material.BLAZE_ROD)
                .setName("&6&lHerramienta de localización")
                .setLore(
                        " &8● &7Haz &fclic izquierdo &7para seleccionar una localización.",
                        " &8● &7Haz &fclic derecho &7para ver las localizaciones.",
                        " &8● &7Haz &fshift + clic izquierdo &7para eliminar una localización."
                )
                .setEnchanted(true)
                .addNBT(plugin, "location-wand", "true")
                .build();
        this.locations = SerializeUtil.deserializeBlockLocations(configFile.getStringList("pond-locations"));
        this.items = new HashMap<>();
        this.onReload();
    }

    public void addPondItem(ItemStack itemStack) {
        ItemStack clone = itemStack.clone();
        ItemMeta itemMeta = clone.getItemMeta();
        if (itemMeta == null) return;

        String id = JavaUtil.randomAlphaNumeric(5);
        itemMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "pond-item-id"), PersistentDataType.STRING, id);
        clone.setItemMeta(itemMeta);

        PondItem item = new PondItem(id, clone);
        savePondItem(item, false);
    }

    public PondItem getPondItemById(String id) {
        return this.items.get(id);
    }

    public PondItem getItemByItemStack(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return null;

        return this.items.get(itemMeta.getPersistentDataContainer().get(new NamespacedKey(plugin, "pond-item-id"), PersistentDataType.STRING));
    }

    public PondItem getRandomPondItem() {
        if (this.items.isEmpty()) return null;

        List<PondItem> values = new ArrayList<>(this.items.values());
        return values.get(plugin.getRandom().nextInt(values.size()));
    }

    public void savePondItem(PondItem item, boolean remove) {
        String id = item.getId();

        ConfigurationSection section = this.configFile.getConfiguration().getConfigurationSection("pond-rewards");
        if (section == null) throw new NullPointerException("Configuration 'pond-rewards' not found.");

        if (remove) {
            section.set(id, null);
            this.items.remove(id);
        }
        else {
            section.set(id + ".item", SerializeUtil.serializeItemStack(item.getItemStack()));
            section.set(id + ".commands", item.getCommands());
            this.items.put(id, item);
        }

        this.configFile.save();
    }

    public void clearPondItems() {
        items.clear();

        configFile.getConfiguration().set("pond-rewards", new HashMap<>());
        configFile.save();
    }

    public boolean isLocationWand(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() != Material.BLAZE_ROD) return false;

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return false;

        return itemMeta.getPersistentDataContainer().has(new NamespacedKey(plugin, "location-wand"), PersistentDataType.STRING);
    }

    public void startPondStartTask() {
        if (this.pondStartTask != null) this.pondStartTask.cancel();

        this.pondStartTask = new PondStartTask(plugin);
        this.pondStartTask.start();
    }

    public void stopPondStartTask() {
        if (this.pondStartTask != null) this.pondStartTask.cancel();
        this.pondStartTask = null;
    }

    public boolean isStarted() {
        return pondStartTask == null;
    }

    public void startPondEvent() {
        configFile.getStringList("pond-message.started")
                .forEach(message -> ChatUtil.broadcast(message
                        .replace("%time%", duration)));
        stopPondStartTask();
        startPondEventTask();
    }

    public void startPondEventTask() {
        if (this.pondEventTask != null) this.pondEventTask.cancel();

        this.pondEventTask = new PondEventTask(plugin, duration);
        this.pondEventTask.start();

        Bukkit.getPluginManager().callEvent(new WebhookEvent("pond-event"));
    }

    public void stopPondEventTask() {
        if (this.pondEventTask != null) this.pondEventTask.cancel();
        this.pondEventTask = null;
    }

    public List<Location> getRandomLocations(int amount) {
        List<Location> locations = new ArrayList<>(this.locations);
        if (amount > locations.size()) return locations;

        Collections.shuffle(locations, plugin.getRandom());
        return locations.subList(0, amount);
    }

    public boolean isLocation(Location location) {
        return locations.contains(location);
    }

    public void updateLocation(Location location, boolean remove) {
        if (remove) locations.remove(location);
        else locations.add(location);

        configFile.getConfiguration().set("pond-locations", SerializeUtil.serializeBlockLocations(locations));
        configFile.save();
    }

    public void clearLocations() {
        locations.clear();

        configFile.getConfiguration().set("pond-locations", new ArrayList<>());
        configFile.save();
    }

    public void onReload() {
        this.duration = configFile.getString("pond-settings.duration");
        this.playersNeeded = configFile.getInt("pond-settings.minimun-players");
        this.items.clear();

        ConfigurationSection section = configFile.getConfiguration().getConfigurationSection("pond-rewards");
        if (section == null) return;

        section.getKeys(false).forEach(key -> {
            ConfigurationSection itemSection = section.getConfigurationSection(key);
            if (itemSection == null) return;

            ItemStack item = SerializeUtil.deserializeItemStack(itemSection.getString("item"));
            if (item == null) return;

            PondItem pondItem = new PondItem(key, item, itemSection.getStringList("commands"));
            this.items.put(key, pondItem);
        });
    }

    public void onDisable() {
        if (this.pondEventTask != null) this.pondEventTask.onDisable();
    }
}
