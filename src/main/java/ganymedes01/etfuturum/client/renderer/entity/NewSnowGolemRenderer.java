package ganymedes01.etfuturum.client.renderer.entity;

import net.minecraft.client.renderer.entity.RenderSnowMan;
import net.minecraft.entity.monster.EntitySnowman;

import ganymedes01.etfuturum.entities.EntityNewSnowGolem;

public class NewSnowGolemRenderer extends RenderSnowMan {

    @Override
    protected void renderEquippedItems(EntitySnowman entity, float partialTickTime) {
        if (((EntityNewSnowGolem) entity).hasPumpkin()) super.renderEquippedItems(entity, partialTickTime);
    }
}
