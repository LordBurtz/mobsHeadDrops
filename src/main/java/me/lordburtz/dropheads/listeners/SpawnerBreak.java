package me.lordburtz.dropheads.listeners;

import me.lordburtz.dropheads.DropHeads;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

public class SpawnerBreak implements Listener, CommandExecutor {
    private DropHeads plugin;

    public static NamespacedKey key_spawnerType = null;

    public SpawnerBreak(DropHeads plugin) {
        this.plugin = plugin;
        plugin.getCommand("test").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        key_spawnerType = new NamespacedKey(plugin, "spawnerType");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;

        Bukkit.dispatchCommand(commandSender, "give Fingolf1n spawner 1 0 {BlockEntityTag:{EntityId:Bat}}");

        //player.getWorld().dropItem(player.getLocation(), block);
        return true;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.getBlockPlaced().getType().equals(Material.SPAWNER)) return;
        if (!event.getItemInHand().hasItemMeta()) return;
        String type = event.getItemInHand().getItemMeta().getPersistentDataContainer().get(key_spawnerType, PersistentDataType.STRING);
        Location loc = event.getBlock().getLocation();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Block block = loc.getBlock();
            CreatureSpawner spawner = (CreatureSpawner) block.getState();
            spawner.setSpawnedType(EntityType.COW);
            System.out.println("EnittyType: "  +EntityType.valueOf(type));
            System.out.println( "Spawned type1: " + spawner.getSpawnedType().toString());
            spawner.update();
            System.out.println( "Spawned type2: " + spawner.getSpawnedType().toString());
        }, 10);
    }


    @EventHandler (priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.getBlock().getType().equals(Material.SPAWNER)) return;
        if (!plugin.getConfig().getBoolean("dropSpawners")) return;
        Player player = event.getPlayer();
        if (plugin.getConfig().getBoolean("needSilkTouch")) {
            if (player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
                Location loc = event.getBlock().getLocation();
                ItemStack block = new ItemStack(Material.SPAWNER);
                String mobtype = ((CreatureSpawner) event.getBlock().getState()).getSpawnedType().name();
                ItemMeta meta = block.getItemMeta();
                meta.getPersistentDataContainer().set(key_spawnerType, PersistentDataType.STRING, mobtype);
                List<String> lore = new ArrayList<>();
                lore.add("MobSpawner");
                meta.setDisplayName(String.format("%s's Spawner", mobtype));
                meta.setLore(lore);
                block.setItemMeta(meta);
                loc.getWorld().dropItem(loc, block);
            } else {
                return;
            }
        } else {
            Location loc = event.getBlock().getLocation();
            ItemStack block = new ItemStack(Material.SPAWNER);
            String mobtype = ((CreatureSpawner) event.getBlock().getState()).getSpawnedType().name();
            ItemMeta meta = block.getItemMeta();
            meta.getPersistentDataContainer().set(key_spawnerType, PersistentDataType.STRING, mobtype);
            List<String> lore = new ArrayList<>();
            lore.add("MobSpawner");
            meta.setLore(lore);
            meta.setDisplayName(String.format("%s's Spawner", mobtype));
            block.setItemMeta(meta);
            loc.getWorld().dropItem(loc, block);
        }
    }
}
