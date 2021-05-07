package me.lordburtz.dropheads.commands;

import me.lordburtz.dropheads.DropHeads;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

import static me.lordburtz.dropheads.DropHeads.key;

public class SellHeads implements CommandExecutor, TabCompleter {
    private DropHeads plugin;
    private Economy econ;

    public static final List<String> COMMANDS = new ArrayList<>();
    public SellHeads(DropHeads plugin, Economy economy) {
        createCommands();

        this.plugin = plugin;
        this.econ = economy;
        plugin.getCommand("sell-heads").setExecutor(this);
        plugin.getCommand("sell-heads").setTabCompleter(this);
    }

    public static void log(String msg) {DropHeads.log(msg);}

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED+ "console is not supported");
            return true;
        }
        Player player = ((Player) commandSender).getPlayer();
        ItemStack stack = player.getInventory().getItemInMainHand();

        if (stack.getType() == null) {
            commandSender.sendMessage("this is not a custom mob head");
            return true;
        }

        if (!stack.getType().equals(Material.PLAYER_HEAD)) {
            commandSender.sendMessage("this is not a custom mob head");
            return true;
        }
        if (!stack.getItemMeta().hasLore()) {
            commandSender.sendMessage("this is not a custom mob head");
            return true;
        }

        String mobname = stack.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
        double payment = plugin.getConfig().getDouble("payment4Head." + mobname);

        if (mobname == null || payment == 0) {
            commandSender.sendMessage("unsupported mobytpe! report this");
            log(ChatColor.RED + String.format("unsupported mobtype used by %s \n type is: %s", commandSender.getName(), mobname));
            return true;
        }

        if (args.length == 0) {
            double final_amount = payment * stack.getAmount();
            player.getInventory().remove(stack);
            econ.depositPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), final_amount);
            player.sendMessage(ChatColor.GOLD + String.format("%.2f have been added to your balance", final_amount));
            return true;
        } else {
            switch (args[0]) {
                case "help":
                    //TODO: add help msg via config
                    player.sendMessage("custom help message");
                    break;
                case "all":
                    double final_amount = payment * stack.getAmount();
                    player.getInventory().remove(stack);
                    econ.depositPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), final_amount);
                    player.sendMessage(ChatColor.GOLD + String.format("%.2f have been added to your balance", final_amount));
                    break;
                case "single":
                    econ.depositPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), payment);
                    stack.setAmount(stack.getAmount() -1);
                    player.sendMessage(ChatColor.GOLD + String.format("%.2f have been added to your balance", payment));
                    break;
            }
            return true;
        }
    }

    public final void createCommands() {
        COMMANDS.add("help");
        COMMANDS.add("single");
        COMMANDS.add("all");
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> completions = new ArrayList<>();

        if (strings.length == 1) {
            for (String a : COMMANDS) {
                if (a.toLowerCase().startsWith(strings[0].toLowerCase())) completions.add(a);
            }
            return completions;
        }
        return COMMANDS;
    }
}

