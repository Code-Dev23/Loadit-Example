package it.scopped.loaditexample.player;

import it.ytnoos.loadit.api.UserData;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
public class PlayerData extends UserData {

    private int kills;
    private int deaths;

    public PlayerData(@NotNull UUID uuid, @NotNull String name, int kills, int deaths) {
        super(uuid, name);
        this.kills = kills;
        this.deaths = deaths;
    }

    public void incrementKills() {
        this.kills += 1;
    }

    public void incrementDeaths() {
        this.deaths += 1;
    }

    public void decrementKills() {
        if (kills == 0)
            return;

        this.kills -= 1;
    }

    public void decrementDeaths() {
        if (deaths == 0)
            return;

        this.deaths -= 1;
    }
}