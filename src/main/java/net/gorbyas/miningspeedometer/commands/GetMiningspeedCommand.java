package net.gorbyas.miningspeedometer.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.state.BlockState;

public class GetMiningspeedCommand {
    public GetMiningspeedCommand(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("speedometer").then(Commands.literal("for").then(Commands.argument("item", ItemArgument.item())).executes((command) -> {
            return 0;
        })));
    }

    private boolean isTool(ItemStack itemStack){
        assert itemStack.getTag() != null;
        return itemStack.getTag().contains("DiggingTool");
    }

    private boolean isVaultTool(ItemStack itemStack){
        if(itemStack.getTag() == null || itemStack.isEmpty()){
            return false;
        } else return itemStack.getTag().contains("vault_tool");
    }

    private int nEffi (ItemStack itemStack){
        if(itemStack.isEnchanted()){
            if(itemStack.getEnchantmentTags().contains("Efficiency")){
                for (Tag tag : itemStack.getEnchantmentTags()){
                    if (tag.getType() == )
                }
            }
        }
    }

    private int getMiningspeed(CommandSourceStack source, ItemArgument itemArgument) throws CommandSyntaxException{
        Inventory playerInventory = source.getPlayerOrException().getInventory();
        ItemStack item = playerInventory.player.getMainHandItem();
        float toolSpeed;
        float msNeeded;
        if (isVaultTool(item)){
            toolSpeed = 0;
            if (item.isEnchanted()){
                if(item.getEnchantmentTags().contains("Efficiency")){
                    toolSpeed *= 1.2f;
                }
            }
            return 1;
        }
        source.sendSuccess(new TextComponent("You’ll need x more"), true);
        return 0;

    }
}
