package dev.integra.timedrewards.command;

import dev.integra.api.command.CommandInfo;
import dev.integra.api.command.CommandResult;
import dev.integra.command.IntegraCommand;
import dev.integra.command.IntegraCommandExecutor;
import dev.integra.timedrewards.gui.RewardsGui;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class RewardsCommand extends IntegraCommand {
    public RewardsCommand() {
        super("rewards");
    }

    @Override
    protected String getMessage(CommandResult commandResult, IntegraCommandExecutor integraCommandExecutor, String[] strings) {
        switch (commandResult) {
            case ERROR:
                return "§cAn error occurred while executing the command.";
            case INVALID_SENDER:
                return "§cOnly players can use this command.";
            case MISSING_ARGUMENTS:
                return "§cMissing arguments.";
            case NO_PERMISSION:
                return "§cYou don't have permission to use this command.";
            case NOT_FOUND:
                return "§cSubcommand not found.";
            case SUCCESS:
            default:
                return "";
        }
    }

    @CommandInfo(
            name = "",
            description = "Opens the rewards GUI",
            usage = "/rewards",
            playerOnly = true
    )
    public void defaultCommand(CommandSender sender, Map<String, String> args) {
        new RewardsGui((Player) sender);
    }
}
