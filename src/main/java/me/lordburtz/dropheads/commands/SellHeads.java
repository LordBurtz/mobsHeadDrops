package me.lordburtz.dropheads.commands;

import me.lordburtz.dropheads.DropHeads;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Warning;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SellHeads implements CommandExecutor {
    private DropHeads plugin;
    private Economy econ;

    public SellHeads(DropHeads plugin, Economy economy) {
        this.plugin = plugin;
        this.econ = economy;
        plugin.getCommand("sell-heads").setExecutor(this);
    }

    public static void log(String msg) {DropHeads.log(msg);}

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return true;
        log("cmd executed");
        Player player = ((Player) commandSender).getPlayer();
        ItemStack stack = player.getInventory().getItemInMainHand();
        if (!stack.getItemMeta().hasLore()) return true;
        log("has lore");
        //somehow this isnt working I dont know why
        //it should.. I tried with and without the invisString method??
        //TODO: make this work; removed till working
        //if (!stack.getItemMeta().getLore().contains(DropHeads.invisString("DropHeads"))) return true;
        commandSender.sendMessage("working");
        commandSender.sendMessage(EntityType.BAT.name());
        //have to add config support but i really gtg now
        econ.depositPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), 10);
        return true;
    }
}

