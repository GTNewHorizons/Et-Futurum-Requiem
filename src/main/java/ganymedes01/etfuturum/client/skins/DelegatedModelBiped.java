package ganymedes01.etfuturum.client.skins;

import ganymedes01.etfuturum.client.model.ModelPlayer;
import net.minecraft.entity.Entity;

public abstract class DelegatedModelBiped {

    protected final ModelPlayer model;

    public DelegatedModelBiped(ModelPlayer model) {
        this.model = model;
    }

    public void preRender(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) { }
    public void postRender(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) { }

    public void preSetRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) { }
    public void postSetRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) { }

    public void preRenderCloak(float p_78111_1_) { }
    public void postRenderCloak(float p_78111_1_) { }

    public void preRenderEars(float p_78110_1_) { }
    public void postRenderEars(float p_78110_1_) { }
}
