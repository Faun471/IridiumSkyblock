package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.api.IridiumSkyblockAPI;
import com.iridium.iridiumskyblock.database.Island;
import com.iridium.iridiumskyblock.database.IslandWarp;
import com.iridium.iridiumskyblock.database.User;
import com.iridium.iridiumskyblock.utils.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SetWarpCommand extends Command {

    /**
     * The default constructor.
     */
    public SetWarpCommand() {
        super(Arrays.asList("setwarp", "createwarp"), "Creates an Island warp", "", true);
    }

    /**
     * Executes the command for the specified {@link CommandSender} with the provided arguments.
     * Not called when the command execution was invalid (no permission, no player or command disabled).
     * Shows an overview over the members of the Island and allows quick rank management.
     *
     * @param sender The CommandSender which executes this command
     * @param args   The arguments used with this command. They contain the sub-command
     */
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("/is setwarp <name>");
        }
        Player player = (Player) sender;
        User user = IridiumSkyblockAPI.getInstance().getUser(player);
        Optional<Island> island = user.getIsland();

        if (island.isPresent()) {
            List<IslandWarp> islandWarps = IridiumSkyblock.getInstance().getDatabaseManager().getIslandWarpTableManager().getEntries(island.get());
            if (islandWarps.stream().anyMatch(islandWarp -> islandWarp.getName().equalsIgnoreCase(args[1]))) {
                player.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().warpAlreadyExists.replace("%prefix%",
                        IridiumSkyblock.getInstance().getConfiguration().prefix)));
            } else {
                if (islandWarps.size() < IridiumSkyblock.getInstance().getConfiguration().islandWarpSlots.size()) {
                    IslandWarp islandWarp = new IslandWarp(island.get(), player.getLocation(), args[1]);
                    if (args.length == 3) {
                        islandWarp.setPassword(args[2]);
                    }
                    IridiumSkyblock.getInstance().getDatabaseManager().getIslandWarpTableManager().addEntry(islandWarp);
                    player.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().createdWarp
                            .replace("%name%", args[1])
                            .replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix))
                    );
                } else {
                    player.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().warpLimitReached.replace("%prefix%",
                            IridiumSkyblock.getInstance().getConfiguration().prefix)));
                }
            }
        } else {
            player.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().dontHaveIsland.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
        }

    }

    /**
     * Handles tab-completion for this command.
     *
     * @param commandSender The CommandSender which tries to tab-complete
     * @param command       The command
     * @param label         The label of the command
     * @param args          The arguments already provided by the sender
     * @return The list of tab completions for this command
     */
    @Override
    public List<String> onTabComplete(CommandSender commandSender, org.bukkit.command.Command command, String label, String[] args) {
        return Collections.emptyList();
    }

}