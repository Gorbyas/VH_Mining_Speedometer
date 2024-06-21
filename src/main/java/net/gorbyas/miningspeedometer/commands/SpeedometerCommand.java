package net.gorbyas.miningspeedometer.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.block.VaultChestBlock;
import iskallia.vault.item.tool.ToolItem;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.tags.*;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ClientCommandHandler; //this has to be a part of the solution I suppose, idk how to implement it tho
import net.minecraftforge.client.ClientCommandSourceStack;

import java.util.Locale;
import java.util.Objects;


public class SpeedometerCommand {
    public SpeedometerCommand(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("speedometer").then(Commands.argument("item", ItemArgument.item()).executes((command) -> {
            return speedometer(command.copyFor(command.getSource()), ItemArgument.getItem(command, "item"));
        })));
    }

    private int speedometer(CommandContext<CommandSourceStack> context, ItemInput item) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        Player player = source.getPlayerOrException();
        ItemStack playerMainHandItem = player.getMainHandItem();

        if (!(playerMainHandItem.getItem() instanceof ToolItem)) {
            source.sendFailure(new TextComponent("You need to hold a Vault Tool in your main hand"));
            return 0;
        }

        //Check if the item is actually a block
        if (!(item.getItem() instanceof BlockItem)){
            source.sendFailure(new TextComponent("Selected item must be a block"));
            return 0;
        }

        Block block = ((BlockItem) item.getItem()).getBlock();

        String bestTool = getBestTool(block);
        float speed = getMiningSpeed(block.defaultBlockState(), player, source);

        if (speed < 0){
            source.sendFailure(new TextComponent("This block cannot be instamined"));
            return 0;
        } else if (speed == 0) {
            source.sendSuccess(new TextComponent("You don’t need any more mining speed to instamine this one!\nall you need is " + bestTool), false);
            return 1;
        }
        else {
            source.sendSuccess(new TextComponent("You need " + speed + " total additional Mining speed to instamine this one!\n" + bestTool + " will be the most effective"), false);
            return 1;
        }
    }

    private String getBestTool (Block block){

        if (block instanceof VaultChestBlock){
            return "+" + ((VaultChestBlock) block).getType().name().toLowerCase(Locale.ROOT) + " affinity";
        }

        if(block.defaultBlockState().is(BlockTags.MINEABLE_WITH_PICKAXE)){
            return "+Picking";
        }
        else if(block.defaultBlockState().is(BlockTags.MINEABLE_WITH_AXE)){
            return "+Axing";
        }
        else if(block.defaultBlockState().is(BlockTags.MINEABLE_WITH_HOE)){
            return "+Reaping";
        }
        else if(block.defaultBlockState().is(BlockTags.MINEABLE_WITH_SHOVEL)){
            return "+Shoveling";
        } else if (block.getName().getString().equalsIgnoreCase("cobweb")) {
            return "+Reaping";
        } else return "No tool";

    }

    public float getMiningSpeed(BlockState pState, Player player, CommandSourceStack source) {
        float f = player.getMainHandItem().getDestroySpeed(pState);
        float speedToInstamine = pState.getBlock().defaultDestroyTime() * 30;
        float defaultVaultToolSpeed = 9.0F;


        if (MobEffectUtil.hasDigSpeed(player)) {
            speedToInstamine *= 1/(1.0F + (float)(MobEffectUtil.getDigSpeedAmplification(player) + 1) * 0.2F);
            if(MobEffectUtil.getDigSpeedAmplification(player) < 2){
                source.sendSuccess(new TextComponent("You can lower the required mining speed with higher Haste level"),false);
            }
        }
        if (f > 1.0F) {
            int i = EnchantmentHelper.getBlockEfficiency(player);
            ItemStack itemstack = player.getMainHandItem();
            if (i > 0 && !itemstack.isEmpty()) {
                speedToInstamine -= (float)(i * i + 1);
            } else if (i == 0 || itemstack.isEmpty()){
                source.sendSuccess(new TextComponent("You can lower the required mining speed by getting the efficiency enchantment"),false);
            }
        }
        if (player.hasEffect(MobEffects.DIG_SLOWDOWN)) {
            float f1;
            source.sendSuccess(new TextComponent("You can lower the required mining speed by getting rid of negative effects"),false);
            f1 = switch (Objects.requireNonNull(player.getEffect(MobEffects.DIG_SLOWDOWN)).getAmplifier()) {
                case 0 -> 0.3F;
                case 1 -> 0.09F;
                case 2 -> 0.0027F;
                default -> 8.1E-4F;
            };

            speedToInstamine *= (float) 1/f1;
        }

        if (player.isEyeInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(player)) {
            speedToInstamine *= 5.0F;
            source.sendSuccess(new TextComponent("You can lower the required mining speed by getting Aqua Affinity"),false);
        }

        if (!player.isOnGround()) {
            speedToInstamine *= 5.0F;
            source.sendSuccess(new TextComponent("You can lower the required mining speed by standing on ground"),false);
        }

        if (speedToInstamine < 0 && !(pState.getBlock() instanceof VaultChestBlock)) {return -1;}
        speedToInstamine -= defaultVaultToolSpeed;

        if (0 >= speedToInstamine) {return 0;}
        return speedToInstamine;
    }
}
