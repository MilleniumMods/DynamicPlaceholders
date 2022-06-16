package io.github.joshy56.dynamicplaceholders.commands;

import co.aikar.timings.Timing;
import co.aikar.timings.TimingsManager;
import com.destroystokyo.paper.event.server.ServerExceptionEvent;
import com.destroystokyo.paper.exception.ServerCommandException;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 16/6/2022.
 */
public abstract class CompoundCommand extends Command implements CommandMap {
    private final Map<Set<String>, Command> knownCommands;

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
        COMMAND_ARGUMENT_PATTERN = Pattern.compile("[^\\s\"']+|\"[^\"]*\"|'[^']*'");
    }

    protected CompoundCommand(@NotNull String name, @NotNull String description, @NotNull String usageMessage, @NotNull List<String> aliases) {
        super(name, description, usageMessage, aliases);
        knownCommands = new HashMap<>();
        BiMap<Command, Collection<String>> biMap = HashBiMap.create();
        biMap.inverse();
        Map<Set<String>, Command> map = new HashMap<>(){
            @Override
            public boolean containsKey(Object key) {
                return super.containsKey(key);
            }
        };
        map.keySet().stream().reduce(
                (left, right) -> {
                    left.addAll(right);
                    return left;
                }
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerAll(@NotNull String fallbackPrefix, @NotNull List<Command> commands) {
        if (commands != null)
            for (Command c : commands) {
                register(fallbackPrefix, c);
            }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean register(@NotNull String fallbackPrefix, @NotNull Command command) {
        return register(command.getName(), fallbackPrefix, command);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean register(@NotNull String label, @NotNull String fallbackPrefix, @NotNull Command command) {
        command.timings = co.aikar.timings.TimingsManager.getCommandTiming(fallbackPrefix, command); // Paper
        label = label.toLowerCase(java.util.Locale.ENGLISH).trim();
        fallbackPrefix = fallbackPrefix.toLowerCase(java.util.Locale.ENGLISH).trim();
        boolean registered = register(label, command, false, fallbackPrefix);

        Iterator<String> iterator = command.getAliases().iterator();
        while (iterator.hasNext()) {
            if (!register(iterator.next(), command, true, fallbackPrefix)) {
                iterator.remove();
            }
        }

        // If we failed to register under the real name, we need to set the command label to the direct address
        if (!registered) {
            command.setLabel(fallbackPrefix + ":" + label);
        }

        // Register to us so further updates of the commands label and aliases are postponed until its re-registered
        command.register(this);

        return registered;
    }

    /**
     * Registers a command with the given name is possible. Also uses
     * fallbackPrefix to create a unique name.
     *
     * @param label the name of the command, without the '/'-prefix.
     * @param command the command to register
     * @param isAlias whether the command is an alias
     * @param fallbackPrefix a prefix which is prepended to the command for a
     *     unique address
     * @return true if command was registered, false otherwise.
     */
    private synchronized boolean register(@NotNull String label, @NotNull Command command, boolean isAlias, @NotNull String fallbackPrefix) {
        knownCommands.put(fallbackPrefix + ":" + label, command);
        if ((command instanceof BukkitCommand || isAlias) && knownCommands.containsKey(label)) {
            // Request is for an alias/fallback command and it conflicts with
            // a existing command or previous alias ignore it
            // Note: This will mean it gets removed from the commands list of active aliases
            return false;
        }

        boolean registered = true;

        // If the command exists but is an alias we overwrite it, otherwise we return
        Command conflict = knownCommands.get(label);
        if (conflict != null && conflict.getLabel().equals(label)) {
            return false;
        }

        if (!isAlias) {
            command.setLabel(label);
        }
        knownCommands.put(label, command);

        return registered;
    }

    @Override
    public boolean dispatch(@NotNull CommandSender sender, @NotNull String cmdLine) throws CommandException {
        List<String> args = getArgumentsOf(cmdLine);
        if(args.isEmpty())
            return false;

        String label = args.get(0).toLowerCase();
        Command target = (label.equalsIgnoreCase(getLabel())) ? this : getCommand(label);
        if(target == null)
            return false;

        // Paper start - Plugins do weird things to workaround normal registration
        if (target.timings == null) {
            target.timings = co.aikar.timings.TimingsManager.getCommandTiming(null, target);
        }
        // Paper end

        try {
            try (Timing ignored = target.timings.startTiming()) { // Paper - use try with resources
                // Note: we don't return the result of target.execute as that's success / failure, we return handled (true) or not handled (false)
                target.execute(sender, label, args.subList(1, args.size()).toArray(new String[0]));
            } // target.timings.stopTiming(); // Spigot // Paper
        } catch (CommandException ex) {
            new ServerExceptionEvent(new ServerCommandException(ex, target, sender, args.toArray(new String[0]))).callEvent(); // Paper
            //target.timings.stopTiming(); // Spigot // Paper
            throw ex;
        } catch (Throwable ex) {
            //target.timings.stopTiming(); // Spigot // Paper
            String msg = "Unhandled exception executing '" + cmdLine + "' in " + target;
            new ServerExceptionEvent(new ServerCommandException(ex, target, sender, args.toArray(new String[0]))).callEvent(); // Paper
            throw new CommandException(msg, ex);
        }

        // return true as command was handled
        return true;
    }

    @Override
    public void clearCommands() {

    }

    @Override
    public @Nullable Command getCommand(@NotNull String name) {
        getKnownCommands().keySet()
                .stream()
                .reduce(
                        new HashSet<String>(),
                        (left, right) -> {
                            left.
                        }
                );
        return null;
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String cmdLine) throws IllegalArgumentException {
        return null;
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String cmdLine, @Nullable Location location) throws IllegalArgumentException {
        return null;
    }

    @Deprecated
    @Override
    public @NotNull Map<String, Command> getKnownCommands() {
        return knownCommands
                .entrySet()
                .stream()
                .map(
                        entry -> entry
                                .getKey()
                                .stream()
                                .map(
                                        key -> Map.entry(key, entry.getValue())
                                )
                                .collect(
                                        Collectors.toMap(
                                                key -> key,
                                                value -> value
                                        )
                                )
                );
    }

    protected List<String> getArgumentsOf(@NotNull final String commandLine){
        ImmutableList.Builder<String> argsBuilder = ImmutableList.builder();
        Matcher matcher = COMMAND_ARGUMENT_PATTERN.matcher(commandLine);
        while (matcher.find()) {
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
