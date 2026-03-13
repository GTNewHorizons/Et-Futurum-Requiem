package ganymedes01.etfuturum.mixinplugin;

import static ganymedes01.etfuturum.lib.Reference.MOD_ID;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

import ganymedes01.etfuturum.configuration.configs.ConfigMixins;

@LateMixin
public class EtFuturumLateMixins implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins." + MOD_ID + ".late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        List<String> mixins = new ArrayList<>();

        if (ConfigMixins.enableSpectatorMode) {
            if (loadedMods.contains("IronChest")) {
                mixins.add("spectator.MixinContainerIronChest");
            }
            if (loadedMods.contains("appliedenergistics2")) {
                mixins.add("spectator.MixinPacketInventoryAction");
            }
            if (loadedMods.contains("TConstruct")) {
                mixins.add("spectator.MixinArmorProxyClientTConstruct");
            }
        }

        return mixins;
    }
}
