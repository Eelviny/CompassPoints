package compassPoints;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerLoginListener implements Listener{
	
	@EventHandler(priority = EventPriority.HIGH)
    public void onLogin(PlayerJoinEvent event) {
		
		Player player = event.getPlayer();
		Location spawn = player.getWorld().getSpawnLocation();
		
		if(player.getBedSpawnLocation() != null){
			player.setCompassTarget(player.getBedSpawnLocation());
		}else{
			player.setCompassTarget(spawn);
		}
    }
	
	
}
