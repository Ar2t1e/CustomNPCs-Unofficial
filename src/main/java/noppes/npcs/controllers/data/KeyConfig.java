package noppes.npcs.controllers.data;

import java.util.List;
import java.util.Objects;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.INbt;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.handler.data.IKeySetting;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.controllers.KeyController;
import noppes.npcs.mixin.client.settings.IKeyBindingMixin;
import org.lwjgl.input.Keyboard;

public class KeyConfig implements IKeySetting {

	private Object parent;
	private int id;
	public String name = "key.custom.name";
	public String category = "key.custom.category";
	public int keyId = Keyboard.KEY_Z;
	public int modifer = 2; // 0-none, 1-Shift, 2-Ctrl, 3-Alt

	public KeyConfig(int idIn) {
		if (idIn < 0) { idIn *= -1; }
		id = idIn;
	}

	public void load(NBTTagCompound nbtKey) {
		name = nbtKey.getString("Name");
		category = nbtKey.getString("Category");
		id = nbtKey.getInteger("ID");
		keyId = nbtKey.getInteger("KeyID");
		if (keyId < 0) { keyId *= -1; }
		if (keyId < Keyboard.KEY_1 ||
				keyId == Keyboard.KEY_RCONTROL || keyId == Keyboard.KEY_LCONTROL ||
				keyId == Keyboard.KEY_RSHIFT || keyId == Keyboard.KEY_LSHIFT ||
				keyId == Keyboard.KEY_RMENU || keyId == Keyboard.KEY_LMENU) {
			keyId = Keyboard.KEY_Z;
		}
		modifer = nbtKey.getInteger("ModiferType") % 4;
		if (modifer < 0) { modifer *= -1; }
	}

	public NBTTagCompound save() {
		NBTTagCompound nbtKey = new NBTTagCompound();
		nbtKey.setString("Name", name);
		nbtKey.setString("Category", category);
		nbtKey.setInteger("KeyID", keyId);
		nbtKey.setInteger("ModiferType", modifer);
		nbtKey.setInteger("ID", id);
		return nbtKey;
	}

	@SideOnly(Side.CLIENT)
	public Object getMCKeyBinding() {
		if (parent == null) {
			try {
				Class<?> cls = Class.forName("net.minecraft.client.settings.KeyBinding");
				parent = cls.getConstructor(String.class, int.class, String.class).newInstance("", 0, "");
			}
			catch (Exception ignored) { }
		}
		if (parent != null) {
			String oldName = ((IKeyBindingMixin) parent).getKeyDescription();
			int oldKey = ((IKeyBindingMixin) parent).getKeyCode();
			if (oldKey != keyId) {
				ClientProxy.removeKeyFromMAP(parent);
				((IKeyBindingMixin) parent).setKeyCode(keyId);
				((IKeyBindingMixin) parent).setKeyCodeDefault(keyId);
			}
			((IKeyBindingMixin) parent).setKeyDescription(name);
			((IKeyBindingMixin) parent).setKeyCategory(category);
			if (!oldName.equals(name)) {
				((IKeyBindingMixin) parent).getAll().remove(oldName);
				ClientProxy.addKeyToAll(name, parent);
			}
			ClientProxy.tryAddKeyToMap(parent);
			((IKeyBindingMixin) parent).getCategories().add(category);
		}
		return parent;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof KeyConfig)) { return false; }
		if (obj == this) { return true; }
		KeyConfig key = (KeyConfig) obj;
		if (id != key.id || keyId != key.keyId || modifer != key.modifer) { return false; }
        return name.equals(key.name) && category.equals(key.category);
    }

	@Override
	public String getCategory() { return category; }

	@Override
	public int getId() { return id; }

	@Override
	public int getKeyId() { return keyId; }

	@Override
	public int getModiferType() { return modifer; }

	@Override
	public String getName() { return name; }

	@Override
	public INbt getNbt() { return Objects.requireNonNull(NpcAPI.Instance()).getINbt(save()); }

	public boolean isActive(int key, List<Integer> keyPress) {
		if (keyId != key) { return false; }
		// 0-none, 1-Shift, 2-Ctrl, 3-Alt
		switch (modifer) {
			case 1: return keyPress.contains(Keyboard.KEY_LSHIFT) || keyPress.contains(Keyboard.KEY_RSHIFT);
			case 2: return keyPress.contains(Keyboard.KEY_LCONTROL) || keyPress.contains(Keyboard.KEY_RCONTROL);
			case 3: return keyPress.contains(Keyboard.KEY_LMENU) || keyPress.contains(Keyboard.KEY_RMENU);
			default: return true;
		}
	}

	@Override
	public void setCategory(String name) {
		if (name == null || name.isEmpty()) { name = "key.custom.category"; }
		category = name;
		KeyController.getInstance().update(id);
	}

	@Override
	public void setKeyId(int keyIdIn) {
		if (keyIdIn < Keyboard.KEY_1) {
			throw new CustomNPCsException("Key ID:" + keyIdIn + " must be greater than " + Keyboard.KEY_1);
		}
		if (keyIdIn == Keyboard.KEY_RCONTROL || keyIdIn == Keyboard.KEY_LCONTROL ||
				keyIdIn == Keyboard.KEY_RSHIFT || keyIdIn == Keyboard.KEY_LSHIFT ||
				keyIdIn == Keyboard.KEY_RMENU || keyIdIn == Keyboard.KEY_LMENU) {
			throw new CustomNPCsException("Key ID:" + keyIdIn + " cannot be of type Ctrl, Alt or Shift");
		}
		keyId = keyIdIn;
		KeyController.getInstance().update(id);
	}

	@Override
	public void setModiferType(int type) {
		if (type < 0 || type > 3) {
			throw new CustomNPCsException("Modifer Type must be between 0 and 3");
		}
		modifer = type;
	}

	@Override
	public void setName(String nameIn) {
		if (nameIn == null || nameIn.isEmpty()) { nameIn = "key.custom.name"; }
		name = nameIn;
		KeyController.getInstance().update(id);
	}

	@Override
	public void setNbt(INbt nbt) { load(nbt.getMCNBT()); }

	@Override
	public String toString() {
		return "KeyConfig { ID: " + id + "; keyID: " + keyId + "; modiferType: " + modifer + ", name: \""
				+ name + "\"; category: \"" + category + "\"}";
	}

}
