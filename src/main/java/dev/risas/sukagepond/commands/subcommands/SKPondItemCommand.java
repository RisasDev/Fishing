package dev.risas.sukagepond.commands.subcommands;

import dev.risas.sukagepond.SukagePond;
import dev.risas.sukagepond.commands.SubCommand;
import dev.risas.sukagepond.controllers.PondController;
import dev.risas.sukagepond.models.PondItem;
import dev.risas.sukagepond.utilities.JavaUtil;
import dev.risas.sukagepond.utilities.chat.ChatUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SKPondItemCommand implements SubCommand {

    private final PondController pondController;

    public SKPondItemCommand(SukagePond plugin) {
        this.pondController = plugin.getPondController();
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            ChatUtil.sendMessage(sender, new String[]{
                    ChatUtil.NORMAL_LINE,
                    "&6&lSukagePond Item Commands",
                    "",
                    " &8● &f/" + label + " &7item add &8- &fAñade un item.",
                    " &8● &f/" + label + " &7item command &8- &fMostar las opciones de comandos de item.",
                    " &8● &f/" + label + " &7item list &8- &fMuestra todos los items.",
                    " &8● &f/" + label + " &7item clear &8- &fElimina todos los items.",
                    ChatUtil.NORMAL_LINE
            });
            return;
        }

        if (!(sender instanceof Player player)) {
            ChatUtil.sendMessage(sender, "&cNo puedes ejecutar este comando desde la consola.");
            return;
        }

        switch (args[1].toLowerCase()) {
            case "add": {
                ItemStack itemStack = player.getInventory().getItemInMainHand();

                if (itemStack.getType() == Material.AIR) {
                    ChatUtil.sendMessage(player, "&cDebes tener un item en la mano.");
                    return;
                }

                pondController.addPondItem(itemStack);
                ChatUtil.sendMessage(player, "&aSe ha añadido el item correctamente.");
                break;
            }
            case "give": {
                if (args.length < 4) {
                    ChatUtil.sendMessage(sender, "&cUsage: /" + label + " item give [id] [player]");
                    return;
                }

                String targetName = args[3];
                Player target = Bukkit.getPlayer(targetName);

                if (target == null) {
                    ChatUtil.sendMessage(player, "&cPlayer '" + targetName + "' no encontrado.");
                    return;
                }

                String pondItemId = args[2];
                PondItem pondItem = pondController.getPondItemById(pondItemId);

                if (pondItem == null) {
                    ChatUtil.sendMessage(player, "&cPondItem '" + pondItemId + "' no encontrado.");
                    return;
                }

                target.getInventory().addItem(pondItem.getItemStack());
                ChatUtil.sendMessage(player, "&aSe ha dado el item correctamente.");
                break;
            }
            case "command": {
                if (args.length < 3) {
                    ChatUtil.sendMessage(sender, new String[]{
                            ChatUtil.NORMAL_LINE,
                            "&6&lSukagePond Item Commands",
                            "",
                            " &8● &f/" + label + " &7item command add <command> &8- &fAñade un comando a un item.",
                            " &8● &f/" + label + " &7item command remove <index> &8- &fElimina un comando de un item.",
                            " &8● &f/" + label + " &7item command list &8- &fMuestra todos los comandos de un item.",
                            ChatUtil.NORMAL_LINE
                    });
                    return;
                }

                ItemStack itemStack = player.getInventory().getItemInMainHand();

                if (itemStack.getType() == Material.AIR) {
                    ChatUtil.sendMessage(player, "&cDebes tener un item en la mano.");
                    return;
                }

                PondItem pondItem = pondController.getItemByItemStack(itemStack);

                if (pondItem == null) {
                    ChatUtil.sendMessage(player, "&cPondItem no encontrado.");
                    return;
                }

                switch (args[2].toLowerCase()) {
                    case "add": {
                        if (args.length < 4) {
                            ChatUtil.sendMessage(sender, "&cUsage: /" + label + " item command add <command>");
                            return;
                        }

                        String pondItemCommand = JavaUtil.join(args, ' ', 3, args.length);
                        pondItem.addCommand(pondItemCommand);

                        ChatUtil.sendMessage(player, "&aSe ha añadido el comando correctamente.");
                        break;
                    }
                    case "remove": {
                        if (args.length < 4) {
                            ChatUtil.sendMessage(sender, "&cUsage: /" + label + " item command remove <index>");
                            return;
                        }

                        String indexString = args[3];
                        Integer index = JavaUtil.tryParseInt(indexString);

                        if (index == null) {
                            ChatUtil.sendMessage(player, "&cEl índice '" + indexString + "' no es un número.");
                            return;
                        }

                        pondItem.removeCommand(player, index);
                        break;
                    }
                    case "list": {
                        ChatUtil.sendMessage(player, new String[]{
                                ChatUtil.NORMAL_LINE,
                                "&6&lComandos de " + pondItem.getId(),
                                ""
                        });

                        for (int i = 0; i < pondItem.getCommands().size(); i++) {
                            ChatUtil.sendMessage(player, " &8● &7[" + i + "] &f" + pondItem.getCommands().get(i));
                        }

                        ChatUtil.sendMessage(player, ChatUtil.NORMAL_LINE);
                        break;
                    }
                    default:
                        ChatUtil.sendMessage(sender, "&cUsa /" + label + " item command para ver los comandos.");
                        break;
                }
                break;
            }
            case "remove": {
                if (args.length < 3) {
                    ChatUtil.sendMessage(sender, "&cUsage: /" + label + " item remove [id]");
                    return;
                }

                String pondItemId = args[2];
                PondItem pondItem = pondController.getPondItemById(pondItemId);

                if (pondItem == null) {
                    ChatUtil.sendMessage(player, "&cPondItem '" + pondItemId + "' no encontrado.");
                    return;
                }

                pondController.savePondItem(pondItem, true);
                ChatUtil.sendMessage(player, "&aSe ha eliminado el item correctamente.");
                break;
            }
            case "list":
                ChatUtil.sendMessage(player, new String[]{
                        ChatUtil.NORMAL_LINE,
                        "&6&lItems",
                        ""
                });

                pondController.getItems().forEach((id, item) -> {
                    TextComponent mainMessage = new TextComponent(ChatUtil.translate(" &8● &f" + id + " - " + item.getItemStack().getType().name() + " &7(" + item.getCommands().size() + " comandos)"));

                    TextComponent giveComponent = new TextComponent(ChatUtil.translate(" &a&l[GIVE]"));
                    giveComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " item give " + id + " " + player.getName()));
                    giveComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatUtil.translate("&7Haz clic para dar item."))));

                    TextComponent removeComponent = new TextComponent(ChatUtil.translate(" &c&l[REMOVE]"));
                    removeComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " item remove " + id));
                    removeComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatUtil.translate("&7Haz clic para eliminar item."))));

                    mainMessage.addExtra(giveComponent);
                    mainMessage.addExtra(removeComponent);

                    ChatUtil.sendMessageComponent(player, mainMessage);
                });

                ChatUtil.sendMessage(player, ChatUtil.NORMAL_LINE);
                break;
            case "clear":
                pondController.clearPondItems();
                ChatUtil.sendMessage(player, "&aSe han eliminado todos los items.");
                break;
            default:
                ChatUtil.sendMessage(sender, "&cUsa /" + label + " item para ver los comandos.");
                break;
        }
    }
}
