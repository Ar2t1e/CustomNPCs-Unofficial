package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.NBTJsonUtil;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class SPacketSaveClientScripts extends PacketServerBasic {

    protected static int channelId;

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public boolean toolAllowed(ItemStack item){ return true; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.EDIT_CLIENT_SCRIPT); }

    public static void encode(SPacketSaveClientScripts ignoredMsg, FriendlyByteBuf ignoredBuf) { }

    public static SPacketSaveClientScripts decode(FriendlyByteBuf ignoredBuf) { return new SPacketSaveClientScripts(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        ScriptController sData = ScriptController.Instance;
        CompoundTag compound = new CompoundTag();
        ScriptController.Instance.clientScripts.save(compound);
        File file = sData.clientScriptsFile();
        try {
            compound.remove("DirPath");
            compound.remove("Languages");
            compound.remove("WorldName");
            NBTJsonUtil.SaveFile(file, compound);
        }
        catch (Exception e) { LogWriter.error(e); }
        if (CustomNpcs.Server != null) {
            for (ServerPlayer player : CustomNpcs.Server.getPlayerList().getPlayers()) {
                ScriptController.Instance.sendClientTo(player);
            }
        }
        CustomNpcs.debugData.end("Packets");
    }

}