package noppes.npcs.client.gui.player;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import noppes.npcs.client.renderer.obj.ModelBuffer;
import noppes.npcs.client.renderer.obj.ParameterizedModel;
import noppes.npcs.controllers.MarcetController;
import noppes.npcs.controllers.data.Deal;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.util.ValueUtil;

import java.util.*;

public class GuiOpenCase extends GuiBasic {

    protected final Screen parent;
    protected final Map<ItemStack, Integer> map = new LinkedHashMap<>();
    protected final ResourceLocation objModel;
    protected final Map<String, ResourceLocation> materialTextures = new HashMap<>();
    protected int scrollX;
    protected int step;
    protected long startTicks = 0L;
    protected long maxTick;
    protected ParameterizedModel CHEST_FULL;
    protected ParameterizedModel CHEST_BODY;
    protected ParameterizedModel CHEST_TOP;

    public GuiOpenCase(Screen parentIn, int dealID, Map<ItemStack, Integer> mapIn) {
        super();
        drawDefaultBackground = false;
        hoverIsGame = true;
        parent = parentIn;
        map.putAll(mapIn);
        if (minecraft == null) { minecraft = Minecraft.getInstance(); }
        Deal deal = MarcetController.getInstance().getDeal(dealID);
        if (deal != null) {
            objModel = deal.getCaseObjModel();
            materialTextures.put("#material", deal.getCaseTexture());
            if (deal.getCaseSound() != null) {
                minecraft.getSoundManager().play(new SimpleSoundInstance(deal.getCaseSound(), SoundSource.PLAYERS, 1.0F, 1.0F,
                        SoundInstance.createUnseededRandom(), false, 0, SoundInstance.Attenuation.NONE,
                        player.getX(), player.getY() + player.getEyeHeight(), player.getZ(), false));
            }
        }
        else { objModel = null; }
        CHEST_FULL = ModelBuffer.getParameterizedModel(objModel, null, materialTextures, false, 0);
        CHEST_BODY = ModelBuffer.getParameterizedModel(objModel, List.of("body"), materialTextures, false, 0);
        CHEST_TOP = ModelBuffer.getParameterizedModel(objModel, List.of("top"), materialTextures, false, 0);
        maxTick = 18;
        if (minecraft.level != null) { startTicks = minecraft.level.getGameTime() + maxTick; }
        step = 0;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (map.isEmpty()) {
            onClose();
            return;
        }
        if (minecraft == null) { minecraft = Minecraft.getInstance(); }
        minecraft.options.hideGui = true;
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        float rotTop = 0.0f;
        float x = width * 0.5f - 10.0f;
        float y = height * 0.5f - 19.0f;
        float caseScale = 36.0f;
        boolean isRotX = true;
        if (minecraft.level != null) {
            if (startTicks > 0) {
                float tick = ValueUtil.correctFloat(partialTicks + (float) (startTicks - minecraft.level.getGameTime()), 0.0f , Float.MAX_VALUE);
                switch (step) {
                    case 0: {
                        rotTop = 0.0f;
                        caseScale = 72.0f;
                        float sin = ValueUtil.correctFloat((float) Math.sin(90.0d * tick / (double) maxTick * Math.PI / 180.0d), 0.0f, 1.0f);
                        matrixStack.translate(x + sin * width * 0.75f, y, 0.0f);
                        if (tick <= 0.0f) {
                            maxTick = 12;
                            startTicks = minecraft.level.getGameTime() + maxTick;
                            step = 1;
                        }
                        break;
                    } // move to center
                    case 1: {
                        isRotX = false;
                        rotTop = 0.0f;
                        caseScale = 72.0f;
                        float cos = ValueUtil.correctFloat((float) Math.cos(90.0d * tick / (double) maxTick * Math.PI / 180.0d), 0.0f, 1.0f);
                        matrixStack.translate(x, y, 0.0f);
                        matrixStack.translate(8.0f, 18.0f, 36.0f);
                        matrixStack.mulPose(Axis.XP.rotationDegrees(-30.0f));
                        matrixStack.mulPose(Axis.YP.rotationDegrees(cos * -360.0f));
                        matrixStack.translate(-8.0f, 0.0f, -36.0f);
                        if (tick <= 0.0f) {
                            maxTick = 10;
                            startTicks = minecraft.level.getGameTime() + maxTick;
                            step = 2;
                        }
                        break;
                    } // rotate
                    case 2: {
                        rotTop = 0.0f;
                        caseScale = 72.0f;
                        matrixStack.translate(x, y, 0.0f);
                        if (tick <= 0.0f) {
                            maxTick = 5;
                            startTicks = minecraft.level.getGameTime() + maxTick;
                            step = 3;
                        }
                        break;
                    } // wait open
                    case 3: {
                        caseScale = 72.0f;
                        float a = -135.0f / (float) maxTick;
                        float b = -a * (float) maxTick;
                        rotTop = a * tick + b;
                        matrixStack.translate(x, y, 0.0f);
                        if (tick <= 0.0f) {
                            maxTick = 6;
                            startTicks = minecraft.level.getGameTime() + maxTick;
                            step = 4;
                        }
                        break;
                    } // open
                    case 4: {
                        rotTop = 135.0f;
                        float cos = ValueUtil.correctFloat((float) Math.cos(90.0d * tick / (double) maxTick * Math.PI / 180.0d), 0.0f, 1.0f);
                        float a = - 0.5f / (float) maxTick;
                        float b = - a * (float) maxTick;
                        drawStacks(graphics, ValueUtil.correctFloat(a * tick + b, 0.0f, 1.0f),
                                0.0f, height * -0.4f * cos, mouseX, mouseY);

                        a = 18.0f / (float) maxTick;
                        b = 72.0f - a * (float) maxTick;
                        caseScale = a * tick + b;
                        matrixStack.translate(x, y, 0.0f);
                        if (tick <= 0.0f) {
                            maxTick = 6;
                            startTicks = minecraft.level.getGameTime() + maxTick;
                            step = 5;
                        }
                        break;
                    } // items up
                    case 5: {
                        rotTop = 135.0f;
                        float sin = ValueUtil.correctFloat((float) Math.sin(90.0d * tick / (double) maxTick * Math.PI / 180.0d), 0.0f, 1.0f);
                        float a = -0.5f / (float) maxTick;
                        float b = 0.5f - a * (float) maxTick;
                        drawStacks(graphics, ValueUtil.correctFloat(a * tick + b, 0.0f, 1.0f),
                                0.0f, height * -0.4f * sin, mouseX, mouseY);

                        a = 18.0f / (float) maxTick;
                        b = 54.0f - a * (float) maxTick;
                        caseScale = a * tick + b;
                        matrixStack.translate(x, y, 0.0f);
                        if (tick <= 0.0f) {
                            maxTick = 5;
                            startTicks = minecraft.level.getGameTime() + maxTick;
                            step = 6;
                        }
                        break;
                    } // items to center
                    case 6: {
                        rotTop = 135.0f;
                        drawStacks(graphics, 1.0f, 0.0f, 0.0f, mouseX, mouseY);
                        matrixStack.translate(x, y, 0.0f);
                        if (tick <= 0.0f) { startTicks = 0; }
                        break;
                    } // end
                    case 7: {
                        //tick = maxTick;
                        rotTop = 135.0f;
                        float a = 1.0f / (float) maxTick;
                        float b = 1.0f - a * (float) maxTick;
                        float c = - width * 0.5f / (float) maxTick;
                        float d = - c * (float) maxTick;
                        float e = - height * 0.5f / (float) maxTick;
                        float f = - e * (float) maxTick;
                        drawStacks(graphics, ValueUtil.correctFloat(a * tick + b, 0.0f, 1.0f),
                                c * tick + d, e * tick + f, mouseX, mouseY);

                        a = x / (float) maxTick;
                        b = x - a * (float) maxTick;
                        c = (y - height + 19.0f)/ (float) maxTick;
                        d = y - c * (float) maxTick;
                        matrixStack.translate(a * tick + b, c * tick + d, 0.0f);

                        a = 24.0f / (float) maxTick;
                        b = 36.0f - a * (float) maxTick;
                        caseScale = a * tick + b;
                        if (tick <= 0.0f) { onClose(); }
                        break;
                    } // close
                }
            }
            else {
                rotTop = 135.0f;
                drawStacks(graphics, 1.0f, 0.0f, 0.0f, mouseX, mouseY);
                matrixStack.translate(x, y, 0.0f);
            }
        }
        if (isRotX) { matrixStack.mulPose(Axis.XP.rotationDegrees(-30.0f)); }
        matrixStack.mulPose(Axis.YP.rotationDegrees(-75.0f));
        matrixStack.scale(caseScale, -caseScale, caseScale);
        if (rotTop != 0.0f) {
            ModelBuffer.render(CHEST_BODY, graphics.pose(), graphics.bufferSource(), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            matrixStack.mulPose(Axis.ZP.rotationDegrees(rotTop));
            ModelBuffer.render(CHEST_TOP, graphics.pose(), graphics.bufferSource(), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        }
        else { ModelBuffer.render(CHEST_FULL, graphics.pose(), graphics.bufferSource(), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY); }
        matrixStack.popPose();
        if (startTicks == 0 && step == 6) { super.render(graphics, mouseX, mouseY, partialTicks); }
    }

    @Override
    public boolean keyPressed(int key, int key_1, int key_2) {
        if (startTicks == 0 && step == 6 && key == InputConstants.KEY_ESCAPE) {
            if (minecraft == null) { minecraft = Minecraft.getInstance(); }
            if (minecraft.level != null) {
                maxTick = 5;
                startTicks = minecraft.level.getGameTime() + maxTick;
                step = 7;
            }
        }
        return super.keyPressed(key, key_1, key_2);
    }

    @Override
    public boolean shouldCloseOnEsc() { return startTicks == 0 && step == 6; }

    @Override
    public void onClose() {
        if (minecraft == null) { minecraft = Minecraft.getInstance(); }
        minecraft.popGuiLayer();
        minecraft.options.hideGui = false;
        minecraft.setScreen(parent);
    }

    private void drawStacks(GuiGraphics graphics, float scale, float posX, float posY, int mouseX, int mouseY) {
        if (map.isEmpty()) { return; }
        if (minecraft == null) { minecraft = Minecraft.getInstance(); }
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        float s = 4.0f * scale;
        int h = (int) (height * 0.5f) - 32;
        int w0 = (int) (width * 0.5f) - 32;
        matrixStack.translate(width * 0.5f - 8.0f * s + posX, height * 0.5f - 8.0f * s + posY, 150.0f);
        matrixStack.scale(s, s, s);
        ItemStack stack;
        List<Component> hovers;
        ArrayList<Map.Entry<ItemStack, Integer>> list = new ArrayList<>(map.entrySet());
        if (map.size() == 1) {
            stack = list.get(0).getKey();
            graphics.renderItem(stack, 0, 0);
            // hovers
            if (scale == 1.0f && isMouseHover(mouseX, mouseY, w0, h, 64, 64)) {
                hovers = stack.getTooltipLines(player, minecraft.options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL);
                if (hovers.get(0) instanceof MutableComponent component) { component.append(" ").append(Component.literal(" x" + list.get(0).getValue()).withStyle(ChatFormatting.RESET)); }
                hoverText.addAll(hovers);
            }
        }
        else if (list.size() == 2) {
            stack = list.get(0).getKey();
            matrixStack.translate(-9.0f, 0.0f, 0.0f);
            graphics.renderItem(stack, 0, 0);
            // hover 0
            if (scale == 1.0f && isMouseHover(mouseX, mouseY, w0 - 36, h, 64, 64)) {
                hovers = stack.getTooltipLines(player, minecraft.options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL);
                if (hovers.get(0) instanceof MutableComponent component) { component.append(" ").append(Component.literal(" x" + list.get(0).getValue()).withStyle(ChatFormatting.RESET)); }
                hoverText.addAll(hovers);
            }
            stack = list.get(1).getKey();
            matrixStack.translate(18.0f, 0.0f, 0.0f);
            graphics.renderItem(stack, 0, 0);
            // hover 1
            if (scale == 1.0f && isMouseHover(mouseX, mouseY, w0 + 36, h, 64, 64)) {
                hovers = stack.getTooltipLines(player, minecraft.options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL);
                if (hovers.get(0) instanceof MutableComponent component) { component.append(" ").append(Component.literal(" x" + list.get(1).getValue()).withStyle(ChatFormatting.RESET)); }
                hoverText.addAll(hovers);
            }
        }
        else if (list.size() == 3) {
            stack = list.get(0).getKey();
            matrixStack.translate(-18.0f, 0.0f, 0.0f);
            graphics.renderItem(stack, 0, 0);
            // hover 0
            w0 -= 72;
            if (scale == 1.0f && isMouseHover(mouseX, mouseY, w0, h, 64, 64)) {
                hovers = stack.getTooltipLines(player, minecraft.options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL);
                if (hovers.get(0) instanceof MutableComponent component) { component.append(" ").append(Component.literal(" x" + list.get(0).getValue()).withStyle(ChatFormatting.RESET)); }
                hoverText.addAll(hovers);
            }
            for (int i = 1; i < 3; i++) {
                matrixStack.translate(18.0f, 0.0f, 0.0f);
                stack = list.get((i + scrollX) % list.size()).getKey();
                graphics.renderItem(stack, 0, 0);
                // hover i
                if (scale == 1.0f && isMouseHover(mouseX, mouseY, w0 + i * 72, h, 64, 64)) {
                    hovers = stack.getTooltipLines(player, minecraft.options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL);
                    if (hovers.get(0) instanceof MutableComponent component) { component.append(" ").append(Component.literal(" x" + list.get((i + scrollX) % list.size()).getValue()).withStyle(ChatFormatting.RESET)); }
                    hoverText.addAll(hovers);
                }
            }
        }
        else if (list.size() == 4) {
            stack = list.get(0).getKey();
            matrixStack.translate(-27.0f, 0.0f, 0.0f);
            graphics.renderItem(stack, 0, 0);
            // hover 0
            w0 -= 108;
            if (scale == 1.0f && isMouseHover(mouseX, mouseY, w0, h, 64, 64)) {
                hovers = stack.getTooltipLines(player, minecraft.options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL);
                if (hovers.get(0) instanceof MutableComponent component) { component.append(" ").append(Component.literal(" x" + list.get(0).getValue()).withStyle(ChatFormatting.RESET)); }
                hoverText.addAll(hovers);
            }
            for (int i = 1; i < 4; i++) {
                matrixStack.translate(18.0f, 0.0f, 0.0f);
                stack = list.get((i + scrollX) % list.size()).getKey();
                graphics.renderItem(stack, 0, 0);
                // hover i
                if (scale == 1.0f && isMouseHover(mouseX, mouseY, w0 + i * 72, h, 64, 64)) {
                    hovers = stack.getTooltipLines(player, minecraft.options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL);
                    if (hovers.get(0) instanceof MutableComponent component) { component.append(" ").append(Component.literal(" x" + list.get((i + scrollX) % list.size()).getValue()).withStyle(ChatFormatting.RESET)); }
                    hoverText.addAll(hovers);
                }
            }
        }
        else {
            stack = list.get(0).getKey();
            matrixStack.translate(-36.0f, 0.0f, 0.0f);
            graphics.renderItem(stack, 0, 0);
            // hover 0
            w0 -= 144;
            if (scale == 1.0f && isMouseHover(mouseX, mouseY, w0, h, 64, 64)) {
                hovers = stack.getTooltipLines(player, minecraft.options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL);
                if (hovers.get(0) instanceof MutableComponent component) { component.append(" ").append(Component.literal(" x" + list.get(0).getValue()).withStyle(ChatFormatting.RESET)); }
                hoverText.addAll(hovers);
            }
            for (int i = 1; i < 5; i++) {
                matrixStack.translate(18.0f, 0.0f, 0.0f);
                stack = list.get((i + scrollX) % list.size()).getKey();
                graphics.renderItem(stack, 0, 0);
                // hover i
                if (scale == 1.0f && isMouseHover(mouseX, mouseY, w0 + i * 72, h, 64, 64)) {
                    hovers = stack.getTooltipLines(player, minecraft.options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL);
                    if (hovers.get(0) instanceof MutableComponent component) { component.append(" ").append(Component.literal(" x" + list.get((i + scrollX) % list.size()).getValue()).withStyle(ChatFormatting.RESET)); }
                    hoverText.addAll(hovers);
                }
            }
        }
        matrixStack.popPose();
    }

}
