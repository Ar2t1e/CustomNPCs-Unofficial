package noppes.npcs.controllers.data;

import java.util.*;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class Lines {

   private static final Random random = new Random();
   private int lastLine = -1;
   public final TreeMap<Integer, Line> lines = new TreeMap<>();

   public CompoundTag save() {
      CompoundTag compound = new CompoundTag();
      ListTag tagList = new ListTag();
      for (int slot : lines.keySet()) {
         Line line = lines.get(slot);
         CompoundTag nbtLine = new CompoundTag();
         nbtLine.putInt("Slot", slot);
         nbtLine.putString("Line", line.getText());
         nbtLine.putString("Song", line.getSound());
         tagList.add(nbtLine);
      }
      compound.put("Lines", tagList);
      return compound;
   }

   public void load(CompoundTag compound) {
      ListTag tagList = compound.getList("Lines", 10);
      lines.clear();
      for(int i = 0; i < tagList.size(); ++i) {
         CompoundTag nbtLine = tagList.getCompound(i);
         Line line = new Line();
         line.setText(nbtLine.getString("Line"));
         line.setSound(nbtLine.getString("Song"));
         lines.put(nbtLine.getInt("Slot"), line);
      }
   }

   public Line getLine(boolean isRandom) {
      if (lines.isEmpty()) { return null; }
      // New from Unofficial (BetaZavr)
      if (isRandom) {
         int i = lastLine;
         if (lines.size() == 1) { i = 0; }
         else {
            while (i == lastLine) { i = Lines.random.nextInt(lines.size()); }
         }
         if (lines.containsKey(i)) {
            lastLine = i;
            return lines.get(i).copy();
         }
         for (Map.Entry<Integer, Line> e : lines.entrySet()) {
            if (--i < 0) {
               lastLine = e.getKey();
               return e.getValue().copy();
            }
         }
      }
      ++lastLine;
      Line line;
      while (true) {
         lastLine %= lines.size();
         line = lines.get(lastLine);
         if (line != null) { break; }
         ++lastLine;
      }
      return line.copy();
   }

   public boolean isEmpty() { return lines.isEmpty(); }

   // New from Unofficial (BetaZavr)
   public Lines copy() {
      Lines newLines = new Lines();
      for (int i : lines.keySet()) { newLines.lines.put(i, lines.get(i)); }
      return newLines;
   }

   public void remove(int pos) {
      if (!lines.containsKey(pos)) { return; }
      lines.remove(pos);
      correctLines();
   }

   public void correctLines() {
      Map<Integer, Line> newLines = new TreeMap<>();
      int i = 0;
      boolean isChanged = false;
      for (int pos : lines.keySet()) {
         if (pos != i) { isChanged = true; }
         Line line = lines.get(pos);
         if (line.getText().isEmpty()) {
            isChanged = true;
            continue;
         }
         newLines.put(i, line);
         i++;
      }
      if (isChanged) {
         lines.clear();
         lines.putAll(newLines);
      }
   }

}
