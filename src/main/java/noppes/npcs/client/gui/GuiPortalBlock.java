package noppes.npcs.client.gui;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.CustomNpcs;
import noppes.npcs.blocks.custom.CustomBlockPortal;
import noppes.npcs.blocks.custom.tiles.CustomTileEntityPortal;
import noppes.npcs.client.gui.availability.SubGuiNpcAvailability;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.controllers.data.DimensionData;
import noppes.npcs.controllers.DimensionController;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketTileEntityGet;
import noppes.npcs.packets.server.SPacketTileEntitySave;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiPortalBlock extends GuiNPCInterface
        implements IGuiData, ITextfieldListener {

    private final CustomTileEntityPortal tile;
    protected final Map<Integer, DimensionData> dataDimIDs = new HashMap<>();

    protected int dimension;
    protected int nextDimension;
    protected BlockPos position;

    public GuiPortalBlock(BlockPos pos) {
        super();

        tile = (CustomTileEntityPortal) player.world.getTileEntity(pos);
        if (tile != null) {
            if (tile.homeDimensionId == player.world.provider.getDimension()) {
                dimension = tile.dimensionId;
                nextDimension = tile.homeDimensionId;
                position = tile.posTp;
            } else {
                dimension = tile.homeDimensionId;
                nextDimension = tile.dimensionId;
                position = tile.posHomeTp;
            }
        }
        Packets.sendServer(new SPacketTileEntityGet(pos));
    }

    @Override
    public void initGui() {
        super.initGui();
        int xl = guiLeft + 1;
        int w = 72;
        int x = xl + w + 1;
        int y = guiTop + 40;
        int lId = 0;
        // availability
        addButton(4, x - 20, y, "availability.available")
                .setSize(120, 20)
                .setHoverTexts("portal.hover.availability");
        int color = new Color(0xFFFFFF).getRGB();
        // dimensions
        dataDimIDs.clear();
        List<Integer> dimMap = DimensionController.getInstance().getAllIDs();
        List<Object> dimIDList = new ArrayList<>();
        int p = 0;
        for (Integer line : dimMap) {
            DimensionData dd = DimensionController.getDimensionData(line);
            if (dd != null && line != nextDimension) {
                if (line == dimension) { p = dimIDList.size(); }
                dataDimIDs.put(dimIDList.size(), dd);
                dimIDList.add(line);
            }
        }
        Object[] dimIDs = dimIDList.toArray();
        addLabel(lId++, xl, (y += 25) + 5, Component.translatable("parameter.dimension.id").append(":"))
                .setSize(w, 10)
                .setColor(color);
        addButton(5, x, y, true, p, dimIDs)
                .setSize(160, 16)
                .setHoverTexts(Component.translatable("portal.hover.dimension.id", dimIDs.length > p ? dimIDs[p] : "???"));
        // position
        int xp = x;
        addLabel(lId++, xl, (y += 24) + 3, "gui.position").setColor(color)
                .setSize(w - 12, 10)
                .setColor(color);
        addLabel(lId++, xp - 12, y + 3, "X:").setColor(color)
                .setSize(12, 10)
                .setColor(color);
        addTextField(0, xp, y, 49, 14, position.getX())
                .setHoverTexts(Component.translatable("portal.hover.pos.xz", "X"));
        addLabel(lId++, xp += 52, y + 3, "Y:").setColor(color)
                .setSize(12, 10)
                .setColor(color);
        addTextField(1, xp += 12, y, 49, 14, position.getY())
                .setHoverTexts(Component.translatable("portal.hover.pos.xz", "Y")
                        .append(Component.translatable("portal.hover.pos.y")));
        addLabel(lId++, xp += 52, y + 3, "Z:").setColor(color)
                .setSize(12, 10)
                .setColor(color);
        addTextField(2, xp + 12, y, 49, 14, position.getZ())
                .setHoverTexts(Component.translatable("portal.hover.pos.xz", "X"));

        if (tile != null) {
            String shaderPath = "assets/" + CustomNpcs.MODID + "/shaders/core" + ((CustomBlockPortal) tile.getBlockType()).getCustomName();
            // sky texture
            addLabel(lId++, xl, (y += 24) + 3, Component.translatable("portal.texture.sky").append(":")).setColor(color)
                    .setSize(w, 10)
                    .setColor(color);
            addTextField(3, xp + 12, y, 177, 14, tile.getSkyTexture())
                    .setHoverTexts(Component.translatable("portal.hover.texture.sky", shaderPath));

            // portal texture
            addLabel(lId, xl, (y += 24) + 3, Component.translatable("portal.texture.portal").append(":")).setColor(color)
                    .setSize(w, 10)
                    .setColor(color);
            addTextField(4, xp + 12, y, 177, 14, tile.getPortalTexture())
                    .setHoverTexts(Component.translatable("portal.hover.texture.portal", shaderPath));
        }
        // exit
        addButton(66, x - 20, guiTop + 188, "gui.done")
                .setSize(120, 20)
                .setHoverTexts("hover.exit");
    }

    @Override
    public void buttonEvent(GuiButtonNop button) {
        switch (button.id) {
            case 4: save(); setSubGui(new SubGuiNpcAvailability(tile.availability, this)); break;
            case 5: {
                DimensionData dd = dataDimIDs.get(button.getValue());
                if (dd != null) {
                    dimension = dd.dimensionId;
                    position = dd.spawnPos;
                    initGui();
                }
                break;
            }
            case 66: onClose(); break;
        }
    }

    @Override
    public void save() {
        if (tile != null) {
            if (tile.homeDimensionId == player.world.provider.getDimension()) {
                tile.dimensionId = dimension;
                tile.posTp = position;
            } else {
                tile.homeDimensionId = dimension;
                tile.posHomeTp = position;
            }
            Packets.sendServer(new SPacketTileEntitySave(tile.writeToNBT(new NBTTagCompound())));
        }
    }

    @Override
    public void setGuiData(NBTTagCompound compound) {
        if (tile != null) {
            tile.readFromNBT(compound);
            if (tile.homeDimensionId == player.world.provider.getDimension()) {
                dimension = tile.dimensionId;
                nextDimension = tile.homeDimensionId;
                position = tile.posTp;
            } else {
                dimension = tile.homeDimensionId;
                nextDimension = tile.dimensionId;
                position = tile.posHomeTp;
            }
        }
        initGui();
    }

    @Override
    public void unFocused(GuiTextFieldNop textField) {
        switch (textField.id) {
            case 0: position = new BlockPos(textField.getInteger(), position.getY(), position.getZ()); break;
            case 1: position = new BlockPos(position.getX(), textField.getInteger(), position.getZ()); break;
            case 2: position = new BlockPos(position.getX(), position.getY(), textField.getInteger()); break;
            case 3: if (tile != null) { tile.setSkyTexture(textField.getValue()); } break;
            case 4: if (tile != null) { tile.setPortalTexture(textField.getValue()); } break;
        }
    }


}
