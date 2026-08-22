package noppes.npcs.shared.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.*;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.mixin.client.resources.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AssetsFinder {

    public static List<ResourceLocation> find(String root, String suffix) {
        List<ResourceLocation> list = new ArrayList<>();
        Minecraft.getMinecraft().getResourcePackRepository().getRepositoryEntries().forEach((p) -> {
            if (p.getResourcePack() instanceof FallbackResourceManager) {
                List<IResourcePack> packs = ((IFallbackResourceManagerMixin) p.getResourcePack()).getResourcePacks();
                if (packs == null) { return; }
                for (IResourcePack pack : packs) {
                    if (pack instanceof LegacyV2Adapter) { pack = ((ILegacyV2AdapterMixin) pack).getPack(); }
                    if (pack instanceof DefaultResourcePack) {
                        ResourceIndex resourceIndex = ((IDefaultResourcePackMixin) pack).getResourceIndex();
                        Map<String, File> resourceMap = ((IResourceIndexMixin) resourceIndex).getResourceMap();
                        for (String key : resourceMap.keySet()) {
                            File f = resourceMap.get(key);
                            if (f.getAbsolutePath().contains(root) && f.getName().endsWith(suffix)) {
                                addFile(key, list);
                            }
                        }
                    }
                    else if (pack instanceof AbstractResourcePack) {
                        File directory = ((IAbstractResourcePackMixin) pack).getResourcePackFile();
                        if (directory == null || !directory.isDirectory()) { continue; }
                        File dir = new File(directory, "assets");
                        if (dir.exists() && dir.isDirectory()) { checkFolder(dir, list); }
                    }
                }
            }
        });
        return list;
    }

    private static void checkFolder(File file, List<ResourceLocation> list) {
        if (file != null) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        checkFolder(f, list);
                        continue;
                    }
                    addFile(f.getAbsolutePath(), list);
                }
            }
        }
    }

    private static void addFile(String path, List<ResourceLocation> mainList) {
        if (path == null || !path.contains("assets")) { return; }
        if (path.contains("\\")) {
            List<String> list = new ArrayList<>();
            while (path.contains("\\")) {
                list.add(path.substring(0, path.indexOf("\\")));
                path = path.substring(path.indexOf("\\") + 1);
            }
            list.add(path);
            StringBuilder pathBuilder = new StringBuilder();
            for (String p : list) {
                pathBuilder.append(p).append("/");
            }
            path = pathBuilder.toString();
            path = path.substring(0, path.length() - 1);
        }
        path = path.substring(path.lastIndexOf("assets") + 7);
        String domain = path.substring(0, path.indexOf("/"));
        if (domain.isEmpty()) { return; }
        path = path.substring(path.indexOf("/") + 1);
        mainList.add(new ResourceLocation(domain, path));
    }

}
