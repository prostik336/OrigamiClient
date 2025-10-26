package me.origami.module.render;

import me.origami.module.Module;
import me.origami.impl.settings.Setting;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import com.mojang.authlib.GameProfile;

import java.util.UUID;

public class FakePlayer extends Module {
    // Settings like in Skeet
    public Setting<String> name = this.register(new Setting<>("Name", "FakePlayer", "Name of the fake player"));
    public Setting<Double> health = this.register(new Setting<>("Health", 20.0, "Health of the fake player", 1.0, 20.0, 1.0));

    private OtherClientPlayerEntity fakePlayer;

    public FakePlayer() {
        super("FakePlayer", "Spawns a fake player entity", Category.RENDER);

        // Всегда в конце - добавляем бинд
        finishRegistration();
    }

    @Override
    public void onEnable() {
        if (mc.world == null || mc.player == null) return;

        fakePlayer = new OtherClientPlayerEntity(mc.world, new GameProfile(UUID.randomUUID(), name.getValue()));
        fakePlayer.copyPositionAndRotation(mc.player);
        fakePlayer.setHealth(health.getValue().floatValue());
        mc.world.addEntity(fakePlayer); // Исправлено: только один аргумент
    }

    @Override
    public void onDisable() {
        if (fakePlayer != null && mc.world != null) {
            mc.world.removeEntity(fakePlayer.getId(), Entity.RemovalReason.DISCARDED);
            fakePlayer = null;
        }
    }

    @Override
    public void onTick() {
        if (fakePlayer == null || mc.world == null) {
            fakePlayer = null;
            setEnabled(false);
            return;
        }

        // Проверяем существует ли ентити в мире
        boolean entityExists = false;
        for (Entity entity : mc.world.getEntities()) {
            if (entity == fakePlayer) {
                entityExists = true;
                break;
            }
        }

        if (!entityExists) {
            fakePlayer = null;
            setEnabled(false);
            return;
        }

        fakePlayer.setHealth(health.getValue().floatValue());

        // Update name if changed
        if (!fakePlayer.getGameProfile().getName().equals(name.getValue())) {
            mc.world.removeEntity(fakePlayer.getId(), Entity.RemovalReason.DISCARDED);
            fakePlayer = new OtherClientPlayerEntity(mc.world, new GameProfile(UUID.randomUUID(), name.getValue()));
            fakePlayer.copyPositionAndRotation(mc.player);
            fakePlayer.setHealth(health.getValue().floatValue());
            mc.world.addEntity(fakePlayer); // Исправлено: только один аргумент
        }
    }
}