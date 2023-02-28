package me.kambing.gardenia.config;

import me.kambing.gardenia.module.Module;
import me.kambing.gardenia.settings.Setting;
import me.kambing.gardenia.Gardenia;
import me.kambing.gardenia.utils.StatsAnalyzerUtil;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class SaveLoad {
    private final File dir;
    private File dataFile;
    private final String extension;
    private String fileName;

    public SaveLoad() {
        dir = new File(Minecraft.getMinecraft().mcDataDir, Gardenia.MODID.toLowerCase());
        if (!dir.exists()) {
            dir.mkdir();
        }
        this.fileName = "current";
        this.extension = "gardenia";
        dataFile = new File(dir, fileName + "." + extension);
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.load();
        loadHypixelApiKey();
    }

    public void save() {
        if (Gardenia.instance.destructed) {
            return;
        }
        ArrayList<String> toSave = new ArrayList<String>();

        for (Module mod : Gardenia.instance.moduleManager.getModulesList()) {
            toSave.add(Gardenia.MODID + "MOD:" + mod.getName() + ":" + mod.isToggled() + ":" + mod.visible + ":" + mod.getKey());
        }

        for (Setting set : Gardenia.instance.settingsManager.getSettings()) {
            if (set.isCheck()) {
                toSave.add(Gardenia.MODID + "SET:" + set.getName() + ":" + set.getParentMod().getName() + ":" + set.getValBoolean());
            }
            if (set.isCombo()) {
                toSave.add(Gardenia.MODID + "SET:" + set.getName() + ":" + set.getParentMod().getName() + ":" + set.getValString());
            }
            if (set.isSlider()) {
                toSave.add(Gardenia.MODID + "SET:" + set.getName() + ":" + set.getParentMod().getName() + ":" + set.getValDouble());
            }
            if (set.isColor()) {
                toSave.add(Gardenia.MODID + "SET:" + set.getName() + ":" + set.getParentMod().getName() + ":" + set.getColor().getRGB());
            }
        }

        try {
            PrintWriter printWriter = new PrintWriter(this.dataFile);
            for (String str : toSave) {
                printWriter.println(str);
            }
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void save(String configname) {
        if (Gardenia.instance.destructed) {
            return;
        }
        ArrayList<String> toSave = new ArrayList<String>();

        for (Module mod : Gardenia.instance.moduleManager.getModulesList()) {
            toSave.add(Gardenia.MODID + "MOD:" + mod.getName() + ":" + mod.isToggled() + ":" + mod.visible + ":" + mod.getKey());
        }

        for (Setting set : Gardenia.instance.settingsManager.getSettings()) {
            if (set.isCheck()) {
                toSave.add(Gardenia.MODID + "SET:" + set.getName() + ":" + set.getParentMod().getName() + ":" + set.getValBoolean());
            }
            if (set.isCombo()) {
                toSave.add(Gardenia.MODID + "SET:" + set.getName() + ":" + set.getParentMod().getName() + ":" + set.getValString());
            }
            if (set.isSlider()) {
                toSave.add(Gardenia.MODID + "SET:" + set.getName() + ":" + set.getParentMod().getName() + ":" + set.getValDouble());
            }
        }

        try {
            PrintWriter printWriter = new PrintWriter(this.dataFile);
            for (String str : toSave) {
                printWriter.println(str);
            }
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        if (Gardenia.instance.destructed) {
            return;
        }

        ArrayList<String> lines = new ArrayList<String>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.dataFile));
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (lines.size() == 0) {
            return;
        }

        for (String s : lines) {
            String[] args = s.split(":");
            if (s.toLowerCase().startsWith(Gardenia.MODID.toLowerCase() + "mod:")) {

                Module m = Gardenia.instance.moduleManager.getModule(args[1]);
                if (m != null) {
                    m.setToggled(Boolean.parseBoolean(args[2]));
                    m.visible = Boolean.parseBoolean(args[3]);
                    m.setKey(Integer.parseInt(args[4]));
                } else {System.out.println("m is null fuck line 82");}
            } else if (s.toLowerCase().startsWith(Gardenia.MODID.toLowerCase() + "set:")) {
                Module m = Gardenia.instance.moduleManager.getModule(args[2]);
                if (m != null) {
                    Setting set = Gardenia.instance.settingsManager.getSettingByName(m, args[1]);
                    if (set != null) {
                        if (set.isCheck()) {
                            set.setValBoolean(Boolean.parseBoolean(args[3]));
                        }
                        if (set.isCombo()) {
                            set.setValString(args[3]);
                        }
                        if (set.isSlider()) {
                            set.setValDouble(Double.parseDouble(args[3]));
                        }
                        if (set.isColor()) {
                            set.setColor(new Color((int)Double.parseDouble(args[3]), true));
                        }
                    } else {System.out.println("s is null fuck line 91");}
                }else {System.out.println("m is null fuck line 89");}
            } else {
                System.out.println("idfk how to read");
            }
        }
    }

    public String getExtension() {
        return extension;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        this.dataFile = new File(dir, this.fileName + this.extension);
        if (!this.dataFile.exists()) {
            try {
                this.dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<String> getConfigs() {
        File[] files = new File(Gardenia.MODID.toLowerCase() + "/Configs/").listFiles();
        ArrayList<String> results = new ArrayList<String>();

        for (File file : files) {
            if (file.isFile()) {
                results.add(file.getName());
            }
        }
        return results;
    }

    /*
    public void saveDefault() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/resources/assets/configs/default.galaxy")));
        ArrayList<String> lines = new ArrayList<String>();
        // gettting the default cfg
        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (line != null) {
            lines.add(line);
            try {
                line = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            PrintWriter printWriter = new PrintWriter(this.dataFile);
            for (String str : lines) {
                printWriter.println(str);
            }
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }*/
    public void saveHypixelApiKey() {
        try {
            File file = new File("gardenia/hypixelapikey.txt");
            try {
                if (!file.exists())
                    file.createNewFile();
            } catch (Exception ignored) {
            }
            file.getParentFile().mkdirs();
            PrintWriter writer = new PrintWriter(new FileWriter(file, true));
            writer.print("");
            writer.println("hypixelapikey-" + StatsAnalyzerUtil.hypixelApiKey);
            writer.close();
        } catch (Exception ignored) {
        }

    }
    private File getHypixelApiKeyFile() {
        File file = new File("gardenia/hypixelapikey.txt");
        try {
            if (!file.exists())
                file.createNewFile();
        } catch (Exception ignored) {
        }
        return file;
    }
    public void loadHypixelApiKey() {
        List<String> strings = null;
        try {
            strings = Files.readAllLines(getHypixelApiKeyFile().toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            for (String s : strings)
                if (s.startsWith("hypixelapikey-")) {
                    StatsAnalyzerUtil.hypixelApiKey = s.replace("hypixelapikey-", "");
                    Gardenia.getExecutor().execute(() -> {
                        if (!StatsAnalyzerUtil.isHypixelKeyValid(StatsAnalyzerUtil.hypixelApiKey)) {
                            StatsAnalyzerUtil.hypixelApiKey = "";
                        }
                    });
                }
        } catch (NullPointerException e) {

        }
    }
}
