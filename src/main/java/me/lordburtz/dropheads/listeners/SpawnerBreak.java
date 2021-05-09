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

public class SpawnerBreak implements Listener, CommandExecutor {
    private DropHeads plugin;

    public static NamespacedKey key_spawnerType = null;

    public SpawnerBreak(DropHeads plugin) {
        this.plugin = plugin;
        plugin.getCommand("test").setExecutor(this);
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
        String type = event.getItemInHand().getItemMeta().getPersistentDataContainer().get(key_spawnerType, PersistentDataType.STRING);
        Block block = event.getBlockPlaced();
        ((CreatureSpawner) block).setSpawnedType(EntityType.valueOf(type));
    }


    @EventHandler (priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.getBlock().getType().equals(Material.SPAWNER)) return;
        if (!plugin.getConfig().getBoolean("dropSpawners")) return;
        System.out.println("spawner recognized");
        Player player = event.getPlayer();
        if (plugin.getConfig().getBoolean("needSilkTouch")) {
            if (player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
                Location loc = event.getBlock().getLocation();
                ItemStack block = new ItemStack(Material.SPAWNER);
                String mobtype = ((CreatureSpawner) block.getItemMeta()).getSpawnedType().name();
                block.getItemMeta().getPersistentDataContainer().set(key_spawnerType, PersistentDataType.STRING, mobtype);
                loc.getWorld().dropItem(loc, block);
            } else {
                System.out.println("silk return");
                return;
            }
        } else {
            Location loc = event.getBlock().getLocation();
            ItemStack block = new ItemStack(Material.SPAWNER);
            String mobtype = ((CreatureSpawner) block.getItemMeta()).getSpawnedType().name();
            block.getItemMeta().getPersistentDataContainer().set(key_spawnerType, PersistentDataType.STRING, mobtype);
            loc.getWorld().dropItem(loc, block);
            System.out.println("dropped");
        }
    }
}
