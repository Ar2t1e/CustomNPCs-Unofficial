package noppes.npcs.api.entity.data.role;

import net.minecraft.util.text.ITextComponent;
import noppes.npcs.api.INbt;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.interfaces.ParamName;

import javax.annotation.Nullable;
import java.util.List;

public interface IJobSpawner {

	void clear();

	List<IEntity<?>> spawnEntity(@ParamName("slotId") int slotId, @ParamName("isDead") boolean isDead);

	IJobSpawnerSettings get(@ParamName("isDead") boolean isDead);

	interface IJobSpawnerSettings {

		void clear();

		@Nullable
		IJobSpawnerData add(boolean isClone);

		@Nullable IJobSpawnerData get(@ParamName("slotId") int slotId);

		boolean up(@ParamName("slotId") int slotId);

		boolean down(@ParamName("slotId") int slotId);

		void setNbt(@ParamName("nbt") INbt nbt);

		INbt getNbt();

	}

	interface IJobSpawnerData {

		ITextComponent getTitle();

		int getCount();

		void setNbt(@ParamName("nbt") INbt nbt);

		void setCount(int countIn);

		INbt getNbt();

		IEntity<?> getEntity();

		boolean isValid();
	}

}
