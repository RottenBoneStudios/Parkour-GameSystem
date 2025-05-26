package rottenbonestudio.parkourgame.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import rottenbonestudio.parkourgame.SpeedrunPlugin;

public class JoinListener implements Listener {

	private final SpeedrunPlugin plugin;

	public JoinListener(SpeedrunPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

	    Bukkit.getScheduler().runTaskLater(plugin, () -> {
	        Location spawnLocation = new Location(Bukkit.getWorld("world"), 16, 41, -5, 0f, 0f);
	        player.teleport(spawnLocation);
	    }, 15L);

		ItemStack item = player.getInventory().getItem(8);
		if (PlayerInteractListener.isToggleItem(item)) {
			player.getInventory().clear(8);
		}

		for (Player other : Bukkit.getOnlinePlayers()) {
			if (!other.equals(player)) {
				player.showPlayer(plugin, other);
			}
		}

		for (Player other : Bukkit.getOnlinePlayers()) {
			if (!other.equals(player)) {
				other.showPlayer(plugin, player);
			}
		}

		ItemStack firstSlot = player.getInventory().getItem(0);
		if (PlayerInteractListener.isExitItem(firstSlot)) {
			player.getInventory().clear(0);
		}

	}

}
