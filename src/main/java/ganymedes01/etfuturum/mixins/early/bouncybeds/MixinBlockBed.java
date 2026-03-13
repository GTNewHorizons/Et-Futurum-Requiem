package ganymedes01.etfuturum.mixins.early.bouncybeds;

import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = BlockBed.class)
public class MixinBlockBed extends Block {

    @Unique
    private static final Map<Entity, Double> BED_BOUNCE_CACHE = new WeakHashMap<>();
    @Unique
    private long etfuturum$lastBounceTick;

    protected MixinBlockBed(Material materialIn) {
        super(materialIn);
    }

    @Override
    public void onFallenUpon(World world, int x, int y, int z, Entity entity, float fallDistance) {
        if (!entity.isSneaking()) {
            entity.fallDistance /= 2;
            if (entity.motionY < 0) {
                BED_BOUNCE_CACHE.put(entity, entity.motionY * -0.66);
                etfuturum$lastBounceTick = world.getTotalWorldTime();
            }
        }
        super.onFallenUpon(world, x, y, z, entity, fallDistance);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (etfuturum$lastBounceTick == world.getTotalWorldTime()) {
            Double bounce = BED_BOUNCE_CACHE.remove(entity);
            if (bounce != null) {
                entity.motionY = bounce;
            }
        } else {
            BED_BOUNCE_CACHE.clear();
        }
        super.onEntityCollidedWithBlock(world, x, y, z, entity);
    }
}
