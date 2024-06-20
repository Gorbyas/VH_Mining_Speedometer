package net.gorbyas.miningspeedometer;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.block.VaultChestBlock;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.item.tool.ToolItem;
import iskallia.vault.item.tool.ToolMaterial;
import iskallia.vault.item.tool.ToolType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.event.world.BlockEvent;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Properties;

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
            source.sendFailure(new TextComponent("Selected block must be mine-able"));
            return 0;
        }
        Block block = ((BlockItem) item.getItem()).getBlock();

/*

        //Old code from JustAHuman, works for Vault Chests, not for any block as was primarily intended

        float woodenSpeed = player.getDigSpeed(ModBlocks.WOODEN_CHEST.defaultBlockState(), null);
        float gildedSpeed = player.getDigSpeed(ModBlocks.GILDED_CHEST.defaultBlockState(), null);
        float livingSpeed = player.getDigSpeed(ModBlocks.LIVING_CHEST.defaultBlockState(), null);
        float ornateSpeed = player.getDigSpeed(ModBlocks.ORNATE_CHEST.defaultBlockState(), null);

        float gildedStrongboxSpeed = player.getDigSpeed(ModBlocks.GILDED_STRONGBOX.defaultBlockState(), null);
        float livingStrongboxSpeed = player.getDigSpeed(ModBlocks.LIVING_STRONGBOX.defaultBlockState(), null);
        float ornateStrongboxSpeed = player.getDigSpeed(ModBlocks.ORNATE_STRONGBOX.defaultBlockState(), null);

        boolean instaMineWood = woodenSpeed > ModBlocks.WOODEN_CHEST.defaultDestroyTime() * 30;
        boolean instaMineGilded = gildedSpeed > ModBlocks.GILDED_CHEST.defaultDestroyTime() * 30;
        boolean instaMineLiving = livingSpeed > ModBlocks.LIVING_CHEST.defaultDestroyTime() * 30;
        boolean instaMineOrnate = ornateSpeed > ModBlocks.ORNATE_CHEST.defaultDestroyTime() * 30;

        boolean instaMineGildedStrongbox = gildedStrongboxSpeed > ModBlocks.GILDED_STRONGBOX.defaultDestroyTime() * 30;
        boolean instaMineLivingStrongbox = livingStrongboxSpeed > ModBlocks.LIVING_STRONGBOX.defaultDestroyTime() * 30;
        boolean instaMineOrnateStrongbox = ornateStrongboxSpeed > ModBlocks.ORNATE_STRONGBOX.defaultDestroyTime() * 30;

        //source.sendSuccess(new TextComponent("You can instamine: "), true);

        if (instaMineWood) source.sendSuccess(new TextComponent("Wooden Chests"), true);
        if (instaMineGilded) source.sendSuccess(new TextComponent("Gilded Chests"), true);
        if (instaMineLiving) source.sendSuccess(new TextComponent("Living Chests"), true);
        if (instaMineOrnate) source.sendSuccess(new TextComponent("Ornate Chests"), true);
        if (instaMineGildedStrongbox) source.sendSuccess(new TextComponent("Gilded Strongboxes"), true);
        if (instaMineLivingStrongbox) source.sendSuccess(new TextComponent("Living Strongboxes"), true);
        if (instaMineOrnateStrongbox) source.sendSuccess(new TextComponent("Ornate Strongboxes"), true);

        //source.sendSuccess(new TextComponent("You cannot instamine: "), true);

        if (!instaMineWood) source.sendSuccess(new TextComponent("Wooden Chests"), true);
        if (!instaMineGilded) source.sendSuccess(new TextComponent("Gilded Chests"), true);
        if (!instaMineLiving) source.sendSuccess(new TextComponent("Living Chests"), true);
        if (!instaMineOrnate) source.sendSuccess(new TextComponent("Ornate Chests"), true);
        if (!instaMineGildedStrongbox) source.sendSuccess(new TextComponent("Gilded Strongboxes"), true);
        if (!instaMineLivingStrongbox) source.sendSuccess(new TextComponent("Living Strongboxes"), true);
        if (!instaMineOrnateStrongbox) source.sendSuccess(new TextComponent("Ornate Strongboxes"), true);
        */

        String bestTool = getBestTool(block, player);

        //Command response
        source.sendSuccess(new TextComponent("You need " + Float.toString(getMiningSpeed(block.defaultBlockState(), player, source)) + " Mining speed to instamine this one!\n" + bestTool + "will be the most effective"), true);

        return 1;
    }

    private String getBestTool (Block block, Player player){

        //Make a
        ItemStack pickaxeStack = new ItemStack(ToolItem.create(ToolMaterial.CHROMATIC_IRON_INGOT, ToolType.PICK).getItem(), 1);
        ItemStack axeStack = new ItemStack(ToolItem.create(ToolMaterial.CHROMATIC_IRON_INGOT, ToolType.AXE).getItem(), 1);
        ItemStack shovelStack = new ItemStack(ToolItem.create(ToolMaterial.CHROMATIC_IRON_INGOT, ToolType.SHOVEL).getItem(), 1);
        ItemStack sickleStack = new ItemStack(ToolItem.create(ToolMaterial.CHROMATIC_IRON_INGOT, ToolType.SICKLE).getItem(), 1);


        float pickaxeSpeed = pickaxeStack.getDestroySpeed(block.defaultBlockState());
        float axeSpeed = axeStack.getDestroySpeed(block.defaultBlockState());
        float shovelSpeed = shovelStack.getDestroySpeed(block.defaultBlockState());
        float sickleSpeed = sickleStack.getDestroySpeed(block.defaultBlockState());

        if (block instanceof VaultChestBlock){
            return ((VaultChestBlock) block).getType().name();
        }

        if(pickaxeSpeed > axeSpeed && pickaxeSpeed > shovelSpeed && pickaxeSpeed > sickleSpeed){
            return "Picking";
        } else if (axeSpeed > pickaxeSpeed && axeSpeed > shovelSpeed && axeSpeed > sickleSpeed) {
            return "Axing";
        } else if (shovelSpeed > axeSpeed && shovelSpeed > pickaxeSpeed && shovelSpeed > sickleSpeed) {
            return "Shoveling";
        } else if (sickleSpeed > axeSpeed && sickleSpeed > pickaxeSpeed && sickleSpeed > shovelSpeed) {
            return "Reaping";
        }
        else return "No tool";


    }

    public float getMiningSpeed(BlockState pState, Player player, CommandSourceStack source) {
        float f = player.getMainHandItem().getDestroySpeed(pState);
        float speedToInstamine = pState.getBlock().defaultDestroyTime() * 30;
        float defaultVaultToolSpeed = 9.0F;
        if (f > 1.0F) {
            int i = EnchantmentHelper.getBlockEfficiency(player);
            ItemStack itemstack = player.getMainHandItem();
            if (i > 0 && !itemstack.isEmpty()) {
                f += (float)(i * i + 1);
                speedToInstamine -= (float)(i * i + 1);
            }
        }

        if (MobEffectUtil.hasDigSpeed(player)) {
            f *= 1.0F + (float)(MobEffectUtil.getDigSpeedAmplification(player) + 1) * 0.2F;
            defaultVaultToolSpeed *= 1.0F + (float)(MobEffectUtil.getDigSpeedAmplification(player) + 1) * 0.2F;
            speedToInstamine *= 1/(1.0F + (float)(MobEffectUtil.getDigSpeedAmplification(player) + 1) * 0.2F);
            if(MobEffectUtil.getDigSpeedAmplification(player) < 2){
                source.sendSuccess(new TextComponent("You can lower the required mining speed with higher Haste level"),true);
            }
        }

        if (player.hasEffect(MobEffects.DIG_SLOWDOWN)) {
            float f1;
            source.sendSuccess(new TextComponent("You can lower the required mining speed by getting rid of negative effects"),true);
            switch(player.getEffect(MobEffects.DIG_SLOWDOWN).getAmplifier()) {
                case 0:
                    f1 = 0.3F;
                    break;
                case 1:
                    f1 = 0.09F;
                    break;
                case 2:
                    f1 = 0.0027F;
                    break;
                case 3:
                default:
                    f1 = 8.1E-4F;
            }

            f *= f1;
            defaultVaultToolSpeed *= f1;
            speedToInstamine *= (float) 1/f1;
        }

        if (player.isEyeInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(player)) {
            f /= 5.0F;
            defaultVaultToolSpeed /= 5.0F;
            speedToInstamine *= 5.0F;
            source.sendSuccess(new TextComponent("You can lower the required mining speed by getting Aqua Affinity"),true);
        }

        if (!player.isOnGround()) {
            f /= 5.0F;
            defaultVaultToolSpeed /= 5.0F;
            speedToInstamine *= 5.0F;
            source.sendSuccess(new TextComponent("You can lower the required mining speed by standing on ground"),true);
        }

        f = net.minecraftforge.event.ForgeEventFactory.getBreakSpeed(player, pState, f, null);
        speedToInstamine -= defaultVaultToolSpeed;
        return speedToInstamine;
    }
}
