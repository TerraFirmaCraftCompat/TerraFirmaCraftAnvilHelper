package com.fox2code.tfcah.client;

import com.fox2code.tfcah.TerraFirmaCraftAnvilHelper;
import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.dries007.tfc.common.capabilities.forge.ForgeStep;
import net.dries007.tfc.common.capabilities.forge.Forging;
import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = TerraFirmaCraftAnvilHelper.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class TerraFirmaCraftAnvilHelperClient {
    private static TerraFirmaCraftAnvilSolution SOLUTION = TerraFirmaCraftAnvilSolution.UNDEFINED;
    private static TerraFirmaCraftAnvilRecipeInfo RECIPE_CACHE = null;
    private static ForgeStep nextForgeStep;

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new TerraFirmaCraftAnvilHelperClient());
    }

    public TerraFirmaCraftAnvilHelperClient() {
        Thread asyncLoadThread = new Thread(() -> {
            long startMs = System.currentTimeMillis();
            if (TerraFirmaCraftAnvilSolveTable.checkSanity()) {
                long timeDiff = System.currentTimeMillis() - startMs;
                if (timeDiff == 0L) timeDiff = 1L;
                TerraFirmaCraftAnvilHelper.getLOGGER().info(
                        "Lookup table successfully loaded in {}ms", timeDiff);
            }
        }, "TerraFirmaCraft - AnvilHelper - AsyncTableInitThread");
        asyncLoadThread.setPriority(Thread.MIN_PRIORITY);
        asyncLoadThread.setDaemon(true);
        asyncLoadThread.start();
    }

    private static TerraFirmaCraftAnvilRecipeInfo getRecipeInfo(AnvilRecipe anvilRecipe) {
        TerraFirmaCraftAnvilRecipeInfo anvilRecipeInfo = RECIPE_CACHE;
        if (anvilRecipeInfo != null && anvilRecipeInfo.anvilRecipe == anvilRecipe) {
            return anvilRecipeInfo;
        }
        return RECIPE_CACHE = TerraFirmaCraftAnvilRecipeInfo.getRecipeInfo(anvilRecipe);
    }

    @SubscribeEvent
    public void onRenderedScreen(RenderGuiEvent.Post renderGuiEvent) {
        Screen screen = Minecraft.getInstance().screen;
        if (screen == null || Minecraft.getInstance().level == null) {
            return;
        }
        ForgeStep forgeStepSolution = null;
        if (screen instanceof AnvilScreenAccessor anvilScreenAccessor) {
            // AnvilContainer anvilContainer = anvilScreenAccessor.tfcah$getAnvilContainer();
            AnvilBlockEntity anvilBlockEntity = anvilScreenAccessor.tfcah$getAnvilBlockEntity();
            Forging forging = anvilBlockEntity.getMainInputForging();
            assert anvilBlockEntity.getLevel() != null;
            if (forging == null) return;
            AnvilRecipe anvilRecipe = forging.getRecipe(anvilBlockEntity.getLevel());
            if (anvilRecipe == null) {
                nextForgeStep = null;
                return;
            }
            int steps = SOLUTION.getStepForForging(forging, anvilBlockEntity.getLevel());
            if (steps == -1) {
                SOLUTION = TerraFirmaCraftAnvilSolver.solveFor(getRecipeInfo(anvilRecipe), forging);
                steps = SOLUTION.getStepForForging(forging, anvilBlockEntity.getLevel());
            }
            if (steps == -1) {
                nextForgeStep = null;
                return;
            }
            if (SOLUTION.forgeSteps().length != steps) {
                forgeStepSolution = SOLUTION.forgeSteps()[steps];
            }
        }
        nextForgeStep = forgeStepSolution;
    }

    public static boolean shouldGlow(ForgeStep forgeStep) {
        return forgeStep == nextForgeStep;
    }
}
