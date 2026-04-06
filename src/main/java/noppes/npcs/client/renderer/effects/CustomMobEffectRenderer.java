package noppes.npcs.client.renderer.effects;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;
import noppes.npcs.CustomNpcs;
import noppes.npcs.potions.CustomMobEffect;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class CustomMobEffectRenderer implements IClientMobEffectExtensions {

    protected final @Nonnull CustomMobEffect effect;
    protected final @Nonnull Minecraft mc;
    protected ResourceLocation resource;
    protected boolean isLoad = false;
    protected boolean visibleInInventory;
    protected boolean visibleInGui;

    public CustomMobEffectRenderer(@Nonnull CustomMobEffect effectIn) {
        mc = Minecraft.getInstance();
        effect = effectIn;
    }

    @Override
    public boolean isVisibleInInventory(MobEffectInstance instance)  {
        if (!isLoad) { load(); }
        return visibleInInventory;
    }

    @Override
    public boolean isVisibleInGui(MobEffectInstance instance) {
        if (!isLoad) { load(); }
        return visibleInGui;
    }

    @Override
    public boolean renderInventoryIcon(MobEffectInstance effect, EffectRenderingInventoryScreen<?> screen, GuiGraphics graphics, int x, int y, int blitOffset) {
        if (!isLoad) { load(); }
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate((float) x, (float) y + 7.0f, 0.0f);
        matrixStack.scale(0.0703125f, 0.0703125f, 1.0f);
        graphics.blit(resource, 0, 0, 0, 0, 256, 256);
        matrixStack.popPose();
        return true;
    }

    @Override
    public boolean renderGuiIcon(MobEffectInstance effect, Gui gui, GuiGraphics graphics, int x, int y, float z, float alpha) {
        if (!isLoad) { load(); }
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        graphics.setColor(1.0F, 1.0F, 1.0F, alpha);
        matrixStack.translate((float) x + 3.0f, (float) y + 3.0f, 0.0f);
        matrixStack.scale(0.0703125f, 0.0703125f, 1.0f);
        graphics.blit(resource, 0, 0, 0, 0, 256, 256);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.popPose();
        return true;
    }

    private void load() {
        if (isLoad) { return; }
        resource = new ResourceLocation(CustomNpcs.MODID, "textures/mob_effect/" + effect.getCustomName() + ".png");
        CompoundTag nbtData = effect.getCustomNbt().getMCNBT();
        visibleInInventory = !nbtData.contains("VisibleInInventory", 1) || nbtData.getBoolean("VisibleInInventory");
        visibleInGui = !nbtData.contains("VisibleInGui", 1) || nbtData.getBoolean("VisibleInGui");
        isLoad = true;
    }

}

