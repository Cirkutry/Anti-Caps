package com;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import filter.MessagePipeline;
import filter.PipelineApplier;

public class AntiCaps extends JavaPlugin {
    private static final String MESSAGES_FILE = "messages.yml";

    private FileConfiguration messagesConfig;

    private MessagePipeline pipeline;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadMessages();

        this.pipeline = new MessagePipeline(this);

        getServer().getPluginManager().registerEvents(new PipelineApplier(this, pipeline), this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("anticaps"))
            return false;

        if (args.length == 0 || !args[0].equalsIgnoreCase("reload"))
            return false;

        if (!sender.hasPermission("anticaps.reload")) {
            sendMessage("no-permission", sender);
            return true;
        }

        reloadConfig();
        loadMessages();
        sendMessage("reload-success", sender);
        return true;
    }

    public void sendMessage(String messageKey, Player player) {
        sendMessage(messageKey, (CommandSender) player);
    }

    public void sendMessage(String messageKey, CommandSender sender) {
        String prefix = getMessage("prefix");
        String message = getMessage("messages." + messageKey);
        if (message == null)
            message = messageKey;

        sender.sendMessage(colorize(prefix + message));
    }

    private void loadMessages() {
        File messagesFile = new File(getDataFolder(), MESSAGES_FILE);
        if (!messagesFile.exists())
            saveResource(MESSAGES_FILE, false);
        this.messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    private String getMessage(String path) {
        if (messagesConfig == null)
            return null;
        return messagesConfig.getString(path);
    }

    private String colorize(String message) {
        if (message == null)
            return "";
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
