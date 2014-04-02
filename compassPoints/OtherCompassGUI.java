package compassPoints;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class OtherCompassGUI {
	
	private HashMap<String, String> messageData = CompassPoints.getMessageData();
	private Listener listener;
	private Inventory inven;
	
public OtherCompassGUI(Player player, final String otherPlayer){
	
	 listener = new Listener(){
			
			@EventHandler(priority=EventPriority.HIGH)
		    public void onInventoryClick(final InventoryClickEvent event){
				
		        final Player player = (Player) event.getWhoClicked();
		        final Inventory inventory = event.getInventory();
		        
		        ItemStack fire = new ItemStack(Material.FIRE);
		    	ItemMeta fireMeta = fire.getItemMeta();
		    	fireMeta.setDisplayName("Delete");
		    	fireMeta.setLore(Arrays.asList( ChatColor.DARK_RED + "Toggle Delete Mode"));
		    	fire.setItemMeta(fireMeta);
		    	
		    	ItemStack bed = new ItemStack(Material.BED);
		    	ItemMeta bedMeta = bed.getItemMeta();
		    	bedMeta.setDisplayName("Bed Spawn");
		    	bedMeta.setLore(Arrays.asList( ChatColor.GREEN + "Go to Bed Spawn"));
		    	bed.setItemMeta(bedMeta);
		    	
		    	ItemStack bookAndQuill = new ItemStack(Material.BOOK_AND_QUILL);
		    	ItemMeta bookMeta = bookAndQuill.getItemMeta();
		    	bookMeta.setDisplayName("Add CompassPoint");
		    	bookMeta.setLore(Arrays.asList( ChatColor.BLUE + "Add a new CompassPoint at", ChatColor.BLUE +" your current location"));
		    	bookAndQuill.setItemMeta(bookMeta);
		    	
		    	ItemStack anvil = new ItemStack(Material.ANVIL);
		        ItemMeta anvilMeta = anvil.getItemMeta();
		        anvilMeta.setDisplayName("Rename");
		        anvilMeta.setLore(Arrays.asList( ChatColor.GRAY + "Rename a Point", ChatColor.DARK_GREEN + " 7 Level Charge!"));
		        anvil.setItemMeta(anvilMeta);
		   
		if(inventory != null && event.getCurrentItem() != null)  
		 if(inventory.getName().equals(inven.getName()) && inventory.getHolder().equals(inven.getHolder())){
			 event.setCancelled(true);
			 
			 messageData = CompassPoints.getMessageData();
			 
			  if(event.getSlot() <= 5){
				  
				  TreeMap<CompassPoint, ItemStack> configMap = CompassPoints.getConfigMap();
				  
				 for(CompassPoint compassPoint: configMap.keySet()){
							if(event.getCurrentItem().equals(configMap.get(compassPoint))){ 
							   if(player.hasPermission("compasspoints.teleport")){
								  
								   if(player.isInsideVehicle()){
			        					
			        					player.sendMessage(messageData.get("gui.ridingerror") + player.getVehicle().getType().toString().toLowerCase() + "!");
			        					player.playSound(player.getLocation(),Sound.NOTE_BASS, 2, 1);
			        				}else{
			        					
			        					World world = compassPoint.getWorld();
		        						
		        						if(world.isChunkLoaded(world.getChunkAt(world.getSpawnLocation()))){
			        					
			        						player.teleport(compassPoint.getLocation());
			        						player.sendMessage(messageData.get("gui.teleport") + configMap.get(compassPoint).getItemMeta().getDisplayName());
			        				}else{
			        					event.setCancelled(true);
		        						player.sendMessage(messageData.get("gui.noworld"));
		        						player.playSound(player.getLocation(),Sound.NOTE_BASS, 2, 1);
			        				}
			        					}
			        				}
								   
								   player.closeInventory();
							}
				}
				  
			  }else if (event.getCurrentItem().equals(bed)){
				  
				  String playersInven = otherPlayer;
				  Player playersInvenPlayer = Bukkit.getPlayer(playersInven);
				  
				  if(playersInvenPlayer == null){
					  player.sendMessage(messageData.get("commands.other.error.offlinebed"));
					  player.getWorld().playSound(player.getLocation(),Sound.NOTE_BASS, 2, 1);
					  event.setCancelled(true);
				  }else{
				  
		        	if(playersInvenPlayer.getBedSpawnLocation() != null){	
		        		if(player.hasPermission("compasspoints.teleport")){

		        				if(player.isInsideVehicle()){
		        					
		        					player.sendMessage(messageData.get("gui.ridingerror") + player.getVehicle().getType().toString().toLowerCase() + "!");
		        					player.playSound(player.getLocation(),Sound.NOTE_BASS, 2, 1);
		        				}else{
		        					World world = player.getBedSpawnLocation().getWorld();
	        						
	        						if(world.isChunkLoaded(world.getChunkAt(world.getSpawnLocation()))){
		        						player.teleport(playersInvenPlayer.getBedSpawnLocation());
		        						player.sendMessage(messageData.get("gui.teleport") + playersInvenPlayer.getDisplayName() + "'s" + " Bed Spawn");
		        					}else{
			        					event.setCancelled(true);
		        						player.sendMessage(messageData.get("gui.noworld"));
		        						player.playSound(player.getLocation(),Sound.NOTE_BASS, 2, 1);
			        				}
		        				}
		        				
		        				   player.closeInventory();
		        		}
		        	
		        	}else{
		        		player.sendMessage(messageData.get("commands.other.error.nobed"));
		        		player.getWorld().playSound(player.getLocation(),Sound.NOTE_BASS, 2, 1);
		        		player.closeInventory();
		        	}
				  }
				  
			 }else if (event.getCurrentItem().getType().equals(Material.MAP) && event.getCurrentItem().getItemMeta().getLore() != null){
		    		
				  String playersInven = otherPlayer;
				  Player playersInvenPlayer = Bukkit.getPlayer(playersInven);
				  
				 
		    		ArrayList<CompassPoint> compassPoints = null;
		    		
		    		if(playersInvenPlayer == null){
		    			
		    			File playersInvenFile = null;
		    			
		    				File dir = new File(CompassPoints.getThisDataFolder()+File.separator+"Player"+File.separator);
		    				try {
								Files.createDirectories(dir.toPath());
							} catch (IOException e) {
								e.printStackTrace();
							}
		    				 playersInvenFile = new File( dir, playersInven + ".yml");
		    			
		    			if(playersInvenFile.exists())
		    				 compassPoints = PlayerCompassPointsFile.readPlayerFile(playersInven);
		    			
		    		}else{
		    				compassPoints = PlayerCompassPointsFile.readPlayerFile(playersInvenPlayer.getDisplayName());
		    		}
		    		
		    				if(player.hasPermission("compasspoints.teleport")){
		    					
		    					String worldName = "Overworld";
		    					
		    					if(compassPoints.get(event.getSlot() - 9).getWorld() == Bukkit.getWorld("world_nether") || compassPoints.get(event.getSlot() - 9).getWorld() == Bukkit.getWorld("nether")){
		    						worldName = "Nether";
		    					}else if(compassPoints.get(event.getSlot() - 9).getWorld() == Bukkit.getWorld("world_the_end")){
		    						worldName = "The End";
		    					}else if(compassPoints.get(event.getSlot() - 9).getWorld() == Bukkit.getWorld("world")){
		    						worldName = "Overworld";
		    					}else if(compassPoints.get(event.getSlot() - 9).getWorld() == Bukkit.getWorld("flat")){
		    						worldName = "Flat world";
		    					}else{
		    						worldName = compassPoints.get(event.getSlot() - 9).getWorld().getName().replace("world_", "");
		    					}
		    					
		    					
		    						if(player.isInsideVehicle()){
		        					
		    							player.sendMessage(messageData.get("gui.ridingerror") + player.getVehicle().getType().toString().toLowerCase() + "!");
		    							player.playSound(player.getLocation(),Sound.NOTE_BASS, 2, 1);
		    						}else{
		    							World world = compassPoints.get(event.getSlot() - 9).getWorld();
		        						
		        						if(world.isChunkLoaded(world.getChunkAt(world.getSpawnLocation()))){
		    								player.teleport(compassPoints.get(event.getSlot() - 9).getLocation());
		    								player.getWorld().playSound(player.getLocation(),Sound.ENDERMAN_TELEPORT, 5, 1);
		    								
		    								if( compassPoints.get(event.getSlot() - 9).getName().equals("DEFAULT")){
			        							player.sendMessage(messageData.get("gui.teleport")
				   													+ compassPoints.get(event.getSlot() - 9).getX() + ", " +
				   														compassPoints.get(event.getSlot() - 9).getY() + ", " +
				   															compassPoints.get(event.getSlot() - 9).getZ() +
				   															" in the " +  worldName);
			        						}else{
			        							StringBuilder colorBuilder = new StringBuilder(messageData.get("gui.teleport"));
		    									StringBuilder color = new StringBuilder();
		    									color.append("§");
		    									color.append(colorBuilder.charAt(colorBuilder.lastIndexOf("§") + 1));
			        							
			        							player.sendMessage(messageData.get("gui.teleport")
			        									+ compassPoints.get(event.getSlot() - 9).getName() + color.toString() + 
			        										" (" + compassPoints.get(event.getSlot() - 9).getX() + ", " +
																	compassPoints.get(event.getSlot() - 9).getY() + ", " +
																		compassPoints.get(event.getSlot() - 9).getZ() +
																			" in the " +  worldName + ") ");
			        						}
		    							}else{
				        					event.setCancelled(true);
			        						player.sendMessage(messageData.get("gui.noworld"));
			        						player.playSound(player.getLocation(),Sound.NOTE_BASS, 2, 1);
				        				}
			        						
		    						}
		    					}
		    					
		    					player.closeInventory();
		    					
		    	}else{
		    		
					player.playSound(player.getLocation(),Sound.NOTE_BASS, 2, 2);
					event.setCancelled(true);
					return;
			 }	
		 }
		}

		@EventHandler(priority=EventPriority.HIGH)
		public void onInventoryClose(InventoryCloseEvent event){
			
		Inventory inventory = event.getInventory();

		  if(inventory != null){
			  HandlerList.unregisterAll(listener);
		  }
		}
	};
		
		TreeMap<CompassPoint, ItemStack> configMap = CompassPoints.getConfigMap();
		
		StringBuilder playerName = new StringBuilder(otherPlayer);
		
		boolean addDots = false;
		
		while(playerName.length() > 14){
			playerName.deleteCharAt(playerName.length()-1);
			addDots = true;
		}
		
		if(addDots == true)
			playerName.insert(playerName.length(), "...");
		
			 inven = Bukkit.createInventory(player, 45, "§8" + playerName.toString() + "'s " + "Compass");

    	ItemStack fire = new ItemStack(Material.FIRE);
    	ItemMeta fireMeta = fire.getItemMeta();
    	fireMeta.setDisplayName("Delete");
    	fireMeta.setLore(Arrays.asList( ChatColor.DARK_RED + "Toggle Delete Mode"));
    	fire.setItemMeta(fireMeta);
    	
    	ItemStack bed = new ItemStack(Material.BED);
    	ItemMeta bedMeta = bed.getItemMeta();
    	bedMeta.setDisplayName("Bed Spawn");
    	bedMeta.setLore(Arrays.asList( ChatColor.GREEN + "Go to Bed Spawn"));
    	bed.setItemMeta(bedMeta);
    	
    	ItemStack anvil = new ItemStack(Material.ANVIL);
    	ItemMeta anvilMeta = anvil.getItemMeta();
    	anvilMeta.setDisplayName("Rename");
    	anvilMeta.setLore(Arrays.asList( ChatColor.GRAY + "Rename a Point", ChatColor.DARK_GREEN + " 7 Level Charge!"));
    	anvil.setItemMeta(anvilMeta);
    	
    if(player.getBedSpawnLocation() != null)
    	if(player.getWorld() != player.getBedSpawnLocation().getWorld() && !player.hasPermission("compasspoints.teleport")){
    		bedMeta.setLore(Arrays.asList( ChatColor.GREEN + "Go to Bed Spawn",
    											ChatColor.DARK_RED +"This point is in a different world!"));
    		bed.setItemMeta(bedMeta);
    	}
    	
    	ItemStack bookAndQuill = new ItemStack(Material.BOOK_AND_QUILL);
    	ItemMeta bookMeta = bookAndQuill.getItemMeta();
    	bookMeta.setDisplayName("Add CompassPoint");
    	bookMeta.setLore(Arrays.asList( ChatColor.BLUE + "Add a new CompassPoint at", ChatColor.BLUE +" your current location"));
    	bookAndQuill.setItemMeta(bookMeta);
    	
    	for(CompassPoint compassPoint: configMap.keySet()){
    		
    	ItemMeta configItemMeta =  configMap.get(compassPoint).getItemMeta();
   		 List<String> itemMetaLore = configItemMeta.getLore();
   		if(itemMetaLore.get(itemMetaLore.size()-1).toString().contains("This point is in different World! "))
   			 itemMetaLore.remove(itemMetaLore.size()-1);
   		
   		 configItemMeta.setLore(itemMetaLore);
   		 configMap.get(compassPoint).setItemMeta(configItemMeta);
    		
    		inven.setItem(compassPoint.getPosition()-1, configMap.get(compassPoint));
    	}
    if(player.hasPermission("compasspoints.rename")){
    	inven.setItem(7, anvil);
    }
    if(player.hasPermission("compasspoints.delete")){
    	inven.setItem(8, fire);
    }
    	inven.setItem(6, bed);
    	
    	int limit = 2;
		
    	if(player.hasPermission("compasspoints.row.5")){
    		limit = 45;
    	}else if(player.hasPermission("compasspoints.row.4")){
			limit = 36;
		}else if(player.hasPermission("compasspoints.row.3")){
			limit = 27;
		}else if(player.hasPermission("compasspoints.row.2")){
			limit = 18;
		}else if(player.hasPermission("compasspoints.row.1")){
			limit = 9;
		}
		
		ArrayList<CompassPoint> compassPoints = null;
		
			
			File playersInvenFile = null;
		
				File dir = new File(CompassPoints.getThisDataFolder()+File.separator+"Player"+File.separator);
				try {
					Files.createDirectories(dir.toPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
				 playersInvenFile = new File( dir, otherPlayer + ".yml");
			
			if(playersInvenFile.exists()){
				 compassPoints = PlayerCompassPointsFile.readPlayerFile(otherPlayer);

    int counter = 9;
    int compassPointInt = 0;
    int compassPointName = 1;
    	for(CompassPoint compassPoint : compassPoints){
    		
    		String worldName = "Overworld";
			
			if(compassPoint.getWorld() == Bukkit.getWorld("world_nether") || compassPoint.getWorld() == Bukkit.getWorld("nether")){
				worldName = "Nether";
			}else if(compassPoint.getWorld() == Bukkit.getWorld("world_the_end")){
				worldName = "The End";
			}else if(compassPoint.getWorld() == Bukkit.getWorld("world")){
				worldName = "Overworld";
			}else if(compassPoint.getWorld() == Bukkit.getWorld("flat")){
				worldName = "Flat world";
			}else{
				worldName = compassPoint.getWorld().getName().replace("world_", "");
			}
    		
    		ItemStack Map = new ItemStack(Material.MAP);
        	ItemMeta itemMeta = Map.getItemMeta();
        	if(compassPoint.getName().equals("DEFAULT"))
        		itemMeta.setDisplayName("Point " + compassPointName);
        	else
        		itemMeta.setDisplayName(compassPoint.getName());
        	if(player.hasPermission("compasspoints.teleport")) 
        		itemMeta.setLore(Arrays.asList(ChatColor.BLUE + "" +compassPoint.getX() +" ,"+ compassPoint.getY() +" ,"+ compassPoint.getZ() + ", " + worldName));
        	else
        		itemMeta.setLore(Arrays.asList(ChatColor.BLUE + "" +compassPoint.getX() +" ,"+ compassPoint.getY() +" ,"+ compassPoint.getZ()));
        	
        	if(compassPoints.size() >= 1)
        		if(compassPoints.get(compassPointInt).getWorld() != player.getWorld() && !player.hasPermission("compasspoints.teleport")){
        			itemMeta.setLore(Arrays.asList(ChatColor.BLUE + "" + compassPoint.getX() +" ,"+ compassPoint.getY() +" ,"+ compassPoint.getZ(),
        												ChatColor.DARK_RED + "This point is in a different world!"));
        		}else{
        			if(player.hasPermission("compasspoints.teleport")) 
        				if(compassPoint.getHeadLocation().getBlock().getType() == Material.WATER || compassPoint.getHeadLocation().getBlock().getType() == Material.STATIONARY_WATER){
  							 List<String> itemMetaLore = itemMeta.getLore();
  							 itemMetaLore.add(ChatColor.DARK_RED + "WARNING: §cThis point is under Water!");
  							 itemMeta.setLore(itemMetaLore);	
  							 
  						 }else if(compassPoint.getHeadLocation().getBlock().getType() != Material.AIR){
  							 List<String> itemMetaLore = itemMeta.getLore();
  							 itemMetaLore.add(ChatColor.DARK_RED + "WARNING: §cThis point is in a Block!");
  							 itemMeta.setLore(itemMetaLore);	
  						 }
					 }
        	
        	Map.setItemMeta(itemMeta);
    		
    		inven.setItem(counter, Map);
    		counter++;
    		compassPointInt++;
    		compassPointName++;
    	}
    	
    if(counter - 9 != limit )
    	inven.setItem(counter, bookAndQuill);
    	
		player.openInventory(inven);
		Bukkit.getPluginManager().registerEvents( listener, CompassPoints.getInstance());

	}else{
	player.sendMessage(messageData.get("commands.other.error.noplayer"));
}
}
}