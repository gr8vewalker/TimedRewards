package dev.integra.timedrewards.gui;

import dev.integra.timedrewards.TimedRewardsPlugin;
import dev.integra.timedrewards.data.PlayerRewardClaimData;
import dev.integra.timedrewards.rewards.TimedReward;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class RewardsGui implements InventoryHolder {

    public static final HashMap<UUID, RewardsGui> rewardsGuis = new HashMap<>();

    private final Player owner;
    private final Inventory inventory;

    public RewardsGui(Player owner) {
        this.owner = owner;
        this.inventory = Bukkit.createInventory(this, ((int) Math.ceil(TimedRewardsPlugin.getInstance().getTimedRewards().getRewards().size() / 7f) + 2) * 9, ChatColor.translateAlternateColorCodes('&', TimedRewardsPlugin.getInstance().getTimedRewards().getGuiTitle()));
        rewardsGuis.put(owner.getUniqueId(), this);
        updateInventory();
        this.owner.openInventory(inventory);
    }

    public void updateInventory() {
        inventory.clear();

        // add items to 1-7 index every row
        int index = 11;
        for (TimedReward timedReward : TimedRewardsPlugin.getInstance().getTimedRewards().getRewards()) {
            if (index % 9 == 0) {
                index += 2;
            }
            inventory.setItem(index-1, timedReward.asItemStack(this.owner));
            index++;
        }

    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    public static class RewardGuiListener implements Listener {

        @EventHandler
        public void onClick(InventoryClickEvent event) {
            if (event.getInventory().getHolder() instanceof RewardsGui) {
                event.setCancelled(true);
                RewardsGui rewardsGui = (RewardsGui) event.getInventory().getHolder();
                ItemStack itemStack = event.getCurrentItem();
                if (itemStack == null) {
                    return;
                }
                if (itemStack.getType() != Material.AIR) {
                    ItemMeta meta = itemStack.getItemMeta();
                    if (meta == null) return;
                    int id = meta.getPersistentDataContainer().get(TimedReward.REWARD_ID_KEY, PersistentDataType.INTEGER);
                    TimedReward timedReward = TimedRewardsPlugin.getInstance().getTimedRewards().get(id);
                    if (timedReward == null) {
                        return;
                    }
                    if (!timedReward.hasPermission(rewardsGui.owner)) {
                        rewardsGui.owner.sendMessage(ChatColor.translateAlternateColorCodes('&', timedReward.getResponseNoPerm()));
                        rewardsGui.owner.closeInventory();
                        return;
                    }
                    Optional<PlayerRewardClaimData> optional = TimedRewardsPlugin.getInstance().getRewardClaimData(rewardsGui.owner.getUniqueId());
                    optional.ifPresent(playerRewards -> {
                        if (playerRewards.isClaimed(timedReward)) {
                            rewardsGui.owner.sendMessage("§cBu ödülü zaten aldın!");
                        } else {
                            playerRewards.claim(timedReward);
                            rewardsGui.owner.sendMessage(ChatColor.translateAlternateColorCodes('&', timedReward.getResponse()));
                            for (String command : timedReward.getCommands()) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", rewardsGui.owner.getName()));
                            }
                            rewardsGui.updateInventory();
                        }
                    });
                }
            }
        }

        @EventHandler
        public void onDrag(InventoryDragEvent event) {
            if (event.getInventory().getHolder() instanceof RewardsGui) {
                event.setCancelled(true);
            }
        }

        @EventHandler
        public void onClose(InventoryCloseEvent event) {
            if (event.getInventory().getHolder() instanceof RewardsGui) {
                rewardsGuis.remove(((RewardsGui) event.getInventory().getHolder()).owner.getUniqueId());
            }
        }
    }

}

