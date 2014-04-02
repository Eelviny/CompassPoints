package compassPoints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
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
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CompassPointGUI{
	
	private HashMap<String, String> messageData = CompassPoints.getMessageData();
	private boolean deleteMode = false;
	private boolean renameMode = false;
	private Listener listener;
	private Inventory inven;
	
public CompassPointGUI(Player player){
		
	 listener = new Listener(){
		
	@EventHandler(priority=EventPriority.HIGH)
    public void onInventoryClick(final InventoryClickEvent event){
		
        final Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        
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
		        					
		        					boolean canTP = true;
		        					
		        					for(World configWorld: CompassPoints.getWorlds()){
		        						if(world.equals(configWorld)){
		        							canTP = true;
		        							break;
		        						}else{
		        							canTP = false;
		        						}
		        					}
		        				if(canTP){
	        						if(world.isChunkLoaded(world.getChunkAt(world.getSpawnLocation()))){
			        					
		        						player.teleport(compassPoint.getLocation());
		        						player.sendMessage(messageData.get("gui.teleport") + configMap.get(compassPoint).getItemMeta().getDisplayName());
		        					}else{
		        						event.setCancelled(true);
		        						player.sendMessage(messageData.get("gui.noworld"));
		        						player.playSound(player.getLocation(),Sound.NOTE_BASS, 2, 1);
		        					}
		        					
		        				}else{
		        					player.sendMessage(messageData.get("gui.noteleport"));
		        					player.playSound(player.getLocation(),Sound.NOTE_BASS, 2, 1);
		        				}
		        		}
							   
							   player.closeInventory();
						   }else{
							   
							 if(compassPoint.getWorld() == player.getWorld()){
							   if(player.getWorld() == Bukkit.getWorld("world_nether") || player.getWorld() == Bukkit.getWorld("nether")){
								   player.sendMessage(messageData.get("gui.pointcompass") + ChatColor.MAGIC + configMap.get(compassPoint).getItemMeta().getDisplayName());
								   player.sendMessage(messageData.get("gui.pointcompassinnether"));
								}else{
									 player.sendMessage(messageData.get("gui.pointcompass") + configMap.get(compassPoint).getItemMeta().getDisplayName());
								}
							   player.playSound( player.getLocation(),Sound.ITEM_PICKUP,5, 1);
							   player.setCompassTarget(compassPoint.getLocation());
							   player.closeInventory();
							   
							 }else{
							  player.playSound(player.getLocation(),Sound.NOTE_BASS, 2, 1);
						    }
						 
						  }
		  }
	}
		   
	  }else if(event.getCurrentItem().equals(anvil)){
				 
			   if(renameMode == false){
	    			player.playSound(player.getLocation(),Sound.PISTON_EXTEND, 3, 0);
	    			
	    			if(deleteMode == true){
	    				deleteMode = false;
	    			}
	    				renameMode = true;
	    				player.sendMessage(messageData.get("gui.rename.enter"));
	    			
	    			}else if (renameMode == true){
	    			
	    				player.playSound(player.getLocation(),Sound.PISTON_RETRACT, 3, 0);
	    				renameMode = false;
	    				player.sendMessage(messageData.get("gui.rename.exit"));
	    			}
		
		  
	  }else if (event.getCurrentItem().equals(bed)){
		  
	        	if(player.getBedSpawnLocation() != null){	
	        		if(player.hasPermission("compasspoints.teleport")){
	
	        				if(player.isInsideVehicle()){
	        					
	        					player.sendMessage(messageData.get("gui.ridingerror") + player.getVehicle().getType().toString().toLowerCase() + "!");
	        					player.playSound(player.getLocation(),Sound.NOTE_BASS, 2, 1);
	        				}else{
	        					World world = player.getBedSpawnLocation().getWorld();
        						
	        					boolean canTP = true;
	        					
	        					for(World configWorld: CompassPoints.getWorlds()){
	        						if(world.equals(configWorld)){
	        							canTP = true;
	        							break;
	        						}else{
	        							canTP = false;
	        						}
	        					}
	        				if(canTP){
	        					
        						if(world.isChunkLoaded(world.getChunkAt(world.getSpawnLocation()))){
	        						player.teleport(player.getBedSpawnLocation());
	        						player.sendMessage(messageData.get("gui.bed.teleport"));
	        						player.setCompassTarget((player.getBedSpawnLocation()));
	        					}else{
	        						event.setCancelled(true);
	        						player.sendMessage(messageData.get("gui.noworld"));
	        						player.playSound(player.getLocation(),Sound.NOTE_BASS, 2, 1);
	        					}
        						
	        				}else{
	        					player.sendMessage(messageData.get("gui.noteleport"));
	        					player.playSound(player.getLocation(),Sound.NOTE_BASS, 2, 1);
	        				}
	        			}
	        				   player.closeInventory();
	        		}else{
	        			
	        				player.sendMessage(messageData.get("gui.bed.pointcompass"));
	        				player.getWorld().playSound(player.getLocation(),Sound.ITEM_PICKUP, 5, 1);
	        				player.setCompassTarget((player.getBedSpawnLocation()));
	        				player.closeInventory();
	        		}
	        		
	        	}else{
	        		player.sendMessage(messageData.get("gui.bed.error"));
	        		player.getWorld().playSound(player.getLocation(),Sound.NOTE_BASS, 2, 1);
	        		player.closeInventory();
	        	}
	        		
	  }else if (event.getCurrentItem().equals(fire)){
	        		
	        		if(deleteMode == false){
	        			
	        			player.playSound(player.getLocation(),Sound.PISTON_EXTEND, 3, 0);
	        			
	        		if(renameMode == true){
	        				renameMode = false;
	        			}
	        		
	        			deleteMode = true;
	        			player.sendMessage(messageData.get("gui.delete.enter"));
	        			
	        		}else if (deleteMode == true){
	        			
	        			player.playSound(player.getLocation(),Sound.PISTON_RETRACT, 3, 0);
	        			
	        			deleteMode = false;
	        			player.sendMessage(messageData.get("gui.delete.exit"));
	        		}
	        		
	 }else if (event.getCurrentItem().equals(bookAndQuill)){
	        		
	        		ArrayList<CompassPoint> compassPoints = PlayerCompassPointsFile.readPlayerFile(player.getDisplayName());
	        		
	        		player.playSound(player.getLocation(),Sound.CLICK, 2, 1);
	        		
	        		player.sendMessage(messageData.get("gui.point.create")
								+ player.getLocation().getBlockX() + ", " +
									player.getLocation().getBlockY() + ", " +
										player.getLocation().getBlockZ());
	        		
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
	        		
	        			if(compassPoints.size() <= limit){
	        				compassPoints.add(new CompassPoint(player.getLocation().getBlockX(),
	        													 player.getLocation().getBlockY(),
	        														player.getLocation().getBlockZ(),
	        															player.getWorld(), NameUtils.RandomName(compassPoints)));
	        			
	        				PlayerCompassPointsFile.writePlayerFile(compassPoints ,player.getDisplayName());
	        			
	        				updateInven(player, inventory, limit, compassPoints);  	
	        			}
	        			
	  }else if (event.getCurrentItem().getType().equals(Material.MAP) && event.getCurrentItem().getItemMeta().getLore() != null){
	        		
	        		ArrayList<CompassPoint> compassPoints = PlayerCompassPointsFile.readPlayerFile(player.getDisplayName());
	        		
	        		if(renameMode == true){
	        		
	        		  if(player.getLevel() >= 7L || player.getGameMode().equals(GameMode.CREATIVE)){	
	        			final ItemStack map = new ItemStack(Material.MAP);
			        	ItemMeta itemMeta = map.getItemMeta();
			        	if(compassPoints.get(event.getSlot() - 9).getName().equals("DEFAULT"))
			        		itemMeta.setDisplayName("Point " + String.valueOf(event.getSlot() - 8));
			        	else
			        		itemMeta.setDisplayName(compassPoints.get(event.getSlot() - 9).getName());
			        	
			        		map.setItemMeta(itemMeta);
	        			 
						    AnvilGUI gui = new AnvilGUI(player, new AnvilGUI.AnvilClickEventHandler(){
								  @Override
								  public void onAnvilClick(AnvilGUI.AnvilClickEvent anvilEvent){
									  
									  ArrayList<CompassPoint> compassPoints = PlayerCompassPointsFile.readPlayerFile(player.getDisplayName());
									  
								 if(anvilEvent.getSlot() == AnvilGUI.AnvilSlot.OUTPUT && !anvilEvent.getName().isEmpty()){
								  anvilEvent.setWillDestroy(true);
								  
								
								  compassPoints.get(event.getSlot() - 9).setName(MessageUtils.FormatString(anvilEvent.getName()));
								  player.setLevel(player.getLevel() - 7);
								  
								  PlayerCompassPointsFile.writePlayerFile(compassPoints, player.getDisplayName());
								  
								 renameMode = false;
								 player.playSound(player.getLocation(),Sound.ANVIL_USE, 1, 1);
								 new CompassPointGUI(player);
								 
								  } else {
								  anvilEvent.setWillDestroy(false);
								  }
								  }
								  });
								   
								  gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, map);
								   
								  player.playSound(player.getLocation(),Sound.ANVIL_LAND, 1, 1);
								  gui.open();
	        		  }else{
	      				player.playSound(player.getLocation(),Sound.NOTE_BASS, 2, 1);
	      				player.sendMessage(messageData.get("gui.rename.noexp"));
	      				player.playSound(player.getLocation(),Sound.PISTON_RETRACT, 3, 0);
	        			
	    				renameMode = false;
	      			  }    
	        		}else if(deleteMode == false){
	        				
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
	        							
	        							boolean explosionIminent = false;
	        							
	        							if(player.getInventory().contains(new ItemStack(Material.TNT, 64))){
	        								for(int i = 0; i <= 8; i++){
			        						
			        						if(player.getInventory().getItem(i) != null)
			        							if(player.getInventory().getItem(i).equals(new ItemStack(Material.TNT, 64))){
			        								player.sendMessage(ChatColor.RED + "Can not teleport while holding a stack TNT in your hotbar! It may ignite in the teleporting process!");
			        								player.playSound(player.getLocation(),Sound.NOTE_BASS, 2, 1);
			        								explosionIminent = true;
			        								break;
			        							}
			        						}
	        							}
	        							
	        					if(!explosionIminent){
	        						
	        						World world = compassPoints.get(event.getSlot() - 9).getWorld();
	        						
	        						boolean canTP = true;
		        					
		        					for(World configWorld: CompassPoints.getWorlds()){
		        						if(world.equals(configWorld)){
		        							canTP = true;
		        							break;
		        						}else{
		        							canTP = false;
		        						}
		        					}
		        				if(canTP){
	        						
	        						if(world.isChunkLoaded(world.getChunkAt(world.getSpawnLocation()))){
	    								player.teleport(compassPoints.get(event.getSlot() - 9).getLocation());
	    								player.setCompassTarget(compassPoints.get(event.getSlot() - 9).getLocation());
	    								
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
		        				}else{
		        					player.sendMessage(messageData.get("gui.noteleport"));
		        					player.playSound(player.getLocation(),Sound.NOTE_BASS, 2, 1);
		        				}
	        				  }
	        				}
	        					player.closeInventory();
	        					
	        				}else{
	        					if(compassPoints.get(event.getSlot() - 9).getWorld() == player.getWorld()){
	        						if(player.getWorld() == Bukkit.getWorld("world_nether") || player.getWorld() == Bukkit.getWorld("nether")){
	        							
	        							player.sendMessage(messageData.get("gui.pointcompass")
	        												+ ChatColor.MAGIC + compassPoints.get(event.getSlot() - 9).getX() + ", " +
																					compassPoints.get(event.getSlot() - 9).getY() + ", " +
																						compassPoints.get(event.getSlot() - 9).getZ());
	        							
	        							player.sendMessage(messageData.get("gui.pointcompassinnether"));
	        							player.playSound(player.getLocation(),Sound.ITEM_PICKUP, 5, 1);
	        							player.closeInventory();
	        							
	        						}else{
	        							player.sendMessage(messageData.get("gui.pointcompass")
		   																		+ compassPoints.get(event.getSlot() - 9).getX() + ", " +
		   																			compassPoints.get(event.getSlot() - 9).getY() + ", " +
		   																				compassPoints.get(event.getSlot() - 9).getZ());
	        					
	        							player.setCompassTarget(compassPoints.get(event.getSlot() - 9).getLocation());
	        							player.playSound(player.getLocation(),Sound.ITEM_PICKUP, 5, 1);
	        							player.closeInventory();
	        						}
	        						
	        					}else{
	        						player.playSound(player.getLocation(),Sound.NOTE_BASS, 2, 1);
	        					}
	        				}
	
	        		}else{
	        			
	        			player.playSound( player.getLocation(),Sound.GLASS,2, 1);
	        			
	        			player.sendMessage(messageData.get("gui.point.delete")
									+ compassPoints.get(event.getSlot() - 9).getX() + ", " +
										compassPoints.get(event.getSlot() - 9).getY() + ", " +
											compassPoints.get(event.getSlot() - 9).getZ());
	        			
	        			compassPoints.remove(event.getSlot() - 9);
	        			PlayerCompassPointsFile.writePlayerFile(compassPoints ,player.getDisplayName());
	        			
	        			 compassPoints = PlayerCompassPointsFile.readPlayerFile(player.getDisplayName());
	        			
	        			 int size = 18;
	 		    		
	 		    		if(player.hasPermission("compasspoints.row.5")){
	 		        		size = 54;
	 		        	}else if(player.hasPermission("compasspoints.row.4")){
	 	        			size = 45;
	 	        		}else if(player.hasPermission("compasspoints.row.3")){
	 	        			size = 36;
	 	        		}else if(player.hasPermission("compasspoints.row.2")){
	 	        			size = 27;
	 	        		}else if(player.hasPermission("compasspoints.row.1")){
	 	        			size = 18;
	 	        		}
	        			 
	        			for(int I = 9; I < size; I++)
	        			 inventory.clear(I);
	        			
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
	        			 
	        			updateInven(player, inventory, limit, compassPoints);
	        			
	        			player.playSound(player.getLocation(),Sound.PISTON_RETRACT, 3, 0);
	        			deleteMode = false;
	        		}
	        	}
	  
	          event.setCancelled(true);
	        }
	    }
	
private void updateInven(Player player, Inventory inventory, int limit, ArrayList<CompassPoint> compassPoints){

ItemStack bookAndQuill = new ItemStack(Material.BOOK_AND_QUILL);
ItemMeta bookMeta = bookAndQuill.getItemMeta();
bookMeta.setDisplayName("Add CompassPoint");
bookMeta.setLore(Arrays.asList( ChatColor.BLUE + "Add a new CompassPoint at", ChatColor.BLUE + " your current location"));
bookAndQuill.setItemMeta(bookMeta);

ItemStack bed = new ItemStack(Material.BED);
ItemMeta bedMeta = bed.getItemMeta();
bedMeta.setDisplayName("Bed Spawn");
bedMeta.setLore(Arrays.asList( ChatColor.GREEN + "Go to Bed Spawn"));
bed.setItemMeta(bedMeta);

if(player.getBedSpawnLocation() != null)
if(player.getWorld() != player.getBedSpawnLocation().getWorld() && !player.hasPermission("compasspoints.teleport")){
	if(player.getWorld() != Bukkit.getWorld("world")){
		bedMeta.setLore(Arrays.asList( ChatColor.GREEN + "Go to Bed Spawn",
											ChatColor.DARK_RED + "This point is in a different world!"));
		bed.setItemMeta(bedMeta);
	}
}

inventory.setItem(6, bed);

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
    			itemMeta.setLore(Arrays.asList(ChatColor.BLUE + "" +compassPoint.getX() +" ,"+ compassPoint.getY() +" ,"+ compassPoint.getZ(),
    												ChatColor.DARK_RED + "This point is in a different world!"));
    		}else{
    			if(player.hasPermission("compasspoints.teleport") && compassPoint.getHeadLocation().getWorld() != null)
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
		
		inventory.setItem(counter, Map);
		counter++;
		compassPointInt++;
		compassPointName++;
	}
	
	if(counter - 9 != limit )
		inventory.setItem(counter, bookAndQuill);
}

@EventHandler(priority=EventPriority.HIGH)
public void onInventoryClose(InventoryCloseEvent event){

final Player player = (Player) event.getPlayer();
Inventory inventory = event.getInventory();

  if(inventory != null){
	  if(inventory.getType().equals(InventoryType.ANVIL) || inventory.getName().equals(inven.getName()) && inventory.getHolder().equals(inven.getHolder())){
		  
		  if(deleteMode == true){
			  deleteMode = false;
			  player.sendMessage(messageData.get("gui.delete.exit"));
    	}  
		  
		  if(renameMode == true){
			  renameMode = false;
			  player.sendMessage(messageData.get("gui.rename.exit"));
    	}  
		  
		  HandlerList.unregisterAll(listener);
		  
	  }
  }
}
};
		int size = 18;
		
		if(player.hasPermission("compasspoints.row.5")){
    		size = 54;
    	}else if(player.hasPermission("compasspoints.row.4")){
			size = 45;
		}else if(player.hasPermission("compasspoints.row.3")){
			size = 36;
		}else if(player.hasPermission("compasspoints.row.2")){
			size = 27;
		}else if(player.hasPermission("compasspoints.row.1")){
			size = 18;
		}
		
	 inven = Bukkit.createInventory(player, size, "§8Compass");
	
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
		bedMeta.setLore(Arrays.asList(  ChatColor.GREEN + "Go to Bed Spawn",
											ChatColor.DARK_RED + "This point is in a different world!"));
		bed.setItemMeta(bedMeta);
	}
	
	ItemStack bookAndQuill = new ItemStack(Material.BOOK_AND_QUILL);
	ItemMeta bookMeta = bookAndQuill.getItemMeta();
	bookMeta.setDisplayName("Add CompassPoint");
	bookMeta.setLore(Arrays.asList( ChatColor.BLUE + "Add a new CompassPoint at", ChatColor.BLUE +" your current location"));
	bookAndQuill.setItemMeta(bookMeta);
	
	TreeMap<CompassPoint, ItemStack> configMap = CompassPoints.getConfigMap();

		
	for(CompassPoint compassPoint: configMap.keySet()){
		
		ItemMeta configItemMeta =  configMap.get(compassPoint).getItemMeta();
		 List<String> itemMetaLore = configItemMeta.getLore();
		if(itemMetaLore.get(itemMetaLore.size()-1).toString().contains("This point is in different World! "))
			 itemMetaLore.remove(itemMetaLore.size()-1);
		
		 configItemMeta.setLore(itemMetaLore);
		 configMap.get(compassPoint).setItemMeta(configItemMeta);
		 
		
		if(player.getWorld() != compassPoint.getWorld() && !player.hasPermission("compasspoints.teleport")){
		
			 itemMetaLore.add(ChatColor.DARK_RED +"This point is in different World! ");
			 configItemMeta.setLore(itemMetaLore);
			 configMap.get(compassPoint).setItemMeta(configItemMeta);
			 
			 inven.setItem(compassPoint.getPosition()-1,  configMap.get(compassPoint));
		}else{
			inven.setItem(compassPoint.getPosition()-1, configMap.get(compassPoint));
		}
	}
	
		if(player.hasPermission("compasspoints.rename")){
    	inven.setItem(7, anvil);
    }
		if(player.hasPermission("compasspoints.delete")){
    	inven.setItem(8, fire);
    }
	inven.setItem(6, bed);
	
	int limit = 9;

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

	ArrayList<CompassPoint> compassPoints = PlayerCompassPointsFile.readPlayerFile(player.getDisplayName());
	
boolean trim = false;
	while(compassPoints.size() > limit){
		compassPoints.remove(compassPoints.size()-1);
		trim = true;
	}
	
if(trim == true)
	PlayerCompassPointsFile.writePlayerFile(compassPoints ,player.getDisplayName());
	
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
					 
					if(player.hasPermission("compasspoints.teleport") && compassPoint.getHeadLocation().getWorld() != null)
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
		
}
	
}


