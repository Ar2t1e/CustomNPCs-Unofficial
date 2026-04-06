package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSyncUpdate;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketSyncUpdate extends PacketServerBasic {

    protected static int channelId;
    private int type;
    private NBTTagCompound data;

    public SPacketSyncUpdate() { }

    public SPacketSyncUpdate(int typeIn, NBTTagCompound dataIn) {
        type = typeIn;
        data = dataIn;
    }

    @Override
    public boolean toolAllowed(ItemStack item){ return true; }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(type);
        buf.writeNbt(data);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        type = buf.readInt();
        data = buf.readNbt();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        switch (type) {
            case 0: {
                CustomNpcs.TypeShowQuestCompass = data.getInteger("value");
                Packets.sendAll(new PacketSyncUpdate(CustomNpcs.TypeShowQuestCompass, 16, new NBTTagCompound()));
                break;
            } // TypeShowQuestCompass
            case 5: {
                PlayerData.get(player).dialogData.load(data);
                break;
            } // player dialogs data
            case 6: {
                PlayerData.get(player).minimap.load(data);
                break;
            } // minimap
            case 10: {
                PlayerData.get(player).compass.load(data);
                break;
            } // player compass data
        }
        CustomNpcs.debugData.end("Packets");
    }

}
