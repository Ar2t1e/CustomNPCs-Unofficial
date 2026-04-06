package noppes.npcs.packets.client;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.SchematicController;
import noppes.npcs.schematics.Schematic;
import noppes.npcs.schematics.SchematicWrapper;
import noppes.npcs.shared.common.PacketBasic;

public class PacketSaveSchematic extends PacketBasic {

    protected static int channelId;
    private NBTTagCompound data;

    public PacketSaveSchematic() { }

    public PacketSaveSchematic(NBTTagCompound dataIn) { data = dataIn; }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeNbt(data); }

    @Override
    public void decode(FriendlyByteBuf buf) { data = buf.readAnySizeNbt(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        Schematic schema = new Schematic("");
        schema.load(data);
        schema.save(player);
        SchematicController.Instance.map.put(schema.getName(), new SchematicWrapper(schema));
        CustomNpcs.debugData.end("Packets");
    }

}