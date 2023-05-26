package dev.integra.timedrewards.data;

import dev.integra.api.data.IData;
import dev.integra.timedrewards.TimedRewardsPlugin;
import dev.integra.timedrewards.rewards.TimedReward;

public class PlayerRewardClaimData implements IData {

    private final ClaimDataMap claims;

    public PlayerRewardClaimData(ClaimDataMap claims) {
        this.claims = claims;
    }

    @Override
    public Class<? extends ClaimDataMap> getType() {
        return ClaimDataMap.class;
    }

    @Override
    public Object getData() {
        return claims;
    }

    public boolean isClaimed(TimedReward reward) {
        return claims.containsKey(reward.getId()) && claims.get(reward.getId()) + reward.getSeconds() * 1000L > System.currentTimeMillis();
    }

    public void claim(TimedReward reward) {
        claims.put(reward.getId(), System.currentTimeMillis());
    }

    public String remainingTimeFormatted(TimedReward reward) {
        if (!isClaimed(reward)) {
            return "0g 0sa 0dk 0sn";
        }
        long remaining = claims.get(reward.getId()) + reward.getSeconds() * 1000L - System.currentTimeMillis();
        long seconds = remaining / 1000L;
        long minutes = seconds / 60L;
        long hours = minutes / 60L;
        long days = hours / 24L;
        return String.format(TimedRewardsPlugin.getInstance().getConfig().getString("time_format", "%dg %dsa %ddk %dsn"), days, hours % 24L, minutes % 60L, seconds % 60L);
    }

}
