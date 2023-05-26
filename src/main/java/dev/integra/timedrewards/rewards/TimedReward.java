package dev.integra.timedrewards.rewards;

import dev.integra.api.data.IQuery;
import dev.integra.timedrewards.TimedRewardsPlugin;
import dev.integra.timedrewards.data.PlayerRewardClaimData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TimedReward {

    public static final NamespacedKey REWARD_ID_KEY = new NamespacedKey(TimedRewardsPlugin.getInstance(), "reward-id");

    private final int id;
    private final String name;
    private final List<String> loreRewardIsNotTaken;
    private final List<String> loreRewardIsTaken;
    private final int seconds;
    private final String response;
    private final String responseNoPermission;
    private final List<String> commands;
    private final String permission;

    public TimedReward(int id, String name, List<String> loreRewardIsNotTaken, List<String> loreRewardIsTaken, int seconds, String response, String responseNoPermission, List<String> commands, String permission) {
        this.id = id;
        this.name = name;
        this.loreRewardIsNotTaken = loreRewardIsNotTaken;
        this.loreRewardIsTaken = loreRewardIsTaken;
        this.seconds = seconds;
        this.response = response;
        this.responseNoPermission = responseNoPermission;
        this.commands = commands;
        this.permission = permission;
    }

    public List<String> getCommands() {
        return commands;
    }

    public String getResponse() {
        return response;
    }

    public String getResponseNoPerm() {
        return responseNoPermission;
    }

    public int getSeconds() {
        return seconds;
    }

    public List<String> getNotTakenLore() {
        return loreRewardIsNotTaken;
    }

    public List<String> getTakenLore() {
        return loreRewardIsTaken;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public ItemStack asItemStack(Player player) {
        Optional<PlayerRewardClaimData> data = TimedRewardsPlugin.getInstance().getRewardClaimData(player.getUniqueId());
        boolean taken = data.map(tr -> tr.isClaimed(this)).orElse(false);

        ItemStack stack = new ItemStack(taken ? Material.RED_TERRACOTTA : Material.LIME_TERRACOTTA);
        ItemMeta meta = stack.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.name));

        List<String> lore = (taken ? loreRewardIsTaken : loreRewardIsNotTaken).stream()
                .map(s -> data
                        .map(playerRewardClaimData -> s.replaceAll("%remaining_time%", playerRewardClaimData.remainingTimeFormatted(this)))
                        .orElse(s))
                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                .collect(Collectors.toList());
        meta.setLore(lore);

        meta.getPersistentDataContainer().set(REWARD_ID_KEY, PersistentDataType.INTEGER, id);

        stack.setItemMeta(meta);
        return stack;
    }

    public boolean hasPermission(Player player) {
        return permission == null || player.hasPermission(permission);
    }
}
