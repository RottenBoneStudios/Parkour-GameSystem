package rottenbonestudio.parkourgame.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import rottenbonestudio.parkourgame.SpeedrunPlugin;

public class QuitListener implements Listener {

    private final SpeedrunPlugin plugin;

    public QuitListener(SpeedrunPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (plugin.getParkourManager().hasActiveTimer(event.getPlayer())) {
            plugin.getParkourManager().cancelParkour(event.getPlayer());
        }
    }
    
}
