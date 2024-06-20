package net.gorbyas.miningspeedometer;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.item.tool.ToolItem;
import iskallia.vault.item.tool.ToolMaterial;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

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

        if (!(item.getItem() instanceof BlockItem)){
            source.sendFailure(new TextComponent("Selected block must be mine-able"));
            return 0;
        }
        Block block = ((BlockItem) item.getItem()).getBlock();

        float itemSpeed = player.getDigSpeed(block.defaultBlockState(), null);
/*
        float woodenSpeed = player.getDigSpeed(ModBlocks.WOODEN_CHEST.defaultBlockState(), null);
        float gildedSpeed = player.getDigSpeed(ModBlocks.GILDED_CHEST.defaultBlockState(), null);
        float livingSpeed = player.getDigSpeed(ModBlocks.LIVING_CHEST.defaultBlockState(), null);
        float ornateSpeed = player.getDigSpeed(ModBlocks.ORNATE_CHEST.defaultBlockState(), null);

        float gildedStrongboxSpeed = player.getDigSpeed(ModBlocks.GILDED_STRONGBOX.defaultBlockState(), null);
        float livingStrongboxSpeed = player.getDigSpeed(ModBlocks.LIVING_STRONGBOX.defaultBlockState(), null);
        float ornateStrongboxSpeed = player.getDigSpeed(ModBlocks.ORNATE_STRONGBOX.defaultBlockState(), null);
*/
        boolean instaMineItem = itemSpeed > block.defaultDestroyTime() * 30;
/*
        boolean instaMineWood = woodenSpeed > ModBlocks.WOODEN_CHEST.defaultDestroyTime() * 30;
        boolean instaMineGilded = gildedSpeed > ModBlocks.GILDED_CHEST.defaultDestroyTime() * 30;
        boolean instaMineLiving = livingSpeed > ModBlocks.LIVING_CHEST.defaultDestroyTime() * 30;
        boolean instaMineOrnate = ornateSpeed > ModBlocks.ORNATE_CHEST.defaultDestroyTime() * 30;

        boolean instaMineGildedStrongbox = gildedStrongboxSpeed > ModBlocks.GILDED_STRONGBOX.defaultDestroyTime() * 30;
        boolean instaMineLivingStrongbox = livingStrongboxSpeed > ModBlocks.LIVING_STRONGBOX.defaultDestroyTime() * 30;
        boolean instaMineOrnateStrongbox = ornateStrongboxSpeed > ModBlocks.ORNATE_STRONGBOX.defaultDestroyTime() * 30;
*/
        source.sendSuccess(new TextComponent("You can instamine: "), true);
        if (instaMineItem) source.sendSuccess(new TextComponent(item.createItemStack(1,false).getDisplayName().toString()), true);
/*
        if (instaMineWood) source.sendSuccess(new TextComponent("Wooden Chests"), true);
        if (instaMineGilded) source.sendSuccess(new TextComponent("Gilded Chests"), true);
        if (instaMineLiving) source.sendSuccess(new TextComponent("Living Chests"), true);
        if (instaMineOrnate) source.sendSuccess(new TextComponent("Ornate Chests"), true);
        if (instaMineGildedStrongbox) source.sendSuccess(new TextComponent("Gilded Strongboxes"), true);
        if (instaMineLivingStrongbox) source.sendSuccess(new TextComponent("Living Strongboxes"), true);
        if (instaMineOrnateStrongbox) source.sendSuccess(new TextComponent("Ornate Strongboxes"), true);
*/
        source.sendSuccess(new TextComponent("You cannot instamine: "), true);
        if (!instaMineItem) source.sendSuccess(new TextComponent(item.createItemStack(1,false).getDisplayName().toString()), true);
        /*
        if (!instaMineWood) source.sendSuccess(new TextComponent("Wooden Chests"), true);
        if (!instaMineGilded) source.sendSuccess(new TextComponent("Gilded Chests"), true);
        if (!instaMineLiving) source.sendSuccess(new TextComponent("Living Chests"), true);
        if (!instaMineOrnate) source.sendSuccess(new TextComponent("Ornate Chests"), true);
        if (!instaMineGildedStrongbox) source.sendSuccess(new TextComponent("Gilded Strongboxes"), true);
        if (!instaMineLivingStrongbox) source.sendSuccess(new TextComponent("Living Strongboxes"), true);
        if (!instaMineOrnateStrongbox) source.sendSuccess(new TextComponent("Ornate Strongboxes"), true);
        */


        return 1;
    }
}
