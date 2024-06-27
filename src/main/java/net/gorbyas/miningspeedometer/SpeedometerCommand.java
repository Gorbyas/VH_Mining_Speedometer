package net.gorbyas.miningspeedometer;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import iskallia.vault.block.VaultChestBlock;
import iskallia.vault.item.tool.ToolItem;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

public class SpeedometerCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("speedometer")
                        .then(Commands.argument("block", BlockStateArgument.block())
                                .executes(SpeedometerCommand::execute))
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        BlockState state = BlockStateArgument.getBlock(context, "block").getState();
        Block block = state.getBlock();

        if (block.defaultDestroyTime() == -1) {
            source.sendFailure(new TextComponent("This block cannot be instamined"));
            return 0;
        }

        Player player = Minecraft.getInstance().player;
        ItemStack stack = player.getMainHandItem();

        if (stack.isEmpty() || !(stack.getItem() instanceof ToolItem)) {
            source.sendFailure(new TextComponent("You need to hold a Vault Tool in your main hand"));
            return 0;
        }

        String bestTool = getBestTool(stack, block, state);
        ClientConfig.OutputFormat format = ClientConfig.COMMAND_OUTPUT_FORMAT.get();
        float neededSpeed = MiningSpeedometer.format(format, MiningSpeedometer.getMiningSpeed(player, stack, block, state, true), stack.getDestroySpeed(state));

        if (neededSpeed == 0) {
            source.sendSuccess(new TextComponent("You don’t need any more Mining Speed to instamine this block!\nAll you need is " + bestTool), false);
        } else {
            source.sendSuccess(new TextComponent("You need +" + neededSpeed + (format == ClientConfig.OutputFormat.ADDITIONAL_MINING_SPEED ? " more" : "")
                    + " Mining Speed to instamine this block!\n" + bestTool + " will be the most effective"), false);
        }

        return 1;
    }

    private static String getBestTool(ItemStack stack, Block block, BlockState state) {
        if (block instanceof VaultChestBlock chestBlock) {
            return "+" + StringUtils.capitalize(chestBlock.getType().name().toLowerCase(Locale.ROOT)) + " Affinity";
        }

        if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
            return "+Picking";
        } else if (state.is(BlockTags.MINEABLE_WITH_AXE)) {
            return "+Axing";
        } else if (state.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
            return "+Shoveling";
        } else if (state.is(BlockTags.MINEABLE_WITH_HOE)
                || Items.NETHERITE_SWORD.getDestroySpeed(stack, state) > 1.0F || Items.NETHERITE_SWORD.isCorrectToolForDrops(state)
                || Items.SHEARS.getDestroySpeed(stack, state) > 1.0F || Items.SHEARS.isCorrectToolForDrops(state)) {
            return "+Reaping";
        }
        
        return "No tool";
    }
}
