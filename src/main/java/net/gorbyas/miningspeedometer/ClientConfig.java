package net.gorbyas.miningspeedometer;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<OutputFormat> COMMAND_OUTPUT_FORMAT;
    public static final ForgeConfigSpec.ConfigValue<OutputFormat> TOOLTIP_OUTPUT_FORMAT;

    static {
        BUILDER.push("Config for VH Mining Speedometer");

        COMMAND_OUTPUT_FORMAT = BUILDER.comment("The Output Format for the /speedometer command [TOTAL_MINING_SPEED_WITHBASE, TOTAL_MINING_SPEED_WITHOUTBASE, ADDITIONAL_MINING_SPEED]")
                .define("Command Output Format", OutputFormat.TOTAL_MINING_SPEED_WITHOUTBASE);

        TOOLTIP_OUTPUT_FORMAT = BUILDER.comment("The Output Format for the speedometer jade tooltip [TOTAL_MINING_SPEED_WITHBASE, TOTAL_MINING_SPEED_WITHOUTBASE, ADDITIONAL_MINING_SPEED]")
                .define("Jade Output Format", OutputFormat.TOTAL_MINING_SPEED_WITHOUTBASE);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public enum OutputFormat {
        TOTAL_MINING_SPEED_WITHBASE,
        TOTAL_MINING_SPEED_WITHOUTBASE,
        ADDITIONAL_MINING_SPEED
    }
}