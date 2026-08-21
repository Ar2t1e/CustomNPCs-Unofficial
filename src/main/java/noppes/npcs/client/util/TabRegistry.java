package noppes.npcs.client.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.client.gui.player.tabs.AbstractTab;
import noppes.npcs.client.gui.player.tabs.InventoryTabVanilla;
import noppes.npcs.shared.common.util.LogWriter;

public class TabRegistry {

	private static Class<?> clazzJEIConfig = null;
	public static Class<?> clazzNEIConfig = null;
	private static boolean initWithPotion;
	private static final Minecraft mc = FMLClientHandler.instance().getClient();
	private static final ArrayList<AbstractTab> tabList = new ArrayList<>();

	static {
		try {
			clazzJEIConfig = Class.forName("mezz.jei.config.Config");
		} catch (Exception e) { LogWriter.info("Mezz Config is missed:"); }
		if (clazzJEIConfig == null) {
			try {
				clazzNEIConfig = Class.forName("codechicken.nei.NEIClientConfig");
			}
			catch (Exception ee) { LogWriter.info("Code Chicken Config is missed:"); }
		}
	}

	public static void addTabsToList(List<GuiButton> buttonList) {
		for (AbstractTab tab : tabList) {
			if (tab.shouldAddToList()) {
				buttonList.add(tab);
			}
		}
	}

	public static int getPotionOffset() {
		if (!mc.player.getActivePotionEffects().isEmpty()) {
			initWithPotion = true;
			return 60 + getPotionOffsetJEI() + getPotionOffsetNEI();
		}
		initWithPotion = false;
		return 0;
	}

	public static int getPotionOffsetJEI() {
		if (clazzJEIConfig != null) {
			try {
				Object enabled = clazzJEIConfig.getMethod("isOverlayEnabled", new Class[0]).invoke(null);
				if (enabled instanceof Boolean) {
					if (!(boolean) enabled) {
						return 0;
					}
					return -60;
				}
			} catch (Exception e) { LogWriter.error(e); }
		}
		return 0;
	}

	public static int getPotionOffsetNEI() {
		if (initWithPotion && clazzNEIConfig != null) {
			try {
				Object hidden = clazzNEIConfig.getMethod("isHidden", new Class[0]).invoke(null);
				Object enabled = clazzNEIConfig.getMethod("isEnabled", new Class[0]).invoke(null);
				if (hidden instanceof Boolean && enabled instanceof Boolean) {
					if ((boolean) hidden || !(boolean) enabled) {
						return 0;
					}
					return -60;
				}
			} catch (Exception e) { LogWriter.error(e); }
		}
		return 0;
	}

	public static ArrayList<AbstractTab> getTabList() { return tabList; }

	public static void registerTab(AbstractTab tab) {
		for (AbstractTab t : tabList) {
			if (t.getClass() == tab.getClass()) {
				return;
			}
		}
		tabList.add(tab);
	}

	public static void updateTabValues(int guiLeft, int guiTop, Class<?> selectedButton) {
		int count = 100;
        for (AbstractTab t : tabList) {
            if (t.shouldAddToList()) {
                t.id = count;
                t.x = guiLeft + (count - 100) * 28;
                t.y = guiTop - 28;
                t.enabled = !t.getClass().equals(selectedButton);
                t.potionOffsetLast = getPotionOffsetNEI();
                ++count;
            }
        }
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void guiPostInit(GuiScreenEvent.InitGuiEvent.Post event) {
		if (event.getGui() instanceof GuiInventory) {
			int guiLeft = (event.getGui().width - 176) / 2;
			int guiTop = (event.getGui().height - 166) / 2;
			guiLeft += getPotionOffset();
			updateTabValues(guiLeft, guiTop, InventoryTabVanilla.class);
			addTabsToList(event.getButtonList());
		}
	}
}
