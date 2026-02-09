package filter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.AntiCaps;

public class PipelineApplier implements Listener {
    private final AntiCaps plugin;
    private final MessagePipeline pipeline;
    private static final String BYPASS_PERMISSION = "anticaps.bypass";

    public PipelineApplier(AntiCaps plugin, MessagePipeline pipeline) {
        this.plugin = plugin;
        this.pipeline = pipeline;
    }

    @EventHandler
    public void onAsyncChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        boolean debug = plugin.getConfig().getBoolean("debug", false);
        boolean bypass = bypasses(event.getPlayer());

        if (bypass) {
            if (debug) {
                plugin.getLogger().info(String.format(
                        "Chat from %s: '%s' | bypass=true | filterApplied=false | output='%s'",
                        event.getPlayer().getName(), message, message));
            }
            return;
        }

        String filtered = pipeline.filter(message);
        boolean applied = !filtered.equals(message);
        if (applied) {
            boolean silent = plugin.getConfig().getBoolean("silent", false);
            if (!silent) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    plugin.sendMessage("filter-applied", event.getPlayer());
                }, 1);
            }
        }

        if (debug) {
            plugin.getLogger().info(String.format(
                    "Chat from %s: '%s' | bypass=false | filterApplied=%s | output='%s'",
                    event.getPlayer().getName(), message, Boolean.toString(applied), filtered));
        }

        event.setMessage(filtered);

    }

    private boolean bypasses(Player player) {
        return player.hasPermission(BYPASS_PERMISSION);
    }

}
