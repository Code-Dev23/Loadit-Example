package it.scopped.loaditexample.listeners;

import it.scopped.loaditexample.LoaditExample;
import it.scopped.loaditexample.player.PlayerData;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

@RequiredArgsConstructor
public class DataListeners implements Listener {
    private final LoaditExample instance;

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        PlayerData playerData = instance.getLoadit().getContainer().getCached(player);
        playerData.incrementDeaths();

        instance.getDatabase().saveOrUpdateAsynchronously(playerData);

        Player killer = event.getEntity().getKiller();
        if(killer == null) return;

        PlayerData killerData = instance.getLoadit().getContainer().getCached(killer);
        playerData.incrementKills();

        instance.getDatabase().saveOrUpdateAsynchronously(killerData);
    }
}