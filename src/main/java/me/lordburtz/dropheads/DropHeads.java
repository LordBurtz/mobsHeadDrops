package me.lordburtz.dropheads;

import me.lordburtz.dropheads.commands.SellHeads;
import me.lordburtz.dropheads.util.SkullCreator;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.RegisteredServiceProvider;
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

    public static DropHeads plugin;
    public static Logger logger;
    public static String prefix = "[DropHeads] ";
    public static NamespacedKey key;

    public static void log(String message) {
        logger.log(Level.INFO, prefix + message);
    }
    public static void log(String msg, boolean bool) {logger.log(Level.SEVERE, msg);}

    private static Economy econ = null;

    @Override
    public void onEnable() {
        plugin = this;
        Bukkit.getPluginManager().registerEvents(this, this);
        //this.getCommand("test").setExecutor(this);
        logger = Bukkit.getLogger();
        this.saveDefaultConfig();
        loadConfig();

        if (!setupEconomy() ) {
            log(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        new SellHeads(this, econ);
        key = new NamespacedKey(plugin, "mobtype");

        log("Drop Heads loaded");
    }

    public static Economy getEconomy() {
        return econ;
    }

    @Override
    public void onDisable() {
        vanilla_mob_heads = null;
        custom_mob_heads = null;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
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
            //use valueOf as fromeName is deprecated
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
            dropCustomSkull(event.getEntity().getWorld(), event.getEntity().getLocation(), custom_mob_heads.get(event.getEntityType()), headItemName, event.getEntity().getName(), event.getEntityType().name());
        }

    }

    public NamespacedKey getKey() {
        return new NamespacedKey(this, "mobtype");
    }

    public static void dropCustomSkull(World world, Location location, String skinBase64, String itemName, String mobname, String type) {
        ItemStack item = SkullCreator.itemFromBase64(skinBase64);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(itemName);

        //this lore can safely be removed
        List<String> lore;
        if (meta.hasLore()) {
            lore = meta.getLore();
            lore.add(invisString(mobname));
        } else {
            lore = new ArrayList<String>();
            lore.add(invisString(mobname));
        }
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, type);
        meta.setLore(lore);
        item.setItemMeta(meta);
        world.dropItem(location, item);
    }

    public static String invisString(String s) {
        String hidden = "";
        for (char c : s.toCharArray()) hidden += ChatColor.COLOR_CHAR + "" + c;
        return hidden;
    }
}
