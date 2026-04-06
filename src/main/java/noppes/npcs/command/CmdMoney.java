package noppes.npcs.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.command.arguments.PlayerDataArgument;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.util.ValueUtil;

public class CmdMoney {

    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("moneynpc")
                .requires(source -> true)
                .then(CmdMoney.registerGet())
                .then(CmdMoney.registerSet())
                .then(CmdMoney.registerAdd())
                .then(CmdMoney.registerPay());
    }

    public static LiteralArgumentBuilder<CommandSourceStack> registerGet() {
        return Commands.literal("get")
                .then(Commands.literal("money")
                        .executes((context) -> {
                            if (context.getSource().getPlayer() != null) {
                                PlayerData data = PlayerData.get(context.getSource().getPlayer());
                                context.getSource().sendSuccess(() -> Component.translatable("command.money.get.money",
                                        data.game.getMoney(), CustomNpcs.displayCurrencies), false);
                            }
                            return 1;
                        })
                        .then(Commands.argument("playername", PlayerDataArgument.dataArg())
                                .requires((source) -> source.hasPermission(2))
                                .suggests(PlayerDataArgument.getSuggests())
                                .executes((context) -> {
                                    if (context.getSource().getPlayer() == null ||
                                            !CustomNpcsPermissions.hasPermission(context.getSource().getPlayer(), CustomNpcsPermissions.MONEY_MANAGER)) { throw CmdPermissions.NO_PERMISSION.create(); }
                                    PlayerData data = PlayerDataArgument.getData(context, "playername");
                                    if (data == null) { throw EntityArgument.NO_PLAYERS_FOUND.create(); }
                                    if (context.getSource().getPlayer() != null &&
                                            context.getSource().getPlayer().getName().getString().equalsIgnoreCase(context.getArgument("playername", String.class))) {
                                        context.getSource().sendSuccess(() -> Component.translatable("command.money.get.money",
                                                data.game.getMoney(), CustomNpcs.displayCurrencies), false);
                                    } else {
                                        context.getSource().sendSuccess(() -> Component.translatable("command.money.get.money.player",
                                                context.getArgument("playername", String.class), data.game.getMoney(), CustomNpcs.displayCurrencies), false);
                                    }
                                    return 1;
                                }))
                )
                .then(Commands.literal("donat")
                        .executes((context) -> {
                            if (context.getSource().getPlayer() != null) {
                                PlayerData data = PlayerData.get(context.getSource().getPlayer());
                                context.getSource().sendSuccess(() -> Component.translatable("command.money.get.donat",
                                        data.game.getDonat(), CustomNpcs.displayDonation), false);
                            }
                            return 1;
                        })
                        .then(Commands.argument("playername", PlayerDataArgument.dataArg())
                                .requires((source) -> source.hasPermission(2))
                                .suggests(PlayerDataArgument.getSuggests())
                                .executes((context) -> {
                                    if (context.getSource().getPlayer() == null ||
                                            !CustomNpcsPermissions.hasPermission(context.getSource().getPlayer(), CustomNpcsPermissions.DONAT_MANAGER)) { throw CmdPermissions.NO_PERMISSION.create(); }
                                    PlayerData data = PlayerDataArgument.getData(context, "playername");
                                    if (data == null) { throw EntityArgument.NO_PLAYERS_FOUND.create(); }
                                    if (context.getSource().getPlayer() != null &&
                                            context.getSource().getPlayer().getName().getString().equalsIgnoreCase(context.getArgument("playername", String.class))) {
                                        context.getSource().sendSuccess(() -> Component.translatable("command.money.get.donat",
                                                data.game.getMoney(), CustomNpcs.displayCurrencies), false);
                                    } else {
                                        context.getSource().sendSuccess(() -> Component.translatable("command.money.get.donat.player",
                                                context.getArgument("playername", String.class), data.game.getMoney(), CustomNpcs.displayCurrencies), false);
                                    }
                                    return 1;
                                }))
                );
    }

    public static LiteralArgumentBuilder<CommandSourceStack> registerSet() {
        return Commands.literal("set")
                .then(Commands.literal("money")
                        .requires((source) -> source.hasPermission(2))
                        .then(Commands.argument("value", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                .suggests(getIntSuggests())
                                .executes((context) -> {
                                    if (context.getSource().getPlayer() == null ||
                                            !CustomNpcsPermissions.hasPermission(context.getSource().getPlayer(), CustomNpcsPermissions.MONEY_MANAGER)) { throw CmdPermissions.NO_PERMISSION.create(); }
                                    if (context.getSource().getPlayer() != null) {
                                        int value = IntegerArgumentType.getInteger(context, "value");
                                        PlayerData data = PlayerData.get(context.getSource().getPlayer());
                                        data.game.setMoney(value);
                                        context.getSource().sendSuccess(() -> Component.translatable("command.money.set.money",
                                                value, CustomNpcs.displayCurrencies), false);
                                    }
                                    return 1;
                                })
                                .then(Commands.argument("playername", PlayerDataArgument.dataArg())
                                        .suggests(PlayerDataArgument.getSuggests())
                                        .executes((context) -> {
                                            if (context.getSource().getPlayer() == null ||
                                                    !CustomNpcsPermissions.hasPermission(context.getSource().getPlayer(), CustomNpcsPermissions.MONEY_MANAGER)) { throw CmdPermissions.NO_PERMISSION.create(); }
                                            int value = IntegerArgumentType.getInteger(context, "value");
                                            PlayerData data = PlayerDataArgument.getData(context, "playername");
                                            if (data == null) { throw EntityArgument.NO_PLAYERS_FOUND.create(); }
                                            data.game.setMoney(value);
                                            String name = context.getArgument("playername", String.class);
                                            ServerPlayer player = context.getSource().getServer().getPlayerList().getPlayerByName(name);
                                            if (player != null) {
                                                if (player.equals(context.getSource().getPlayer())) {
                                                    context.getSource().sendSuccess(() -> Component.translatable("command.money.set.money", value, CustomNpcs.displayCurrencies), false);
                                                }
                                                else {
                                                    context.getSource().sendSuccess(() -> Component.translatable("command.money.set.money.player",
                                                            name, value, CustomNpcs.displayCurrencies), false);
                                                    MutableComponent message = Component.translatable("command.money.set.money", value, CustomNpcs.displayCurrencies);
                                                    if (context.getSource().getPlayer() != null) {
                                                        message.append(Component.literal(" <Admin: " + context.getSource().getPlayer().getName().getString() + ">").withStyle(ChatFormatting.DARK_GRAY));
                                                    }
                                                    player.sendSystemMessage(message, false);
                                                }
                                            } else {
                                                context.getSource().sendSuccess(() -> Component.translatable("command.money.set.money.player",
                                                        name, value, CustomNpcs.displayCurrencies), false);
                                            }
                                            if (data.player == null) { data.save(false); }
                                            return 1;
                                        })))
                )
                .then(Commands.literal("donat")
                        .requires((source) -> source.hasPermission(2))
                        .then(Commands.argument("value", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                .suggests(getIntSuggests())
                                .executes((context) -> {
                                    if (context.getSource().getPlayer() == null ||
                                            !CustomNpcsPermissions.hasPermission(context.getSource().getPlayer(), CustomNpcsPermissions.DONAT_MANAGER)) { throw CmdPermissions.NO_PERMISSION.create(); }
                                    if (context.getSource().getPlayer() != null) {
                                        int value = IntegerArgumentType.getInteger(context, "value");
                                        PlayerData data = PlayerData.get(context.getSource().getPlayer());
                                        data.game.setDonat(value);
                                        context.getSource().sendSuccess(() -> Component.translatable("command.money.set.donat",
                                                value, CustomNpcs.displayDonation), false);
                                    }
                                    return 1;
                                })
                                .then(Commands.argument("playername", PlayerDataArgument.dataArg())
                                        .suggests(PlayerDataArgument.getSuggests())
                                        .executes((context) -> {
                                            if (context.getSource().getPlayer() == null ||
                                                    !CustomNpcsPermissions.hasPermission(context.getSource().getPlayer(), CustomNpcsPermissions.DONAT_MANAGER)) { throw CmdPermissions.NO_PERMISSION.create(); }
                                            int value = IntegerArgumentType.getInteger(context, "value");
                                            PlayerData data = PlayerDataArgument.getData(context, "playername");
                                            if (data == null) { throw EntityArgument.NO_PLAYERS_FOUND.create(); }
                                            data.game.setDonat(value);
                                            String name = context.getArgument("playername", String.class);
                                            ServerPlayer player = context.getSource().getServer().getPlayerList().getPlayerByName(name);
                                            if (player != null) {
                                                if (player.equals(context.getSource().getPlayer())) {
                                                    context.getSource().sendSuccess(() -> Component.translatable("command.money.set.donat", value, CustomNpcs.displayDonation), false);
                                                }
                                                else {
                                                    context.getSource().sendSuccess(() -> Component.translatable("command.money.set.donat.player",
                                                            name, value, CustomNpcs.displayDonation), false);
                                                    MutableComponent message = Component.translatable("command.money.set.donat", value, CustomNpcs.displayDonation);
                                                    if (context.getSource().getPlayer() != null) {
                                                        message.append(Component.literal(" <Admin: " + context.getSource().getPlayer().getName().getString() + ">").withStyle(ChatFormatting.DARK_GRAY));
                                                    }
                                                    player.sendSystemMessage(message, false);
                                                }
                                            } else {
                                                context.getSource().sendSuccess(() -> Component.translatable("command.money.set.donat.player",
                                                        name, value, CustomNpcs.displayDonation), false);
                                            }
                                            if (data.player == null) { data.save(false); }
                                            return 1;
                                        })))
                );
    }

    public static LiteralArgumentBuilder<CommandSourceStack> registerAdd() {
        return Commands.literal("add")
                .then(Commands.literal("money")
                        .requires((source) -> source.hasPermission(2))
                        .then(Commands.argument("value", IntegerArgumentType.integer(Integer.MIN_VALUE, Integer.MAX_VALUE))
                                .suggests(getIntSuggests())
                                .executes((context) -> {
                                    if (context.getSource().getPlayer() == null ||
                                            !CustomNpcsPermissions.hasPermission(context.getSource().getPlayer(), CustomNpcsPermissions.MONEY_MANAGER)) { throw CmdPermissions.NO_PERMISSION.create(); }
                                    if (context.getSource().getPlayer() != null) {
                                        int value = IntegerArgumentType.getInteger(context, "value");
                                        PlayerData data = PlayerData.get(context.getSource().getPlayer());
                                        data.game.addMoney(value);
                                        context.getSource().sendSuccess(() -> Component.translatable("command.money.add.money",
                                                value, data.game.getMoney(), CustomNpcs.displayCurrencies), false);
                                    }
                                    return 1;
                                })
                                .then(Commands.argument("playername", PlayerDataArgument.dataArg())
                                        .suggests(PlayerDataArgument.getSuggests())
                                        .executes((context) -> {
                                            if (context.getSource().getPlayer() == null ||
                                                    !CustomNpcsPermissions.hasPermission(context.getSource().getPlayer(), CustomNpcsPermissions.MONEY_MANAGER)) { throw CmdPermissions.NO_PERMISSION.create(); }
                                            int value = IntegerArgumentType.getInteger(context, "value");
                                            PlayerData data = PlayerDataArgument.getData(context, "playername");
                                            if (data == null) { throw EntityArgument.NO_PLAYERS_FOUND.create(); }
                                            data.game.addMoney(value);
                                            String name = context.getArgument("playername", String.class);
                                            ServerPlayer player = context.getSource().getServer().getPlayerList().getPlayerByName(name);
                                            if (player != null) {
                                                if (player.equals(context.getSource().getPlayer())) {
                                                    context.getSource().sendSuccess(() -> Component.translatable("command.money.add.money", value, data.game.getMoney(), CustomNpcs.displayCurrencies), false);
                                                }
                                                else {
                                                    context.getSource().sendSuccess(() -> Component.translatable("command.money.add.money.player",
                                                            name, value, data.game.getMoney(), CustomNpcs.displayCurrencies), false);
                                                    MutableComponent message = Component.translatable("command.money.add.money", value, data.game.getMoney(), CustomNpcs.displayCurrencies);
                                                    if (context.getSource().getPlayer() != null) {
                                                        message.append(Component.literal(" <Admin: " + context.getSource().getPlayer().getName().getString() + ">").withStyle(ChatFormatting.DARK_GRAY));
                                                    }
                                                    player.sendSystemMessage(message, false);
                                                }
                                            } else {
                                                context.getSource().sendSuccess(() -> Component.translatable("command.money.add.money.player",
                                                        name, value, data.game.getMoney(), CustomNpcs.displayCurrencies), false);
                                            }
                                            if (data.player == null) { data.save(false); }
                                            return 1;
                                        })))
                )
                .then(Commands.literal("donat")
                        .requires((source) -> source.hasPermission(2))
                        .then(Commands.argument("value", IntegerArgumentType.integer(Integer.MIN_VALUE, Integer.MAX_VALUE))
                                .suggests(getIntSuggests())
                                .executes((context) -> {
                                    if (context.getSource().getPlayer() == null ||
                                            !CustomNpcsPermissions.hasPermission(context.getSource().getPlayer(), CustomNpcsPermissions.DONAT_MANAGER)) { throw CmdPermissions.NO_PERMISSION.create(); }
                                    if (context.getSource().getPlayer() != null) {
                                        int value = IntegerArgumentType.getInteger(context, "value");
                                        PlayerData data = PlayerData.get(context.getSource().getPlayer());
                                        data.game.addDonat(value);
                                        context.getSource().sendSuccess(() -> Component.translatable("command.money.add.donat",
                                                value, data.game.getDonat(), CustomNpcs.displayDonation), false);
                                    }
                                    return 1;
                                })
                                .then(Commands.argument("playername", PlayerDataArgument.dataArg())
                                        .suggests(PlayerDataArgument.getSuggests())
                                        .executes((context) -> {
                                            if (context.getSource().getPlayer() == null ||
                                                    !CustomNpcsPermissions.hasPermission(context.getSource().getPlayer(), CustomNpcsPermissions.DONAT_MANAGER)) { throw CmdPermissions.NO_PERMISSION.create(); }
                                            int value = IntegerArgumentType.getInteger(context, "value");
                                            PlayerData data = PlayerDataArgument.getData(context, "playername");
                                            if (data == null) { throw EntityArgument.NO_PLAYERS_FOUND.create(); }
                                            data.game.addDonat(value);
                                            String name = context.getArgument("playername", String.class);
                                            ServerPlayer player = context.getSource().getServer().getPlayerList().getPlayerByName(name);
                                            if (player != null) {
                                                if (player.equals(context.getSource().getPlayer())) {
                                                    context.getSource().sendSuccess(() -> Component.translatable("command.money.add.donat", value, data.game.getDonat(), CustomNpcs.displayDonation), false);
                                                }
                                                else {
                                                    context.getSource().sendSuccess(() -> Component.translatable("command.money.add.donat.player",
                                                            name, value, data.game.getDonat(), CustomNpcs.displayDonation), false);
                                                    MutableComponent message = Component.translatable("command.money.add.donat", value, data.game.getDonat(), CustomNpcs.displayDonation);
                                                    if (context.getSource().getPlayer() != null) {
                                                        message.append(Component.literal(" <Admin: " + context.getSource().getPlayer().getName().getString() + ">").withStyle(ChatFormatting.DARK_GRAY));
                                                    }
                                                    player.sendSystemMessage(message, false);
                                                }
                                            } else {
                                                context.getSource().sendSuccess(() -> Component.translatable("command.money.add.donat.player",
                                                        name, value, data.game.getDonat(), CustomNpcs.displayDonation), false);
                                            }
                                            if (data.player == null) { data.save(false); }
                                            return 1;
                                        })))
                );
    }

    public static LiteralArgumentBuilder<CommandSourceStack> registerPay() {
        return Commands.literal("pay")
                .requires(source -> true)
                .then(Commands.argument("playername", PlayerDataArgument.dataArg())
                        .suggests(PlayerDataArgument.getSuggests())
                        .then(Commands.argument("value", IntegerArgumentType.integer(1, Integer.MAX_VALUE))
                                .suggests(getIntSuggests())
                                .executes((context) -> {
                                    if (context.getSource().getPlayer() != null) {
                                        ServerPlayer senderPlayer = context.getSource().getPlayer();
                                        String name = context.getArgument("playername", String.class);
                                        PlayerData data = PlayerDataArgument.getData(context, "playername");
                                        if (data == null) { throw EntityArgument.NO_PLAYERS_FOUND.create(); }
                                        if (senderPlayer.getName().getString().equalsIgnoreCase(name)) { throw EntityArgument.ERROR_SELECTORS_NOT_ALLOWED.create(); }
                                        int value = IntegerArgumentType.getInteger(context, "value");
                                        if (!senderPlayer.isCreative()) {
                                            float commission = ValueUtil.correctFloat(CustomNpcs.CoinCommission, 0.0f, Integer.MAX_VALUE);
                                            int total = value + (int) Math.max(((float) value * commission / 100.0f), 1.0f);
                                            PlayerData senderData = PlayerData.get(senderPlayer);
                                            if (senderData.game.getMoney() < total) {
                                                throw new SimpleCommandExceptionType(Component.translatable("command.money.not.balance."+(commission > 0.0f ? "1" : "0"),
                                                                ChatFormatting.WHITE + "" + commission, ChatFormatting.WHITE + "" + total)
                                                        .append(" ")
                                                        .append(Component.translatable("command.money.get.money",
                                                                ChatFormatting.WHITE + "" + senderData.game.getMoney(), ChatFormatting.GRAY + CustomNpcs.displayCurrencies))).create();
                                            }
                                            senderData.game.addMoney(-total);
                                        }
                                        data.game.addMoney(value);
                                        if (data.player == null) { data.save(false); }
                                    }
                                    else { throw new SimpleCommandExceptionType(Component.literal("Command for players only")).create(); }
                                    return 1;
                                }))
                );
    }

    private static SuggestionProvider<CommandSourceStack> getIntSuggests() {
        return (context, builder) -> {
            builder.suggest(1);
            builder.suggest(5);
            builder.suggest(10);
            builder.suggest(25);
            builder.suggest(50);
            builder.suggest(100);
            builder.suggest(250);
            builder.suggest(500);
            builder.suggest(1000);
            builder.suggest(5000);
            builder.suggest(10000);
            builder.suggest(50000);
            builder.suggest(100000);
            builder.suggest(500000);
            builder.suggest(1000000);
            builder.suggest(5000000);
            return builder.buildFuture();
        };
    }

}
