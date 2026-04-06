package noppes.npcs.client.gui.availability;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import noppes.npcs.api.IPos;
import noppes.npcs.api.IWorld;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.constants.EnumAvailabilityRegion;
import noppes.npcs.controllers.BorderController;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.controllers.data.Zone3D;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketTeleportTo;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;

import java.util.*;

// New from Unofficial (BetaZavr)
public class SubGuiNpcAvailabilityRegions
        extends GuiNPCInterface
        implements ICustomScrollListener {

    private final Availability availability;
    private final Map<Component, Integer> data = new LinkedHashMap<>();
    private GuiCustomScrollNop scroll;
    private Component select = Component.empty();

    public SubGuiNpcAvailabilityRegions(Availability availabilityIn) {
        super();
        setBackground("smallbg.png");
        imageWidth = 176;
        imageHeight = 222;
        closeOnEsc = true;

        availability = availabilityIn;
    }

    @Override
    public void buttonEvent(GuiButtonNop guiButton) {
        switch (guiButton.id) {
            case 0: {
                if (!data.containsKey(select)) { return; }
                Zone3D region = BorderController.getInstance().regions.get(data.get(select));
                if (region != null) {
                    Packets.sendAll(new SPacketTeleportTo(ResourceKey.create(Registries.DIMENSION, region.dimension), region.getCenter().getMCBlockPos()));
                }
                break;
            }
            case 1: {
                if (data.containsKey(select)) {
                    int id = data.get(select);
                    if (guiButton.getValue() == 0) { availability.regions.remove(id); }
                    else {
                        if (!availability.regions.containsKey(id)) { availability.regions.put(id, EnumAvailabilityRegion.Always); }
                        else { availability.regions.put(id, EnumAvailabilityRegion.values()[guiButton.getValue() - 1]); }
                    }
                    init();
                }
                break;
            }
            case 66: onClose(); break;
        }
    }

    @Override
    public void init() {
        super.init();
        int x = guiLeft + 5;
        int y = guiTop + 197;
        boolean isSelect = !select.getString().isEmpty();
        // title
        addLabel(1, x - 1, guiTop + 4, "availability.available.7")
                .setSize(imageWidth - 8, 12)
                .setCenter(imageWidth - 12);
        // exit
        addButton(66, x, y, "gui.done")
                .setSize(70, 20)
                .setHoverTexts("hover.back");
        // data
        int selID = -1;
        if (isSelect && data.containsKey(select)) { selID = data.get(select); }
        data.clear();
        if (scroll == null) { scroll = addScroll(0).setSize(168, 179); }
        List<Component> list = new ArrayList<>();
        List<Component> suffixes = new ArrayList<>();
        Map<String, Map<Integer, Zone3D>> regionWorlds = new TreeMap<>();
        for (int id : BorderController.getInstance().regions.keySet()) {
            Zone3D region = BorderController.getInstance().regions.get(id);
            if (!regionWorlds.containsKey(region.getDimension())) { regionWorlds.put(region.getDimension(), new TreeMap<>()); }
            regionWorlds.get(region.getDimension()).put(id, region);
        }
        LinkedHashMap<Integer, List<Component>> hts = new LinkedHashMap<>();
        for (String worldID : regionWorlds.keySet()) {
            for (int id : regionWorlds.get(worldID).keySet()) {
                Zone3D region = regionWorlds.get(worldID).get(id);
                IPos iPos = region.getCenter();
                IWorld iWorld = Objects.requireNonNull(NpcAPI.Instance()).getIWorld(worldID);
                MutableComponent name = Component.translatable(region.getName());
                List<Component> hoverList = new ArrayList<>();
                hoverList.add(Component.empty()
                        .append(Component.literal("Name: ").withStyle(ChatFormatting.GRAY))
                        .append(name.withStyle(ChatFormatting.RESET)));
                hoverList.add(Component.empty()
                        .append(Component.literal("World ID: ").withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(region.getDimension()).withStyle(ChatFormatting.YELLOW))
                        .append(Component.literal(" - ").withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(iWorld.getDimension().getId()).withStyle(ChatFormatting.YELLOW)));
                hoverList.add(Component.empty()
                        .append(Component.literal("Center in X: ").withStyle(ChatFormatting.GRAY))
                        .append(Component.literal("" + (int) iPos.getX()).withStyle(ChatFormatting.GOLD))
                        .append(Component.literal(", Y:").withStyle(ChatFormatting.GRAY))
                        .append(Component.literal("" + (int) iPos.getY()).withStyle(ChatFormatting.GOLD))
                        .append(Component.literal(", Z:").withStyle(ChatFormatting.GRAY))
                        .append(Component.literal("" + (int) iPos.getZ()).withStyle(ChatFormatting.GOLD))

                );
                hts.put(list.size(), hoverList);
                Component key;
                if (availability.regions.containsKey(id)) {
                    key = Component.empty()
                            .append(Component.literal("ID:").withStyle(ChatFormatting.GRAY))
                            .append(Component.literal("" + id).withStyle(ChatFormatting.RESET))
                            .append(Component.literal(" - ").withStyle(ChatFormatting.GRAY))
                            .append(name.withStyle(ChatFormatting.RESET));
                    if (availability.regions.get(id) == EnumAvailabilityRegion.Always) { suffixes.add(Component.literal("A").withStyle(ChatFormatting.GREEN)); }
                    else if (availability.regions.get(id) == EnumAvailabilityRegion.InSide) { suffixes.add(Component.literal("In").withStyle(ChatFormatting.AQUA)); }
                    else { suffixes.add(Component.literal("Out").withStyle(ChatFormatting.LIGHT_PURPLE)); }
                } else {
                    key = Component.literal("ID:" + id + " - " + name).withStyle(ChatFormatting.GRAY);
                    suffixes.add(Component.literal("N").withStyle(ChatFormatting.RED));
                }
                data.put(key, id);
                if (select.getString().isEmpty() || selID == id) {
                    select = key;
                    isSelect = true;
                }
                list.add(key);
            }
        }
        scroll.setUnsortedList(list)
                .setSuffixes(suffixes)
                .setHoverTexts(hts);
        if (isSelect) { scroll.setSelected(select); }
        add(scroll.setPos(guiLeft + 4, guiTop + 14));
        EnumAvailabilityRegion aData = null;
        if (isSelect && data.containsKey(select) && availability.regions.containsKey(data.get(scroll.getNormalSelected()))) { aData = availability.regions.get(data.get(scroll.getNormalSelected())); }
        // tp
        addButton(0, x += 73, y, "TP")
                .setSize(20, 20)
                .setIsEnabled(isSelect)
                .setHoverTexts("hover.teleport");
        // type
        addButton(1, x + 23, y, false, aData == null ? 0 : aData.ordinal() + 1,
                "gui.disabled", "availability.always", "availability.inside", "availability.outside")
                .setSize(70, 20)
                .setIsEnabled(isSelect)
                .setHoverTexts("region.hover.available.type");
    }

    @Override
    public void scrollClicked(GuiCustomScrollNop scroll) {
        select = scroll.getNormalSelected();
        init();
    }

    @Override
    public void scrollDoubleClicked(GuiCustomScrollNop scroll) { }

}
