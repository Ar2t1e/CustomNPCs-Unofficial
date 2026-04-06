package noppes.npcs.shared.client.gui.components;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.renderer.obj.ModelBuffer;
import noppes.npcs.mixin.client.IMouseHandlerMixin;
import noppes.npcs.shared.client.gui.util.ResourceData;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IComponentGui;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;
import noppes.npcs.shared.common.util.ComponentOrderComparator;
import noppes.npcs.util.Util;
import noppes.npcs.util.ValueUtil;

import javax.annotation.Nonnull;

// Change from Unofficial (BetaZavr)
public class GuiCustomScrollNop extends Screen implements IComponentGui {

   public static final ResourceLocation resource = new ResourceLocation(CustomNpcs.MODID, "textures/gui/misc.png");
   // standard
   protected int x = 0;
   protected int y = 0;
   // main
   public int id;
   protected int hover = -1;
   protected Object listener;
   // data
   protected final List<Component> list = new ArrayList<>();
   protected final List<Integer> selectedList = new ArrayList<>();
   protected boolean selectable = true;
   protected int listSize = 0;
   protected int selected = -1;
   public boolean multipleSelection = false;
   // scroll vars
   protected final GuiTextFieldNop textField = new GuiTextFieldNop(null, 0, 1, 1, 175, 18, "");
   protected int listHeight = 0;
   protected int scrollY = 0;
   protected int maxScrollY;
   protected int scrollHeight = 0;
   protected boolean isSorted = true;
   protected boolean mouseInList = false;
   protected int lastClickedItem = -1;
   protected long lastClickedTime = 0L;
   protected boolean hasSearch = true;
   protected String searchStr = "";
   protected String[] searchWords = new String[0];
   protected boolean focused = false;
   protected boolean enabled = true;
   public boolean visible = true;

   // New from Unofficial (BetaZavr)
   protected final Map<Integer, List<Component>> hoversTexts = new TreeMap<>();
   protected ClientProxy.FontContainer customFont = null;
   protected List<Component> hoverText = new ArrayList<>();
   protected List<Component> suffixes;
   protected List<ResourceData> prefixes;
   protected List<ItemStack> stacks = null;
   protected boolean isScrolling = false;
   public int colorBackS = 0xC0101010;
   public int colorBackE = 0xE0101010;
   public int border = 0xFF000000;

   public GuiCustomScrollNop(Object parent, int idIn) {
      super(Component.empty());
      id = idIn;
      width = 176;
      height = 159;
      listener = parent;
      minecraft = Minecraft.getInstance();
      font = minecraft.font;
   }

   public GuiCustomScrollNop(Object parent, int id, boolean isMultipleSelection) {
      this(parent, id);
      multipleSelection = isMultipleSelection;
   }

   public GuiCustomScrollNop setSize(int x, int y) {
      textField.setWidth(x - 2);
      height = y - textFieldHeight();
      width = x;
      listHeight = 14 * listSize;
      if (listHeight > 0) { scrollHeight = (int)((double)(height - 2) / (double)listHeight * (double)(height - 2)); }
      else { scrollHeight = Integer.MAX_VALUE; }
      maxScrollY = listHeight - (height - 2) - 1;
      resetRoll();
      return this;
   }

   public GuiCustomScrollNop disabledSearch() {
      hasSearch = false;
      return this;
   }

   private int textFieldHeight() { return hasSearch ? 22 : 0; }

   private void reset() {
      if (searchWords.length == 0) { listSize = list.size(); }
      else { listSize = (int)list.stream().filter((line) -> isSearched(line.getString())).count(); }
      setSize(width, height + textFieldHeight());
      if (selected >= 0 && selected >= list.size()) { selected = -1; }
   }

   private boolean isSearched(String s) {
      String line = s.toLowerCase();
      for (String k : searchWords) {
         if (!line.contains(k)) { return false; }
      }
      return true;
   }

   public int getWidth() { return width; }

   public int getHeight() { return height + textFieldHeight(); }

   @Override
   public int getId() { return id; }

   @Override
   public boolean isEnabled() { return enabled; }

   @Override
   public boolean isVisible() { return visible; }

   public boolean isHovered() { return mouseInList; }

   @Override
   public void moveTo(int addX, int addY) {
      x += addX;
      y += addY;
   }

   @Override
   public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      if (!visible) { return; }
      if (minecraft == null) { minecraft = Minecraft.getInstance(); }
      if (hasSearch) {
         textField.setX(x + 1);
         textField.setY(y + 1);
         textField.render(graphics, mouseX, mouseY, partialTicks);
      }
      y += textFieldHeight();
      mouseInList = isMouseOver(mouseX, mouseY);

      // add elements
      boolean parentAllows = !(listener instanceof IGuiInterface gui) || !gui.hasSubGui();
      if (parentAllows) {
         if (prefixes != null) { drawPrefixes(graphics); }
         if (stacks != null) { drawStacks(graphics); }
      }

      // background
      if (border != 0xFF000000) { graphics.fillGradient(x - 1, y - 1, width + x + 1, height + y + 1, border, border); }
      if ((colorBackS >> 24 & 255) > 0 || (colorBackE >> 24 & 255) > 0) {
         graphics.fillGradient(x, y, width + x, height + y, colorBackS, colorBackE);
      }

      // draw scrolling
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      if (scrollHeight < height - 2) {
         double xPos = mouseX - x;
         double yPos = mouseY - y;
         float color = isScrolling ? 0.5f : xPos >= width - 10 && xPos < width - 1 && yPos >= 1 && yPos < height - 2 ? 0.75f : 1.0f;
         drawScrollBar(graphics, color);
      }

      // positions:
      PoseStack matrixStack = graphics.pose();
      matrixStack.pushPose();
      if (selectable) { hover = getMouseOver(mouseX, mouseY); }
      drawItems(graphics);
      matrixStack.popPose();

      // scrolling pos
      if (scrollHeight < height - 2) {
         mouseY -= y;
         if (isScrolling) {
            isScrolling = ((IMouseHandlerMixin) minecraft.mouseHandler).getActiveButton() == 0;
            if (isScrolling) {
               scrollY = (mouseY - 2) * listHeight / (height - 2) - scrollHeight;
               if (scrollY < 0) { scrollY = 0; }
               if (scrollY > maxScrollY) { scrollY = maxScrollY; }
            }
         }
      }
      if (listener instanceof IGuiInterface gui) {
         if (mouseInList && !hoverText.isEmpty()) { gui.setHoverText(hoverText); }
         else if (hover >= 0 && hover < list.size() && parentAllows) {
            if (hoversTexts.containsKey(hover)) { gui.setHoverText(hoversTexts.get(hover)); }
            else if (stacks != null && hover < stacks.size() && minecraft != null) {
               gui.setHoverText(stacks.get(hover).getTooltipLines(minecraft.player, minecraft.options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL));
            }
         }
      }
      y -= textFieldHeight();
   }

   @Override
   public int[] getCenter() { return new int[] { x + width / 2, y + height  / 2}; }

   @Override
   public List<Component> getHoversText() { return hoverText; }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double mouseScrolled) {
      if (visible && isHovered() && mouseScrolled != 0.0D && mouseInList) {
         scrollY += mouseScrolled > 0.0D ? -14 : 14;
         if (scrollY > maxScrollY) { scrollY = maxScrollY; }
         if (scrollY < 0) { scrollY = 0; }
         return true;
      }
      return false;
   }

   public void mouseForcedScrolled(double mouseScrolled) {
      if (visible && mouseScrolled != 0.0D && mouseInList) {
         scrollY += mouseScrolled > 0 ? -14 : 14;
         if (scrollY > maxScrollY) { scrollY = maxScrollY; }
         if (scrollY < 0) { scrollY = 0; }
      }
   }

   public boolean mouseInOption(int mouseX, int mouseY, int displayIndex) {
      int xOffset = scrollHeight < height - 2 ? 10 : 0;
      int posX = 4;
      int posY = 14 * displayIndex + 4 - scrollY;
      return mouseX >= posX - 1 && mouseX < width - 2 - xOffset && mouseY >= posY - 1 && mouseY < posY + 8;
   }

   protected void drawItems(GuiGraphics graphics) {
      if (minecraft == null) { minecraft = Minecraft.getInstance(); }
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      int xOffset = listHeight < height - 2 ? 0 : 10;
      int displayIndex = 0;
      int alpha = 255 << 24;
      for(int i = 0; i < list.size(); ++i) {
         if (!isSearched(list.get(i).getString())) { continue; }
         int left = x + 3;
         int top = 14 * displayIndex + 4 - scrollY;
         ++displayIndex;
         if (top < 4 || top + 10 >= height) { continue; }
         top += y;
         int r = left + width - 5 - xOffset;
         Component displayString = list.get(i) == null ? Component.literal("null") : list.get(i);
         //Component text;
         // add bz
         // add bz
         if ((stacks != null && i < stacks.size()) || (prefixes != null && i < prefixes.size())) { left += 10; }
         // main
         int right = r - 1;
         // add bz
         if (suffixes != null && i < suffixes.size() && suffixes.get(i) != null && !suffixes.get(i).getString().isEmpty()) {
            right -= 1 + font.width(suffixes.get(i).getString());
            graphics.drawString(font, suffixes.get(i), right, top, (i == hover ? CustomNpcs.HoverColor.getRGB() : CustomNpcs.MainColor.getRGB()));
         }
         if (multipleSelection && selectedList.contains(i) || !multipleSelection && selected == i) {
            graphics.vLine(left - 2, top - 4, top + 10, -1);
            graphics.vLine(r, top - 4, top + 10, -1);
            graphics.hLine(left - 2, r, top - 3, -1);
            graphics.hLine(left - 2, r, top + 10, -1);
            //graphics.drawString(font, text, left, top, color);
            GuiButtonNop.renderString(graphics, displayString, left, top, right, top + 10,
                    CustomNpcs.MainColor.getRGB() | alpha, true, false, customFont);
         }
         else if (i == hover) {
            GuiButtonNop.renderString(graphics, displayString, left, top, right, top + 10,
                    CustomNpcs.HoverColor.getRGB() | alpha, true, false, customFont);
         }
         else {
            GuiButtonNop.renderString(graphics, displayString, left, top, right, top + 10,
                    CustomNpcs.MainColor.getRGB() | alpha, true, false, customFont);
         }
      }
   }

   public @Nonnull String getSelected() {
      return selected >= 0 && selected < list.size() ? list.get(selected).getString() : "";
   }

   public @Nonnull Component getNormalSelected() {
      return selected >= 0 && selected < list.size() ? list.get(selected) : Component.empty();
   }

   private int getMouseOver(int mouseX, int mouseY) {
      mouseX -= x;
      mouseY -= y;
      if (mouseX >= 4 && mouseX < width - 4 && mouseY >= 1 && mouseY < height - 2) {
         int displayIndex = 0;
         for(int index = 0; index < list.size(); ++index) {
            if (!isSearched(list.get(index).getString())) { continue; }
            if (mouseInOption(mouseX, mouseY, displayIndex)) { return index; }
            ++displayIndex;
         }
      }
      return -1;
   }

   @Override
   public boolean keyPressed(int key, int key_1, int key_2) {
      if (hasSearch && textField.isFocused()) {
         boolean bo = textField.keyPressed(key, key_1, key_2);
         if (!searchStr.equals(textField.getValue().toLowerCase())) {
            searchStr = textField.getValue().toLowerCase().trim();
            searchWords = searchStr.split(" ");
            if (selected >= 0 && !isSearched(list.get(selected).getString())) { selected = -1; }
            scrollY = 0;
            reset();
         }
         return bo;
      }
      if (list.size() <= 1) { return false; }
      boolean canPressed = GuiTextFieldNop.getActive() == null;
      if (canPressed && listener instanceof IGuiInterface gui && !gui.hasSubGui()) {
         canPressed = gui.getWrapper().onlyScroll == this || mouseInList;
      }
      if (canPressed) {
         if (minecraft == null) { minecraft = Minecraft.getInstance(); }
         if (key == InputConstants.getKey("key.keyboard.up").getValue()  || key == minecraft.options.keyUp.getKey().getValue()) { // up
            if (multipleSelection) { scrollY = ValueUtil.correctInt(scrollY - 14, 0, maxScrollY); }
            else {
               if (selected < 1) { return false; }
               selected--;
               resetRoll();
               if (listener instanceof ICustomScrollListener gui) { gui.scrollClicked(this); }
            }
            return true;
         }
         else if (key == InputConstants.getKey("key.keyboard.down").getValue() || key == minecraft.options.keyDown.getKey().getValue()) { // down
            if (multipleSelection) { scrollY = ValueUtil.correctInt(scrollY + 14, 0, maxScrollY); }
            else {
               if (selected >= getList().size() - 1) { return false; }
               selected++;
               resetRoll();
               if (listener instanceof ICustomScrollListener gui) { gui.scrollClicked(this); }
            }
            return true;
         }
      }
      return false;
   }

   @Override
   public boolean keyReleased(int p_94715_, int p_94716_, int p_94717_) {
      return super.keyReleased(p_94715_, p_94716_, p_94717_);
   }

   @Override
   public boolean charTyped(char c, int keyId) {
      if (hasSearch) {
         boolean bo = textField.charTyped(c, keyId);
         if (!searchStr.equals(textField.getValue().toLowerCase())) {
            searchStr = textField.getValue().toLowerCase().trim();
            searchWords = searchStr.split(" ");
            if (selected >= 0 && !isSearched(list.get(selected).getString())) { selected = -1; }
            scrollY = 0;
            reset();
         }
         return bo;
      } else {
         return super.charTyped(c, keyId);
      }
   }

   @Override
   public void setFocused(boolean isFocused) {
      setIsFocused(isFocused);
   }

   @Override
   public boolean isFocused() { return focused; }

   @Override
   public @Nonnull Optional<GuiEventListener> getChildAt(double p_94730_, double p_94731_) {
      return super.getChildAt(p_94730_, p_94731_);
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
      if (hasSearch) { textField.mouseClicked(mouseX, mouseY, mouseButton); }
      if (scrollHeight < height - 2) {
         double xPos = mouseX - x;
         double yPos = mouseY - y;
         if (hasSearch) { yPos -= 24; }
         isScrolling = xPos >= width - 10 && xPos < width - 1 && yPos >= 1 && yPos < height - 2;
         if (isScrolling) { return true; }
      }
      if (mouseButton != 0 || hover < 0) { return false; }
      boolean clicked = true;
      if (multipleSelection) {
         if (selectedList.contains(hover)) { selectedList.removeIf(value -> value == hover); }
         else { selectedList.add(hover); }
      }
      else {
         clicked = selected != hover;
         selected = hover;
         hover = -1;
      }
      if (clicked && listener instanceof ICustomScrollListener gui) { gui.scrollClicked(this); }
      long time = System.currentTimeMillis();
      if (listener instanceof ICustomScrollListener gui && selected >= 0 && selected == lastClickedItem && time - lastClickedTime < 500L) {
         gui.scrollDoubleClicked(this);
      }
      lastClickedTime = time;
      lastClickedItem = selected;
      return true;
   }

   @Override
   public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dx, double dy) {
      super.mouseDragged(mouseX, mouseY, mouseButton, dx, dy);
      return scrollHeight < height - 2 && mouseInList;
   }

   private void drawScrollBar(GuiGraphics graphics, float color) {
      RenderSystem.setShaderTexture(0, resource);
      PoseStack matrixStack = graphics.pose();
      matrixStack.pushPose();
      RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0F);
      matrixStack.translate(x + width - 10, y, 0.0f);
      int h0 = height / 2;
      int h1 = height - h0;
      graphics.blit(resource, 0, 0, 0, 0, 10, h0);
      graphics.blit(resource, 0, h0, 0, 256 - h1, 10, h1);
      matrixStack.popPose();

      h0 = (scrollHeight - 1) / 2;
      h1 = scrollHeight - h0;
      matrixStack.pushPose();
      matrixStack.translate(x + width - 9.0f, y + (int) ((float) scrollY / (float) listHeight * (float)(height - 2)) + 1.0f, 0.0f);
      RenderSystem.setShaderColor(color, color, color, 1.0F);
      graphics.blit(resource, 0, 0, 10, 0, 8, h0);
      graphics.blit(resource, 0, h0, 10, 256 - h1, 8, h1);
      matrixStack.popPose();
   }

   public boolean hasSelected() {
      return selected >= 0;
   }

   public GuiCustomScrollNop setList(List<String> newList) {
      List<Component> list = new ArrayList<>();
      for (String line : newList) { list.add(Component.translatable(line)); }
      setNormalList(list);
      return this;
   }

   public GuiCustomScrollNop setNormalList(List<Component> newList) {
      if (!isSameList(newList)) {
         isSorted = true;
         scrollY = 0;
         newList.sort(new ComponentOrderComparator());
         list.clear();
         list.addAll(newList);
         reset();
      }
      return this;
   }

   public GuiCustomScrollNop setUnsortedList(List<Component> newList) {
      if (isSameList(newList)) { return this; }
      isSorted = false;
      list.clear();
      list.addAll(newList);
      reset();
      return this;
   }

   private boolean isSameList(List<Component> checklist) {
      if (list.size() != checklist.size()) { return false; }
      List<String> main = new ArrayList<>();
      for (Component component : list) { main.add(component.getString()); }
      List<String> check = new ArrayList<>();
      for (Component component : checklist) { check.add(component.getString()); }
      for (int i = 0; i < check.size(); i++) {
         String line = main.get(i);
         if (!check.contains(line) || !check.get(i).equalsIgnoreCase(line)) { return false; }
      }
      return true;
   }

   public void replace(Component old, Component newLine) {
      int i = 0;
      for (Component line : new ArrayList<>(list)) {
         if (Util.instance.deleteColor(line.getString()).equals(old.getString())) {
            list.remove(line);
            list.add(i, newLine);
            if (isSorted) { list.sort(new ComponentOrderComparator()); }
            reset();
            break;
         }
         i++;
      }
   }

   public void replace(String old, String newLine) { replace(Component.literal(old), Component.literal(newLine)); }

   public GuiCustomScrollNop setSelected(String line) {
      int i = 0;
      selected = -1;
      for (Component l : list) {
         if (Util.instance.equalsDeleteColor(l.getString(), line, false)) {
            selected = i;
            break;
         }
         i++;
      }
      return this;
   }

   public GuiCustomScrollNop setSelected(Component line) {
      if (list.contains(line)) { selected = list.indexOf(line); }
      else {
         selected = -1;
         int i = 0;
         for (Component l : list) {
            if (line.getString().equals(l.getString())) {
               selected = i;
               break;
            }
            i++;
         }
      }
      return this;
   }

   public void clear() {
      list.clear();
      selected = -1;
      scrollY = 0;
      searchStr = "";
      searchWords = new String[0];
      textField.setValue("");
      reset();
   }

   public void clearSelection() {
      selectedList.clear();
      selected = -1;
   }

   public List<String> getList() {
      List<String> retList = new ArrayList<>();
      for (Component line : list) { retList.add(line.getString()); }
      return retList;
   }

   public List<Component> getSelectedList() {
      return IntStream.range(0, list.size())
              .filter(selectedList::contains)
              .mapToObj(list::get)
              .collect(Collectors.toList());
   }

   public GuiCustomScrollNop setSelectedList(HashSet<Component> newSelectedList) {
      int i = 0;
      selectedList.clear();
      for (Component line : list) {
         for (Component component : newSelectedList) {
            if (line.getString().equals(component.getString())) {
               selectedList.add(i);
               break;
            }
         }
         i++;
      }
      return this;
   }

   public void setSelectedList(Collection<String> newSelectedList) {
      int i = 0;
      selectedList.clear();
      for (Component line : list) {
         for (String str : newSelectedList) {
            if (line.getString().equals(str)) {
               selectedList.add(i);
               break;
            }
         }
         i++;
      }
   }

   public GuiCustomScrollNop setUnselectable() {
      selectable = false;
      return this;
   }

   public void scrollTo(String name) {
      int i = 0;
      for (Component line : list) {
         if (line.getString().equals(name)) {
            if (i >= 0 && scrollHeight < height - 2) {
               int pos = (int)((float) i / (float)list.size() * (float)listHeight);
               if (pos > maxScrollY) { pos = maxScrollY; }
               scrollY = pos;
            }
            break;
         }
         i++;
      }
   }

   public void scrollTo(Component name) {
      if (name == null) { return; }
      int i = list.indexOf(name);
      if (i >= 0 && scrollHeight < height - 2) {
         int pos = (int) ((float) i / (float) listSize * (float) listHeight);
         if (pos > maxScrollY) { pos = maxScrollY; }
         scrollY = pos;
      }
   }

   public void resetRoll() {
      if (selected < 0 || scrollHeight >= height - 2) { return; }
      int pos = (int)((float) selected / (float) list.size() * (float) listHeight);
      if (pos < scrollY) { scrollY = pos; }
      else {
         while (pos >= scrollY + height - 14) { scrollY += 14; }
         if (scrollY > maxScrollY) { scrollY = maxScrollY; }
      }
   }

   public boolean isMouseOver(int xPos, int yPos) {
      return xPos >= x && xPos <= x + width && yPos >= y && yPos <= y + height;
   }

   public int getSelectedIndex() {
      return selected;
   }

   public GuiCustomScrollNop setSelectedIndex(int i) {
      selected = i < 0 ? -1 : i >= list.size() ? list.size() - 1 : i;
      return this;
   }

   // New fields from Unofficial (BetaZavr)
   public GuiCustomScrollNop setSelect(int index) {
      if (index < 0) { selected = -1; }
      else if (index < list.size()) { selected = index; }
      return this;
   }

   @SuppressWarnings("all")
   public Map<Integer, List<Component>> getHoversTexts() { return hoversTexts; }

   public GuiCustomScrollNop setHoverTexts(LinkedHashMap<Integer, List<Component>> map) {
      hoversTexts.clear();
      if (map == null || map.isEmpty()) { return this; }
      hoverText.clear();
      hoversTexts.putAll(map);
      return this;
   }

   public GuiCustomScrollNop setStacks(List<ItemStack> newStacks) { stacks = newStacks; return this; }

   public GuiCustomScrollNop setSuffixes(List<Component> newSuffixes) { suffixes = newSuffixes; return this; }

   @SuppressWarnings("all")
   public GuiCustomScrollNop setPrefixes(List<ResourceData> newPrefixes) { prefixes = newPrefixes; return this; }

   private void drawStacks(@Nonnull GuiGraphics graphics) {
      if (stacks == null || minecraft == null) { return; }
      int displayIndex = 0;
      PoseStack matrixStack = graphics.pose();
      for (int i = 0; i < list.size() && i < stacks.size(); ++i) {
         if (!isSearched(list.get(i).getString())) { continue; }
         int k = 14 * displayIndex + 4 - scrollY;
         displayIndex++;
         if (k < 4 || k + 10 > height) { continue; }
         matrixStack.pushPose();
         matrixStack.translate(x, y, 0.0f);
         matrixStack.translate(0, k - 2.5f, 300.0f);
         matrixStack.scale(0.75f, 0.75f, 0.75f);
         graphics.renderItem(stacks.get(i), 0, 0);
         matrixStack.popPose();
      }
   }

   private void drawPrefixes(@Nonnull GuiGraphics graphics) {
      if (prefixes == null || minecraft == null) { return; }
      int size = Math.min(list.size(), prefixes.size());
      if (size == 0) { return; }
      int displayIndex = 0;
      PoseStack matrixStack = graphics.pose();
      for (int i = 0; i < list.size() && i < prefixes.size(); ++i) {
         if (!isSearched(list.get(i).getString())) { continue; }
         ResourceData rd = prefixes.get(i);
         int k = 14 * displayIndex + 4 - scrollY;
         displayIndex++;
         if (rd == null || rd.resource == null || rd.width <= 0 || rd.height <= 0) { continue; }
         if (k < 4 || k + 12 >= height) { continue; }
         matrixStack.pushPose();
         if (rd.isOBJ()) {
            matrixStack.translate(x + 5.0f + rd.tW, y + k + 3.0f + rd.tH, rd.tD);
            if (rd.rotateX != 0.0f) { matrixStack.mulPose(Axis.XP.rotationDegrees(rd.rotateX)); }
            if (rd.rotateY != 0.0f) { matrixStack.mulPose(Axis.YP.rotationDegrees(rd.rotateY)); }
            if (rd.rotateZ != 0.0f) { matrixStack.mulPose(Axis.ZP.rotationDegrees(rd.rotateZ)); }
            matrixStack.scale(rd.scaleX, -rd.scaleY, rd.scaleZ);
            if (rd.modelOBJ == null) { rd.modelOBJ = ModelBuffer.getParameterizedModel(rd.resource, rd.visibleMeshes, rd.materialTextures, true, 0); }
            ModelBuffer.render(rd.modelOBJ, graphics.pose(), graphics.bufferSource(), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
         }
         else {
            boolean hasStack = stacks != null && !stacks.isEmpty() && i < stacks.size();
            matrixStack.translate(x + (hasStack ? -13.0f : 0.5f) + rd.tW, y + k - 1.5f + rd.tH, rd.tD);
            float scale = 12.0f / (float) (Math.max(rd.width, rd.height));
            float scaleX = scale;
            float scaleY = scale;
            if (rd.scaleX != 0.0f || rd.scaleY != 0.0f) {
               scaleX *= rd.scaleX;
               scaleY *= rd.scaleY;
               matrixStack.translate(12.0f * rd.scaleX, 6.0f * rd.scaleY, 0.0f);
            }
            matrixStack.scale(scaleX, scaleY, 1.0f);
            graphics.blit(rd.resource, 0, 0, rd.u, rd.v, rd.width, rd.height);
         }
         matrixStack.popPose();
      }
   }

    public int getHover() { return hover; }

   public List<Component> getNormalList() { return list; }

   public boolean hasSearch() { return hasSearch; }

   @Override
   public GuiCustomScrollNop setHoverTexts(Object... components) {
      hoverText.clear();
      if (components == null) { return this; }
      Util.instance.putHovers(hoverText, components);
      return this;
   }

   @Override
   public GuiCustomScrollNop setIsEnabled(boolean isEnabled) {
      selectable = isEnabled;
      return this;
   }

   @Override
   public GuiCustomScrollNop setIsVisible(boolean isVisible) {
      visible = isVisible;
      return this;
   }

   @Override
   public GuiCustomScrollNop setIsFocused(boolean isFocused) {
      focused = isFocused;
      return this;
   }

   @Override
   public GuiCustomScrollNop setCustomFont(ClientProxy.FontContainer font) {
      customFont = font;
      return this;
   }

   @Override
   public GuiComponentType getElementType() { return GuiComponentType.SCROLL; }

   public GuiCustomScrollNop setPos(int xIn, int yIn) {
      x = xIn;
      y = yIn;
      return this;
   }

   public int getX() { return x; }

   public int getY() { return y; }

   public String getSearchValue() { return textField.getValue(); }

   public void setSearchValue(String text) { textField.setValue(text); }

}
