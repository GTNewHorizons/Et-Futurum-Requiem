package ganymedes01.etfuturum.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelCushion extends ModelBase {

	private final ModelRenderer base;

	public ModelCushion() {
		textureWidth = 64;
		textureHeight = 64;
		base = new ModelRenderer(this, 0, 0);
		base.addBox(-8.0F, 0.0F, -8.0F, 16, 4, 16, -0.005F);
	}

	public void render() {
		base.render(0.0625F);
	}
	
}
