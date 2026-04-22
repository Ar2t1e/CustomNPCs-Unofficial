package noppes.npcs.shared.client.gui.listeners;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.shared.client.gui.components.*;

import javax.annotation.Nullable;
import java.util.List;

@SideOnly(Side.CLIENT)
public interface IGuiInterface {

	void add(IComponentGui element);

	void buttonEvent(GuiButtonNop button);

	boolean mouseButtonEvent(GuiButtonNop button, int mouseButton);

	void save();

	boolean hasSubGui();

	GuiScreen getSubGui();

	int getWidth();

	int getHeight();

	GuiScreen getParent();

	void subGuiClosed(GuiScreen screen);

	GuiWrapper getWrapper();

	// New fields from Unofficial (BetaZavr)
	void drawHoverText(String text, Object... args);

	void drawWait();

	List<Component> getHoverText();

	boolean isMouseHover(double mX, double mY, double px, double py, double pwidth, double pheight);

	void setHoverText(@Nullable List<Component> components);

	void setHoverText(Object... components);

	IComponentGui get(int id);

	GuiLabel addLabel(int id, int x, int y, Object label);

	GuiButtonNop addButton(int id, int x, int y, Object label);

	GuiButtonNop addButton(int id, int x, int y, boolean isBiDirectional,  int variant, Object... variants);

	GuiCheckBoxNop addCheckBox(int id, int x, int y, Object labelTrue, Object labelFalse, boolean selected);

	GuiMenuTopButton addTopButton(int id, int x, int y, Object label);

	GuiMenuTopIconButton addTopButton(int id, int x, int y, Object label, ItemStack stack);

	GuiMenuSideButton addSideButton(int id, int x, int y, Object label);

	GuiButtonYesNo addYesNo(int id, int x, int y, boolean isYes);

	GuiSliderNop addSlider(int id, int x, int y, float sliderValue);

	GuiTextFieldNop addTextField(int id, int x, int y, int width, int height, Object value);

	GuiCustomScrollNop addScroll(int id);

	GuiCustomScrollNop addScroll(int id, boolean isMultipleSelection);

	void extraEvent(Object extra);

	int getX();

	int getY();

	// for 1.12.2
	void preDrawScreen(int mouseX, int mouseY);

	boolean mouseScrolled(double mouseX, double mouseY, double scrolled);

	boolean mouseClicked(double mouseX, double mouseY, int mouseButton);

	boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dx, double dy);

	boolean mouseReleased(double x, double y, int mouseButton);

	IComponentGui getLastFocused();

	void focusedNextComponent();

	void focusedPrevComponent();

	void onClose();

	boolean keyPressed(char typedChar, int keyCode);

}
