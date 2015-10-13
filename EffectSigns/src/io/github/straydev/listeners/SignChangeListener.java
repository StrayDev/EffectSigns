package io.github.straydev.listeners;

import io.github.straydev.EffectSigns;
import io.github.straydev.enums.InvalidObject;
import io.github.straydev.enums.PermissionType;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

/**
 * Created by Stray on 9/26/2015.
 */
public class SignChangeListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignChange(SignChangeEvent event) {
        if (!event.isCancelled()) {
            if (event.getLine(0).equalsIgnoreCase(EffectSigns.playerLine0)) {
                if (event.getPlayer().hasPermission(EffectSigns.placePermission)) {
                    if (!event.getLine(1).isEmpty() && !event.getLine(2).isEmpty() && !event.getLine(3).isEmpty()) {
                        if (EffectSigns.isPotionEffect(event.getLine(1))) {
                            if (EffectSigns.isInt(event.getLine(2))) {
                                if (EffectSigns.isInt(event.getLine(3))) {
                                    if(Integer.parseInt(event.getLine(3)) <= 255 && Integer.parseInt(event.getLine(3)) >= 0) {
                                        EffectSigns.createSign(event);
                                        event.getPlayer().sendMessage(EffectSigns.chatPrefix + ChatColor.WHITE + "Successfully created EffectSign.");
                                    }
                                    else {
                                        EffectSigns.invalid(event.getPlayer(), InvalidObject.AMPLIFIER);
                                    }
                                }
                            }
                            else {
                                EffectSigns.invalid(event.getPlayer(), InvalidObject.DURATION);
                            }
                        }
                        else {
                            EffectSigns.invalid(event.getPlayer(), InvalidObject.EFFECT);
                        }
                    }
                } else {
                    event.setCancelled(true);
                    EffectSigns.noPermissions(event.getPlayer(), PermissionType.CREATE_SIGN);
                }
            }
        }
    }

}
