package hunternif.mc.kern;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class BetterKerningCoreMod implements IFMLLoadingPlugin {

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
	public void injectData(Map<String, Object> data) {}

}
