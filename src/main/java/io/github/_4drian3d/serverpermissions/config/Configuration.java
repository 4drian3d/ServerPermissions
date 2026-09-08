package io.github._4drian3d.serverpermissions.config;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public record Configuration(
    String noPermissionMessage,
    boolean shouldLogConnectionsAttempts
) {

  public static Configuration loadConfig(final Path path) throws IOException {
    final Path configPath = loadFiles(path);
    final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
        .path(configPath)
        .build();

    final CommentedConfigurationNode loaded = loader.load();

    return new Configuration(
        loaded.node("no-permission-message").getString(""),
        loaded.node("should-log-connections-attempts").getBoolean(false)
    );
  }

  private static Path loadFiles(final Path path) throws IOException {
    if (Files.notExists(path)) {
      Files.createDirectory(path);
    }
    final Path configPath = path.resolve("config.conf");
    if (Files.notExists(configPath)) {
      try (final var stream = Configuration.class.getClassLoader().getResourceAsStream("config.conf")) {
        Files.copy(Objects.requireNonNull(stream), configPath);
      }
    }
    return configPath;
  }
}
