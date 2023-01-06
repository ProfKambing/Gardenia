package me.kambing.gardenia.module.misc;

import me.kambing.gardenia.Gardenia;
import me.kambing.gardenia.module.Category;
import me.kambing.gardenia.module.Module;
import org.lwjgl.input.Keyboard;

public class SelfDestruct extends Module {
    public SelfDestruct() {
        super("SelfDestruct", "Mc stays here, client goes back to mumbai. Babai", false, false, Category.Misc);
        this.setKey(Keyboard.KEY_BACK);
    }

    @Override
    public void onEnabled() {
        Gardenia.instance.onDestruct();
    }
}
