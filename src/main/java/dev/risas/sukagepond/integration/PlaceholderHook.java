package dev.risas.sukagepond.integration;

import dev.risas.sukagepond.SukagePond;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

@UtilityClass
public class PlaceholderHook {

    public void initialize(SukagePond plugin) {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            PlaceholderExpansion papi = new PlaceholderExpansion(plugin);
            if (!papi.isRegistered()) papi.register();
        }
    }
}
