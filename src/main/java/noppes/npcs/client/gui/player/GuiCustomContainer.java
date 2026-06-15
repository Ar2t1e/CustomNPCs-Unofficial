package noppes.npcs.client.gui.player;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import noppes.npcs.CustomNpcs;
import noppes.npcs.containers.ContainerChestCustom;
import noppes.npcs.mixin.world.inventory.ISlotMixin;
import noppes.npcs.shared.client.gui.GuiBasicContainer;

import javax.annotation.Nonnull;

public class GuiCustomContainer extends GuiBasicContainer<ContainerChestCustom> {

    private static final ResourceLocation BACK_TEXTURE = new ResourceLocation(CustomNpcs.MODID, "textures/gui/smallbg.png");
    private static final ResourceLocation TABS_TEXTURE = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
    private static final ResourceLocation ROW_TEXTURE = new ResourceLocation("textures/gui/container/creative_inventory/tab_items.png");
    private static final ResourceLocation LOCK_TEXTURE = new ResourceLocation("textures/gui/widgets.png");
    private static final ResourceLocation SLOT_TEXTURE = new ResourceLocation(CustomNpcs.MODID, "textures/gui/slot.png");

    private final int guiColor;
    private final int[] guiColorArr;
    private final boolean isMany;
    private final int maxRows;
    private final int step;
    private int row;
    private int yPos;
    private boolean hoverScroll;
    private boolean isScrolling;
    private final String lock;

    public GuiCustomContainer(ContainerChestCustom container, Inventory inv, Component title) {
        super(container, inv, title);
        imageWidth = 176;
        imageHeight = 116 + container.height;

        isMany = container.customChest.getContainerSize() > 45;
        maxRows = (int) Math.ceil((double) container.customChest.getContainerSize() / 9.0d) - 5;
        guiColor = container.customChest.guiColor;
        guiColorArr = container.customChest.guiColorArr;
        step = maxRows > 0 ? (int) (73.0f / (float) maxRows) : 0;
        row = 0;
        lock = container.customChest.getLockCode();
        isScrolling = false;
        resetSlots();
    }

    @Override
    public void init() {
        super.init();
        resetSlots();
    }

    @Override
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        int left = (width - imageWidth) / 2;
        int top = (height - imageHeight) / 2;
        int h = menu.height + 107;

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        if (guiColorArr != null && guiColorArr.length > 1) {
            // Gradient background
            float r0 = (float) (guiColorArr[0] >> 16 & 255) / 255.0F;
            float g0 = (float) (guiColorArr[0] >> 8 & 255) / 255.0F;
            float b0 = (float) (guiColorArr[0] & 255) / 255.0F;
            float r1 = (float) (guiColorArr[1] >> 16 & 255) / 255.0F;
            float g1 = (float) (guiColorArr[1] >> 8 & 255) / 255.0F;
            float b1 = (float) (guiColorArr[1] & 255) / 255.0F;
            float s = 1.0f / (float) h;

            for (int i = 0; i < h; i++) {
                float sd = i * s;
                float r = r0 * (1.0f - sd) + r1 * sd;
                float g = g0 * (1.0f - sd) + g1 * sd;
                float b = b0 * (1.0f - sd) + b1 * sd;
                RenderSystem.setShaderColor(r, g, b, 1.0f);
                // Draw thin strips for gradient
                int w = i <= h - 4 ? i : 222 + i - h;
                graphics.blit(BACK_TEXTURE, left, top + i, 0, w, 176, 1);
                if (isMany) { graphics.blit(BACK_TEXTURE, left + 172, top + i, 156, w, 20, 1); }
            }
        } else {
            // Solid color or default
            if (guiColor != -1) {
                float r = (float) (guiColor >> 16 & 255) / 255.0F;
                float g = (float) (guiColor >> 8 & 255) / 255.0F;
                float b = (float) (guiColor & 255) / 255.0F;
                RenderSystem.setShaderColor(r, g, b, 1.0F);
            }
            graphics.blit(BACK_TEXTURE, left, top, 0, 0, 176, h - 4);
            graphics.blit(BACK_TEXTURE, left, top + h - 4, 0, 218, 176, 4);
        }

        // Reset color
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // Draw slots
        graphics.blit(SLOT_TEXTURE, left, top, 0, 0, 0, 0); // Bind texture
        for (int s = 0; s < menu.slots.size(); s++) {
            Slot slot = menu.getSlot(s);
            if (slot.x >= 0 && slot.y >= 0) {
                graphics.blit(SLOT_TEXTURE, left + slot.x - 1, top + slot.y - 1, 0, 0, 18, 18);
            }
        }

        // Scrollbar
        if (isMany) {
            graphics.blit(ROW_TEXTURE, left + 172, top + 14, 174, 17, 14, 86);
            graphics.blit(ROW_TEXTURE, left + 172, top + 100, 174, 125, 14, 4);

            float currentScroll = (float) row / (float) maxRows;
            int scrollH = (int) (currentScroll * 73.0f);
            int u = left + 173;
            int v = top + 15 + scrollH;
            hoverScroll = mouseX >= u && mouseX <= u + 12 && mouseY >= v && mouseY <= v + 15;
            graphics.blit(TABS_TEXTURE, u, v, hoverScroll ? 244 : 232, 0, 12, 15);
        }

        // Lock icon
        if (!lock.isEmpty()) {
            graphics.blit(LOCK_TEXTURE, left + 164 + (isMany ? 16 : 0), top - 8, 0, 146, 20, 20);
        }
    }

    @Override
    protected void renderLabels(@Nonnull GuiGraphics graphics, int mouseX, int mouseY) {
        int color = 0xFFFFFFFF;
        if (guiColor != -1) {
            int r = guiColor >> 16 & 255;
            int g = guiColor >> 8 & 255;
            int b = guiColor & 255;
            if (r + g + b > 384) { color = 0xFF000000; }
        }
        if (guiColorArr != null) {
            int r = guiColorArr[0] >> 16 & 255;
            int g = guiColorArr[0] >> 8 & 255;
            int b = guiColorArr[0] & 255;
            if (r + g + b > 384) { color = 0xFF000000; }
        }
        graphics.drawString(font, menu.customChest.getDisplayName(), 8, 5, color, false);
        graphics.drawString(font, playerInventoryTitle, 8, imageHeight - 100, color, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hoverScroll) {
            yPos = (int) mouseY;
            isScrolling = true;
            return true;
        } else if (isMany) {
            int u = 173 + (width - imageWidth) / 2;
            int v = 18 + (height - imageHeight) / 2;
            if (mouseX >= u && mouseX <= u + 11 && mouseY >= v && mouseY <= v + 88) {
                int h = (int) mouseY - v;
                int r;
                if (h <= 7) {
                    r = 0;
                } else if (h >= 81) {
                    r = maxRows;
                } else {
                    r = (int) ((double) maxRows * (double) h / 88.0d);
                }
                int old = row;
                r = Math.max(0, Math.min(r, maxRows));
                if (old != r) {
                    row = r;
                    resetSlots();
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isScrolling) {
            isScrolling = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isScrolling) {
            int mouseYInt = (int) mouseY;
            if (mouseYInt - yPos >= step) {
                resetRow(true);
                yPos = mouseYInt;
            } else if ((yPos - mouseYInt) >= step) {
                resetRow(false);
                yPos = mouseYInt;
            }
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (isMany) {
            if (delta > 0) {
                resetRow(false);
            } else if (delta < 0) {
                resetRow(true);
            }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (minecraft == null) { minecraft = Minecraft.getInstance(); }
        if (keyCode == InputConstants.getKey("key.keyboard.down").getValue() ||
                keyCode == minecraft.options.keyDown.getKey().getValue()) { // Down arrow
            resetRow(true);
            return true;
        }
        if (keyCode == InputConstants.getKey("key.keyboard.up").getValue() ||
                keyCode == minecraft.options.keyUp.getKey().getValue()) { // Up arrow
            resetRow(false);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void resetRow(boolean down) {
        if (!isMany) return;
        int old = row;
        if (down) {
            row++;
        } else {
            row--;
        }
        row = Math.max(0, Math.min(row, maxRows));
        if (old != row) {
            resetSlots();
        }
    }

    private void resetSlots() {
        if (!isMany) return;
        int m = row * 9;
        int n = m + 45;
        int i = -1;
        int t = menu.customChest.getContainerSize();
        int u = 0;
        int e = t;
        if (t % 9 != 0) {
            e -= t % 9;
        }

        for (int s = 0; s < menu.slots.size(); s++) {
            Slot slot = menu.getSlot(s);
            // Only modify chest slots (not player inventory)
            if (s >= menu.customChest.getContainerSize()) continue;
            if (s < m || s >= n) {
                ((ISlotMixin) slot).setX(-5000);
                ((ISlotMixin) slot).setY(-5000);
                continue;
            }
            i++;
            if (s >= e) {
                u = (int) (((9.0d - ((double) t % 9.0d)) / 2.0d) * 18.0d);
            }
            ((ISlotMixin) slot).setX(8 + u + (i % 9) * 18);
            ((ISlotMixin) slot).setY(15 + (i / 9) * 18);
        }
    }

}