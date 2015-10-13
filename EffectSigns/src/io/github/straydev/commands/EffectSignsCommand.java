package io.github.straydev.commands;

import io.github.straydev.EffectSigns;
import io.github.straydev.enums.PermissionType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Stray on 9/26/2015.
 */
public class EffectSignsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmnd, String[] args) {
        if(cmd.getName().equalsIgnoreCase("effectsigns")) {
            if(args.length == 1) {
                if(args[0].equalsIgnoreCase("reload")) {
                    if(sender.isOp()) {
                        EffectSigns.plugin.reloadConfig();
                        EffectSigns.loadConfig();
                        sender.sendMessage(EffectSigns.chatPrefix + ChatColor.GRAY + "Config reloaded.");
                    }
                    else {
                        if(sender instanceof Player) {
                            EffectSigns.noPermissions(((Player) sender).getPlayer(), PermissionType.COMMAND);
                        }
                    }
                }
                else {
                    sender.sendMessage(EffectSigns.chatPrefix + ChatColor.GRAY + "Running EffectSigns v" + EffectSigns.plugin.getDescription().getVersion() + ".");
                    sender.sendMessage(EffectSigns.chatPrefix + ChatColor.GRAY + "Plugin developed by Stray.");
                }
            }
            else {
                sender.sendMessage(EffectSigns.chatPrefix + ChatColor.GRAY + "Running EffectSigns v" + EffectSigns.plugin.getDescription().getVersion() + ".");
                sender.sendMessage(EffectSigns.chatPrefix + ChatColor.GRAY + "Plugin developed by Stray.");
            }
        }
        return false;
    }

}
