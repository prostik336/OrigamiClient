package me.origami.module.combat;

import me.origami.module.Module;
import me.origami.systems.rotate.RotateHandler;
import me.origami.systems.placing.PlaceHandler;
import me.origami.systems.breaking.BreakHandler;
import me.origami.systems.swapping.SwapHandler;
import me.origami.systems.rendering.RenderHandler;
import me.origami.systems.SubModule;

import java.util.ArrayList;
import java.util.List;

public class AutoCrystal extends Module {
    private final RotateHandler rotate;
    private final PlaceHandler place;
    private final BreakHandler breakHandler; // переименовал чтобы избежать конфликта с ключевым словом break
    private final SwapHandler swap;
    private final RenderHandler render;

    private final List<SubModule> subModules;

    public AutoCrystal() {
        super("AutoCrystal", "Modular auto crystal system", Category.COMBAT);

        // Используем готовые системы из папки systems
        this.rotate = new RotateHandler("Rotate");
        this.place = new PlaceHandler("Place");
        this.breakHandler = new BreakHandler("Break"); // переименовано
        this.swap = new SwapHandler("Swap");
        this.render = new RenderHandler("Render");

        this.subModules = new ArrayList<>();
        subModules.add(rotate);
        subModules.add(place);
        subModules.add(breakHandler); // переименовано
        subModules.add(swap);
        subModules.add(render);

        // Добавляем настройки всех подсистем в основной модуль
        for (SubModule sub : subModules) {
            getSettings().addAll(sub.getSettings());
        }
    }

    @Override
    public void onEnable() {
        for (SubModule sub : subModules) {
            if (sub.isEnabled()) sub.onEnable();
        }
    }

    @Override
    public void onDisable() {
        for (SubModule sub : subModules) {
            sub.onDisable();
        }
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;

        // Вызываем логику каждой включенной подсистемы
        for (SubModule sub : subModules) {
            if (sub.isEnabled()) sub.onTick();
        }

        // Основная логика AutoCrystal
        performAutoCrystal();
    }

    private void performAutoCrystal() {
        if (mc.player == null || mc.world == null) return;

        // Здесь будет основная логика AutoCrystal
        // которая использует включенные подсистемы

        // Пример:
        if (place.isEnabled()) {
            // Логика размещения кристаллов
        }

        if (breakHandler.isEnabled()) { // исправлено
            // Логика ломания кристаллов
        }

        if (rotate.isEnabled()) {
            // Логика поворота
        }

        if (swap.isEnabled()) {
            // Логика свапа
        }

        if (render.isEnabled()) {
            // Логика рендера
        }
    }

    public List<SubModule> getSubModules() {
        return subModules;
    }

    // Геттеры для доступа к конкретным обработчикам
    public RotateHandler getRotate() { return rotate; }
    public PlaceHandler getPlace() { return place; }
    public BreakHandler getBreak() { return breakHandler; } // исправлено
    public SwapHandler getSwap() { return swap; }
    public RenderHandler getRender() { return render; }
}