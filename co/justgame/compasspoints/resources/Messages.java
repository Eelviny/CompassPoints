package co.justgame.compasspoints.resources;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import co.justgame.compasspoints.main.CompassPoints;

public class Messages {

    private static HashMap<String, String> messageData = new HashMap<String, String>();
    static Plugin plugin = CompassPoints.getInstance();

    public static synchronized String get(String key){
        return messageData.get(key);
    }

    public static synchronized void loadMessages(){

        File Messages = new File(plugin.getDataFolder() + File.separator + "messages.yml");
        if(!Messages.exists()) try{
            Messages.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }

        setMessage("commands.disimbiguation", "&cDid you mean &6/point&c, &6/point <Player>&c, or &6/point reload&c ?");
        setMessage("commands.command.nopermission", "&cYou do not have permission to use this command! Contact an administrator if you think this is a mistake!");
        setMessage("commands.command.extra", "&aUse a compass to open your &4CompassPoints");
        setMessage("commands.other.nopermission", "&c You do not have permission to view another players &4CompassPoints! &cContact an administrator if you think this is a mistake!");
        setMessage("commands.other.error.noplayer", "&cThe Player specified does not exist or does not have any &4CompassPoints");
        setMessage("commands.other.error.offlinebed", "&cThe Player specified is not Online! You can not teleport to his Spawn Bed!");
        setMessage("commands.other.error.nobed", "&cThe Player specified does not have a Spawn Bed!");
        setMessage("commands.reload.nopermission", "&cYou do not have permission to reload the config file! Contact an administrator if you think this is a mistake! ");
        setMessage("commands.reload.error.world", "&4Error: The World specified in the config cannot be idetified");
        setMessage("commands.reload.error.duplicate", "&4Error: Duplicate or invalid item location in config");
        setMessage("commands.reload.error.failed", "&4Reload Failed:");
        setMessage("commands.reload.complete", "&aReload Complete");
        setMessage("commands.home.nopermission", "&cYou do not have permission to use this command! &cContact an administrator if you think this is a mistake!");
        setMessage("commands.home.disimbiguation", "&cDid you mean &6/home?");

        setMessage("gui.bed.error", "&4 You do not have a spawn bed!");
        setMessage("gui.bed.pointcompass", "&4 Pointed compass towards... &8 Your Spawn Bed");
        setMessage("gui.bed.teleport", "&dTeleported to... &8 Your Spawn Bed");
        setMessage("gui.rename.enter", "&4You have Entered Rename Mode. &6You can Rename the next CompassPoint you click.");
        setMessage("gui.rename.exit", "&aYou have Exited Rename Mode. ");
        setMessage("gui.rename.noexp", "&cYou must have a least seven levels to rename a point!");
        setMessage("gui.delete.enter", "&4You have Entered Delete Mode. &6The next CompassPoint you click will be deleted.");
        setMessage("gui.delete.exit", "&aYou have Exited Delete Mode. ");
        setMessage("gui.teleport", "&dTeleported to... &8");
        setMessage("gui.direction", "&8You are facing &2");
        setMessage("gui.noworld", "&c The World you are trying to teleport to does not exist!");
        setMessage("gui.ridingerror", "&cYou can not teleport while riding a ");
        setMessage("gui.noteleport", "&cYou can not teleport to this world! ");
        setMessage("gui.pointcompass", "&4Pointed compass towards... &8");
        setMessage("gui.pointcompassinnether", "&cThe compass can not point towards a CompassPoint in the &4NETHER");
        setMessage("gui.point.create", "&2Created new CompassPoint at... &8");
        setMessage("gui.point.delete", "&4Deleted CompassPoint at... &8");

        try{
            FileConfiguration config = YamlConfiguration.loadConfiguration(Messages);
            for(String message: config.getConfigurationSection("").getKeys(true)){
                messageData.put(message, formatString(config.getString(message)));
            }
        }catch (Exception e){
            Bukkit.getServer().getLogger().log(Level.WARNING, "§cError loading messages.yml!");
        }

    }

    private static void setMessage(String name, String message){
        File f = new File(plugin.getDataFolder() + File.separator + "messages.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(f);
        if(!config.isSet(name)){
            config.set(name, message);
            try{
                config.save(f);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public synchronized static String formatString(String string){
        return string.replaceAll("&", "§");
    }
}
