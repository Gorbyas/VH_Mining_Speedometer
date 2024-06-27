package net.gorbyas.miningspeedometer.jade;

import iskallia.vault.item.tool.ToolItem;
import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import net.gorbyas.miningspeedometer.ClientConfig;
import net.gorbyas.miningspeedometer.MiningSpeedometer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SpeedometerComponent implements IComponentProvider {
    public static final SpeedometerComponent INSTANCE = new SpeedometerComponent();

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        if (!iPluginConfig.get(SpeedometerJadePlugin.SPEEDOMETER)) {
            return;
        }

        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof ToolItem)) {
            return;
        }

        Block block = blockAccessor.getBlock();
        if (block.defaultDestroyTime() == -1) {
            return;
        }

        BlockState state = blockAccessor.getBlockState();
        float neededSpeed = MiningSpeedometer.getMiningSpeed(player, stack, block, state, false);
        ClientConfig.OutputFormat format = ClientConfig.TOOLTIP_OUTPUT_FORMAT.get();

        float formattedSpeed = MiningSpeedometer.format(format, neededSpeed, stack.getDestroySpeed(state));
        boolean canInstamine = neededSpeed == 0 || stack.getDestroySpeed(state) >= neededSpeed;
        boolean additional = format == ClientConfig.OutputFormat.ADDITIONAL_MINING_SPEED;
        if (canInstamine && additional) {
            iTooltip.add(new TranslatableComponent("speedometer.jade.tooltip.instamine").withStyle(ChatFormatting.GREEN));
            return;
        }

        iTooltip.add(new TranslatableComponent("speedometer.jade.tooltip.speed_" + (additional ? "additional" : "needed"),
                formattedSpeed, (canInstamine ? "✔" : "✘")).withStyle(canInstamine ? ChatFormatting.GREEN : ChatFormatting.RED));
    }
}
