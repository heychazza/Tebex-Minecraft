package io.tebex.plugin.analytics.listener;

import io.tebex.plugin.TebexPlugin;
import io.tebex.sdk.analytics.obj.AnalysePlayer;
import io.tebex.sdk.exception.NotFoundException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.logging.Level;

public class QuitListener implements Listener {
    private final TebexPlugin platform;

    public QuitListener(TebexPlugin platform) {
        this.platform = platform;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        Player bukkitPlayer = event.getPlayer();
        AnalysePlayer player = platform.getAnalyticsManager().getPlayer(bukkitPlayer.getUniqueId());

        if(player == null) return;

        platform.debug("Preparing to track " + bukkitPlayer.getName() + "..");

        platform.getAnalyticsSDK().trackPlayerSession(player).thenAccept(successful -> {
            if(! successful) {
                platform.warning("Failed to track player session for " + player.getName() + ".");
                return;
            }

            platform.getAnalyticsManager().removePlayer(player.getUniqueId());
            platform.debug("Successfully tracked player session for " + player.getName() + ".");
        }).exceptionally(ex -> {
            Throwable cause = ex.getCause();
            platform.log(Level.WARNING, "Failed to track player session: " + cause.getMessage());

            if(cause instanceof NotFoundException) {
                platform.halt();
            } else {
                cause.printStackTrace();
            }

            return null;
        });
    }
}