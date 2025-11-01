package ganymedes01.etfuturum.entities;

import cpw.mods.fml.common.registry.EntityRegistry;
import ganymedes01.etfuturum.Tags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.*;

public class ModEntityList {

	private static EntityData[] array = new EntityData[0];
	private static final Map<Integer, Class<? extends Entity>> map = new HashMap<Integer, Class<? extends Entity>>();
	public static List<Integer> eggIDs = new ArrayList<Integer>();
	public static Map<Class<? extends Entity>, Integer> eggIds = new HashMap<Class<? extends Entity>, Integer>();

	public static void registerEntity(Class<? extends Entity> entityClass, String entityName, int id, Object mod, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates) {
		registerEntity(entityClass, entityName, id, mod, trackingRange, updateFrequency, sendsVelocityUpdates, -1, -1, false);
	}

	public static void registerEntity(Class<? extends Entity> entityClass, String entityName, int id, Object mod, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates, int eggColour1, int eggColour2) {
		registerEntity(entityClass, entityName, id, mod, trackingRange, updateFrequency, sendsVelocityUpdates, eggColour1, eggColour2, true);
	}

	private static void registerEntity(Class<? extends Entity> entityClass, String entityName, int id, Object mod, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates, int eggColour1, int eggColour2, boolean hasEgg) {
		EntityRegistry.registerModEntity(entityClass, entityName, id, mod, trackingRange, updateFrequency, sendsVelocityUpdates);

		if (id >= array.length) {
			EntityData[] newArray = new EntityData[id + 5];
			System.arraycopy(array, 0, newArray, 0, array.length);
			array = newArray;
		}
		if (array[id] != null)
			throw new IllegalArgumentException("ID " + id + " is already being used! Please report this error!");
		array[id] = new EntityData(entityName, id, eggColour1, eggColour2, hasEgg);
		map.put(id, entityClass);
		if (eggColour1 != -1)
			registerEntityEgg(entityClass, eggColour1, eggColour2);
	}

	public static String getName(int id) {
		EntityData data = getData(id);
		if (data == null)
			return null;
		return Tags.MOD_ID + "." + data.name;
	}

	public static EntityData getData(int id) {
		if (id >= array.length)
			return null;
		return array[id];
	}

	public static boolean hasEntitiesWithEggs() {
		for (EntityData data : array)
			if (data != null && data.hasEgg)
				return true;
		return false;
	}

	public static Entity createEntityByID(int id, World world) {
		EntityData data = getData(id);
		if (data == null || !data.hasEgg)
			return null;

		try {
			Class<? extends Entity> cls = map.get(id);
			if (cls != null)
				return cls.getConstructor(World.class).newInstance(world);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static EntityData[] getDatasWithEggs() {
		List<EntityData> list = new LinkedList<EntityData>();
		for (Integer id : map.keySet()) {
			EntityData data = getData(id);
			if (data != null && data.hasEgg)
				list.add(data);
		}
		return list.toArray(new EntityData[list.size()]);
	}

	public static int eggIDCounter = 499;

	public static void registerEntityEgg(Class<? extends Entity> entity, int primaryColor, int secondaryColor) {
		int id = getUniqueEntityEggId();

		EntityList.IDtoClassMapping.put(id, entity);
		EntityList.entityEggs.put(id, new EntityEggInfo(id, primaryColor, secondaryColor));
		eggIds.put(entity, id);
	}

	public static ItemStack getEggFromEntity(Entity entity) {
		return new ItemStack(Items.spawn_egg, 1, eggIds.get(entity.getClass()));
	}

	public static int getUniqueEntityEggId() {
		while (EntityList.getClassFromID(++eggIDCounter) != null) ;
		eggIDs.add(eggIDCounter);
		return eggIDCounter;
	}

	public static class EntityData {

		public final String name;
		public final int id, eggColour1, eggColour2;
		public final boolean hasEgg;

		EntityData(String name, int id, int eggColour1, int eggColour2, boolean hasEgg) {
			this.name = name;
			this.id = id;
			this.eggColour1 = eggColour1;
			this.eggColour2 = eggColour2;
			this.hasEgg = hasEgg;
		}
	}
}