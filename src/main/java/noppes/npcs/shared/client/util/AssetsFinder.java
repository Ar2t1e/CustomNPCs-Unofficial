package noppes.npcs.shared.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.*;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.mixin.client.resources.IFallbackResourceManagerMixin;
import noppes.npcs.reflection.client.resources.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AssetsFinder {

    public static List<ResourceLocation> find(String root, String suffix) {
        List<ResourceLocation> list = new ArrayList<>();
        Minecraft.getMinecraft().getResourcePackRepository().getRepositoryEntries().forEach((p) -> {
            for (String s : p.getResourcePack().getResourceDomains()) {
                if (p.getResourcePack() instanceof FallbackResourceManager) {
                    List<IResourcePack> packs = ((IFallbackResourceManagerMixin) p.getResourcePack()).getResourcePacks();
                    if (packs == null) { return; }
                    for (IResourcePack pack : packs) {
                        if (pack instanceof LegacyV2Adapter) { pack = LegacyV2AdapterReflection.getIResourcePack((LegacyV2Adapter) pack); }
                        if (pack instanceof DefaultResourcePack) {
                            ResourceIndex resourceIndex = DefaultResourcePackReflection.getResourceIndex((DefaultResourcePack) pack);
                            Map<String, File> resourceMap = ResourceIndexReflection.getResourceMap(resourceIndex);
                            for (String key : resourceMap.keySet()) {
                                File f = resourceMap.get(key);
                                if (f.getAbsolutePath().contains(root) && f.getName().endsWith(suffix)) {
                                    //list.add();
                                }
                            }
                        }
                        else if (pack instanceof AbstractResourcePack) {
                            File directory = AbstractResourcePackReflection.getResourcePackFile((AbstractResourcePack) pack);
                            if (directory == null || !directory.isDirectory()) { continue; }
                            File dir = new File(directory, "assets");
                            if (dir.exists() && dir.isDirectory()) {

                            }
                        }
                    }
                }
            }
        });
        return list;
    }

}
