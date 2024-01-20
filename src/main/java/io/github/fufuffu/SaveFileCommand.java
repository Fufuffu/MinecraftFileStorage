package io.github.fufuffu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class SaveFileCommand extends Command {
    private final ComponentLogger logger;
    private final World world;

    protected SaveFileCommand(ComponentLogger logger, World world) {
        super("savefile");
        this.logger = logger;
        this.world = world;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        logger.info("Got savefile command with args: " + Arrays.toString(args));

        // We do not care about the sender, this could be done in server console.
        // Expect 1 args, the filename
        if (args.length != 1) {
            return false;
        }

        String fileName = args[0];

        world.getBlockAt(0, 100, 0).setType(Material.ANDESITE);
        world.getBlockAt(1, 100, 0).setType(Material.ACACIA_WOOD);
        world.getBlockAt(0, 100, 1).setType(Material.BAMBOO_BLOCK);
        world.getBlockAt(1, 100, 1).setType(Material.BIRCH_LOG);

        sender.sendMessage(Component.text("Finished executing savefile command"));
        return true;
    }
}
