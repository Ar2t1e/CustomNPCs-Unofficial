package noppes.npcs.shared.client.util;


import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.util.ImageDownloadAlt;
import noppes.npcs.mixin.client.resources.ISkinManagerMixin;
import noppes.npcs.shared.SharedReferences;
import noppes.npcs.shared.common.util.LogWriter;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ResourceDownloader {
    private static final Set<ResourceLocation> active = Collections.synchronizedSet(new HashSet<>());
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    public static void load(ImageDownloadAlt resource) {
        if (!active.contains(resource.location)) {
            active.add(resource.location);
            executor.execute(() -> {
                resource.loadTextureFromServer();
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    Minecraft.getMinecraft().getTextureManager().loadTexture(resource.location, resource);
                    active.remove(resource.location);
                });
                try {
                    Thread.sleep(400L);
                } catch (InterruptedException var2) {
                    LogWriter.error(var2);
                }
            });
        }
    }

    @SuppressWarnings("unused")
    public static ResourceLocation getUrlResourceLocation(String url, boolean fixSkin) {
        return new ResourceLocation(SharedReferences.modid(), "skins/" + (url + fixSkin).hashCode() + (fixSkin ? "" : "32"));
    }

    @SuppressWarnings("unused")
    public static File getUrlFile(String url, boolean fixSkin) {
        return new File(((ISkinManagerMixin) Minecraft.getMinecraft().getSkinManager()).getSkinCacheDir(), "" + (url + fixSkin).hashCode());
    }

    public static boolean contains(ResourceLocation location) {
        return active.contains(location);
    }
}
