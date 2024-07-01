package OpenAI;

import OpenAI.OpenAiService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class OpenAiBot {

    private final OpenAiService openAiService;

    public OpenAiBot() {
        this.openAiService = new OpenAiService();
    }

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
        return false;
    }
}
