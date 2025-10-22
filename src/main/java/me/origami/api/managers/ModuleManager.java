package me.origami.api.managers;

import me.origami.api.module.Module;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private static final List<Module> modules = new ArrayList<>();

    public static void init() {
        // Здесь можно вручную добавить модули при старте, если нужно
        // Например: modules.add(new BetterChat());
        // Но лучше полагаться на регистрацию из конструкторов модулей
    }

    public static List<Module> getModules() {
        return modules;
    }

    public static void addModule(Module module) {
        if (!modules.contains(module)) {
            modules.add(module);
        }
    }

    public static void removeModule(Module module) {
        modules.remove(module);
    }

    public static Module getModuleByName(String name) {
        for (Module module : modules) {
            if (module.getName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }
}