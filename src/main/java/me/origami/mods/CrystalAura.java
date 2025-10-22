package me.origami.mods;

import me.origami.api.module.Module;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.lang.reflect.Field;
import java.util.List;

public class CrystalAura extends Module {
    private double range = 6.0;
    private double placeRange = 5.0;
    private double breakRange = 5.0;
    private double minDamage = 6.0;
    private double maxSelfDamage = 8.0;
    private int placeDelay = 2;
    private int breakDelay = 2;

    private int placeTimer = 0;
    private int breakTimer = 0;

    public CrystalAura() {
        super("CrystalAura", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        placeTimer = 0;
        breakTimer = 0;
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

      
        if (placeTimer > 0) placeTimer--;
        if (breakTimer > 0) breakTimer--;

    
        PlayerEntity target = findTarget();
        if (target == null) return;

      
        if (breakTimer <= 0) {
            EndCrystalEntity crystal = findCrystalToBreak(target);
            if (crystal != null) {
                breakCrystal(crystal);
                breakTimer = breakDelay;
                return;
            }
        }

     
        if (placeTimer <= 0) {
            BlockPos placePos = findPlacePos(target);
            if (placePos != null) {
                placeCrystal(placePos);
                placeTimer = placeDelay;
            }
        }
    }

    private PlayerEntity findTarget() {
        List<PlayerEntity> players = mc.world.getEntitiesByClass(PlayerEntity.class,
                new Box(mc.player.getBlockPos()).expand(range),
                player -> player != mc.player && player.isAlive()
        );

        PlayerEntity closest = null;
        double closestDist = range + 1;

        for (PlayerEntity player : players) {
            double dist = mc.player.squaredDistanceTo(player);
            if (dist < closestDist) {
                closest = player;
                closestDist = dist;
            }
        }

        return closest;
    }

    private EndCrystalEntity findCrystalToBreak(PlayerEntity target) {
        List<EndCrystalEntity> crystals = mc.world.getEntitiesByClass(EndCrystalEntity.class,
                new Box(mc.player.getBlockPos()).expand(breakRange),
                crystal -> crystal.isAlive()
        );

        for (EndCrystalEntity crystal : crystals) {
            // Проверяем, нанесет ли кристалл урон цели
            double targetDamage = calculateDamage(crystal.getPos(), target.getPos());
            double selfDamage = calculateDamage(crystal.getPos(), mc.player.getPos());

            if (targetDamage >= minDamage && selfDamage <= maxSelfDamage) {
                return crystal;
            }
        }

        return null;
    }

    private BlockPos findPlacePos(PlayerEntity target) {
        BlockPos playerPos = mc.player.getBlockPos();
        BlockPos targetPos = target.getBlockPos();

        // Ищем возможные позиции для установки кристаллов вокруг цели
        for (int x = -2; x <= 2; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos checkPos = targetPos.add(x, y, z);

                    if (isValidPlacePos(checkPos) &&
                            mc.player.squaredDistanceTo(checkPos.toCenterPos()) <= placeRange * placeRange) {

                        // Проверяем урон
                        double targetDamage = calculateDamage(checkPos.toCenterPos(), target.getPos());
                        double selfDamage = calculateDamage(checkPos.toCenterPos(), mc.player.getPos());

                        if (targetDamage >= minDamage && selfDamage <= maxSelfDamage) {
                            return checkPos;
                        }
                    }
                }
            }
        }

        return null;
    }

    private boolean isValidPlacePos(BlockPos pos) {
        // Проверяем, можно ли поставить кристалл на этот блок
        if (!mc.world.getBlockState(pos).isAir()) return false;

        // Кристалл должен стоять на обсидиане или bedrock
        BlockPos below = pos.down();
        Block block = mc.world.getBlockState(below).getBlock();

        return block == Blocks.OBSIDIAN || block == Blocks.BEDROCK ||
                block == Blocks.RESPAWN_ANCHOR || block == Blocks.ANVIL;
    }

    private double calculateDamage(Vec3d crystalPos, Vec3d playerPos) {
        // Упрощенный расчет урона от кристалла
        double distance = crystalPos.distanceTo(playerPos);
        double damage = (12.0 * (1.0 - distance / 6.0)) * 2.0;
        return Math.max(0, damage);
    }

    private void breakCrystal(EndCrystalEntity crystal) {
        // Атакуем кристалл
        mc.interactionManager.attackEntity(mc.player, crystal);
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    private void setSelectedSlot(int slot) {
        try {
            Field selectedSlotField = mc.player.getInventory().getClass().getDeclaredField("selectedSlot");
            selectedSlotField.setAccessible(true);
            selectedSlotField.set(mc.player.getInventory(), slot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getSelectedSlot() {
        try {
            Field selectedSlotField = mc.player.getInventory().getClass().getDeclaredField("selectedSlot");
            selectedSlotField.setAccessible(true);
            return selectedSlotField.getInt(mc.player.getInventory());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void placeCrystal(BlockPos pos) {
        // Ищем слот с кристаллами
        int crystalSlot = findCrystalSlot();
        if (crystalSlot == -1) return;

        // Сохраняем текущий слот
        int prevSlot = getSelectedSlot();

        // Переключаемся на слот с кристаллами
        setSelectedSlot(crystalSlot);

        // Ставим кристалл
        BlockHitResult hitResult = new BlockHitResult(
                pos.toCenterPos(),
                mc.player.getHorizontalFacing(),
                pos,
                false
        );

        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);
        mc.player.swingHand(Hand.MAIN_HAND);

        // Возвращаем предыдущий слот
        setSelectedSlot(prevSlot);
    }

    private int findCrystalSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack != null && stack.getItem() == Items.END_CRYSTAL) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public String getDescription() {
        return "Automatically places and breaks end crystals to damage enemies";
    }
}
