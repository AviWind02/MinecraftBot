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

public class Portal implements Listener {//This plugin part would create a simple portal/TP block. You stand on it and it'll TP you to the other block.

    private Block[] portals = new Block[2];

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) { //Handles setting up the two portals
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock != null) {
                if (!isPortalBlock(clickedBlock)) {//Only set if its not set on that block yet
                    setBlockAsPortal(player, clickedBlock);
                }
            }
        }
    }

    public boolean isPortalBlock(Block block) {//Could have added a Delay to stop it but for now this would do. The same block should not be the portal twice
        return (portals[0] != null && block.equals(portals[0])) ||
                (portals[1] != null && block.equals(portals[1]));
    }

    public Block getBlockPlayerIsStandingOn(Player player) {
        Location loc = player.getLocation();
        return loc.subtract(0, 1, 0).getBlock();
    }

    public void setBlockAsPortal(Player player, Block block) {//Sets the block for now just two is ok till we get close to the final part of this mod
        if (portals[0] == null) {
            portals[0] = block;
            player.sendMessage(block + " set as portal 1 block");
        } else if (portals[1] == null) {
            portals[1] = block;
            player.sendMessage(block + " set as portal 2 block");
        } else {
            player.sendMessage("Both portals are already set.");
        }
    }



    public boolean isStandingOnPortal(Player player, Block blockBeneath) {
        return (portals[0] != null && blockBeneath.equals(portals[0])) ||
                (portals[1] != null && blockBeneath.equals(portals[1]));
    }

    public Block getTargetPortal(Block blockBeneath) {
        if (portals[0] != null && blockBeneath.equals(portals[0])) {
            return portals[1];
        } else if (portals[1] != null && blockBeneath.equals(portals[1])) {
            return portals[0];
        }
        return null;
    }

    public void teleportPlayer(Player player, Block targetBlock) {
        if (targetBlock != null) {
            Location targetLocation = targetBlock.getLocation().add(0.5, 1, 0.5);
            player.teleport(targetLocation);
            player.sendMessage("You have been teleported!");
        } else {
            player.sendMessage("No target portal set.");
        }
    }

    public void checkPlayerStandingOnPortal(JavaPlugin plugin) {//Main part for this mods, handles the TP
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Block blockBeneath = getBlockPlayerIsStandingOn(player);
                    if (isStandingOnPortal(player, blockBeneath)) {
                        teleportPlayer(player, getTargetPortal(blockBeneath));
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

}
