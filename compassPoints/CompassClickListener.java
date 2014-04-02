package compassPoints;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class CompassClickListener implements Listener {
	
	private HashMap<String, String> messageData = CompassPoints.getMessageData();
	
	  @EventHandler(priority=EventPriority.HIGH)
	    public void onPlayerUse(PlayerInteractEvent event){
		  	Player player = event.getPlayer();
		  
		  if( player.hasPermission("compasspoints.use") && player.getItemInHand().getType() == Material.COMPASS){
	        	if( event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK ){
	        		if(player.isSneaking()){
	        			player.sendMessage(messageData.get("gui.direction") + getCardinalDirection(player));
	        			player.playSound(player.getLocation(),Sound.CLICK, 2, 1);
	        		}else{
	        			new CompassPointGUI(player);
	        		}
	        	}
		  }
	  }
	  
	  private synchronized static String getCardinalDirection(Player player) {
	        double rotation = (player.getLocation().getYaw() - 90) % 360;
	        if (rotation < 0) {
	            rotation += 360.0;
	        }
	         if (0 <= rotation && rotation < 22.5) {
	            return "West";
	        } else if (22.5 <= rotation && rotation < 67.5) {
	            return "North West";
	        } else if (67.5 <= rotation && rotation < 112.5) {
	            return "North ";
	        } else if (112.5 <= rotation && rotation < 157.5) {
	            return "North East";
	        } else if (157.5 <= rotation && rotation < 202.5) {
	            return "East ";
	        } else if (202.5 <= rotation && rotation < 247.5) {
	            return "South East";
	        } else if (247.5 <= rotation && rotation < 292.5) {
	            return "South ";
	        } else if (292.5 <= rotation && rotation < 337.5) {
	            return "South West";
	        } else if (337.5 <= rotation && rotation < 360.0) {
	            return "West";
	        } else {
	            return null;
	        }
	    }
}