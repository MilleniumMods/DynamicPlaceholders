package io.github.joshy56.dynamicplaceholders.commands;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import io.github.joshy56.dynamicplaceholders.util.AdvancedBooleans;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 18/6/2022.
 */
public abstract class ExtendedCommand extends Command {
    private static final VarHandle handle;

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
        try {
            handle = MethodHandles
                    .privateLookupIn(Command.class, MethodHandles.lookup())
                    .findVarHandle(Command.class, "commandMap", CommandMap.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        COMMAND_ARGUMENT_PATTERN = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']+)*'");
    }

    protected ExtendedCommand(@NotNull String name) {
        super(name);
    }

    protected ExtendedCommand(@NotNull String name, @NotNull String description, @NotNull String usageMessage, @NotNull List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    @Nullable
    protected CommandMap getCommandMap() {
        return (CommandMap) handle.get(this);
    }

    @Override
    @Deprecated
    public boolean execute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (Strings.isNullOrEmpty(alias))
            return false;

        if (
                AdvancedBooleans.xor(
                        getAliases().stream().noneMatch(alias::equalsIgnoreCase),
                        !alias.equalsIgnoreCase(getLabel())
                )
        )
            return false;

        List<String> complexArgs = getArgumentsOf(String.join(" ", args));
        return execute(sender, complexArgs);
    }

    public abstract boolean execute(@NotNull CommandSender sender, @NotNull List<String> args);

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
