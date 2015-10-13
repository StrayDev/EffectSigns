package io.github.straydev.listeners;

import io.github.straydev.EffectSigns;
import io.github.straydev.enums.PermissionType;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * Created by Stray on 10/12/2015.
 */
public class BlockBreakListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if(!event.isCancelled()) {
            Material m = event.getBlock().getType();
            if(m == Material.SIGN || m == Material.SIGN_POST || m == Material.WALL_SIGN) {
                Sign s = (Sign) event.getBlock().getState();
                if(EffectSigns.isValidEffectSign(s)) {
                    if(!event.getPlayer().hasPermission(EffectSigns.breakPermission)) {
                        event.setCancelled(true);
                        EffectSigns.noPermissions(event.getPlayer(), PermissionType.BREAK_SIGN);
                    }
                }
            }
        }
    }

}
