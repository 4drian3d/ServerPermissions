package me.adrianed.serverpermissions;

import java.io.IOException;
import java.nio.file.Path;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;

import com.velocitypowered.api.plugin.annotation.DataDirectory;
import me.adrianed.serverpermissions.listener.ServerListener;
import me.adrianed.serverpermissions.utils.Constants;
import org.slf4j.Logger;

import me.adrianed.serverpermissions.config.Configuration;

@Plugin(
        id = "serverpermissions",
        name = "ServerPermissions",
        version = Constants.VERSION,
        description = "Add permissions to access your servers",
        authors = {"4drian3d"}
)
public final class ServerPermissions {
    @Inject
    private Logger logger;
    @Inject
    @DataDirectory
    private Path path;
    @Inject
    private EventManager eventManager;
    @Inject
    private Injector injector;

    private Configuration configuration;

    @Subscribe
    void onProxyInitialize(ProxyInitializeEvent event) {
        try {
            configuration = Configuration.loadConfig(path);
        } catch (IOException e) {
            logger.error("Cannot load configuration", e);
            return;
        }

        eventManager.register(this, injector.getInstance(ServerListener.class));

        logger.info("ServerPermission has been correctly started");
    }

    public Configuration configuration() {
        return this.configuration;
    }
}
