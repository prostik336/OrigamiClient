package me.origami.impl.managers;

import me.origami.module.misc.BetterChat;
import me.origami.module.Module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModuleManager {
    public List<me.origami.module.Module> modules = new ArrayList<>();

    public ModuleManager() {
        load();
    }

    public void load() {
        // Только существующие модули
        modules.add(new BetterChat());
    }

    public me.origami.module.Module getModuleByName(String name) {
        for (me.origami.module.Module module : this.modules) {
            if (module.getName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }

    public void enableModule(String name) {
        me.origami.module.Module module = this.getModuleByName(name);
        if (module != null) {
            module.setEnabled(true);
            module.onEnable();
        }
    }

    public void disableModule(String name) {
        me.origami.module.Module module = this.getModuleByName(name);
        if (module != null) {
            module.setEnabled(false);
            module.onDisable();
        }
    }

    public ArrayList<me.origami.module.Module> getEnabledModules() {
        ArrayList<me.origami.module.Module> enabledModules = new ArrayList<>();
        for (me.origami.module.Module module : this.modules) {
            if (module.isEnabled()) {
                enabledModules.add(module);
            }
        }
        return enabledModules;
    }

    public ArrayList<me.origami.module.Module> getModules() {
        return new ArrayList<>(modules);
    }

    public ArrayList<me.origami.module.Module> getModulesByCategory(me.origami.module.Module.Category category) {
        ArrayList<me.origami.module.Module> modulesCategory = new ArrayList<>();
        for (me.origami.module.Module module : this.modules) {
            if (module.getCategory() == category) {
                modulesCategory.add(module);
            }
        }
        return modulesCategory;
    }

    public List<me.origami.module.Module.Category> getCategories() {
        return Arrays.asList(me.origami.module.Module.Category.values());
    }

    public void onTick() {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onTick();
            }
        }
    }
}