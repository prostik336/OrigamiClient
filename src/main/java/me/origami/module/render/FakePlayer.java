package me.origami.module.render;

import me.origami.impl.settings.Setting;
import me.origami.module.Module;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;

public class FakePlayer extends Module {

    private OtherClientPlayerEntity fakePlayer;
    private final Setting<String> playerName;

    public FakePlayer() {
        super("FakePlayer", "Создает локального неуязвимого фейкового игрока", Category.RENDER);
        this.playerName = new Setting<>("Name", "OrigamiBot", "Имя фейк игрока");
        getSettings().add(playerName);
    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) return;

        // Удаляем старого фейк-игрока если он есть
        removeFakePlayer();

        try {
            // Создаем нового фейк-игрока
            fakePlayer = new OtherClientPlayerEntity(mc.world, mc.player.getGameProfile());

            // Копируем начальную позицию (но не обновляем её постоянно)
            fakePlayer.copyPositionAndRotation(mc.player);
            fakePlayer.setHeadYaw(mc.player.getHeadYaw());
            fakePlayer.setBodyYaw(mc.player.getBodyYaw());

            // Устанавливаем имя из настройки
            fakePlayer.setCustomName(Text.literal(playerName.getValue()));
            fakePlayer.setCustomNameVisible(true);

            // Копируем инвентарь
            fakePlayer.getInventory().clone(mc.player.getInventory());

            // Делаем его неуязвимым и неподвижным
            fakePlayer.setInvulnerable(true);
            fakePlayer.noClip = true;
            fakePlayer.setNoGravity(true);
            fakePlayer.setHealth(20.0f);

            // Устанавливаем фиксированную позицию (не обновляем её)
            fakePlayer.setPos(mc.player.getX(), mc.player.getY(), mc.player.getZ());

            // Добавляем в мир
            mc.world.addEntity(fakePlayer);

            System.out.println("FakePlayer создан: " + playerName.getValue());

        } catch (Exception e) {
            System.err.println("Ошибка при создании FakePlayer: " + e.getMessage());
            e.printStackTrace();
            fakePlayer = null;
            this.setEnabled(false);
        }
    }

    @Override
    public void onDisable() {
        removeFakePlayer();
    }

    private void removeFakePlayer() {
        if (fakePlayer != null && mc.world != null) {
            try {
                fakePlayer.discard();
                System.out.println("FakePlayer удален");
            } catch (Exception e) {
                System.err.println("Ошибка при удалении FakePlayer: " + e.getMessage());
            } finally {
                fakePlayer = null;
            }
        }
    }

    @Override
    public void onTick() {
        // НЕ обновляем позицию фейк-игрока - он должен оставаться на месте
        if (fakePlayer != null) {
            // Только обновляем анимации, но не позицию
            fakePlayer.setBodyYaw(fakePlayer.getBodyYaw() + 1); // Легкое движение для "оживления"
            fakePlayer.setVelocity(0, 0, 0); // Гарантируем, что не двигается
        }
    }

    public OtherClientPlayerEntity getFakePlayer() {
        return fakePlayer;
    }
}