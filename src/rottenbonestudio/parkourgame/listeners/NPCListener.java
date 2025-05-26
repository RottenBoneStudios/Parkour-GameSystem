package rottenbonestudio.parkourgame.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import rottenbonestudio.parkourgame.SpeedrunPlugin;
import rottenbonestudio.parkourgame.managers.ParkourManager;

@SuppressWarnings("unused")
public class NPCListener implements Listener {

    private final SpeedrunPlugin plugin;

    public NPCListener(SpeedrunPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onNPCClick(NPCRightClickEvent event) {
    	Player player = event.getClicker();
        int npcId = event.getNPC().getId();

        ParkourManager pm = plugin.getParkourManager();
        String npcParkourId = pm.getParkourByNPCId(npcId);
        String playerParkourId = pm.getActiveParkour(player);

        if (npcParkourId == null) return;

        if (pm.isStartNPC(npcId, npcParkourId)) {
            if (playerParkourId != null) {
                player.sendMessage(plugin.name + "§cYa estás en el parkour: " + playerParkourId + ". Termínalo primero.");
                return;
            }
            player.sendMessage(plugin.name + "§e¡Comenzaste el parkour: " + npcParkourId + "!");
            pm.startParkour(player, npcParkourId);
            return;
        }

        if (pm.isEndNPC(npcId, npcParkourId)) {
            if (playerParkourId == null) {
                return;
            }
            if (!playerParkourId.equals(npcParkourId)) {
                player.sendMessage(plugin.name + "§cEste NPC no es el final de tu parkour actual.");
                return;
            }
            pm.endParkour(player, npcParkourId);
        }
    }

}
