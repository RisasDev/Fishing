package dev.risas.sukagepond.integration;

import dev.risas.sukagepond.SukagePond;
import dev.risas.sukagepond.controllers.PondController;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderExpansion extends me.clip.placeholderapi.expansion.PlaceholderExpansion {

    private final SukagePond plugin;
    private final PondController pondController;

    public PlaceholderExpansion(SukagePond plugin) {
        this.plugin = plugin;
        this.pondController = plugin.getPondController();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getAuthor() {
        return "Risas";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "skpond";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) return null;

        boolean started = pondController.isStarted();

        return switch (identifier) {
            case "nextpond" -> started ? "Ahora" : pondController.getPondStartTask().getCountdownRemaining();
            case "runningpond" -> started ? "true" : "false";
            case "pondtimer" -> pondController.getDuration();
            case "playersneeded" -> String.valueOf(pondController.getPlayersNeeded());
            default -> null;
        };
    }
}
