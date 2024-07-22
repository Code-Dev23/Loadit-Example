package it.scopped.loaditexample.database.implementations;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import it.scopped.loaditexample.database.IDatabase;
import it.scopped.loaditexample.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.*;
import java.util.UUID;

public class SQLite implements IDatabase {
    private HikariDataSource dataSource;

    public SQLite() {
        connect();
        createTables();
        Bukkit.getLogger().info("[DATABASE] SQLite connected.");
    }

    @Override
    public void connect() {
        try {
            File dbFile = new File(JavaPlugin.getProvidingPlugin(getClass()).getDataFolder(), "database.db");
            if (!dbFile.exists() && !dbFile.createNewFile()) {
                Bukkit.getLogger().severe("[DATABASE] Failed to create new database file.");
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe("[DATABASE] Error creating database file: " + e.getMessage());
            e.printStackTrace();
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + new File(JavaPlugin.getProvidingPlugin(getClass()).getDataFolder(), "database.db").getAbsolutePath());
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setMaxLifetime(1800000);
        config.setConnectionTimeout(5000);
        config.setPoolName("AtlasBounty-pool");

        this.dataSource = new HikariDataSource(config);
    }

    private void createTables() {
        String query = """
                CREATE TABLE IF NOT EXISTS `players` (
                    `uuid` VARCHAR(36) NOT NULL,
                    `name` VARCHAR(16) NOT NULL,
                    `kills` INTEGER NOT NULL,
                    `deaths` INTEGER NOT NULL,
                    PRIMARY KEY (`uuid`)
                );
                """;

        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute(query);
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[DATABASE] Error creating tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public PlayerData getPlayerSynchronously(String name) {
        String query = "SELECT * FROM `players` WHERE `name` = ? LIMIT 1;";
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                    return new PlayerData(uuid, name, resultSet.getInt("kills"), resultSet.getInt("deaths"));
                }
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[DATABASE] Error fetching player data: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public PlayerData getPlayerSynchronously(UUID uuid, String name) {
        String query = "SELECT * FROM `players` WHERE `uuid` = ? AND `name` = ? LIMIT 1;";
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, uuid.toString());
            statement.setString(2, name);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new PlayerData(uuid, name, resultSet.getInt("kills"), resultSet.getInt("deaths"));
                }
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[DATABASE] Error fetching player data: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void saveOrUpdate(PlayerData player) {
        String query = """
                INSERT INTO `players` (`uuid`, `name`, `kills`, `deaths`) 
                VALUES (?, ?, ?, ?) 
                ON CONFLICT(`uuid`) DO UPDATE SET 
                    `name` = EXCLUDED.`name`, 
                    `kills` = EXCLUDED.`kills`,
                    `deaths` = EXCLUDED.`deaths`;
                """;
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, player.getUUID().toString());
            statement.setString(2, player.getName());
            statement.setInt(3, player.getKills());
            statement.setInt(4, player.getDeaths());
            statement.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[DATABASE] Error saving player data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void deletePlayer(PlayerData player) {
        deletePlayer(player.getUUID());
    }

    @Override
    public void deletePlayer(UUID uuid) {
        String query = "DELETE FROM `players` WHERE `uuid` = ?;";
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, uuid.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[DATABASE] Error deleting player data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed())
            dataSource.close();
    }
}