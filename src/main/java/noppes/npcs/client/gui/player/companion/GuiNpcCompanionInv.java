package noppes.npcs.client.gui.player.companion;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.constants.EnumCompanionTalent;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.containers.ContainerNPCCompanion;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;

import javax.annotation.Nonnull;

public class GuiNpcCompanionInv extends GuiContainerNPCInterface<ContainerNPCCompanion> {

	protected final ResourceLocation resource = getResource("companioninv.png");
	protected final RoleCompanion role;

	public GuiNpcCompanionInv(EntityNPCInterface npcIn, ContainerNPCCompanion container) {
		super(npcIn, container, Component.empty());
		closeOnEsc = true;
		xSize = 171;
		ySize = 166;

		role = (RoleCompanion) npc.role;
	}

	@Override
	public void initGui() {
		super.initGui();
		GuiNpcCompanionStats.addTopMenu(role, this, 3);
	}

	@Override
	public void buttonEvent(@Nonnull GuiButtonNop button) {
		NoppesUtilServer.setEditingNpc(player, npc);
		switch (button.id) {
			case 1: CustomNpcs.proxy.openGui(npc, EnumGuiType.Companion, null); break;
			case 2: CustomNpcs.proxy.openGui(npc, EnumGuiType.CompanionTalent, null); break;
		}
	}

    @Override
	public void drawScreen(int mouseXIn, int mouseYIn, float partialTicks) {
		super.drawScreen(mouseXIn, mouseYIn, partialTicks);
		drawNpc(52, 70);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int xMouse, int yMouse) {
		drawWorldBackground(0);
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		mc.getTextureManager().bindTexture(resource);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		mc.getTextureManager().bindTexture(GuiNPCInterface.RESOURCE_SLOT);
		if (role.getTalentLevel(EnumCompanionTalent.ARMOR) > 0) {
			for (int i = 0; i < 4; ++i) {
				drawTexturedModalRect(guiLeft + 5, guiTop + 7 + i * 18, 0, 0, 18, 18);
			}
		}
		if (role.getTalentLevel(EnumCompanionTalent.SWORD) > 0) {
			drawTexturedModalRect(guiLeft + 78, guiTop + 16, 0, (npc.inventory.weapons.get(0) == null) ? 18 : 0, 18, 18);
		}
		role.getTalentLevel(EnumCompanionTalent.RANGED);
		if (role.talents.containsKey(EnumCompanionTalent.INVENTORY)) {
			for (int size = (role.getTalentLevel(EnumCompanionTalent.INVENTORY) + 1) * 2, j = 0; j < size; ++j) {
				drawTexturedModalRect(guiLeft + 113 + j % 3 * 18, guiTop + 7 + j / 3 * 18, 0, 0, 18, 18);
			}
		}
	}

}
