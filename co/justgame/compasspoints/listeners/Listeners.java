package co.justgame.compasspoints.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import co.justgame.compasspoints.main.CompassPoints;

public class Listeners implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onLogin(PlayerJoinEvent e){
        Player p = e.getPlayer();

        Location point = p.getBedSpawnLocation() != null ? p.getBedSpawnLocation() : p.getWorld().getSpawnLocation();
        p.setCompassTarget(point);

        CompassPoints.addUUID(p.getName(), p.getUniqueId().toString());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onWorldUnload(WorldUnloadEvent e){
        CompassPoints.declareWorldUnafe(e.getWorld());
    }
}
