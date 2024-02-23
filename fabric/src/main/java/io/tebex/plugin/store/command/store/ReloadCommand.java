package io.tebex.plugin.store.command.store;

import com.mojang.brigadier.context.CommandContext;
import dev.dejvokep.boostedyaml.YamlDocument;
import io.tebex.plugin.TebexPlugin;
import io.tebex.plugin.obj.SubCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

import java.io.IOException;

public class ReloadCommand extends SubCommand {
    public ReloadCommand(TebexPlugin platform) {
        super(platform, "reload", "tebex.reload");
    }

    @Override
    public void execute(CommandContext<ServerCommandSource> context) {
        final ServerCommandSource source = context.getSource();

        TebexPlugin platform = getPlatform();
        try {
            YamlDocument configYaml = platform.initPlatformConfig();
            platform.loadServerPlatformConfig(configYaml);
            platform.refreshListings();
//            platform.setBuyGUI(new BuyGUI(platform));

            source.sendFeedback(new LiteralText("§8[Tebex] §7Successfully reloaded."), false);
        } catch (IOException e) {
            source.sendFeedback(new LiteralText("§8[Tebex] §cFailed to reload the plugin: Check Console."), false);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getDescription() {
        return "Reloads the plugin.";
    }
}
