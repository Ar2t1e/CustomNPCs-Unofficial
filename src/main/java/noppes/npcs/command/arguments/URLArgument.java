package noppes.npcs.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class URLArgument implements ArgumentType<String> {

    private static final SimpleCommandExceptionType HTTPS_EXCEPTION = new SimpleCommandExceptionType(Component.translatable("argument.no.https"));
    public static final String HTTPS_START = "https://";

    public static String getURL(CommandContext<CommandSourceStack> ctx, String arg) {
        return ctx.getArgument(arg, String.class);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        StringBuilder url = new StringBuilder();
        while(reader.canRead() && reader.peek() != ' ') {
            url.append(reader.read());
            String compareString = url.toString();
            if(!compareString.startsWith(compareString.length() < HTTPS_START.length() ? HTTPS_START.substring(0, compareString.length()) : HTTPS_START)) {
                throw HTTPS_EXCEPTION.createWithContext(reader);
            }
        }
        return url.toString();
    }

    public static SuggestionProvider<CommandSourceStack> getSuggests() {
        return (context, builder) -> {
            builder.suggest("https://");
            builder.suggest("https://github.com/BetaZavr/CustomNPCs_1.12.2-Unofficial/blob/master/hd%20skins/betazavr.png?raw=true");
            builder.suggest("https://i.imgur.com/mORJxcm.png");
            return builder.buildFuture();
        };
    }

    public static URLArgument urlArg() { return new URLArgument(); }

}
