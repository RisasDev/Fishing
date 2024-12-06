package dev.risas.sukagepond.controllers;

import dev.risas.sukagepond.SukagePond;
import dev.risas.sukagepond.models.webhook.Webhook;
import dev.risas.sukagepond.models.webhook.types.PondEventWebhook;
import dev.risas.sukagepond.utilities.file.FileConfig;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class WebhookController {

    private final SukagePond plugin;
    private final FileConfig webhookFile;

    @Getter
    private final Map<String, Webhook> webhooks;

    public WebhookController(SukagePond plugin) {
        this.plugin = plugin;
        this.webhookFile = plugin.getWebhookFile();
        this.webhooks = new HashMap<>();
        this.onRefresh();
    }

    public void onRefresh() {
        webhooks.clear();
        webhooks.put("pond-event", new PondEventWebhook(plugin, "pond-event", webhookFile));
    }
}
