package noppes.npcs.mixin.client.gui.multiplayer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.Component;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.shared.common.util.LogWriter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = JoinMultiplayerScreen.class, priority = 498)
public class JoinMultiplayerScreenMixin {

    @Inject(
            at = {@At("HEAD")},
            method = "join",
            cancellable = true
    )
    private void npcs$join(ServerData server, CallbackInfo ci) {
        try {
            ScriptController.setLevelKey(server.name + ";" + server.version + ";" + server.ip + ";" + server.isLan());
            if (ScriptController.Instance.notAgreement(ScriptController.getLevelKey())) {
                ci.cancel();
                Minecraft minecraft = Minecraft.getInstance();
                Screen backScreen = minecraft.screen;
                minecraft.setScreen(new ConfirmScreen((agree) -> {
                    if (agree && backScreen != null) {
                        ScriptController.Instance.setAgreement(ScriptController.getLevelKey(), true);
                        ConnectScreen.startConnecting(backScreen, minecraft, ServerAddress.parseString(server.ip), server, false);
                    }
                    else {
                        minecraft.setScreen(backScreen);
                        ScriptController.setLevelKey("");
                    }
                },
                        Component.empty(),
                        Component.translatable("system.check.scripts.agree"),
                        Component.translatable("gui.agree"),
                        Component.translatable("gui.cancel")));
            }
        }
        catch (Exception e) { LogWriter.error("Error while checking user agreement: "); }
    }

}
