package io.github.joshy56.dynamicplaceholders.commands.specific;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.github.joshy56.dynamicplaceholders.DynamicPlaceholders;
import io.github.joshy56.dynamicplaceholders.commands.TranslatableCommand;
import io.github.joshy56.dynamicplaceholders.commands.compounds.DynamicPlaceholdersCommand;
import io.github.joshy56.dynamicplaceholders.util.Storage;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 11/7/2022.
 */
public class Reload extends TranslatableCommand {

    public Reload(@NotNull Storage storage, @NotNull DynamicPlaceholders plugin) {
        super(
                "reload",
                "Recarga los archivos de configuracion",
                "/",
                Lists.newArrayList("recharge"),
                storage
        );
        setPermission("dph.reload");
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull List<String> args) {
        return execute(sender);
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender) {
        if(super.execute(sender))
            return true;

        ConfigurationSection messages = getMessagesSection();
        if(messages == null)
            return false;

        Command command = Bukkit.getCommandMap().getCommand("dynamicplaceholders:dynamicplaceholders");
        if(command == null)
            return false;
        if(!(command instanceof DynamicPlaceholdersCommand))
            return false;

        Plugin plugin = Bukkit.getPluginManager().getPlugin("DynamicPlaceholders");
        if(plugin == null)
            return false;
        if(!(plugin instanceof DynamicPlaceholders))
            return false;
        if(!plugin.isEnabled())
            return false;

        DynamicPlaceholders dynamicPlaceholders = (DynamicPlaceholders) plugin;
        ((DynamicPlaceholdersCommand) command).reloadStorage();
        dynamicPlaceholders.reloadConfig();
        dynamicPlaceholders.getCommandsMessages().reload();
        dynamicPlaceholders.getPlaceholderStorage().reload();

        String message = messages.getString("success");
        if(Strings.isNullOrEmpty(message)) {
            message = "%dynamicplaceholders:environmental_prefix% &a¡Todos los archivos recargados!";
            messages.set("success", message);
            getStorage().getStorage().set(messages.getName(), messages);
            saveStorage();
            getStoragePlugin().getSLF4JLogger().warn(String.format("Missing command message '%s' please configure it", messages.getCurrentPath() + ".success"));
        }

        sender.sendMessage(
                ChatColor.translateAlternateColorCodes(
                        '&',
                        PlaceholderAPI.setPlaceholders(null, message)
                )
        );
        return true;
    }

    @Override
    protected boolean help(@NotNull CommandSender sender) {
        sender.sendMessage(
                Component.text(
                                ChatColor.translateAlternateColorCodes(
                                        '&',
                                        PlaceholderAPI.setPlaceholders(
                                                ((sender instanceof Player) ? (Player) sender : null),
                                                "%dynamicplaceholders:environmental_prefix% &6- &eComandos"
                                        )
                                )
                        )
                        .append(Component.newline())
                        .append(Component.text(getName(), NamedTextColor.YELLOW))
                        .append(Component.text(" >> ", NamedTextColor.GOLD))
                        .append(Component.text(getDescription(), NamedTextColor.YELLOW))
        );
        return true;
    }

    @Override
    protected @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull List<String> args, @Nullable Location location) {
        return tabComplete(sender, location);
    }

    @Override
    protected @NotNull List<String> tabComplete(@NotNull CommandSender sender, @Nullable Location location) {
        return new ArrayList<>(0);
    }
}
