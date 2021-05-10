package me.lordburtz.dropheads.commands;

import me.lordburtz.dropheads.DropHeads;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ShowLevel implements CommandExecutor {
    private DropHeads plugin;

    public ShowLevel(DropHeads plugin) {
        this.plugin = plugin;
        plugin.getCommand("getLevel").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return true;
    }
}
