package b.bplugins.bmotd;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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
        String line1 = getConfig().getString("motd.line1", "");
        String line2 = getConfig().getString("motd.line2", "");

        // 1. PlaceholderAPI Unterstützung hinzufügen
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            line1 = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(null, line1);
            line2 = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(null, line2);
        }

        if (getConfig().getBoolean("always-middle", false)) {
            line1 = centerText(line1);
            line2 = centerText(line2);
        }

        event.motd(mm.deserialize(line1 + "\n" + line2));
    }

    // Die Magie: Text zentrieren
    private String centerText(String text) {
        if (text == null || text.isEmpty()) return "";

        // Tags entfernen für die Messung
        String plainText = PlainTextComponentSerializer.plainText().serialize(mm.deserialize(text));

        double messagePx = 0;
        for (char c : plainText.toCharArray()) {
            messagePx += getCharWidth(c);
        }

        // Ein MOTD hat eine Breite von ca. 280-300 Pixeln in der Anzeige.
        // Die Mitte liegt bei etwa 140-150 Pixeln.
        double centerPoint = 150;
        double halfMessage = messagePx / 2;
        double paddingPx = centerPoint - halfMessage;

        // Ein Leerzeichen ist ca. 4 Pixel breit
        int spaceCount = (int) (paddingPx / 4);

        return " ".repeat(Math.max(0, spaceCount)) + text;
    }

    private double getCharWidth(char c) {
        // Pixel-Breiten für Minecraft Default Font
        if (c == 'i' || c == '!' || c == '|' || c == ':') return 2;
        if (c == 'l' || c == '.' || c == ',') return 3;
        if (c == 't' || c == 'I' || c == '[' || c == ']') return 4;
        if (c == 'f' || c == 'k' || c == '(' || c == ')' || c == ' ') return 5;
        if (c == '<' || c == '>') return 4;

        // Die meisten anderen Buchstaben (A-Z, 0-9) sind 6 Pixel breit
        return 6;
    }
    public FileConfiguration getLangConfig() {
        return langConfig;
    }

    public MiniMessage getMiniMessage() {
        return mm;
    }
}