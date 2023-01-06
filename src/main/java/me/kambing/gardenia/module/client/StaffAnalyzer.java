package me.kambing.gardenia.module.client;

import me.kambing.gardenia.module.Category;
import me.kambing.gardenia.module.Module;
import me.kambing.gardenia.settings.Setting;
import me.kambing.gardenia.utils.staffanalyzer.StaffAnalyzerUtil;

public class StaffAnalyzer extends Module {

    public StaffAnalyzer() {
        super("StaffAnalyzer", "scan for staff activities and notifies you about it", false, false, Category.Client);
        new Setting("Delay", this,180, 60, 500, true);
        new Setting("Notify0Ban",this, true);
        StaffAnalyzerUtil thread = new StaffAnalyzerUtil();
        thread.start();
    }
}

