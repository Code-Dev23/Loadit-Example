package it.scopped.loaditexample.player.loader;

import it.scopped.loaditexample.database.IDatabase;
import it.scopped.loaditexample.player.PlayerData;
import it.ytnoos.loadit.api.DataLoader;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class PlayerDataLoader implements DataLoader<PlayerData> {
    private final IDatabase database;

    @Override
    public Optional<PlayerData> getOrCreate(UUID uuid, String s) {
        PlayerData player = database.getPlayerSynchronously(uuid, s);

        if (player == null) {
            player = new PlayerData(uuid, s, 0, 0);
            database.saveOrUpdate(player);
        }

        return Optional.of(player);
    }

    @Override
    public Optional<PlayerData> load(UUID uuid) {
        return Optional.ofNullable(database.getPlayerSynchronously(uuid));
    }

    @Override
    public Optional<PlayerData> load(String s) {
        return Optional.ofNullable(database.getPlayerSynchronously(s));
    }
}