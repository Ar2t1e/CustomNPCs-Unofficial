package noppes.npcs.mixin.client.gui.screens;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(value = ConfirmScreen.class, priority = 498)
public class ConfirmScreenMixin {

    @Final @Shadow private Component message;
    @Final @Shadow protected BooleanConsumer callback;
    @Shadow private MultiLineLabel multilineMessage;
    @Shadow protected Component yesButton;
    @Shadow protected Component noButton;

    @Unique private MultiLineLabel npcs$multilineTitle = MultiLineLabel.EMPTY;

    @Inject(at = {@At("TAIL")}, method = {"render"})
    private void npcs$render(GuiGraphics graphics, int xMouse, int yMouse, float partialTicks, CallbackInfo ci) {
        if (npcs$isScriptsAgree()) {
            npcs$multilineTitle.renderCentered(graphics, ((ConfirmScreen) (Object) this).width / 2, npcs$titleCustomTop());
        }
    }

    @Inject(at = {@At("HEAD")}, method = {"init"})
    protected void npcs$init(CallbackInfo ci) {
        if (npcs$isScriptsAgree()) {
            ConfirmScreen parent = (ConfirmScreen) (Object) this;
            Objects.requireNonNull(Minecraft.getInstance().font);
            if (multilineMessage == null) {
                multilineMessage = MultiLineLabel.create(Minecraft.getInstance().font, message, parent.width - 50);
            }
            npcs$multilineTitle = MultiLineLabel.create(Minecraft.getInstance().font, Component.translatable("system.check.scripts.title"), parent.width - 50);
        }
    }

    @Inject(
            at = {@At("RETURN")},
            method = {"titleTop"},
            cancellable = true
    )
    private void npcs$titleTop(CallbackInfoReturnable<Integer> cir) {
        if (npcs$isScriptsAgree()) {
            cir.setReturnValue(npcs$titleCustomTop());
        }
    }

    @Inject(
            at = {@At("RETURN")},
            method = {"messageTop"},
            cancellable = true
    )
    private void npcs$messageTop(CallbackInfoReturnable<Integer> cir) {
        if (npcs$isScriptsAgree()) {
            cir.setReturnValue(npcs$titleCustomTop() + npcs$multilineTitle.getLineCount() * 9 + 12);
        }
    }

    @Inject(
            method = "addExitButton",
            at = @At("HEAD")
    )
    protected void npcs$addExitButton(Button button, CallbackInfo ci) {
        if (npcs$isScriptsAgree()) {
            button.setY(npcs$titleCustomTop() + (multilineMessage.getLineCount() + npcs$multilineTitle.getLineCount()) * 9 + 24);
        }
    }

    @Unique
    private boolean npcs$isScriptsAgree() {
        return message != null && message.getContents() instanceof TranslatableContents trCont && trCont.getKey().equals("system.check.scripts.agree");
    }

    @Unique
    private int npcs$titleCustomTop() {
        ConfirmScreen parent = (ConfirmScreen) (Object) this;
        Objects.requireNonNull(Minecraft.getInstance().font);
        int linesPos = (parent.height - (multilineMessage.getLineCount() + npcs$multilineTitle.getLineCount()) * 9) / 2;
        return Mth.clamp(linesPos - 29, 10, 160);
    }

}
