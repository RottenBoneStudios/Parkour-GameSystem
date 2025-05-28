package rottenbonestudio.parkourgame.listeners;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.entity.Player;

import rottenbonestudio.parkourgame.SpeedrunPlugin;
import rottenbonestudio.parkourgame.managers.ParkourManager;

public class FlightPreventionListener implements Listener {

    private final SpeedrunPlugin plugin;

    public FlightPreventionListener(SpeedrunPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        ParkourManager parkourManager = plugin.getParkourManager();

        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;

        if (parkourManager.getActiveParkour(player) != null) {
            event.setCancelled(true);
            player.setAllowFlight(false);
            player.setFlying(false);
        }
    }
}
