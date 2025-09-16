package com.fox2code.tfcah.client;

import net.dries007.tfc.common.component.forge.ForgeRule;
import net.dries007.tfc.common.component.forge.ForgeStep;
import net.dries007.tfc.common.recipes.AnvilRecipe;

import java.util.HashSet;

public final class TerraFirmaCraftAnvilRecipeInfo {
    public final AnvilRecipe anvilRecipe;
    public final boolean notLastStep;
    public final ForgeStep[] last3Steps;

    private TerraFirmaCraftAnvilRecipeInfo(AnvilRecipe anvilRecipe, boolean notLastStep, ForgeStep[] last3Steps) {
        this.anvilRecipe = anvilRecipe;
        this.notLastStep = notLastStep;
        this.last3Steps = last3Steps;
    }

    public static TerraFirmaCraftAnvilRecipeInfo getRecipeInfo(AnvilRecipe anvilRecipe) {
        ForgeStep[] last3Steps = new ForgeStep[3];
        HashSet<ForgeStep> hitAny = new HashSet<>();
        boolean notLastStep = true;
        for (ForgeRule rule : anvilRecipe.getRules()) {
            ForgeStep forgeStep = ForgeRuleAccessor.getForgeStep(rule);
            switch (ForgeRuleAccessor.getOrder(rule)) {
                case ANY: {
                    hitAny.add(forgeStep);
                    break;
                }
                case LAST: {
                    last3Steps[2] = forgeStep;
                    notLastStep = false;
                    break;
                }
                case NOT_LAST: {
                    if (notLastStep) {
                        last3Steps[2] = forgeStep;
                    }
                    hitAny.add(forgeStep);
                    break;
                }
                case THIRD_LAST: {
                    last3Steps[0] = forgeStep;
                    break;
                }
                case SECOND_LAST: {
                    last3Steps[1] = forgeStep;
                    break;
                }
            }
        }
        for (ForgeStep forgeStep : last3Steps) {
            if (forgeStep != null) {
                hitAny.remove(forgeStep);
            }
        }
        for (ForgeStep forgeStep : hitAny) {
            for (int i = 2; i >= 0; i--) {
                if (last3Steps[i] == null) {
                    last3Steps[i] = forgeStep;
                    break;
                }
                if (i == 0) {
                    if (notLastStep && last3Steps[2] != forgeStep) {
                        last3Steps[2] = forgeStep;
                    } else {
                        // Unsolvable
                        return null;
                    }
                }
            }
        }
        if (last3Steps[2] == null) {
            notLastStep = false;
        }
        // Support notLastStep.
        if (notLastStep) {
            if (last3Steps[1] == null) {
                last3Steps[1] = last3Steps[2];
                last3Steps[2] = null;
                notLastStep = false;
            } else if (last3Steps[0] == null) {
                last3Steps[0] = last3Steps[2];
                last3Steps[2] = null;
                notLastStep = false;
            }
        }
        return new TerraFirmaCraftAnvilRecipeInfo(anvilRecipe, notLastStep, last3Steps);
    }
}
