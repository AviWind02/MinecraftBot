package org.mcbot.mcbotnew;

import Mods.Player.CarryRideEntity;
import Mods.World.Portal;
import coordinate.CoordFunctions;
import OpenAI.OpenAiBot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin implements Listener {

    private OpenAiBot openAiBot;
    private CoordFunctions coordFunctions;
    private CarryRideEntity carryRideEntity;
    private Portal portal;

    @Override
    public void onEnable() {
        // Registering the command "/GPT"
        this.getCommand("GPT").setExecutor(this);
        getLogger().info("Minecraft plugin has been enabled!");

        // Initialize OpenAiBot
        openAiBot = new OpenAiBot();

        // Initialize and register CoordFunctions
        coordFunctions = new CoordFunctions(this);
        coordFunctions.registerEvents();

        carryRideEntity = new CarryRideEntity();

        portal = new Portal();
        portal.checkPlayerStandingOnPortal(this);
        // Register the event listener
        getServer().getPluginManager().registerEvents(this, this);


    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (openAiBot.onCommand(sender, command, label, args)) {
            return true;
        }

        if (carryRideEntity.onCommand(sender, command, label, args)) {
            return true;
        }

        return coordFunctions.onCommand(sender, command, label, args);
    }

    // Event handler for player interactions

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {

        carryRideEntity.onPlayerInteractCarryRide(event);
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        carryRideEntity.onPlayerInteractRemoveRider(event);
        portal.onPlayerInteract(event);
    }

}
