package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.TextFormatting;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.api.wrapper.ItemScriptedWrapper;
import noppes.npcs.blocks.tiles.TileScripted;
import noppes.npcs.blocks.tiles.TileScriptedDoor;
import noppes.npcs.controllers.IScriptHandler;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.controllers.data.*;
import noppes.npcs.entity.data.DataScript;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.util.ScriptEncryption;

import java.io.File;
import java.lang.reflect.Method;

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
    public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.TOOL_SCRIPTER; }

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
            if (type == 6) { warn(CustomNpcsPermissions.EDIT_CLIENT_SCRIPT); }
            else {
                IScriptHandler handler = SPacketScriptText.handlers.get(type);
                if (handler instanceof DataScript) { ((DataScript) handler).load(compound); ((DataScript) handler).lastInited = -1L; } // 0: NPC
                if (handler instanceof TileScripted) { ((TileScripted) handler).setNBT(compound); ((TileScripted) handler).lastInited = -1L; } // 1: Block
                if (handler instanceof ItemScriptedWrapper) { ((ItemScriptedWrapper) handler).setMCNbt(compound); ((ItemScriptedWrapper) handler).lastInited = -1L; } // 2: Item
                if (handler instanceof ForgeScriptData) { ((ForgeScriptData) handler).load(compound); ((ForgeScriptData) handler).lastInited = -1L; } // 3: Forge
                if (handler instanceof PlayerScriptData) { ((PlayerScriptData) handler).load(compound); ((PlayerScriptData) handler).lastInited = -1L; } // 4: Player
                if (handler instanceof TileScriptedDoor) { ((TileScriptedDoor) handler).setNBT(compound); ((TileScriptedDoor) handler).lastInited = -1L; } // 5: Door
                if (handler instanceof ClientScriptData) { ((ClientScriptData) handler).load(compound); ((ClientScriptData) handler).lastInited = -1L; } // 6: Client
                if (handler instanceof PotionScriptData) { ((PotionScriptData) handler).load(compound); ((PotionScriptData) handler).lastInited = -1L; } // 7: Potion
                if (handler instanceof NpcScriptData) { ((NpcScriptData) handler).load(compound); ((NpcScriptData) handler).lastInited = -1L; } // 8: Npcs
                // file
                boolean error = false;
                File file = new File(compound.getString("Path"));
                String filePath = file.getAbsolutePath();
                if (filePath.contains(".\\")) { filePath = filePath.substring(filePath.indexOf(".\\")); }
                else {
                    File tempFile = new File(compound.getString("Path"));
                    while (tempFile.getParentFile() != null) {
                        tempFile = tempFile.getParentFile();
                        if ((new File(tempFile, "config")).exists()) { break; }
                    }
                    filePath = filePath.replace(tempFile.getParentFile() + "\\", "");
                }
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
                        .append(Component.literal(filePath).withStyle(TextFormatting.RESET))
                        .append(Component.literal("\" for ").withStyle(error ? TextFormatting.RED : TextFormatting.GRAY))
                        .append(Component.literal(handlerType).withStyle(TextFormatting.RESET))
                );
            }
        }
        CustomNpcs.debugData.end("Packets");
    }

}