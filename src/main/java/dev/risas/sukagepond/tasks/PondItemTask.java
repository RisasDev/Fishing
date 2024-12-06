package dev.risas.sukagepond.tasks;

import dev.risas.sukagepond.SukagePond;
import dev.risas.sukagepond.controllers.PondController;
import dev.risas.sukagepond.models.PondItem;
import dev.risas.sukagepond.utilities.chat.ChatUtil;
import dev.risas.sukagepond.utilities.item.ItemUtil;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class PondItemTask extends BukkitRunnable {

    private final SukagePond plugin;
    private final PondController pondController;

    private final Location location;
    private int countdown;

    private final Set<Item> items;

    public PondItemTask(SukagePond plugin, Location location) {
        this.plugin = plugin;
        this.pondController = plugin.getPondController();
        this.location = location;
        this.countdown = 3 * 20;
        this.items = new HashSet<>();
    }

    @Override
    public void run() {
        World world = this.location.getWorld();

        if (world == null) {
            this.cancel();
            return;
        }

        if (this.countdown <= 0) {
            this.cancel();

            PondItem pondItem = pondController.getRandomPondItem();
            if (pondItem == null) return;

            ItemStack itemStack = pondItem.getItemStack().clone();
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta == null) return;

            Item item = world.dropItem(location, itemStack);
            item.setPickupDelay(1000);

            item.setCustomName(ChatUtil.translate(ItemUtil.getCustomDisplayName(itemStack)));
            item.setCustomNameVisible(true);

            this.items.add(item);

            world.getNearbyEntities(location, 1, 1, 1)
                    .forEach(entity -> entity.setVelocity(new Vector(0, 1, 0)));

            Bukkit.getScheduler().runTaskLater(plugin, item::remove, 20L * 3L);
            return;
        }

        if (this.countdown % 3 == 0) {
            world.spawnParticle(Particle.FIREWORKS_SPARK, this.location, 5, 0.1, 0.1, 0.1, 0.1);
        }

        this.countdown--;
    }

    public void onDisable() {
        this.items.forEach(Item::remove);
    }
}
