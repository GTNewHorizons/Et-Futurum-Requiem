package ganymedes01.etfuturum.mixins.late.deepslateores;

import appeng.client.render.BlockRenderInfo;
import appeng.client.render.blocks.RenderQuartzOre;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.blocks.BaseSubtypesBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RenderQuartzOre.class)
public class MixinRenderQuartzOre {

	@WrapOperation(method = "renderInventory(Lappeng/block/solids/OreQuartz;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/RenderBlocks;Lnet/minecraftforge/client/IItemRenderer$ItemRenderType;[Ljava/lang/Object;)V",
			at = @At(value = "INVOKE", target = "Lappeng/client/render/BlockRenderInfo;setTemporaryRenderIcon(Lnet/minecraft/util/IIcon;)V"), remap = false)
	private void overrideInventoryTexture(BlockRenderInfo instance, IIcon originalIcon, Operation<Void> original,
										  @Local(argsOnly = true) ItemStack stack, @Local(argsOnly = true) Object[] args) {
		BaseSubtypesBlock block = (BaseSubtypesBlock) ModBlocks.DEEPSLATE_CERTUS_QUARTZ_ORE.get();
		ItemStack actualStack = args[args.length - 1] instanceof ItemStack itemStack ? itemStack : null; // Check for inserted data, if found, override texture
		original.call(instance, originalIcon == null ? null : actualStack == null ? originalIcon : block.getIcons()[stack.getItemDamage() == 1 ? 1 : 0]);
	}

	@WrapOperation(method = "renderInWorld(Lappeng/block/solids/OreQuartz;Lnet/minecraft/world/IBlockAccess;IIILnet/minecraft/client/renderer/RenderBlocks;)Z",
			at = @At(value = "INVOKE", target = "Lappeng/client/render/BlockRenderInfo;setTemporaryRenderIcon(Lnet/minecraft/util/IIcon;)V"), remap = false)
	private void overrideWorldTexture(BlockRenderInfo instance, IIcon originalIcon, Operation<Void> original,
									  @Local(argsOnly = true) IBlockAccess world,
									  @Local(argsOnly = true, ordinal = 0) int x, @Local(argsOnly = true, ordinal = 1) int y, @Local(argsOnly = true, ordinal = 2) int z) {
		BaseSubtypesBlock block = (BaseSubtypesBlock) ModBlocks.DEEPSLATE_CERTUS_QUARTZ_ORE.get();
		original.call(instance, originalIcon == null ? null : world.getBlock(x, y, z) != block ? originalIcon : block.getIcons()[world.getBlockMetadata(x, y, z) == 1 ? 1 : 0]);
	}
}
