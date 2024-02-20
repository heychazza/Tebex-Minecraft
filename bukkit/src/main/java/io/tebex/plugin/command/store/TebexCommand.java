package io.tebex.plugin.command.store;

import com.google.common.collect.ImmutableList;
import io.tebex.plugin.Lang;
import io.tebex.plugin.command.SubCommand;
import io.tebex.plugin.manager.StoreCommandManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TebexCommand implements TabExecutor {
    private final StoreCommandManager commandManager;

    public TebexCommand(StoreCommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0) {
            commandManager.getPlatform().sendMessage(sender, "Welcome to Tebex!");
            commandManager.getPlatform().sendMessage(sender, "This server is running version &fv" + commandManager.getPlatform().getDescription().getVersion() + "&7.");
            return true;
        }

        Map<String, SubCommand> commands = commandManager.getCommands();
        if(! commands.containsKey(args[0].toLowerCase())) {
            commandManager.getPlatform().sendMessage(sender, "&cUnknown command.");
            return true;
        }

        final SubCommand subCommand = commands.get(args[0].toLowerCase());
        if (! sender.hasPermission(subCommand.getPermission())) {
            commandManager.getPlatform().sendMessage(sender, "&cYou do not have access to that command.");
            return true;
        }

        String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);
        if(commandArgs.length < subCommand.getMinArgs()) {
            commandManager.getPlatform().sendMessage(sender, Lang.INVALID_USAGE.getMessage("tebex", subCommand.getName() + " " + subCommand.getUsage()));
            return true;
        }

        subCommand.execute(sender, commandArgs);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 1) {
            return commandManager.getCommands()
                    .keySet()
                    .stream()
                    .filter(s -> s.startsWith(args[0]))
                    .filter(s -> sender.hasPermission(commandManager.getCommands().get(s).getPermission()))
                    .collect(Collectors.toList());
        }

        return ImmutableList.of();
    }
}
