package io.tebex.plugin.analytics.command.sub;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.tebex.plugin.TebexPlugin;
import io.tebex.plugin.obj.SubCommand;
import io.tebex.sdk.analytics.obj.AnalysePlayer;
import io.tebex.sdk.analytics.obj.Event;
import io.tebex.sdk.analytics.obj.PlayerEvent;
import io.tebex.sdk.platform.PlatformLang;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.stream.Stream;

public class TrackCommand extends SubCommand {
    public TrackCommand(TebexPlugin platform) {
        super(platform, "track", "analytics.track");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player bukkitPlayer = Bukkit.getServer().getPlayer(args[0]);
        if (bukkitPlayer == null) {
            getPlatform().sendMessage(sender, PlatformLang.PLAYER_NOT_FOUND.get());
            return;
        }

        String[] namespace = args[1].split(":", 2);
        String origin = namespace[0];
        String eventName = namespace[1];

        String jsonMetadata = String.join(" ", Stream.of(args).skip(2).toArray(String[]::new));

        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> fields = gson.fromJson(jsonMetadata, type);

        Event event = new Event(eventName, origin);
        event.setPlayer(bukkitPlayer.getUniqueId());
        for(Map.Entry<String, Object> entry : fields.entrySet()) {
            event.withMetadata(entry.getKey(), entry.getValue());
        }

        getPlatform().getAnalyticsManager().addEvent(event);
        getPlatform().sendMessage(sender, PlatformLang.EVENT_TRACKED.get());
    }

    @Override
    public String getUsage() {
        return "<player> <event> <metadata>";
    }

    @Override
    public String getDescription() {
        return "Track an event for a player.";
    }

    @Override
    public int getMinArgs() {
        return 3;
    }
}
