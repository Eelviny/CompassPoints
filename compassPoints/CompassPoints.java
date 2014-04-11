package compassPoints;

import java.io.File;
import java.io.IOException;
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
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import compassPoints.CompassClickListener;

public class CompassPoints extends JavaPlugin{

	public static Plugin compassPoints; 
	public static File dataFolder;

	public static TreeMap<CompassPoint, ItemStack> configMap = new TreeMap<CompassPoint, ItemStack >();
	public static ArrayList<World> worlds = new ArrayList<World>();
	public static HashMap<String, String> messageData = new HashMap<String, String>();

	FileConfiguration config;

	@Override
	public void onEnable(){
		getLogger().info("CompassPoints has been enabled");
		compassPoints = this;
		dataFolder = compassPoints.getDataFolder();

		try{
			config = this.getConfig();

			config.options().header( "The type variable must be equal to a Bukkit Material, such as STONE, DIAMOND_SWORD, ect. "
					+ "If you want a variable to be ignored and the value set to the default, type DEFAULT "
					+ "in the type or absolutepath fields. "
					+ "The worlds field is used to determine which worlds can be used by the plugin. Seperate each world with a comma."
					+ "Example: 'world, world_nether '");

				config.addDefault("worlds", "world, world_nether");
				
			for(int i = 1; i<= 6; i++) {
				config.addDefault("item"+i+".type", "AIR");
				config.addDefault("item"+i+".name", "");
				config.addDefault("item"+i+".lore", "");
				config.addDefault("item"+i+".location.x", 0 );
				config.addDefault("item"+i+".location.y", 0 );
				config.addDefault("item"+i+".location.z", 0 );
				config.addDefault("item"+i+".location.world", "world" );
			}	 
			config.options().copyDefaults(true);
			saveConfig();

			final Thread thread = new Thread() {

				public void run(){

					String[] Worlds = config.getString("worlds").split(",");
					
					for(String world: Worlds){
						while(Bukkit.getWorld(world.trim()) == null){}
					}
					getLogger().info("Worlds loaded");
					
					for(String world: Worlds){
						worlds.add(Bukkit.getWorld(world.trim()));
					}

					for(int i = 1; i<= 6; i++) {
						String type = config.getString("item"+i+".type");
						String name = config.getString("item"+i+".name");
						String lore = config.getString("item"+i+".lore", "");
						int teleportX = config.getInt("item"+i+".location.x");
						int teleportY = config.getInt("item"+i+".location.y");
						int teleportZ = config.getInt("item"+i+".location.z");
						String teleportWorld = config.getString("item"+i+".location.world");

						if(!type.equals("AIR")){
							Material configType1 = Material.getMaterial(type);
							ItemStack itemStack = new ItemStack(configType1);
							ItemMeta typeMeta = itemStack.getItemMeta();
							typeMeta.setDisplayName(MessageUtils.FormatString(name));
							
						if(lore.isEmpty()){
							typeMeta.setLore(Arrays.asList(" "));
						}else{
							typeMeta.setLore(Arrays.asList(MessageUtils.FormatString(lore).split(",")));
						}
							itemStack.setItemMeta(typeMeta);

							CompassPoint configCompassPoint = null;

							try{
								configCompassPoint = new CompassPoint(teleportX, teleportY, teleportZ, Bukkit.getWorld(teleportWorld), name);
								configCompassPoint.setPosition(i);
							}catch(Exception exception){
								System.out.println("Error: The World specified in the config cannot be idetified");
								configCompassPoint = new CompassPoint();
								configCompassPoint.setPosition(i);
							}
								
								configMap.put(configCompassPoint, itemStack);
								
						}
					}
					
				}
			};

			thread.start();
			
			File Messages = new File(getDataFolder()+File.separator+"messages.yml");
			if (!Messages.exists()) 
				Messages.createNewFile();

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
			setMessage("gui.rename.noexp", "&cYou must have a least seven level to rename a point!");
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


			FileConfiguration config = YamlConfiguration.loadConfiguration(Messages);
			for (String message : config.getConfigurationSection("").getKeys(true)) {
				messageData.put(message, MessageUtils.FormatString(config.getString(message)));
			}

		}catch(Exception e1){
			e1.printStackTrace();
		}


		getServer().getPluginManager().registerEvents(new CompassClickListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerLoginListener(), this);

	}
	@Override
	public void onDisable() {
		getLogger().info("CompassPoints has been disabled");
		for(Player player: Bukkit.getServer().getOnlinePlayers()){
    		player.closeInventory();
    	}
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){

		if(args.length >= 1 && args.length < 2 && args[0].equalsIgnoreCase("reload") && cmd.getName().equalsIgnoreCase("point")){ 

			if(sender.hasPermission("compasspoints.reload") || !(sender instanceof Player)){

				reloadConfig(sender);
				
				return true;

			}else{
				Player player = (Player) sender;
				player.sendMessage(messageData.get("commands.reload.nopermission"));
				return true;
			}

		}else if(args.length == 1 && cmd.getName().equalsIgnoreCase("point")){ 

			if(!(sender instanceof Player)){
				System.out.println("This command cannot be used by the Console");
				return true;
			}

			if(sender.hasPermission("compasspoints.other")){

				Player player = (Player) sender;

				new OtherCompassGUI(player, args[0]);
				return true;

			}else{
				Player player = (Player) sender;
				player.sendMessage(messageData.get("commands.other.nopermission"));
				return true;
			}

		}else if(cmd.getName().equalsIgnoreCase("point") && args.length <= 0){ 

			if(!(sender instanceof Player)){
				System.out.println("This command cannot be used by the Console");
				return true;
			}

			if(sender.hasPermission("compasspoints.command")){

				Player player = (Player) sender;

				new CompassPointGUI(player);

				return true;
			}else{
				Player player = (Player) sender;
				player.sendMessage(messageData.get("commands.command.nopermission"));
				player.sendMessage(messageData.get("commands.command.extra"));
				return true;
			}

		}else if(cmd.getName().equalsIgnoreCase("home")){
			if(args.length != 0){
				sender.sendMessage(messageData.get("commands.home.disimbiguation"));
				return true;
			}else{
			Player player = (Player) sender;
			if(player.hasPermission("compasspoints.home")){
				if(player.getBedSpawnLocation() != null){	
					if(player.hasPermission("compasspoints.teleport")){

						if(player.isInsideVehicle()){
							player.sendMessage(messageData.get("gui.ridingerror") + player.getVehicle().getType().toString().toLowerCase() + "!");
							return true;
						}else{
							player.teleport(player.getBedSpawnLocation());
							player.sendMessage(messageData.get("gui.bed.teleport"));

							return true;
						}
					}else{
						player.sendMessage(messageData.get("gui.bed.pointcompass"));
						player.getWorld().playSound(player.getLocation(),Sound.ITEM_PICKUP, 5, 1);
						player.setCompassTarget((player.getBedSpawnLocation()));
						return true;
					}

				}else{
					player.sendMessage(messageData.get("gui.bed.error"));
					return true;
				}
			}else{
				player.sendMessage(messageData.get("commands.home.nopermission"));
				return true;
			}
		  }

		}else if(cmd.getName().equalsIgnoreCase("point")){ 
			sender.sendMessage(messageData.get("commands.disimbiguation"));
			return true;
		}
		return false;
	}

	public static synchronized TreeMap<CompassPoint, ItemStack> getConfigMap(){

		return configMap;

	}
	public static synchronized HashMap<String, String> getMessageData(){

		return messageData;

	}
	
	public static synchronized ArrayList<World> getWorlds(){

		return worlds;

	}

	public static synchronized Plugin getInstance(){

		return compassPoints;

	}

	public static synchronized File getThisDataFolder(){

		return dataFolder;

	}

	private void setMessage(String name, String message) {
		File f = new File(getDataFolder()+File.separator+"messages.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(f);
		if (!config.isSet(name)) {
			config.set(name, message);
			try {
				config.save(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private boolean reloadConfig(CommandSender sender){

		configMap = new TreeMap<CompassPoint, ItemStack>();

		try{

			this.reloadConfig();
			config = this.getConfig();
			
			String[] Worlds = config.getString("worlds").split(",");
			
			for(String world: Worlds){
				worlds.add(Bukkit.getWorld(world.trim()));
			}

			for(int i = 1; i<= 6; i++) {
				String type = config.getString("item"+i+".type");
				String name = config.getString("item"+i+".name");
				String lore = config.getString("item"+i+".lore", "");
				int teleportX = config.getInt("item"+i+".location.x");
				int teleportY = config.getInt("item"+i+".location.y");
				int teleportZ = config.getInt("item"+i+".location.z");
				String teleportWorld = config.getString("item"+i+".location.world");

				if(!type.equals("AIR")){
					Material configType1 = Material.getMaterial(type);

					ItemStack itemStack = new ItemStack(configType1);
					ItemMeta typeMeta = itemStack.getItemMeta();
					typeMeta.setDisplayName(MessageUtils.FormatString(name));
					
					if(lore.isEmpty()){
						typeMeta.setLore(Arrays.asList(" "));
					}else{
						typeMeta.setLore(Arrays.asList(MessageUtils.FormatString(lore).split(",")));
					}
					itemStack.setItemMeta(typeMeta);

					CompassPoint configCompassPoint = null;

					try{
						configCompassPoint = new CompassPoint(teleportX, teleportY, teleportZ, Bukkit.getWorld(teleportWorld), name);
						configCompassPoint.setPosition(i);
					}catch(Exception exception){
						System.out.println("Error: The World specified in the config cannot be idetified");
						configCompassPoint = new CompassPoint();
						configCompassPoint.setPosition(i);
					}
					
						configMap.put(configCompassPoint, itemStack);
				}
			}

			File Messages = new File(getDataFolder()+File.separator+"messages.yml");

			FileConfiguration config = YamlConfiguration.loadConfiguration(Messages);
			for (String message : config.getConfigurationSection("").getKeys(true)) {
				messageData.put(message, MessageUtils.FormatString(config.getString(message)));
			}

		}catch(Exception e1){
			e1.printStackTrace();
			sender.sendMessage( messageData.get("commands.reload.error.failed") + e1);
			return true;
		}

		sender.sendMessage(messageData.get("commands.reload.complete"));
		return true;

	}

}
