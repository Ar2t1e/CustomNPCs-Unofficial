package noppes.npcs.client.gui.player.companion;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import net.minecraft.util.math.MathHelper;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketCompanionOpenInv;
import noppes.npcs.packets.server.SPacketNpcRoleGet;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiMenuTopButton;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.constants.EnumCompanionJobs;
import noppes.npcs.constants.EnumCompanionTalent;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.shared.client.gui.listeners.IGuiData;

public class GuiNpcCompanionStats extends GuiNPCInterface implements IGuiData {

	public static void addTopMenu(RoleCompanion role, GuiScreen screen, int active) {
		GuiMenuTopButton button;
		if (screen instanceof IGuiInterface) {
			IGuiInterface gui = (IGuiInterface) screen;
			button = gui.addTopButton(1, gui.getX() + 4, gui.getY() - 27,
							Component.translatable("menu.stats"), new ItemStack(Items.BOOK))
					.setIsEnabled(active == 1);
			button = gui.addTopButton(2, button.getX() + button.getWidth(), button.getY(),
							Component.translatable("companion.talent"), new ItemStack(Items.NETHER_STAR))
					.setIsEnabled(active == 2);
			if (role.hasInv()) {
				button = gui.addTopButton(3, button.getX() + button.getWidth(), button.getY(),
								Component.translatable("inv.inventory"), new ItemStack(Blocks.CHEST))
						.setIsEnabled(active == 3);
			}
			if (role.job.getType() != EnumCompanionJobs.NONE) {
				gui.addTopButton(4, button.getX() + button.getWidth(), button.getY(),
								Component.translatable("job.name"), new ItemStack(Items.CARROT))
						.setIsEnabled(active == 4);
			}
		}
	}

	protected final RoleCompanion role;
	protected boolean isEating = false;

	public GuiNpcCompanionStats(EntityNPCInterface npc) {
		super(npc);
		setBackground("companion.png");
		imageWidth = 171;
		imageHeight = 166;

		role = (RoleCompanion)npc.role;
		Packets.sendServer(new SPacketNpcRoleGet());
	}

	@Override
	public void buttonEvent(GuiButtonNop button) {
		switch (button.id) {
			case 2: {
				NoppesUtilServer.setEditingNpc(player, npc);
				CustomNpcs.proxy.openGui(npc, EnumGuiType.CompanionTalent, null);
				break;
			}
			case 3: Packets.sendServer(new SPacketCompanionOpenInv()); break;
		}
	}

	public void drawHealth(int y) {
		mc.getTextureManager().bindTexture(GuiNpcCompanionStats.ICONS);
		int max = role.getTotalArmorValue();
		if (role.talents.containsKey(EnumCompanionTalent.ARMOR) || max > 0) {
			for (int i = 0; i < 10; ++i) {
				int x = guiLeft + 66 + i * 10;
				if (i * 2 + 1 < max) { drawTexturedModalRect(x, y, 34, 9, 9, 9); }
				if (i * 2 + 1 == max) { drawTexturedModalRect(x, y, 25, 9, 9, 9); }
				if (i * 2 + 1 > max) { drawTexturedModalRect(x, y, 16, 9, 9, 9); }
			}
			y += 10;
		}
		max = MathHelper.ceil(npc.getMaxHealth());
		int k = (int) npc.getHealth();
		float scale;
		if (max > 40) {
			scale = max / 40.0f;
			k /= (int) scale;
			max = 40;
		}
		for (int j = 0; j < max; ++j) {
			int x2 = guiLeft + 66 + j % 20 * 5;
			int offset = j / 20 * 10;
			drawTexturedModalRect(x2, y + offset, 52 + j % 2 * 5, 9, (j % 2 == 1) ? 4 : 5, 9);
			if (k > j) { drawTexturedModalRect(x2, y + offset, 52 + j % 2 * 5, 0, (j % 2 == 1) ? 4 : 5, 9); }
		}
		k = role.foodstats.getFoodLevel();
		y += 10;
		if (max > 20) { y += 10; }
		for (int j = 0; j < 20; ++j) {
			int x2 = guiLeft + 66 + j % 20 * 5;
			drawTexturedModalRect(x2, y, 16 + j % 2 * 5, 27, (j % 2 == 1) ? 4 : 5, 9);
			if (k > j) { drawTexturedModalRect(x2, y, 52 + j % 2 * 5, 27, (j % 2 == 1) ? 4 : 5, 9); }
		}
	}

	@Override
	public void drawScreen(int mouseXIn, int mouseYIn, float partialTicks) {
		super.drawScreen(mouseXIn, mouseYIn, partialTicks);
		if (isEating && !role.isEating()) { Packets.sendServer(new SPacketNpcRoleGet()); }
		isEating = role.isEating();
		super.drawNpc(34, 150);
		drawHealth(guiTop + 88);
	}

	@Override
	public void initGui() {
		super.initGui();
		int x = guiLeft + 4;
		int y = guiTop + 10;
		addLabel(0, x, y, Component.translatable("gui.name").append(": ")
				.append(npc.display.getName()));
		addLabel(1, x, y += 12, Component.translatable("companion.owner").append(": ")
				.append(role.ownerName));
		addLabel(2, x, y += 12, Component.translatable("companion.age").append(": ")
				.append("" + role.ticksActive / 18000L)
				.append(" (").append(role.stage.name).append(")"));
		addLabel(3, x, y += 12, Component.translatable("companion.strength").append(": ")
				.append("" + npc.stats.melee.getStrength()));
		addLabel(4, x, y += 12, Component.translatable("companion.level").append(": ")
				.append("" + role.getTotalLevel()));
		addLabel(5, x, y + 12, Component.translatable("job.name").append(": ")
				.append(Component.translatable("gui.none")));
		addTopMenu(role, this, 1);
	}

    @Override
	public void setGuiData(NBTTagCompound compound) { role.load(compound); }

}
