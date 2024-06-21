package net.gorbyas.miningspeedometer.events;

import net.gorbyas.miningspeedometer.MiningSpeedometer;
import net.gorbyas.miningspeedometer.commands.SpeedometerCommand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

@Mod.EventBusSubscriber(modid = MiningSpeedometer.MOD_ID, value = Dist.CLIENT)
public class ModEvents {


    //RegisterClientCommandsEvent instead of RegisterCommandsEvent results into "a player is required to run this command here" fail message
    @SubscribeEvent
    public static void onCommandRegister(RegisterClientCommandsEvent event){

        new SpeedometerCommand(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
    }

}
