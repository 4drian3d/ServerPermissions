package io.github._4drian3d.serverpermissions;

import java.io.IOException;
import java.nio.file.Path;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;

import com.velocitypowered.api.plugin.annotation.DataDirectory;
import io.github._4drian3d.serverpermissions.listener.ServerListener;
import io.github._4drian3d.serverpermissions.utils.Constants;
import org.slf4j.Logger;

import io.github._4drian3d.serverpermissions.config.Configuration;

@Plugin(
        id = "serverpermissions",
        name = "ServerPermissions",
        version = Constants.VERSION,
        description = "Add permissions to access your servers",
        url = "https://modrinth.com/plugin/serverpermissions",
        authors = {"4drian3d"},
        dependencies = { @Dependency(id = "miniplaceholders", optional = true) }
)
public final class ServerPermissions {
    @Inject
    private Logger logger;
    @Inject
    @DataDirectory
    private Path path;
    @Inject
    private Injector injector;

    private Configuration configuration;

    @Subscribe
    void onProxyInitialize(final ProxyInitializeEvent event) {
        try {
            configuration = Configuration.loadConfig(path);
        } catch (IOException e) {
            logger.error("Cannot load configuration", e);
            return;
        }

        injector.getInstance(ServerListener.class).register();

        logger.info("ServerPermissions has been correctly started");
    }

    public Configuration configuration() {
        return this.configuration;
    }
}
