package noppes.npcs.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.util.LogWriter;

public class GlobalDataController {

   public static GlobalDataController instance;
   private int itemGiverId = 0;

   public GlobalDataController() {
      instance = this;
      this.load();
   }

   private void load() {
      File saveDir = CustomNpcs.getLevelSaveDirectory();
      try {
         File file = new File(saveDir, "global.dat");
         if (file.exists()) {
            this.loadData(file);
         }
      } catch (Exception var5) {
         try {
            File file = new File(saveDir, "global.dat_old");
            if (file.exists()) {
               this.loadData(file);
            }
         } catch (Exception e) {
            LogWriter.error(e);
         }
      }

   }

   private void loadData(File file) throws Exception {
      CompoundTag compound = NbtIo.readCompressed(new FileInputStream(file));
      this.itemGiverId = compound.getInt("itemGiverId");
   }

   public void saveData() {
      try {
         File saveDir = CustomNpcs.getLevelSaveDirectory();
         CompoundTag compound = new CompoundTag();
         compound.putInt("itemGiverId", this.itemGiverId);
         File file = new File(saveDir, "global.dat_new");
         File file1 = new File(saveDir, "global.dat_old");
         File file2 = new File(saveDir, "global.dat");
         NbtIo.writeCompressed(compound, new FileOutputStream(file));
         if (file1.exists()) {
            file1.delete();
         }
         file2.renameTo(file1);
         if (file2.exists()) {
            file2.delete();
         }
         file.renameTo(file2);
         if (file.exists()) {
            file.delete();
         }
      } catch (Exception e) {
         LogWriter.error(e);
      }
   }

   public int incrementItemGiverId() {
      ++this.itemGiverId;
      this.saveData();
      return this.itemGiverId;
   }

}
