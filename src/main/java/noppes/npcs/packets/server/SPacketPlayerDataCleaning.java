package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.constants.EnumPlayerData;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.util.Util;

import java.io.File;
import java.util.Objects;

public class SPacketPlayerDataCleaning extends PacketServerBasic {

    protected static int channelId;
    private final long time;

    public SPacketPlayerDataCleaning(long timeIn) { time = timeIn; }

    @Override
    public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.GLOBAL_PLAYERDATA; }

    public static void encode(SPacketPlayerDataCleaning msg, FriendlyByteBuf buf) { buf.writeLong(msg.time); }

    public static SPacketPlayerDataCleaning decode(FriendlyByteBuf buf) { return new SPacketPlayerDataCleaning(buf.readLong()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        MinecraftServer server = player.getServer();
        if (server == null) { server = CustomNpcs.Server; }
        if (server != null) {
            File dirMod = CustomNpcs.getLevelSaveDirectory("playerdata");
            int i = 0;
            int s = Objects.requireNonNull(Objects.requireNonNull(dirMod).listFiles()).length;
            for (File dir : Objects.requireNonNull(dirMod.listFiles())) {
                if (dir.isDirectory()) {
                    for (File file : Objects.requireNonNull(dir.listFiles())) {
                        if (file.getName().endsWith(".json")) {
                            if (file.lastModified() > time) {
                                delete(dir);
                                i++;
                                ServerPlayer p = server.getPlayerList().getPlayerByName(file.getName().substring(0, file.getName().length() - 5));
                                if (p != null) {
                                    PlayerData.get(p).save(true);
                                    p.sendSystemMessage(Component.translatable("message.change.mod.data"));
                                }
                                break;
                            }
                            break;
                        }
                    }
                }
            }
            if (i > 0) { player.sendSystemMessage(Component.translatable("message.data.cleaning.true", "" + i, "" + s)); }
            else { player.sendSystemMessage(Component.translatable("message.data.cleaning.false", "" + s)); }
        }
        SPacketPlayerDataGet.sendPlayerData(EnumPlayerData.Players, player, player.getName().getString());
        CustomNpcs.debugData.end("Packets");
    }

    protected static void delete(File dir) {
        if (dir == null) { return; }
        if (dir.getName().equals("playerdata")) {
            Util.instance.removeFile(dir);
            Util.instance.removeFile(new File(dir.getParentFile().getParentFile(), "advancements"));
            Util.instance.removeFile(new File(dir.getParentFile().getParentFile(), "stats"));
        } // all
        else {
            Util.instance.removeFile(dir);
            Util.instance.removeFile(new File(dir.getParentFile().getParentFile().getParentFile(), "advancements/" + dir.getName() + ".json"));
            Util.instance.removeFile(new File(dir.getParentFile().getParentFile().getParentFile(), "stats/" + dir.getName() + ".json"));
        } // player
    }

}
