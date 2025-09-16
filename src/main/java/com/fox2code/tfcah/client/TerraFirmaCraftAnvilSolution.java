package com.fox2code.tfcah.client;

import net.dries007.tfc.common.component.forge.ForgeStep;
import net.dries007.tfc.common.component.forge.Forging;
import net.dries007.tfc.common.recipes.AnvilRecipe;

import java.util.List;

public record TerraFirmaCraftAnvilSolution(TerraFirmaCraftAnvilRecipeInfo terraFirmaCraftAnvilRecipeInfo,
                                           int target, ForgeStep[] forgeSteps, int[] forgeIndexes) {
    public static final TerraFirmaCraftAnvilSolution UNDEFINED =
            new TerraFirmaCraftAnvilSolution(null, -1, new ForgeStep[0], new int[0]);

    public TerraFirmaCraftAnvilSolution(TerraFirmaCraftAnvilRecipeInfo terraFirmaCraftAnvilRecipeInfo,
                                        int target, ForgeStep[] forgeSteps, int[] forgeIndexes) {
        this.terraFirmaCraftAnvilRecipeInfo = terraFirmaCraftAnvilRecipeInfo;
        this.target = target;
        this.forgeSteps = forgeSteps;
        this.forgeIndexes = forgeIndexes;
        if (forgeSteps.length != forgeIndexes.length) {
            throw new IllegalArgumentException();
        }
    }

    public int getStepForForging(Forging forging) {
        AnvilRecipe anvilRecipe = forging.getRecipe();
        final int currentTarget = forging.target();
        if (this.terraFirmaCraftAnvilRecipeInfo == null ||
                this.terraFirmaCraftAnvilRecipeInfo.anvilRecipe != anvilRecipe ||
                this.target != currentTarget) {
            return -1;
        }
        final int currentWork = forging.work();
        final int len = this.forgeIndexes.length;
        if (currentWork == currentTarget) {
            // Check for fully completed solution
            List<ForgeStep> lastSteps = forging.lastSteps();
            ForgeStep[] last3Steps = this.terraFirmaCraftAnvilRecipeInfo.last3Steps;
            boolean valid = true;
            for (int i2 = 3; i2 > 0; i2--) {
                if (last3Steps[3 - i2] != null &&
                        last3Steps[3 - i2] != getStep(lastSteps, i2)) {
                    valid = false;
                    break;
                }
            }
            if (valid) {
                return len;
            }
        }
        int i = 1;
        if (len > 3) {
            // Strict check for the last 3 steps
            List<ForgeStep> lastSteps = forging.lastSteps();
            ForgeStep[] last3Steps = this.terraFirmaCraftAnvilRecipeInfo.last3Steps;
            for (; i <= 3; i++) {
                int expectedWork = this.forgeIndexes[len - i];
                if (expectedWork == currentWork) {
                    boolean valid = true;
                    for (int i2 = 3 - i; i2 > 0; i2--) {
                        // last; i -> 1; i2 -> 2; lastStepsIndex -> 0;
                        // last; i -> 1; i2 -> 1; lastStepsIndex -> 1;
                        // last; i -> 2; i2 -> 1; lastStepsIndex -> 0;
                        int lastStepsIndex = 3 - (i + i2);
                        if (last3Steps[lastStepsIndex] != null &&
                                last3Steps[lastStepsIndex] != getStep(lastSteps, i2)) {
                            valid = false;
                            break;
                        }
                    }
                    if (valid) {
                        return len - i;
                    }
                }
            }
        }
        // Loose check for all other steps
        for (; i <= len; i++) {
            int expectedWork = this.forgeIndexes[len - i];
            if (expectedWork == currentWork) {
                return len - i;
            }
        }
        return -1;
    }

    private static ForgeStep getStep(List<ForgeStep> list, int fromEnd) {
        int index = list.size() - fromEnd;
        return index < 0 ? null : list.get(index);
    }
}
