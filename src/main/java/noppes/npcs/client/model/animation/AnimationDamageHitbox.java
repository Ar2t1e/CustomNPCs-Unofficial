package noppes.npcs.client.model.animation;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class AnimationDamageHitbox {

    public float[] offset = new float[] { 1.2f, 0.95f, 0.0f }; // [ D:radius, H:height, W:addYaw ]
    public float[] scale = new float[] { 1.2f, 1.5f, 1.2f }; // [ x, y, z ]
    public int id;

    public AnimationDamageHitbox(int i) { id = i; }


    public void clear() {
        offset[0] = 1.2f;
        offset[1] = 0.95f;
        offset[2] = 0.0f;
        for (int i = 0; i < 3; i++) {
            scale[i] = 1.0f;
        }
    }

    public CompoundTag getNBT() {
        CompoundTag compound = new CompoundTag();

        compound.putInt("ID", id);

        ListTag listO = new ListTag();
        ListTag listS = new ListTag();
        for (int i = 0; i < 3; i++) {
            listO.add(FloatTag.valueOf(offset[i]));
            listS.add(FloatTag.valueOf(scale[i]));
        }
        compound.put("Offset", listO);
        compound.put("Scale", listS);

        return compound;
    }

    public AnimationDamageHitbox(CompoundTag compound, int i) {
        id = i;
        ListTag listO = compound.getList("Offset", 5);
        for (int j = 0; j < 3 && j < listO.size(); j++) { offset[j] = listO.getFloat(j); }
        ListTag listS = compound.getList("Scale", 5);
        for (int j = 0; j < 3 && j < listS.size(); j++) { scale[j] = listS.getFloat(j); }
    }

    public AABB getScaledDamageHitbox(LivingEntity entity) {
        AABB aabb = new AABB(-0.5d * scale[0], -0.5d * scale[1], -0.5d * scale[2],
                0.5d * scale[0], 0.5d * scale[1], 0.5d * scale[2])
                .move(entity.getX(), entity.getY(), entity.getZ());
        double radYaw = Math.toRadians(entity.getYRot()) + offset[2];
        return aabb.move(Math.sin(radYaw) * -offset[0], offset[1], Math.cos(radYaw) * offset[0]);
    }


    public Component getKey() {
        return Component.empty()
                .append(Component.literal("ID:").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(""+(id + 1)).withStyle(ChatFormatting.RESET))
                .append(Component.literal("; d:").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(""+Math.round(offset[0] * 10.0d) / 10.0d).withStyle(ChatFormatting.GREEN))
                .append(Component.literal("; h:").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(""+Math.round(offset[1] * 10.0d) / 10.0d).withStyle(ChatFormatting.GREEN))
                .append(Component.literal("; w:").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(""+Math.round(offset[2] * 10.0d) / 10.0d).withStyle(ChatFormatting.GREEN));
    }

    public List<Component> getHoverKey() {
        List<Component> list = new ArrayList<>();
        list.add(Component.empty()
                .append(Component.literal("ID:").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(""+(id + 1)).withStyle(ChatFormatting.GOLD)));
        list.add(Component.empty()
                .append(Component.literal("D:").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(""+offset[0]).withStyle(ChatFormatting.GREEN)));
        list.add(Component.empty()
                .append(Component.literal("H:").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(""+offset[1]).withStyle(ChatFormatting.GREEN)));
        list.add(Component.empty()
                .append(Component.literal("W:").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(""+offset[2]).withStyle(ChatFormatting.GREEN)));
        list.add(Component.empty()
                .append(Component.literal("Scale X:").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(""+scale[0]).withStyle(ChatFormatting.AQUA)));
        list.add(Component.empty()
                .append(Component.literal("Scale Y:").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(""+scale[1]).withStyle(ChatFormatting.AQUA)));
        list.add(Component.empty()
                .append(Component.literal("Scale Z:").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(""+scale[2]).withStyle(ChatFormatting.AQUA)));
        return list;
    }

}
