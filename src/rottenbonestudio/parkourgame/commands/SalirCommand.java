package rottenbonestudio.parkourgame.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;

import rottenbonestudio.parkourgame.SpeedrunPlugin;
import rottenbonestudio.parkourgame.managers.ParkourManager;

public class SalirCommand implements CommandExecutor {

    private final SpeedrunPlugin plugin;

    public SalirCommand(SpeedrunPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if (!(sender instanceof Player)) {
    	    sender.sendMessage("Este comando solo puede ser usado por jugadores.");
    	    return true;
    	}
    	
    	Player player = (Player) sender;

        ParkourManager pm = plugin.getParkourManager();
        String parkourId = pm.getActiveParkour(player);

        if (parkourId == null) {
            player.sendMessage("§cNo estás haciendo un parkour.");
            return true;
        }

        pm.cancelParkour(player);
        player.sendMessage("§cHas cancelado el parkour: §e" + parkourId);

        ConfigurationSection section = plugin.getConfig().getConfigurationSection("parkours." + parkourId + ".finishLocation");
        if (section != null) {
            String worldName = section.getString("world");
            World world = Bukkit.getWorld(worldName);
            if (world != null) {
                double x = section.getDouble("x");
                double y = section.getDouble("y");
                double z = section.getDouble("z");
                float yaw = (float) section.getDouble("yaw");
                float pitch = (float) section.getDouble("pitch");
                Location loc = new Location(world, x, y, z, yaw, pitch);
                player.teleport(loc);
            } else {
                player.sendMessage("§c¡No se encontró el mundo '" + worldName + "' para teletransportar!");
            }
        }

        return true;
    }
    
}
