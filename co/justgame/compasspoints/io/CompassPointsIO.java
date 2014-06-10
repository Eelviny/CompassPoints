package co.justgame.compasspoints.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import co.justgame.compasspoints.compasspoint.CompassPoint;
import co.justgame.compasspoints.main.CompassPoints;
import co.justgame.compasspoints.resources.NameUtils;

public class CompassPointsIO extends JavaPlugin {

    public static synchronized void writePlayerFile(ArrayList<CompassPoint> compassPoints, String playerName, boolean convert){

        File playerFile1 = createPlayerFile(CompassPoints.getUUID(playerName), false);
        File playerFile2 = createPlayerFile(playerName, false);
        if(playerFile1.exists()){
            playerFile1.delete();
        }else{
            playerFile2.delete();
        }

        File newPlayerFile = null;

        if(convert){
            newPlayerFile = createPlayerFile(CompassPoints.getUUID(playerName), true);
        }else{
            newPlayerFile = createPlayerFile(playerName, true);
        }

        int counter = 0;
        for(CompassPoint compassPoint: compassPoints){

            setMessage(newPlayerFile, "compasspoint" + counter + ".x", compassPoint.getX().toString());
            setMessage(newPlayerFile, "compasspoint" + counter + ".y", compassPoint.getY().toString());
            setMessage(newPlayerFile, "compasspoint" + counter + ".z", compassPoint.getZ().toString());
            setMessage(newPlayerFile, "compasspoint" + counter + ".name", compassPoint.getName());
            setMessage(newPlayerFile, "compasspoint" + counter + ".world", compassPoint.getWorldName());

            counter++;
        }

    }

    public static synchronized ArrayList<CompassPoint> readPlayerFile(String playerName, boolean convert){

        boolean switchToUUID = false;

        File playerFile = createPlayerFile(CompassPoints.getUUID(playerName), false);
        if(!playerFile.exists()){
            playerFile = createPlayerFile(playerName, true);
            switchToUUID = true;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

        ArrayList<CompassPoint> compassPoints = new ArrayList<CompassPoint>();
        CompassPoint compassPoint = new CompassPoint();

        boolean rewrite = false;

        for(String message: config.getConfigurationSection("").getKeys(true)){

            if(message.contains(".x")){
                compassPoint.setX(Integer.parseInt(config.getString(message)));
            }else if(message.contains(".y")){
                compassPoint.setY(Integer.parseInt(config.getString(message)));
            }else if(message.contains(".z")){
                compassPoint.setZ(Integer.parseInt(config.getString(message)));
            }else if(message.contains(".world")){
                compassPoint.setWorld(config.getString(message));
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

        if(switchToUUID && convert) CompassPointsIO.writePlayerFile(compassPoints, playerName, true);

        if(rewrite) CompassPointsIO.writePlayerFile(compassPoints, playerName, true);

        return compassPoints;
    }

    public static boolean hasFile(String p){
        File playerFile1 = createPlayerFile(CompassPoints.getUUID(p), false);
        File playerFile2 = createPlayerFile(p, false);

        if(playerFile1.exists() || playerFile2.exists())
            return true;
        else
            return false;
    }

    private static File createPlayerFile(String playerName, boolean bool){
        try{

            File dir = new File(CompassPoints.getThisDataFolder() + File.separator + "Player" + File.separator);
            Files.createDirectories(dir.toPath());
            File playerFile = new File(dir, playerName + ".yml");

            if(!playerFile.exists() && bool){
                playerFile.createNewFile();
            }

            return playerFile;

        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    private static void setMessage(File file, String name, String message){

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set(name, message);
        try{
            config.save(file);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
