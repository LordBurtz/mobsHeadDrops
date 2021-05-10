package me.lordburtz.dropheads.commands;

import me.lordburtz.dropheads.DropHeads;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import static me.lordburtz.dropheads.DropHeads.key_playerXP;

public class ResetXP implements CommandExecutor {
    private DropHeads plugin;

    public ResetXP(DropHeads plugin) {
        this.plugin = plugin;
        plugin.getCommand("reset-xp").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            ((Player) commandSender ).getPersistentDataContainer().set(key_playerXP, PersistentDataType.INTEGER, 0);
            commandSender.sendMessage("xp resetted");
        }
        return true;
    }
}
