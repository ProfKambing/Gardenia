package me.kambing.gardenia;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import java.lang.instrument.Instrumentation;

public class Agent {
    public static boolean isAnInjection;
    public static void agentmain(String args, Instrumentation instrumentation) throws Exception {
        for (Class<?> classes : instrumentation.getAllLoadedClasses()) {
            if (classes.getName().startsWith("net.minecraft.client.Minecraft")) {
                isAnInjection = true;
                LaunchClassLoader classLoader = (LaunchClassLoader) classes.getClassLoader();
                classLoader.addURL(Agent.class.getProtectionDomain().getCodeSource().getLocation());
                Launch.classLoader.addURL(Agent.class.getResource("assets.minecraft.textures"));
                Launch.classLoader.addURL(Agent.class.getResource("mixins.gardenia.json"));
                Class<?> client = classLoader.loadClass(Gardenia.class.getName());
                client.newInstance();
                MixinBootstrap.init();
                Mixins.addConfiguration("mixins.gardenia.json");
            }
        }
    }
}
