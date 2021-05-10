package me.lordburtz.dropheads.listeners;

import me.lordburtz.dropheads.DropHeads;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import static me.lordburtz.dropheads.DropHeads.key_mobType;

public class HeadRightClick implements Listener {
    private DropHeads plugin;
    private Economy econ;

    public HeadRightClick(DropHeads plugin, Economy economy) {
        this.plugin = plugin;
        this.econ = economy;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onAirRightlick(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK ||
            event.getAction() == Action.PHYSICAL) return;
        if (!event.hasItem()) return;
        if (event.getItem().getType() != Material.PLAYER_HEAD) return;
        if (!event.getItem().hasItemMeta()) return;
        if (event.getItem().getItemMeta().getPersistentDataContainer().isEmpty()) return;

        Player player = event.getPlayer();
        ItemStack stack = event.getItem();

        String mobname = stack.getItemMeta().getPersistentDataContainer().get(key_mobType, PersistentDataType.STRING);
        double payment = plugin.getConfig().getDouble("payment4Head." + mobname);

        double final_amount = payment * stack.getAmount();
        player.getInventory().remove(stack);
        econ.depositPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), final_amount);
        player.sendMessage(ChatColor.GOLD + String.format("%.2f have been added to your balance", final_amount));
    }
}
