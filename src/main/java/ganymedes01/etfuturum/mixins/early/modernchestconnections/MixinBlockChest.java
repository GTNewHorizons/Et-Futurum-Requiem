package ganymedes01.etfuturum.mixins.early.modernchestconnections;

import ganymedes01.etfuturum.core.utils.ChestDir;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.ForgeDirection;
import org.joml.Vector3ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockChest.class)
public class MixinBlockChest extends Block {
    @Shadow
    private static boolean func_149953_o(World world, int x, int y, int z) {throw new UnsupportedOperationException("Implemented via mixin");}

    protected MixinBlockChest(Material materialIn) {
        super(materialIn);
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float subX, float subY, float subZ, int meta) {
        int oldMeta = super.onBlockPlaced(world, x, y, z, side, subX, subY, subZ, meta);
        int newMeta = ChestDir.NONE.toConnectionMeta(oldMeta);

        ChestDir conn = ChestDir.fromSideMeta(side);
        // automatic connections depend on placer state
        if (conn == ChestDir.NONE) return newMeta;

        // horizontal side clicked, connect to it if it's a connectable chest
        Vector3ic off = conn.toOffset();
        Block adjacentBlock = world.getBlock(x + off.x(), y + off.y(), z + off.z());
        // no adjacent chest to connect to
        if (adjacentBlock != this) return newMeta;

        int adjacentMeta = world.getBlockMetadata(x + off.x(), y + off.y(), z + off.z());
        // only connect to not already connected chests
        if (ChestDir.fromConnectionMeta(adjacentMeta) == ChestDir.NONE) {
            return conn.toConnectionMeta(newMeta);
        }

        return newMeta;
    }

    /**
     * @author kurrycat
     * @reason replace existing block connection logic
     */
    @Overwrite
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        if (!ChestDir.hasConnectionMeta(meta)) {
            // we were not placed by a user, default to unconnected modern chest
            int newMeta = ChestDir.NONE.toConnectionMeta(meta);
            world.setBlockMetadataWithNotify(x, y, z, newMeta, 0);
        }
    }

    /**
     * @author kurrycat
     * @reason replace existing block connection logic
     */
    @Overwrite
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack item) {
        int explicitMeta = world.getBlockMetadata(x, y, z);

        int quadrant = MathHelper.floor_double((double) (placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        ChestDir dir = switch (quadrant) {
            case 0 -> ChestDir.POS_Z;
            case 1 -> ChestDir.NEG_X;
            case 2 -> ChestDir.NEG_Z;
            case 3 -> ChestDir.POS_X;
            default -> ChestDir.NONE;
        };

        int dirExplicitMeta = dir.toSideMeta(explicitMeta);
        etfu$tryConnectOnPlacedBy(world, x, y, z, placer, dirExplicitMeta);

        if (item.hasDisplayName()) {
            TileEntity tile = world.getTileEntity(x, y, z);
            ((TileEntityChest) tile).func_145976_a(item.getDisplayName());
        }
    }

    // always writes the meta to world and notifies neighbors of affected adjacent
    @Unique
    private void etfu$tryConnectOnPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, int explicitMeta) {
        // try explicit connect
        ChestDir explicitConn = ChestDir.fromConnectionMeta(explicitMeta);
        if (explicitConn != ChestDir.NONE) {
            if (etfu$tryConnect(world, x, y, z, explicitConn, explicitMeta)) return;
        }

        int meta = ChestDir.NONE.toConnectionMeta(explicitMeta);

        // if sneaking, don't connect automatically
        if (placer.isSneaking()) {
            world.setBlockMetadataWithNotify(x, y, z, meta, 3);
            return;
        }

        // try automatic connect
        for (ChestDir conn : ChestDir.VALID_CONNECTIONS) {
            if (etfu$tryConnect(world, x, y, z, conn, meta)) return;
        }

        // can't connect to anything, just set the given meta
        world.setBlockMetadataWithNotify(x, y, z, meta, 3);
    }

    @Unique
    private boolean etfu$tryConnect(World world, int x, int y, int z, ChestDir conn, int baseMeta) {
        Vector3ic off = conn.toOffset();
        int cx = x + off.x();
        int cy = y + off.y();
        int cz = z + off.z();

        Block adjBlock = world.getBlock(cx, cy, cz);
        if (adjBlock != this) return false;

        int oldAdjMeta = world.getBlockMetadata(cx, cy, cz);
        // check if candidate already connected
        ChestDir adjConn = etfu$getConnection(world, cx, cy, cz, oldAdjMeta, conn.flip());
        if (adjConn != ChestDir.NONE) return false;

        // connect to valid connection target chest
        ChestDir otherConn = conn.flip();
        int meta = conn.toConnectionMeta(baseMeta);
        int adjMeta = otherConn.toConnectionMeta(oldAdjMeta);

        int newMeta = etfu$updateFacingMeta(world, conn, meta, adjMeta, x, y, z);
        int newAdjMeta = etfu$updateFacingMeta(world, otherConn, adjMeta, newMeta, cx, cy, cz);

        world.setBlockMetadataWithNotify(x, y, z, newMeta, 2);
        world.setBlockMetadataWithNotify(cx, cy, cz, newAdjMeta, 3);
        etfu$notifyBlock(world, x, y, z, this);
        return true;
    }

    @Inject(method = "onNeighborBlockChange", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockContainer;onNeighborBlockChange(Lnet/minecraft/world/World;IIILnet/minecraft/block/Block;)V"))
    private void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor, CallbackInfo ci) {
        int meta = world.getBlockMetadata(x, y, z);

        ChestDir conn = ChestDir.fromConnectionMeta(meta);
        // we don't care about neighbors
        if (conn == ChestDir.NONE) return;

        Vector3ic off = conn.toOffset();
        Block adjBlock = world.getBlock(x + off.x(), y + off.y(), z + off.z());
        if (adjBlock == this) {
            int adjMeta = world.getBlockMetadata(x + off.x(), y + off.y(), z + off.z());
            // connected chest is also connected to us, we're fine
            if (ChestDir.fromConnectionMeta(adjMeta) == conn.flip()) return;
        }

        // connected to a non-chest or a chest that's not connected to us, reset connection
        int newMeta = ChestDir.NONE.toConnectionMeta(meta);
        world.setBlockMetadataWithNotify(x, y, z, newMeta, 3);
    }

    /**
     * @author kurrycat
     * @reason replace existing inventory connection logic
     */
    @Overwrite
    public IInventory func_149951_m/*getInventory*/(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);

        TileEntityChest tile = (TileEntityChest) world.getTileEntity(x, y, z);
        if (tile == null || etfu$isChestBlocked(world, x, y, z)) return null;

        // find valid connection target
        ChestDir conn = etfu$getConnection(world, x, y, z, meta, null);
        // single chest
        if (conn == ChestDir.NONE) return tile;

        // check for broken connection
        if (ChestDir.hasConnectionMeta(meta)) {
            Vector3ic off = conn.toOffset();

            Block adjBlock = world.getBlock(x + off.x(), y + off.y(), z + off.z());
            // we're connected to a chest, but there is no chest, broken state
            if (adjBlock != this) return null;

            int adjMeta = world.getBlockMetadata(x + off.x(), y + off.y(), z + off.z());
            // we're connected to a chest, but it's not connected to us, broken state
            if (ChestDir.fromConnectionMeta(adjMeta) != conn.flip()) return null;
        }

        Vector3ic off = conn.toOffset();

        // blocked
        if (etfu$isChestBlocked(world, x + off.x(), y + off.y(), z + off.z())) return null;

        TileEntityChest adjTile = (TileEntityChest) world.getTileEntity(x + off.x(), y + off.y(), z + off.z());
        // broken TE
        if (adjTile == null) return null;

        IInventory upper = conn.isPos() ? tile : adjTile;
        IInventory lower = conn.isPos() ? adjTile : tile;
        return new InventoryLargeChest("container.chestDouble", upper, lower);
    }

    @Unique
    private boolean etfu$isChestBlocked(World world, int x, int y, int z) {
        if (world.isSideSolid(x, y + 1, z, ForgeDirection.DOWN)) return true;
        return func_149953_o/*blockedBySittingOcelot*/(world, x, y, z);
    }

    /**
     * @author kurrycat
     * @reason modern chests can always be placed
     */
    @Overwrite
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return true;
    }

    /**
     * @author kurrycat
     * @reason replace existing block connection logic
     */
    @Overwrite
    public void func_149954_e/*updateChestFacing*/(World world, int x, int y, int z) {
        if (world.isRemote) return;

        int meta = world.getBlockMetadata(x, y, z);

        ChestDir conn = etfu$getConnection(world, x, y, z, meta, null);
        // single chest
        if (conn == ChestDir.NONE) return;

        Vector3ic off = conn.toOffset();
        int adjMeta = world.getBlockMetadata(x + off.x(), y + off.y(), z + off.z());
        int newMeta = etfu$updateFacingMeta(world, conn, meta, adjMeta, x, y, z);
        world.setBlockMetadataWithNotify(x, y, z, newMeta, 3);
    }

    /**
     * @author kurrycat
     * @reason replace existing block connection logic
     */
    @Overwrite
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);

        ChestDir conn = etfu$getConnection(world, x, y, z, meta, null);
        switch (conn) {
            case NONE -> setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
            case POS_Z -> setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 1.0F);
            case NEG_Z -> setBlockBounds(0.0625F, 0.0F, 0.0F, 0.9375F, 0.875F, 0.9375F);
            case POS_X -> setBlockBounds(0.0625F, 0.0F, 0.0625F, 1.0F, 0.875F, 0.9375F);
            case NEG_X -> setBlockBounds(0.0F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
        }
    }

    @Unique
    private ChestDir etfu$getConnection(IBlockAccess world, int x, int y, int z, int meta, ChestDir skip) {
        if (ChestDir.hasConnectionMeta(meta)) return ChestDir.fromConnectionMeta(meta);

        // find legacy connection
        for (ChestDir conn : ChestDir.VALID_CONNECTIONS) {
            if (conn == skip) continue;

            Vector3ic off = conn.toOffset();

            Block adjBlock = world.getBlock(x + off.x(), y + off.y(), z + off.z());
            if (adjBlock != this) continue;

            int adjMeta = world.getBlockMetadata(x + off.x(), y + off.y(), z + off.z());
            // invalid legacy connection target
            if (ChestDir.hasConnectionMeta(adjMeta)) continue;

            // valid connection target
            return conn;
        }
        return ChestDir.NONE;
    }

    // only reads blocks from world, no meta
    @Unique
    private int etfu$updateFacingMeta(IBlockAccess world, ChestDir conn, int meta, int adjMeta, int x, int y, int z) {
        ChestDir facing = ChestDir.fromSideMeta(meta);
        // facing is already correct
        if (facing.orthogonal(conn)) return meta;

        ChestDir connFacing = ChestDir.fromSideMeta(adjMeta);
        // wrong facing, but conn is correct, so take their facing
        if (connFacing.orthogonal(conn)) return connFacing.toSideMeta(meta);

        // prefer the side with less opaque blocks
        Vector3ic r = conn.turn().toOffset();
        Vector3ic l = conn.turn().flip().toOffset();

        Block tr = world.getBlock(x + r.x(), y + r.y(), z + r.z());
        Block tl = world.getBlock(x + l.x(), y + l.y(), z + l.z());

        Vector3ic o = conn.toOffset();
        Block cr = world.getBlock(x + o.x() + r.x(), y + o.y() + r.y(), z + o.z() + r.z());
        Block cl = world.getBlock(x + o.x() + l.x(), y + o.y() + l.y(), z + o.z() + l.z());

        int rOpaque = (tr.func_149730_j() ? 1 : 0) + (cr.func_149730_j() ? 1 : 0);
        int lOpaque = (tl.func_149730_j() ? 1 : 0) + (cl.func_149730_j() ? 1 : 0);

        ChestDir newFacing = rOpaque <= lOpaque ? conn.turn().flip() : conn.turn();
        return newFacing.toSideMeta(meta);
    }

    @Unique
    private void etfu$notifyBlock(World world, int x, int y, int z, Block block) {
        Chunk chunk = world.getChunkFromBlockCoords(x, z);
        world.markAndNotifyBlock(x, y, z, chunk, block, block, 1);
    }
}
