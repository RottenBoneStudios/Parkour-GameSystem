package rottenbonestudio.parkourgame;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import rottenbonestudio.parkourgame.commands.SalirCommand;
import rottenbonestudio.parkourgame.listeners.CheckpointListener;
import rottenbonestudio.parkourgame.listeners.DeathListener;
import rottenbonestudio.parkourgame.listeners.JoinListener;
import rottenbonestudio.parkourgame.listeners.NPCListener;
import rottenbonestudio.parkourgame.listeners.PlayerInteractListener;
import rottenbonestudio.parkourgame.listeners.QuitListener;
import rottenbonestudio.parkourgame.managers.CheckpointManager;
import rottenbonestudio.parkourgame.managers.ParkourManager;
import rottenbonestudio.parkourgame.managers.PlayerDataManager;

public class SpeedrunPlugin extends JavaPlugin {

	private ParkourManager parkourManager;
	private PlayerDataManager playerDataManager;
	private CheckpointManager checkpointManager;

	public String name = "§x§F§F§F§8§0§0§lᴘ§x§F§7§D§C§0§0§lᴀ§x§E§F§C§1§0§0§lʀ§x§E§6§A§5§0§0§lᴋ§x§D§E§8§A§0§0§lᴏ§x§D§6§6§E§0§0§lᴜ§x§C§E§5§3§0§0§lʀ §x§B§D§1§C§0§0§l>§x§B§5§0§0§0§0§l> §r";

	@Override
	public void onEnable() {
		saveDefaultConfig();

		this.parkourManager = new ParkourManager(this);
		this.playerDataManager = new PlayerDataManager(this);
		this.checkpointManager = new CheckpointManager();
		
		getCommand("salir").setExecutor(new SalirCommand(this));
		
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
	        new ParkourTopExpansion(this).register();
	        getLogger().info("PlaceholderAPI expansion 'parkour_top' registrada correctamente.");
	    }
	    
		getServer().getPluginManager().registerEvents(new NPCListener(this), this);
		getServer().getPluginManager().registerEvents(new QuitListener(this), this);
		getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
		getServer().getPluginManager().registerEvents(new JoinListener(this), this);
	    getServer().getPluginManager().registerEvents(new CheckpointListener(this), this);
	    getServer().getPluginManager().registerEvents(new DeathListener(this), this);
	}
	
	public CheckpointManager getCheckpointManager() {
	    return checkpointManager;
	}

	public PlayerDataManager getPlayerDataManager() {
		return playerDataManager;
	}

	public ParkourManager getParkourManager() {
		return parkourManager;
	}
}
