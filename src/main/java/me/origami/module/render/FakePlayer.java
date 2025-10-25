package me.origami.module.render;

import me.origami.impl.settings.Setting;
import me.origami.module.Module;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.text.Text;

public class FakePlayer extends Module {

    private OtherClientPlayerEntity fakePlayer;

    private final Setting<String> playerName = new Setting<>("Name", "OrigamiBot", "Имя фейк игрока");
    private final Setting<Boolean> copyInventory = new Setting<>("CopyInventory", true, "Копировать твой инвентарь");
    private final Setting<Boolean> copySkin = new Setting<>("CopySkin", true, "Использовать твой скин");

    public FakePlayer() {
        super("FakePlayer", "Создает локального неуязвимого фейкового игрока", Category.RENDER);
        getSettings().add(playerName);
        getSettings().add(copyInventory);
        getSettings().add(copySkin);
    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) return;
        if (fakePlayer != null) {
            mc.world.removeEntity(fakePlayer.getId(), Entity.RemovalReason.DISCARDED);
            fakePlayer = null;
        }

        fakePlayer = new OtherClientPlayerEntity(mc.world, mc.player.getGameProfile());
        Vec3d pos = mc.player.getPos();
        fakePlayer.copyPositionAndRotation(mc.player);
        fakePlayer.setCustomName(Text.literal(playerName.getValue()));
        fakePlayer.setCustomNameVisible(true);

        if (copyInventory.getValue()) {
            fakePlayer.getInventory().clone(mc.player.getInventory());
        }

        // делаем его полностью неуязвимым
        fakePlayer.setInvulnerable(true);

        // не взаимодействует с физикой
        fakePlayer.noClip = true;

        mc.world.addEntity(fakePlayer);
    }

    @Override
    public void onDisable() {
        if (mc.world == null) return;
        if (fakePlayer != null) {
            mc.world.removeEntity(fakePlayer.getId(), Entity.RemovalReason.DISCARDED);
            fakePlayer = null;
        }
    }

    @Override
    public void onTick() {
        if (fakePlayer != null && mc.player != null) {
            // фиксируем фейка на месте
            fakePlayer.setVelocity(0, 0, 0);
        }
    }
}