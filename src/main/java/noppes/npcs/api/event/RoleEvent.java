package noppes.npcs.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import noppes.npcs.api.entity.data.role.ITransportLocation;
import noppes.npcs.api.interfaces.EventName;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.entity.data.IPlayerMail;
import noppes.npcs.api.entity.data.role.IRoleTransporter;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.constants.EnumScriptType;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class RoleEvent extends CustomNPCsEvent {

   public final ICustomNpc<?> npc;
   public final IPlayer<?> player;

   public RoleEvent(Player player, ICustomNpc<?> npc) {
      this.npc = npc;
      this.player = (IPlayer<?>) Objects.requireNonNull(NpcAPI.Instance()).getIEntity(player);
   }

   @EventName(EnumScriptType.ROLE)
   public static class BankUpgradedEvent extends RoleEvent {
      public final int slot;

      public BankUpgradedEvent(Player player, ICustomNpc<?> npc, int slot) {
         super(player, npc);
         this.slot = slot;
      }
   }

   @EventName(EnumScriptType.ROLE)
   public static class BankUnlockedEvent extends RoleEvent {
      public final int slot;

      public BankUnlockedEvent(Player player, ICustomNpc<?> npc, int slot) {
         super(player, npc);
         this.slot = slot;
      }
   }

   // Changed from Unofficial (BetaZavr)
   @EventName(EnumScriptType.ROLE)
   public static class TradeFailedEvent extends RoleEvent {

      public Map<IItemStack, Integer> currency;
      public IItemStack sold;

      public TradeFailedEvent(Player player, ICustomNpc<?> npc, ItemStack soldIn, Map<ItemStack, Integer> items) {
         super(player, npc);
         currency = new LinkedHashMap<>();
         for (ItemStack stack : items.keySet()) {
            if (stack == null || stack.isEmpty()) { continue; }
            currency.put(Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(stack), items.get(stack));
         }
         sold = Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(soldIn.copy());
      }
   }

   // Changed from Unofficial (BetaZavr)
   @Cancelable
   @EventName(EnumScriptType.ROLE)
   public static class TraderEvent extends RoleEvent {

      public Map<IItemStack, Integer> currency;
      public IItemStack sold;

      public TraderEvent(Player player, ICustomNpc<?> npc, ItemStack soldIn, Map<ItemStack, Integer> items) {
         super(player, npc);
         currency = new LinkedHashMap<>();
         for (ItemStack stack : items.keySet()) {
            if (stack == null || stack.isEmpty()) { continue; }
            currency.put(Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(stack), items.get(stack));
         }
         sold = Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(soldIn);
      }
   }

   @EventName(EnumScriptType.ROLE)
   public static class FollowerFinishedEvent extends RoleEvent {
      public FollowerFinishedEvent(Player player, ICustomNpc<?> npc) {
         super(player, npc);
      }
   }

   @Cancelable
   @EventName(EnumScriptType.ROLE)
   public static class FollowerHireEvent extends RoleEvent {
      public int days;

      public FollowerHireEvent(Player player, ICustomNpc<?> npc, int days) {
         super(player, npc);
         this.days = days;
      }
   }

   @Cancelable
   @EventName(EnumScriptType.ROLE)
   public static class MailmanEvent extends RoleEvent {
      public final IPlayerMail mail;

      public MailmanEvent(Player player, ICustomNpc<?> npc, IPlayerMail mail) {
         super(player, npc);
         this.mail = mail;
      }
   }

   @Cancelable
   @EventName(EnumScriptType.ROLE)
   public static class TransporterUnlockedEvent extends RoleEvent {
      public TransporterUnlockedEvent(Player player, ICustomNpc<?> npc) {
         super(player, npc);
      }
   }

   @Cancelable
   @EventName(EnumScriptType.ROLE)
   public static class TransporterUseEvent extends RoleEvent {
      public final ITransportLocation location;

      public TransporterUseEvent(Player player, ICustomNpc<?> npc, ITransportLocation location) {
         super(player, npc);
         this.location = location;
      }
   }

}
