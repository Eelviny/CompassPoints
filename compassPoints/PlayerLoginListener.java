package compassPoints;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
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
		
		final String playersName = player.getName();
		final UUIDFetcher UUIDF = new UUIDFetcher(Arrays.asList(playersName));
		Thread getter = new Thread(){
			public void run (){
				Map<String, UUID> UUIDS;
				try{
					UUIDS = UUIDF.call();
					System.out.println(UUIDS.toString());
					CompassPoints.addUUID(playersName, UUIDS.get(playersName).toString()); 
				}catch (Exception e){
					Bukkit.getServer().getLogger().log(Level.SEVERE, "WARNING: Error retrieving player UUID!");
				}
			}
		};
		getter.start();
    }
	
	
}
