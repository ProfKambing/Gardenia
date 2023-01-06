package me.kambing.gardenia.module;

import me.kambing.gardenia.module.combat.*;
import me.kambing.gardenia.module.misc.*;
import me.kambing.gardenia.module.movement.*;
import me.kambing.gardenia.module.player.*;
import me.kambing.gardenia.module.client.*;
import me.kambing.gardenia.module.render.*;

import java.util.ArrayList;

public class ModuleManager {
    public ArrayList<Module> modules;
    public ClickGUI clickGUI;
    public HUD hud;
    public StaffAnalyzer staffAnalyzer;

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
        this.modules.add(staffAnalyzer = new StaffAnalyzer());
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

    public void addModule(Module m){
        this.modules.add(m);
    }
}
