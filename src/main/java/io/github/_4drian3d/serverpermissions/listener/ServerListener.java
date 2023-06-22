package io.github._4drian3d.serverpermissions.listener;

import com.google.inject.Inject;
import com.velocitypowered.api.event.*;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.proxy.Player;
import io.github._4drian3d.serverpermissions.ServerPermissions;
import io.github.miniplaceholders.api.MiniPlaceholders;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

public final class ServerListener implements AwaitingEventExecutor<ServerPreConnectEvent> {
    @Inject
    private ServerPermissions plugin;
    @Inject
    private PluginManager pluginManager;
    @Inject
    private EventManager eventManager;

    public void register() {
        this.eventManager.register(plugin, ServerPreConnectEvent.class, this);
    }

    @Override
    public EventTask executeAsync(ServerPreConnectEvent event) {
        return EventTask.withContinuation(continuation -> {
            if (!event.getResult().isAllowed()) {
                continuation.resume();
                return;
            }

            event.getResult().getServer().ifPresent(server -> {
                final Player player = event.getPlayer();
                final String serverName = server.getServerInfo().getName();

                // Permission Check
                if (player.hasPermission("serverpermissions.server." + serverName)) {
                    continuation.resume();
                    return;
                }

                event.setResult(ServerPreConnectEvent.ServerResult.denied());

                // In case the server to which the player is connecting is the initial one,
                // it is not necessary to send the message as the player will not see it
                if (event.getPreviousServer() == null) {
                    continuation.resume();
                    return;
                }

                final String noPermissionMessage = plugin.configuration().noPermissionMessage();
                // If the message is empty, it avoids sending to the player
                if (noPermissionMessage.isBlank()) {
                    continuation.resume();
                    return;
                }

                final TagResolver.Builder builder = TagResolver.builder()
                        .resolver(Placeholder.unparsed("server", serverName));

                // MiniPlaceholders custom placeholders support
                if (pluginManager.isLoaded("miniplaceholders")) {
                    builder.resolver(MiniPlaceholders.getAudienceGlobalPlaceholders(player));
                }

                final Component message = miniMessage().deserialize(noPermissionMessage, builder.build());
                player.sendMessage(message);
                continuation.resume();
            });
        });
    }
}
