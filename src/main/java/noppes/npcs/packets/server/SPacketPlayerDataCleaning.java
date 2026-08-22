package noppes.npcs.packets.server;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.constants.EnumPlayerData;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.util.Util;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SPacketPlayerDataCleaning extends PacketServerBasic {

    protected static int channelId;
    private long time;

    public SPacketPlayerDataCleaning() { }

    public SPacketPlayerDataCleaning(long timeIn) { time = timeIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_PLAYERDATA); }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeLong(time); }

    @Override
    public void decode(FriendlyByteBuf buf) { time = buf.readLong(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        MinecraftServer server = player.getServer();
        if (server == null) { server = CustomNpcs.Server; }
        if (server != null) {
            File dirMod = CustomNpcs.getWorldSaveDirectory("playerdata");
            int i = 0;
            int s = Objects.requireNonNull(Objects.requireNonNull(dirMod).listFiles()).length;
            for (File dir : Objects.requireNonNull(dirMod.listFiles())) {
                if (dir.isDirectory()) {
                    for (File file : Objects.requireNonNull(dir.listFiles())) {
                        if (file.getName().endsWith(".json")) {
                            if (file.lastModified() > time) {
                                delete(dir);
                                i++;
                                EntityPlayerMP p = server.getPlayerList().getPlayerByUsername(file.getName().substring(0, file.getName().length() - 5));
                                if (p != null) {
                                    PlayerData.get(p).save(true);
                                    p.sendMessage(Component.translatable("message.change.mod.data").getParent());
                                }
                                break;
                            }
                            break;
                        }
                    }
                }
            }
            if (i > 0) { player.sendMessage(Component.translatable("message.data.cleaning.true", "" + i, "" + s).getParent()); }
            else { player.sendMessage(Component.translatable("message.data.cleaning.false", "" + s).getParent()); }
        }
        SPacketPlayerDataGet.sendPlayerData(EnumPlayerData.Players, player, player.getName());
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
