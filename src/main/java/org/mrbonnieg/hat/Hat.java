package org.mrbonnieg.hat;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class Hat extends JavaPlugin implements CommandExecutor {
    private String reloadMsg;
    private String noPermissions;
    private String notPlayer;
    private String noItem;
    private String hatSuccessful;
    private String wrongMaterial;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadMessages();
        getCommand("hat").setExecutor(this);
        getLogger().log(Level.INFO, "Plugin enabled");
    }

    @Override
    public void onDisable() { getLogger().log(Level.INFO, "Plugin disabled"); }

    public static String color(String message) {
        if (message == null) return null;
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private void loadMessages() {
        reloadMsg = color(getConfig().getString("messages.config-reload", "&aConfig reloaded!"));
        noPermissions = color(getConfig().getString("messages.no-permissions", "&cYou don't have permission!"));
        notPlayer = color(getConfig().getString("messages.not-player", "&cOnly players can use this command!"));
        noItem = color(getConfig().getString("messages.no-item", "&cYou must hold an item in your hand!"));
        wrongMaterial = color(getConfig().getString("messages.wrong-material", "&cYou can't use this material!"));
        hatSuccessful = color(getConfig().getString("messages.hat-successful", "&aYou placed the item on your head!"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if(!sender.hasPermission("hat.reload")) { sender.sendMessage(noPermissions); return true; }
            reloadConfig();
            loadMessages();
            sender.sendMessage(reloadMsg);
            return true;
        }

        if(!(sender instanceof Player player)) {
            sender.sendMessage(notPlayer); return true;
        } else if(!sender.hasPermission("hat.use")) {
            sender.sendMessage(noPermissions); return true;
        }

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if(itemInHand.getType() == Material.AIR) {
            player.sendMessage(noItem); return true;
        } else if (getConfig().getBoolean("materialsWhitelistEnabled") && !getConfig().getStringList("materialsWhitelist").contains(itemInHand.getType().toString())) {
            player.sendMessage(wrongMaterial); return true;
        }

        ItemStack helmet = player.getInventory().getHelmet();
        player.getInventory().setHelmet(itemInHand);
        player.getInventory().setItemInMainHand(helmet);
        player.sendMessage(hatSuccessful);
        return true;
    }
}
