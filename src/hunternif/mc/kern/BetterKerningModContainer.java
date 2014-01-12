package hunternif.mc.kern;

import java.util.Arrays;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.ModMetadata;

public class BetterKerningModContainer extends DummyModContainer {
	public BetterKerningModContainer() {
		super(new ModMetadata());
		ModMetadata meta = super.getMetadata();
		meta.authorList = Arrays.asList(new String[]{"Hunternif"});
		meta.modId = "betterkerning";
		meta.name = "Better Kerning";
		meta.version = "1.0";
		meta.description = "Small changes to FontRenderer making the small 'Unicode' font have nicer kerning.";
	}
}
