package dev.risas.sukagepond.models.webhook.types;

import dev.risas.sukagepond.SukagePond;
import dev.risas.sukagepond.controllers.PondController;
import dev.risas.sukagepond.models.webhook.Webhook;
import dev.risas.sukagepond.utilities.file.FileConfig;
import dev.risas.sukagepond.utilities.webhook.DiscordWebhook;

import java.awt.*;
import java.util.Map;

public class PondEventWebhook extends Webhook {

    private final PondController pondController;

    public PondEventWebhook(SukagePond plugin, String path, FileConfig webhookFile) {
        super(path, webhookFile);
        this.pondController = plugin.getPondController();
    }

    @Override
    public void sendWebhook(Map<String, Object> data) {
        DiscordWebhook webhook = new DiscordWebhook(super.getUrl());
        DiscordWebhook.EmbedObject embedObject = new DiscordWebhook.EmbedObject();

        embedObject.setTitle(super.getTitle());

        embedObject.setDescription(embedObject.buildDescription(super.getDescription())
                .replace("<duration>", pondController.getDuration()));

        embedObject.setColor(Color.decode(super.getColor()));
        webhook.addEmbed(embedObject);

        try {
            webhook.execute();
        } catch (Exception e) {
            throw new RuntimeException("Error while sending webhook", e);
        }
    }
}
