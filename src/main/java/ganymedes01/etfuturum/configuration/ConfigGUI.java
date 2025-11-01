package ganymedes01.etfuturum.configuration;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import ganymedes01.etfuturum.Tags;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.launchwrapper.Launch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigGUI extends GuiConfig {

	public ConfigGUI(GuiScreen parent) {
		super(parent, getElements(), Tags.MOD_ID, Tags.MOD_ID, false, false, GuiConfig.getAbridgedConfigPath(Launch.minecraftHome + "config" + File.separator + Tags.MOD_ID));
	}

	@SuppressWarnings("rawtypes")
	private static List<IConfigElement> getElements() {
		List<IConfigElement> list = new ArrayList<IConfigElement>();
//      for (String category : ConfigBase.usedCategories)
//          list.add(new ConfigElement(ConfigBase.INSTANCE.cfg.getCategory(category)));
		return list;
	}
}