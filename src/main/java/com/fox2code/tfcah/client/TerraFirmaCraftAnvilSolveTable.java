package com.fox2code.tfcah.client;

import com.fox2code.tfcah.TerraFirmaCraftAnvilHelper;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.dries007.tfc.common.capabilities.forge.ForgeStep;

import java.util.*;

public final class TerraFirmaCraftAnvilSolveTable {
    private static final Int2ObjectArrayMap<List<ForgeStep>> solvedMaps = new Int2ObjectArrayMap<>();
    private static final Comparator<ForgeStep> COMPARATOR = Comparator.comparingInt(a -> -Math.abs(a.step()));
    private static final ForgeStep[] VALUES = ForgeStep.values();

    private TerraFirmaCraftAnvilSolveTable() {}

    static {
        Arrays.sort(VALUES, COMPARATOR);
        solvedMaps.put(0, Collections.emptyList());
        for (int i = 0; i < VALUES.length; i++) {
            ForgeStep forgeStepOne = VALUES[i];
            solvedMaps.put(forgeStepOne.step(), Collections.singletonList(forgeStepOne));
            for (int i2 = i; i2 < VALUES.length; i2++) {
                ForgeStep forgeStepTwo = VALUES[i2];
                int diff = forgeStepOne.step() + forgeStepTwo.step();
                List<ForgeStep> forgeSteps = solvedMaps.get(diff);
                if (forgeSteps == null || forgeSteps.size() > 2) {
                    solvedMaps.put(diff, Arrays.asList(forgeStepOne, forgeStepTwo));
                }
                for (int i3 = i2; i3 < VALUES.length; i3++) {
                    ForgeStep forgeStepThree = VALUES[i3];
                    diff = forgeStepOne.step() + forgeStepTwo.step() + forgeStepThree.step();
                    forgeSteps = solvedMaps.get(diff);
                    if (forgeSteps == null || forgeSteps.size() > 3) {
                        solvedMaps.put(diff, Arrays.asList(forgeStepOne, forgeStepTwo, forgeStepThree));
                    }
                    for (int i4 = i3; i4 < VALUES.length; i4++) {
                        ForgeStep forgeStepFour = VALUES[i4];
                        diff = forgeStepOne.step() + forgeStepTwo.step() +
                                forgeStepThree.step() + forgeStepFour.step();
                        forgeSteps = solvedMaps.get(diff);
                        if (forgeSteps == null || forgeSteps.size() > 4) {
                            solvedMaps.put(diff, Arrays.asList(forgeStepOne, forgeStepTwo,
                                    forgeStepThree, forgeStepFour));
                        }
                    }
                }
            }
        }
    }

    public static List<ForgeStep> solveForSteps(int steps) {
        return solvedMaps.computeIfAbsent(steps, TerraFirmaCraftAnvilSolveTable::computeSolution);
    }

    private static List<ForgeStep> computeSolution(final int steps) {
        ForgeStep forgeStepBestSolve =
                steps < 0 ? ForgeStep.DRAW : ForgeStep.SHRINK;
        List<ForgeStep> listSource = solveForSteps(steps - forgeStepBestSolve.step());
        ArrayList<ForgeStep> forgeSteps = new ArrayList<>(listSource);
        forgeSteps.add(forgeStepBestSolve);
        forgeSteps.sort(COMPARATOR);
        return Collections.unmodifiableList(forgeSteps);
    }

    public static boolean checkSanity() {
        boolean sane = true;
        for (Int2ObjectMap.Entry<List<ForgeStep>> entry : solvedMaps.int2ObjectEntrySet()) {
            int diff = 0;
            for (ForgeStep forgeStep : entry.getValue()) {
                diff += forgeStep.step();
            }
            if (diff != entry.getIntKey()) {
                sane = false;
                TerraFirmaCraftAnvilHelper.getLOGGER().error(
                        "Sanity check failed, steps {} are registered for {} steps, but are actually {} steps",
                        entry.getValue(), entry.getIntKey(), diff);
            }
        }
        return sane;
    }
}
