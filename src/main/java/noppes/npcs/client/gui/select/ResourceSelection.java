package noppes.npcs.client.gui.select;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.resource.DelegatingPackResources;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.mixin.client.renderer.texture.ITextureAtlasMixin;
import noppes.npcs.mixin.client.renderer.texture.ITextureManagerMixin;
import noppes.npcs.mixin.minecraftforge.resource.IDelegatingPackResourcesMixin;
import noppes.npcs.mixin.server.packs.IFilePackResourcesMixin;
import noppes.npcs.mixin.server.packs.IVanillaPackResourcesMixin;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.shared.client.gui.GuiBasicContainer;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.Util;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public abstract class ResourceSelection
        extends GuiNPCInterface
        implements ICustomScrollListener {

    public static Map<String, Map<String, TreeMap<ResourceLocation, Long>>> resourcesData = new HashMap<>();

    protected final Map<String, TreeMap<ResourceLocation, Long>> data = new TreeMap<>(); // (Directory, Files)
    protected final Screen parent;
    protected final MutableComponent back;
    protected ResourceLocation selectDir;
    protected GuiCustomScrollNop scroll;
    protected String suffix;
    protected String baseResource = "";
    protected int offsetX = 0;
    protected int scrollWidth;
    protected Component select = Component.empty();

    public final int id;
    public ResourceLocation resource;

    public ResourceSelection(Screen parentIn, int idIn, EntityNPCInterface npcIn, @Nonnull String startIn, String suffixIn) {
        super(npcIn);
        drawDefaultBackground = false;
        setBackground("menubg.png");
        imageWidth = 366;
        imageHeight = 226;
        scrollWidth = imageWidth - 10;

        id = idIn;
        parent = parentIn;
        suffix = suffixIn.toLowerCase();
        back = Component.literal("   " + Character.toChars(0x2190)[0] + " (")
                .append(Component.translatable("gui.back")).append(Component.literal(")"))
                .withStyle(ChatFormatting.GOLD);

        if (resourcesData.containsKey(suffix)) { data.putAll(resourcesData.get(suffix)); }

        selectDir = null;
        if (minecraft == null) { minecraft = Minecraft.getInstance(); }
        ResourceLocation loc = new ResourceLocation(startIn);
        if (data.containsKey(loc.getNamespace()) && !data.get(loc.getNamespace()).containsKey(loc)) {
            try {
                if (!startIn.isEmpty()) { minecraft.getTextureManager().getTexture(loc); }
            }
            catch (Exception ignored) { }
        }
        if (!data.containsKey(loc.getNamespace())) {
            resetFiles();
            resourcesData.put(suffix, data);
        }
        baseResource = startIn;
        if (!startIn.isEmpty()) {
            resource = new ResourceLocation(startIn);
            if (startIn.lastIndexOf("/") != -1) {
                startIn = startIn.substring(0, startIn.lastIndexOf("/"));
            }
            selectDir = new ResourceLocation(startIn);
            if (!data.containsKey(selectDir.getNamespace())) {
                selectDir = null;
                return;
            }
            for (ResourceLocation r : data.get(selectDir.getNamespace()).keySet()) {
                if (r.getPath().indexOf(selectDir.getPath()) == 0) { return; }
            }
            selectDir = null;
        }
    }

    @Override
    public void buttonEvent(GuiButtonNop button) {
        if (button.id == 1 || button.id == 2 || button.id == 66) {
            if ((button.id == 1 || button.id == 66)) { cancel(); }
            onClose();
        }
    }

    @Override
    public void init() {
        super.init();
        guiLeft += offsetX;
        int h = guiTop + imageHeight - 25;
        if (minecraft == null) { minecraft = Minecraft.getInstance(); }
        addButton(2, guiLeft + 271, h, "gui.done")
                .setSize(90, 20)
                .setHoverTexts("selection.hover.done");
        addButton(1, guiLeft + 5, h, "gui.cancel")
                .setSize(90, 20)
                .setHoverTexts("hover.back");
        if (scroll == null) { scroll = addScroll(0).setSize(scrollWidth, 180); }
        Component domain = Component.literal("All Data in Game (mods)/");
        if (selectDir == null) { scroll.setList(new ArrayList<>(data.keySet())); }
        else {
            List<Component> list = new ArrayList<>();
            Map<String, Long> ds = new TreeMap<>();
            Map<String, Long> fs = new TreeMap<>();
            String path = selectDir.getPath();
            for (ResourceLocation res : data.get(selectDir.getNamespace()).keySet()) {
                if (!res.getPath().contains("/")) {
                    fs.put(res.getPath(), data.get(selectDir.getNamespace()).get(res));
                }
                else if (res.getPath().indexOf(path) == 0) {
                    String key = res.getPath().substring(path.length() + 1);
                    if (key.contains("/")) {
                        ds.put(key.substring(0, key.indexOf("/")), data.get(selectDir.getNamespace()).get(res));
                    } else if ((suffix.isEmpty() || res.getPath().toLowerCase().endsWith(suffix))) {
                        fs.put(res.getPath().substring(res.getPath().lastIndexOf("/") + 1), data.get(selectDir.getNamespace()).get(res));
                    }
                }
            }
            String txrName = resource != null ? resource.getPath() : "";
            if (!txrName.isEmpty()) {
                txrName = txrName.substring(txrName.lastIndexOf("/") + 1);
            }
            List<Component> suffixes = new ArrayList<>();
            int i = 1, pos = -1;
            suffixes.add(Component.empty());
            for (String key : ds.keySet()) {
                suffixes.add(Component.empty());
                MutableComponent line = Component.literal(key).withStyle(ChatFormatting.GOLD);
                list.add(line);
                i++;
            }
            for (String key : fs.keySet()) {
                if (fs.get(key) == 0L) { suffixes.add(Component.empty()); }
                else {
                    suffixes.add(Component.literal(Util.instance.getTextReducedNumber(fs.get(key), false, false, true) + "b"));
                }
                MutableComponent line = Component.literal(key);
                line.withStyle(line.getStyle().withColor(0xCAEAEA));
                list.add(line);
                if (txrName.equals(key)) {
                    pos = i;
                }
                i++;
            }
            list.add(0, back);
            scroll.setUnsortedList(list).setSuffixes(suffixes);
            if (scroll.getHover() != pos) { scroll.setSelected(pos); }
            domain = Component.empty().append(Component.literal(selectDir.getNamespace() + "/" + path));
            while (minecraft.font.width(domain) > 250 && path.contains("/")) {
                path = path.substring(path.indexOf("/") + 1);
                domain = Component.empty().append(Component.literal(selectDir.getNamespace() + "/.../" + path));
            }
        }
        add(scroll.setPos(guiLeft + 5, guiTop + 19));
        addLabel(0, guiLeft + 6, guiTop + 6, domain)
                .setSize(250, 10)
                .setColor(new Color(0xFF000000).getRGB());
        addButton(66, guiLeft + imageWidth - 17, guiTop + 5, "X")
                .setSize(12, 12);
        select = scroll.getNormalSelected();
    }

    @Override
    public boolean keyPressed(int key, int key_1, int key_2) {
        if (shouldCloseOnEsc() && GuiBasic.isEscKey(key)) { cancel(); }
        if (scroll != null && scroll.getSearchValue().isEmpty() &&
                key == InputConstants.KEY_BACKSPACE) {
            List<String> list = scroll.getList();
            if (!list.isEmpty() && list.get(0).equals(back.getString())) {
                if (selectDir != null) {
                    if (!selectDir.getPath().contains("/")) { selectDir = null; }
                    else { selectDir = new ResourceLocation(selectDir.getNamespace(), selectDir.getPath().substring(0, selectDir.getPath().lastIndexOf("/"))); }
                    init();
                    return true;
                }
            }
        }
        return super.keyPressed(key, key_1, key_2);
    }

    @Override
    public void scrollClicked(GuiCustomScrollNop scroll) {
        if (scroll.getNormalSelected().equals(back)) {
            if (selectDir == null) { return; }
            if (!selectDir.getPath().contains("/")) { selectDir = null; }
            else { selectDir = new ResourceLocation(selectDir.getNamespace(), selectDir.getPath().substring(0, selectDir.getPath().lastIndexOf("/"))); }
            init();
        }
        else if (selectDir != null) {
            if (!scroll.getSelected().endsWith(suffix)) {
                if (suffix.equals(".ogg")) { resource = new ResourceLocation(selectDir.getNamespace(), scroll.getSelected()); }
                else {
                    selectDir = new ResourceLocation(selectDir.getNamespace(), selectDir.getPath() + "/" + scroll.getSelected());
                    init();
                }
            } else {
                resource = new ResourceLocation(selectDir.getNamespace(), selectDir.getPath() + "/" + scroll.getSelected());
            }
        }
        else if (data.containsKey(scroll.getSelected())) {
            String res = null, def = null;
            for (ResourceLocation loc : data.get(scroll.getSelected()).keySet()) {
                if (def == null) {
                    if (loc.getPath().contains("/")) { def = loc.getPath().substring(0, loc.getPath().indexOf("/")); }
                    else { def = loc.getPath(); }
                }
                if (loc.getPath().contains("/") &&
                        loc.getPath().substring(0, loc.getPath().indexOf("/")).equals("textures")) {
                    res = "textures";
                    break;
                }
            }
            if (res == null) { res = def; }
            if (res != null) { selectDir = new ResourceLocation(scroll.getSelected(), res); }
            init();
        }
    }

    @Override
    public void scrollDoubleClicked(GuiCustomScrollNop scroll) {
        if (resource != null) {
            onClose();
            if (parent instanceof GuiBasic gui) { gui.init(); }
            else if (parent instanceof GuiBasicContainer<?> gui) { gui.init(); }
        }
    }

    protected void cancel() {
        if (!baseResource.isEmpty()) { resource = new ResourceLocation(baseResource); }
        else { resource = null; }
    }

    protected void resetFiles() {
        data.clear();
        if (suffix.isEmpty()) { return; }
        if (minecraft == null) { minecraft = Minecraft.getInstance(); }
        if (suffix.equals(".png")) {
            /* Texture manager data */
            for (ResourceLocation key : ((ITextureManagerMixin) minecraft.getTextureManager()).getByPath().keySet()) {
                addFile(key);
            }
            /* Texture blocks data */
            Map<ResourceLocation, TextureAtlasSprite> texturesByName = ((ITextureAtlasMixin) minecraft.getModelManager()
                    .getAtlas(new ResourceLocation("minecraft", "textures/atlas/blocks.png"))).getTexturesByName();
            for (ResourceLocation key : texturesByName.keySet()) {
                addFile(new ResourceLocation(key.getNamespace(), "textures/" + key.getPath() + ".png"));
            }
        }
        /* Mod jars */
        for (IModInfo mod : ModList.get().getMods()) {
            Optional<? extends ModContainer> modContainer = ModList.get().getModContainerById(mod.getModId());
            modContainer.ifPresent(container -> progressPath(container.getModInfo().getOwningFile().getFile().getFilePath()));
        }
        /* Resource packs */
        PackRepository repos = Minecraft.getInstance().getResourcePackRepository();
        for (String packName : repos.getAvailableIds()) {
            Pack pack = repos.getPack(packName);
            if (pack == null) { continue; }
            progressPackResources(pack.open());
        }
        /* Custom mod resources */
        checkFolder(new File(CustomNpcs.Dir, "assets"));
    }

    protected void progressPackResources(PackResources packResources) {
        if (packResources instanceof IFilePackResourcesMixin filePack) {
            progressFile(filePack.getFile());
            try { checkZipFile(filePack.getZipFile()); } catch (Exception ignored) { }
        }
        else if (packResources instanceof DelegatingPackResources delegatingPack) {
            Map<String, List<PackResources>> map = ((IDelegatingPackResourcesMixin) delegatingPack).getNamespacesAssets();
            for (String mod : map.keySet()) {
                for (PackResources packRes : map.get(mod)) {
                    progressPackResources(packRes);
                }
            }
        }
        else if (packResources instanceof VanillaPackResources vanillaPack) {
            for (Path path : ((IVanillaPackResourcesMixin) vanillaPack).getPathsForType().get(PackType.CLIENT_RESOURCES)) {
                progressPath(path);
            }
        }
    }

    protected void progressPath(Path path) {
        if (path == null) { return; }
        try {
            progressFile(path.toFile());
            return;
        }
        catch (Throwable ignored) { }
        if (!Files.exists(path)) { return; }
        Set<Path> allPaths = new HashSet<>();
        try (Stream<Path> stream = Files.walk(path, FileVisitOption.FOLLOW_LINKS)) { stream.filter(Files::isRegularFile).forEach(allPaths::add); }
        catch (Throwable ignored) { return; }
        for (Path p : allPaths) { addPath(p); }
    }

    protected void progressFile(File file) {
        try {
            if (!file.isDirectory() && (file.getName().endsWith(".jar") || file.getName().endsWith(".zip"))) { checkZipFile(new ZipFile(file)); }
            else if (file.isDirectory()) { checkFolder(file); }
        } catch (Exception e) { LogWriter.error("Error:", e); }
    }

    protected void addPath(Path path) {
        if (path == null) { return; }
        String p = path.toString();
        if (!p.startsWith("assets")) { return; }
        p = p.substring(7);
        if (!p.contains("/")) { return; }
        //ResourceLocation location = new ResourceLocation()
        long size = 0L;
        try  {
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
            size = attrs.size();
        }
        catch (Exception e) { LogWriter.info("Error create png file path: "+e); }
        try  {
            ResourceLocation location = new ResourceLocation(p.substring(0, p.indexOf("/")), p.substring(p.indexOf("/") + 1));
            if (!suffix.isEmpty() && !location.getPath().toLowerCase().endsWith(suffix.toLowerCase())) { return; }
            if (!data.containsKey(location.getNamespace())) { data.put(location.getNamespace(), new TreeMap<>()); }
            else {
                for (ResourceLocation r : data.get(location.getNamespace()).keySet()) {
                    if (r.getPath().equals(location.getPath())) { return; }
                }
            }
            data.get(location.getNamespace()).put(location, size);
        }
        catch (Exception ignored) {}
    }

    protected void addFile(ResourceLocation location) {
        String path = location.getPath();
        if (!suffix.isEmpty() && !path.toLowerCase().endsWith(suffix.toLowerCase())) { return; }
        String domain = location.getNamespace();
        if (!data.containsKey(domain)) { data.put(domain, new TreeMap<>()); }
        else {
            for (ResourceLocation r : data.get(domain).keySet()) {
                if (r.getPath().equals(path)) { return; }
            }
        }
        long size = 0L;
        try {
            Optional<Resource> res = Minecraft.getInstance().getResourceManager().getResource(location);
            if (res.isPresent()) {
                Resource stream = res.get();
                try (InputStream inputStream = stream.open()) { size = inputStream.available(); }
            }
        }
        catch (Exception ignored) { }
        data.get(domain).put(location, size);
    }

    private void addFile(String path, long size) {
        if (path == null || !path.contains("assets") || (!suffix.isEmpty() && !path.toLowerCase().endsWith(suffix.toLowerCase()))) { return; }
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
        ResourceLocation res = new ResourceLocation(domain, path);
        if (!data.containsKey(domain)) {
            data.put(domain, new TreeMap<>());
        } else {
            for (ResourceLocation r : data.get(domain).keySet()) {
                if (r.getPath().equals(path)) { return; }
            }
        }
        data.get(domain).put(res, size);
    }

    private void checkZipFile(ZipFile zip) throws IOException {
        if (zip == null) { return; }
        Enumeration<? extends ZipEntry> entries = zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry zipentry = entries.nextElement();
            String entryName = zipentry.getName();
            int a = entryName.indexOf("assets");
            int t = entryName.indexOf("texture", a);
            if (a != -1 && t != -1) {
                addFile(entryName, zipentry.getSize());
            }
        }
        zip.close();
    }

    private void checkFolder(File file) {
        if (file != null) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        checkFolder(f);
                        continue;
                    }
                    addFile(f.getAbsolutePath(), f.length());
                }
            }
        }
    }

    public ResourceSelection setOffsetX(int posX) {
        offsetX = posX;
        return this;
    }

}
