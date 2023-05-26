package dev.integra.timedrewards.rewards;

import dev.integra.timedrewards.TimedRewardsPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TimedRewards {

    public final ArrayList<TimedReward> timedRewards = new ArrayList<>();
    private List<String> joinMessages;
    private String guiTitle;

    public TimedRewards() {
        init();
    }

    public void init() {
        timedRewards.clear();

        FileConfiguration config = TimedRewardsPlugin.getInstance().getConfig();

        joinMessages = config.getStringList("join_message");
        guiTitle = config.getString("gui_title");

        List<Map<?, ?>> rewards = config.getMapList("rewards");
        for (Map<?, ?> reward : rewards) {
            timedRewards.add(new TimedReward((Integer) reward.get("id"), (String) reward.get("name"), (List<String>) reward.get("lore_not_taken"), (List<String>) reward.get("lore_taken"), (Integer) reward.get("cooldown"), (String) reward.get("response"), (String) reward.get("response_no_perm"), (List<String>) reward.get("commands"), (String) reward.get("permission")));
        }
    }

    public ArrayList<TimedReward> getRewards() {
        return timedRewards;
    }

    public String getGuiTitle() {
        return guiTitle;
    }

    public List<String> getJoinMessages() {
        return joinMessages;
    }

    public TimedReward get(int id) {
        for (TimedReward reward : timedRewards) {
            if (reward.getId() == id) {
                return reward;
            }
        }
        return null;
    }
}
