package org.mcbot.mcbotnew;

import Mods.Player.CarryRideEntity;
import coordinate.CoordFunctions;
import OpenAI.OpenAiBot;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.List;

import static org.bukkit.block.BlockType.AIR;

public final class MCBotNew extends JavaPlugin implements Listener {

    private OpenAiBot openAiBot;
    private CoordFunctions coordFunctions;
    private CarryRideEntity carryRideEntity;

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
    }

}
