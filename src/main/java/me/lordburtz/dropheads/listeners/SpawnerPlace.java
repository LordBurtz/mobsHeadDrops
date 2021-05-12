package me.lordburtz.dropheads.listeners;

import com.google.common.collect.Lists;
import me.lordburtz.dropheads.DropHeads;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Set;

import static me.lordburtz.dropheads.DropHeads.*;
import static me.lordburtz.dropheads.listeners.SpawnerBreak.key_spawnerType;

public class SpawnerPlace implements Listener {
    private final DropHeads plugin;

    public SpawnerPlace(DropHeads plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerFirstJoin(PlayerJoinEvent event) {
        if (event.getPlayer().getPersistentDataContainer().has(key_playerXP, PersistentDataType.INTEGER)) return;
        event.getPlayer().getPersistentDataContainer().set(key_playerXP, PersistentDataType.INTEGER, 0);
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onSpawnerPlace(BlockPlaceEvent event) {
        if (!event.getBlockPlaced().getType().equals(Material.SPAWNER)) return;
        CreatureSpawner spawner = (CreatureSpawner) event.getBlockPlaced().getState();
        String type = event.getItemInHand().getItemMeta().getPersistentDataContainer().get(key_spawnerType, PersistentDataType.STRING);
        int level = event.getPlayer().getPersistentDataContainer().get(key_playerXP, PersistentDataType.INTEGER);
        System.out.println(event.getItemInHand().getItemMeta().getPersistentDataContainer().getKeys());
        int mob_tier = plugin.getConfig().getInt("SpawnerTiers." + type);
        int player_tier = 0;


        for (int i = 1; i<6; i++) {
            int tier = plugin.getConfig().getInt("Tiers.Tier"+ i);
            if (level > tier) {
                player_tier = i;
            }
        }

        if (player_tier >= mob_tier) {
            return;
        } else {
            String msg = plugin.getConfig().getString("insufficient_level");
            if (msg == null) {
                msg = "your MobLevel is insufficient";
                log(ChatColor.RED + "insufficient_level msg not set in the config.yml", true);
            }
            event.getPlayer().sendMessage(ChatColor.RED + msg);
            event.setCancelled(true);
        }
    }
}
