package ganymedes01.etfuturum.mixins.early.wither;

import ganymedes01.etfuturum.blocks.BlockSoulSoil;

import static net.minecraft.block.Block.getBlockById;
import net.minecraft.block.BlockSkull;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockSkull.class, remap = false)
public abstract class MixinBlockSkull_OnPlacingWitherSkull {
    // spotless:off
    private static final int[][] PATTERN_BASE = {
        {0,2,-1}, {0,2,0}, {0,2,1}, // Top row of skulls
        {0,1,-1}, {0,1,0}, {0,1,1}, // Middle row: soul sand / soil
                  {0,0,0}           // last row: soul sand / soil
    };

    private static final int[][] FORWARD = {
        {1,0,0}, {0,1,0}, {0,0,1}
    };

    private static final int[][][] PERPENDICULAR = {
        {{0,1,0}, {0,-1,0}, {0,0,1}, {0,0,-1}},  // forward = +X
        {{1,0,0}, {-1,0,0}, {0,0,1}, {0,0,-1}},  // forward = +Y
        {{1,0,0}, {-1,0,0}, {0,1,0}, {0,-1,0}},  // forward = +Z
    };
    // spotless:on

    // rotation: 0: +x, 1: -x, 2: +y, 3: -y, 4: +z, 5: -z; x: right; y: up; z: forward
    private boolean matchPattern(int[][] pattern, World w, int x, int y, int z, int offset, int rotation) {
        int[] f = FORWARD[rotation / 4];
        int[] u = PERPENDICULAR[rotation / 4][rotation % 4];

        int k = offset - 1;
        int ox = x - k * f[0] - 2 * u[0];
        int oy = y - k * f[1] - 2 * u[1];
        int oz = z - k * f[2] - 2 * u[2];

        if (oy < 0) return false;

        for (int i = 0; i < pattern.length; i++) {
            int dx = pattern[i][0];
            int dy = pattern[i][1];
            int dz = pattern[i][2];

            int wx = ox + dy * u[0] + dz * f[0];
            int wy = oy + dy * u[1] + dz * f[1];
            int wz = oz + dy * u[2] + dz * f[2];

            boolean isHead = i < 3;
            if (isHead ? !isWitherSkull(w, wx, wy, wz)
                    : !isSoulSand(w, wx, wy, wz)) {
                return false;
            }
        }
        return true;
    }

    private boolean isWitherSkull(World w, int x, int y, int z) {
        if (w.getBlock(x, y, z) != Blocks.skull) return false;
        TileEntity te = w.getTileEntity(x, y, z);
        return te instanceof TileEntitySkull
                && ((TileEntitySkull) te).func_145904_a() == 1;
    }

    private boolean isSoulSand(World w, int x, int y, int z) {
        return w.getBlock(x, y, z) == Blocks.soul_sand || w.getBlock(x, y, z) instanceof BlockSoulSoil;
    }

    private void summon(World w, int ox, int oy, int oz, int[] u, int[] f) {
        for (int i = 0; i < PATTERN_BASE.length; i++) {
            int dy = PATTERN_BASE[i][1];
            int dz = PATTERN_BASE[i][2];
            if (i < 3) {
                w.setBlock(ox + dy * u[0] + dz * f[0],
                           oy + dy * u[1] + dz * f[1],
                           oz + dy * u[2] + dz * f[2],
                              getBlockById(0), 8, 2);
            }
            w.setBlock(ox + dy * u[0] + dz * f[0],
                       oy + dy * u[1] + dz * f[1],
                       oz + dy * u[2] + dz * f[2],
                          getBlockById(0), 0, 2);
        }

        if (w.isRemote) return;

        int sx = ox + 2 * u[0];
        int sy = oy + 2 * u[1];
        int sz = oz + 2 * u[2];

        float yaw = (f[0] != 0) ? 0.0F : 90.0F;

        EntityWither entityWither = new EntityWither(w);
        entityWither.setLocationAndAngles(sx + 0.5, sy + 0.55, sz + 0.5, yaw, 0.0F);
        entityWither.renderYawOffset = yaw;
        entityWither.func_82206_m();

        for (Object obj : w.getEntitiesWithinAABB(EntityPlayer.class,
                entityWither.boundingBox.expand(50.0, 50.0, 50.0))) {
            ((EntityPlayer) obj).triggerAchievement(AchievementList.field_150963_I);
        }

        w.spawnEntityInWorld(entityWither);

        for (int i = 0; i < 120; i++) {
            w.spawnParticle("snowballpoof",
            sx + w.rand.nextDouble(),
            sy - 2 + w.rand.nextDouble() * 3.9,
            sz + w.rand.nextDouble(),
            0, 0, 0);
        }

        for (int i = 0; i < PATTERN_BASE.length; i++) {
            int dy = PATTERN_BASE[i][1];
            int dz = PATTERN_BASE[i][2];
            w.notifyBlockChange(ox + dy * u[0] + dz * f[0],
                                oy + dy * u[1] + dz * f[1],
                                oz + dy * u[2] + dz * f[2],
                                getBlockById(0));
        }
    }

    @Inject(method = "func_149965_a", at = @At(value = "HEAD"), cancellable = true, remap = true)
    private void etfuturum$checkWitherPattern(World w, int x, int y, int z, TileEntitySkull skull, CallbackInfo ci) {
        if (w.isRemote || w.difficultySetting == EnumDifficulty.PEACEFUL || skull.func_145904_a() != 1) return;
        for (int rotation = 0; rotation < 12; rotation++) {
            for (int offset = 0; offset < 3; offset++) {
                if (!matchPattern(PATTERN_BASE, w, x, y, z, offset, rotation)) {
                    continue;
                }
                int[] f = FORWARD[rotation / 4];
                int[] u = PERPENDICULAR[rotation / 4][rotation % 4];
                int k = offset - 1;
                int ox = x - k * f[0] - 2 * u[0];
                int oy = y - k * f[1] - 2 * u[1];
                int oz = z - k * f[2] - 2 * u[2];
                summon(w, ox, oy, oz, u, f);
                ci.cancel();
                return;
            }
        }
    }
}
