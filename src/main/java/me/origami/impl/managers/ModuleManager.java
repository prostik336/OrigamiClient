package me.origami.impl.managers;

import me.origami.module.Module;
import me.origami.module.combat.AutoCrystal;
import me.origami.module.render.FakePlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModuleManager {
    public List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        load();
    }

    public void load() {
        // Add your modules here
        modules.add(new FakePlayer());
        modules.add(new AutoCrystal());
        // Add other modules...
    }

    public Module getModuleByName(String name) {
        for (Module module : this.modules) {
            if (module.getName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }

    public void enableModule(String name) {
        Module module = this.getModuleByName(name);
        if (module != null) {
            module.setEnabled(true);
            module.onEnable();
        }
    }

    public void disableModule(String name) {
        Module module = this.getModuleByName(name);
        if (module != null) {
            module.setEnabled(false);
            module.onDisable();
        }
    }

    public ArrayList<Module> getEnabledModules() {
        ArrayList<Module> enabledModules = new ArrayList<>();
        for (Module module : this.modules) {
            if (module.isEnabled()) {
                enabledModules.add(module);
            }
        }
        return enabledModules;
    }

    public ArrayList<Module> getModules() {
        return new ArrayList<>(modules);
    }

    public ArrayList<Module> getModulesByCategory(Module.Category category) {
        ArrayList<Module> modulesCategory = new ArrayList<>();
        for (Module module : this.modules) {
            if (module.getCategory() == category) {
                modulesCategory.add(module);
            }
        }
        return modulesCategory;
    }

    public List<Module.Category> getCategories() {
        return Arrays.asList(Module.Category.values());
    }

    public void onTick() {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onTick();
            }
        }
    }
}