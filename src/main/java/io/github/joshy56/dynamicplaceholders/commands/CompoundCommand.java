package io.github.joshy56.dynamicplaceholders.commands;

import co.aikar.timings.Timing;
import co.aikar.timings.TimingsManager;
import com.google.common.base.Strings;
import io.github.joshy56.dynamicplaceholders.util.Storage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 16/6/2022.
 */
public abstract class CompoundCommand extends TranslatableCommand implements CommandMap {
    protected final Map<String, Command> commands;

    protected CompoundCommand(@NotNull String name, @NotNull String description, @NotNull String usageMessage, @NotNull List<String> aliases, @NotNull final Storage storage) {
        super(name, description, usageMessage, aliases, storage);
        commands = new HashMap<>();
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull List<String> args) {

        Bukkit.getConsoleSender().sendMessage(
                new String[]{
                        "DynamicPlaceholders:Debug of:",
                        "Class '" + getClass().getSimpleName() + "' on ",
                        "Method: 'execute' with 2 params and his values ",
                        "Param: 'sender' = " + sender.getClass().getSimpleName(),
                        "Param: 'args' = " + Arrays.toString(args.toArray())
                }
        );

        if(args.isEmpty())
            return execute(sender);

        return dispatch(sender, args);
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        return tabComplete(sender, alias, args, null);
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args, @Nullable Location location) throws IllegalArgumentException {
        return tabComplete(
                sender,
                alias,
                getArgumentsOf(String.join(" ", args)),
                location
        );
    }

    protected @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull List<String> args, @Nullable Location location) {
        if(Strings.isNullOrEmpty(alias))
            return new ArrayList<>(0);

        Bukkit.getConsoleSender().sendMessage(
                new String[]{
                        "DynamicPlaceholders:Debug of:",
                        "Class '" + getClass().getSimpleName() + "' on ",
                        "Method: 'tabComplete' with 4 params and his values ",
                        "Param: 'sender' = " + sender.getClass().getSimpleName(),
                        "Param: 'alias' = " + alias,
                        "Param: 'args' = " + Arrays.toString(args.toArray()),
                        "Param: 'location' = " + (location == null ? "undefined" : location.toString())
                }
        );
        /**
        if(args.isEmpty())
            return tabComplete(sender, location);

        if(
                !alias.equalsIgnoreCase(getLabel()) &&
                        getAliases().stream().noneMatch(alias::equalsIgnoreCase)
        )
            return new ArrayList<>();

        return Optional.ofNullable(tabComplete(sender, args, location))
                .orElse(new ArrayList<>(0));
         */
        return new ArrayList<>(0);
    }

    @Override
    public void registerAll(@NotNull String fallbackPrefix, @NotNull List<Command> commands) {
        if (commands == null || commands.isEmpty())
            return;

        commands.forEach(command -> register(fallbackPrefix, command));
    }

    @Override
    public boolean register(@NotNull String alias, @NotNull String fallbackPrefix, @NotNull Command command) {
        String finalFallbackPrefix = fallbackPrefix.toLowerCase().trim();
        command.timings = TimingsManager.getCommandTiming(finalFallbackPrefix, command);
        alias = alias.toLowerCase().trim();

        Bukkit.getConsoleSender().sendMessage(
                new String[]{
                        "DynamicPlaceholders:Debug of:",
                        "Class '" + getClass().getSimpleName() + "' on ",
                        "Method: 'register' with 3 params and his values ",
                        "Param: 'fallbackPrefix' = " + finalFallbackPrefix,
                        "Param: 'alias' = " + alias,
                        "Param: 'command' = " + command,
                        "Value of 'aliases' on Param 'command' = " + Arrays.toString(command.getAliases().toArray())
                }
        );

        boolean registered = register(alias, finalFallbackPrefix, false, command);

        command.getAliases().removeIf(a -> !register(a, finalFallbackPrefix, true, command));

        Bukkit.getConsoleSender().sendMessage(
                new String[]{
                        "DynamicPlaceholders:Debug of:",
                        "Class '" + getClass().getSimpleName() + "' on ",
                        "Method: 'register' with 3 params and his values ",
                        "Param: 'fallbackPrefix' = " + finalFallbackPrefix,
                        "Param: 'alias' = " + alias,
                        "Param: 'command' = " + command,
                        "Value of 'aliases' on Param 'command' = " + Arrays.toString(command.getAliases().toArray())
                }
        );

        if (!registered)
            command.setLabel(finalFallbackPrefix + ":" + alias);

        return registered;
    }

    protected boolean register(@NotNull String alias, @NotNull String fallbackPrefix, boolean isAlias, @NotNull Command command) {
        commands.put(fallbackPrefix + ":" + alias, command);

        if ((command instanceof BukkitCommand || isAlias) && commands.containsKey(alias))
            return false;

        Command conflict = commands.get(alias);
        if (conflict != null && conflict.getLabel().equalsIgnoreCase(alias))
            return false;

        if (!isAlias)
            command.setLabel(alias);

        commands.put(alias, command);

        return true;
    }

    @Override
    public boolean register(@NotNull String fallbackPrefix, @NotNull Command command) {
        return register(command.getName(), fallbackPrefix, command);
    }

    @Override
    public boolean dispatch(@NotNull CommandSender sender, @NotNull String commandLine) throws CommandException {

        Bukkit.getConsoleSender().sendMessage(
                new String[]{
                        "DynamicPlaceholders:Debug of:",
                        "Class '" + getClass().getSimpleName() + "' on ",
                        "Method: 'dispatch' with 2 params and his values ",
                        "Param: 'sender' = " + sender.getClass().getSimpleName(),
                        "Param: 'commandLine' = " + commandLine
                }
        );

        return dispatch(sender, getArgumentsOf(commandLine));
    }

    protected boolean dispatch(CommandSender sender, List<String> args) {

        Bukkit.getConsoleSender().sendMessage(
                new String[]{
                        "DynamicPlaceholders:Debug of:",
                        "Class '" + getClass().getSimpleName() + "' on ",
                        "Method: 'dispatch' with 2 params and his values ",
                        "Param: 'sender' = " + sender.getClass().getSimpleName(),
                        "Param: 'args' = " + Arrays.toString(args.toArray())
                }
        );

        if (args.isEmpty())
            return false;

        Command target = getCommand(args.get(0));
        if (target == null)
            return false;

        if (target.timings == null)
            target.timings = TimingsManager.getCommandTiming(null, target);

        List<String> subArgs = args.subList(1, args.size());
        try (Timing ignored = target.timings.startTiming()) {
            target.execute(
                    sender,
                    args.get(0),
                    String.join(" ", subArgs).split(" ")
            );
        }
        return true;
    }

    @Override
    public void clearCommands() {
        commands.forEach(
                (id, command) -> command.unregister(this)
        );
        commands.clear();
    }

    @Override
    public @Nullable Command getCommand(@NotNull String alias) {
        return getKnownCommands().get(alias.toLowerCase());
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String commandLine) throws IllegalArgumentException {
        return tabComplete(sender, commandLine, (Location) null);
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String commandLine, @Nullable Location location) throws IllegalArgumentException {
        return tabComplete(sender, getArgumentsOf(commandLine), location);
    }

    /**
     * Command Map
     * @param sender
     * @param args
     * @param location
     * @return
     */
    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull List<String> args, @Nullable Location location) {
        if (args.isEmpty())
            return getKnownCommands()
                    .entrySet()
                    .stream()
                    .filter(
                            entry -> entry.getValue().testPermissionSilent(sender)
                    )
                    .map(Map.Entry::getKey)
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .collect(Collectors.toList());

        if (args.size() == 1)
            return getKnownCommands()
                    .entrySet()
                    .stream()
                    .filter(
                            entry -> StringUtil.startsWithIgnoreCase(entry.getKey(), args.get(0))
                    )
                    .filter(
                            entry -> entry.getValue().testPermissionSilent(sender)
                    )
                    .map(Map.Entry::getKey)
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .collect(Collectors.toList());

        return Optional.ofNullable(getKnownCommands().get(args.get(0)))
                .filter(
                        command -> command.testPermissionSilent(sender)
                )
                .map(
                        command -> {
                            List<String> subArgs = args.subList(1, args.size());
                            return command.tabComplete(
                                    sender,
                                    args.get(0),
                                    String.join(" ", subArgs).split(" "),
                                    location
                            );
                        }
                )
                .orElse(new ArrayList<>(0));
    }

    @Override
    public @NotNull Map<String, Command> getKnownCommands() {
        return commands;
    }

}
