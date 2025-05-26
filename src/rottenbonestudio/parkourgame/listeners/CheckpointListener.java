package rottenbonestudio.parkourgame.listeners;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import rottenbonestudio.parkourgame.SpeedrunPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CheckpointListener implements Listener {

    private final SpeedrunPlugin plugin;
    private final Map<UUID, Long> lastCheckpointTime = new HashMap<>();
    private static final long COOLDOWN_MS = 3000;

    public CheckpointListener(SpeedrunPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!plugin.getParkourManager().hasActiveTimer(player)) return;

        Block block = player.getLocation().getBlock();
        if (block.getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {

            long now = System.currentTimeMillis();
            if (lastCheckpointTime.containsKey(uuid) && (now - lastCheckpointTime.get(uuid)) < COOLDOWN_MS) {
                return;
            }
            lastCheckpointTime.put(uuid, now);

            if (plugin.getCheckpointManager().hasCheckpoint(uuid)) {
                if (plugin.getCheckpointManager().getCheckpoint(uuid).getBlock().equals(block)) {
                    return;
                }
            }

            plugin.getCheckpointManager().setCheckpoint(uuid, player.getLocation());
            player.sendMessage(plugin.name + "§6✔ Punto de control guardado.");
            player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation().add(0, 1, 0), 20, 0.3, 0.5, 0.3);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.6f, 1.6f);
        }
    }
    
}
