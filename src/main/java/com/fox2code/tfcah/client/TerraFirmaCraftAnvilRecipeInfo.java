package com.fox2code.tfcah.client;

import net.dries007.tfc.common.component.forge.ForgeRule;
import net.dries007.tfc.common.component.forge.ForgeStep;
import net.dries007.tfc.common.recipes.AnvilRecipe;

import java.util.HashSet;

public final class TerraFirmaCraftAnvilRecipeInfo {
    private static final ForgeStep[] LAST3STEPS_INVALID = new ForgeStep[0];
    public final AnvilRecipe anvilRecipe;
    public final ForgeStep[] last3Steps;
    public final boolean invalid;

    private TerraFirmaCraftAnvilRecipeInfo(AnvilRecipe anvilRecipe, ForgeStep[] last3Steps, boolean invalid) {
        this.anvilRecipe = anvilRecipe;
        this.last3Steps = last3Steps;
        this.invalid = invalid;
    }

    public static TerraFirmaCraftAnvilRecipeInfo getRecipeInfo(AnvilRecipe anvilRecipe) {
        ForgeStep[] last3Steps = new ForgeStep[3];
        HashSet<ForgeStep> hitAny = new HashSet<>();
        HashSet<ForgeStep> hitNotLast = new HashSet<>();
        for (ForgeRule rule : anvilRecipe.getRules()) {
            ForgeStep forgeStep = ForgeRuleAccessor.getForgeStep(rule);
            switch (ForgeRuleAccessor.getOrder(rule)) {
                case ANY: {
                    hitAny.add(forgeStep);
                    break;
                }
                case LAST: {
                    last3Steps[2] = forgeStep;
                    break;
                }
                case NOT_LAST: {
                    hitNotLast.add(forgeStep);
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
                    // Unsolvable
                    return makeInvalid(anvilRecipe);
                }
            }
        }
        hitNotLast.remove(last3Steps[0]);
        hitNotLast.remove(last3Steps[1]);
        for (ForgeStep forgeStep : hitNotLast) {
            if (forgeStep != null &&
                    forgeStep != last3Steps[0] &&
                    forgeStep != last3Steps[1]) {
                if (last3Steps[1] == null) {
                    last3Steps[1] = forgeStep;
                } else if (last3Steps[0] == null) {
                    last3Steps[0] = forgeStep;
                } else {
                    // Unsolvable
                    return makeInvalid(anvilRecipe);
                }
            }
        }
        return new TerraFirmaCraftAnvilRecipeInfo(anvilRecipe, last3Steps, false);
    }

    private static TerraFirmaCraftAnvilRecipeInfo makeInvalid(AnvilRecipe anvilRecipe) {
        return new TerraFirmaCraftAnvilRecipeInfo(anvilRecipe, LAST3STEPS_INVALID, true);
    }
}
