package ganymedes01.etfuturum.mixins.early.worldheight;

import com.llamalad7.mixinextras.sugar.Local;
import ganymedes01.etfuturum.world.WorldHeightHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilChunkLoader.class)
public class MixinAnvilChunkLoader {

    @ModifyConstant(method = "readChunkFromNBT", constant = @Constant(intValue = 16))
    private int getIncreasedChunkSections(int original, World world, NBTTagCompound nbt) {

        return WorldHeightHandler.getChunkSections();
    }

    @Inject(method = "readChunkFromNBT", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagList;tagCount()I"), cancellable = true)
    private void continueIfAboveWorldHeight(World world, NBTTagCompound chunkNbt, CallbackInfoReturnable<Chunk> cir, @Local(name = "chunk") Chunk chunk, @Local(name = "nbttaglist") NBTTagList nbttaglist, @Local(name = "b0") byte b0, @Local(name = "aextendedblockstorage") ExtendedBlockStorage[] aextendedblockstorage, @Local(name = "flag") boolean flag) {

        for (int k = 0; k < nbttaglist.tagCount(); ++k) {

            NBTTagCompound sectionTag = nbttaglist.getCompoundTagAt(k);

            byte y = sectionTag.getByte("Y");

            if (y >= WorldHeightHandler.getChunkSections()) { continue; }

            ExtendedBlockStorage storage = new ExtendedBlockStorage(y << 4, flag);
            storage.setBlockLSBArray(sectionTag.getByteArray("Blocks"));

            if (sectionTag.hasKey("Data", 7)) {

                storage.setBlockMetadataArray(new NibbleArray(sectionTag.getByteArray("Data"), 4));
            }

            storage.setBlocklightArray(new NibbleArray(sectionTag.getByteArray("BlockLight"), 4));

            if (flag) {

                storage.setSkylightArray(new NibbleArray(sectionTag.getByteArray("SkyLight"), 4));
            }

            storage.removeInvalidBlocks();
            aextendedblockstorage[y] = storage;
        }

        chunk.setStorageArrays(aextendedblockstorage);

        if (chunkNbt.hasKey("Biomes", 7)) {

            chunk.setBiomeArray(chunkNbt.getByteArray("Biomes"));
        }

        cir.setReturnValue(chunk);
    }

    @Redirect(method = "loadEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntity;createAndLoadEntity(Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/tileentity/TileEntity;"))
    private TileEntity ignoreTileEntitiesAboveWorldHeight(NBTTagCompound tileEntities) {

        if(tileEntities.getInteger("y") > WorldHeightHandler.getMaxWorldHeight()) {

            return null;
        }

        return TileEntity.createAndLoadEntity(tileEntities);
    }
}
