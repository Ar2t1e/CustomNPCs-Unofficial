package noppes.npcs.client.gui;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.blocks.tiles.TileCopy;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketSchematicsStore;
import noppes.npcs.packets.server.SPacketTileEntityGet;
import noppes.npcs.packets.server.SPacketTileEntitySave;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class GuiBlockCopy
		extends GuiNPCInterface
		implements IGuiData, ITextfieldListener {

	protected final TileCopy tile;

	public GuiBlockCopy(BlockPos posIn) {
		super();
		setBackground("menubg.png");
		imageWidth = 256;
		imageHeight = 216;

		tile = (TileCopy) player.world.getTileEntity(posIn);
		Packets.sendServer(new SPacketTileEntityGet(posIn));
	}

	@Override
	public void initGui() {
		super.initGui();
		int y = guiTop + 4;
		int x = guiLeft + 104;
		addTextField(0, x, y, 50, 20, tile.height)
				.setMinMaxDefault(0, 1000, 10);
		addLabel(0, guiLeft + 5, y + 5, "schematic.height");
		addTextField(1, x, y += 23, 50, 20, tile.width)
				.setMinMaxDefault(0, 1000, 10);
		addLabel(1, guiLeft + 5, y + 5, "schematic.width");
		addTextField(2, x, y += 23, 50, 20, tile.length)
				.setMinMaxDefault(0, 1000, 10);
		addLabel(2, guiLeft + 5, y + 5, "schematic.length");
		addTextField(5, x, y += 23, 100, 20, "");
		addLabel(5, guiLeft + 5, y + 5, "gui.name");
		x = guiLeft + 5;
		addButton(0, x, y += 30, "gui.save")
				.setSize(60, 20);
		addButton(1, guiLeft + 67, y, "gui.cancel")
				.setSize(60, 20);
	}

	@Override
	public void buttonEvent(GuiButtonNop button) {
		if (button.id == 0) {
			NBTTagCompound compound = new NBTTagCompound();
			tile.writeToNBT(compound);
			Packets.sendServer(new SPacketSchematicsStore(getTextField(5).getValue(), 0, compound));
		}
		onClose();
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
			case 0: tile.height = (short) textField.getInteger(); break;
			case 1: tile.width = (short) textField.getInteger(); break;
			case 2: tile.length = (short) textField.getInteger(); break;
		}
	}

}
