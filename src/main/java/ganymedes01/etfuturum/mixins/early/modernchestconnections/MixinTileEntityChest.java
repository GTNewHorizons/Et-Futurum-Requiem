package ganymedes01.etfuturum.mixins.early.modernchestconnections;

import ganymedes01.etfuturum.core.utils.ChestDir;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import org.joml.Vector3ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityChest.class)
public abstract class MixinTileEntityChest extends TileEntity {
    @Shadow
    public boolean adjacentChestChecked;

    @Shadow
    public TileEntityChest adjacentChestZNeg;

    @Shadow
    public TileEntityChest adjacentChestXPos;

    @Shadow
    public TileEntityChest adjacentChestXNeg;

    @Shadow
    public TileEntityChest adjacentChestZPos;

    @Inject(method = "invalidate", at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntity;invalidate()V", shift = At.Shift.AFTER))
    private void updateAdjacent(CallbackInfo ci) {
        if (worldObj == null) return;

        TileEntityChest self = (TileEntityChest) (Object) this;
        for (ChestDir conn : ChestDir.VALID_CONNECTIONS) {
            Vector3ic off = conn.toOffset();
            int adjX = xCoord + off.x();
            int adjY = yCoord + off.y();
            int adjZ = zCoord + off.z();

            Block adjBlock = worldObj.getBlock(adjX, adjY, adjZ);
            if (adjBlock != getBlockType()) continue;

            TileEntityChest adjTile = (TileEntityChest) worldObj.getTileEntity(adjX, adjY, adjZ);
            if (adjTile == null) continue;

            int dir = etfu$adjacentUpdateDir(conn);
            ((AccessorTileEntityChest) adjTile).etfu$updateAdjacentChest(self, dir);
        }
    }

    /**
     * @author kurrycat
     * @reason replace existing block connection logic
     */
    @Overwrite
    public void checkForAdjacentChests() {
        if (adjacentChestChecked) return;

        adjacentChestChecked = true;
        adjacentChestZNeg = null;
        adjacentChestXPos = null;
        adjacentChestXNeg = null;
        adjacentChestZPos = null;

        if (worldObj == null) return;
        if (isInvalid()) return;

        int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
        if (ChestDir.hasConnectionMeta(meta)) {
            // only try to link connected chest
            ChestDir conn = ChestDir.fromConnectionMeta(meta);
            if (conn == ChestDir.NONE) return;

            TileEntityChest adjTile = etfu$getConnectedChestInDir(conn, meta);
            if (adjTile != null) {
                etfu$linkToChest(conn, adjTile);
            }
        } else {
            // try to link to first possible legacy chest connection
            for (ChestDir conn : ChestDir.VALID_CONNECTIONS) {
                TileEntityChest adjTile = etfu$getConnectedChestInDir(conn, meta);
                if (adjTile != null) {
                    etfu$linkToChest(conn, adjTile);
                    break;
                }
            }
        }
    }

    @Unique
    private TileEntityChest etfu$getConnectedChestInDir(ChestDir dir, int meta) {
        Vector3ic off = dir.toOffset();
        int adjX = xCoord + off.x();
        int adjY = yCoord + off.y();
        int adjZ = zCoord + off.z();

        Block adjBlock = worldObj.getBlock(adjX, adjY, adjZ);
        if (adjBlock != getBlockType()) return null;

        int adjMeta = worldObj.getBlockMetadata(adjX, adjY, adjZ);
        // only connect to modern chests that are connected to us
        if (ChestDir.hasConnectionMeta(meta) && ChestDir.fromConnectionMeta(adjMeta) != dir.flip()) return null;
        // if we're legacy, connect to any non-modern chest
        if (!ChestDir.hasConnectionMeta(meta) && ChestDir.hasConnectionMeta(adjMeta)) return null;

        return (TileEntityChest) worldObj.getTileEntity(adjX, adjY, adjZ);
    }

    @Unique
    private void etfu$linkToChest(ChestDir conn, TileEntityChest tile) {
        etfu$setAdjacentChest(conn, tile);
        int dir = etfu$adjacentUpdateDir(conn);
        ((AccessorTileEntityChest) tile).etfu$updateAdjacentChest((TileEntityChest) (Object) this, dir);
    }

    @Unique
    private void etfu$setAdjacentChest(ChestDir dir, TileEntityChest tile) {
        switch (dir) {
            case NEG_Z -> adjacentChestZNeg = tile;
            case POS_X -> adjacentChestXPos = tile;
            case POS_Z -> adjacentChestZPos = tile;
            case NEG_X -> adjacentChestXNeg = tile;
        }
    }

    @Unique
    private int etfu$adjacentUpdateDir(ChestDir dir) {
        return switch (dir) {
            case NEG_Z -> 0;
            case POS_X -> 1;
            case POS_Z -> 2;
            case NEG_X -> 3;
            case NONE -> throw new AssertionError();
        };
    }
}
