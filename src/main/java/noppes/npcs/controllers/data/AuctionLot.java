package noppes.npcs.controllers.data;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.handler.data.IAuctionLot;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.containers.inventories.NpcMiscInventory;
import noppes.npcs.util.ValueUtil;

public class AuctionLot implements IAuctionLot {

	protected boolean currencyType = true; // true = money, false = donat
	protected long creationDate;
	protected long duration = 10L * 60L * 1000L;
	protected int startingPrice = 0;
	protected int currentBidPrice = 0;
	protected int buyoutPrice = 0;
	protected boolean canBuyout = true;
	protected String ownerName = "Admin";
	protected String description = "";
	protected String currentBidderName = "";
	public final NpcMiscInventory inventory = new NpcMiscInventory(1);

	public AuctionLot(long timeIdIn) { creationDate = timeIdIn; }

	@Override
	public long getCreationDate() { return creationDate; }

	public void setCreationDate(long creationDateIn) {
		if (creationDateIn == 0L) { creationDateIn = System.currentTimeMillis(); }
		creationDate = creationDateIn;
	}

	@Override
	public long getDuration() { return duration; }

	@Override
	public void setDuration(long delayIn) {
		duration = ValueUtil.correctLong(delayIn, 10L * 60L * 1000L, 3L * 24L * 60L * 60L * 1000L);
	}

	@Override
	public String getOwnerName() { return ownerName; }

	@Override
	public void setOwnerName(String name) {
		if (name == null || name.isEmpty()) { name = "Admin"; }
		ownerName = name;
	}

	@Override
	public IItemStack getItem() {
		NpcAPI api = NpcAPI.Instance();
		return api == null ? ItemStackWrapper.AIR : api.getIItemStack(inventory.getStackInSlot(0));
	}

	public ItemStack getStack() { return inventory.getStackInSlot(0);}

	@Override
	public void setItem(IItemStack iStack) { setStack(iStack == null ? null : iStack.getMCItemStack()); }

	public void setStack(ItemStack stack) { inventory.setInventorySlotContents(0, stack == null ? ItemStack.EMPTY : stack); }

	@Override
	public int getStartingPrice() { return startingPrice; }

	@Override
	public void setStartingPrice(int price) { startingPrice = ValueUtil.correctInt(price, 1, Integer.MAX_VALUE); }

	@Override
	public int getBuyoutPrice() { return buyoutPrice; }

	@Override
	public void setBuyoutPrice(int price) { buyoutPrice = ValueUtil.correctInt(price, 1, Integer.MAX_VALUE); }

	/**
	 * @return true - is money; false - is donat
	 */
	@Override
	public boolean getCurrencyType() { return currencyType; }

	@Override
	public void setCurrencyType(boolean currencyTypeIn) { currencyType = currencyTypeIn; }

	@Override
	public boolean canBuyout() { return canBuyout; }

	@Override
	public void setCanBuyout(boolean can) { canBuyout = can; }

	@Override
	public String getDescription() { return description; }

	@Override
	public void setDescription(String description) {
		if (description != null && description.length() > 1000) { description = description.substring(0, 1000); }
		this.description = description != null ? description : "";
	}

	@Override
	public String getCurrentBidderName() { return currentBidderName; }

	@Override
	public void setCurrentBidderName(String name) { currentBidderName = name != null ? name : ""; }

	@Override
	public int getCurrentBidPrice() { return currentBidPrice; }

	@Override
	public void setCurrentBidPrice(int price) { currentBidPrice = ValueUtil.correctInt(price, 1, Integer.MAX_VALUE); }

	public boolean isItemValid() { return !inventory.getStackInSlot(0).isEmpty(); }

	public void load(NBTTagCompound compound) {
		setCreationDate(compound.getLong("CreationDate"));
		setOwnerName(compound.getString("OwnerName"));
		setDuration(compound.getLong("Duration"));
		inventory.load(compound.getCompoundTag("Item"));
		startingPrice = compound.getInteger("StartingPrice");
		buyoutPrice = compound.getInteger("BuyoutPrice");
		currencyType = compound.getBoolean("CurrencyType");
		canBuyout = compound.getBoolean("CanBuyout");
		description = compound.hasKey("Description", 8) ? compound.getString("Description") : "";
		currentBidderName = compound.hasKey("CurrentBidderName", 8) ? compound.getString("CurrentBidderName") : "";
		currentBidPrice = compound.getInteger("CurrentBidPrice");
	}

	public NBTTagCompound save() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setLong("CreationDate", creationDate);
		compound.setLong("Duration", duration);
		compound.setString("OwnerName", ownerName);
		compound.setTag("Item", inventory.save());
		compound.setInteger("StartingPrice", startingPrice);
		compound.setInteger("BuyoutPrice", buyoutPrice);
		compound.setBoolean("CurrencyType", currencyType);
		compound.setBoolean("CanBuyout", canBuyout);
		compound.setString("Description", description);
		compound.setString("CurrentBidderName", currentBidderName);
		compound.setInteger("CurrentBidPrice", currentBidPrice);
		return compound;
	}

}
