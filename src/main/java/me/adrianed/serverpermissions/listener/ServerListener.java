package me.adrianed.serverpermissions.listener;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import me.adrianed.serverpermissions.ServerPermissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class ServerListener {
    @Inject
    private ServerPermissions plugin;

    @Subscribe
    void onServerSwitch(ServerPreConnectEvent event) {
        event.getResult().getServer().ifPresent(server -> {
            final Player player = event.getPlayer();
            final String serverName = server.getServerInfo().getName();

            if (!player.hasPermission("serverpermissions.server." + serverName)) {
                event.setResult(ServerPreConnectEvent.ServerResult.denied());
                final Component message = MiniMessage.miniMessage()
                        .deserialize(plugin.configuration().noPermissionMessage(),
                                Placeholder.unparsed("server", serverName));
                player.sendMessage(message);
            }
        });
    }
}