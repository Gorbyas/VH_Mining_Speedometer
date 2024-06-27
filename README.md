# VH Mining Speedometer <a href="https://www.curseforge.com/minecraft/mc-mods/vh-mining-speedometer"><img src="http://cf.way2muchnoise.eu/1040963.svg" alt="CF"></a>
Have you ever wanted to know just how much mining speed you needed to instamine that random block?
Whether it's a wooden chest, an ornate strong box, or just a random block, this mod is the mod for you!
VH Mining Speedometer is a mod that adds both a command `/speedometer modid:block` and a jade tooltip (configurable) that tells you exactly how much mining speed you need!
The mod will take all potion effects, enchantments, etc, into account to ensure you get the most accurate values possible!

## Config
The mod also has a set of config options to format the values in a way that suits your needs the best, just check out the config file!
### Config Output Format Types:
- `TOTAL_MINING_SPEED_WITHBASE`: The total mining speed required to instamine the block, including the base mining speed of a vault tool.
- `TOTAL_MINING_SPEED_WITHOUT`: The total mining speed required to instamine the block, excluding the base mining speed of the tool. (`TOTAL_MINING_SPEED_WITHBASE` - 9)
- `ADDITIONAL_MINING_SPEED`: The additional mining speed required to instamine the block, how much more mining speed you need to instamine the block. (`TOTAL_MINING_SPEED_WITHBASE` - `Current Tool's total Mining Speed`)