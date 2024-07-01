package coordinate;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class CoordFunctions implements Listener {

    private final Map<String, Location> homeCoords = new HashMap<>();
    private final Map<String, Location> deathCoords = new HashMap<>();
    private final Map<String, Location> sharedCoords = new HashMap<>();
    private final Map<String, Boolean> notifyOnDeath = new HashMap<>();

    private final JavaPlugin plugin;
    private final File configFile;
    private FileConfiguration config;

    public CoordFunctions(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "coordinates.yml");
        loadCoordinates();
    }

    public void registerEvents() {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;
        String playerName = player.getName();

        try {
            switch (command.getName().toLowerCase()) {
                case "sethome":
                    homeCoords.put(playerName, player.getLocation());
                    player.sendMessage(ChatColor.GREEN + "Home location set!");
                    logAction(playerName, "set home location");
                    saveCoordinates();
                    return true;
                case "home":
                    if (homeCoords.containsKey(playerName)) {
                        Location home = homeCoords.get(playerName);
                        player.sendMessage(ChatColor.GREEN + "Home coordinates: " + formatLocation(home));
                    } else {
                        player.sendMessage(ChatColor.RED + "Home location not set.");
                    }
                    return true;
                case "setdeathnotify":
                    if (args.length != 1) {
                        player.sendMessage(ChatColor.RED + "Usage: /setdeathnotify <on|off>");
                        return false;
                    }
                    boolean notify = args[0].equalsIgnoreCase("on");
                    notifyOnDeath.put(playerName, notify);
                    player.sendMessage(ChatColor.GREEN + "Death notification set to " + args[0]);
                    logAction(playerName, "set death notification to " + args[0]);
                    saveCoordinates();
                    return true;
                case "setcoord":
                    if (args.length != 1) {
                        player.sendMessage(ChatColor.RED + "Usage: /setcoord <name>");
                        return false;
                    }
                    String coordName = args[0];
                    sharedCoords.put(coordName, player.getLocation());
                    player.sendMessage(ChatColor.GREEN + "Coordinates saved as " + coordName);
                    logAction(playerName, "set shared coordinate " + coordName);
                    saveCoordinates();
                    return true;
                case "getcoord":
                    if (args.length != 1) {
                        player.sendMessage(ChatColor.RED + "Usage: /getcoord <name>");
                        return false;
                    }
                    coordName = args[0];
                    if (sharedCoords.containsKey(coordName)) {
                        Location loc = sharedCoords.get(coordName);
                        player.sendMessage(ChatColor.GREEN + coordName + " coordinates: " + formatLocation(loc));
                    } else {
                        player.sendMessage(ChatColor.RED + "No coordinates found with name " + coordName);
                    }
                    logAction(playerName, "retrieved coordinate " + coordName);
                    return true;
                case "listcoords":
                    listCoordinates(player);
                    logAction(playerName, "listed all coordinates");
                    return true;
                default:
                    return false;
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred while executing command: " + command.getName(), e);
            player.sendMessage(ChatColor.RED + "An error occurred while executing the command. Please check the server logs for details.");
            return true;
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        String playerName = player.getName();
        deathCoords.put(playerName, player.getLocation());
        if (notifyOnDeath.getOrDefault(playerName, false)) {
            player.sendMessage(ChatColor.RED + "You died at: " + formatLocation(player.getLocation()));
        }
        logAction(playerName, "died at " + formatLocation(player.getLocation()));
        saveCoordinates();
    }

    private String formatLocation(Location location) {
        return "X: " + location.getBlockX() + ", Y: " + location.getBlockY() + ", Z: " + location.getBlockZ();
    }

    private void saveCoordinates() {
        config = YamlConfiguration.loadConfiguration(configFile);

        for (Map.Entry<String, Location> entry : homeCoords.entrySet()) {
            config.set("homeCoords." + entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Location> entry : deathCoords.entrySet()) {
            config.set("deathCoords." + entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Location> entry : sharedCoords.entrySet()) {
            config.set("sharedCoords." + entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Boolean> entry : notifyOnDeath.entrySet()) {
            config.set("notifyOnDeath." + entry.getKey(), entry.getValue());
        }

        try {
            config.save(configFile);
            plugin.getLogger().info("Coordinates have been saved successfully.");
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred while saving coordinates to file.", e);
        }
    }

    private void loadCoordinates() {
        if (!configFile.exists()) {
            return;
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        try {
            if (config.getConfigurationSection("homeCoords") != null) {
                for (String key : config.getConfigurationSection("homeCoords").getKeys(false)) {
                    homeCoords.put(key, config.getLocation("homeCoords." + key));
                }
            }

            if (config.getConfigurationSection("deathCoords") != null) {
                for (String key : config.getConfigurationSection("deathCoords").getKeys(false)) {
                    deathCoords.put(key, config.getLocation("deathCoords." + key));
                }
            }

            if (config.getConfigurationSection("sharedCoords") != null) {
                for (String key : config.getConfigurationSection("sharedCoords").getKeys(false)) {
                    sharedCoords.put(key, config.getLocation("sharedCoords." + key));
                }
            }

            if (config.getConfigurationSection("notifyOnDeath") != null) {
                for (String key : config.getConfigurationSection("notifyOnDeath").getKeys(false)) {
                    notifyOnDeath.put(key, config.getBoolean("notifyOnDeath." + key));
                }
            }

            plugin.getLogger().info("Coordinates have been loaded successfully.");
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred while loading coordinates from file.", e);
        }
    }

    private void listCoordinates(Player player) {
        player.sendMessage(ChatColor.GREEN + "Home coordinates:");
        String playerName = player.getName();
        if (homeCoords.containsKey(playerName)) {
            player.sendMessage(ChatColor.GREEN + "Home: " + formatLocation(homeCoords.get(playerName)));
        } else {
            player.sendMessage(ChatColor.RED + "No home coordinates found.");
        }

        player.sendMessage(ChatColor.GREEN + "Death coordinates:");
        if (deathCoords.containsKey(playerName)) {
            player.sendMessage(ChatColor.GREEN + "Last Death: " + formatLocation(deathCoords.get(playerName)));
        } else {
            player.sendMessage(ChatColor.RED + "No death coordinates found.");
        }

        player.sendMessage(ChatColor.GREEN + "Shared coordinates:");
        if (!sharedCoords.isEmpty()) {
            for (Map.Entry<String, Location> entry : sharedCoords.entrySet()) {
                player.sendMessage(ChatColor.GREEN + entry.getKey() + ": " + formatLocation(entry.getValue()));
            }
        } else {
            player.sendMessage(ChatColor.RED + "No shared coordinates found.");
        }
    }

    private void logAction(String playerName, String action) {
        plugin.getLogger().info("Player " + playerName + " " + action);
    }
}
