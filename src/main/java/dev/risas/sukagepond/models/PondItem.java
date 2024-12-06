package dev.risas.sukagepond.models;

import dev.risas.sukagepond.utilities.chat.ChatUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class PondItem {

    private final String id;
    private final ItemStack itemStack;
    private final List<String> commands;

    public PondItem(String id, ItemStack itemStack, List<String> commands) {
        this.id = id;
        this.itemStack = itemStack;
        this.commands = commands;
    }

    public PondItem(String id, ItemStack itemStack) {
        this.id = id;
        this.itemStack = itemStack;
        this.commands = new ArrayList<>();
    }

    public void addCommand(String command) {
        commands.add(command);
    }

    public void removeCommand(Player player, int index) {
        try {
            commands.remove(index);
            ChatUtil.sendMessage(player, "&aSe ha eliminado el comando correctamente.");
        }
        catch (IndexOutOfBoundsException e) {
            ChatUtil.sendMessage(player, "&cNo se ha encontrado ningún comando con el índice " + index + ".");
        }
    }

    public void executeCommands(Player player) {
        commands.forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
                .replace("%player%", player.getName())));
    }

    public void giveReward(Player player) {
        if (commands.isEmpty()) {
            player.getInventory().addItem(itemStack);
        }
        else {
            executeCommands(player);
        }
    }
}
