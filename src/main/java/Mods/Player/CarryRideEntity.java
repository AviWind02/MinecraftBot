package Mods.Player;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.logging.Level;

import static org.bukkit.block.BlockType.AIR;

public class CarryRideEntity {

    private boolean toggleRide = false;
    private boolean carryPlugin = false;

    public void onPlayerInteractCarryRide(PlayerInteractEntityEvent event) {

        if(!carryPlugin)
            return;

        Player player = event.getPlayer();//Get the player who performed the interaction
        ItemStack itemInHand = player.getInventory().getItemInMainHand();// Get the item in the player's main hand
        boolean isHandEmpty = itemInHand == null || itemInHand.getType() == AIR.asMaterial();// Check if the item in hand is null or air (empty hand)

        if(isHandEmpty) // If hand is empty ride entity.
        {
            Entity clickedEntity = event.getRightClicked();// Get the entity that was right-clicked
            event.getPlayer().sendMessage("You right-clicked an entity: " + clickedEntity.getType());//Just log it here
            if(toggleRide) {
                clickedEntity.addPassenger(player);//Ride Clicked Entity
            }
            else {
                player.addPassenger(clickedEntity);//Have Entity Ride you
            }
        }
    }
    public void onPlayerInteractRemoveRider(PlayerInteractEvent event) {

        if(!carryPlugin)
            return;

        Player player = event.getPlayer();//Get the player who performed the interaction
        ItemStack itemInHand = player.getInventory().getItemInMainHand();// Get the item in the player's main hand
        boolean isHandEmpty = itemInHand == null || itemInHand.getType() == AIR.asMaterial();// Check if the item in hand is null or air (empty hand)

        if(isHandEmpty) // If hand is empty ride entity.
        {
          if((!toggleRide && isHandEmpty) && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK))
          {
            List Rider = player.getPassengers();
            int numOfRider = Rider.size();
            for (int i = 0; i < numOfRider; i++)
            {
                player.removePassenger((Entity) Rider.get(i));
            }

          }
        }
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;

        try {
            if (command.getName().toLowerCase() == "toggleride") {
                toggleRide = !toggleRide;
                player.sendMessage(ChatColor.BLUE + "Ride is not Toggle: " + toggleRide);

            }
            if (command.getName().toLowerCase() == "togglecarryplugin") {
                carryPlugin = !carryPlugin;
                player.sendMessage(ChatColor.BLUE + "Ride is not Toggle: " + carryPlugin);

            }
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "An error occurred while executing the command. Please check the server logs for details.");
            return true;
        }
        return false;
    }
}


