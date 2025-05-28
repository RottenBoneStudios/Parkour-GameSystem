package rottenbonestudio.parkourgame.managers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import rottenbonestudio.parkourgame.SpeedrunPlugin;
import rottenbonestudio.parkourgame.utils.ParkourTimer;

public class ParkourManager {

	private final SpeedrunPlugin plugin;
	private final Map<UUID, BossBar> timers = new HashMap<>();
	private final Map<UUID, Long> startTimes = new HashMap<>();
	private final Map<String, ConfigurationSection> parkours;

	private final Map<UUID, String> activeParkour = new HashMap<>();
	
	private final Map<UUID, ItemStack[]> savedInventories = new HashMap<>();
	private final Map<UUID, ItemStack[]> savedArmorContents = new HashMap<>();

	public ParkourManager(SpeedrunPlugin plugin) {
		this.plugin = plugin;
		this.parkours = new HashMap<>();
		ConfigurationSection section = plugin.getConfig().getConfigurationSection("parkours");
		if (section != null) {
			for (String key : section.getKeys(false)) {
				parkours.put(key, section.getConfigurationSection(key));
			}
		}
	}

	public String getParkourByNPCId(int npcId) {
		for (Map.Entry<String, ConfigurationSection> entry : parkours.entrySet()) {
			ConfigurationSection section = entry.getValue();
			if (npcId == section.getInt("startNPC") || npcId == section.getInt("endNPC")) {
				return entry.getKey();
			}
		}
		return null;
	}

	public boolean isStartNPC(int npcId, String parkourId) {
		ConfigurationSection section = parkours.get(parkourId);
		return section != null && section.getInt("startNPC") == npcId;
	}

	public boolean isEndNPC(int npcId, String parkourId) {
		ConfigurationSection section = parkours.get(parkourId);
		return section != null && section.getInt("endNPC") == npcId;
	}

	public void startParkour(Player player, String parkourId) {
		if (activeParkour.containsKey(player.getUniqueId())) {
			player.sendMessage(plugin.name + "§cYa estás haciendo un parkour. ¡Termínalo antes de empezar otro!");
			return;
		}

		ConfigurationSection parkour = parkours.get(parkourId);
		if (parkour == null) {
			player.sendMessage(plugin.name + "§cNo se encontró la configuración del parkour.");
			return;
		}

		ConfigurationSection locSec = parkour.getConfigurationSection("startLocation");
		if (locSec != null) {
			String worldName = locSec.getString("world");
			double x = locSec.getDouble("x");
			double y = locSec.getDouble("y");
			double z = locSec.getDouble("z");
			float yaw = (float) locSec.getDouble("yaw");
			float pitch = (float) locSec.getDouble("pitch");

			if (Bukkit.getWorld(worldName) != null) {
				player.teleport(new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch));
			} else {
				player.sendMessage(plugin.name + "§c¡No se encontró el mundo '" + worldName + "' para teleport!");
				return;
			}
		}

		activeParkour.put(player.getUniqueId(), parkourId);
		savedInventories.put(player.getUniqueId(), player.getInventory().getContents().clone());
		savedArmorContents.put(player.getUniqueId(), player.getInventory().getArmorContents().clone());
		player.getInventory().clear();
		player.sendMessage(plugin.name + "§ePreparándote para comenzar...");


		ItemStack tinte = new ItemStack(Material.LIME_DYE);
		ItemMeta meta = tinte.getItemMeta();
		if (meta != null) {
			meta.setDisplayName("§aOcultar jugadores");
			meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "hide_toggle"), PersistentDataType.STRING,
					"hide");
			tinte.setItemMeta(meta);
		}
		player.getInventory().setItem(8, tinte);

		ItemStack cama = new ItemStack(Material.RED_BED);
		ItemMeta camaMeta = cama.getItemMeta();
		if (camaMeta != null) {
			camaMeta.setDisplayName("§cSalir del parkour");
			camaMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "parkour_exit"),
					PersistentDataType.STRING, "exit");
			cama.setItemMeta(camaMeta);
		}
		player.getInventory().setItem(0, cama);

		ItemStack checkpoint = new ItemStack(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
		ItemMeta cpMeta = checkpoint.getItemMeta();
		if (cpMeta != null) {
			cpMeta.setDisplayName("§eVolver al punto de control");
			cpMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "parkour_checkpoint"),
					PersistentDataType.STRING, "checkpoint");
			checkpoint.setItemMeta(cpMeta);
		}
		player.getInventory().setItem(1, checkpoint);

		new BukkitRunnable() {
			int count = 3;
			final Location fixedLocation = player.getLocation().clone();

			@Override
			public void run() {
				if (!player.isOnline() || !activeParkour.containsKey(player.getUniqueId())) {
					cancel();
					return;
				}

				if (!player.getLocation().getBlock().equals(fixedLocation.getBlock())) {
					cancelParkour(player);
					player.sendMessage(plugin.name + "§c¡Te moviste! Cancelando el inicio del parkour.");
					cancel();
					return;
				}

				if (count > 0) {
					player.sendTitle("§e" + count, "§7¡No te muevas!", 0, 20, 10);
					player.playSound(player.getLocation(), "minecraft:block.note_block.pling", 1.0f, 1.5f);
					count--;
				} else {
					cancel();
					beginTimer(player, parkourId);
				}
			}
		}.runTaskTimer(plugin, 0L, 20L);
	}

	private void beginTimer(Player player, String parkourId) {
		BossBar bar = Bukkit.createBossBar(
				"§x§F§F§F§8§0§0§lᴛ§x§F§9§E§5§0§0§lɪ§x§F§4§D§2§0§0§lᴇ§x§E§E§B§F§0§0§lᴍ§x§E§8§A§C§0§0§lᴘ§x§E§3§9§9§0§0§lᴏ §x§D§7§7§2§0§0§lᴀ§x§D§1§5§F§0§0§lᴄ§x§C§C§4§C§0§0§lᴛ§x§C§6§3§9§0§0§lᴜ§x§C§0§2§6§0§0§lᴀ§x§B§B§1§3§0§0§lʟ§x§B§5§0§0§0§0§l §f0s",
				BarColor.BLUE, BarStyle.SOLID);
		bar.addPlayer(player);
		timers.put(player.getUniqueId(), bar);
		startTimes.put(player.getUniqueId(), System.currentTimeMillis());

		new ParkourTimer(player, bar, plugin).runTaskTimer(plugin, 0L, 20L);

		player.sendMessage("");
		player.sendMessage(plugin.name + ": información útil");
		player.sendMessage("§8§m                                        ");
		player.sendMessage("§6§l¡Has comenzado el parkour §e" + parkourId + "§6!");
		player.sendMessage("§7Utiliza §a/salir §7o §a/spawn §7para cancelarlo.");
		player.sendMessage("§8§m                                        ");
	}

	public void endParkour(Player player, String parkourId) {
		long start = startTimes.remove(player.getUniqueId());
		long elapsed = (System.currentTimeMillis() - start) / 1000;

		BossBar bar = timers.remove(player.getUniqueId());
		if (bar != null)
			bar.removeAll();

		activeParkour.remove(player.getUniqueId());
		plugin.getCheckpointManager().clearCheckpoint(player.getUniqueId());

		for (Player online : Bukkit.getOnlinePlayers()) {
			if (!online.equals(player)) {
				player.showPlayer(plugin, online);
			}
		}

		ItemStack item = player.getInventory().getItem(8);
		if (isToggleItem(item)) {
			player.getInventory().clear(8);
		}

		ItemStack slot0 = player.getInventory().getItem(0);
		if (isExitItem(slot0)) {
			player.getInventory().clear(0);
		}

		ItemStack checkpoint = player.getInventory().getItem(1);
		if (isCheckpointItem(checkpoint)) {
			player.getInventory().clear(1);
		}
		
		ItemStack[] inventory = savedInventories.remove(player.getUniqueId());
		ItemStack[] armor = savedArmorContents.remove(player.getUniqueId());
		if (inventory != null) {
		    player.getInventory().setContents(inventory);
		}
		if (armor != null) {
		    player.getInventory().setArmorContents(armor);
		}

		ConfigurationSection parkour = parkours.get(parkourId);
		if (parkour != null) {
			for (String cmd : parkour.getStringList("endCommands")) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName()));
			}
		}

		player.sendMessage(plugin.name + "§aTerminaste el parkour en §b" + elapsed + "s");

		UUID uuid = player.getUniqueId();
		int previousTime = plugin.getPlayerDataManager().getBestTime(uuid, parkourId);
		boolean isNewPersonal = previousTime == -1 || elapsed < previousTime;

		if (isNewPersonal) {
			plugin.getPlayerDataManager().setBestTime(uuid, parkourId, (int) elapsed);
			player.sendMessage(
					plugin.name + "§b¡Nuevo récord personal para " + parkourId + "! §a" + elapsed + " segundos.");
		} else {
			player.sendMessage(plugin.name + "§7Tu récord actual para " + parkourId + " es §e" + previousTime + "s");
		}

		Map<String, Object> all = plugin.getPlayerDataManager().getData().getValues(false);
		Map<UUID, Integer> tiempos = new HashMap<>();

		for (String uuidStr : all.keySet()) {
			try {
				UUID u = UUID.fromString(uuidStr);
				int t = plugin.getPlayerDataManager().getData().getInt(uuidStr + "." + parkourId, -1);
				if (t > 0)
					tiempos.put(u, t);
			} catch (IllegalArgumentException ignored) {
			}
		}

		List<Map.Entry<UUID, Integer>> ordenado = tiempos.entrySet().stream().sorted(Map.Entry.comparingByValue())
				.collect(Collectors.toList());

		if (!ordenado.isEmpty() && ordenado.get(0).getKey().equals(uuid) && elapsed == ordenado.get(0).getValue()) {
			Bukkit.broadcastMessage(plugin.name + "§6§l¡" + player.getName()
					+ " ha establecido un nuevo récord mundial en §e" + parkourId + "§6 con §b" + elapsed + "s§6!");

			Bukkit.getOnlinePlayers()
					.forEach(p -> p.playSound(p.getLocation(), "minecraft:ui.toast.challenge_complete", 1f, 1f));
		}

		for (int i = 0; i < Math.min(10, ordenado.size()); i++) {
			Map.Entry<UUID, Integer> entry = ordenado.get(i);
			if (entry.getKey().equals(uuid) && entry.getValue() == elapsed) {
				Bukkit.broadcastMessage(plugin.name + "§e¡" + player.getName() + " ha entrado al Top §6" + (i + 1)
						+ "§e del parkour §b" + parkourId + "§e!");
				break;
			}
		}

		player.playSound(player.getLocation(), "minecraft:entity.player.levelup", 1f, 1.2f);

		if (parkour != null && parkour.isConfigurationSection("finishLocation")) {
			ConfigurationSection locSec = parkour.getConfigurationSection("finishLocation");
			if (locSec != null) {
				String worldName = locSec.getString("world");
				double x = locSec.getDouble("x");
				double y = locSec.getDouble("y");
				double z = locSec.getDouble("z");
				float yaw = (float) locSec.getDouble("yaw");
				float pitch = (float) locSec.getDouble("pitch");

				if (Bukkit.getWorld(worldName) != null) {
					player.teleport(new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch));
				} else {
					player.sendMessage(plugin.name + "§c¡No se encontró el mundo '" + worldName + "' para teleport!");
				}
			}
		}
	}

	public String getParkourByNPC(String npcName) {
		for (Map.Entry<String, ConfigurationSection> entry : parkours.entrySet()) {
			if (npcName.equals(entry.getValue().getString("startNPC")))
				return entry.getKey();
			if (npcName.equals(entry.getValue().getString("endNPC")))
				return entry.getKey();
		}
		return null;
	}

	public void cancelParkour(Player player) {
		UUID uuid = player.getUniqueId();

		startTimes.remove(uuid);
		plugin.getCheckpointManager().clearCheckpoint(player.getUniqueId());

		BossBar bar = timers.remove(uuid);
		if (bar != null)
			bar.removeAll();

		ItemStack item = player.getInventory().getItem(8);
		if (isToggleItem(item)) {
			player.getInventory().clear(8);
		}

		ItemStack slot0 = player.getInventory().getItem(0);
		if (isExitItem(slot0)) {
			player.getInventory().clear(0);
		}

		ItemStack checkpoint = player.getInventory().getItem(1);
		if (isCheckpointItem(checkpoint)) {
			player.getInventory().clear(1);
		}
		
		ItemStack[] inventory = savedInventories.remove(player.getUniqueId());
		ItemStack[] armor = savedArmorContents.remove(player.getUniqueId());
		if (inventory != null) {
		    player.getInventory().setContents(inventory);
		}
		if (armor != null) {
		    player.getInventory().setArmorContents(armor);
		}

		activeParkour.remove(uuid);

		for (Player online : Bukkit.getOnlinePlayers()) {
			if (!online.equals(player)) {
				player.showPlayer(plugin, online);
			}
		}
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

	public static boolean isCheckpointItem(ItemStack item) {
		if (item == null || !item.hasItemMeta())
			return false;
		return item.getItemMeta().getPersistentDataContainer().has(
				new NamespacedKey(SpeedrunPlugin.getPlugin(SpeedrunPlugin.class), "parkour_checkpoint"),
				PersistentDataType.STRING);
	}

	public boolean isStartNPC(String npcName) {
		return parkours.values().stream().anyMatch(s -> npcName.equals(s.getString("startNPC")));
	}

	public boolean isEndNPC(String npcName) {
		return parkours.values().stream().anyMatch(s -> npcName.equals(s.getString("endNPC")));
	}

	public boolean hasActiveTimer(Player player) {
		return timers.containsKey(player.getUniqueId());
	}

	public String getActiveParkour(Player player) {
		return activeParkour.get(player.getUniqueId());
	}

	public String getAllowedWorld(String parkourId) {
		ConfigurationSection section = parkours.get(parkourId);
		return (section != null) ? section.getString("allowedWorld") : null;
	}

}
