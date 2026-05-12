package noppes.npcs.mixin.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumRarity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.IRarity;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.gui.util.GuiNpcUtil;
import noppes.npcs.shared.client.gui.GuiBasicContainer;
import noppes.npcs.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GuiContainer.class, priority = 499)
public class GuiContainerMixin {

    @Unique
    private static final ResourceLocation[] NPCS$RARITY_LOCATIONS = new ResourceLocation[] {
            new ResourceLocation(CustomNpcs.MODID, "textures/item/rarity/0.png"),
            new ResourceLocation(CustomNpcs.MODID, "textures/item/rarity/1.png"),
            new ResourceLocation(CustomNpcs.MODID, "textures/item/rarity/2.png"),
            new ResourceLocation(CustomNpcs.MODID, "textures/item/rarity/3.png"),
            new ResourceLocation(CustomNpcs.MODID, "textures/item/rarity/4.png")
    };

    @Inject(at = {@At("RETURN")}, method = {"getSlotAtPosition"}, cancellable = true)
    private void npcs$findSlot(int mouseX, int mouseY, CallbackInfoReturnable<Slot> cir) {
        Object parent = this;
        if (parent instanceof GuiBasicContainer<?>) {
            cir.setReturnValue(((GuiBasicContainer<?>) parent).findSlot(mouseX, mouseY, cir.getReturnValue()));
        }
    }

    /**
     * @author BetaZavr
     * @reason Shows the rarity of the item in the inventory slot
     */
    @Inject(method = "drawSlot", at = @At("HEAD"))
    public void npcs$drawSlot(Slot slotIn, CallbackInfo ci) {
        if (CustomNpcs.ShowRarityItem && !slotIn.getStack().isEmpty()) {
            int type = 0;
            float[] color = null; // [ red, green, blue, alpha ]
            IRarity rarity = slotIn.getStack().getItem().getForgeRarity(slotIn.getStack());
            if (rarity != EnumRarity.COMMON) { color = Util.instance.getColorF(rarity.getColor().getColorIndex()); }
            Util.instance.getColorF(rarity.getColor().getColorIndex());
            if (slotIn.getStack().getTagCompound() != null && slotIn.getStack().getTagCompound().hasKey("RarityShow", 10)) {
                NBTTagCompound nbt = slotIn.getStack().getTagCompound().getCompoundTag("RarityShow");
                if (nbt.hasKey("color", 3) || nbt.hasKey("color", 4) || nbt.hasKey("color", 8)) {
                    int c;
                    if (nbt.hasKey("color", 4)) {
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
                        if (nbt.hasKey("color", 3)) { c = nbt.getInteger("color"); }
                        else if (nbt.hasKey("color", 4)) { c = (int) nbt.getLong("color"); }
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
                else if (nbt.hasKey("rarity", 8)) {
                    String name = nbt.getString("rarity");
                    for (EnumRarity r : EnumRarity.values()) {
                        if (r != EnumRarity.COMMON && r.getName().equalsIgnoreCase(name) || r.name().equalsIgnoreCase(name)) {
                            color = Util.instance.getColorF(r.getColor().getColorIndex());
                            break;
                        }
                    }
                }
                if (nbt.hasKey("type", 3)) { type = nbt.getInteger("type") % NPCS$RARITY_LOCATIONS.length; }
            }
            if (color != null) {
                GlStateManager.pushMatrix();
                GlStateManager.enableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.translate(slotIn.xPos, slotIn.yPos, 1.0F);
                GlStateManager.scale(0.5f, 0.5f, 1.0f);
                GlStateManager.color(color[0], color[1], color[2], color[3]);

                GuiNpcUtil.drawTexturedModalRect(NPCS$RARITY_LOCATIONS[type], 0, 0, 256, 256, 256.0f);

                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
                float f = 0.00390625f;
                float f0 = type * f * 32.0f;
                float f1 = (1.0f + type) * f * 32.0f;
                bufferbuilder.pos(0.0d, 32.0d, 0.0d).tex(0.734375f, f1).endVertex(); // [188, 32]
                bufferbuilder.pos(32.0d, 32.0d, 0.0d).tex(0.859375f, f1).endVertex(); // [188 + 32, 32]
                bufferbuilder.pos(32.0d, 0.0d, 0.0d).tex(0.859375f, f0).endVertex(); // [188 + 32, 0]
                bufferbuilder.pos(0.0d, 0.0d, 0.0d).tex(0.734375f, f0).endVertex(); // [188, 0]
                tessellator.draw();

                GlStateManager.disableAlpha();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }
    }

}
