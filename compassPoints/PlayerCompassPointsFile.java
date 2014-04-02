package compassPoints;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerCompassPointsFile extends JavaPlugin{

public static synchronized void writePlayerFile(ArrayList<CompassPoint> compassPoints, String playerName){
		
	File playerFile = createPlayerFile(playerName);
	playerFile.delete();
	File newplayerFile = createPlayerFile(playerName);
	
	int counter = 0;
		for(CompassPoint compassPoint : compassPoints){
			
			setMessage(newplayerFile, "compasspoint" + counter +".x", compassPoint.getX().toString());
			setMessage(newplayerFile, "compasspoint" + counter +".y", compassPoint.getY().toString());
			setMessage(newplayerFile, "compasspoint" + counter +".z", compassPoint.getZ().toString());
			setMessage(newplayerFile, "compasspoint" + counter +".name", compassPoint.getName());
			setMessage(newplayerFile, "compasspoint" + counter +".world", compassPoint.getWorld().getName());
			
			counter++;
		}
		
	}
	
	public static synchronized ArrayList<CompassPoint> readPlayerFile(String playerName){
		
		File playerFile = createPlayerFile(playerName);
		 FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
	
		 ArrayList<CompassPoint> compassPoints = new ArrayList<CompassPoint>();
		 CompassPoint compassPoint = new CompassPoint();
		 
		 boolean rewrite = false;
		 
		 for (String message :  config.getConfigurationSection("").getKeys(true)) {
			 
			 if(message.contains(".x")){
				 compassPoint.setX(Integer.parseInt(config.getString(message)));
			 }else if(message.contains(".y")){
				 compassPoint.setY(Integer.parseInt(config.getString(message)));
			 }else if(message.contains(".z")){
				 compassPoint.setZ(Integer.parseInt(config.getString(message)));
			 }else if(message.contains(".world")){
				 compassPoint.setWorld(Bukkit.getWorld(config.getString(message)));
				  compassPoints.add(compassPoint);
				 compassPoint = new CompassPoint();
			 }else if(message.contains(".name")){
				 String name = config.getString(message);
			if(name.equals("DEFAULT")){
				 compassPoint.setName(NameUtils.RandomName(compassPoints));
				rewrite = true;
			}else{
				compassPoint.setName(name);
			}
				
			 }
		 }
		 
		 if(rewrite)
			 PlayerCompassPointsFile.writePlayerFile(compassPoints ,playerName);

		 return compassPoints;
	}
	
	private static File createPlayerFile(String playerName){
		try {
			
			File dir = new File(CompassPoints.getThisDataFolder()+File.separator+"Player"+File.separator);
			Files.createDirectories(dir.toPath());
			File playerFile = new File( dir, playerName + ".yml");
			
			if(!playerFile.exists()){
				playerFile.createNewFile();
			}
			
			return playerFile;
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static void setMessage(File file, String name, String message) {
		
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.set(name, message);
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

	
}
