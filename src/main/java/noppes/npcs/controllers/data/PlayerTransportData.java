package noppes.npcs.controllers.data;

import java.util.HashSet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import noppes.npcs.api.handler.data.IPlayerData;

public class PlayerTransportData implements IPlayerData {

   public final HashSet<Integer> transports = new HashSet<>();

   @Override
   public void load(CompoundTag compound) {
      transports.clear();
      if (compound != null) {
         ListTag list = compound.getList("TransportData", 10);
         for (int i = 0; i < list.size(); ++i) {
            CompoundTag nbt = list.getCompound(i);
            transports.add(nbt.getInt("Transport"));
         }
      }
   }

   @Override
   public CompoundTag save(CompoundTag compound) {
      ListTag list = new ListTag();
      for (int dia : this.transports) {
         CompoundTag nbt = new CompoundTag();
         nbt.putInt("Transport", dia);
         list.add(nbt);
      }
      compound.put("TransportData", list);
      return compound;
   }

   public void clear() { transports.clear(); }

}
