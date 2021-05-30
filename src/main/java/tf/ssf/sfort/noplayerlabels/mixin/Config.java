package tf.ssf.sfort.noplayerlabels.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import javax.swing.event.DocumentEvent;
import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Config implements IMixinConfigPlugin {
    public static final String mod = "tf.ssf.sfort.noplayerlabels";
    public static Logger LOGGER = LogManager.getLogger();
    public static double distance = 0.0;
    public static Boolean wall = null;
    public static Boolean team = null;
    public static boolean sneak = false;
    @Override
    public void onLoad(String mixinPackage) {
        // Configs
        File confFile = new File(
                FabricLoader.getInstance().getConfigDir().toString(),
                "PlayerLabels.conf"
        );
        try {
            confFile.createNewFile();
            List<String> la = Files.readAllLines(confFile.toPath());
            List<String> defaultDesc = Arrays.asList(
                    "^-Show when nearby  [0.0] 0.0 - ...",
                    "^-Hide when behind Walls [off] on | invert | off",
                    "^-Show when on the same team [off] on | invert | off"
            );
            String[] ls = la.toArray(new String[Math.max(la.size(), defaultDesc.size() * 2)|1]);
            int hash = Arrays.hashCode(ls);
            for (int i = 0; i<defaultDesc.size();++i)
                ls[i*2+1]= defaultDesc.get(i);

            try{
                distance = Double.parseDouble(ls[0]);}catch (Exception ignore){}
            ls[0] = String.valueOf(distance);

            try{
                boolean bl1=ls[2].contains("on");
                wall = bl1||ls[2].contains("invert")? bl1:null;
            }catch (Exception ignore){}
            ls[2]= wall==null?"off": wall?"on":"invert";
            try{
                boolean bl1=ls[4].contains("on");
                team = bl1||ls[4].contains("invert")? bl1:null;
            }catch (Exception ignore){}
            ls[4]= team==null?"off": team?"on":"invert";
            
            if(hash != Arrays.hashCode(ls))
                Files.write(confFile.toPath(), Arrays.asList(ls));
            LOGGER.log(Level.INFO,mod+" successfully loaded config file");
        } catch(Exception e) {
            LOGGER.log(Level.ERROR,mod+" failed to load config file, using defaults\n"+e);
        }
    }



    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if(wall==null && distance == 0.0 && mixinClassName.equals(mod+".mixin.AlwaysOff")){
            return true;
        }
        switch (mixinClassName){
            case mod+".mixin.Distance":{return distance != 0.0;}
            case mod+".mixin.Wall":{return wall != null;}
            case mod+".mixin.Team":{return team != null;}
            default:{return false;}
        }
    }
    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) { }
    @Override public List<String> getMixins() { return null; }
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
}
