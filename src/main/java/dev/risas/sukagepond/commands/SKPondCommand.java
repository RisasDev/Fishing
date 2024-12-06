package dev.risas.sukagepond.commands;

import dev.risas.sukagepond.SukagePond;
import dev.risas.sukagepond.commands.subcommands.SKPondItemCommand;
import dev.risas.sukagepond.controllers.PondController;
import dev.risas.sukagepond.utilities.chat.ChatUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class SKPondCommand implements CommandExecutor, TabCompleter {

    private final SukagePond plugin;
    private final PondController pondController;

    public SKPondCommand(SukagePond plugin) {
        this.plugin = plugin;
        this.pondController = plugin.getPondController();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendMessage(sender, new String[]{
                    ChatUtil.NORMAL_LINE,
                    "&6&lSukagePond Commands",
                    "",
                    " &8● &f/" + label + " &7start &8- &fInicia el evento.",
                    " &8● &f/" + label + " &7location [clear] &8- &fObten la herramienta de localización.",
                    " &8● &f/" + label + " &7item &8- &fMuestra los comandos de item.",
                    " &8● &f/" + label + " &7reload &8- &fRecarga la configuración del plugin.",
                    ChatUtil.NORMAL_LINE
            });
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "start": {
                if (pondController.isStarted()) {
                    ChatUtil.sendMessage(sender, "&cEl evento ya ha sido iniciado.");
                    return false;
                }

                pondController.startPondEvent();
                break;
            }
            case "tp": {
                Player player = (Player) sender;
                Location location = new Location(
                        player.getWorld(),
                        Double.parseDouble(args[1]),
                        Double.parseDouble(args[2]),
                        Double.parseDouble(args[3])
                );

                player.teleport(location);
                break;
            }
            case "location": {
                if (args.length == 2 && args[1].equalsIgnoreCase("clear")) {
                    pondController.clearLocations();
                    ChatUtil.sendMessage(sender, "&aSe han eliminado todas las localizaciones.");
                    return false;
                }

                Player player = (Player) sender;
                player.setGameMode(GameMode.CREATIVE);
                player.getInventory().addItem(pondController.getLocationWand());
                ChatUtil.sendMessage(player, "&aHas recibido la herramienta de localización.");
                break;
            }
            case "item":
                new SKPondItemCommand(plugin).onCommand(sender, command, label, args);
                break;
            case "reload":
                plugin.onReload();
                ChatUtil.sendMessage(sender, "&aSukagePond se ha recargado correctamente.");
                break;
            default:
                ChatUtil.sendMessage(sender, "&cComando no encontrado. Usa /" + label + " para ver los comandos disponibles.");
                break;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return List.of("start", "location", "item", "reload");
        }
        else if (args.length == 2 && args[0].equalsIgnoreCase("item")) {
            return List.of("add", "command", "list", "clear");
        }
        else if (args.length == 3 && args[0].equalsIgnoreCase("item") && args[1].equalsIgnoreCase("command")) {
            return List.of("add", "remove", "list");
        }
        return null;
    }
}
