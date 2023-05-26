package dev.integra.timedrewards;

import com.google.common.collect.Lists;
import dev.integra.IntegraPlugin;
import dev.integra.api.IntegraAPI;
import dev.integra.api.data.IDataSource;
import dev.integra.command.IntegraCommand;
import dev.integra.data.file.FileDataSource;
import dev.integra.data.impl.UUIDQuery;
import dev.integra.data.serialization.DataRegistration;
import dev.integra.timedrewards.command.RewardsCommand;
import dev.integra.timedrewards.data.ClaimDataMap;
import dev.integra.timedrewards.data.PlayerRewardClaimData;
import dev.integra.timedrewards.data.serializer.ClaimDataMapSerializer;
import dev.integra.timedrewards.gui.RewardsGui;
import dev.integra.timedrewards.listener.TimedRewardListener;
import dev.integra.timedrewards.rewards.TimedRewards;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TimedRewardsPlugin extends IntegraPlugin {

    private static TimedRewardsPlugin instance;

    private final File rewardsFile = new File(getDataFolder(), "rewards.data");
    private FileDataSource<UUID> rewardsSource;
    private TimedRewards timedRewards;

    @Override
    protected @NotNull List<IDataSource> registerDataSources() {
        // only file data source for now
        // TODO: configurable JSON or YAML
        // TODO: add MySQL and other data sources
        return Lists.newArrayList(rewardsSource = new FileDataSource<>(rewardsFile, FileDataSource.FileType.JSON, UUID.class));
    }

    @Override
    protected int getDataSourceSaveInterval() {
        return 20 * 60 * 60; // config -> data-source-save-interval
    }

    @Override
    protected @NotNull List<Listener> registerEventListeners() {
        return Lists.newArrayList(new TimedRewardListener(), new RewardsGui.RewardGuiListener());
    }

    @Override
    protected @NotNull List<IntegraCommand> registerCommands() {
        return Lists.newArrayList(new RewardsCommand());
    }

    @Override
    protected void init() {
        DataRegistration.register(new ClaimDataMapSerializer());
    }

    @Override
    protected void enable() {
        instance = this;
        this.saveDefaultConfig();
        timedRewards = new TimedRewards();

        Bukkit.getScheduler().runTaskTimer(this, () -> RewardsGui.rewardsGuis.values().forEach(RewardsGui::updateInventory), 10L, 10L);
    }

    @Override
    protected void disable() {

    }

    public Optional<PlayerRewardClaimData> getRewardClaimData(UUID uuid) {
        Optional<PlayerRewardClaimData> optional = rewardsSource.queryTyped(new UUIDQuery(uuid));
        if (optional.isPresent()) {
            return optional;
        }
        PlayerRewardClaimData data = new PlayerRewardClaimData(new ClaimDataMap());
        rewardsSource.insertOrUpdate(new UUIDQuery(uuid), data);
        return Optional.of(data);
    }

    public FileDataSource<UUID> getRewardsSource() {
        return rewardsSource;
    }

    public TimedRewards getTimedRewards() {
        return timedRewards;
    }

    public static TimedRewardsPlugin getInstance() {
        return instance;
    }
}
