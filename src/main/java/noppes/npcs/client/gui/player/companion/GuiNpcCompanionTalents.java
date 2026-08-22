package noppes.npcs.client.gui.player.companion;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.network.chat.Component;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketCompanionOpenInv;
import noppes.npcs.packets.server.SPacketCompanionTalentExp;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.constants.EnumCompanionTalent;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleCompanion;

public class GuiNpcCompanionTalents extends GuiNPCInterface {

	protected final Map<Integer, GuiNpcCompanionTalents.GuiTalent> talents = new HashMap<>();
	protected final RoleCompanion role;
	protected GuiButtonNop selected;
	protected long lastPressedTime = 0L;
	protected long startPressedTime = 0L;

	public GuiNpcCompanionTalents(EntityNPCInterface npc) {
		super(npc);
		setBackground("companion_empty.png");
		imageWidth = 171;
		imageHeight = 166;

		role = (RoleCompanion) npc.role;
	}

	@Override
	public void initGui() {
		super.initGui();
		talents.clear();
		addLabel(0, guiLeft + 4, guiTop + 10, Component.translatable("quest.exp").append(": "));
		GuiNpcCompanionStats.addTopMenu(role, this, 2);
		int i = 0;
		for (EnumCompanionTalent e : role.talents.keySet()) { addTalent(i++, e); }
	}

	@Override
	public void buttonEvent(GuiButtonNop button) {
		switch (button.id) {
			case 1: {
				NoppesUtilServer.setEditingNpc(player, npc);
				CustomNpcs.proxy.openGui(npc, EnumGuiType.Companion, null);
				break;
			}
			case 3: Packets.sendServer(new SPacketCompanionOpenInv()); break;
			default: {
				if (button.id >= 10) {
					selected = button;
					lastPressedTime = startPressedTime = (minecraft.world == null ? 0L : minecraft.world.getWorldTime());
					addExperience(1);
				}
				break;
			}
		}
	}

	private void addExperience(int exp) {
		EnumCompanionTalent talent = talents.get(selected.id - 10).talent;
		if (role.canAddExp(-exp) || role.currentExp > 0) {
			if (exp > role.currentExp) { exp = role.currentExp; }
			Packets.sendServer(new SPacketCompanionTalentExp(talent, exp));
			role.talents.put(talent, role.talents.get(talent) + exp);
			role.addExp(-exp);
			getLabel(selected.id - 10).setMessage(Component.literal(role.talents.get(talent) + "/" + role.getNextLevel(talent)));
		}
	}

	private void addTalent(int i, EnumCompanionTalent talent) {
		int y = guiTop + 28 + i / 2 * 26;
		int x = guiLeft + 4 + i % 2 * 84;
		GuiNpcCompanionTalents.GuiTalent gui = new GuiNpcCompanionTalents.GuiTalent(role, talent, x, y);
		gui.setWorldAndResolution(minecraft, width, height);
		talents.put(i, gui);
		if (role.getTalentLevel(talent) < 5) {
			addButton(i + 10, x + 26, y, "+")
					.setSize(14, 14);
			y += 8;
		}
		addLabel(i, x + 26, y + 8, role.talents.get(talent) + "/" + role.getNextLevel(talent));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		if (selected != null && mc.world.getWorldTime() - startPressedTime > 4L &&
				lastPressedTime < mc.world.getWorldTime() && mc.world.getWorldTime() % 4L == 0L) {
			if (selected.mouseClicked(mouseX, mouseY, 0)) {
				lastPressedTime = mc.world.getWorldTime();
				if (lastPressedTime - startPressedTime < 20L) { addExperience(1); }
				else if (lastPressedTime - startPressedTime < 40L) { addExperience(2); }
				else if (lastPressedTime - startPressedTime < 60L) { addExperience(4); }
				else if (lastPressedTime - startPressedTime < 90L) { addExperience(8); }
				else if (lastPressedTime - startPressedTime < 140L) { addExperience(14); }
				else { addExperience(28); }
			}
			else {
				lastPressedTime = 0L;
				selected = null;
			}
		}
		mc.getTextureManager().bindTexture(Gui.ICONS);
		drawTexturedModalRect(guiLeft + 4, guiTop + 20, 10, 64, 162, 5);
		if (role.currentExp > 0) {
			float v = 1.0f * role.currentExp / role.getMaxExp();
			if (v > 1.0f) { v = 1.0f; }
			drawTexturedModalRect(guiLeft + 4, guiTop + 20, 10, 69, (int) (v * 162.0f), 5);
		}
		String s = role.currentExp + "\\" + role.getMaxExp();
		mc.fontRenderer.drawString(s, guiLeft + imageWidth / 2 - mc.fontRenderer.getStringWidth(s) / 2, guiTop + 10,
				CustomNpcResourceListener.DefaultTextColor);
		for (GuiTalent talent : talents.values()) { talent.drawScreen(mouseX, mouseY, partialTicks); }
	}

	public static class GuiTalent extends GuiScreen {

		protected static final ResourceLocation resource = new ResourceLocation(CustomNpcs.MODID, "textures/gui/talent.png");
		protected final RoleCompanion role;
		protected final EnumCompanionTalent talent;
		protected final int x;
		protected final int y;

		public GuiTalent(RoleCompanion roleIn, EnumCompanionTalent talentIn, int xIn, int yIn) {
			talent = talentIn;
			role = roleIn;
			x = xIn;
			y = yIn;
		}

		public void drawScreen(int i, int j, float f) {
			Minecraft mc = Minecraft.getMinecraft();
			mc.getTextureManager().bindTexture(GuiTalent.resource);
			ItemStack item = talent.item;
			item.getItem();
			GlStateManager.pushMatrix();
			GlStateManager.color(1.0f, 1.0f, 1.0f);
			GlStateManager.enableBlend();
			boolean hover = x < i && x + 24 > i && y < j && y + 24 > j;
			drawTexturedModalRect(x, y, 0, hover ? 24 : 0, 24, 24);
			zLevel = 100.0f;
			itemRender.zLevel = 100.0f;
			GlStateManager.enableLighting();
			GlStateManager.enableRescaleNormal();
			RenderHelper.enableGUIStandardItemLighting();
			itemRender.renderItemAndEffectIntoGUI(item, x + 4, y + 4);
			itemRender.renderItemOverlays(mc.fontRenderer, item, x + 4, y + 4);
			RenderHelper.disableStandardItemLighting();
			GlStateManager.disableLighting();
			GlStateManager.translate(0.0f, 0.0f, 200.0f);
			drawCenteredString(mc.fontRenderer, role.getTalentLevel(talent) + "", x + 20, y + 16, 16777215);
			itemRender.zLevel = 0.0f;
			zLevel = 0.0f;
			GlStateManager.popMatrix();
		}

	}

}
