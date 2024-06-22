package net.gorbyas.miningspeedometer.jade;

import mcp.mobius.waila.api.IWailaClientRegistration;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import mcp.mobius.waila.impl.config.ConfigEntry;
import mcp.mobius.waila.impl.config.PluginConfig;
import net.gorbyas.miningspeedometer.MiningSpeedometer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

@WailaPlugin
public class SpeedometerJadePlugin implements IWailaPlugin {
    public static final ResourceLocation SPEEDOMETER = new ResourceLocation(MiningSpeedometer.MOD_ID, "speedometer");

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        PluginConfig.INSTANCE.addConfig(new ConfigEntry(SPEEDOMETER, true, false));
        registration.registerComponentProvider(SpeedometerComponent.INSTANCE, TooltipPosition.TAIL, Block.class);
    }
}
