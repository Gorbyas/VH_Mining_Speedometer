package net.gorbyas.miningspeedometer.events;

import net.gorbyas.miningspeedometer.MiningSpeedometer;
import net.gorbyas.miningspeedometer.commands.GetMiningspeedCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

@Mod.EventBusSubscriber(modid = MiningSpeedometer.MOD_ID)

public class ModEvents {

    @SubscribeEvent
    public static void onCommandsRegister (RegisterCommandsEvent event){
        new GetMiningspeedCommand(event.getDispatcher());

        ConfigCommand.register(event.getDispatcher());
    }

}
