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
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

import io.github._4drian3d.serverpermissions.config.Configuration;

import static net.kyori.adventure.text.Component.text;

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
    private ComponentLogger logger;
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

        logger.info(text("ServerPermissions has been correctly started", NamedTextColor.GREEN));
    }

    public Configuration configuration() {
        return this.configuration;
    }
}
