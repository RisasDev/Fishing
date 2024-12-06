package dev.risas.sukagepond;

import dev.risas.sukagepond.commands.SKPondCommand;
import dev.risas.sukagepond.controllers.PondController;
import dev.risas.sukagepond.controllers.WebhookController;
import dev.risas.sukagepond.integration.PlaceholderHook;
import dev.risas.sukagepond.listeners.PondListener;
import dev.risas.sukagepond.utilities.file.FileConfig;
import lombok.Getter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.Random;

@Getter
public class SukagePond extends JavaPlugin {

    private Random random;
    private FileConfig configFile, webhookFile;
    private PondController pondController;
    private WebhookController webhookController;

    @Override
    public void onEnable() {
        this.random = new Random();

        this.configFile = new FileConfig(this, "config.yml");
        this.webhookFile = new FileConfig(this, "webhook.yml");
        this.pondController = new PondController(this);
        this.pondController.startPondStartTask();
        this.webhookController = new WebhookController(this);

        Objects.requireNonNull(this.getCommand("skpond")).setExecutor(new SKPondCommand(this));
        Objects.requireNonNull(this.getCommand("skpond")).setTabCompleter(new SKPondCommand(this));

        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new PondListener(this), this);

        PlaceholderHook.initialize(this);
    }

    @Override
    public void onDisable() {
        this.pondController.onDisable();
    }

    public void onReload() {
        this.configFile.reload();
        this.pondController.onReload();
    }
}
