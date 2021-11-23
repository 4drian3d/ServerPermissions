package me.dreamerzero.serverpermissions;

import java.nio.file.Path;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent.ServerResult;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;

import org.slf4j.Logger;

import me.dreamerzero.serverpermissions.config.Configuration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ServerPermissions {
    private final Logger logger;
    private final Path path;

    @Inject
    public ServerPermissions(Logger logger, @DataDirectory Path path) {
        this.path = path;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        Configuration.loadConfig(path, logger);
    }

    @Subscribe
    public void onServerSwitch(ServerPreConnectEvent event){
        event.getResult().getServer().ifPresent(server -> {
            Player player = event.getPlayer();
            if(!event.getPlayer().hasPermission("serverpermissions.server."+server.getServerInfo().getName())){
                event.setResult(ServerResult.denied());
                player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(Configuration.getConfig().getNoPermissionMessage()));
            }
        });
    }

}
