package net.gorbyas.miningspeedometer;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(MiningSpeedometer.MOD_ID)
public class MiningSpeedometer {
    public static final String MOD_ID = "miningspeedometer";
    private static final Logger LOGGER = LogUtils.getLogger();

    public MiningSpeedometer() {
        MinecraftForge.EVENT_BUS.addListener(this::onCommandsRegister);
    }

    public void onCommandsRegister(RegisterCommandsEvent event) {
        new SpeedometerCommand(event.getDispatcher());
    }
}