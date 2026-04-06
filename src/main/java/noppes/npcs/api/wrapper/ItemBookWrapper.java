package noppes.npcs.api.wrapper;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.api.item.IItemBook;

public class ItemBookWrapper extends ItemStackWrapper implements IItemBook {

   protected ItemBookWrapper(ItemStack item) {
      super(item);
   }

   public String getTitle() {
      return this.getTag().getString("title");
   }

   public void setTitle(String title) {
      this.getTag().putString("title", title);
   }

   public String getAuthor() {
      return this.getTag().getString("author");
   }

   public void setAuthor(String author) {
      this.getTag().putString("author", author);
   }

   public String[] getText() {
      List<String> list = new ArrayList<>();
      ListTag pages = this.getTag().getList("pages", 8);

      for(int i = 0; i < pages.size(); ++i) {
         list.add(pages.getString(i));
      }

      return list.toArray(new String[0]);
   }

   public void setText(String... pages) {
      ListTag list = new ListTag();
      if (pages != null) {
         for (String page : pages) {
            list.add(StringTag.valueOf(page));
         }
      }

      this.getTag().put("pages", list);
   }

   private CompoundTag getTag() {
      CompoundTag comp = this.item.getTag();
      if (comp == null) {
         this.item.setTag(comp = new CompoundTag());
      }

      return comp;
   }

   public boolean isBook() {
      return true;
   }

   public int getType() {
      return 1;
   }

}
