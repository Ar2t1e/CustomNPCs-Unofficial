package noppes.npcs.client.gui;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.CustomNpcs;
import noppes.npcs.blocks.tiles.TileWaypoint;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketTileEntityGet;
import noppes.npcs.packets.server.SPacketTileEntitySave;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

import javax.annotation.Nonnull;
import java.awt.*;

public class GuiNpcWaypoint extends GuiNPCInterface implements IGuiData, ITextfieldListener {

	protected final TileWaypoint tile;

	public GuiNpcWaypoint(BlockPos pos) {
		super();
		imageWidth = 265;

		tile = (TileWaypoint) player.world.getTileEntity(pos);
		Packets.sendServer(new SPacketTileEntityGet(pos));
	}

	@Override
	public void initGui() {
		super.initGui();
		if (tile == null) { onClose(); }
		else {
			// name
			addLabel(0, guiLeft + 1, guiTop + 76, "gui.name")
					.setColor(CustomNpcs.MainColor.getRGB());
			addTextField(0, guiLeft + 60, guiTop + 71, 200, 20, tile.name);
			// range
			addLabel(1, guiLeft + 1, guiTop + 97, "gui.range")
					.setColor(CustomNpcs.MainColor.getRGB());
			addTextField(1, guiLeft + 60, guiTop + 92, 200, 20, tile.range)
					.setMinMaxDefault(2, 60, 10);
			// exit
			addButton(0, guiLeft + 40, guiTop + 190,"gui.done")
					.setSize(120, 20)
					.setHoverTexts("hover.exit");
		}
	}

	@Override
	public void buttonEvent(GuiButtonNop button) {
		if (button.id == 0) { onClose(); }
	}

	@Override
	public void save() {
		NBTTagCompound compound = new NBTTagCompound();
		tile.writeToNBT(compound);
		Packets.sendServer(new SPacketTileEntitySave(compound));
	}

	@Override
	public void setGuiData(NBTTagCompound compound) {
		tile.readFromNBT(compound);
		initGui();
	}

	@Override
	public void unFocused(GuiTextFieldNop textField) {
		switch (textField.id) {
			case 0: tile.name = textField.getValue(); break;
			case 1: tile.range = textField.getInteger(); break;
		}
	}

}
