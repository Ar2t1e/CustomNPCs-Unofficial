package noppes.npcs.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.CommandNoppesBase;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketHeapAnalyzer;
import noppes.npcs.shared.client.gui.util.NoppesStringUtils;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.CustomNPCsScheduler;
import noppes.npcs.util.Util;
import noppes.npcs.util.ValueUtil;

import javax.annotation.Nonnull;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.File;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.lang.management.ManagementFactory;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class CmdHeapAnalyzer extends CommandNoppesBase {

    private static final long MB = 1024 * 1024;
    private static final Path DUMP_DIR;
    static {
        Path d;
        try { d = Loader.instance().getConfigDir().getParentFile().toPath().resolve("dumps"); }
        catch (Throwable t) { d = Paths.get("dumps").toAbsolutePath(); }
        DUMP_DIR = d;
    }

    private static final AtomicBoolean dumping = new AtomicBoolean(false);

    /* ===================== SESSION (Server/Client isolation) ===================== */
    private static class Session {
        final String side;
        volatile boolean running = false;
        Thread worker;
        int topCount = 30;
        final Object lock = new Object();

        public void start(ICommandSender sender, boolean isAuto) {
            running = true;
            state = Session.State.WAITING;
            path = null;
            bytes.clear();

            if (isAuto) {
                worker = new Thread(() -> loop(this, sender));
                worker.setDaemon(true);
                worker.setName(side + "-GC-Monitor");
                worker.start();
            }
            else {
                worker = null;
                first(sender);
            }
            sendMessageIn(sender, "command.dump.started." + isAuto, null);
        }

        public void first(ICommandSender sender) {
            if (path != null) { deleteFile(path); }
            path = dumpSync("first");
            bytes.clear();
            bytes.putAll(takeSnapshot());
            Runtime r = Runtime.getRuntime();
            if (state == Session.State.WAITING) { sendMessageIn(sender, "command.dump.first", mb(r.totalMemory() - r.freeMemory(), r.totalMemory())); }
            else { sendMessageIn(sender, "command.dump.refirst", mb(r.totalMemory() - r.freeMemory(), r.totalMemory())); }
            state = Session.State.DROPPED;
        }

        public void stop(ICommandSender sender, boolean isAuto, String sizes) {
            running = false;
            if (path != null) {
                String lastPath = dumpSync("last");
                if (lastPath != null) {
                    if (sizes == null) {
                        Runtime r = Runtime.getRuntime();
                        sizes = mb(r.totalMemory() - r.freeMemory(), r.totalMemory());
                    }
                    sendMessageIn(sender, "command.dump.second", sizes);
                    generateReport(this, sender, path, lastPath);
                }
                deleteFile(path);
                if (isAuto) {
                    path = null;
                    if (lastPath != null) { deleteFile(lastPath); }
                }
                else if (lastPath != null) { path = lastPath; }
            }
            else { sendMessageIn(sender, "command.dump.stopped.early", null); }
            bytes.clear();
            synchronized (lock) { lock.notifyAll(); }
            if (worker != null && Thread.currentThread() != worker) { worker.interrupt(); }
        }

        enum State { WAITING, DROPPED }
        State state = State.WAITING;
        String path;

        final Map<String, long[]> bytes = new HashMap<>();

        Session(String sideIn) { side = sideIn; }
    }
    private static final Session SERVER = new Session("Server");
    private static final Session CLIENT = new Session("Client");
    private static Session getSession(boolean isClient) { return isClient ? CLIENT : SERVER; }

    /* ===================== PUBLIC API ===================== */
    @Override
    public int getRequiredPermissionLevel() { return 4; }

    @Override
    public String getDescription() { return "Heap memory analyzer"; }

    @Override
    public @Nonnull String getName() { return "dump"; }

    @SubCommand(desc = "Start auto heap tracking", usage = "[topCount]", permission = 4)
    public void start(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        int count = 30;
        if (args.length > 0) {
            try { count = Integer.parseInt(args[0]); }
            catch (NumberFormatException e) { throw new CommandException("Value must be an integer. (" + args[0] + ")"); }
        }
        startTracking(sender, count);
    }

    @SubCommand(desc = "Stop auto heap tracking", usage = "[topCount]", permission = 4)
    public void stop(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        int count = 0;
        if (args.length > 0) {
            try { count = Integer.parseInt(args[0]); }
            catch (NumberFormatException e) { throw new CommandException("Value must be an integer. (" + args[0] + ")"); }
        }
        stopTracking(sender, count);
    }

    @SubCommand(desc = "Manual heap dump", usage = "[topCount]", permission = 4)
    public void manual(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        int count = 30;
        if (args.length > 0) {
            try { count = Integer.parseInt(args[0]); }
            catch (NumberFormatException e) { throw new CommandException("Value must be an integer. (" + args[0] + ")"); }
        }
        doManual(sender, count);
    }

    private static void setTopCount(int count, Session s) {
        if (count > 0) { s.topCount = ValueUtil.correctInt(count, 5, 100); }
    }

    private static void sendMessageIn(ICommandSender sender, String key, String added) {
        if (key == null || key.isEmpty()) return;
        if (added == null) added = "";
        String finalAdded = added;
        if (sender != null) {
            sender.sendMessage(net.minecraft.network.chat.Component.translatable(key, TextFormatting.AQUA + "Server", finalAdded).getParent());
        } else {
            EntityPlayer player = CustomNpcs.proxy.getPlayer();
            if (player != null) {
                player.sendMessage(net.minecraft.network.chat.Component.translatable(key, TextFormatting.YELLOW + "Client", finalAdded).getParent());
            }
        }
    }

    /* ===================== START / STOP / MANUAL ===================== */
    public static void startTracking(ICommandSender sender, int count) {
        boolean isClient = (sender == null);
        Session s = getSession(isClient);
        if (!isClient && sender instanceof EntityPlayerMP) {
            Packets.send((EntityPlayerMP) sender, new PacketHeapAnalyzer(PacketHeapAnalyzer.State.START, count));
        }
        if (s.running) {
            sendMessageIn(sender, "command.dump.already.running", null);
            return;
        }
        cleanupOldHprof();
        setTopCount(count, s);
        try { Files.createDirectories(DUMP_DIR); } catch (Exception e) { LogWriter.error(e); }
        s.start(sender, true);
    }

    public static void stopTracking(ICommandSender sender, int count) {
        boolean isClient = (sender == null);
        Session s = getSession(isClient);
        if (!isClient && sender instanceof EntityPlayerMP) {
            Packets.send((EntityPlayerMP) sender, new PacketHeapAnalyzer(PacketHeapAnalyzer.State.STOP, 0));
        }
        if (!s.running) {
            sendMessageIn(sender, "command.dump.not.running", null);
            return;
        }
        setTopCount(count, s);
        s.stop(sender, true, null);
    }

    public static void doManual(ICommandSender sender, int count) {
        boolean isClient = (sender == null);
        Session s = getSession(isClient);
        setTopCount(count, s);
        if (s.running) {
            if (!isClient && sender instanceof EntityPlayerMP) {
                Packets.send((EntityPlayerMP) sender, new PacketHeapAnalyzer(PacketHeapAnalyzer.State.STOP, 0));
            }
            s.stop(sender, false, null);
        }
        else {
            if (!isClient && sender instanceof EntityPlayerMP) {
                Packets.send((EntityPlayerMP) sender, new PacketHeapAnalyzer(PacketHeapAnalyzer.State.MANUAL, count));
            }
            try { Files.createDirectories(DUMP_DIR); } catch (Exception e) { LogWriter.error(e); }
            if (s.path == null) {
                s.start(sender, false);
            }
            else {
                s.stop(sender, false, null);
            }
        }
    }

    /* ===================== TRACKER LOOP ===================== */
    private static void loop(Session s, ICommandSender sender) {
        long lastUsed = 0L;
        long peakBytes = 0L;
        long waitTime = 50L;
        boolean isDown = true;
        while (!Thread.interrupted() && s.running) {
            Runtime r = Runtime.getRuntime();
            long used = r.totalMemory() - r.freeMemory();
            if (used < lastUsed) {
                peakBytes = lastUsed;
                waitTime = s.side.equals("Client") ? 5000L : 10000L;
                isDown = true;
                s.first(sender);
            }
            else {
                if (isDown && s.state == Session.State.DROPPED && used >= 0.98d * peakBytes) {
                    isDown = false;
                    s.stop(sender, true, mb(used, peakBytes));
                }
            }
            lastUsed = used;
            synchronized (s.lock) {
                try {
                    s.lock.wait(waitTime);
                    waitTime = 50L;
                } catch (InterruptedException e) { break; }
            }
        }
    }

    /* ===================== SNAPSHOT ===================== */
    private static Map<String, long[]> takeSnapshot() {
        Map<String, long[]> curr = new LinkedHashMap<>();
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = new ObjectName("com.sun.management:type=DiagnosticCommand");
            String histo = (String) mbs.invoke(name, "gcClassHistogram",
                    new Object[]{ new String[0] },
                    new String[]{ String[].class.getName() });
            for (String line : histo.split("\n")) {
                line = line.trim();
                if (line.isEmpty() || line.contains("class name") || line.contains("------")) continue;
                String[] p = line.split("\\s+");
                if (p.length >= 4) {
                    String cls = p[3];
                    long count = Long.parseLong(p[1]);
                    long bytes = Long.parseLong(p[2]);
                    curr.put(cls, new long[]{count, bytes});
                }
            }
        } catch (Exception e) { LogWriter.error(e); }
        return curr;
    }

    private static List<Diff> calcDiffMaps(Map<String, long[]> a, Map<String, long[]> b) {
        List<Diff> list = new ArrayList<>();
        for (Map.Entry<String, long[]> e : b.entrySet()) {
            long[] pa = a.getOrDefault(e.getKey(), new long[]{0, 0});
            long dCount = e.getValue()[0] - pa[0];
            long dBytes = e.getValue()[1] - pa[1];
            if (dCount != 0 || dBytes != 0)
                list.add(new Diff(formatClassName(e.getKey()), dCount, dBytes));
        }
        list.sort((aa, bb) -> Long.compare(Math.abs(bb.size), Math.abs(aa.size)));
        return list;
    }

    /* ===================== HPROF PARSER ===================== */
    @SuppressWarnings("StatementWithEmptyBody")
    public static Result parse(String path) throws Exception {
        try (RandomAccessFile raf = new RandomAccessFile(path, "r");
             FileChannel ch = raf.getChannel()) {

            MappedByteBuffer buf = ch.map(FileChannel.MapMode.READ_ONLY, 0, ch.size());
            while (buf.get() != 0) { } // skip null-terminated header text
            int idSize = buf.getInt();
            buf.getLong();                      // time

            Result r = new Result(idSize);

            // Pass 1: UTF8 + LOAD CLASS
            while (buf.hasRemaining()) {
                int tag = buf.get() & 0xFF;
                buf.getInt(); // timestamp, ignored
                int len = buf.getInt();
                int nxt = buf.position() + len;

                if (tag == 0x01) {
                    long id = readId(buf, idSize);
                    byte[] b = new byte[len - idSize];
                    buf.get(b);
                    r.utf8.put(id, new String(b, StandardCharsets.UTF_8));
                } else if (tag == 0x02) {
                    buf.getInt();
                    long cid = readId(buf, idSize);
                    buf.getInt();
                    long nid = readId(buf, idSize);
                    r.classIdToNameId.put(cid, nid);
                }
                buf.position(nxt);
            }

            // Pass 2: HEAP DUMP
            buf.clear();
            while (buf.get() != 0) {  } // skip null-terminated header text
            buf.getInt();
            buf.getLong();

            while (buf.hasRemaining()) {
                int tag = buf.get() & 0xFF;
                buf.getInt(); // timestamp, ignored
                int len = buf.getInt();
                int end = buf.position() + len;

                if (tag == 0x0C || tag == 0x1C) {
                    while (buf.position() < end) {
                        int sub = buf.get() & 0xFF;
                        switch (sub) {
                            case 0x01: case 0x06: case 0x08:
                                skip(buf, idSize);
                                break;
                            case 0x02:
                                skip(buf, idSize * 2);
                                break;
                            case 0x03: case 0x04: case 0x09:
                                skip(buf, idSize + 4 + 4);
                                break;
                            case 0x05: case 0x07:
                                skip(buf, idSize + 4);
                                break;
                            case 0x20:
                                parseClassDump(buf, idSize);
                                break;
                            case 0x21:
                                skip(buf, idSize); buf.getInt();
                                long cid = readId(buf, idSize);
                                int bytes = buf.getInt();
                                skip(buf, bytes);
                                r.counts.merge(cid, 1, Integer::sum);
                                r.sizes.merge(cid, (long) bytes + idSize + 4 + 4 + 4, Long::sum);
                                break;
                            case 0x22:
                                skip(buf, idSize); buf.getInt();
                                int num = buf.getInt();
                                long cid22 = readId(buf, idSize);
                                skip(buf, num * idSize);
                                r.counts.merge(cid22, 1, Integer::sum);
                                r.sizes.merge(cid22, (long) num * idSize + idSize + 4 + 4 + 4, Long::sum);
                                break;
                            case 0x23:
                                skip(buf, idSize); buf.getInt();
                                int num23 = buf.getInt();
                                int pType = buf.get() & 0xFF;
                                int es = elementSize(pType);
                                skip(buf, num23 * es);
                                long pseudo = 0xFFFFFFFF00000000L | pType;
                                r.counts.merge(pseudo, 1, Integer::sum);
                                r.sizes.merge(pseudo, (long) num23 * es + idSize + 4 + 4 + 4 + 1, Long::sum);
                                break;
                            default:
                                buf.position(end);
                                break;
                        }
                    }
                }
                buf.position(end);
            }
            return r;
        }
    }

    public static class Result {
        final int idSize;
        final Map<Long, String> utf8 = new HashMap<>();
        final Map<Long, Long> classIdToNameId = new HashMap<>();
        final Map<Long, Integer> counts = new HashMap<>();
        final Map<Long, Long> sizes = new HashMap<>();

        Result(int idSize) { this.idSize = idSize; }

        String getClassName(long id) {
            if ((id & 0xFFFFFFFF00000000L) == 0xFFFFFFFF00000000L) {
                switch ((int) (id & 0xFF)) {
                    case 4: return "[Z";
                    case 5: return "[C";
                    case 6: return "[F";
                    case 7: return "[D";
                    case 8: return "[B";
                    case 9: return "[S";
                    case 10: return "[I";
                    case 11: return "[J";
                    default: return "[?";
                }
            }
            Long nameId = classIdToNameId.get(id);
            if (nameId == null) return "unknown(0x" + Long.toHexString(id) + ")";
            String n = utf8.get(nameId);
            return n == null ? "null" : n;
        }
    }

    private static List<Diff> calcDiff(Result a, Result b) {
        List<Diff> list = new ArrayList<>();
        for (Long id : b.counts.keySet()) {
            int d = b.counts.getOrDefault(id, 0) - a.counts.getOrDefault(id, 0);
            long ds = b.sizes.getOrDefault(id, 0L) - a.sizes.getOrDefault(id, 0L);
            if (d != 0 || ds != 0) list.add(new Diff(b.getClassName(id), d, ds));
        }
        list.sort((aa, bb) -> Long.compare(Math.abs(bb.size), Math.abs(aa.size)));
        return list;
    }

    /* ===================== REPORT ===================== */
    private static void generateReport(Session s, ICommandSender sender, String path, String lastPath) {
        try {
            Result r1 = parse(path);
            Result r2 = parse(lastPath);
            List<Diff> diffs = calcDiff(r1, r2);
            String date = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            Path report = DUMP_DIR.resolve(s.side.toLowerCase() + "_report_" + date + ".txt");
            try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(report))) {
                pw.println("=== MEM Analysis Report [" + s.side + "] ===");
                pw.println("Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                if (ScriptController.getLevelKey() != null) {
                    pw.println("Session key: " + ScriptController.getLevelKey());
                }
                pw.println();
                pw.println("--- HPROF Diff (First -> Last) ---");
                pw.printf("%-4s %-100s %-12s %-12s%n", "#", "Class", "InstDiff", "SizeDiff");
                pw.println(repeatHyphen());
                int limit = Math.min(s.topCount, diffs.size());
                for (int i = 0; i < limit; i++) {
                    Diff d = diffs.get(i);
                    pw.printf("%-4d %-100s %-12d %-12d%n", i + 1, formatClassName(d.name), d.count, d.size);
                }
                pw.println();
                if (!path.isEmpty() && !lastPath.isEmpty()) {
                    Map<String, long[]> curr = takeSnapshot();
                    diffs = calcDiffMaps(s.bytes, curr);
                    pw.println("--- Live Snap Diff (Baseline -> Peak) ---");
                    pw.printf("%-4s %-100s %-12s %-12s%n", "#", "Class", "InstDiff", "SizeDiff");
                    pw.println(repeatHyphen());

                    limit = Math.min(s.topCount, diffs.size());
                    for (int j = 0; j < limit; j++) {
                        Diff d = diffs.get(j);
                        pw.printf("%-4d %-100s %-12d %-12d%n", j + 1, formatClassName(d.name), d.count, d.size);
                    }
                    pw.println();
                }
                pw.println("=== End of Report ===");
            }
            sendMessageIn(sender, "command.dump.saved", report.toString());
            if (s.side.equals("Client")) {
                NoppesStringUtils.setClipboardContents(report.getParent().toString());
                sendMessageIn(sender, "command.dump.copy.path", null);
            }
        } catch (Exception e) {
            LogWriter.error(e);
        }
    }

    /* ===================== HELPERS ===================== */
    private static String dumpSync(String suffix) {
        if (!dumping.compareAndSet(false, true)) return null;
        try {
            String path = DUMP_DIR.resolve(suffix + "_" + time() + ".hprof").toString();
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName on = new ObjectName("com.sun.management:type=HotSpotDiagnostic");
            mbs.invoke(on, "dumpHeap", new Object[]{path, true},
                    new String[]{String.class.getName(), boolean.class.getName()});
            return path;
        } catch (Exception e) {
            LogWriter.error(e);
            return null;
        } finally {
            dumping.set(false);
        }
    }

    public static void cleanupOldHprof() {
        if (!Files.exists(DUMP_DIR)) { return; }
        try (Stream<Path> sour = Files.list(DUMP_DIR)) {
            sour.filter(p -> p.toString().endsWith(".hprof"))
                    .forEach(p -> {
                        try { Files.deleteIfExists(p); } catch (Exception ignored) {}
                    });
        }
        catch (Exception ignored) {}
    }

    private static void deleteFile(String path) {
        if (path == null) { return; }
        CustomNPCsScheduler.runTack(() -> Util.instance.removeFile(new File(path)), 5000);
    }

    private static String repeatHyphen() {
        StringBuilder sb = new StringBuilder(140);
        for (int i = 0; i < 140; i++) sb.append('-');
        return sb.toString();
    }

    /** [B → byte[], [Ljava.util.HashMap$Node; → java.util.HashMap$Node[] */
    private static String formatClassName(String raw) {
        if (raw == null) return "null";
        if (raw.startsWith("[L") && raw.endsWith(";")) {
            return raw.substring(2, raw.length() - 1).replace('/', '.') + "[]";
        }
        if (raw.startsWith("[")) {
            String prefix = raw;
            int dims = 0;
            while (prefix.startsWith("[")) { dims++; prefix = prefix.substring(1); }
            String base;
            switch (prefix) {
                case "B": base = "byte"; break;
                case "C": base = "char"; break;
                case "D": base = "double"; break;
                case "F": base = "float"; break;
                case "I": base = "int"; break;
                case "J": base = "long"; break;
                case "S": base = "short"; break;
                case "Z": base = "boolean"; break;
                default:
                    if (prefix.startsWith("L") && prefix.endsWith(";"))
                        base = prefix.substring(1, prefix.length() - 1).replace('/', '.');
                    else base = prefix;
            }
            StringBuilder sb = new StringBuilder(base);
            for (int i = 0; i < dims; i++) sb.append("[]");
            return sb.toString();
        }
        return raw.replace('/', '.');
    }

    private static String time() { return String.valueOf(System.currentTimeMillis()); }
    private static String mb(long used, long total) { return (used / MB) + "/" + (total / MB) + " Mb"; }
    private static long readId(ByteBuffer b, int idSize) { return idSize == 8 ? b.getLong() : b.getInt() & 0xFFFFFFFFL; }
    private static void skip(ByteBuffer b, int n) { b.position(b.position() + n); }
    private static int elementSize(int t) {
        switch (t) {
            case 5: case 9: return 2;
            case 6: case 10: return 4;
            case 7: case 11: return 8;
            default: return 1;
        }
    }
    private static void parseClassDump(ByteBuffer b, int idSize) {
        skip(b, idSize); skip(b, 4); skip(b, idSize * 6); skip(b, 4);
        int cst = b.getShort() & 0xFFFF;
        for (int i = 0; i < cst; i++) { skip(b, 2); int t = b.get() & 0xFF; skip(b, valueSize(t, idSize)); }
        int st = b.getShort() & 0xFFFF;
        for (int i = 0; i < st; i++) { skip(b, idSize); int t = b.get() & 0xFF; skip(b, valueSize(t, idSize)); }
        int fld = b.getShort() & 0xFFFF;
        for (int i = 0; i < fld; i++) { skip(b, idSize); b.get(); }
    }
    private static int valueSize(int t, int idSize) {
        switch (t) {
            case 4: case 8: return 1;
            case 5: case 9: return 2;
            case 6: case 10: return 4;
            case 7: case 11: return 8;
            default: return idSize;
        }
    }

    private static class Diff {
        String name;
        long count;
        long size;
        Diff(String n, long c, long s) { name = n; count = c; size = s; }
    }

}
