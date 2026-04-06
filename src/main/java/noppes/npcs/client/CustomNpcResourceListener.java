package noppes.npcs.client;

import com.mojang.blaze3d.systems.RenderSystem;

import java.io.IOException;

import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.gui.select.ResourceSelection;
import noppes.npcs.client.parts.MpmPartReader;
import noppes.npcs.shared.client.model.util.CustomRenderStates;
import noppes.npcs.shared.common.util.LogWriter;
import org.jetbrains.annotations.NotNull;

public class CustomNpcResourceListener implements ResourceManagerReloadListener {

   public static int DefaultTextColor = CustomNpcs.LableColor.getRGB();

   public void onResourceManagerReload(@NotNull ResourceManager manager) {

      try { DefaultTextColor = Integer.parseInt(Component.translatable(CustomNpcs.MODID + ".defaultTextColor").toString(), 16); }
      catch (NumberFormatException var3) { DefaultTextColor = CustomNpcs.LableColor.getRGB(); }

      ResourceSelection.resourcesData.clear();
      MpmPartReader.reload();
      RenderSystem.recordRenderCall(() -> {
         try {
            CustomRenderStates.posTexNormalShader = new ShaderInstance(manager, new ResourceLocation("moreplayermodels", "position_tex_normal"), CustomRenderStates.POS_TEX_NORMAL);
         } catch (IOException e) {
            LogWriter.error(e);
         }
      });
   }

}
