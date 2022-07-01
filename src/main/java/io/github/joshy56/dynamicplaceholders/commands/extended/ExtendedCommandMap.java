package io.github.joshy56.dynamicplaceholders.commands.extended;

import co.aikar.timings.Timing;
import co.aikar.timings.TimingsManager;
import com.google.common.collect.ImmutableList;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 28/6/2022.
 */
public class ExtendedCommandMap implements CommandMap {
    protected final Map<String, Command> commands;

    /**
     * Pattern that match on single word, double-quoted phrase, phrase single-quoted and word with apostrophe.
     * <ul>
     *     <li>Single word: word</li>
     *     <li>Phrase double-quoted: "another double-quoted phrase"</li>
     *     <li>Phrase single-quoted: 'another single-quoted phrase'</li>
     *     <li>Word with apostrophe: "yeah i'm a word with apostrophe"</li>
     * </ul>
     */
    protected static final Pattern COMMAND_ARGUMENT_PATTERN;

    static {
        COMMAND_ARGUMENT_PATTERN = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']+)*'");
    }

    public ExtendedCommandMap() {
        this.commands = new HashMap<>();
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

    private boolean dispatch(CommandSender sender, List<String> args) {
        if (args.isEmpty())
            return false;

        Command target = getCommand(args.get(0));
        if (target == null)
            return false;

        if (target.timings == null)
            target.timings = TimingsManager.getCommandTiming(null, target);

        List<String> subArgs = args.subList(1, args.size() - 1);
        try (Timing ignored = target.timings.startTiming()) {
            target.execute(
                    sender,
                    args.get(0),
                    subArgs.stream().collect(Collectors.joining(" ", "\"", "\"")).split(" ")
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
        return tabComplete(sender, commandLine, null);
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String commandLine, @Nullable Location location) throws IllegalArgumentException {
        return tabComplete(sender, getArgumentsOf(commandLine), location);
    }

    public @NotNull List<String> tabComplete(CommandSender sender, List<String> args, Location location) {
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
                            List<String> subArgs = args.subList(1, args.size() - 1);
                            return command.tabComplete(
                                    sender,
                                    args.get(0),
                                    subArgs.stream().collect(Collectors.joining(" ", "\"", "\"")).split(" "),
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

    protected List<String> getArgumentsOf(@NotNull final String commandLine) {
        ImmutableList.Builder<String> argsBuilder = ImmutableList.builder();
        Matcher matcher = COMMAND_ARGUMENT_PATTERN.matcher(commandLine);
        while (matcher.find()) {
            Bukkit.getConsoleSender()
                    .sendMessage("Group count=" + matcher.groupCount());
            if (matcher.group(1) != null) {
                // Add double-quoted string without the quotes
                argsBuilder.add(matcher.group(1));
            } else if (matcher.group(2) != null) {
                // Add single-quoted string without the quotes
                argsBuilder.add(matcher.group(2));
            } else {
                // Add unquoted word
                argsBuilder.add(matcher.group());
            }
        }
        return argsBuilder.build();
    }
}
