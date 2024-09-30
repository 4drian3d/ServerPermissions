package io.github._4drian3d.serverpermissions.listener;

import com.google.inject.Inject;
import com.velocitypowered.api.event.*;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.github._4drian3d.serverpermissions.ServerPermissions;
import io.github.miniplaceholders.api.MiniPlaceholders;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

public final class ServerListener implements AwaitingEventExecutor<ServerPreConnectEvent> {
    private final ServerPermissions plugin;
    private final EventManager eventManager;
    private final Logger logger;
    private final boolean hasMiniPlaceholders;

    @Inject
    public ServerListener(
            final ServerPermissions plugin,
            final PluginManager pluginManager,
            final EventManager eventManager,
            final Logger logger
    ) {
        this.plugin = plugin;
        this.eventManager = eventManager;
        this.logger = logger;
        this.hasMiniPlaceholders = pluginManager.isLoaded("miniplaceholders");
    }

    public void register() {
        this.eventManager.register(plugin, ServerPreConnectEvent.class, this);
    }

    @Override
    public EventTask executeAsync(final ServerPreConnectEvent event) {
        return EventTask.async(() -> {
            if (!event.getResult().isAllowed()) {
                return;
            }

            // The check is already done in the #isAllowed() method
            // noinspection OptionalGetWithoutIsPresent
            final RegisteredServer server = event.getResult().getServer().get();
            final Player player = event.getPlayer();
            final String serverName = server.getServerInfo().getName();

            // Permission Check
            if (player.hasPermission("serverpermissions.server." + serverName)) {
                return;
            }

            // If the player does not have permission, access to the server is denied
            event.setResult(ServerPreConnectEvent.ServerResult.denied());

            final String noPermissionMessage = plugin.configuration().noPermissionMessage();
            // If the message is empty, it avoids sending to the player
            if (noPermissionMessage.isBlank()) {
                return;
            }

            final TagResolver.Builder builder = TagResolver.builder()
                    .resolver(new ServerResolver(serverName));

            // MiniPlaceholders custom placeholders support
            if (hasMiniPlaceholders) {
                builder.resolver(MiniPlaceholders.getAudienceGlobalPlaceholders(player));
            }

            final Component message = miniMessage().deserialize(noPermissionMessage, builder.build());

            // if it is the initial connection, we disconnect the player from the proxy so they do not time out
            if (event.getPreviousServer() == null) {
                player.disconnect(message);
            } else {
                player.sendMessage(message);
            }
        });
    }

    private record ServerResolver(String server) implements TagResolver {
        @Override
        public @Nullable Tag resolve(
                final @NotNull String name,
                final @NotNull ArgumentQueue arguments,
                final @NotNull Context ctx
        ) throws ParsingException {
            if (name.equalsIgnoreCase("server")) {
                return Tag.preProcessParsed(server);
            }
            return null;
        }

        @Override
        public boolean has(final @NotNull String name) {
            return name.equalsIgnoreCase("server");
        }
    }
}
