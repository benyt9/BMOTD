package b.bplugins.bmotd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    private final Main plugin;

    public ReloadCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String prefix = plugin.getLangConfig().getString("messages.prefix", "");

        if (!sender.hasPermission("bmotd.admin")) {
            String noPerm = plugin.getLangConfig().getString("messages.no_permission", "Keine Rechte.");
            sender.sendMessage(plugin.getMiniMessage().deserialize(prefix + noPerm));
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            plugin.loadLanguage();
            String reloadMsg = plugin.getLangConfig().getString("messages.reload", "Reloaded!");
            sender.sendMessage(plugin.getMiniMessage().deserialize(prefix + reloadMsg));
            return true;
        }

        return false;
    }
}