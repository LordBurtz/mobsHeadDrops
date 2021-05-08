package me.lordburtz.dropheads.listeners;

import com.mysql.jdbc.Buffer;
import me.lordburtz.dropheads.DropHeads;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Set;

import static me.lordburtz.dropheads.DropHeads.key_playerXP;

public class SpawnerPlace implements Listener {
    private DropHeads plugin;

    public SpawnerPlace(DropHeads plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onSpawnerPlace(BlockPlaceEvent event) {
        if (!event.getBlockPlaced().getType().equals(Material.SPAWNER)) return;
        CreatureSpawner spawner = (CreatureSpawner) event.getBlockPlaced().getState();
        String type = spawner.getSpawnedType().name();
        int level = event.getPlayer().getPersistentDataContainer().get(key_playerXP, PersistentDataType.INTEGER);
        int mob_tier = plugin.getConfig().getInt("SpawnerTiers." + type);
        Set<String> sec = plugin.getConfig().getConfigurationSection("Tiers").getKeys(false);
        for ()

    }
}
