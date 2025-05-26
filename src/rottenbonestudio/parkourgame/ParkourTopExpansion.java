package rottenbonestudio.parkourgame;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import java.util.*;
import java.util.stream.Collectors;

public class ParkourTopExpansion extends PlaceholderExpansion {

	private final SpeedrunPlugin plugin;

	public ParkourTopExpansion(SpeedrunPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getIdentifier() {
		return "parkourtop";
	}

	@Override
	public String getAuthor() {
		return String.join(", ", plugin.getDescription().getAuthors());
	}

	@Override
	public String getVersion() {
		return plugin.getDescription().getVersion();
	}

	@Override
	public boolean persist() {
		return true;
	}

	@Override
	public String onPlaceholderRequest(Player player, String params) {
		String[] parts = params.split("_");
		if (parts.length != 2)
			return "§cFormato inválido";

		String parkourId = parts[0];
		int position;
		try {
			position = Integer.parseInt(parts[1]) - 1;
		} catch (NumberFormatException e) {
			return "§cTop inválido";
		}

		Map<String, Object> all = plugin.getPlayerDataManager().getData().getValues(false);
		Map<UUID, Integer> tiempos = new HashMap<>();

		for (String uuidStr : all.keySet()) {
			try {
				UUID uuid = UUID.fromString(uuidStr);
				int tiempo = plugin.getPlayerDataManager().getData().getInt(uuidStr + "." + parkourId, -1);
				if (tiempo > 0) {
					tiempos.put(uuid, tiempo);
				}
			} catch (IllegalArgumentException ignored) {
			}
		}

		List<Map.Entry<UUID, Integer>> ordenado = tiempos.entrySet().stream().sorted(Map.Entry.comparingByValue())
				.collect(Collectors.toList());

		if (position >= 0 && position < ordenado.size()) {
			Map.Entry<UUID, Integer> entry = ordenado.get(position);
			OfflinePlayer topPlayer = Bukkit.getOfflinePlayer(entry.getKey());
			String nombre = topPlayer.getName() != null ? topPlayer.getName() : "§7Desconocido";
			String tiempoFormateado = formatearTiempo(entry.getValue());

			String posColor = position == 0 ? "§e" : "§f";
			String nameColor = "§f";
			String dashColor = "§7";
			String timeColor = "§f";

			return posColor + (position + 1) + "° " + nameColor + nombre + " " + dashColor + "- " + timeColor
					+ tiempoFormateado;
		}

		return "§7Vacío";
	}

	private String formatearTiempo(int totalSecs) {
		int horas = totalSecs / 3600;
		int minutos = (totalSecs % 3600) / 60;
		int segundos = totalSecs % 60;

		StringBuilder sb = new StringBuilder();
		if (horas > 0)
			sb.append(horas).append(" hora").append(horas > 1 ? "s" : "").append(", ");
		if (minutos > 0)
			sb.append(minutos).append(" minuto").append(minutos > 1 ? "s" : "").append(", ");
		sb.append(segundos).append(" seg");

		return sb.toString();
	}

}
