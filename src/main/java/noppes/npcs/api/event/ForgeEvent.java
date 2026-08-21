package noppes.npcs.api.event;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.IWorld;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.interfaces.EventName;
import noppes.npcs.api.IPos;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.wrapper.BlockWrapper;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.data.Zone3D;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Cancelable
public class ForgeEvent extends CustomNPCsEvent {

	public Event event;

	// New from Unofficial (BetaZavr)
	public final @Nullable IWorld world;
	public final @Nullable IPos pos;
	public final @Nullable IBlock block;
	public final @Nullable IPlayer<?> player;
	public final @Nullable IEntity<?> entity;

	public ForgeEvent(Event eventIn) {
		super();
		event = eventIn;
		IWorld iWorld = null;
		IPos iPos = null;
		IBlock iBlock = null;
		IEntity<?> iEntity = null;
		IPlayer<?> iPlayer = null;
		if (eventIn instanceof net.minecraftforge.event.entity.EntityEvent) {
			net.minecraftforge.event.entity.EntityEvent event = (net.minecraftforge.event.entity.EntityEvent) eventIn;
			iEntity = event.getEntity() != null ? API.getIEntity(event.getEntity()) : null;
		}
		if (eventIn instanceof net.minecraftforge.event.world.WorldEvent) {
			net.minecraftforge.event.world.WorldEvent event = (net.minecraftforge.event.world.WorldEvent) eventIn;
			iWorld = event.getWorld() != null ? API.getIWorld(event.getWorld()) : null;
		}
		if (!CustomNpcs.SimplifiedForgeEvents && eventIn != null) {
			List<Field> fields = new ArrayList<>(Arrays.asList(eventIn.getClass().getDeclaredFields()));
			for (Field field : eventIn.getClass().getFields()) {
				if (!fields.contains(field)) { fields.add(field); }
			}
			List<Method> methods = new ArrayList<>(Arrays.asList(eventIn.getClass().getDeclaredMethods()));
			for (Method method : eventIn.getClass().getMethods()) {
				if (!methods.contains(method)) { methods.add(method); }
			}
			if (iEntity == null) {
				for (Field field : fields) {
					try {
						if (Entity.class.isAssignableFrom(field.getType())) {
							Object obj = field.get(eventIn);
							if (obj instanceof Entity) {
								iEntity = API.getIEntity((Entity) obj);
								break;
							}
						}
					}
					catch (Exception ignored) { }
				}
			}
			if (iEntity == null) {
				for (Method method : methods) {
					if (method.getParameterCount() == 0 && Entity.class.isAssignableFrom(method.getReturnType())) {
						try {
							Object obj = method.invoke(eventIn);
							if (obj instanceof Entity) {
								iEntity = API.getIEntity((Entity) obj);
								break;
							}
						}
						catch (Exception ignored) { }
					}
				}
			}
			if (iEntity instanceof IPlayer<?>) { iPlayer = (IPlayer<?>) iEntity; }
			if (iPlayer == null) {
				for (Field field : fields) {
					try {
						if (EntityPlayer.class.isAssignableFrom(field.getType())) {
							Object obj = field.get(eventIn);
							if (obj instanceof EntityPlayer) {
								iPlayer = (IPlayer<?>) API.getIEntity((EntityPlayer) obj);
								break;
							}
						}
					}
					catch (Exception ignored) { }
				}
			}
			if (iPlayer == null) {
				for (Method method : methods) {
					if (method.getParameterCount() == 0 && EntityPlayer.class.isAssignableFrom(method.getReturnType())) {
						try {
							Object obj = method.invoke(eventIn);
							if (obj instanceof EntityPlayer) {
								iPlayer = (IPlayer<?>) API.getIEntity((EntityPlayer) obj);
								break;
							}
						}
						catch (Exception ignored) { }
					}
				}
			}
			if (iWorld == null) {
				for (Field field : fields) {
					try {
						if (World.class.isAssignableFrom(field.getType())) {
							Object obj = field.get(eventIn);
							if (obj instanceof World) {
								iWorld = API.getIWorld((World) obj);
								break;
							}
						}
					}
					catch (Exception ignored) { }
				}
			}
			if (iWorld == null) {
				for (Method method : methods) {
					if (method.getParameterCount() == 0 && World.class.isAssignableFrom(method.getReturnType())) {
						try {
							Object obj = method.invoke(eventIn);
							if (obj instanceof World) {
								iWorld = API.getIWorld((World) obj);
								break;
							}
						}
						catch (Exception ignored) { }
					}
				}
			}
			if (iWorld == null) {
				if (iEntity != null) { iWorld = iEntity.getWorld(); }
				else if (iPlayer != null) { iWorld = iPlayer.getWorld(); }
			}
			if (iWorld != null) {
				for (Field field : fields) {
					try {
						if (BlockPos.class.isAssignableFrom(field.getType())) {
							Object obj = field.get(eventIn);
							if (obj instanceof BlockPos) {
								iPos = API.getIPos((BlockPos) obj);
								break;
							}
						}
					}
					catch (Exception ignored) { }
				}
				if (iPos == null) {
					for (Method method : methods) {
						if (method.getParameterCount() == 0 && BlockPos.class.isAssignableFrom(method.getReturnType())) {
							try {
								Object obj = method.invoke(eventIn);
								if (obj instanceof BlockPos) {
									iPos = API.getIPos((BlockPos) obj);
									break;
								}
							}
							catch (Exception ignored) { }
						}
					}
				}
				if (iPos == null) {
					if (iEntity != null) { iPos = iEntity.getPos(); }
					else if (iPlayer != null) { iPos = iPlayer.getPos(); }
				}
				World world = iWorld.getMCWorld();
				for (Field field : fields) {
					try {
						if (IBlockState.class.isAssignableFrom(field.getType())) {
							Object obj = field.get(eventIn);
							if (obj instanceof IBlockState) {
								iBlock = BlockWrapper.createNew(world, iPos == null ? BlockPos.ORIGIN : iPos.getMCBlockPos(), (IBlockState) obj);
								break;
							}
						}
					}
					catch (Exception ignored) { }
				}
				if (iBlock == null) {
					for (Field field : fields) {
						try {
							if (Block.class.isAssignableFrom(field.getType())) {
								Object obj = field.get(eventIn);
								if (obj instanceof Block) {
									iBlock = BlockWrapper.createNew(world, iPos == null ? BlockPos.ORIGIN : iPos.getMCBlockPos(), ((Block) obj).getDefaultState());
									break;
								}
							}
						}
						catch (Exception ignored) { }
					}

				}
				if (iBlock == null) {
					for (Method method : methods) {
						if (method.getParameterCount() == 0 && IBlockState.class.isAssignableFrom(method.getReturnType())) {
							try {
								Object obj = method.invoke(eventIn);
								if (obj instanceof IBlockState) {
									iBlock = BlockWrapper.createNew(world, iPos == null ? BlockPos.ORIGIN : iPos.getMCBlockPos(), (IBlockState) obj);
									break;
								}
							}
							catch (Exception ignored) { }
						}
					}
				}
				if (iBlock == null) {
					for (Method method : methods) {
						if (method.getParameterCount() == 0 && Block.class.isAssignableFrom(method.getReturnType())) {
							try {
								Object obj = method.invoke(eventIn);
								if (obj instanceof Block) {
									iBlock = BlockWrapper.createNew(world, iPos == null ? BlockPos.ORIGIN : iPos.getMCBlockPos(), ((Block) obj).getDefaultState());
									break;
								}
							}
							catch (Exception ignored) { }
						}
					}
				}
			}
		}
		world = iWorld;
		pos = iPos;
		block = iBlock;
		player = iPlayer;
		entity = iEntity;
	}

	@EventName(EnumScriptType.INIT)
	public static class InitEvent extends ForgeEvent {
		public InitEvent() {super(null); }
	}

	// New from Unofficial (BetaZavr)
	@Cancelable
	@EventName(EnumScriptType.REGION_ENTER)
	public static class EnterToRegion extends CustomNPCsEvent {
		public final Entity entity;
		public final Zone3D region;
		public EnterToRegion(Entity entityIn, Zone3D zone) {
			super();
			entity = entityIn;
			region = zone;
		}
	}

	@EventName(EnumScriptType.REGION_LEAVE)
	public static class LeaveRegion extends CustomNPCsEvent {
		public final Entity entity;
		public final Zone3D region;
		public LeaveRegion(Entity entityIn, Zone3D zone) {
			super();
			entity = entityIn;
			region = zone;
		}
	}

	@Cancelable
	@EventName(EnumScriptType.PLAY_SOUND)
	public static class ClientSoundPlayEvent extends ClientSoundEvent {
		public ClientSoundPlayEvent(Event eventIn, IPlayer<?> playerIn, String nameIn, String resourceIn, IPos posIn, float volumeIn, float pitchIn, double milliSecondsIn, double totalSecondIn) {
			super(eventIn, playerIn, nameIn, resourceIn, posIn, volumeIn, pitchIn, milliSecondsIn, totalSecondIn);
		}
	}

	@Cancelable
	@EventName(EnumScriptType.SOUND_TICK_EVENT)
	public static class ClientSoundTickEvent extends ClientSoundEvent {
		public ClientSoundTickEvent(Event eventIn, IPlayer<?> playerIn, String nameIn, String resourceIn, IPos posIn, float volumeIn, float pitchIn, double milliSecondsIn, double totalSecondIn) {
			super(eventIn, playerIn, nameIn, resourceIn, posIn, volumeIn, pitchIn, milliSecondsIn, totalSecondIn);
		}
	}

	@EventName(EnumScriptType.STOP_SOUND)
	public static class ClientSoundStopEvent extends ClientSoundEvent {
		public ClientSoundStopEvent(Event eventIn, IPlayer<?> playerIn, String nameIn, String resourceIn, IPos posIn, float volumeIn, float pitchIn, double milliSecondsIn, double totalSecondIn) {
			super(eventIn, playerIn, nameIn, resourceIn, posIn, volumeIn, pitchIn, milliSecondsIn, totalSecondIn);
		}
	}

	private static class ClientSoundEvent extends ForgeEvent {

		public double currentTime;
		public double duration;
		public float volume;
		public float pitch;
		public String name;
		public String resource;
		public IPlayer<?> player;
		public IPos pos;

		public ClientSoundEvent(Event eventIn, IPlayer<?> playerIn, String nameIn, String resourceIn, IPos posIn,
								float volumeIn, float pitchIn, double currentTimeIn, double durationIn) {
			super(eventIn);
			currentTime = currentTimeIn;
			duration = durationIn;
			name = nameIn;
			resource = resourceIn;
			volume = volumeIn;
			pitch = pitchIn;
			pos = posIn;
			player = playerIn;
		}

	}

}
