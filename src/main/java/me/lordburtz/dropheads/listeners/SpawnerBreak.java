package me.lordburtz.dropheads.listeners;

import me.lordburtz.dropheads.DropHeads;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.nio.Buffer;

public class SpawnerBreak implements Listener, CommandExecutor {
    private DropHeads plugin;

    public SpawnerBreak(DropHeads plugin) {
        this.plugin = plugin;
        plugin.getCommand("test").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;

        Bukkit.dispatchCommand(commandSender, "give Fingolf1n spawner 1 0 {BlockEntityTag:{EntityId:Bat}}");

        //player.getWorld().dropItem(player.getLocation(), block);
        return true;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.getBlock().getType().equals(Material.SPAWNER)) return;
        if (!plugin.getConfig().getBoolean("dropSpawners")) return;
        Player player = event.getPlayer();
        if (plugin.getConfig().getBoolean("needSilkTouch")) {
            if (player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
                Location loc = event.getBlock().getLocation();
                ItemStack block = new ItemStack(Material.SPAWNER);
                CreatureSpawner spawner = (CreatureSpawner) block.getItemMeta();
                spawner.setSpawnedType(((CreatureSpawner) event.getBlock()).getSpawnedType());
                block.setItemMeta((ItemMeta) spawner);
                loc.getWorld().dropItem(loc, block);
            }
        }

    }
}
