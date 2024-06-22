package net.gorbyas.miningspeedometer;

import com.mojang.logging.LogUtils;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(net.gorbyas.miningspeedometer.MiningSpeedometer.MOD_ID)
@Mod.EventBusSubscriber(modid = MiningSpeedometer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class MiningSpeedometer {
    public static final String MOD_ID = "miningspeedometer";
    private static final Logger LOGGER = LogUtils.getLogger();

    public MiningSpeedometer() {
        LOGGER.info("VH Mining Speedometer");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC, "miningspeedometer.toml");
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public static void onCommandRegister(RegisterClientCommandsEvent event) {
        SpeedometerCommand.register(event.getDispatcher());
    }

    public static float getMiningSpeed(Player player, ItemStack stack, Block block, BlockState state, boolean sendTips) {
        float baseSpeed = stack.getDestroySpeed(state);
        float speedToInstamine = block.defaultDestroyTime() * 30;

        if (MobEffectUtil.hasDigSpeed(player)) {
            int haste = MobEffectUtil.getDigSpeedAmplification(player);
            speedToInstamine *= 1 / (1.0F + (float) (haste + 1) * 0.2F);
            if (sendTips && haste < 2) {
                player.sendMessage(new TextComponent("You can lower the required mining speed with Haste III"), Util.NIL_UUID);
            }
        }

        if (baseSpeed > 1.0F) {
            int efficiency = EnchantmentHelper.getBlockEfficiency(player);
            if (efficiency > 0) {
                speedToInstamine -= (float) (efficiency * efficiency + 1);
            }

            if (sendTips && efficiency < 5) {
                player.sendMessage(new TextComponent("You can lower the required mining speed by getting Efficiency V"), Util.NIL_UUID);
            }
        }

        if (player.hasEffect(MobEffects.DIG_SLOWDOWN)) {
            float speedReduction = switch (player.getEffect(MobEffects.DIG_SLOWDOWN).getAmplifier()) {
                case 0 -> 0.3F;
                case 1 -> 0.09F;
                case 2 -> 0.0027F;
                default -> 8.1E-4F;
            };
            speedToInstamine *= (float) 1 / speedReduction;

            if (sendTips) {
                player.sendMessage(new TextComponent("You can lower the required mining speed by getting rid of negative effects"), Util.NIL_UUID);
            }
        }

        if (player.isEyeInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(player)) {
            speedToInstamine *= 5.0F;
            if (sendTips) {
                player.sendMessage(new TextComponent("When in water you can lower the required mining speed by getting Aqua Affinity"), Util.NIL_UUID);
            }
        }

        if (!player.isOnGround()) {
            speedToInstamine *= 5.0F;
            if (sendTips) {
                player.sendMessage(new TextComponent("You can lower the required mining speed by standing on ground"), Util.NIL_UUID);
            }
        }

        return Math.max(speedToInstamine, 0);
    }

    public static float format(ClientConfig.OutputFormat outputFormat, float speedToInstamine, float baseSpeed) {
        return Math.max(switch (outputFormat) {
            case TOTAL_MINING_SPEED_WITHBASE -> speedToInstamine;
            case TOTAL_MINING_SPEED_WITHOUTBASE -> speedToInstamine - 9.0F;
            case ADDITIONAL_MINING_SPEED -> speedToInstamine - baseSpeed;
        }, 0);
    }
}
