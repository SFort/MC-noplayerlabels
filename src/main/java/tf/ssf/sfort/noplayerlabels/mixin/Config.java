package tf.ssf.sfort.noplayerlabels.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import tf.ssf.sfort.ini.SFIni;
import tf.ssf.sfort.noplayerlabels.ConfigLegacy;
import tf.ssf.sfort.noplayerlabels.EnumOnInvOff;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class Config implements IMixinConfigPlugin {
	public static final String mod = "tf.ssf.sfort.noplayerlabels";
	public static Logger LOGGER = LogManager.getLogger();
	public static double distance = 0.0;
	public static boolean wall = false;
	public static boolean team = false;
	public static EnumOnInvOff wallEnum = EnumOnInvOff.OFF;
	public static EnumOnInvOff teamEnum = EnumOnInvOff.OFF;
	@Override
	public void onLoad(String mixinPackage) {
		SFIni defIni = new SFIni();
		defIni.load(String.join("\n", new String[]{
				"; Show name tag when nearby. [0.0] 0.0+",
				";  Making the value negative will make it visible when far away",
				"visibleDistance=0.0",
				"; Hide name tag when behind walls. [off] on | invert | off",
				"hideBehindWall=off",
				"; Show name tag when on the same team. [off] on | invert | off",
				"showTeamMembers=off",
		}));
		ConfigLegacy.loadLegacy(defIni);

		File confFile = new File(
				FabricLoader.getInstance().getConfigDir().toString(),
				"PlayerLabels.sf.ini"
		);
		if (!confFile.exists()) {
			try {
				Files.write(confFile.toPath(), defIni.toString().getBytes());
				LOGGER.log(Level.INFO,mod+" successfully created config file");
				loadIni(defIni);
			} catch (IOException e) {
				LOGGER.log(Level.ERROR,mod+" failed to create config file, using defaults", e);
			}
			return;
		}
		try {
			SFIni ini = new SFIni();
			String text = Files.readString(confFile.toPath());
			int hash = text.hashCode();
			ini.load(text);
			for (Map.Entry<String, List<SFIni.Data>> entry : defIni.data.entrySet()) {
				List<SFIni.Data> list = ini.data.get(entry.getKey());
				if (list == null || list.isEmpty()) {
					ini.data.put(entry.getKey(), entry.getValue());
				} else {
					list.get(0).comments = entry.getValue().get(0).comments;
				}
			}
			loadIni(ini);
			String iniStr = ini.toString();
			if (hash != iniStr.hashCode()) {
				Files.write(confFile.toPath(), iniStr.getBytes());
			}
		} catch (IOException e) {
			LOGGER.log(Level.ERROR,mod+" failed to load config file, using defaults", e);
		}
	}
	public void loadIni(SFIni ini) {
		try{
			distance = ini.getDouble("visibleDistance");
		} catch (Exception e){
			SFIni.Data data = ini.getLastData("visibleDistance");
			if (data != null) data.val = Double.toString(distance);
			LOGGER.log(Level.ERROR,mod+" failed to load visibleDistance, setting to default value", e);
		}

		try{
			wallEnum = ini.getEnum("hideBehindWall", EnumOnInvOff.class);
			wall = wallEnum == EnumOnInvOff.ON;
		}catch (Exception e){
			SFIni.Data data = ini.getLastData("hideBehindWall");
			if (data != null) data.val = wallEnum.name().toLowerCase(Locale.ROOT);
			LOGGER.log(Level.ERROR,mod+" failed to load hideBehindWall, setting to default value", e);
		}

		try{
			teamEnum = ini.getEnum("showTeamMembers", EnumOnInvOff.class);
			team = teamEnum == EnumOnInvOff.ON;
		}catch (Exception e){
			SFIni.Data data = ini.getLastData("showTeamMembers");
			if (data != null) data.val = teamEnum.name().toLowerCase(Locale.ROOT);
			LOGGER.log(Level.ERROR,mod+" failed to load showTeamMembers, setting to default value", e);
		}
        LOGGER.log(Level.INFO,mod+" finished loaded config file");
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		if(wallEnum==EnumOnInvOff.OFF && teamEnum==EnumOnInvOff.OFF && distance == 0.0){
			return mixinClassName.equals(mod+".mixin.AlwaysOff");
		}
		switch (mixinClassName){
			case mod+".mixin.Distance": return distance != 0.0;
			case mod+".mixin.Wall": return wallEnum != EnumOnInvOff.OFF;
			case mod+".mixin.Team": return teamEnum != EnumOnInvOff.OFF;
			default: return false;
		}
	}
	@Override public String getRefMapperConfig() { return null; }
	@Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) { }
	@Override public List<String> getMixins() { return null; }
	@Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
	@Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
}
