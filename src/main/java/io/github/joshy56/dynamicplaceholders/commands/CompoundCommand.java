package io.github.joshy56.dynamicplaceholders.commands;

import co.aikar.timings.Timing;
import co.aikar.timings.TimingsManager;
import io.github.joshy56.dynamicplaceholders.util.Storage;
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
        getStoragePlugin().getSLF4JLogger().warn(
                String.format(
                        "exe Args: '%s'",
                        args
                )
        );
        if (args.isEmpty())
            return execute(sender);

        return dispatch(sender, args);
    }

    @Override
    protected @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull List<String> args, @Nullable Location location) {
        List<String> aliases = new ArrayList<>(mapTabComplete(sender, args, location));
        if (args.isEmpty())
            aliases.addAll(tabComplete(sender, location));
        return aliases;
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

        boolean registered = register(alias, finalFallbackPrefix, false, command);

        command.getAliases().removeIf(a -> !register(a, finalFallbackPrefix, true, command));

        if (!registered)
            command.setLabel(finalFallbackPrefix + ":" + alias);
        command.register(this);

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
        return dispatch(sender, getArgumentsOf(commandLine));
    }

    protected boolean dispatch(CommandSender sender, List<String> args) {
        getStoragePlugin().getSLF4JLogger().warn(
                String.format(
                        "Args: '%s'",
                        args
                        )
        );
        if (args.isEmpty())
            return false;

        Command target = getCommand(args.get(0));
        if (target == null)
            return false;

        if (target.timings == null)
            target.timings = TimingsManager.getCommandTiming(null, target);

        List<String> subArgs = args.subList(1, args.size());
        getStoragePlugin().getSLF4JLogger().warn(
                String.format(
                        "SubArgs: '%s'",
                        subArgs
                )
        );
        String[] arraySubArgs = subArgs.stream()
//                .filter(arg -> !arg.matches(COMMAND_ARGUMENT_PATTERN.pattern()))
                .map(
                        arg -> "\"" + arg + "\""
                )
                .collect(Collectors.joining(" "))
                .split(" ");
        getStoragePlugin().getSLF4JLogger().warn(
                String.format(
                        "ArraySubArgs: '%s'",
                        (Object[]) arraySubArgs
                )
        );
        try (Timing ignored = target.timings.startTiming()) {
            target.execute(
                    sender,
                    args.get(0),
                    arraySubArgs
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
        return mapTabComplete(sender, getArgumentsOf(commandLine), location);
    }

    /**
     * Command Map
     *
     * @param
     * @param
     * @param
     * @return
     */
    public @NotNull List<String> mapTabComplete(@NotNull CommandSender sender, @NotNull List<String> args, @Nullable Location location) {
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
                                    subArgs.stream()
                                            .filter(arg -> !arg.matches(COMMAND_ARGUMENT_PATTERN.pattern()))
                                            .map(
                                                    arg -> "\"" + arg + "\""
                                            )
                                            .collect(Collectors.joining(" "))
                                            .split(" "),
                                    location
                            );
                        }
                )
                .orElse(

                        getKnownCommands()
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
                                .collect(Collectors.toList())
                );
    }

    @Override
    public @NotNull Map<String, Command> getKnownCommands() {
        return commands;
    }

    @Override
    public void reloadStorage() {
        super.reloadStorage();
        getKnownCommands().values()
                .stream()
                .filter(command -> command instanceof TranslatableCommand)
                .map(command -> (TranslatableCommand) command)
                .forEach(TranslatableCommand::reloadStorage);
    }

}
