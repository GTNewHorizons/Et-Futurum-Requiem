package ganymedes01.etfuturum.entities;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.items.ItemLingeringPotion;
import io.netty.buffer.ByteBuf;

public class EntityLingeringEffect extends Entity implements IEntityAdditionalSpawnData {

    private static final int TICKS_DATA_WATCHER = 10, WIDTH_DATA_WATCHER = 11, HEIGHT_DATA_WATCHER = 12;

    private EntityLivingBase thrower;
    private ItemStack stack;
    private final int MAX_TICKS = 30 * 20;
    private int colorOverride;

    public EntityLingeringEffect(World world) {
        super(world);

        yOffset = 0;
        setSize(1, 1);
    }

    public EntityLingeringEffect(World world, EntityLingeringPotion potion) {
        this(world, potion.getStack(), potion.getThrower());
        setPosition(potion.posX, potion.posY, potion.posZ);
    }

    public EntityLingeringEffect(World world, ItemStack stack, EntityLivingBase thrower) {
        this(world);
        this.stack = stack;
        this.thrower = thrower;
    }

    public void setColorOverride(int color) {
        colorOverride = color;
    }

    @Override
    public boolean canBePushed() {
        return true;
    }

    @Override
    public void applyEntityCollision(Entity e) {
        if (!(e instanceof EntityLivingBase entity)) return;
        List<PotionEffect> effects = ((ItemLingeringPotion) ModItems.LINGERING_POTION.get()).getEffects(stack);
        boolean addedEffect = false;

        for (PotionEffect effect : effects) {
            int effectID = effect.getPotionID();
            if (Potion.potionTypes[effectID].isInstant()) {
                Potion.potionTypes[effectID].affectEntity(thrower, entity, effect.getAmplifier(), 0.25);
                addedEffect = true;
            } else if (!entity.isPotionActive(effectID)) {
                entity.addPotionEffect(effect);
                addedEffect = true;
            }
        }

        if (addedEffect) {
            int ticks = dataWatcher.getWatchableObjectInt(TICKS_DATA_WATCHER);
            if (setTickCount(ticks + 5 * 20)) // Add 5 seconds to the expiration time (decreasing radius by 0.5 blocks)
            {}
        }
    }

    @Override
    protected void entityInit() {
        dataWatcher.addObject(TICKS_DATA_WATCHER, 0);
        dataWatcher.addObject(WIDTH_DATA_WATCHER, 6.0F);
        dataWatcher.addObject(HEIGHT_DATA_WATCHER, 0.5F);
    }

    @Override
    public void onUpdate() {
        int ticks = dataWatcher.getWatchableObjectInt(TICKS_DATA_WATCHER);

        if (worldObj.isRemote) {
            float w = dataWatcher.getWatchableObjectFloat(WIDTH_DATA_WATCHER);
            if (w != width) width = w;
            float h = dataWatcher.getWatchableObjectFloat(HEIGHT_DATA_WATCHER);
            if (h != height) height = h;

            if (ticksExisted % 5 == 0) {
                double radius = 3 * ((double) (MAX_TICKS - ticks) / MAX_TICKS);
                int colour = colorOverride > 0 ? colorOverride
                    : stack.getItem()
                        .getColorFromItemStack(stack, 0);
                float red = (colour >> 16 & 255) / 255.0F;
                float green = (colour >> 8 & 255) / 255.0F;
                float blue = (colour & 255) / 255.0F;
                for (int i = 0; i < 30; i++) {
                    float variation = 0.75F + rand.nextFloat() * 0.25F;
                    worldObj.spawnParticle(
                        "mobSpell",
                        posX - radius + rand.nextFloat() * radius * 2,
                        posY,
                        posZ - radius + rand.nextFloat() * radius * 2,
                        red * variation,
                        green * variation,
                        blue * variation);
                }
            }
            return;
        }

        setTickCount(++ticks);
    }

    private boolean setTickCount(int ticks) {
        dataWatcher.updateObject(TICKS_DATA_WATCHER, ticks);
        if (ticks >= MAX_TICKS) {
            setDead();
            return true;
        }
        double radius = 3 * ((double) (MAX_TICKS - ticks) / MAX_TICKS);
        setSize((float) radius * 2, 0.5F);
        return false;
    }

    @Override
    public void moveEntity(double x, double y, double z) {}

    @Override
    public void addVelocity(double x, double y, double z) {}

    @Override
    protected void setSize(float width, float height) {
        super.setSize(width, height);
        dataWatcher.updateObject(WIDTH_DATA_WATCHER, this.width);
        dataWatcher.updateObject(HEIGHT_DATA_WATCHER, this.height);
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        ByteBufUtils.writeItemStack(buffer, stack);
        buffer.writeInt(colorOverride);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        stack = ByteBufUtils.readItemStack(buffer);
        colorOverride = buffer.readInt();
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        stack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("Potion"));
        if (stack == null) {
            setDead();
        } else {
            setTickCount(nbt.getInteger("Ticks"));
            if (nbt.hasKey("colorOverride")) {
                colorOverride = nbt.getInteger("colorOverride");
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        if (stack != null) {
            nbt.setTag("Potion", stack.writeToNBT(new NBTTagCompound()));
            nbt.setInteger("Ticks", dataWatcher.getWatchableObjectInt(TICKS_DATA_WATCHER));
            if (colorOverride > 0) {
                nbt.setInteger("colorOverride", colorOverride);
            }
        }
    }

    public ItemStack getStack() {
        return stack;
    }

    public EntityLivingBase getThrower() {
        return thrower;
    }
}
