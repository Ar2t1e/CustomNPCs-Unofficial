package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.TextFormatting;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.scripts.IScriptHandler;
import noppes.npcs.controllers.scripts.ScriptContainer;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.util.ScriptEncryption;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

public class SPacketScriptEncrypt extends PacketServerBasic {

    protected static int channelId;
    private int type;
    private NBTTagCompound compound;

    public SPacketScriptEncrypt() { }

    public SPacketScriptEncrypt(int typeIn, NBTTagCompound compoundIn) {
        type = typeIn;
        compound = compoundIn;
    }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.TOOL_SCRIPTER); }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(type);
        buf.writeNbt(compound);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        type = buf.readInt();
        compound = buf.readAnySizeNbt();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    public void handle() {
        CustomNpcs.debugData.start("Packets");
        if (SPacketScriptText.handlers.containsKey(type)) {
            if (type == 6) { warn("You can't crypt scripts for the client!"); }
            else {
                IScriptHandler handler = SPacketScriptText.handlers.get(type);
                // file
                boolean error = false;
                File dir = CustomNpcs.getWorldSaveDirectory("scripts");
                if (dir != null) {
                    File file = new File(dir, compound.getString("Path"));
                    // data
                    String handlerType = "";
                    if (handler != null) {
                        handlerType = handler.getClass().getSimpleName();
                        ScriptContainer container = handler.getScripts().get(compound.getInteger("Tab"));
                        error = container == null;
                        if (!error) {
                            boolean onlyTab = compound.getBoolean("OnlyTab");
                            String code = "";
                            if (onlyTab) { code = container.script; } else {
                                try {
                                    Method getTotalCode = container.getClass().getDeclaredMethod("getFullCode");
                                    getTotalCode.setAccessible(true);
                                    code = (String) getTotalCode.invoke(container);
                                }
                                catch (Exception e) { error = true; }
                            }
                            if (!error) { error = !ScriptEncryption.encryptScript(file, compound.getString("Name"), code, onlyTab, container, handler); }
                        }
                    }
                    player.sendMessage(Component.empty()
                            .append(Component.literal("CustomNPCs").withStyle(TextFormatting.DARK_GREEN))
                            .append(Component.literal((error ? ": Error encrypt" : ": Encrypt") + " script to file \"")
                                    .withStyle(error ? TextFormatting.RED : TextFormatting.GRAY))
                            .append(Component.literal(file.getAbsolutePath()).withStyle(TextFormatting.RESET))
                            .append(Component.literal("\" for ").withStyle(error ? TextFormatting.RED : TextFormatting.GRAY))
                            .append(Component.literal(handlerType).withStyle(TextFormatting.RESET))
                            .getParent()
                    );
                }
            }
        }
        CustomNpcs.debugData.end("Packets");
    }

}