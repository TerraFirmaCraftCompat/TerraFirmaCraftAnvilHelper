package com.fox2code.tfcah.client;

import net.dries007.tfc.common.component.forge.ForgeRule;
import net.dries007.tfc.common.component.forge.ForgeStep;

public final class ForgeRuleAccessor {
    private ForgeRuleAccessor() {}

    public static ForgeStep getForgeStep(ForgeRule forgeRule) {
        String name = forgeRule.name();
        if (name.startsWith("HIT_")) {
            return ForgeStep.HIT_LIGHT;
        } else if (name.startsWith("DRAW_")) {
            return ForgeStep.DRAW;
        } else if (name.startsWith("PUNCH_")) {
            return ForgeStep.PUNCH;
        } else if (name.startsWith("BEND_")) {
            return ForgeStep.BEND;
        } else if (name.startsWith("UPSET_")) {
            return ForgeStep.UPSET;
        } else if (name.startsWith("SHRINK_")) {
            return ForgeStep.SHRINK;
        } else throw new UnsupportedOperationException(name);
    }

    public static Order getOrder(ForgeRule forgeRule) {
        String name = forgeRule.name();
        if (name.endsWith("_ANY")) {
            return Order.ANY;
        } else if (name.endsWith("_NOT_LAST")) {
            return Order.NOT_LAST;
        } else if (name.endsWith("_SECOND_LAST")) {
            return Order.SECOND_LAST;
        } else if (name.endsWith("_THIRD_LAST")) {
            return Order.THIRD_LAST;
        } else if (name.endsWith("_LAST")) {
            return Order.LAST;
        } else throw new UnsupportedOperationException(name);
    }

    public enum Order {
        ANY,
        LAST,
        NOT_LAST,
        SECOND_LAST,
        THIRD_LAST;
    }
}
