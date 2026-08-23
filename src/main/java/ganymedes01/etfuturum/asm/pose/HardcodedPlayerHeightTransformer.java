package ganymedes01.etfuturum.asm.pose;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import ganymedes01.etfuturum.core.utils.Logger;
import net.minecraft.launchwrapper.IClassTransformer;
import org.spongepowered.asm.lib.ClassReader;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.lib.tree.FieldInsnNode;
import org.spongepowered.asm.lib.tree.InsnNode;
import org.spongepowered.asm.lib.tree.LdcInsnNode;
import org.spongepowered.asm.lib.tree.MethodInsnNode;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.transformers.MixinClassWriter;
import scala.tools.asm.Opcodes;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class HardcodedPlayerHeightTransformer implements IClassTransformer {
    private static final Map<String, HashSet<String>> targets = new HashMap<>();
    private static void addTarget(String className, String... methods) {
        targets.put(className, new HashSet<>(Arrays.asList(methods)));
    }
    static {
        // minecraft
        addTarget("net.minecraft.block.BlockPistonBase", "determineOrientation(Lnet/minecraft/world/World;IIILnet/minecraft/entity/EntityLivingBase;)I");
        addTarget("net.minecraft.entity.projectile.EntityFishHook", "<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)V");
        addTarget("net.minecraft.item.ItemBoat", "func_77659_a(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;", "onItemRightClick(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;");
        addTarget("net.minecraft.item.ItemEnderEye", "func_77659_a(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;", "onItemRightClick(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;");

        // EntityPlayer
        addTarget("com.darkona.adventurebackpack.item.ItemComponent", "placeBoat(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;Z)Lnet/minecraft/item/ItemStack;");
        addTarget("appeng.items.tools.powered.ToolMassCannon", "func_77659_a(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;", "onItemRightClick(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;");
        addTarget("appeng.util.Platform", "rayTrace(Lnet/minecraft/entity/player/EntityPlayer;ZZ)Lnet/minecraft/util/MovingObjectPosition;");
        addTarget("tuhljin.automagy.lib.TjUtil", "getBlockCoordPlayerIsLookingAt(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;)Ltuhljin/automagy/codechicken/lib/vec/BlockCoord;");
        addTarget("com.arc.bloodarsenal.common.items.sigil.SigilEnder", "func_77659_a(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;", "onItemRightClick(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;");
        addTarget("com.arc.bloodarsenal.common.items.sigil.SigilLightning", "func_77659_a(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;", "onItemRightClick(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;");
        addTarget("WayofTime.alchemicalWizardry.common.items.sigil.SigilFluid", "emptyItemToWorld(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;");
        addTarget("team.chisel.utils.General", "getMovingObjectPositionFromPlayer(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;Z)Lnet/minecraft/util/MovingObjectPosition;");
        addTarget("codechicken.core.commands.PlayerCommand", "getPlayerLookingAtBlock(Lnet/minecraft/entity/player/EntityPlayerMP;F)Lnet/minecraft/world/ChunkPosition;", "getPlayerLookingAtEntity(Lnet/minecraft/entity/player/EntityPlayerMP;F)Lnet/minecraft/entity/Entity;");
        addTarget("emt.item.tool.ItemThorHammer", "func_77659_a(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;", "onItemRightClick(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;");
        addTarget("emt.item.ItemMaterials", "onUsingTick(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;I)V");
        addTarget("fox.spiteful.forbidden.items.wands.ItemFocusBlink", "onFocusRightClick(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/MovingObjectPosition;)Lnet/minecraft/item/ItemStack;");
        addTarget("micdoodle8.mods.galacticraft.core.items.ItemOilExtractor", "getNearestOilBlock(Lnet/minecraft/entity/player/EntityPlayer;)Lmicdoodle8/mods/galacticraft/api/vector/Vector3;");
        addTarget("micdoodle8.mods.galacticraft.core.items.ItemBuggy", "func_77659_a(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;", "onItemRightClick(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;");
        addTarget("gravisuite.redpower.coreLib", "retraceBlock(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;III)Lnet/minecraft/util/MovingObjectPosition;");
        addTarget("mrtjp.projectred.expansion.TileMachine", "calcFacing(Lnet/minecraft/entity/player/EntityPlayer;)I");
        addTarget("tconstruct.smeltery.items.FilledBucket", "func_77659_a(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;", "onItemRightClick(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;");
        addTarget("com.kentington.thaumichorizons.common.items.ItemBoatThaumium", "func_77659_a(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;", "onItemRightClick(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;");
        addTarget("com.kentington.thaumichorizons.common.items.ItemBoatGreatwood", "func_77659_a(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;", "onItemRightClick(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;");
        addTarget("com.emoniph.witchery.item.ItemGeneral", "placeBroom(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;IIIIFFF)Z");

        // Entity
        addTarget("pl.asie.lib.block.BlockBase", "determineRotation(Lnet/minecraft/world/World;IIILnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/item/ItemStack;)I");
        addTarget("com.brandon3055.draconicevolution.common.blocks.machine.FlowGate", "determineOrientation(Lnet/minecraft/world/World;IIILnet/minecraft/entity/EntityLivingBase;)I");
        addTarget("com.brandon3055.draconicevolution.common.utils.Utils", "determineOrientation(IIILnet/minecraft/entity/EntityLivingBase;)I");
        addTarget("com.enderio.core.client.render.RenderUtil", "createBillboardMatrix(Lcom/enderio/core/common/vecmath/Vector3d;Lnet/minecraft/entity/EntityLivingBase;)Lcom/enderio/core/common/vecmath/Matrix4d;");
        addTarget("micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore", "orientCamera(F)V"); // fsub
        addTarget("mods.natura.blocks.tech.NetherPistonBase", "determineOrientation(Lnet/minecraft/world/World;IIILnet/minecraft/entity/EntityLivingBase;)I");
        addTarget("openblocks.common.entity.EntityGoldenEye", "targetStructure(Lnet/minecraft/entity/Entity;Lnet/minecraft/world/ChunkPosition;)V");
        addTarget("mods.railcraft.common.util.misc.MiscTools", "getSideClosestToPlayer(Lnet/minecraft/world/World;IIILnet/minecraft/entity/EntityLivingBase;)Lnet/minecraftforge/common/util/ForgeDirection;");
        addTarget("thaumcraft.common.lib.utils.BlockUtils", "getTargetBlock(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;Z)Lnet/minecraft/util/MovingObjectPosition;");
        addTarget("tb.common.block.BlockRelocator", "determineOrientation(Lnet/minecraft/world/World;IIILnet/minecraft/entity/EntityLivingBase;)I");
        addTarget("twilightforest.block.BlockTFNagastoneEtched", "determineOrientation(Lnet/minecraft/world/World;IIILnet/minecraft/entity/EntityLivingBase;)I");
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;

        if (!targets.containsKey(transformedName)) return basicClass;
        HashSet<String> patchedMethods = targets.get(transformedName);

        ClassReader cr = new ClassReader(basicClass);
        ClassNode cn = new ClassNode();
        cr.accept(cn, ClassReader.SKIP_FRAMES);

        for (MethodNode method : cn.methods)
        {
            String mappedMethodName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(name, method.name, method.desc);
            String mappedMethodDesc = FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(method.desc);
            if (!patchedMethods.contains(mappedMethodName + mappedMethodDesc)) continue;
            boolean methodPatched = false;
            for (AbstractInsnNode instruction = method.instructions.getFirst(); instruction != null; instruction = instruction.getNext())
            {
                if (instruction.getOpcode() == Opcodes.GETFIELD)
                {
                    FieldInsnNode fin = (FieldInsnNode) instruction;
                    if ("F".equals(fin.desc)) {
                        String fieldName = FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(fin.owner, fin.name, fin.desc);
                        if (("yOffset".equals(fieldName) || "field_70129_M".equals(fieldName)) && isTarget(fin)) {
                            methodPatched = true;
                            method.instructions.insertBefore(instruction, new InsnNode(Opcodes.DUP));
                            MethodInsnNode hook = new MethodInsnNode(Opcodes.INVOKESTATIC, "ganymedes01/etfuturum/asm/pose/HardcodedPlayerHeightHook", "adjustHeight", "(Lnet/minecraft/entity/Entity;F)F", false);
                            method.instructions.insert(instruction, hook);
                        }
                    }
                }
            }
            if (!methodPatched){
                Logger.warn("Failed to patch hardcoded height in " + transformedName +"."+ method.name + method.desc + ": target bytecode not found.");
            } else {
                Logger.info("succeed to patch" + transformedName);
            }
        }

        MixinClassWriter cw = new MixinClassWriter(MixinClassWriter.COMPUTE_MAXS | MixinClassWriter.COMPUTE_FRAMES);
        cn.accept(cw);
        return cw.toByteArray();
    }

    private @Nullable AbstractInsnNode getNextValidNode(AbstractInsnNode node)
    {
        AbstractInsnNode next = node.getNext();
        while (next != null && next.getOpcode() == -1) next = next.getNext();
        return next;
    }

    private @Nullable AbstractInsnNode getPrevValidNode(AbstractInsnNode node)
    {
        AbstractInsnNode prev = node.getPrevious();
        while (prev != null && prev.getOpcode() == -1) prev = prev.getPrevious();
        return prev;
    }

    private boolean isTarget(FieldInsnNode fin)
    {
        AbstractInsnNode curr = getNextValidNode(fin);
        boolean isSubFound = false;
        int limit = 5;
        while (curr != null && limit -- > 0){
            int op = curr.getOpcode();
            if (op == Opcodes.DSUB || op == Opcodes.FSUB)
            {
                isSubFound = true;
                break;
            }
            curr = getNextValidNode(curr);
        }
        if (!isSubFound) return false;
        curr = getPrevValidNode(fin);
        limit = 5;
        while (curr != null && limit-- > 0)
        {
            if (hasTargetConstant(curr)) return true;
            curr = getPrevValidNode(curr);
        }
        curr = getNextValidNode(fin);
        limit = 5;
        while (curr != null && limit-- > 0)
        {
            if (hasTargetConstant(curr)) return true;
            curr = getNextValidNode(curr);
        }
        return false;
    }

    private boolean hasTargetConstant(AbstractInsnNode instruction)
    {
        if (instruction.getOpcode() != Opcodes.LDC) return false;
        Object cst = ((LdcInsnNode) instruction).cst;
        if (cst instanceof Double && ((Double) cst == 1.62d || (Double) cst == 1.82d)) return true;
        if (cst instanceof Float && ((Float) cst == 1.62f || (Float) cst == 1.82f)) return true;
        return false;
    }
}
