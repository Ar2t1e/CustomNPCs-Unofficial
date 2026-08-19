package noppes.npcs.api.event;

import net.minecraftforge.fml.common.gameevent.TickEvent;
import noppes.npcs.api.handler.data.IWorldProvider;
import noppes.npcs.api.interfaces.EventFunction;
import noppes.npcs.api.interfaces.EventName;
import noppes.npcs.api.IPos;
import noppes.npcs.api.IWorld;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.dimensions.CustomWorldProvider;

public class WorldEvent extends CustomNPCsEvent {

	@EventName(EnumScriptType.SCRIPT_COMMAND)
	public static class ScriptCommandEvent extends WorldEvent {
		public String[] arguments;
		public IPos pos;

		public ScriptCommandEvent(IWorld world, IPos pos, String[] arguments) {
			super(world);
			this.arguments = arguments;
			this.pos = pos;
		}
	}

	@EventName(EnumScriptType.SCRIPT_TRIGGER)
	public static class ScriptTriggerEvent extends WorldEvent {

		public Object[] arguments;
		public IPos pos;
		public IEntity<?> entity;
		public int id;

		public ScriptTriggerEvent(int id, IWorld world, IPos pos, IEntity<?> entity, Object... arguments) {
			super(world);
			this.id = id;
			this.arguments = arguments;
			this.pos = pos;
			this.entity = entity;
		}

	}

	@EventFunction("worldtick")
	public static class ServerTickEvent extends WorldEvent {

		public TickEvent.ServerTickEvent event;

		public ServerTickEvent(TickEvent.ServerTickEvent event) {
			super(null);
			this.event = event;
		}

	}

	@EventFunction("any_provider_methods")
	public static class ProviderEvent extends WorldEvent {

		public IWorldProvider provider;
		public Object result;
		public final Object[] parameters;
		public final int dimensionId;

		public ProviderEvent(CustomWorldProvider providerIn, Object resultIn, Object ... parametersIn) {
			super(null);
			provider = providerIn;
			dimensionId = providerIn.getMCDimensionType().getId();
			result = resultIn;
			parameters = parametersIn;
		}

	}

	public IWorld world;

	public WorldEvent(IWorld worldIn) { world = worldIn; }

}
