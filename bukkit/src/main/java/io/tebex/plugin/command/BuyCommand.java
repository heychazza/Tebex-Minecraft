package io.tebex.plugin.command;

import io.tebex.plugin.TebexPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuyCommand extends Command {
    private final TebexPlugin platform;

    public BuyCommand(String command, TebexPlugin platform) {
        super(command);
        this.platform = platform;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if(! platform.isSetup()) {
            platform.sendMessage(sender, "&cTebex is not setup yet!");
            return true;
        }

        platform.getBuyGUI().open((Player) sender);
        return true;
    }
}
