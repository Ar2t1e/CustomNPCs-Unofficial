package noppes.npcs.controllers.data;

import java.util.HashMap;
import java.util.Vector;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class TransportCategory {

   public int id = -1;
   public String title = "";
   public final HashMap<Integer, TransportLocation> locations = new HashMap<>();

   public Vector<TransportLocation> getDefaultLocations() {
      Vector<TransportLocation> list = new Vector<>();
      for (TransportLocation loc : this.locations.values()) {
         if (loc.isDefault()) {
            list.add(loc);
         }
      }
      return list;
   }

   public void load(CompoundTag compound) {
      this.id = compound.getInt("CategoryId");
      this.title = compound.getString("CategoryTitle");
      ListTag locations = compound.getList("CategoryLocations", 10);
      if (!locations.isEmpty()) {
         for(int ii = 0; ii < locations.size(); ++ii) {
            TransportLocation location = new TransportLocation();
            location.load(locations.getCompound(ii));
            location.category = this;
            this.locations.put(location.id, location);
         }
      }
   }

   public void save(CompoundTag compound) {
      compound.putInt("CategoryId", this.id);
      compound.putString("CategoryTitle", this.title);
      ListTag locations = new ListTag();
      for (TransportLocation location : this.locations.values()) {
         locations.add(location.save());
      }
      compound.put("CategoryLocations", locations);
   }

}
