package me.origami.impl.managers;

import me.origami.module.Module;
import me.origami.module.combat.AutoCrystal;
import me.origami.module.render.FakePlayer;

import java.util.*;
import java.util.stream.Collectors;

public class ModuleManager {
    private final Map<String, Module> modules = new HashMap<>();
    private final List<Module> moduleList = new ArrayList<>();
    private static ModuleManager instance;

    public ModuleManager() {
        instance = this;
        load();
    }

    public static ModuleManager getInstance() {
        return instance;
    }

    public static List<Module> getModulesStatic() {
        return instance != null ? instance.getModules() : new ArrayList<>();
    }

    private void load() {
        Module[] initialModules = { new FakePlayer(), new AutoCrystal() };
        for (Module module : initialModules) {
            modules.put(module.getName().toLowerCase(), module);
            moduleList.add(module);
        }
    }

    public Module getModuleByName(String name) {
        return modules.get(name.toLowerCase());
    }

    public void enableModule(String name) {
        Module module = getModuleByName(name);
        if (module != null && !module.isEnabled()) {
            module.setEnabled(true);
            module.onEnable();
        }
    }

    public void disableModule(String name) {
        Module module = getModuleByName(name);
        if (module != null && module.isEnabled()) {
            module.setEnabled(false);
            module.onDisable();
        }
    }

    public List<Module> getEnabledModules() {
        return moduleList.stream().filter(Module::isEnabled).collect(Collectors.toList());
    }

    public List<Module> getModules() {
        return new ArrayList<>(moduleList);
    }

    public List<Module> getModulesByCategory(Module.Category category) {
        return moduleList.stream().filter(m -> m.getCategory() == category).collect(Collectors.toList());
    }

    public List<Module.Category> getCategories() {
        return Arrays.asList(Module.Category.values());
    }

    public void onTick() {
        moduleList.forEach(module -> {
            if (module.isEnabled()) module.onTick();
        });
    }
}