package io.github.fufuffu;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.command.UnknownCommandEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SaveFilePlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getComponentLogger().info(Component.text("Loaded save file plugin! :)"));

        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getCommandMap().register("/", new FileCommand(getComponentLogger(), Bukkit.getWorld("world")));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(Component.text("Hello " + event.getPlayer().getName() + ", this server has the save file plugin enabled!"));
    }

    @EventHandler(ignoreCancelled = true)
    public void onUnknownCommand(UnknownCommandEvent event) {
        String command = event.getCommandLine();

        event.message(Component.text("Uhhh, unknown command: " + command));
    }
}
