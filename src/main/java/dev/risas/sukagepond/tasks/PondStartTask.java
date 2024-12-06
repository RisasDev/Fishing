package dev.risas.sukagepond.tasks;

import dev.risas.sukagepond.SukagePond;
import dev.risas.sukagepond.controllers.PondController;
import dev.risas.sukagepond.utilities.TimeUtil;
import dev.risas.sukagepond.utilities.chat.ChatUtil;
import dev.risas.sukagepond.utilities.file.FileConfig;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

public class PondStartTask extends BukkitRunnable {

    private final SukagePond plugin;
    private final FileConfig configFile;
    private final PondController pondController;

    private int countdown;

    public PondStartTask(SukagePond plugin) {
        this.plugin = plugin;
        this.configFile = plugin.getConfigFile();
        this.pondController = plugin.getPondController();
        this.countdown = TimeUtil.formatInt(configFile.getString("pond-settings.every"));
    }

    @Override
    public void run() {
        if (countdown > 0) {
            countdown--;
            return;
        }

        if (Bukkit.getOnlinePlayers().size() < pondController.getPlayersNeeded()) {
            configFile.getStringList("pond-message.no-minimum-players")
                    .forEach(message -> ChatUtil.broadcast(message
                        .replace("%players-needed%", String.valueOf(pondController.getPlayersNeeded()))));

            pondController.startPondStartTask();
        }
        else {
            pondController.startPondEvent();
        }
    }

    public void start() {
        this.runTaskTimer(this.plugin, 20L, 20L);
    }

    public String getCountdownRemaining() {
        long hours = TimeUnit.SECONDS.toHours(countdown),
                minutes = TimeUnit.SECONDS.toMinutes(countdown) - TimeUnit.HOURS.toMinutes(hours),
                seconds = countdown - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(countdown));

        if (hours == 0) {
            return minutes > 0 ?
                    String.format("%02d:%02d", minutes, seconds) :
                    String.format("%02d", seconds) + "s";
        }
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
