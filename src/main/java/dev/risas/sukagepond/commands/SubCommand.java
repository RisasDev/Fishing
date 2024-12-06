package dev.risas.sukagepond.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface SubCommand {

    void onCommand(CommandSender sender, Command command, String label, String[] args);
}
