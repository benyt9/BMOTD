package b.bplugins.bmotd;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends JavaPlugin implements Listener {

    private FileConfiguration langConfig;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final Random random = new Random();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadLanguage();
        getServer().getPluginManager().registerEvents(this, this);

        if (getCommand("bmotd") != null) {
            getCommand("bmotd").setExecutor(new ReloadCommand(this));
        }

        getLogger().info("BMOTD wurde erfolgreich geladen!");
    }

    public void loadLanguage() {
        reloadConfig();
        String langMode = getConfig().getString("language", "de");
        File langFile = new File(getDataFolder(), "lang/lang-" + langMode + ".yml");
        if (!langFile.exists()) saveResource("lang/lang-de.yml", false);
        langConfig = YamlConfiguration.loadConfiguration(langFile);
    }

    @EventHandler
    public void onServerPing(ServerListPingEvent event) {
        // Wir holen uns die "Sektion" motds
        ConfigurationSection motdSection = getConfig().getConfigurationSection("motds");

        String line1 = "<red>BMOTD Fehler</red>";
        String line2 = "<gray>Prüfe deine config.yml!";

        if (motdSection != null) {
            // Wir holen uns alle Schlüssel (0, 1, 2...)
            List<String> keys = new ArrayList<>(motdSection.getKeys(false));
            if (!keys.isEmpty()) {
                String randomKey = keys.get(random.nextInt(keys.size()));
                line1 = motdSection.getString(randomKey + ".line1", "Line 1 fehlt");
                line2 = motdSection.getString(randomKey + ".line2", "Line 2 fehlt");
            }
        }

        // PlaceholderAPI Check
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            line1 = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(null, line1);
            line2 = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(null, line2);
        }

        // Zentrierung
        if (getConfig().getBoolean("always-middle", false)) {
            line1 = centerText(line1);
            line2 = centerText(line2);
        }

        event.motd(mm.deserialize(line1 + "\n" + line2));
    }

    private String centerText(String text) {
        if (text == null || text.isEmpty()) return "";
        String plainText = PlainTextComponentSerializer.plainText().serialize(mm.deserialize(text));
        double messagePx = 0;
        for (char c : plainText.toCharArray()) {
            messagePx += getCharWidth(c);
        }
        double centerPoint = 145;
        double halfMessage = messagePx / 2;
        double paddingPx = centerPoint - halfMessage;
        int spaceCount = (int) (paddingPx / 3.8);
        return " ".repeat(Math.max(0, spaceCount)) + text;
    }

    private double getCharWidth(char c) {
        if (c == 'i' || c == '!' || c == '|' || c == ':') return 2;
        if (c == 'l' || c == '.' || c == ',') return 3;
        if (c == 't' || c == 'I' || c == '[' || c == ']') return 4;
        if (c == 'f' || c == 'k' || c == '(' || c == ')' || c == ' ') return 5;
        if (c == '<' || c == '>') return 4;
        return 6;
    }

    public FileConfiguration getLangConfig() { return langConfig; }
    public MiniMessage getMiniMessage() { return mm; }
}