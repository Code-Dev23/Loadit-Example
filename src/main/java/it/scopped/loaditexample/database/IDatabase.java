package it.scopped.loaditexample.database;

import it.scopped.loaditexample.player.PlayerData;
import org.bukkit.Bukkit;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IDatabase {

    void connect();

    PlayerData getPlayerSynchronously(String name);

    PlayerData getPlayerSynchronously(UUID uuid, String name);

    void saveOrUpdate(PlayerData player);

    default PlayerData getPlayerSynchronously(UUID uuid) {
        return getPlayerSynchronously(Bukkit.getOfflinePlayer(uuid).getName());
    }

    default void saveOrUpdateAsynchronously(PlayerData player) {
        CompletableFuture.runAsync(() -> saveOrUpdate(player));
    }

    void deletePlayer(PlayerData player);

    void deletePlayer(UUID uuid);

    default void deletePlayerAsynchronously(UUID uuid) {
        CompletableFuture.runAsync(() -> deletePlayer(uuid));
    }

    void shutdown();
}