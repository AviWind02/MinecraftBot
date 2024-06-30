package org.mcbot.mcbotnew;

import OpenAI.OpenAiService;
import coordinate.CoordFunctions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class MCBotNew extends JavaPlugin {

    private OpenAiService openAiService;
    private CoordFunctions coordFunctions;

    @Override
    public void onEnable() {
        // Registering the command "/question"
        this.getCommand("GPT").setExecutor(this);
        getLogger().info("MC Bot plugin has been enabled.");

        // Initialize OpenAiService
        openAiService = new OpenAiService();

        // Initialize and register CoordFunctions
        coordFunctions = new CoordFunctions();
        coordFunctions.registerEvents();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("GPT")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length == 0) {
                    player.sendMessage("Please provide a question.");
                    return true;
                }

                String question = String.join(" ", args);
                player.sendMessage("One moment, thinking...");

                // Run the OpenAI request in a separate thread to avoid blocking the server
                new Thread(() -> {
                    try {
                        String response = openAiService.questionAsync(question);
                        player.sendMessage("Response: " + response);
                    } catch (IOException e) {
                        player.sendMessage("Failed to get response from OpenAI: " + e.getMessage());
                        e.printStackTrace();
                    }
                }).start();

            } else {
                sender.sendMessage("Only players can execute this command!");
            }
            return true;
        }

        // Delegate coordinate-related commands to CoordFunctions
        return coordFunctions.onCommand(sender, command, label, args);
    }
}
