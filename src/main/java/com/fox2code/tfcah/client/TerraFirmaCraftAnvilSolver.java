package com.fox2code.tfcah.client;

import net.dries007.tfc.common.component.forge.ForgeStep;
import net.dries007.tfc.common.component.forge.Forging;

import java.util.ArrayList;

public final class TerraFirmaCraftAnvilSolver {
    private TerraFirmaCraftAnvilSolver() {}

    public static TerraFirmaCraftAnvilSolution solveFor(TerraFirmaCraftAnvilRecipeInfo recipe, Forging forging) {
        if (recipe == null || forging == null || recipe.notLastStep) {
            return TerraFirmaCraftAnvilSolution.UNDEFINED;
        }
        int current = forging.work();
        int target = forging.target();
        int totalRestrictions = 0;
        int totalMinForcedMoves = 0;
        int endWorkDiff = 0;
        ForgeStep[] last3Steps = recipe.last3Steps;
        for (ForgeStep forgeStep : last3Steps) {
            if (forgeStep != null) {
                totalRestrictions++;
                endWorkDiff += forgeStep.step();
            } else if (totalRestrictions != 0) {
                totalMinForcedMoves++;
            }
        }
        int targetExcludeForcedSteps = target - endWorkDiff;
        ArrayList<ForgeStep> forgeSteps = new ArrayList<>(
                TerraFirmaCraftAnvilSolveTable.solveForSteps(targetExcludeForcedSteps - current));
        if (forgeSteps.size() < totalMinForcedMoves) {
            return TerraFirmaCraftAnvilSolution.UNDEFINED;
        }
        switch (totalMinForcedMoves) {
            case 0: {
                for (ForgeStep forgeStep : recipe.last3Steps) {
                    if (forgeStep != null) {
                        forgeSteps.add(forgeStep);
                    }
                }
                break;
            }
            case 1: {
                ForgeStep forgeStepForced = forgeSteps.removeLast();
                boolean has = false;
                for (ForgeStep forgeStep : recipe.last3Steps) {
                    if (forgeStep != null) {
                        forgeSteps.add(forgeStep);
                        has = true;
                    } else if (has) {
                        forgeSteps.add(forgeStepForced);
                    }
                }
                break;
            }
            case 2: {
                // Can only happen for [type, null, null] so just optimize for that.
                ForgeStep forgeStepForce1 = forgeSteps.removeLast();
                ForgeStep forgeStepForce2 = forgeSteps.removeLast();
                forgeSteps.add(recipe.last3Steps[0]);
                forgeSteps.add(forgeStepForce1);
                forgeSteps.add(forgeStepForce2);
                break;
            }
            default: {
                return TerraFirmaCraftAnvilSolution.UNDEFINED;
            }
        }
        int[] expectedValues = new int[forgeSteps.size()];
        ForgeStep[] forgeStepArray = forgeSteps.toArray(new ForgeStep[0]);
        int expectedValue = current;
        for (int i = 0; i < forgeStepArray.length; i++) {
            expectedValues[i] = expectedValue;
            expectedValue += forgeStepArray[i].step();
        }
        if (expectedValue != target) {
            throw new IllegalStateException("Invalid steps: " +
                    forgeSteps + " to get to " + target + " from " + current + " (It gets " + expectedValue + ")");
        }
        return new TerraFirmaCraftAnvilSolution(recipe, target, forgeStepArray, expectedValues);
    }
}
