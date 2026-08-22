package noppes.npcs.client.gui;

import java.awt.*;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.chat.Component;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.shared.client.gui.GuiTextAreaScreen;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCheckBoxNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.gui.select.SubGuiNPCSelection;
import noppes.npcs.client.gui.select.SubGuiTextureSelection;
import noppes.npcs.constants.EnumQuestCompletion;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.entity.EntityNPCInterface;

public class SubGuiNpcQuestExtra extends GuiNPCInterface implements ITextfieldListener {

	protected static final ResourceLocation SHEET = new ResourceLocation(CustomNpcs.MODID, "textures/quest/log/q_log_3.png");
	protected static final ResourceLocation TABS = new ResourceLocation(CustomNpcs.MODID, "textures/quest/log/q_log_4.png");
	protected EntityNPCInterface showNpc;
	protected ScaledResolution sw;
	public Quest quest;

	public SubGuiNpcQuestExtra(Quest questIn) {
		super();
		setBackground("menubg.png");
		imageWidth = 256;
		imageHeight = 217;

		quest = questIn;
		showNpc = quest.completer.getNpc();
	}

	@Override
	public void buttonEvent(GuiButtonNop button) {
		switch (button.id) {
			case 0: setSubGui(new SubGuiTextureSelection(this, 0, showNpc, quest.icon.toString(), ".png", 3)); break; // icon select
			case 1: quest.completion = EnumQuestCompletion.values()[button.getValue()]; break; // completion type
			case 2: setSubGui(new SubGuiNPCSelection(quest.completer.getNpc())); break; // select npc
			case 3: setSubGui(new SubGuiTextureSelection(this, 1, showNpc, quest.texture == null ? "" : quest.texture.toString(), ".png", 3)); break; // texture select
			case 4: setSubGui(new GuiTextAreaScreen(0, quest.rewardText)); break; // reward text
			case 5: {
				quest.extraButton = button.getValue();
				initGui();
				break;
			} // extra button type
			case 6: setSubGui(new GuiTextAreaScreen(1, quest.extraButtonText)); break; // extra button hover text
			case 7: quest.showProgressInChat = ((GuiCheckBoxNop) button).selected(); break;
			case 8: quest.showProgressInWindow = ((GuiCheckBoxNop) button).selected(); break;
			case 9: quest.completer.setStrict(((GuiCheckBoxNop) button).selected()); break;
			case 66: onClose(); break;
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		List<Component> tempHoverText = getHoverText();
		hoverText.clear();
		super.drawScreen(mouseX, mouseY, partialTicks);
		int u = guiLeft + 182;
		int v = guiTop + 97;
		if (getButton(2) != null) {
			u = getButton(2).getX() + getButton(2).getWidth() + 7;
			v = getButton(2).getY() + 2;
		}
		GlStateManager.enableBlend();
		// Back on NPC
		int color = new Color(0xFF404040).getRGB();
		GlStateManager.pushMatrix();
		GlStateManager.translate(u + 5.0f, v + 3.0f, 1.0f);
		drawRect(-6, -6, 61, 61, color);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(SHEET);
		drawTexturedModalRect(-5, -5, 34, 54, 65, 65);
		GlStateManager.popMatrix();

		if (showNpc != null && !hasSubGui()) {
			// NPC
			GlStateManager.pushMatrix();
			GL11.glEnable(GL11.GL_SCISSOR_TEST);
			int c = sw.getScaledWidth() < mc.displayWidth
					? (int) Math.round((double) mc.displayWidth / (double) sw.getScaledWidth())
					: 1;
			GL11.glScissor((u + 10) * c, (v + 11) * c, (54) * c, (44) * c);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.translate(0.0f, 0.0f, 10.0f);
			//drawRect((u + 10), (v + 11), (u + 54), (v + 44), 0xFFFF0000);
			drawNpc(showNpc, 218, 149, 1.0f, 30, -5, 1);
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
			GlStateManager.popMatrix();

			// Fase
			GlStateManager.enableBlend();
			GlStateManager.pushMatrix();
			GlStateManager.disableDepth();
			GlStateManager.translate(u + 1.0f, v + 1.0f, 150.0f);
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
			minecraft.getTextureManager().bindTexture(TABS);
			drawTexturedModalRect(0, 0, 193, 0, 63, 52);
			GlStateManager.popMatrix();

			// Name
			Component name = Component.empty().append(Component.literal(quest.completer != null ? quest.completer.getName() : "Empty"));
			u += 1;
			v += 51;
			GlStateManager.pushMatrix();
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
			GlStateManager.translate(0.0f, 0.0f, 200.0f);
			GuiButtonNop.renderString(name, u, v, u + 63, v +10, CustomNpcs.QuestLogColor.getRGB(), false, true, null);
			GlStateManager.popMatrix();
		}
		// script button
		if (quest.extraButton > 0 && !hasSubGui()) {
			u = guiLeft + 98;
			v = guiTop + 134;
			if (getButton(5) != null) {
				u = getButton(5).getX() - 12;
				v = getButton(5).getY() + 3;
			}
			GlStateManager.pushMatrix();
			GlStateManager.translate(u, v, 100.0f);
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
			minecraft.getTextureManager().bindTexture(SHEET);
			drawTexturedModalRect(-1, -1, 34, 20, 11, 11);
			minecraft.getTextureManager().bindTexture(TABS);
			drawTexturedModalRect(0, 0, 116 + quest.extraButton * 9, 0, 9, 9);
			GlStateManager.popMatrix();
		}
		// quest icon
		u = guiLeft + 214;
		v = guiTop + 4;
		if (getButton(0) != null) {
			u = getButton(0).getX() + getButton(0).getWidth() + 5;
			v = getButton(0).getY() - 1;
		}
		GlStateManager.pushMatrix();

		GlStateManager.translate(u + 1.0f, v + 1.0f, 1.0f);
		drawRect(-1, -1,  33, 33, color);
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		minecraft.getTextureManager().bindTexture(SHEET);
		drawTexturedModalRect(0, 0, 34, 54, 32, 32);
		GlStateManager.popMatrix();

		if (quest.icon != null) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(u + 1.0f, v + 1.0f, 1.0f);
			GlStateManager.scale(0.125f, 0.125f, 1.0f);
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
			minecraft.getTextureManager().bindTexture(quest.icon);
			drawTexturedModalRect(0, 0, 0, 0, 256, 256);
			GlStateManager.popMatrix();
		}

		// quest texture
		u = guiLeft + 214;
		v = guiTop + 38;
		if (getButton(3) != null) {
			u = getButton(3).getX() + getButton(3).getWidth() + 5;
			v = getButton(3).getY() - 1;
		}
		GlStateManager.pushMatrix();
		GlStateManager.translate(u + 1.0f, v + 1.0f, 1.0f);
		drawRect(-1, -1, 33, 33, color);
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		minecraft.getTextureManager().bindTexture(SHEET);
		drawTexturedModalRect(0, 0, 34, 54, 32, 32);
		GlStateManager.popMatrix();

		if (quest.texture != null) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(u + 1.0f, v + 1.0f, 1.0f);
			GlStateManager.scale(0.125f, 0.125f, 1.0f);
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
			minecraft.getTextureManager().bindTexture(quest.texture);
			drawTexturedModalRect(0, 0, 0, 0, 256, 256);
			GlStateManager.popMatrix();
		}
		if (tempHoverText != null) { setHoverText(tempHoverText); }
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public void initGui() {
		super.initGui();
		sw = new ScaledResolution(mc);
		int x0 = guiLeft + 5;
		int x1 = x0 + 110;
		int x2 = x1 + 34;
		int y = guiTop + 5;
		int lId = 0;
		// icon
		addLabel(lId++, x0, y + 2, "quest.icon")
				.setSize(142, 10);
		addButton(0, x2, y, "availability.select")
				.setSize(60, 14)
				.setHoverTexts("quest.hover.edit.quest.icon.sel");
		addTextField(0, x0, y += 16, 203, 16, quest.icon.toString())
				.setHoverTexts("quest.hover.edit.quest.icon.path");
		// texture description
		addLabel(lId++, x0, (y += 18) + 2, "quest.texture")
				.setSize(142, 10);
		addButton(3, x2, y, "availability.select")
				.setSize(60, 14)
				.setHoverTexts("quest.hover.edit.quest.texture.sel");
		addTextField(1, x0, y += 16, 203, 16, quest.texture)
				.setHoverTexts("quest.hover.edit.quest.texture.path");
		// completion npc
		addButton(1, x0, y += 19, false, quest.completion.ordinal(), "quest.npc", "quest.instant")
				.setSize(100, 14)
				.setHoverTexts("quest.hover.edit.quest.completion");
		addButton(2, x1, y, "availability.select")
				.setSize(60, 14)
				.setHoverTexts("quest.hover.edit.quest.completion.npc");
		addCheckBox(9, guiLeft + 5, y += 18, "quest.completer.strict.true", "quest.completer.strict.false", quest.completer.isStrict())
				.setSize(170, 14)
				.setHoverTexts("quest.hover.completer.strict");
		// reward text
		addLabel(lId++, guiLeft + 5, (y += 16) + 2, "quest.questrewardtext")
				.setSize(108, 10);
		addButton(4, x1, y, quest.rewardText.isEmpty() ? "selectServer.edit" : "advanced.editing mode")
				.setSize(60, 14)
				.setHoverTexts("quest.hover.edit.reward.text");
		// extra button
		addLabel(lId++, guiLeft + 5, (y += 16) + 2, "quest.extra.button.type")
				.setSize(108, 10);
		addButton(5, x1, y, true, quest.extraButton, "gui.none", "1", "2", "3", "4", "5")
				.setSize(60, 14)
				.setHoverTexts("quest.hover.extra.button.type", EnumScriptType.QUEST_LOG_BUTTON.function);
		// extra button text
		addLabel(lId, guiLeft + 5, (y += 16) + 2, "quest.extra.button.text")
				.setSize(108, 10);
		addButton(6, x1, y, "selectServer.edit")
				.setSize(60, 14)
				.setIsEnabled(quest.extraButton > 0).setHoverTexts("quest.hover.extra.button.text");
		// progress in chat / window
		addCheckBox(7, x0, (y += 17), "quest.show.progress.in.chat", null, quest.showProgressInChat)
				.setSize(242, 14)
				.setHoverTexts("quest.hover.show.in.chat");
		addCheckBox(8, x0, y + 16, "quest.show.progress.in.window", null, quest.showProgressInWindow)
				.setSize(242, 14)
				.setHoverTexts("quest.hover.show.in.window");
		// exit
		addButton(66, x0, guiTop + imageHeight - 19, "gui.done")
				.setSize(60, 14)
				.setHoverTexts("hover.back");
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		boolean bo = super.mouseClicked(mouseX, mouseY, mouseButton);
		if (!hasSubGui()) {
			int u = guiLeft + 214, v = guiTop + 5;
			if (getButton(0) != null) {
				u = getButton(0).getX() + getButton(0).getWidth() + 6;
				v = getButton(0).getY();
			}
			if (isMouseHover(mouseX, mouseY, u, v, 32, 32)) {
				setSubGui(new SubGuiTextureSelection(this, 0, showNpc, quest.icon.toString(), ".png", 3));
				return bo;
			}
			v = guiTop + 37;
			if (getButton(3) != null) {
				u = getButton(3).getX() + getButton(3).getWidth() + 6;
				v = getButton(3).getY();
			}
			if (isMouseHover(mouseX, mouseY, u, v, 32, 32)) {
				setSubGui(new SubGuiTextureSelection(this,1, showNpc, quest.texture == null ? "" : quest.texture.toString(), ".png", 3));
				return bo;
			}
			if (isMouseHover(mouseX, mouseY, guiLeft + 182, guiTop + 95, 65, 65)) {
				setSubGui(new SubGuiNPCSelection(quest.completer.getNpc()));
			}
		}
		return bo;
	}

	@Override
	public void subGuiClosed(GuiScreen subgui) {
		if (subgui instanceof GuiTextAreaScreen) {
			GuiTextAreaScreen gui = (GuiTextAreaScreen) subgui;
			if (gui.id == 0) { quest.rewardText = gui.text; }
			else if (gui.id == 1) { quest.extraButtonText = gui.text; }
			initGui();
		}
		else if (subgui instanceof SubGuiTextureSelection) {
			if (((SubGuiTextureSelection) subgui).id == 0) {
				quest.icon = ((SubGuiTextureSelection) subgui).resource;
				if (quest.icon == null) { quest.icon = new ResourceLocation(CustomNpcs.MODID, "textures/quest icon/q_0.png"); }
			}
			else { quest.texture = ((SubGuiTextureSelection) subgui).resource; }
			initGui();
		}
		else if (subgui instanceof SubGuiNPCSelection) {
			if (((SubGuiNPCSelection) subgui).selectEntity == null) {
				return;
			}
			Entity entity = player.world.getEntityByID(((SubGuiNPCSelection) subgui).selectEntity.getEntityId());
			if (entity instanceof EntityNPCInterface) {
				quest.completer.reset((EntityNPCInterface) entity);
				showNpc = quest.completer.getNpc();
				initGui();
			}
		}
	}

	@Override
	public void unFocused(GuiTextFieldNop textField) {
		if (textField.id == 0) {
			if (textField.getValue().isEmpty()) { quest.icon = new ResourceLocation(CustomNpcs.MODID, "textures/quest icon/q_0.png"); }
			else { quest.icon = new ResourceLocation(textField.getValue()); }
			textField.setValue(quest.icon.toString());
		}
		else if (textField.id == 1) {
			if (textField.getValue().isEmpty()) { quest.texture = null; }
			else { quest.texture = new ResourceLocation(textField.getValue()); }
			textField.setValue(quest.texture == null ? "" : quest.texture.toString());
		}
	}

}
