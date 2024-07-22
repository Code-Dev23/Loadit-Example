package it.scopped.loaditexample;

import it.scopped.loaditexample.database.IDatabase;
import it.scopped.loaditexample.database.implementations.SQLite;
import it.scopped.loaditexample.listeners.DataListeners;
import it.scopped.loaditexample.player.PlayerData;
import it.scopped.loaditexample.player.loader.PlayerDataLoader;
import it.ytnoos.loadit.Loadit;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class LoaditExample extends JavaPlugin {

    @Getter
    private static LoaditExample instance;

    private IDatabase database;
    private Loadit<PlayerData> loadit;

    @Override
    public void onEnable() {
        instance = this;

        this.database = new SQLite();

        this.loadit = Loadit.createInstance(this, new PlayerDataLoader(database));
        loadit.init();

        getServer().getPluginManager().registerEvents(new DataListeners(this), this);
    }

    @Override
    public void onDisable() {
        loadit.stop();
        database.shutdown();
    }
}