package rottenbonestudio.parkourgame.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import rottenbonestudio.parkourgame.SpeedrunPlugin;

public class DeathListener implements Listener {

	private final SpeedrunPlugin plugin;

	public DeathListener(SpeedrunPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (plugin.getParkourManager().getActiveParkour(event.getEntity()) != null) {
			event.setKeepInventory(true);
			event.getDrops().clear();
		}
	}
}
