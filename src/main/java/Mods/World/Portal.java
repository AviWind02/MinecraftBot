package Mods.World;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Portal implements Listener {

    private Block portalBlock;
    private Block portalBlock2;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock != null) {
                player.sendMessage(clickedBlock + " set as portal block");
                setBlockAsPortal(clickedBlock);
            }
        }
    }

    public Block getBlockPlayerIsStandingOn(Player player) {
        Location loc = player.getLocation();
        return loc.subtract(0, 1, 0).getBlock(); // Get the block beneath the player's feet
    }

    public void setBlockAsPortal(Block block) {
        this.portalBlock = block;
    }

    public void checkPlayerStandingOnPortal(JavaPlugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Block blockBeneath = getBlockPlayerIsStandingOn(player);
                    if (portalBlock != null && blockBeneath.equals(portalBlock)) {
                        player.sendMessage("You are standing on the portal block!");
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // Runs every second (20 ticks)
    }
}
