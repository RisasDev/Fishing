package dev.risas.sukagepond.models.webhook;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Getter
public class WebhookEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final String id;
    private final Map<String, Object> data;

    public WebhookEvent(String id, Map<String, Object> data) {
        this.id = id;
        this.data = data;
    }

    public WebhookEvent(String id) {
        this.id = id;
        this.data = null;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
