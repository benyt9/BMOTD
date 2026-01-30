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

        if (getConfig().getBoolean("always-middle", false)) {
            line1 = centerText(line1);
            line2 = centerText(line2);
        }

        event.motd(mm.deserialize(line1 + "\n" + line2));
    }

    // Die Magie: Text zentrieren
    private String centerText(String text) {
        // Wir strippen die MiniMessage Tags weg, um die echte Textlänge zu messen
        String plainText = PlainTextComponentSerializer.plainText().serialize(mm.deserialize(text));

        // Ein Standard-MOTD hat ca. 127 Pixel Platz pro Zeile (grob geschätzt für Leerzeichen)
        // Minecraft nutzt ca. 6 Pixel pro Buchstabe, ein Leerzeichen hat ca. 4 Pixel.
        int messagePx = 0;
        for (char c : plainText.toCharArray()) {
            // Sehr vereinfachte Pixel-Rechnung
            messagePx += (c == 'i' || c == 'l' || c == '!' || c == '.') ? 2 : 6;
        }

        int halfDefault = 127; // Mitte der MOTD
        int padding = (halfDefault - (messagePx / 2)) / 4; // Wie viele Leerzeichen brauchen wir?

        return " ".repeat(Math.max(0, padding)) + text;
    }

    public FileConfiguration getLangConfig() { return langConfig; }
    public MiniMessage getMiniMessage() { return mm; }
}