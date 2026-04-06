package noppes.npcs.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;

public class PlayerDataArgument implements ArgumentType<String> {

    public static PlayerData getData(CommandContext<CommandSourceStack> ctx, String arg) {
        return PlayerDataController.instance.getDataFromUsername(CustomNpcs.Server, ctx.getArgument(arg, String.class));
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        StringBuilder name = new StringBuilder();
        while(reader.canRead() && reader.peek() != ' ') {
            name.append(reader.read());
        }
        return name.toString();
    }

    public static SuggestionProvider<CommandSourceStack> getSuggests() {
        return (context, builder) -> {
            for (String name : PlayerDataController.instance.getPlayerNames()) { builder.suggest(name); }
            return builder.buildFuture();
        };
    }

    public static PlayerDataArgument dataArg() {
        return new PlayerDataArgument();
    }

}
