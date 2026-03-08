package com.fox2code.tfcah;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(TerraFirmaCraftAnvilHelper.MODID)
public class TerraFirmaCraftAnvilHelper {
    public static final String MODID = "tfcah";
    private static final Logger LOGGER = LogUtils.getLogger();

    public TerraFirmaCraftAnvilHelper() {}

    public static Logger getLOGGER() {
        return LOGGER;
    }
}
