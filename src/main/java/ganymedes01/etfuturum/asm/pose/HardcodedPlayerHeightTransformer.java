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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class HardcodedPlayerHeightTransformer implements IClassTransformer {
    private static final Map<String, HashSet<String>> targets = new HashMap<>();
    private static void addTarget(String className, String... methods) {
        targets.put(className, new HashSet<>(Arrays.asList(methods)));
    }
    static {
        addTarget("com.darkona.adventurebackpack.item.ItemComponent", "placeBoat");
        addTarget("appeng.items.tools.powered.ToolMassCannon", "func_77659_a", "onItemRightClick");
        addTarget("appeng.util.Platform", "rayTrace");
        addTarget("tuhljin.automagy.lib.TjUtil", "getBlockCoordPlayerIsLookingAt");
        addTarget("com.arc.bloodarsenal.common.items.sigil.SigilEnder", "func_77659_a", "onItemRightClick");
        addTarget("com.arc.bloodarsenal.common.items.sigil.SigilLightning", "func_77659_a", "onItemRightClick");
        addTarget("WayofTime.alchemicalWizardry.common.items.sigil.SigilFluid", "emptyItemToWorld");
        addTarget("team.chisel.utils.General", "getMovingObjectPositionFromPlayer");
        addTarget("codechicken.core.commands.PlayerCommand", "getPlayerLookingAtBlock", "getPlayerLookingAtEntity");
        addTarget("emt.item.tool.ItemThorHammer", "func_77659_a", "onItemRightClick");
        addTarget("emt.item.ItemMaterials", "onUsingTick");
        addTarget("fox.spiteful.forbidden.items.wands.ItemFocusBlink", "onFocusRightClick");
        addTarget("micdoodle8.mods.galacticraft.core.items.ItemOilExtractor", "getNearestOilBlock");
        addTarget("micdoodle8.mods.galacticraft.core.items.ItemBuggy", "func_77659_a", "onItemRightClick");
        addTarget("gravisuite.redpower.coreLib", "retraceBlock");
        addTarget("mrtjp.projectred.expansion.TileMachine", "calcFacing");
        addTarget("tconstruct.smeltery.items.FilledBucket", "func_77659_a", "onItemRightClick");
        addTarget("com.kentington.thaumichorizons.common.items.ItemBoatThaumium", "func_77659_a", "onItemRightClick");
        addTarget("com.kentington.thaumichorizons.common.items.ItemBoatGreatwood", "func_77659_a", "onItemRightClick");
        addTarget("com.emoniph.witchery.item.ItemGeneral", "placeBroom");
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
            if (!patchedMethods.contains(method.name)) continue;
            boolean methodPatched = false;
            for (AbstractInsnNode instruction = method.instructions.getFirst(); instruction != null; instruction = instruction.getNext())
            {
                if (instruction.getOpcode() == Opcodes.GETFIELD)
                {
                    FieldInsnNode fin = (FieldInsnNode) instruction;
                    if (("yOffset".equals(fin.name) || "field_70129_M".equals(fin.name)) && "F".equals(fin.desc) && isTarget(fin)) {
                        methodPatched = true;
                        method.instructions.insertBefore(instruction, new InsnNode(Opcodes.DUP));
                        MethodInsnNode hook = new MethodInsnNode(Opcodes.INVOKESTATIC, "ganymedes01/etfuturum/asm/pose/HardcodedPlayerHeightHook", "adjustHeight", "(Lnet/minecraft/entity/Entity;F)F", false);
                        method.instructions.insert(instruction, hook);
                    }
                }
            }
            if (!methodPatched){
                Logger.warn("Failed to patch hardcoded height in " + transformedName +"."+ method.name + ": target bytecode not found.");
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

    private boolean isTarget(FieldInsnNode fin)
    {
        AbstractInsnNode curr = getNextValidNode(fin);
        if (curr == null || curr.getOpcode() != Opcodes.F2D) return false;
        curr = getNextValidNode(curr);
        if (curr == null || curr.getOpcode() != Opcodes.DSUB) return false;
        curr = fin.getPrevious();
        int limit = 10;
        while (curr != null && limit-- > 0)
        {
            if (hasTargetConstant(curr)) return true;
            curr = curr.getPrevious();
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
