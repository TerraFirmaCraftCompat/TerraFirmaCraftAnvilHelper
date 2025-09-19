package com.fox2code.tfcah.client;

import com.fox2code.tfcah.TerraFirmaCraftAnvilHelper;
import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.dries007.tfc.common.component.forge.ForgeStep;
import net.dries007.tfc.common.component.forge.Forging;
import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = TerraFirmaCraftAnvilHelper.MODID, dist = Dist.CLIENT)
public class TerraFirmaCraftAnvilHelperClient {
    private static TerraFirmaCraftAnvilSolution SOLUTION = TerraFirmaCraftAnvilSolution.UNDEFINED;
    private static TerraFirmaCraftAnvilRecipeInfo RECIPE_CACHE = null;
    private static ForgeStep nextForgeStep;

    public TerraFirmaCraftAnvilHelperClient(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(this);
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
            AnvilRecipe anvilRecipe = forging.getRecipe();
            if (anvilRecipe == null) {
                nextForgeStep = null;
                return;
            }
            int steps = SOLUTION.getStepForForging(forging);
            if (steps == -1) {
                SOLUTION = TerraFirmaCraftAnvilSolver.solveFor(getRecipeInfo(anvilRecipe), forging);
                steps = SOLUTION.getStepForForging(forging);
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
