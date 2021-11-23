package me.dreamerzero.serverpermissions.config;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

public class Configuration {
    public static void loadConfig(Path path, Logger logger){
        Path configPath = path.resolve("config.conf");
        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
            .defaultOptions(opts -> opts
            .shouldCopyDefaults(true))
            .path(configPath)
            .build();

        try {
            final CommentedConfigurationNode node = loader.load();
            config = node.get(Config.class);
            node.set(Config.class, config);
            loader.save(node);
        } catch (ConfigurateException exception){
            logger.error("Could not load configuration: {}", exception.getMessage());
        }
    }

    @ConfigSerializable
    public static class Config {
        private String noPermissionMessage = "You do not have permission to access this server";

        public String getNoPermissionMessage(){
            return this.noPermissionMessage;
        }
    }
    private static Config config;
    public static Config getConfig(){
        return config;
    }

    private Configuration(){}
}
