package io.github.straydev;

import io.github.straydev.commands.EffectSignsCommand;
import io.github.straydev.enums.InvalidObject;
import io.github.straydev.enums.PermissionType;
import io.github.straydev.listeners.BlockBreakListener;
import io.github.straydev.listeners.SignChangeListener;
import io.github.straydev.listeners.SignInteractListener;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

/**
 * Created by Stray on 9/26/2015.
 */
public class EffectSigns extends JavaPlugin {

    public static String playerLine0 = "[Effect]";
    public static String pluginLine0 = translateColors("&f[&eEffect&f]");
    public static String chatPrefix = translateColors("&f[&eEffectSigns&f] ");
    public static String noCommandPerms = "&cYou do not have permission to perform this command.";
    public static String noCreatePerms = "&cYou do not have permission to create effect signs.";
    public static String noUsePerms = "&cYou do not have permission to use this.";
    public static String noBreakPerms = "&cYou do not have permission to break effect signs.";
    public static String breakPermission = "effectsigns.destroy";
    public static String placePermission = "effectsigns.create";
    public static String usePermission = "effectsigns.use";
    public static String bypassCooldownPermission = "effectsigns.cooldown.bypass";
    public static HashMap<Player, Long> cooldowns = new HashMap<Player, Long>();
    public static int cooldownTime = 3;
    public static boolean cooldownEnabled = true;

    public static Plugin plugin;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new SignChangeListener(), this);
        getServer().getPluginManager().registerEvents(new SignInteractListener(), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
        getCommand("effectsigns").setExecutor(new EffectSignsCommand());
        plugin = this;
        loadConfig();
    }

    @Override
    public void onDisable() {
        plugin = null;
    }

    public static void loadConfig() {
        plugin.saveDefaultConfig();
        pluginLine0 = translateColors(plugin.getConfig().getString("config.signPrefix", pluginLine0));
        chatPrefix = translateColors(plugin.getConfig().getString("config.chatPrefix", chatPrefix) + " ");
        noCommandPerms = translateColors(plugin.getConfig().getString("config.noCommandPermission", noCommandPerms));
        noUsePerms = translateColors(plugin.getConfig().getString("config.noUsePermission", noUsePerms));
        noCreatePerms = translateColors(plugin.getConfig().getString("config.noCreatePermission", noCreatePerms));
        noBreakPerms = translateColors(plugin.getConfig().getString("config.noBreakPermission", noBreakPerms));
        try {
            cooldownTime = plugin.getConfig().getInt("config.signCooldown");
            if(cooldownTime <= 0) {
                cooldownEnabled = false;
            }
        } catch (Exception e) {
            plugin.getLogger().info("Failed to parse cooldown time, defaulting to 3 seconds.");
            cooldownTime = 3;
        }
    }

    public static String translateColors(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public static void createSign(SignChangeEvent e) {
        PotionEffect p = getPotionEffect(e);
        e.setLine(0, pluginLine0);
        e.setLine(1, p.getType().getName());
        e.setLine(2, String.valueOf(p.getDuration()));
        e.setLine(3, String.valueOf(p.getAmplifier()));
    }

    public static boolean isValidEffectSign(Sign s) {
        if(s.getLine(0).equalsIgnoreCase(pluginLine0)) {
            if(isPotionEffect(s.getLine(1))) {
                if(isInt(s.getLine(2))) {
                    if(isInt(s.getLine(3))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void applyEffect(Sign s, Player p) {
        PotionEffectType pet = getPotionType(s.getLine(1));
        int duration;
        int power;
        try {
            duration = Integer.parseInt(s.getLine(2));
            power = Integer.parseInt(s.getLine(3));
        }
        catch (NumberFormatException nfe) {
            plugin.getServer().getLogger().info("Failed to parse Integers (applyEffect)");
            return;
        }
        PotionEffect pe = new PotionEffect(pet, duration * 20, power);
        p.addPotionEffect(pe);
    }

    public static PotionEffect getPotionEffect(SignChangeEvent e) {
        PotionEffectType p = getPotionType(e.getLine(1));
        int duration;
        int power;
        try {
            duration = Integer.parseInt(e.getLine(2));
            power = Integer.parseInt(e.getLine(3));
        }
        catch(NumberFormatException n) {
            plugin.getServer().getLogger().info("Error parsing Integers (getPotionEffect 89/90");
            return null;
        }
        PotionEffect pe = new PotionEffect(p, duration, power);
        return pe;
    }

    public static void addCooldown(Player p) {
        long now = System.currentTimeMillis();
        long cooldown = cooldownTime * 1000;
        long end = cooldown + now;
        cooldowns.put(p, end);
    }

    public static boolean hasCooldown(Player p) {
        if(cooldowns.get(p) == null) return false;
        if(cooldowns.get(p) <= System.currentTimeMillis()) {
            cooldowns.remove(p);
            return false;
        }
        return true;
    }

    public static PotionEffectType getPotionType(String s) {
        PotionEffectType e = null;
        if(s.equalsIgnoreCase("absorption")) {
            e = PotionEffectType.ABSORPTION;
        }
        else if(s.equalsIgnoreCase("blindness")) {
            e = PotionEffectType.BLINDNESS;
        }
        else if(s.equalsIgnoreCase("nausea") || s.equalsIgnoreCase("confusion")) {
            e = PotionEffectType.CONFUSION;
        }
        else if(s.equalsIgnoreCase("resistance") || s.equalsIgnoreCase("damage_resistance")) {
            e = PotionEffectType.DAMAGE_RESISTANCE;
        }
        else if(s.equalsIgnoreCase("haste") || s.equalsIgnoreCase("fast_digging")) {
            e = PotionEffectType.FAST_DIGGING;
        }
        else if(s.equalsIgnoreCase("fire_resistance")) {
            e = PotionEffectType.FIRE_RESISTANCE;
        }
        else if(s.equalsIgnoreCase("harming") || s.equalsIgnoreCase("harm")) {
            e = PotionEffectType.HARM;
        }
        else if(s.equalsIgnoreCase("healing") || s.equalsIgnoreCase("heal")) {
            e = PotionEffectType.HEAL;
        }
        else if(s.equalsIgnoreCase("health_boost")) {
            e = PotionEffectType.HEALTH_BOOST;
        }
        else if(s.equalsIgnoreCase("hunger")) {
            e = PotionEffectType.HUNGER;
        }
        else if(s.equalsIgnoreCase("strength") || s.equalsIgnoreCase("increase_damage")) {
            e = PotionEffectType.INCREASE_DAMAGE;
        }
        else if(s.equalsIgnoreCase("invisibility")) {
            e = PotionEffectType.INVISIBILITY;
        }
        else if(s.equalsIgnoreCase("jump_boost") || s.equalsIgnoreCase("jump")) {
            e = PotionEffectType.JUMP;
        }
        else if(s.equalsIgnoreCase("night_vision")) {
            e = PotionEffectType.NIGHT_VISION;
        }
        else if(s.equalsIgnoreCase("poison")) {
            e = PotionEffectType.POISON;
        }
        else if(s.equalsIgnoreCase("regeneration")) {
            e = PotionEffectType.REGENERATION;
        }
        else if(s.equalsIgnoreCase("saturation")) {
            e = PotionEffectType.SATURATION;
        }
        else if(s.equalsIgnoreCase("slowness") || s.equalsIgnoreCase("slow")) {
            e = PotionEffectType.SLOW;
        }
        else if(s.equalsIgnoreCase("mining_fatigue") || s.equalsIgnoreCase("slow_digging")) {
            e = PotionEffectType.SLOW_DIGGING;
        }
        else if(s.equalsIgnoreCase("speed")) {
            e = PotionEffectType.SPEED;
        }
        else if(s.equalsIgnoreCase("water_breathing")) {
            e = PotionEffectType.WATER_BREATHING;
        }
        else if(s.equalsIgnoreCase("weakness")) {
            e = PotionEffectType.WEAKNESS;
        }
        else if(s.equalsIgnoreCase("wither")) {
            e = PotionEffectType.WITHER;
        }
        return e;
    }

    public static void noPermissions(Player p, PermissionType t) {
        if(t == PermissionType.COMMAND) {
            p.sendMessage(translateColors(noCommandPerms));
        }
        else if(t == PermissionType.USE_SIGN) {
            p.sendMessage(translateColors(noUsePerms));
        }
        else if(t == PermissionType.CREATE_SIGN) {
            p.sendMessage(translateColors(noCreatePerms));
        }
        else if(t == PermissionType.BREAK_SIGN) {
            p.sendMessage(translateColors(noBreakPerms));
        }
    }

    public static void invalid(Player p, InvalidObject io) {
        if(io == InvalidObject.AMPLIFIER) {
            p.sendMessage(chatPrefix + ChatColor.WHITE + "Invalid amplifier.");
        }
        else if(io == InvalidObject.DURATION) {
            p.sendMessage(chatPrefix + ChatColor.WHITE + "Invalid duration.");
        }
        else if(io == InvalidObject.EFFECT) {
            p.sendMessage(chatPrefix + ChatColor.WHITE + "Invalid effect.");
        }
    }

    public static boolean isPotionEffect(String name) {
        PotionEffectType p = getPotionType(name);
        if(p != null) return true;
        return false;
    }
}
