package me.lordburtz.dropheads;

import me.lordburtz.dropheads.commands.SellHeads;
import me.lordburtz.dropheads.listeners.SpawnerPlace;
import me.lordburtz.dropheads.util.SkullCreator;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DropHeads extends JavaPlugin implements Listener {

    public Map<EntityType, Material> vanilla_mob_heads;
    public Map<EntityType, String> custom_mob_heads;

    public static DropHeads plugin;
    public static Logger logger;
    public static String prefix = "[DropHeads] ";
    public static NamespacedKey key_mobType;
    public static NamespacedKey key_playerXP;


    public static void log(String message) {
        logger.log(Level.INFO, prefix + message);
    }
    public static void log(String msg, boolean bool) {logger.log(Level.SEVERE, msg);}

    private static Economy econ = null;

    @Override
    public void onEnable() {
        plugin = this;
        Bukkit.getPluginManager().registerEvents(this, this);
        logger = Bukkit.getLogger();
        this.saveDefaultConfig();
        loadConfig();

        if (!setupEconomy() ) {
            log(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        new SellHeads(this, econ);
        new SpawnerPlace(this);

        key_mobType = new NamespacedKey(plugin, "mobtype");
        key_playerXP = new NamespacedKey(plugin, "mobKillXp");

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
        int xp;

        if(getConfig().getBoolean("mustBePlayerKill") && event.getEntity().getKiller() == null) return;

        String headItemName = ChatColor.WHITE + event.getEntityType().name() + " Head";

        if (vanilla_mob_heads.containsKey(event.getEntityType())) {
            ItemStack itemToDrop = new ItemStack(vanilla_mob_heads.get(event.getEntityType()));
            ItemMeta itemToDropMeta = itemToDrop.getItemMeta();
            itemToDropMeta.setDisplayName(headItemName);
            itemToDrop.setItemMeta(itemToDropMeta);
            event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), itemToDrop);
            xp =getXP(event.getEntityType().name());
        } else if (custom_mob_heads.containsKey(event.getEntityType())) {
            dropCustomSkull(event.getEntity().getWorld(), event.getEntity().getLocation(), custom_mob_heads.get(event.getEntityType()), headItemName, event.getEntity().getName(), event.getEntityType().name());
            xp = getXP(event.getEntityType().name());
            PersistentDataContainer container = event.getEntity().getKiller().getPersistentDataContainer();
            container.set(key_playerXP, PersistentDataType.INTEGER, container.get(key_playerXP, PersistentDataType.INTEGER) + xp);
        }
    }

    public int getXP(String mob) {
        return getConfig().getInt("xp4kill." + mob);
    }

    public NamespacedKey getKey() {
        return new NamespacedKey(this, "mobtype");
    }

    public static void dropCustomSkull(World world, Location location, String skinBase64, String itemName, String mobname, String type) {
        ItemStack item = SkullCreator.itemFromBase64(skinBase64);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(itemName);

        List<String> lore;
        if (meta.hasLore()) {
            lore = meta.getLore();
            lore.add("MobHead");
        } else {
            lore = new ArrayList<>();
            lore.add("MobHead");
        }
        meta.getPersistentDataContainer().set(key_mobType, PersistentDataType.STRING, type);
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
