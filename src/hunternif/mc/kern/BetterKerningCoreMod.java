package hunternif.mc.kern;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;

@MCVersion("1.6.4")
public class BetterKerningCoreMod implements IFMLLoadingPlugin {
	public static boolean runtimeDeobf = true;
	
	@Override
	@Deprecated
	public String[] getLibraryRequestClass() {
		return null;
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[]{"hunternif.mc.kern.FontTransformer"};
	}

	@Override
	public String getModContainerClass() {
		return "hunternif.mc.kern.BetterKerningModContainer";
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		runtimeDeobf = ((Boolean)data.get("runtimeDeobfuscationEnabled")).booleanValue();
	}

}
