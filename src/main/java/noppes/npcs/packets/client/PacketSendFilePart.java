package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.TextFormatting;
import noppes.npcs.CustomNpcs;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketRemoveLoadFile;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.ClientTickHandler;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.shared.common.PacketBasic;
import noppes.npcs.util.TempFile;
import noppes.npcs.util.Util;

import java.io.File;

public class PacketSendFilePart extends PacketBasic {

    protected static int channelId;
    private boolean remove;
    private int partId;
    private String name;
    private String partText;

    public PacketSendFilePart() { }

    public PacketSendFilePart(boolean isRemove, int part, String nameIn, String partTextIn) {
        remove = isRemove;
        partId = part;
        name = nameIn;
        partText = partTextIn;
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        remove = buf.readBoolean();
        partId = buf.readInt();
        name = buf.readUtf();
        partText = buf.readUtf();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(remove);
        buf.writeInt(partId);
        buf.writeUtf(name);
        buf.writeUtf(partText);
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (remove) { ClientProxy.loadFiles.remove(name); }
        if (!ClientProxy.loadFiles.containsKey(name)) {
            CustomNpcs.debugData.end("Packets");
            return;
        }
        TempFile file = ClientProxy.loadFiles.get(name);
        file.data.put(partId, partText);
        file.lastLoad = System.currentTimeMillis() - 15000L;
        file.tryLoads = 0;
        if (file.isLoad()) {
            if (file.saveType == 1) {
                LogWriter.info("Script Client file was received from the Server: \"" + name + "\"");
                File normalFile = new File(CustomNpcs.Dir, ScriptController.Instance.clientScripts.getLanguage().toLowerCase() + "/" + name);
                if (player.isCreative() || CustomNpcs.proxy.getPlayerData(player).game.op) {
                    String s = "" + file.size;
                    if (file.size > 999) {
                        s = Util.instance.getTextReducedNumber(file.size, false, false, false);
                    }
                    player.sendMessage(Component.literal("CustomNpcs").withStyle(TextFormatting.DARK_GREEN)
                            .append(Component.literal(": Received client script: \"").withStyle(TextFormatting.GRAY))
                            .append(Component.literal(normalFile.getAbsolutePath()).withStyle(TextFormatting.WHITE))
                            .append(Component.literal("\" (").withStyle(TextFormatting.GRAY))
                            .append(s)
                            .append(Component.literal("b)").withStyle(TextFormatting.GRAY))
                            .getParent());
                }
                // Put to session
                ScriptController.Instance.clients.put(name, file.getDataText());
                ScriptController.Instance.clientSizes.put(name, file.size);
                // save on client
                Util.instance.saveFile(normalFile, file.getDataText());
            }
            else { file.save(); }
            ClientProxy.loadFiles.remove(name);
            Packets.sendServer(new SPacketRemoveLoadFile(name));
        }
        ClientTickHandler.loadFiles();
        CustomNpcs.debugData.end("Packets");
    }

}
