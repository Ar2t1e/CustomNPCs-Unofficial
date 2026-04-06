package noppes.npcs.controllers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.data.DropsTemplate;
import noppes.npcs.entity.data.AttributeSet;
import noppes.npcs.entity.data.DropSet;
import noppes.npcs.entity.data.EnchantSet;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketDropTemplateClear;
import noppes.npcs.packets.client.PacketDropTemplateSave;
import noppes.npcs.packets.client.PacketGuiUpdate;
import noppes.npcs.packets.server.SPacketDropTemplateClear;
import noppes.npcs.packets.server.SPacketDropTemplateSave;
import noppes.npcs.shared.common.util.LogWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DropController {

    protected static DropController instance;
    public final Map<String, DropsTemplate> templates = new TreeMap<>();

    public static DropController getInstance() {
        if (instance == null) { instance = new DropController(); }
        return instance;
    }

    public List<DropSet> getDrops(String saveDropsName) {
        List<DropSet> allDrops = new ArrayList<>();
        if (saveDropsName == null || saveDropsName.isEmpty() || !this.templates.containsKey(saveDropsName)) { return allDrops; }
        DropsTemplate template = this.templates.get(saveDropsName);
        if (template == null) { return allDrops; }
        return template.getDrops();
    }

    public CompoundTag getNBT() {
        CompoundTag nbtFile = new CompoundTag();
        ListTag templates = new ListTag();
        for (String template : this.templates.keySet()) {
            CompoundTag nbtTemplate = new CompoundTag();
            nbtTemplate.putString("Name", template);
            nbtTemplate.put("Groups", this.templates.get(template).getNBT());
            templates.add(nbtTemplate);
        }
        nbtFile.put("Templates", templates);
        return nbtFile;
    }

    private void loadDefaultDrops() {
        DropsTemplate temp = new DropsTemplate();
        temp.groups.put(0, new TreeMap<>());
        DropSet ds0 = new DropSet(null);
        ds0.amount[0] = 5;
        ds0.amount[1] = 8;
        ds0.chance = 72.5d;
        ds0.item = new ItemStack(Items.COAL);
        ds0.pos = 0;
        temp.groups.get(0).put(0, ds0);
        DropSet ds1 = new DropSet(null);
        ds1.amount[0] = 2;
        ds1.amount[1] = 5;
        ds1.chance = 8.0d;
        ds1.item = new ItemStack(Items.IRON_INGOT);
        ds1.pos = 1;
        temp.groups.get(0).put(1, ds1);
        DropSet ds2 = new DropSet(null);
        ds2.amount[0] = 1;
        ds2.amount[1] = 3;
        ds2.chance = 4.3333d;
        ds2.item = new ItemStack(Items.GOLD_INGOT);
        ds2.pos = 2;
        temp.groups.get(0).put(2, ds2);
        DropSet ds3 = new DropSet(null);
        ds3.amount[0] = 1;
        ds3.amount[1] = 2;
        ds3.chance = 0.575d;
        ds3.item = new ItemStack(Items.DIAMOND);
        ds3.pos = 3;
        temp.groups.get(0).put(3, ds3);

        temp.groups.put(1, new TreeMap<>());
        DropSet df0 = new DropSet(null);
        df0.amount[0] = 1;
        df0.amount[1] = 1;
        df0.chance = 2.5d;
        df0.item = new ItemStack(Items.IRON_AXE);
        df0.pos = 0;
        EnchantSet ench0 = (EnchantSet) df0.addEnchant(Enchantments.UNBREAKING);
        ench0.setChance(50.0d);
        ench0.setLevels(1, 5);
        AttributeSet attr = (AttributeSet) df0.addAttribute(Attributes.ATTACK_DAMAGE.getDescriptionId());
        attr.setChance(25.0d);
        attr.setValues(1.0d, 3.0d);
        attr.setSlot(0);
        df0.addDropNbtSet(8, 12.5d, "display.Name", new String[] { "Sword", "Axe" });
        temp.groups.get(1).put(0, df0);
        this.templates.put("default", temp);
        this.save();
    }

    public void loadFile() {
        CustomNpcs.debugData.start(null);
        LogWriter.info("Loading Drops");
        try {
            File file = new File(CustomNpcs.getLevelSaveDirectory(), "drops.dat");
            if (file.exists()) {
                try {
                    CompoundTag nbtFile = NbtIo.readCompressed(new FileInputStream(file));
                    this.loadNBTData(nbtFile);
                } catch (IOException e) { LogWriter.error(e); }
            } else {
                this.templates.clear();
                this.loadDefaultDrops();
            }
        } catch (Exception e) {
            LogWriter.error(e);
            try {
                File file2 = new File(CustomNpcs.Dir, "recipes.dat_old");
                if (file2.exists()) {
                    try {
                        CompoundTag nbtFile = NbtIo.readCompressed(new FileInputStream(file2));
                        this.loadNBTData(nbtFile);
                    } catch (IOException err) {
                        LogWriter.error(err);
                    }
                }
            } catch (Exception ee) {
                LogWriter.error(ee);
            }
        }
        CustomNpcs.debugData.end(null);
    }

    public void loadNBTData(CompoundTag nbtFile) {
        this.templates.clear();
        if (nbtFile.contains("Templates", 9)) {
            for (int i = 0; i < nbtFile.getList("Templates", 10).size(); i++) {
                CompoundTag nbtTemplate = nbtFile.getList("Templates", 10).getCompound(i);
                if (!nbtTemplate.contains("Name", 8)) {
                    continue;
                }
                this.templates.put(nbtTemplate.getString("Name"),
                        new DropsTemplate(nbtTemplate.getCompound("Groups")));
            }
        }
        if (this.templates.isEmpty()) {
            this.loadDefaultDrops();
        }
    }

    public void save() {
        CustomNpcs.debugData.start(null);
        try {
            NbtIo.writeCompressed(this.getNBT(), new FileOutputStream(new File(CustomNpcs.getLevelSaveDirectory(), "drops.dat")));
        } catch (Exception e) { LogWriter.error(e); }
        CustomNpcs.debugData.end(null);
    }

    public void sendTo(ServerPlayer player) {
        if (templates.isEmpty()) { loadDefaultDrops(); }
        Map<String, DropsTemplate> tempMap = new TreeMap<>(templates);
        Packets.send(player, new PacketDropTemplateClear());
        for (String template : tempMap.keySet()) {
            CompoundTag nbtTemplate = new CompoundTag();
            nbtTemplate.putString("Name", template);
            nbtTemplate.put("Groups", tempMap.get(template).getNBT());
            Packets.send(player, new PacketDropTemplateSave(nbtTemplate));
        }
        Packets.send(player, new PacketGuiUpdate());
    }

    public void sendToServer(String dropTemplate) {
        if (templates.containsKey(dropTemplate)) {
            CompoundTag nbtTemplate = new CompoundTag();
            nbtTemplate.putString("Name", dropTemplate);
            nbtTemplate.put("Groups", templates.get(dropTemplate).getNBT());
            Packets.sendServer(new SPacketDropTemplateSave(nbtTemplate));
            return;
        }
        Packets.sendServer(new SPacketDropTemplateClear());
        Map<String, DropsTemplate> tempMap = new TreeMap<>(templates);
        for (String template : tempMap.keySet()) {
            CompoundTag nbtTemplate = new CompoundTag();
            nbtTemplate.putString("Name", template);
            nbtTemplate.put("Groups", tempMap.get(template).getNBT());
            Packets.sendServer(new SPacketDropTemplateSave(nbtTemplate));
        }
    }

}
