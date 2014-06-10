package co.justgame.compasspoints.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import co.justgame.compasspoints.compasspoint.CompassPoint;
import co.justgame.compasspoints.guis.CompassPointsGUI;
import co.justgame.compasspoints.guis.CompassPointsGUI.TYPE;
import co.justgame.compasspoints.listeners.CompassClickListener;
import co.justgame.compasspoints.listeners.Listeners;
import co.justgame.compasspoints.resources.Messages;

public class CompassPoints extends JavaPlugin {

    public static Plugin compassPoints;
    public static File dataFolder;

    public static HashMap<String, String> UUIDs = new HashMap<String, String>();
    public static TreeMap<CompassPoint, ItemStack> configMap = new TreeMap<CompassPoint, ItemStack>();
    public static ArrayList<World> worlds = new ArrayList<World>();

    FileConfiguration config;

    @Override
    public void onEnable(){
        getLogger().info("CompassPoints has been enabled");
        compassPoints = this;
        Messages.loadMessages();
        loadConfig(Bukkit.getConsoleSender());

        new Thread(){

            public void run(){
                String[] Worlds = config.getString("worlds").split(",");

                for(String world: Worlds){
                    while(Bukkit.getWorld(world.trim()) == null){
                    }
                }
                getLogger().info("Worlds loaded");

                for(String world: Worlds){
                    worlds.add(Bukkit.getWorld(world.trim()));
                }
            }
        }.start();

        for(Player player: Bukkit.getServer().getOnlinePlayers()){
            CompassPoints.addUUID(player.getName(), player.getUniqueId().toString());
        }

        getServer().getPluginManager().registerEvents(new CompassClickListener(), this);
        getServer().getPluginManager().registerEvents(new Listeners(), this);

    }

    @Override
    public void onDisable(){
        getLogger().info("CompassPoints has been disabled");
        for(Player player: Bukkit.getServer().getOnlinePlayers()){
            player.closeInventory();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){

        if(args.length >= 1 && args.length < 2 && args[0].equalsIgnoreCase("reload") && cmd.getName().equalsIgnoreCase("point")){

            if(sender.hasPermission("compasspoints.reload") || !(sender instanceof Player)){
                loadConfig(sender);
                Messages.loadMessages();
            }else{
                Player player = (Player) sender;
                player.sendMessage(Messages.get("commands.reload.nopermission"));
            }

        }else if(args.length == 1 && cmd.getName().equalsIgnoreCase("point")){
            if(!(sender instanceof Player)){
                System.out.println("This command cannot be used by the Console");
            }

            if(sender.hasPermission("compasspoints.other")){
                Player player = (Player) sender;
                new CompassPointsGUI(player, TYPE.EXTERNAL, args[0]);
            }else{
                Player player = (Player) sender;
                player.sendMessage(Messages.get("commands.other.nopermission"));
            }

        }else if(cmd.getName().equalsIgnoreCase("point") && args.length <= 0){
            if(!(sender instanceof Player)){
                System.out.println("This command cannot be used by the Console");
            }

            if(sender.hasPermission("compasspoints.command")){

                Player player = (Player) sender;
                new CompassPointsGUI(player, TYPE.PRIVATE, null);
            }else{
                Player player = (Player) sender;
                player.sendMessage(Messages.get("commands.command.nopermission"));
                player.sendMessage(Messages.get("commands.command.extra"));
            }

        }else if(cmd.getName().equalsIgnoreCase("home")){
            if(args.length != 0){
                sender.sendMessage(Messages.get("commands.home.disimbiguation"));
            }else{
                Player player = (Player) sender;
                if(player.hasPermission("compasspoints.home")){
                    if(player.getBedSpawnLocation() != null){
                        if(player.hasPermission("compasspoints.teleport")){

                            if(player.isInsideVehicle()){
                                player.sendMessage(Messages.get("gui.ridingerror")
                                        + player.getVehicle().getType().toString().toLowerCase() + "!");
                            }else{
                                player.teleport(player.getBedSpawnLocation());
                                player.sendMessage(Messages.get("gui.bed.teleport"));
                            }
                        }else{
                            player.sendMessage(Messages.get("gui.bed.pointcompass"));
                            player.getWorld().playSound(player.getLocation(), Sound.ITEM_PICKUP, 5, 1);
                            player.setCompassTarget((player.getBedSpawnLocation()));
                        }

                    }else{
                        player.sendMessage(Messages.get("gui.bed.error"));
                    }
                }else{
                    player.sendMessage(Messages.get("commands.home.nopermission"));
                }
            }

        }else if(cmd.getName().equalsIgnoreCase("point")){
            sender.sendMessage(Messages.get("commands.disimbiguation"));
        }
        return true;
    }

    public static synchronized TreeMap<CompassPoint, ItemStack> getConfigMap(){
        return configMap;
    }

    public static synchronized ArrayList<World> getWorlds(){
        return worlds;
    }

    public static synchronized void declareWorldUnafe(World w){
        if(worlds.contains(w)) worlds.remove(worlds.indexOf(w));
    }

    public static synchronized boolean safeToTP(World w){
        return worlds.contains(w);
    }

    public static synchronized Plugin getInstance(){
        return compassPoints;
    }

    public static synchronized File getThisDataFolder(){
        return dataFolder;
    }

    public static synchronized String getUUID(String playersName){
        if(UUIDs.containsKey(playersName)){
            return UUIDs.get(playersName);
        }
        return null;
    }

    public static synchronized void addUUID(String playersName, String UUID){
        UUIDs.put(playersName, UUID);
    }

    private boolean loadConfig(CommandSender sender){

        configMap = new TreeMap<CompassPoint, ItemStack>();

        try{

            this.reloadConfig();
            config = this.getConfig();

            config.options()
                    .header("The type variable must be equal to a Bukkit Material, such as STONE, DIAMOND_SWORD, ect. "
                            + "If you want a variable to be ignored and the value set to the default, type DEFAULT "
                            + "in the type or absolutepath fields. "
                            + "The worlds field is used to determine which worlds can be used by the plugin. Seperate each world with a comma."
                            + "Example: 'world, world_nether '");

            config.addDefault("worlds", "world, world_nether");

            for(int i = 1; i <= 6; i++){
                config.addDefault("item" + i + ".type", "AIR");
                config.addDefault("item" + i + ".name", "");
                config.addDefault("item" + i + ".lore", "");
                config.addDefault("item" + i + ".location.x", 0);
                config.addDefault("item" + i + ".location.y", 0);
                config.addDefault("item" + i + ".location.z", 0);
                config.addDefault("item" + i + ".location.world", "world");
            }
            config.options().copyDefaults(true);
            saveConfig();

            String[] Worlds = config.getString("worlds").split(",");

            for(String world: Worlds){
                worlds.add(Bukkit.getWorld(world.trim()));
            }

            for(int i = 1; i <= 6; i++){
                String type = config.getString("item" + i + ".type");
                String name = config.getString("item" + i + ".name");
                String lore = config.getString("item" + i + ".lore", "");
                int teleportX = config.getInt("item" + i + ".location.x");
                int teleportY = config.getInt("item" + i + ".location.y");
                int teleportZ = config.getInt("item" + i + ".location.z");
                String teleportWorld = config.getString("item" + i + ".location.world");

                if(!type.equals("AIR")){
                    Material configType1 = Material.getMaterial(type);

                    ItemStack itemStack = new ItemStack(configType1);
                    ItemMeta typeMeta = itemStack.getItemMeta();
                    typeMeta.setDisplayName(Messages.formatString(name));

                    if(lore.isEmpty()){
                        typeMeta.setLore(Arrays.asList(" "));
                    }else{
                        typeMeta.setLore(Arrays.asList(Messages.formatString(lore).split(",")));
                    }
                    itemStack.setItemMeta(typeMeta);

                    CompassPoint configCompassPoint = null;

                    try{
                        configCompassPoint = new CompassPoint(teleportX, teleportY, teleportZ, Bukkit.getWorld(teleportWorld),
                                name);
                        configCompassPoint.setPosition(i);
                    }catch (Exception exception){
                        System.out.println("Error: The World specified in the config cannot be idetified");
                        configCompassPoint = new CompassPoint();
                        configCompassPoint.setPosition(i);
                    }

                    configMap.put(configCompassPoint, itemStack);
                }
            }
        }catch (Exception e1){
            e1.printStackTrace();
            sender.sendMessage(Messages.get("commands.reload.error.failed") + e1);
            return true;
        }

        sender.sendMessage(Messages.get("commands.reload.complete"));
        return true;

    }

}
