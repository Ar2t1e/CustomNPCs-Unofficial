package noppes.npcs.mixin.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Rarity;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.gui.util.GuiNpcUtil;
import noppes.npcs.shared.client.gui.GuiBasicContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractContainerScreen.class, priority = 498)
public class AbstractContainerScreenMixin {

    @Unique
    private static final ResourceLocation[] NPCS$RARITY_LOCATIONS = new ResourceLocation[] {
            new ResourceLocation(CustomNpcs.MODID, "textures/item/rarity/0.png"),
            new ResourceLocation(CustomNpcs.MODID, "textures/item/rarity/1.png"),
            new ResourceLocation(CustomNpcs.MODID, "textures/item/rarity/2.png"),
            new ResourceLocation(CustomNpcs.MODID, "textures/item/rarity/3.png"),
            new ResourceLocation(CustomNpcs.MODID, "textures/item/rarity/4.png")
    };

    @Inject(at = {@At("RETURN")}, method = {"findSlot"}, cancellable = true)
    private void npcs$findSlot(double mouseX, double mouseY, CallbackInfoReturnable<Slot> cir) {
        if ((Object) this instanceof GuiBasicContainer<?> modGui) {
            cir.setReturnValue(modGui.findSlot(mouseX, mouseY, cir.getReturnValue()));
        }
    }

    /**
     * @author BetaZavr
     * @reason Shows the rarity of the item in the inventory slot
     */
    @Inject(method = "renderSlot", at = @At("HEAD"))
    public void npcs$renderSlot(GuiGraphics graphics, Slot slotIn, CallbackInfo ci) {
        if (CustomNpcs.ShowRarityItem && slotIn.hasItem()) {
            int type = 0;
            float[] color = null; // [ red, green, blue, alpha ]
            Rarity rarity = slotIn.getItem().getRarity();
            if (rarity != Rarity.COMMON) {
                TextColor textColor = slotIn.getItem().getRarity().getStyleModifier().apply(Style.EMPTY).getColor();
                if (textColor != null) {
                    int c = textColor.getValue();
                    color = new float[]{
                            (float) (c >> 16 & 255) / 255.0F, // red
                            (float) (c >> 8 & 255) / 255.0F, // green
                            (float) (c & 255) / 255.0F, // blue
                            1.0f // alpha
                    };
                }
            }
            if (slotIn.getItem().getTag() != null && slotIn.getItem().getTag().contains("RarityShow", 10)) {
                CompoundTag nbt = slotIn.getItem().getTag().getCompound("RarityShow");
                if (nbt.contains("color", 3) || nbt.contains("color", 4) ||
                        nbt.contains("color", 8)) {
                    int c;
                    if (nbt.contains("color", 4)) {
                        c = (int) nbt.getLong("color");
                        color = new float[] {
                                (float)(c >> 16 & 255) / 255.0F,
                                (float)(c >> 8 & 255) / 255.0F,
                                (float)(c & 255) / 255.0F,
                                (float)(c >> 24 & 255) / 255.0F
                        };
                    }
                    else {
                        c = -1;
                        if (nbt.contains("color", 3)) { c = nbt.getInt("color"); }
                        else if (nbt.contains("color", 4)) { c = (int) nbt.getLong("color"); }
                        else {
                            try { c = Integer.getInteger(nbt.getString("color")); } catch (Exception ignored) { }
                        }
                        color = new float[] {
                                (float)(c >> 16 & 255) / 255.0F,
                                (float)(c >> 8 & 255) / 255.0F,
                                (float)(c & 255) / 255.0F,
                                1.0f
                        };
                    }
                }
                else if (nbt.contains("rarity", 8)) {
                    String name = nbt.getString("rarity");
                    for (Rarity r : Rarity.values()) {
                        if (r != Rarity.COMMON && r.name().equalsIgnoreCase(name)) {
                            TextColor textColor = r.getStyleModifier().apply(Style.EMPTY).getColor();
                            if (textColor != null) {
                                int c = textColor.getValue();
                                color = new float[]{
                                        (float) (c >> 16 & 255) / 255.0F, // red
                                        (float) (c >> 8 & 255) / 255.0F, // green
                                        (float) (c & 255) / 255.0F, // blue
                                        1.0f // alpha
                                };
                            }
                            break;
                        }
                    }
                }
                if (nbt.contains("type", 3)) { type = nbt.getInt("type") % NPCS$RARITY_LOCATIONS.length; }
            }
            if (color != null) {
                PoseStack matrixStack = graphics.pose();
                matrixStack.pushPose();
                RenderSystem.enableBlend();
                matrixStack.translate(slotIn.x, slotIn.y, 1.0F);
                matrixStack.scale(0.0625f, 0.0625f, 0.0625f);
                RenderSystem.setShaderColor(color[0], color[1], color[2], color[3]);
                GuiNpcUtil.drawTexturedModalRect(graphics, NPCS$RARITY_LOCATIONS[type], 0, 0, 256, 256, 256.0f);
                RenderSystem.disableBlend();
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                matrixStack.popPose();
            }
        }
    }

}
