package noppes.npcs.roles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.AuctionController;
import noppes.npcs.controllers.data.AuctionHouse;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.server.SPacketGuiOpen;

public class RoleAuctioneer extends RoleInterface {

	private int auctionId = -1;

	public RoleAuctioneer(EntityNPCInterface npc) {
		super(npc);
		type = RoleType.AUCTIONEER;
	}

	public int getAuctionId() { return auctionId; }

	public void setAuctionId(int id) { auctionId = id; }

	@Override
	public void interact(EntityPlayer player) {
		npc.say(player, npc.advanced.getInteractLine());
		AuctionHouse auction = AuctionController.getInstance().getAuction(auctionId);
		if (auction != null && player instanceof EntityPlayerMP) {
			auction.addPlayer((EntityPlayerMP) player);
			SPacketGuiOpen.sendOpenGui((EntityPlayerMP) player, EnumGuiType.PlayerAuction,
					null, new BlockPos(auctionId, 0, 0));
		}
	}

	@Override
	public void load(NBTTagCompound compound) {
		super.load(compound);
		type = RoleType.AUCTIONEER;
		if (compound.hasKey("AuctionId", 3)) {
			auctionId = compound.getInteger("AuctionId");
		}
	}

	@Override
	public NBTTagCompound save(NBTTagCompound compound) {
		super.save(compound);
		compound.setInteger("AuctionId", auctionId);
		return compound;
	}

}
