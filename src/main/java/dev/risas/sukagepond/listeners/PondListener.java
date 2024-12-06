package dev.risas.sukagepond.listeners;

import dev.risas.sukagepond.SukagePond;
import dev.risas.sukagepond.controllers.PondController;
import dev.risas.sukagepond.models.PondItem;
import dev.risas.sukagepond.utilities.chat.ChatUtil;
import dev.risas.sukagepond.utilities.file.FileConfig;
import dev.risas.sukagepond.utilities.item.ItemUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PondListener implements Listener {

    private final FileConfig configFile;
    private final PondController pondController;

    public PondListener(SukagePond plugin) {
        this.configFile = plugin.getConfigFile();
        this.pondController = plugin.getPondController();
    }

    @EventHandler
    private void onPondLocationWand(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (!pondController.isLocationWand(item)) return;

        event.setCancelled(true);

        Player player = event.getPlayer();
        Action action = event.getAction();

        if (action == Action.LEFT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (block == null) return;

            Location location = block.getLocation();
            boolean removed = player.isSneaking();

            if (pondController.isLocation(location) && !removed) {
                ChatUtil.sendMessage(player, "&cYa existe una localización en este bloque.");
                return;
            }
            else if (removed && !pondController.isLocation(location)) {
                ChatUtil.sendMessage(player, "&cNo hay ninguna localización en este bloque.");
                return;
            }

            pondController.updateLocation(location, removed);

            player.playSound(player.getLocation(), removed ? Sound.ENTITY_ARROW_HIT_PLAYER : Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            ChatUtil.sendMessage(player, removed ? "&cSe ha eliminado la localización." : "&aSe ha establecido la localización.");
        }
        else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            ChatUtil.sendMessage(player, ChatUtil.NORMAL_LINE);
            ChatUtil.sendMessage(player, "&6&lLocalizaciones");

            pondController.getLocations().forEach(location -> {
                int x = location.getBlockX(), y = location.getBlockY(), z = location.getBlockZ();

                TextComponent textComponent = new TextComponent(ChatUtil.translate(" &8● &f" + x + ", " + y + ", " + z + " &a&l[TP]"));
                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/skpond tp " + x + " " + y + " " + z));
                textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatUtil.translate("&7Haz clic para teletransportarte."))));

                ChatUtil.sendMessageComponent(player, textComponent);
            });

            ChatUtil.sendMessage(player, ChatUtil.NORMAL_LINE);
        }
    }

    @EventHandler
    private void onPondItemFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_ENTITY) return;
        if (!(event.getCaught() instanceof Item item)) return;

        ItemStack itemStack = item.getItemStack();
        PondItem pondItem = pondController.getItemByItemStack(itemStack);
        if (pondItem == null) return;

        item.remove();

        Player player = event.getPlayer();
        pondItem.giveReward(player);

        String playerName = player.getName();
        String customName = ChatUtil.translate(ItemUtil.getCustomDisplayName(itemStack));

        configFile.getStringList("pond-message.captured")
                .forEach(message -> ChatUtil.sendMessage(player, message
                        .replace("%player%", playerName)
                        .replace("%item%", customName)));
    }
}
