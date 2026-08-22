package noppes.npcs.client.gui.roles;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketNpcJobSave;
import noppes.npcs.roles.JobFarmer;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;

public class GuiJobFarmer extends GuiNPCInterface2 {

	protected final JobFarmer job;

	public GuiJobFarmer(EntityNPCInterface npc) {
		super(npc);

		backGui = EnumGuiType.MainMenuAdvanced;
		job = (JobFarmer) npc.job;
	}

	@Override
	public void initGui() {
		super.initGui();
		addLabel(0, guiLeft + 10, guiTop + 20, "farmer.itempicked")
				.setSize(148, 10);
		addButton(0, guiLeft + 150, guiTop + 15, false, job.chestMode, "farmer.donothing", "farmer.chest", "farmer.drop")
				.setSize(160, 20);
	}

	@Override
	public void buttonEvent(GuiButtonNop button) {
		if (button.id == 0) { job.chestMode = button.getValue(); }
	}

	@Override
	public void save() { Packets.sendServer(new SPacketNpcJobSave(job.save(new NBTTagCompound()))); }

}
