package net.gorbyas.miningspeedometer.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class GetMiningspeedCommand {
    public GetMiningspeedCommand(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("speedometer").then(Commands.literal("set").executes((command) -> {
            return 0;
        })));
    }

    private boolean isVaultTool(ItemStack itemStack){
        if(itemStack.isEmpty()){
            return false;
        } else if (itemStack.getTag() == null) {
            return false;
        } else return itemStack.getTag().contains("vault_tool");
    }

    private int getMiningspeed(CommandSourceStack source) throws CommandSyntaxException{
        Inventory playerInventory = source.getPlayerOrException().getInventory();
        ItemStack item = playerInventory.getSelected();
        if (isVaultTool(item)){
            return 1;
        } else return 0;
    }
}
