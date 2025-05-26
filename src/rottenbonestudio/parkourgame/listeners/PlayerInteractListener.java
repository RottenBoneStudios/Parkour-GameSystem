package rottenbonestudio.parkourgame.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import rottenbonestudio.parkourgame.SpeedrunPlugin;

public class PlayerInteractListener implements Listener {

	private final Map<UUID, Long> lastToggleUse = new HashMap<>();
	private final Map<UUID, Long> lastCheckpointUse = new HashMap<>();

	private static final long COOLDOWN_MS = 3000;

	private final SpeedrunPlugin plugin;

	public PlayerInteractListener(SpeedrunPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();

		if (item == null || !item.hasItemMeta())
			return;
		ItemMeta meta = item.getItemMeta();

		NamespacedKey toggleKey = new NamespacedKey(plugin, "hide_toggle");
		NamespacedKey exitKey = new NamespacedKey(plugin, "parkour_exit");
		NamespacedKey checkpointKey = new NamespacedKey(plugin, "parkour_checkpoint");

		if (meta.getPersistentDataContainer().has(checkpointKey, PersistentDataType.STRING)) {
			UUID uuid = player.getUniqueId();
			long now = System.currentTimeMillis();

			if (lastCheckpointUse.containsKey(uuid) && (now - lastCheckpointUse.get(uuid)) < COOLDOWN_MS) {
				player.sendMessage("§cDebes esperar 3 segundos entre usos.");
				event.setCancelled(true);
				return;
			}

			lastCheckpointUse.put(uuid, now);

			if (plugin.getCheckpointManager().hasCheckpoint(uuid)) {
				player.teleport(plugin.getCheckpointManager().getCheckpoint(uuid));
				player.sendMessage("§eTeletransportado al último punto de control.");
				
				player.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, player.getLocation().add(0, 1, 0), 20, 0.3, 0.5, 0.3);
	            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.6f, 1.6f);
			} else {
				player.sendMessage("§cNo tienes ningún punto de control registrado.");
			}
			event.setCancelled(true);
			return;
		}

		if (meta.getPersistentDataContainer().has(exitKey, PersistentDataType.STRING)) {
			player.performCommand("salir");
			event.setCancelled(true);
			return;
		}

		if (meta.getPersistentDataContainer().has(toggleKey, PersistentDataType.STRING)) {
			UUID uuid = player.getUniqueId();
			long now = System.currentTimeMillis();

			if (lastToggleUse.containsKey(uuid) && (now - lastToggleUse.get(uuid)) < COOLDOWN_MS) {
				player.sendMessage("§cDebes esperar 3 segundos entre usos.");
				event.setCancelled(true);
				return;
			}

			lastToggleUse.put(uuid, now);

			String mode = meta.getPersistentDataContainer().get(toggleKey, PersistentDataType.STRING);

			if ("hide".equals(mode)) {
				for (Player other : plugin.getServer().getOnlinePlayers()) {
					if (!other.equals(player))
						player.hidePlayer(plugin, other);
				}

				ItemStack newItem = new ItemStack(Material.GRAY_DYE);
				ItemMeta newMeta = newItem.getItemMeta();
				if (newMeta != null) {
					newMeta.setDisplayName("§7Mostrar jugadores");
					newMeta.getPersistentDataContainer().set(toggleKey, PersistentDataType.STRING, "show");
					newItem.setItemMeta(newMeta);
				}
				player.getInventory().setItem(8, newItem);
				player.sendMessage("§aJugadores ocultos.");

			} else if ("show".equals(mode)) {
				for (Player other : plugin.getServer().getOnlinePlayers()) {
					if (!other.equals(player))
						player.showPlayer(plugin, other);
				}

				ItemStack newItem = new ItemStack(Material.LIME_DYE);
				ItemMeta newMeta = newItem.getItemMeta();
				if (newMeta != null) {
					newMeta.setDisplayName("§aOcultar jugadores");
					newMeta.getPersistentDataContainer().set(toggleKey, PersistentDataType.STRING, "hide");
					newItem.setItemMeta(newMeta);
				}
				player.getInventory().setItem(8, newItem);
				player.sendMessage("§7Jugadores mostrados.");
			}

			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player))
			return;
		@SuppressWarnings("unused")
		Player player = (Player) event.getWhoClicked();

		ItemStack current = event.getCurrentItem();
		if (isToggleItem(current) || isExitItem(current) || isCheckpointItem(current)) {
			event.setCancelled(true);
		}

	}

	@EventHandler
	public void onItemDrag(InventoryDragEvent event) {
		if (!(event.getWhoClicked() instanceof Player))
			return;
		@SuppressWarnings("unused")
		Player player = (Player) event.getWhoClicked();

		for (ItemStack item : event.getNewItems().values()) {
			if (isToggleItem(item) || isExitItem(item) || isCheckpointItem(item)) {
				event.setCancelled(true);
				break;
			}
		}

	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		ItemStack dropped = event.getItemDrop().getItemStack();
		if (isToggleItem(dropped) || isExitItem(dropped) || isCheckpointItem(dropped)) {
			event.setCancelled(true);
		}

	}

	public static boolean isCheckpointItem(ItemStack item) {
		if (item == null || !item.hasItemMeta())
			return false;
		return item.getItemMeta().getPersistentDataContainer().has(
				new NamespacedKey(SpeedrunPlugin.getPlugin(SpeedrunPlugin.class), "parkour_checkpoint"),
				PersistentDataType.STRING);
	}

	public static boolean isToggleItem(ItemStack item) {
		if (item == null)
			return false;
		if (!item.hasItemMeta())
			return false;
		return item.getItemMeta().getPersistentDataContainer().has(
				new NamespacedKey(SpeedrunPlugin.getPlugin(SpeedrunPlugin.class), "hide_toggle"),
				PersistentDataType.STRING);
	}

	public static boolean isExitItem(ItemStack item) {
		if (item == null || !item.hasItemMeta())
			return false;
		return item.getItemMeta().getPersistentDataContainer().has(
				new NamespacedKey(SpeedrunPlugin.getPlugin(SpeedrunPlugin.class), "parkour_exit"),
				PersistentDataType.STRING);
	}

}
