package me.kambing.gardenia.module;

import me.kambing.gardenia.Gardenia;
import me.kambing.gardenia.module.client.targethud.TargetHUD;
import me.kambing.gardenia.module.combat.*;
import me.kambing.gardenia.module.misc.*;
import me.kambing.gardenia.module.movement.*;
import me.kambing.gardenia.module.player.*;
import me.kambing.gardenia.module.client.*;
import me.kambing.gardenia.module.render.*;
import me.kambing.gardenia.module.render.deatheffects.DeathEffects;
import net.minecraft.network.Packet;

import java.util.ArrayList;

public class ModuleManager {
    public ArrayList<Module> modules;
    public ClickGUI clickGUI;
    public HUD hud;
    public StaffAnalyzer staffAnalyzer;
    public LegitAura legitAura;
    public Color color;
    public TwoDESP twoDESP;
    public DeathEffects deathEffects;
    public BedBreaker bedBreaker;

    public ModuleManager(){
        (modules = new ArrayList<Module>()).clear();
        this.modules.add(clickGUI = new ClickGUI());
        this.modules.add(hud = new HUD());
        this.modules.add(new Sprint());
        this.modules.add(new FastPlace());
        this.modules.add(new SelfDestruct());
        this.modules.add(new DelayRemover());
        this.modules.add(new FastBridge());
        this.modules.add(new BlockClutch());
        this.modules.add(new STap());
        this.modules.add(new Trajectories());
        this.modules.add(new PlayerESP());
        this.modules.add(new BedwarsOverlay());
        this.modules.add(new Radar());
        this.modules.add(new Watermark());
        this.modules.add(new AutoHeader());
        this.modules.add(new HoldClicker());
        this.modules.add(new AimAssist());
        this.modules.add(new Scaffold());
        this.modules.add(staffAnalyzer = new StaffAnalyzer());
        this.modules.add(twoDESP = new TwoDESP());
        this.modules.add(new Velocity());
        this.modules.add(legitAura = new LegitAura());
        this.modules.add(new TargetHUD());
        this.modules.add(color = new Color());
        this.modules.add(deathEffects = new DeathEffects());
        this.modules.add(bedBreaker = new BedBreaker());
        this.modules.add(new ScaffoldRewrite());
        //this.modules.add(new AutoClicker());
        for (Module m : this.modules) {
            Gardenia.eventBus.register(m);
        }
    }

    public Module getModule(String name) {
        for (Module m : this.modules) {
            if (m.getName().equalsIgnoreCase(name)) {
                return m;
            }
        }
        return null;
    }

    public ArrayList<Module> getModulesList() {
        return this.modules;
    }

    public ArrayList<Module> getModulesInCategory(Category c) {
        ArrayList<Module> mods = new ArrayList<Module>();
        for (Module m : this.modules) {
            if(m.getCategory() == c){
                mods.add(m);
            }
        }
        return mods;
    }

    public void receivePacketToAllModules(Packet<?> packet) {
        for (Module m : this.modules) {
            m.onPacketReceived(packet);
        }
    }

    public void addModule(Module m){
        this.modules.add(m);
    }
}
