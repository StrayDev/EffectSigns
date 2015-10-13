package io.github.straydev.listeners;

import io.github.straydev.EffectSigns;
import io.github.straydev.enums.PermissionType;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by Stray on 9/26/2015.
 */
public class SignInteractListener implements Listener {

    @EventHandler
    public void onSignInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;
        Player p = event.getPlayer();
        Material m = event.getClickedBlock().getType();
        if (m == Material.SIGN || m == Material.SIGN_POST || m == Material.WALL_SIGN) {
            Sign s = (Sign) event.getClickedBlock().getState();
            if (EffectSigns.isValidEffectSign(s)) {
                if(EffectSigns.cooldownEnabled) {
                    if(EffectSigns.hasCooldown(event.getPlayer())) {
                        if(!event.getPlayer().hasPermission(EffectSigns.bypassCooldownPermission)) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
                if (p.hasPermission(EffectSigns.usePermission)) {
                    EffectSigns.applyEffect(s, p);
                    if(EffectSigns.cooldownEnabled) {
                        if(!EffectSigns.hasCooldown(event.getPlayer())) {
                            EffectSigns.addCooldown(p);
                        }
                    }
                }
                else {
                    EffectSigns.noPermissions(p, PermissionType.USE_SIGN);
                }
            }
        }
    }
}
