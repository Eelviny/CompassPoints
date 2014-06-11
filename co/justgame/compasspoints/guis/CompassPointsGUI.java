package co.justgame.compasspoints.guis;

import java.util.ArrayList;
import java.util.Arrays;
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

import co.justgame.compasspoints.compasspoint.CompassPoint;
import co.justgame.compasspoints.io.CompassPointsIO;
import co.justgame.compasspoints.main.CompassPoints;
import co.justgame.compasspoints.resources.Messages;
import co.justgame.compasspoints.resources.NameUtils;

public class CompassPointsGUI {

    private ArrayList<CompassPoint> compassPoints;

    public static enum TYPE {
        EXTERNAL, PRIVATE
    }

    private boolean deleteMode = false;
    private boolean renameMode = false;
    private Listener listener;
    private Inventory inven;

    private int LIMIT = 2;

    public CompassPointsGUI(Player p, final TYPE type, final String op){

        if(type == TYPE.PRIVATE)
            compassPoints = CompassPointsIO.readPlayerFile(p.getDisplayName(), true);
        else if(CompassPointsIO.hasFile(op))
            compassPoints = CompassPointsIO.readPlayerFile(op, true);
        else{
            p.sendMessage(Messages.get("commands.other.error.noplayer"));
            return;
        }

        if(p.hasPermission("compasspoints.row.5")){
            LIMIT = 45;
        }else if(p.hasPermission("compasspoints.row.4")){
            LIMIT = 36;
        }else if(p.hasPermission("compasspoints.row.3")){
            LIMIT = 27;
        }else if(p.hasPermission("compasspoints.row.2")){
            LIMIT = 18;
        }else if(p.hasPermission("compasspoints.row.1")){
            LIMIT = 9;
        }

        final ItemStack FIRE = new ItemStack(Material.FIRE);
        ItemMeta fireMeta = FIRE.getItemMeta();
        fireMeta.setDisplayName("Delete");
        fireMeta.setLore(Arrays.asList(ChatColor.DARK_RED + "Toggle Delete Mode"));
        FIRE.setItemMeta(fireMeta);

        final ItemStack BED = new ItemStack(Material.BED);
        ItemMeta bedMeta = BED.getItemMeta();
        bedMeta.setDisplayName("Bed Spawn");
        bedMeta.setLore(Arrays.asList(ChatColor.GREEN + "Go to Bed Spawn"));
        BED.setItemMeta(bedMeta);

        if(p.getBedSpawnLocation() != null)
            if(p.getWorld() != p.getBedSpawnLocation().getWorld() && !p.hasPermission("compasspoints.teleport")){
                bedMeta.setLore(Arrays.asList(ChatColor.GREEN + "Go to Bed Spawn", ChatColor.DARK_RED
                        + "This point is in a different world!"));
                BED.setItemMeta(bedMeta);
            }

        final ItemStack BOOK_AND_QUILL = new ItemStack(Material.BOOK_AND_QUILL);
        ItemMeta bookMeta = BOOK_AND_QUILL.getItemMeta();
        bookMeta.setDisplayName("Add CompassPoint");
        bookMeta.setLore(Arrays.asList(ChatColor.BLUE + "Add a new CompassPoint at", ChatColor.BLUE + " your current location"));
        BOOK_AND_QUILL.setItemMeta(bookMeta);

        final ItemStack ANVIL = new ItemStack(Material.ANVIL);
        ItemMeta anvilMeta = ANVIL.getItemMeta();
        anvilMeta.setDisplayName("Rename");
        anvilMeta.setLore(Arrays.asList(ChatColor.GRAY + "Rename a Point", ChatColor.DARK_GREEN + " 7 Level Charge!"));
        ANVIL.setItemMeta(anvilMeta);

        listener = new Listener(){

            @EventHandler(priority = EventPriority.HIGH)
            public void onInventoryClick(final InventoryClickEvent e){
                final Player p = (Player) e.getWhoClicked();
                Inventory in = e.getInventory();

                if(in != null && e.getCurrentItem() != null)
                    if(inven.getName().equals(in.getName()) && inven.getHolder().equals(in.getHolder())){
                        e.setCancelled(true);

                        ItemStack is = e.getCurrentItem();

                        if(e.getSlot() <= 5){
                            TreeMap<CompassPoint, ItemStack> configMap = CompassPoints.getConfigMap();

                            for(CompassPoint compassPoint: configMap.keySet()){
                                if(is.equals(configMap.get(compassPoint))){
                                    if(p.hasPermission("compasspoints.teleport")){
                                        if(p.isInsideVehicle()) p.leaveVehicle();

                                        World w = compassPoint.getWorld();
                                        if(CompassPoints.safeToTP(w)){
                                            p.teleport(compassPoint.getLocation());
                                            p.sendMessage(Messages.get("gui.teleport")
                                                    + configMap.get(compassPoint).getItemMeta().getDisplayName());

                                        }else{
                                            e.setCancelled(true);
                                            p.sendMessage(Messages.get("gui.noworld"));
                                            p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
                                        }

                                        p.closeInventory();
                                    }else{
                                        if(compassPoint.getWorld() == p.getWorld()){
                                            if(p.getWorld() == Bukkit.getWorld("world_nether")
                                                    || p.getWorld() == Bukkit.getWorld("nether")){
                                                p.sendMessage(Messages.get("gui.pointcompass") + ChatColor.MAGIC
                                                        + configMap.get(compassPoint).getItemMeta().getDisplayName());
                                                p.sendMessage(Messages.get("gui.pointcompassinnether"));
                                            }else{
                                                p.sendMessage(Messages.get("gui.pointcompass")
                                                        + configMap.get(compassPoint).getItemMeta().getDisplayName());
                                            }

                                            p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 5, 1);
                                            p.setCompassTarget(compassPoint.getLocation());
                                            p.closeInventory();

                                        }else{
                                            p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
                                        }
                                    }
                                }
                            }
                        }else if(is.equals(ANVIL) && type == TYPE.PRIVATE){
                            if(renameMode == false){
                                if(deleteMode == true) deleteMode = false;

                                renameMode = true;
                                p.playSound(p.getLocation(), Sound.PISTON_EXTEND, 3, 0);
                                p.sendMessage(Messages.get("gui.rename.enter"));

                            }else{
                                renameMode = false;
                                p.playSound(p.getLocation(), Sound.PISTON_RETRACT, 3, 0);
                                p.sendMessage(Messages.get("gui.rename.exit"));
                            }
                        }else if(is.equals(FIRE) && type == TYPE.PRIVATE){
                            if(deleteMode == false){
                                if(renameMode == true) renameMode = false;

                                deleteMode = true;
                                p.playSound(p.getLocation(), Sound.PISTON_EXTEND, 3, 0);
                                p.sendMessage(Messages.get("gui.delete.enter"));

                            }else{
                                deleteMode = false;
                                p.playSound(p.getLocation(), Sound.PISTON_RETRACT, 3, 0);
                                p.sendMessage(Messages.get("gui.delete.exit"));
                            }
                        }else if(is.equals(BED)){
                            if(type == TYPE.PRIVATE){
                                if(p.getBedSpawnLocation() != null){
                                    if(p.hasPermission("compasspoints.teleport")){

                                        if(p.isInsideVehicle()) p.leaveVehicle();

                                        if(CompassPoints.safeToTP(p.getBedSpawnLocation().getWorld())){
                                            p.teleport(p.getBedSpawnLocation());
                                            p.sendMessage(Messages.get("gui.bed.teleport"));
                                            p.setCompassTarget((p.getBedSpawnLocation()));
                                        }else{
                                            p.sendMessage(Messages.get("gui.noworld"));
                                            p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
                                        }
                                        p.closeInventory();
                                    }else{
                                        p.sendMessage(Messages.get("gui.bed.pointcompass"));
                                        p.getWorld().playSound(p.getLocation(), Sound.ITEM_PICKUP, 5, 1);
                                        p.setCompassTarget((p.getBedSpawnLocation()));
                                        p.closeInventory();
                                    }
                                }else{
                                    p.sendMessage(Messages.get("gui.bed.error"));
                                    p.getWorld().playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
                                    p.closeInventory();
                                }
                            }else{
                                @SuppressWarnings("deprecation")
                                Player ip = Bukkit.getPlayerExact(op);

                                if(ip == null){
                                    p.sendMessage(Messages.get("commands.other.error.offlinebed"));
                                    p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
                                }else{

                                    if(ip.getBedSpawnLocation() != null){
                                        if(p.isInsideVehicle()) p.leaveVehicle();

                                        if(CompassPoints.safeToTP(p.getBedSpawnLocation().getWorld())){
                                            p.teleport(ip.getBedSpawnLocation());
                                            p.sendMessage(Messages.get("gui.teleport") + ip.getName() + "'s" + " Bed Spawn");
                                        }else{
                                            p.sendMessage(Messages.get("gui.noworld"));
                                            p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
                                        }
                                        p.closeInventory();
                                    }else{
                                        p.sendMessage(Messages.get("commands.other.error.nobed"));
                                        p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
                                        p.closeInventory();
                                    }
                                }
                            }
                        }else if(is.equals(BOOK_AND_QUILL) && type == TYPE.PRIVATE){
                            p.playSound(p.getLocation(), Sound.CLICK, 2, 1);

                            p.sendMessage(Messages.get("gui.point.create") + p.getLocation().getBlockX() + ", "
                                    + p.getLocation().getBlockY() + ", " + p.getLocation().getBlockZ());

                            if(compassPoints.size() <= LIMIT){
                                compassPoints.add(new CompassPoint(p.getLocation().getBlockX(), p.getLocation().getBlockY(), p
                                        .getLocation().getBlockZ(), p.getWorld(), NameUtils.RandomName(compassPoints)));

                                drawGUI(p, true);
                            }
                        }else if(is.getType().equals(Material.MAP) && is.getItemMeta().getLore() != null){
                            if(deleteMode){

                                deleteMode = false;

                                p.playSound(p.getLocation(), Sound.GLASS, 2, 1);

                                CompassPoint cp = compassPoints.get(e.getSlot() - 9);
                                p.sendMessage(Messages.get("gui.point.delete") + cp.getX() + ", " + cp.getY() + ", " + cp.getZ());

                                compassPoints.remove(e.getSlot() - 9);
                                p.playSound(p.getLocation(), Sound.PISTON_RETRACT, 3, 0);

                                drawGUI(p, true);
                            }else if(renameMode){

                                renameMode = false;
                                if(p.getLevel() >= 7L || p.getGameMode().equals(GameMode.CREATIVE)){
                                    final ItemStack map = new ItemStack(Material.MAP);
                                    ItemMeta itemMeta = map.getItemMeta();
                                    if(compassPoints.get(e.getSlot() - 9).getName().equals("DEFAULT"))
                                        itemMeta.setDisplayName("Point " + String.valueOf(e.getSlot() - 8));
                                    else
                                        itemMeta.setDisplayName(compassPoints.get(e.getSlot() - 9).getName());

                                    map.setItemMeta(itemMeta);

                                    AnvilGUI gui = new AnvilGUI(p, new AnvilGUI.AnvilClickEventHandler(){

                                        @Override
                                        public void onAnvilClick(AnvilGUI.AnvilClickEvent anvilEvent){

                                            if(anvilEvent.getSlot() == AnvilGUI.AnvilSlot.OUTPUT
                                                    && !anvilEvent.getName().isEmpty()){
                                                anvilEvent.setWillDestroy(true);

                                                compassPoints.get(e.getSlot() - 9).setName(Messages.formatString(anvilEvent
                                                        .getName()));
                                                CompassPointsIO.writePlayerFile(compassPoints, p.getDisplayName(), true);
                                                p.setLevel(p.getLevel() - 7);

                                                p.playSound(p.getLocation(), Sound.ANVIL_USE, 1, 1);
                                                new CompassPointsGUI(p, TYPE.PRIVATE, null);

                                            }else{
                                                anvilEvent.setWillDestroy(false);
                                            }
                                        }
                                    });
                                    gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, map);
                                    p.playSound(p.getLocation(), Sound.ANVIL_LAND, 1, 1);
                                    gui.open();
                                }else{
                                    p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
                                    p.sendMessage(Messages.get("gui.rename.noexp"));
                                    p.playSound(p.getLocation(), Sound.PISTON_RETRACT, 3, 0);
                                }
                            }else if(!renameMode && !deleteMode){
                                if(p.hasPermission("compasspoints.teleport")){
                                    String wn = "Overworld";

                                    World w = compassPoints.get(e.getSlot() - 9).getWorld();

                                    if(w == Bukkit.getWorld("world_nether") || w == Bukkit.getWorld("nether")){
                                        wn = "Nether";
                                    }else if(w == Bukkit.getWorld("world_the_end")){
                                        wn = "The End";
                                    }else if(w == Bukkit.getWorld("world")){
                                        wn = "Overworld";
                                    }else if(w == Bukkit.getWorld("flat")){
                                        wn = "Flat world";
                                    }else{
                                        wn = w.getName().replace("world_", "");
                                    }

                                    if(p.isInsideVehicle()) p.leaveVehicle();

                                    if(CompassPoints.safeToTP(w)){
                                        CompassPoint cp = compassPoints.get(e.getSlot() - 9);

                                        p.teleport(cp.getLocation());
                                        p.setCompassTarget(cp.getLocation());

                                        if(cp.getName().equals("DEFAULT")){

                                            p.sendMessage(Messages.get("gui.teleport") + cp.getX() + ", " + cp.getY() + ", "
                                                    + cp.getZ() + " in the " + wn);
                                        }else{

                                            StringBuilder colorBuilder = new StringBuilder(Messages.get("gui.teleport"));
                                            StringBuilder color = new StringBuilder();
                                            color.append("§");
                                            color.append(colorBuilder.charAt(colorBuilder.lastIndexOf("§") + 1));

                                            p.sendMessage(Messages.get("gui.teleport") + cp.getName() + color.toString() + " ("
                                                    + cp.getX() + ", " + cp.getY() + ", " + cp.getZ() + " in the " + wn + ") ");
                                        }
                                    }else{
                                        p.sendMessage(Messages.get("gui.noworld"));
                                        p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
                                    }
                                    p.closeInventory();

                                }else if(!p.hasPermission("compasspoints.teleport")){
                                    CompassPoint cp = compassPoints.get(e.getSlot() - 9);

                                    if(cp.getWorld() == p.getWorld()){
                                        if(p.getWorld() == Bukkit.getWorld("world_nether")
                                                || p.getWorld() == Bukkit.getWorld("nether")){

                                            p.sendMessage(Messages.get("gui.pointcompass") + ChatColor.MAGIC + cp.getX() + ", "
                                                    + cp.getY() + ", " + cp.getZ());

                                            p.sendMessage(Messages.get("gui.pointcompassinnether"));
                                            p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 5, 1);
                                            p.closeInventory();

                                        }else{
                                            p.sendMessage(Messages.get("gui.pointcompass") + cp.getX() + ", " + cp.getY() + ", "
                                                    + cp.getZ());

                                            p.setCompassTarget(compassPoints.get(e.getSlot() - 9).getLocation());
                                            p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 5, 1);
                                            p.closeInventory();
                                        }

                                    }else{
                                        p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
                                    }
                                }
                            }
                        }
                    }
            }

            @EventHandler(priority = EventPriority.HIGH)
            public void onInventoryClose(InventoryCloseEvent e){
                final Player p = (Player) e.getPlayer();
                Inventory in = e.getInventory();

                if(in != null){
                    if(in.getType().equals(InventoryType.ANVIL) || in.getName().equals(inven.getName())
                            && in.getHolder().equals(inven.getHolder())){

                        if(deleteMode == true){
                            deleteMode = false;
                            p.sendMessage(Messages.get("gui.delete.exit"));
                        }

                        if(renameMode == true){
                            renameMode = false;
                            p.sendMessage(Messages.get("gui.rename.exit"));
                        }

                        HandlerList.unregisterAll(listener);
                        if(type == TYPE.PRIVATE)
                            CompassPointsIO.writePlayerFile(compassPoints, p.getDisplayName(), true);
                        else
                            CompassPointsIO.writePlayerFile(compassPoints, op, true);
                    }
                }
            }
        };

        drawGUI(p, false);
    }

    private void drawGUI(Player p, boolean redraw){
        final ItemStack FIRE = new ItemStack(Material.FIRE);
        ItemMeta fireMeta = FIRE.getItemMeta();
        fireMeta.setDisplayName("Delete");
        fireMeta.setLore(Arrays.asList(ChatColor.DARK_RED + "Toggle Delete Mode"));
        FIRE.setItemMeta(fireMeta);

        final ItemStack BED = new ItemStack(Material.BED);
        ItemMeta bedMeta = BED.getItemMeta();
        bedMeta.setDisplayName("Bed Spawn");
        bedMeta.setLore(Arrays.asList(ChatColor.GREEN + "Go to Bed Spawn"));
        BED.setItemMeta(bedMeta);

        if(p.getBedSpawnLocation() != null)
            if(p.getWorld() != p.getBedSpawnLocation().getWorld() && !p.hasPermission("compasspoints.teleport")){
                bedMeta.setLore(Arrays.asList(ChatColor.GREEN + "Go to Bed Spawn", ChatColor.DARK_RED
                        + "This point is in a different world!"));
                BED.setItemMeta(bedMeta);
            }

        final ItemStack BOOK_AND_QUILL = new ItemStack(Material.BOOK_AND_QUILL);
        ItemMeta bookMeta = BOOK_AND_QUILL.getItemMeta();
        bookMeta.setDisplayName("Add CompassPoint");
        bookMeta.setLore(Arrays.asList(ChatColor.BLUE + "Add a new CompassPoint at", ChatColor.BLUE + " your current location"));
        BOOK_AND_QUILL.setItemMeta(bookMeta);

        final ItemStack ANVIL = new ItemStack(Material.ANVIL);
        ItemMeta anvilMeta = ANVIL.getItemMeta();
        anvilMeta.setDisplayName("Rename");
        anvilMeta.setLore(Arrays.asList(ChatColor.GRAY + "Rename a Point", ChatColor.DARK_GREEN + " 7 Level Charge!"));
        ANVIL.setItemMeta(anvilMeta);

        if(!redraw)
            inven = Bukkit.createInventory(p, LIMIT + 9, "§8Compass");
        else
            inven.clear();

        TreeMap<CompassPoint, ItemStack> configMap = CompassPoints.getConfigMap();

        for(CompassPoint compassPoint: configMap.keySet()){

            ItemMeta configItemMeta = configMap.get(compassPoint).getItemMeta();
            List<String> itemMetaLore = configItemMeta.getLore();
            if(itemMetaLore.get(itemMetaLore.size() - 1).toString().contains("This point is in different World! "))
                itemMetaLore.remove(itemMetaLore.size() - 1);

            configItemMeta.setLore(itemMetaLore);
            configMap.get(compassPoint).setItemMeta(configItemMeta);

            if(p.getWorld() != compassPoint.getWorld() && !p.hasPermission("compasspoints.teleport")){

                itemMetaLore.add(ChatColor.DARK_RED + "This point is in different World! ");
                configItemMeta.setLore(itemMetaLore);
                configMap.get(compassPoint).setItemMeta(configItemMeta);

                inven.setItem(compassPoint.getPosition() - 1, configMap.get(compassPoint));
            }else{
                inven.setItem(compassPoint.getPosition() - 1, configMap.get(compassPoint));
            }
        }

        if(p.hasPermission("compasspoints.rename")) inven.setItem(7, ANVIL);
        if(p.hasPermission("compasspoints.delete")) inven.setItem(8, FIRE);
        inven.setItem(6, BED);

        int counter = 9;
        int compassPointInt = 0;
        int compassPointName = 1;
        for(int i = 0; i < LIMIT; i++){
            if(compassPoints.size() > i){
                CompassPoint cp = compassPoints.get(i);

                String wn = "Overworld";
                World w = cp.getWorld();

                if(w == Bukkit.getWorld("world_nether") || w == Bukkit.getWorld("nether")){
                    wn = "Nether";
                }else if(w == Bukkit.getWorld("world_the_end")){
                    wn = "The End";
                }else if(w == Bukkit.getWorld("world")){
                    wn = "Overworld";
                }else if(w == Bukkit.getWorld("flat")){
                    wn = "Flat world";
                }else{
                    wn = w.getName().replace("world_", "");
                }

                ItemStack Map = new ItemStack(Material.MAP);
                ItemMeta itemMeta = Map.getItemMeta();
                if(cp.getName().equals("DEFAULT"))
                    itemMeta.setDisplayName("Point " + compassPointName);
                else
                    itemMeta.setDisplayName(cp.getName());
                if(p.hasPermission("compasspoints.teleport"))
                    itemMeta.setLore(Arrays.asList(ChatColor.BLUE + "" + cp.getX() + " ," + cp.getY() + " ," + cp.getZ() + ", "
                            + wn));
                else
                    itemMeta.setLore(Arrays.asList(ChatColor.BLUE + "" + cp.getX() + " ," + cp.getY() + " ," + cp.getZ()));

                if(compassPoints.size() >= 1)
                    if(compassPoints.get(compassPointInt).getWorld() != p.getWorld()
                            && !p.hasPermission("compasspoints.teleport")){
                        itemMeta.setLore(Arrays.asList(ChatColor.BLUE + "" + cp.getX() + " ," + cp.getY() + " ," + cp.getZ(), ChatColor.DARK_RED
                                + "This point is in a different world!"));
                    }else{

                        if(p.hasPermission("compasspoints.teleport") && cp.getHeadLocation().getWorld() != null)
                            if(cp.getHeadLocation().getBlock().getType() == Material.WATER
                                    || cp.getHeadLocation().getBlock().getType() == Material.STATIONARY_WATER){

                                List<String> itemMetaLore = itemMeta.getLore();
                                itemMetaLore.add(ChatColor.DARK_RED + "WARNING: §cThis point is under Water!");
                                itemMeta.setLore(itemMetaLore);

                            }else if(cp.getHeadLocation().getBlock().getType() != Material.AIR){
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
            
            if(counter - 9 != LIMIT) inven.setItem(counter, BOOK_AND_QUILL);
        }
        if(!redraw){
            p.openInventory(inven);
            Bukkit.getPluginManager().registerEvents(listener, CompassPoints.getInstance());
        }
    }
}
