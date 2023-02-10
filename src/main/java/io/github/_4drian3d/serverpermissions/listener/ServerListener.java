package io.github._4drian3d.serverpermissions.listener;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import io.github._4drian3d.serverpermissions.ServerPermissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

public final class ServerListener {
    @Inject
    private ServerPermissions plugin;

    @Subscribe
    void onServerSwitch(final ServerPreConnectEvent event, final Continuation continuation) {
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

            final Component message = miniMessage()
                    .deserialize(noPermissionMessage, Placeholder.unparsed("server", serverName));
            player.sendMessage(message);
            continuation.resume();
        });
    }
}
