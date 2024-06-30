package coordinate;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class CoordFunctions implements Listener {

    private final Map<String, Location> homeCoords = new HashMap<>();
    private final Map<String, Location> deathCoords = new HashMap<>();
    private final Map<String, Map<String, Location>> customCoords = new HashMap<>();
    private final Map<String, Boolean> notifyOnDeath = new HashMap<>();

    public void registerEvents() {
        Bukkit.getServer().getPluginManager().registerEvents(this, JavaPlugin.getProvidingPlugin(CoordFunctions.class));
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;
        String playerName = player.getName();

        switch (command.getName().toLowerCase()) {
            case "sethome":
                homeCoords.put(playerName, player.getLocation());
                player.sendMessage(ChatColor.GREEN + "Home location set!");
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
                return true;
            case "setcoord":
                if (args.length != 1) {
                    player.sendMessage(ChatColor.RED + "Usage: /setcoord <name>");
                    return false;
                }
                String coordName = args[0];
                customCoords.putIfAbsent(playerName, new HashMap<>());
                customCoords.get(playerName).put(coordName, player.getLocation());
                player.sendMessage(ChatColor.GREEN + "Coordinates saved as " + coordName);
                return true;
            case "getcoord":
                if (args.length != 1) {
                    player.sendMessage(ChatColor.RED + "Usage: /getcoord <name>");
                    return false;
                }
                coordName = args[0];
                String closestName = findClosestCoordinateName(customCoords, playerName, coordName);
                if (closestName != null) {
                    Location loc = customCoords.get(playerName).get(closestName);
                    player.sendMessage(ChatColor.GREEN + "Closest match for " + coordName + ": " + closestName + " with coordinates: " + formatLocation(loc));
                } else {
                    player.sendMessage(ChatColor.RED + "No coordinates found.");
                }
                return true;
            default:
                return false;
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
    }

    private String formatLocation(Location location) {
        return "X: " + location.getBlockX() + ", Y: " + location.getBlockY() + ", Z: " + location.getBlockZ();
    }

    private int levenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) {
            for (int j = 0; j <= b.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(dp[i - 1][j - 1]
                                    + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1),
                            Math.min(dp[i - 1][j] + 1,
                                    dp[i][j - 1] + 1));
                }
            }
        }
        return dp[a.length()][b.length()];
    }

    private String findClosestCoordinateName(Map<String, Map<String, Location>> customCoords, String playerName, String inputName) {
        if (!customCoords.containsKey(playerName)) {
            return null;
        }
        int minDistance = Integer.MAX_VALUE;
        String closestName = null;
        for (String name : customCoords.get(playerName).keySet()) {
            int distance = levenshteinDistance(name.toLowerCase(), inputName.toLowerCase());
            if (distance < minDistance) {
                minDistance = distance;
                closestName = name;
            }
        }
        return closestName;
    }
}
