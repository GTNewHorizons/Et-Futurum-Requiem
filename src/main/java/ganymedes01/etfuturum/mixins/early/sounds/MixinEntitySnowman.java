package ganymedes01.etfuturum.mixins.early.sounds;

import ganymedes01.etfuturum.Tags;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntitySnowman.class)
public class MixinEntitySnowman extends EntityGolem {

	public MixinEntitySnowman(World p_i1686_1_) {
		super(p_i1686_1_);
	}

	@Override
	protected String getHurtSound() {
		return Tags.MC_ASSET_VER + ":entity.snow_golem.hurt";
	}

	@Override
	protected String getDeathSound() {
		return Tags.MC_ASSET_VER + ":entity.snow_golem.death";
	}

	@Override
	protected String getLivingSound() {
		return Tags.MC_ASSET_VER + ":entity.snow_golem.ambient";
	}
}
