package tf.ssf.sfort.noplayerlabels;

import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.Level;
import tf.ssf.sfort.ini.SFIni;
import tf.ssf.sfort.noplayerlabels.mixin.Config;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigLegacy {
	public static void loadLegacy(SFIni inIni) {
		Map<String, String> oldConf = new HashMap<>();
		File confFile = new File(
				FabricLoader.getInstance().getConfigDir().toString(),
				"PlayerLabels.conf"
		);
		if (!confFile.exists()) return;
		try {
			List<String> la = Files.readAllLines(confFile.toPath());
			String[] ls = la.toArray(new String[Math.max(la.size(), 6)|1]);

			try{
				oldConf.put("visibleDistance", Double.toString(Double.parseDouble(ls[0])));
			}catch (Exception ignore){}

			try{
				oldConf.put("hideBehindWall", ls[2].contains("on") ? "on" : ls[2].contains("invert") ? "invert" : "off");
			}catch (Exception ignore){}

			try{
				oldConf.put("showTeamMembers", ls[2].contains("on") ? "on" : ls[2].contains("invert") ? "invert" : "off");
            }catch (Exception ignore){}

			for (Map.Entry<String, String> entry : oldConf.entrySet()) {
				SFIni.Data data = inIni.getLastData(entry.getKey());
				if (data != null) {
					data.val = entry.getValue();
				}
			}

			Files.delete(confFile.toPath());
			Config.LOGGER.log(Level.INFO, Config.mod+" successfully loaded legacy .conf file");
		} catch(Exception e) {
			Config.LOGGER.log(Level.ERROR, Config.mod+" failed to load legacy .conf file", e);
		}
	}
}
