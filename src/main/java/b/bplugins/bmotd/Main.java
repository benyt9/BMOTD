package b.bplugins.bmotd;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin implements Listener {

    private FileConfiguration langConfig;
    private final MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadLanguage();

        getServer().getPluginManager().registerEvents(this, this);
        getCommand("bmotd").setExecutor(new ReloadCommand(this));

        getLogger().info("bMOTD aktiv!");
    }

    public void loadLanguage() {
        reloadConfig(); // Lädt die config.yml neu (für MOTD & Lang-Wahl)

        String langMode = getConfig().getString("language", "de");
        File langFile = new File(getDataFolder(), "lang/lang-" + langMode + ".yml");

        if (!langFile.exists()) {
            // Speichert die Datei aus den Resources, falls sie auf dem Server fehlt
            saveResource("lang/lang-de.yml", false);
        }
        langConfig = YamlConfiguration.loadConfiguration(langFile);
    }

    @EventHandler
    public void onServerPing(ServerListPingEvent event) {
        // Zieht die MOTD direkt aus der config.yml (nicht aus der lang!)
        String line1 = getConfig().getString("motd.line1", "<red>Line 1 fehlt");
        String line2 = getConfig().getString("motd.line2", "<red>Line 2 fehlt");

        event.motd(mm.deserialize(line1 + "\n" + line2));
    }

    public FileConfiguration getLangConfig() {
        return langConfig;
    }

    public MiniMessage getMiniMessage() {
        return mm;
    }
}