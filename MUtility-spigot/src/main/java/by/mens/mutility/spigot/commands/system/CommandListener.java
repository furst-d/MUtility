package by.mens.mutility.spigot.commands.system;

import by.mens.mutility.spigot.MUtilitySpigot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class CommandListener implements CommandExecutor, TabCompleter {
    private MUtilitySpigot plugin;

    public CommandListener(MUtilitySpigot plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<CommandData> commands = plugin.getCommands();
        for (CommandData commandData: commands) {
            if(command.getName().equalsIgnoreCase(commandData.getCommandName())) {
                //Prikaz nalezen
                List<CommandData> subcommands = commandData.next;
                //Projed vsechny parametry v prikazu
                for (int i = 0; i < args.length; i++) {
                    for (CommandData subcommand: subcommands) {
                        if(subcommand.getSubcommand().equalsIgnoreCase(args[i])) {
                            subcommands = subcommand.next;
                            break;
                        } else {
                            return false;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
