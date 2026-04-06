package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.gui.GuiNpcRemoteEditor;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

public class PacketRemoteNpcsEntity extends PacketBasic {

    protected static int channelId;
    private NBTTagCompound data;

    public PacketRemoteNpcsEntity() { }

    public PacketRemoteNpcsEntity(NBTTagCompound compound) { data = compound; }

    @Override
    public void decode(FriendlyByteBuf buf) { data = buf.readNbt(); }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeNbt(data); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        Entity entity = EntityList.createEntityFromNBT(data, player.world);
        if (entity != null) {
            if (entity instanceof EntityNPCInterface) { ((EntityNPCInterface) entity).ais.setStartPos(npc.getPosition()); }
            entity.readFromNBT(data);
            entity.setPosition(0.0d, 0.0d, 0.0d);
            GuiScreen screen = Minecraft.getMinecraft().currentScreen;
            if (screen instanceof GuiNpcRemoteEditor) {
                ((GuiNpcRemoteEditor) screen).selectEntity = entity;
                screen.initGui();
            }
        }
        CustomNpcs.debugData.end("Packets");
    }

}