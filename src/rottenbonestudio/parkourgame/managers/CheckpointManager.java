package rottenbonestudio.parkourgame.managers;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CheckpointManager {

    private final Map<UUID, Location> checkpoints = new HashMap<>();

    public void setCheckpoint(UUID playerId, Location location) {
        checkpoints.put(playerId, location.clone());
    }

    public Location getCheckpoint(UUID playerId) {
        return checkpoints.get(playerId);
    }

    public boolean hasCheckpoint(UUID playerId) {
        return checkpoints.containsKey(playerId);
    }

    public void clearCheckpoint(UUID playerId) {
        checkpoints.remove(playerId);
    }

    public void clearAll() {
        checkpoints.clear();
    }
}
