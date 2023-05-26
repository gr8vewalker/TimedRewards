package dev.integra.timedrewards.listener;

import dev.integra.data.impl.UUIDQuery;
import dev.integra.timedrewards.TimedRewardsPlugin;
import dev.integra.timedrewards.data.PlayerRewardClaimData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TimedRewardListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        TimedRewardsPlugin plugin = TimedRewardsPlugin.getInstance();
        int notClaimedRewardCount = plugin.getTimedRewards().getRewards().stream().map(reward -> plugin.getRewardsSource().<PlayerRewardClaimData>queryTyped(new UUIDQuery(player.getUniqueId())).map(r -> r.isClaimed(reward)).orElse(false)).mapToInt(b -> b ? 0 : 1).sum();
        if (notClaimedRewardCount > 0) {
            player.sendMessage(plugin.getTimedRewards().getJoinMessages().stream()
                                       .map(msg -> ChatColor.translateAlternateColorCodes('&', msg.replace("%reward_count%", String.valueOf(notClaimedRewardCount)))).toArray(String[]::new));
        }
    }

}
