package io.github.joshy56.dynamicplaceholders.commands;

import co.aikar.timings.Timing;
import co.aikar.timings.TimingsManager;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import io.github.joshy56.dynamicplaceholders.util.AdvancedBooleans;
import io.github.joshy56.dynamicplaceholders.util.Storage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 16/6/2022.
 */
public abstract class CompoundCommand extends TranslatableCommand implements CommandMap {
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

    protected CompoundCommand(@NotNull String name, @NotNull String description, @NotNull String usageMessage, @NotNull List<String> aliases, @NotNull final Storage storage) {
        super(name, description, usageMessage, aliases, storage);
        commands = new HashMap<>();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        Bukkit.getConsoleSender()
                .sendMessage("DPH:Debug CompoundCommand@execute(CommandSender=" + ((sender instanceof Player) ? "Player" : "Other") + " Alias=" + alias + " Args=" + Arrays.toString(args) + ")");
        if (Strings.isNullOrEmpty(alias))
            return false;

        if (
                AdvancedBooleans.xor(
                        getAliases().stream().noneMatch(alias::equalsIgnoreCase),
                        !alias.equalsIgnoreCase(getLabel())
                )
        )
            return false;

        String cmdLine = String.join(" ", args);
        List<String> complexArgs = getArgumentsOf(cmdLine);
        Bukkit.getConsoleSender()
                .sendMessage("Processed args=" + Arrays.toString(complexArgs.toArray(new String[0])));
        if (complexArgs.isEmpty())
            return execute(sender);

        return dispatch(sender, cmdLine);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull List<String> args) {
        return dispatch(sender, String.join(" ", args));
    }

    abstract protected boolean execute(@NotNull CommandSender sender);

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

        if(
                !alias.equalsIgnoreCase(getLabel()) ||
                        getAliases().stream().noneMatch(alias::equalsIgnoreCase)
        )
            return new ArrayList<>();

        if(args.isEmpty())
            return tabComplete(sender, location);

        return Optional.ofNullable(tabComplete(sender, args, location))
                .orElse(new ArrayList<>(0));
    }

    abstract protected @NotNull List<String> tabComplete(@NotNull CommandSender sender, @Nullable Location location);


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

    protected boolean dispatch(CommandSender sender, List<String> args) {
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
