package dev.risas.sukagepond.models.webhook;

import dev.risas.sukagepond.utilities.file.FileConfig;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public abstract class Webhook {

    private final String path;
    private final boolean enabled;
    private final String url;
    private final String title;
    private final List<String> description;
    private final String color;

    protected Webhook(String path, FileConfig webhookFile) {
        this.path = path;
        this.enabled = webhookFile.getBoolean("webhooks." + path + ".enabled");
        this.url = webhookFile.getString("webhooks." + path + ".url");
        this.title = webhookFile.getString("webhooks." + path + ".title");
        this.description = webhookFile.getStringList("webhooks." + path + ".description");
        this.color = webhookFile.getString("webhooks." + path + ".color");
    }

    public abstract void sendWebhook(Map<String, Object> data);
}
