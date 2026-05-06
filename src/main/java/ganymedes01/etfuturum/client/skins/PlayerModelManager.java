package ganymedes01.etfuturum.client.skins;

import com.mojang.authlib.minecraft.MinecraftSessionService;
import cpw.mods.fml.client.registry.RenderingRegistry;
import ganymedes01.etfuturum.client.model.ModelPlayer;
import ganymedes01.etfuturum.lib.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

import java.io.File;
import java.util.*;
import java.util.function.Function;

public class PlayerModelManager {

	public static final String MODEL_KEY = Reference.MOD_ID + "_model";

	public static Map<UUID, Boolean> alexCache = new WeakHashMap<>();

	private static boolean enabled = false;
	private static final Set<Function<ModelPlayer, DelegatedModelBiped>> DELEGATED_BIPED_MODEL_FACTORIES = new HashSet<>();

	public static void registerModelDelegateFactory(Function<ModelPlayer, DelegatedModelBiped> delegatedModelBiped) {
		DELEGATED_BIPED_MODEL_FACTORIES.add(delegatedModelBiped);
	}

	public static boolean shouldRegenerateModelDelegates(ModelPlayer model) {
		return DELEGATED_BIPED_MODEL_FACTORIES.size() != model.getDelegatesCount(); // this is kinda lazy
	}

	public static Set<DelegatedModelBiped> constructDelegatesFor(ModelPlayer model) {
		Set<DelegatedModelBiped> delegates = new HashSet<>();
		for (Function<ModelPlayer, DelegatedModelBiped> factory : DELEGATED_BIPED_MODEL_FACTORIES) {
			delegates.add(factory.apply(model));
		}
		return delegates;
	}

	public static boolean isEnabled() {
		return enabled;
	}

	public static void enableNewModels() {
		TextureManager texManager = Minecraft.getMinecraft().renderEngine;
		File skinFolder = new File(Minecraft.getMinecraft().fileAssets, "skins");
		MinecraftSessionService sessionService = Minecraft.getMinecraft().func_152347_ac(); // getSessionService
		Minecraft.getMinecraft().field_152350_aA/*skinManager*/ = new NewSkinManager(Minecraft.getMinecraft().func_152342_ad()/*getSkinManager*/, texManager, skinFolder, sessionService);
		RenderingRegistry.registerEntityRenderingHandler(EntityPlayer.class, new NewRenderPlayer());
		enabled = true;
	}

	public static boolean isPlayerModelAlex(EntityPlayer player) {
		if (player == null || player.getUniqueID() == null)
			return false;

		Boolean isAlex = alexCache.get(player.getUniqueID());
		if (isAlex == null) {
			NBTTagCompound nbt = player.getEntityData();
			if (nbt.hasKey(MODEL_KEY, Constants.NBT.TAG_BYTE)) {
				nbt.removeTag(MODEL_KEY);
			}
			ThreadCheckAlex skinthread = new ThreadCheckAlex();
			skinthread.startWithArgs(player.getUniqueID());
			return false;
		}
		return isAlex;
	}
}