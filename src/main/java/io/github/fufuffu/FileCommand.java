package io.github.fufuffu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FileCommand extends Command {
    private final ComponentLogger logger;
    private final World world;
    private final int pillarsToReport = 10;
    private final int snakeSize = 32;

    private final HashMap<Byte, Material> blockTable = new HashMap<>();
    private final HashMap<Material, Byte> byteTable = new HashMap<>();

    protected FileCommand(ComponentLogger logger, World world) {
        super("file");
        this.logger = logger;
        this.world = world;

        Material[] allMaterials = Material.values();

        int currentIndex = 0;

        for (Material m : allMaterials) {
            if (currentIndex == 256) {
                break;
            }

            if (!nameValid(m) || m.hasGravity() || !m.isOccluding()) {
                continue;
            }

            blockTable.put((byte) currentIndex, m);
            currentIndex++;
        }

        if (!(blockTable.size() == 256)) {
            throw new RuntimeException("Only got " + blockTable.size() + " items");
        }

        for (Map.Entry<Byte, Material> e : blockTable.entrySet()) {
            byteTable.put(e.getValue(), e.getKey());
        }
    }

    private boolean nameValid(Material m) {
        String[] validNames = {"plank", "sand", "stone", "basalt", "ore", "log", "stem", "froglight",
                "purpur", "quartz", "wool", "terracotta", "concrete", "shulker", "pumpkin", "deepslate",
                "obsidian", "coral block", "netherrack", "debris", "hay", "bedrock", "honey", "block",
                "blue ice", "packed ice", "mud", "melon", "target", "barrel", "dispenser", "furnance",
                "dropper", "chiseled", "brick", "sea lantern", "jukebox", "wood", "andesite", "granite", "diorite",
                "calcite"};

        if (m.name().toLowerCase().contains("grass") || m.name().toLowerCase().contains("copper")
                || m.name().toLowerCase().contains("suspicious") || m.name().toLowerCase().contains("infested")
                || m.name().toLowerCase().contains("command") || m.name().toLowerCase().contains("structure") || m.name().toLowerCase().contains("crying")
                || m.name().toLowerCase().contains("slime")) {
            return false;
        }

        for (String validName : validNames) {
            if (m.name().toLowerCase().contains(validName)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        // We do not care about the sender, this could be done in server console.
        // Expect 1 args, the filename
        if (args.length != 2) {
            sender.sendMessage(Component.text("Args: save/read filename"));
            return false;
        }

        String action = args[0];
        String fileName = args[1];

        int bytesTreated = 0;
        int pillarsTreated = 0;

        int maxHeight = 200;
        int minHeight = 100;

        int x = 10;
        int z = 10;
        boolean goingX = true;

        try {
            if (action.equals("save")) {
                byte[] bytes = Files.readAllBytes(Paths.get(fileName));
                while (bytesTreated < bytes.length) {
                    for (int i = 0; i < maxHeight - minHeight; i++) {
                        if (bytesTreated == bytes.length) {
                            break;
                        }
                        world.getBlockAt(x, maxHeight - i, z).setType(blockTable.get(bytes[bytesTreated]));
                        bytesTreated++;
                    }
                    pillarsTreated++;

                    if (pillarsTreated % pillarsToReport == 0) {
                        sender.sendMessage(Component.text("Written " + bytesTreated + " bytes of: " + bytes.length));
                    }

                    if (goingX) {
                        x++;
                        if (x % snakeSize == 0) {
                            goingX = false;
                        }
                    } else {
                        z++;
                        if (z % snakeSize == 0) {
                            goingX = true;
                        }
                    }
                }
            } else if (action.equals("read")) {
                FileOutputStream file = new FileOutputStream(fileName, false);
                byte[] buf = new byte[maxHeight - minHeight];
                int elementsInBuff = 0;

                boolean shouldEnd = false;
                while (!shouldEnd) {
                    for (int i = 0; i < maxHeight - minHeight; i++) {
                        Material currBlock = world.getBlockAt(x, maxHeight - i, z).getType();
                        if (!byteTable.containsKey(currBlock)) {
                            shouldEnd = true;
                            break;
                        }

                        buf[i] = byteTable.get(currBlock);
                        elementsInBuff++;
                    }
                    file.write(buf, 0, elementsInBuff);
                    bytesTreated += elementsInBuff;
                    elementsInBuff = 0;
                    pillarsTreated++;

                    if (pillarsTreated % pillarsToReport == 0) {
                        sender.sendMessage(Component.text("Read " + bytesTreated + " bytes"));
                    }

                    if (goingX) {
                        x++;
                        if (x % snakeSize == 0) {
                            goingX = false;
                        }
                    } else {
                        z++;
                        if (z % snakeSize == 0) {
                            goingX = true;
                        }
                    }
                }

                file.close();
            } else {
                sender.sendMessage("Invalid action");
            }
        } catch (IOException e) {
            sender.sendMessage(Component.text("File not found or not readable"));
        }

        sender.sendMessage(Component.text("Finished executing savefile command, written " + bytesTreated));
        return true;
    }
}
