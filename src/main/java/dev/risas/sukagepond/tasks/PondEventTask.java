package dev.risas.sukagepond.tasks;

import dev.risas.sukagepond.SukagePond;
import dev.risas.sukagepond.controllers.PondController;
import dev.risas.sukagepond.utilities.TimeUtil;
import dev.risas.sukagepond.utilities.chat.ChatUtil;
import dev.risas.sukagepond.utilities.file.FileConfig;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class PondEventTask extends BukkitRunnable {

    private final SukagePond plugin;
    private final FileConfig configFile;
    private final PondController pondController;

    private int countdown;
    private final Set<PondItemTask> pondItemTasks;

    public PondEventTask(SukagePond plugin, String duration) {
        this.plugin = plugin;
        this.configFile = plugin.getConfigFile();
        this.pondController = plugin.getPondController();
        this.countdown = TimeUtil.formatInt(duration);
        this.pondItemTasks = new HashSet<>();
    }

    @Override
    public void run() {
        if (this.countdown <= 0) {
            pondController.stopPondEventTask();
            pondController.startPondStartTask();
            configFile.getStringList("pond-message.ended").forEach(ChatUtil::broadcast);
            return;
        }

        if (this.countdown % 3 == 0) {
            pondController.getRandomLocations(configFile.getInt("pond-settings.max")).forEach(location -> {
                PondItemTask pondItemTask = new PondItemTask(plugin, location);
                pondItemTask.runTaskTimer(plugin, 0L, 1L);

                this.pondItemTasks.add(pondItemTask);
            });
        }

        this.countdown--;
    }

    public void start() {
        this.runTaskTimer(this.plugin, 20L, 20L);
    }

    public void onDisable() {
        this.pondItemTasks.forEach(PondItemTask::onDisable);
    }
}
