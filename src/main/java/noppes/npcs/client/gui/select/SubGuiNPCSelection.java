package noppes.npcs.client.gui.select;

import java.util.*;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketRemoteNpcsGet;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IGuiData;

// New from Unofficial (BetaZavr)
public class SubGuiNPCSelection
        extends GuiNPCInterface
        implements IGuiData, ICustomScrollListener {

    protected final HashMap<Component, Integer> dataIDs = new HashMap<>();
    protected GuiCustomScrollNop scroll;
    public EntityNPCInterface selectEntity;
    public EntityNPCInterface main;

    public SubGuiNPCSelection(EntityNPCInterface completer) {
        super();
        imageWidth = 256;
        setBackground("menubg.png");

        selectEntity = completer;
        main = completer;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (!hasSubGui()) {
            int u = guiLeft + 182;
            int v = guiTop + 40;
            PoseStack matrixStack = graphics.pose();
            matrixStack.pushPose();
            if (selectEntity != null) {
                drawNpc(graphics, selectEntity, u - guiLeft + 30, v - guiTop + 70, 1.0f, (int) (3 * player.level().getGameTime() % 360), 0, 1);
            }
            matrixStack.translate(0.0f, 0.0f, 1.0f);
            graphics.fill(u - 1, v - 1, u + 60, v + 85, 0xFF808080);
            graphics.fill(u, v, u + 59, v + 84, 0xFF000000);
            matrixStack.popPose();
        }
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void init() {
        super.init();
        if (scroll == null) { scroll = addScroll(0).setSize(165, 209); }
        add(scroll.setPos(guiLeft + 4, guiTop + 4));
        Packets.sendServer(new SPacketRemoteNpcsGet(false));
    }

    @Override
    public boolean keyPressed(int key, int key_1, int key_2) {
        if (isEscKey(key) || isInventoryKey(key)) { onClose(); }
        boolean bo = super.keyPressed(key, key_1, key_2);
        if (isUpKey(key) || isDownKey(key)) { resetEntity(); }
        return bo;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        boolean bo = super.mouseClicked(mouseX, mouseY, mouseButton);
        scroll.mouseClicked(mouseX, mouseY, mouseButton);
        return bo;
    }

    @Override
    public void scrollClicked(GuiCustomScrollNop scroll) {
        resetEntity();
        init();
    }

    @Override
    public void scrollDoubleClicked(GuiCustomScrollNop scroll) { onClose(); }

    @Override
    public void setGuiData(CompoundTag compound) {
        ListTag nbtList = compound.getList("Data", 10);
        List<Component> list = new ArrayList<>();
        dataIDs.clear();
        Component mainKey = Component.empty()
                .append(Component.literal("ID:-1 ").withStyle(ChatFormatting.GREEN))
                .append(Component.literal(main.getName() + " ").withStyle(ChatFormatting.RESET))
                .append(Component.literal(df.format(-1.0f)).withStyle(ChatFormatting.GRAY));
        dataIDs.put(mainKey, -1);
        LinkedHashMap<Integer, List<Component>> hts = new LinkedHashMap<>();
        for (int i = 0; i < nbtList.size(); ++i) {
            CompoundTag nbt = nbtList.getCompound(i);
            int id = nbt.getInt("K");
            ChatFormatting type = switch (nbt.getInt("C")) {
                case 1 -> ChatFormatting.GREEN;
                case 2 -> ChatFormatting.RED;
                case 3 -> ChatFormatting.YELLOW;
                case 4 -> ChatFormatting.AQUA;
                default -> ChatFormatting.GRAY;
            };
            String distance = df.format(nbt.getFloat("V"));
            MutableComponent key = Component.empty()
                    .append(Component.literal("ID:" + id).withStyle(type))
                    .append(Component.literal(" " + nbt.getString("N")).withStyle(ChatFormatting.RESET))
                    .append(Component.literal(" (" + distance + ")").withStyle(ChatFormatting.GRAY));
            list.add(key);
            dataIDs.put(key, nbt.getInt("K"));

            List<Component> hoverList = new ArrayList<>();
            hoverList.add(Component.literal("Name: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(nbt.getString("N")).withStyle(ChatFormatting.WHITE)));
            hoverList.add(Component.literal("Entity ID: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal("" + id).withStyle(type)));
            hoverList.add(Component.literal("Distance to: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(distance).withStyle(ChatFormatting.GOLD))
                    .append(Component.literal(" blocks").withStyle(ChatFormatting.GRAY)));
            hoverList.add(Component.literal("Class Type: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(nbt.getString("S")).withStyle(ChatFormatting.WHITE)));
            hts.put(i, hoverList);
        }
        scroll.setUnsortedList(list);
        scroll.setHoverTexts(hts);
        resetEntity();
    }

    private void resetEntity() {
        selectEntity = null;
        if (dataIDs.containsKey(scroll.getNormalSelected())) {
            Entity entity = player.level().getEntity(dataIDs.get(scroll.getNormalSelected()));
            if (!(entity instanceof EntityNPCInterface)) { return; }
            selectEntity = (EntityNPCInterface) entity;
        }
    }

}
