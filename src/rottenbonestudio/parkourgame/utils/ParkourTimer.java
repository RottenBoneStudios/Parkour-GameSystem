package rottenbonestudio.parkourgame.utils;

import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import rottenbonestudio.parkourgame.SpeedrunPlugin;

public class ParkourTimer extends BukkitRunnable {

	private final Player player;
	private final BossBar bar;
	private final SpeedrunPlugin plugin;
	private long seconds = 0;

	public ParkourTimer(Player player, BossBar bar, SpeedrunPlugin plugin) {
		this.player = player;
		this.bar = bar;
		this.plugin = plugin;
	}

	@Override
	public void run() {
		if (!player.isOnline() || !plugin.getParkourManager().hasActiveTimer(player)) {
			this.cancel();
			return;
		}

		String activeParkour = plugin.getParkourManager().getActiveParkour(player);
		if (activeParkour == null) {
			this.cancel();
			return;
		}

		String allowedWorld = plugin.getParkourManager().getAllowedWorld(activeParkour);
		if (allowedWorld != null && !player.getWorld().getName().equals(allowedWorld)) {
			plugin.getParkourManager().cancelParkour(player);
			player.sendMessage(plugin.name + "§cHas abandonado tu parkour anterior.");
			this.cancel();
			return;
		}

		seconds++;
		bar.setTitle(
				"§x§F§F§F§8§0§0§lᴛ§x§F§9§E§5§0§0§lɪ§x§F§4§D§2§0§0§lᴇ§x§E§E§B§F§0§0§lᴍ§x§E§8§A§C§0§0§lᴘ§x§E§3§9§9§0§0§lᴏ §x§D§7§7§2§0§0§lᴀ§x§D§1§5§F§0§0§lᴄ§x§C§C§4§C§0§0§lᴛ§x§C§6§3§9§0§0§lᴜ§x§C§0§2§6§0§0§lᴀ§x§B§B§1§3§0§0§lʟ§x§B§5§0§0§0§0§l §f"
						+ seconds + "s");
		bar.setProgress(Math.min(seconds / 60.0, 1.0));
	}

}
