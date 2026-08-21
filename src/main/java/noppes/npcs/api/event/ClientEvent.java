package noppes.npcs.api.event;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.entity.EntityNPCInterface;

@SideOnly(Side.CLIENT)
public class ClientEvent extends CustomNPCsEvent {

    public EntityNPCInterface npc;
    public GuiScreen returnGui;

    public ClientEvent(EntityNPCInterface npcIn, GuiScreen returnGuiIn) {
        super();
        npc = npcIn;
        returnGui = returnGuiIn;
    }

    @Cancelable
    public static class PreGetGuiCustomNpcs extends ClientEvent {

        public EnumGuiType guiType;
        public FriendlyByteBuf buffer;

        public PreGetGuiCustomNpcs(EntityNPCInterface npc, EnumGuiType gui, FriendlyByteBuf bufIn) {
            super(npc, null);
            guiType = gui;
            buffer = bufIn;
        }

    }

    @Cancelable
    public static class PostGetGuiCustomNpcs extends ClientEvent {

        public EnumGuiType guiType;
        public FriendlyByteBuf buffer;

        public PostGetGuiCustomNpcs(EntityNPCInterface npc, EnumGuiType gui, FriendlyByteBuf bufferIn, GuiScreen returnGuiIn) {
            super(npc, returnGuiIn);
            guiType = gui;
            buffer = bufferIn;
        }

    }

    @Cancelable
    public static class NextToGuiCustomNpcs extends ClientEvent {

        public GuiScreen parent;

        public NextToGuiCustomNpcs(EntityNPCInterface npc, GuiScreen parentIn, GuiScreen returnGuiIn) {
            super(npc, returnGuiIn);
            parent = parentIn;
        }

    }

    @Cancelable
    public static class SubGuiCustomNpcs extends ClientEvent {

        public GuiScreen oldSubGui;

        public SubGuiCustomNpcs(EntityNPCInterface npc, GuiScreen newSubGuiIn, GuiScreen oldSubGuiIn) {
            super(npc, newSubGuiIn);
            oldSubGui = oldSubGuiIn;
        }

    }

}
