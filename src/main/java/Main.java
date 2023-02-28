import me.kambing.gardenia.Gardenia;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
@Mod(modid = Gardenia.MODID, version = Gardenia.VERSION)
public class Main {

    @Mod.EventHandler
    public void init(FMLInitializationEvent event){
        Gardenia.instance = new Gardenia();
    }
}
