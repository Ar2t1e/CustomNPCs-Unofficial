package noppes.npcs.util;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.ClientTickHandler;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketDebug;
import noppes.npcs.shared.common.util.LogWriter;

import javax.annotation.Nonnull;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.*;

public class DataDebug {

    private static boolean DebugMonitoring = false;

    public static class Debug {

        private long max = 0L;
        private final Map<String, Long> starters = new HashMap<>(); // temp time [unique key, start time]
        private final Map<String, Map<String, Long[]>> times = new HashMap<>(); // Name, [count, all time in work]

        public void end(String eventName, String eventTarget) {
            if (eventName == null || eventTarget == null) { return; }
            String key = Thread.currentThread().getId() + ":" + eventName + ":" + eventTarget;
            if (!starters.containsKey(key)) {
                LogWriter.debug("Double ending debug \""+key+"\"");
                return;
            }
            if (!times.containsKey(eventName)) { times.put(eventName, new HashMap<>()); }
            if (!times.get(eventName).containsKey(eventTarget)) { times.get(eventName).put(eventTarget, new Long[] { 0L, 0L }); }
            Long[] arr = times.get(eventName).get(eventTarget);
            arr[0]++;
            long r = System.currentTimeMillis() - starters.get(key);
            if (arr[1] < r) { arr[1] = r; }
            if (max < r  &&
                    !key.toLowerCase().contains("register") &&
                    !key.toLowerCase().contains("load") &&
                    !key.toLowerCase().contains("<init>")) { max = r; }
            times.get(eventName).put(eventTarget, arr);
            starters.remove(key);
        }

        public void start(String eventName, String eventTarget) {
            if (eventName == null || eventTarget == null) { return; }
            String key = Thread.currentThread().getId() + ":" + eventName + ":" + eventTarget;
            if (starters.containsKey(key)) { LogWriter.debug("Double starting debug \"" + key + "\""); }
            starters.put(key, System.currentTimeMillis());
        }

    }

    public long started = 0L;
    public long startedTicks = 0L;
    private final Map<Dist, Debug> data = new HashMap<>();

    private static @Nonnull String getKey(Object target) {
        if (target == null) { return ""; }
        if (target instanceof String) { return (String) target; }
        if (target instanceof Player) { return "Players"; }
        if (target instanceof EntityNPCInterface) { return "NPC"; }
        if (target instanceof Entity) { return "MOBs"; }
        if (target instanceof Class<?>) { return ((Class<?>) target).getSimpleName(); }
        if (target instanceof CommandSourceStack source) {
            return source.getPlayer() != null ? source.getPlayer().getName().getString() :
                    source.getEntity() != null ? source.getEntity().getName().getString() : "CommandBlock";
        }
        return target.getClass().getSimpleName();
    }

    public void end(Object target) { end(target, ""); }

    public void end(Object target, String addedToMethodName) {
        if (!DebugMonitoring) { return; }
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
        String obj = caller.getClassName();
        Dist side = caller.getMethodName().equals("findChunksForSpawning") || caller.getMethodName().equals("performWorldGenSpawning") ?
                Dist.DEDICATED_SERVER :
                Util.instance.getSide();
        String trg = getKey(target);
        int dotPos = obj.lastIndexOf(".") + 1;
        if (dotPos > 0) { obj = obj.substring(dotPos); }
        if (trg.equals("Packets")) { data.get(side).start(trg, obj); }
        else {
            if (!trg.isEmpty()) { trg = "_" + trg; }
            String methodName = getMethodName(caller.getMethodName(), addedToMethodName);
            data.get(side).end(obj, methodName + trg);
        }
    }

    public void start(Object target) { start(target, ""); }

    public void start(Object target, String addedToMethodName) {
        if (!DebugMonitoring) { return; }
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
        String obj = caller.getClassName();
        Dist side = caller.getMethodName().equals("findChunksForSpawning") || caller.getMethodName().equals("performWorldGenSpawning") ?
                Dist.DEDICATED_SERVER :
                Util.instance.getSide();
        String trg = getKey(target);
        int dotPos = obj.lastIndexOf(".") + 1;
        if (dotPos > 0) { obj = obj.substring(dotPos); }
        if (trg.equals("Packets")) { data.get(side).start(trg, obj); }
        else {
            if (!trg.isEmpty()) { trg = "_" + trg; }
            String methodName = getMethodName(caller.getMethodName(), addedToMethodName);
            data.get(side).start(obj, methodName + trg);
        }
    }

    private String getMethodName(String method, String added) {
        if (method.startsWith("lambda$")) {
            method = method.substring(method.indexOf("$") + 1);
            if (method.contains("$")) { method = method.substring(0, method.indexOf("$")); }
        }
        if (method.contains("npcs$")) {
            method = method.substring(method.lastIndexOf("npcs$") + 5);
            if (method.lastIndexOf("Start") != -1) { method = method.substring(0, method.lastIndexOf("Start")); }
            if (method.lastIndexOf("End") != -1) { method = method.substring(0, method.lastIndexOf("End")); }
        }
        return method + "(" + added + ")";
    }

    public DataDebug() { clear(); }

    public void stop() {
        if (!DebugMonitoring) { return; }
        for (Dist side : data.keySet()) {
            for (String k : data.get(side).starters.keySet()) {
                data.get(side).end(k.substring(0, k.indexOf(':')), k.substring(k.indexOf(':') + 1));
            }
        }
    }

    public void clear() {
        stop();
        data.put(Dist.DEDICATED_SERVER, new Debug());
        data.put(Dist.CLIENT, new Debug());
    }

    public List<String> logging() {
        StringBuilder tempInfo;
        String temp;
        LogWriter.info("Output full debug info " + CustomNpcs.MODNAME + ":");
        LogWriter.info("Showing Monitoring results for ANY side. { Side: [Target.Method names; Runs; Average time] }:");
        StringBuilder maxInfo = new StringBuilder("Output maximums from debug info ").append(CustomNpcs.MODNAME).append(":");
        stop();
        boolean start = false;
        List<String> list = new ArrayList<>();
        for (Dist side : data.keySet()) {
            if (start) { LogWriter.info("----   ----   ----"); }
            LogWriter.info("Side: " + side.name().replace("DEDICATED_", ""));
            List<String> events = new ArrayList<>(data.get(side).times.keySet());
            Collections.sort(events);
            int i = 0;
            long max = Long.MIN_VALUE;
            Object[][] maxInSide = new Object[2][4];
            for (String eventName : events) {
                DataDebug.Debug dd = data.get(side);
                List<String> targets = new ArrayList<>(data.get(side).times.get(eventName).keySet());
                Collections.sort(targets);
                StringBuilder log = new StringBuilder();
                if (targets.size() > 1) { log.append("\n"); }
                int s = 0;
                for (String target : targets) {
                    Long[] time = data.get(side).times.get(eventName).get(target);
                    if (time[0] <= 0) { time[0] = 1L; }
                    if (target.lastIndexOf("_") != -1) {
                        log.append("  [").append(target, 0, target.lastIndexOf("_"))
                                .append(", ").append(target.substring(target.lastIndexOf("_") + 1));
                    }
                    else { log.append("  [").append(target); }
                    log.append(", ")
                            .append(time[0])
                            .append(", ")
                            .append(Util.instance.ticksToElapsedTime(time[1], true, false, false))
                            .append("]");
                    if (s < targets.size() - 1) { log.append(";\n"); }
                    if (time[1] == dd.max) {
                        maxInSide[0][0] = eventName;
                        maxInSide[0][1] = target;
                        maxInSide[0][2] = time[0];
                        maxInSide[0][3] = time[1];
                    }
                    if (max < time[0]) {
                        max = time[0];
                        maxInSide[1][0] = eventName;
                        maxInSide[1][1] = target;
                        maxInSide[1][2] = time[0];
                        maxInSide[1][3] = time[1];
                    }
                    s++;
                }
                LogWriter.info(" [" + (i + 1) + "/" + events.size() + "] - \"" + eventName + "\": " + log);
                i++;
            }
            // long time
            if (maxInSide[0][0] != null) {
                tempInfo = new StringBuilder(ChatFormatting.GRAY.toString())
                        .append(" \"")
                        .append(ChatFormatting.RESET)
                        .append(side.name())
                        .append(ChatFormatting.GRAY)
                        .append("\" a long time [")
                        .append(ChatFormatting.BLUE)
                        .append(maxInSide[0][0])
                        .append(ChatFormatting.GRAY)
                        .append(".")
                        .append(ChatFormatting.DARK_GREEN)
                        .append(maxInSide[0][1])
                        .append(ChatFormatting.GRAY)
                        .append("; runs: ")
                        .append(ChatFormatting.GOLD)
                        .append(maxInSide[0][2])
                        .append(ChatFormatting.GRAY)
                        .append("; time = ")
                        .append(ChatFormatting.RESET)
                        .append(Util.instance.ticksToElapsedTime((long) maxInSide[0][3], true, true, false))
                        .append(ChatFormatting.GRAY)
                        .append("]");
                temp = "\n" + Util.instance.deleteColor(tempInfo.toString());
                maxInfo.append(temp);
                list.add(tempInfo.toString());
            }
            // max count
            if (maxInSide[1][0] != null) {
                tempInfo = new StringBuilder(ChatFormatting.DARK_GRAY.toString())
                        .append(" \"")
                        .append(ChatFormatting.RESET)
                        .append(side.name())
                        .append(ChatFormatting.GRAY)
                        .append("\" most often [")
                        .append(ChatFormatting.BLUE)
                        .append(maxInSide[1][0])
                        .append(ChatFormatting.GRAY)
                        .append(".")
                        .append(ChatFormatting.DARK_GREEN)
                        .append(maxInSide[1][1])
                        .append(ChatFormatting.GRAY)
                        .append("; runs: ")
                        .append(ChatFormatting.GOLD)
                        .append(maxInSide[1][2])
                        .append(ChatFormatting.GRAY)
                        .append("; time = ")
                        .append(ChatFormatting.RESET)
                        .append(Util.instance.ticksToElapsedTime((long) maxInSide[1][3], true, true, false))
                        .append(ChatFormatting.GRAY)
                        .append("]");
                temp = "\n" + Util.instance.deleteColor(tempInfo.toString());
                maxInfo.append(temp);
                list.add(tempInfo.toString());
            }
            start = true;
        }
        // Caches
        list.add("Caches:");
        LogWriter.info("Caches:");
        if (started != 0) {
            long time = (System.currentTimeMillis() - started) / 50L;
            long ticks;
            String side;
            if (CustomNpcs.Server != null && CustomNpcs.Server.getLevel(Level.OVERWORLD) != null) {
                side = "Server";
                ServerLevel level = CustomNpcs.Server.getLevel(Level.OVERWORLD);
                ticks = 0L;
                if (level != null) { ticks = level.getGameTime() - startedTicks; }
            } else {
                side = "Client";
                ticks = ClientTickHandler.ticks - startedTicks;
            }
            tempInfo = new StringBuilder(ChatFormatting.GRAY.toString())
                    .append(side)
                    .append(" system running time ")
                    .append(Util.instance.ticksToElapsedTime(time, false, true, false))
                    .append(ChatFormatting.GRAY)
                    .append(", game running time ")
                    .append(Util.instance.ticksToElapsedTime(ticks, false, true, false))
                    .append(ChatFormatting.GRAY)
                    .append(" (")
                    .append(ChatFormatting.BLUE)
                    .append(Math.round((double) Math.abs(time - ticks) / (double) time * 1000.0d) /1000.0d)
                    .append(ChatFormatting.GRAY)
                    .append("%)");
            list.add(tempInfo.toString());
            LogWriter.info(Util.instance.deleteColor(tempInfo.toString()));
        }

        MemoryUsage memUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        tempInfo = new StringBuilder(ChatFormatting.GRAY.toString())
                .append("Current total memory: ")
                .append(ChatFormatting.RESET)
                .append(Util.instance.getTextReducedNumber(memUsage.getUsed(), false, true, true))
                .append(ChatFormatting.GRAY)
                .append(" / ")
                .append(ChatFormatting.RESET)
                .append(Util.instance.getTextReducedNumber(memUsage.getMax(), false, true, true))
                .append(ChatFormatting.GRAY)
                .append(" byte");
        list.add(tempInfo.toString());
        LogWriter.info(Util.instance.deleteColor(tempInfo.toString()));

        LogWriter.info(maxInfo);
        return list;
    }

    public void startDebugging(CommandSourceStack sender) {
        if (!DebugMonitoring) {
            DebugMonitoring = true;
            CustomNPCsScheduler.runTack(() -> stopDebugging(sender), 50000); // 5 min max
        }
    }

    public void stopDebugging(CommandSourceStack sender) {
        DebugMonitoring = false;
        if (sender != null) {
            List<String> list = CustomNpcs.debugData.logging();
            if (!list.isEmpty()) {
                sender.sendSuccess(() -> Component.literal("Server info:"), false);
                for (String str : list) { sender.sendSuccess(() -> Component.literal(str), false); }
            }
            if (sender.getPlayer() != null && (CustomNpcs.Server == null || !CustomNpcs.Server.isSingleplayer())) {
                sender.sendSuccess(() -> Component.literal("Client info:"), false);
                Packets.send(sender.getPlayer(), new PacketDebug(true));
            }
            if (sender.getPlayer() != null) {
                Packets.send(sender.getPlayer(), new PacketDebug(false));
            }
            CustomNPCsScheduler.runTack(() -> sender.sendSuccess(() -> Component.translatable("command.debug.show"), false), 1000);
            sender.sendSuccess(() -> Component.translatable("command.debug.clear"), false);
        }
        clear();
    }

}
