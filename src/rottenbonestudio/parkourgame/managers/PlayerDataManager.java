package rottenbonestudio.parkourgame.managers;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import rottenbonestudio.parkourgame.SpeedrunPlugin;

public class PlayerDataManager {

    @SuppressWarnings("unused")
	private final SpeedrunPlugin plugin;
    private final File file;
    private final FileConfiguration data;

    public PlayerDataManager(SpeedrunPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "player-data.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.data = YamlConfiguration.loadConfiguration(file);
    }

    public int getBestTime(UUID uuid, String parkourId) {
        return data.getInt(uuid.toString() + "." + parkourId, -1);
    }

    public void setBestTime(UUID uuid, String parkourId, int time) {
        data.set(uuid.toString() + "." + parkourId, time);
        save();
    }

    public void save() {
        try {
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getData() {
        return data;
    }
    
}
