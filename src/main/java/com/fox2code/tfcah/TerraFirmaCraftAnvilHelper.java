package com.fox2code.tfcah;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(TerraFirmaCraftAnvilHelper.MODID)
public class TerraFirmaCraftAnvilHelper {
    public static final String MODID = "tfcah";
    private static final Logger LOGGER = LogUtils.getLogger();

    public TerraFirmaCraftAnvilHelper(IEventBus modEventBus, ModContainer modContainer) {}

    public static Logger getLOGGER() {
        return LOGGER;
    }
}
