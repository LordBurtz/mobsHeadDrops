package me.lordburtz.dropheads.commands;

import me.lordburtz.dropheads.DropHeads;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import static me.lordburtz.dropheads.DropHeads.key_playerXP;

public class ShowLevel implements CommandExecutor {
    private DropHeads plugin;

    public ShowLevel(DropHeads plugin) {
        this.plugin = plugin;
        plugin.getCommand("level").setExecutor(this);
    }

    //TODO: add fancy colors and better messages that sound more natural
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return true;
        Player player = (Player) commandSender;
        int level = player.getPersistentDataContainer().get(key_playerXP, PersistentDataType.INTEGER);
        player.sendMessage("Your current Level is: " + level);
        int player_tier = 0;

        for (int i = 1; i<6; i++) {
            int tier = plugin.getConfig().getInt("Tiers.Tier"+ i);
            if (level > tier) {
                player_tier = i;
            }
        }

        player.sendMessage("Your current Tier is: " + player_tier);
        return true;
    }
}
