package noppes.npcs.packets.server;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.NBTJsonUtil;

import java.io.File;

public class SPacketSaveClientScripts extends PacketServerBasic {

    protected static int channelId;

    @Override
    public boolean toolAllowed(ItemStack item){ return true; }

    @Override
    public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.EDIT_CLIENT_SCRIPT; }

    @Override
    public void encode(FriendlyByteBuf buf) { }

    @Override
    public void decode(FriendlyByteBuf buf) { }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        ScriptController sData = ScriptController.Instance;
        NBTTagCompound compound = new NBTTagCompound();
        ScriptController.Instance.clientScripts.save(compound);
        File file = sData.clientScriptsFile();
        try {
            compound.removeTag("DirPath");
            compound.removeTag("Languages");
            compound.removeTag("WorldName");
            NBTJsonUtil.SaveFile(file, compound);
        }
        catch (Exception e) { LogWriter.error(e); }
        if (CustomNpcs.Server != null) {
            for (EntityPlayerMP player : CustomNpcs.Server.getPlayerList().getPlayers()) {
                ScriptController.Instance.sendClientTo(player);
            }
        }
        CustomNpcs.debugData.end("Packets");
    }

}