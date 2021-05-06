package me.lordburtz.dropheads;

import me.lordburtz.dropheads.util.SkullCreator;
import org.bukkit.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;

public final class DropHeads extends JavaPlugin implements Listener, CommandExecutor {

    public Map<EntityType, Material> vanilla_mob_heads;
    public Map<EntityType, String> custom_mob_heads;

    public static Logger logger;
    public static String prefix = "[DropHeads] ";

    public static void log(String message) {
        logger.log(Level.INFO, prefix + message);
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        //this.getCommand("test").setExecutor(this);
        logger = Bukkit.getLogger();
        this.saveDefaultConfig();
        loadConfig();
        log("Drop Heads loaded");
    }

    @Override
    public void onDisable() {
        vanilla_mob_heads = null;
        custom_mob_heads = null;
    }

    public void loadConfig() {

        vanilla_mob_heads = new HashMap<>();
        custom_mob_heads = new HashMap<>();

        if(getConfig().getBoolean("enableDropCreeper"))
            vanilla_mob_heads.put(EntityType.CREEPER, Material.CREEPER_HEAD);
        if(getConfig().getBoolean("enableDropSkeleton"))
            vanilla_mob_heads.put(EntityType.SKELETON, Material.SKELETON_SKULL);
        if(getConfig().getBoolean("enableDropWitherSkeleton"))
            vanilla_mob_heads.put(EntityType.WITHER_SKELETON, Material.WITHER_SKELETON_SKULL);
        if(getConfig().getBoolean("enableDropZombie"))
            vanilla_mob_heads.put(EntityType.ZOMBIE, Material.ZOMBIE_HEAD);

        Map<String, String> customHeadMapIn = new HashMap<>();
        for(String key : getConfig().getConfigurationSection("customHeadMap").getKeys(false)) {
            customHeadMapIn.put(key, getConfig().getString("customHeadMap." + key));
        }
        for(String key : customHeadMapIn.keySet()) {
            EntityType type = EntityType.fromName(key);
            if(type != null) {
                log("Loaded custom head for \"" + key + "\"");
                custom_mob_heads.put(type, customHeadMapIn.get(key));
            } else {
                log("Unable to load custom head for \"" + key + "\" - that mob may not exist in this version of Minecraft!");
            }
        }

    }

    @EventHandler
    public void onMobKill(EntityDeathEvent event) {

        if(getConfig().getBoolean("mustBePlayerKill") && event.getEntity().getKiller() == null) return;

        String headItemName = ChatColor.WHITE + event.getEntity().getName() + " Head";

        if (vanilla_mob_heads.containsKey(event.getEntityType())) {
            ItemStack itemToDrop = new ItemStack(vanilla_mob_heads.get(event.getEntityType()));
            ItemMeta itemToDropMeta = itemToDrop.getItemMeta();
            itemToDropMeta.setDisplayName(headItemName);
            itemToDrop.setItemMeta(itemToDropMeta);
            event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), itemToDrop);
        } else if (custom_mob_heads.containsKey(event.getEntityType())) {
            dropCustomSkull(event.getEntity().getWorld(), event.getEntity().getLocation(), custom_mob_heads.get(event.getEntityType()), headItemName, event.getEntity().getName());
        }

    }

    public static void dropCustomSkull(World world, Location location, String skinBase64, String itemName, String mobname) {
        ItemStack item = SkullCreator.itemFromBase64(skinBase64);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(itemName);
        List<String> lore = new ArrayList<String>();
        lore.add(invisString(mobname));
        //WARNING: THE LORE IS HERE SET to prevenet null pointer exceptions; will add check for lore later
        meta.setLore(lore);
        item.setItemMeta(meta);
        world.dropItem(location, item);
    }

    public static String invisString(String s) {
        String hidden = "";
        for (char c : s.toCharArray()) hidden += ChatColor.COLOR_CHAR+""+c;
        return hidden;
    }

    //@Override
    //public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    //    ItemStack head = new ItemStack(Material.PLAYER_HEAD);
    //
    //    String base64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L" +
    //            "3RleHR1cmUvNTIyODRlMTMyYmZkNjU5YmM2YWRhNDk3YzRmYTMwOTRjZDkzMjMxYTZiNTA1YTEyY2U3Y2Q1MTM1YmE4ZmY5MyJ9fX0=";
    //
    //    String texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNmMWIzYjNmNTM5ZDJmNjNjMTcyZTk0Y2FjZmFhMzkxZThiMzg1Y2RkNjMzZjNiOTkxYzc0ZTQ0YjI4In19fQ";
    //
    //    String test_texture3 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjc4ZWYyZTRjZjJjNDFhMmQxNGJmZGU5Y2FmZjEwMjE5ZjViMWJmNWIzNWE0OWViNTFjNjQ2Nzg4MmNiNWYwIn19fQ=";
    //
    //    ((Player) sender).getInventory().addItem(SkullCreator.itemFromBase64(test_texture3));
    //    return true;
    //}

}
