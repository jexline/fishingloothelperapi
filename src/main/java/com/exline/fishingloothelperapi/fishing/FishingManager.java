package com.exline.fishingloothelperapi.fishing;

import com.exline.fishingloothelperapi.registry.FishRegistry;
import com.exline.fishingloothelperapi.fishing.FishingEntry.FishingType;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;

import java.util.List;

public class FishingManager {

    private static final int VANILLA_FISH_WEIGHT = 100;
    private static final int VANILLA_JUNK_WEIGHT = 110;
    private static final int VANILLA_TREASURE_WEIGHT = 6;   

    public static FishingEntry getRandomEntry(FishingType type, RandomSource random, Holder<Biome> biome) {
        List<FishingEntry> entries = FishRegistry.getEntries(type).stream().filter(entry -> entry.canCatch(biome)).toList();

        if (entries.isEmpty()) {return null;}

        int vanillaWeight = getVanillaWeight(type);
        int customWeight = 0;

        for (FishingEntry entry : entries) {customWeight += entry.weight();}

        int totalWeight = vanillaWeight + customWeight;
        int roll = random.nextInt(totalWeight);

        if (roll < vanillaWeight) {return null;}

        roll -= vanillaWeight;

        for (FishingEntry entry : entries) {
            roll -= entry.weight();

            if (roll < 0) {return entry;}
        }

        return null;
    }

    private static int getVanillaWeight(FishingType type) {
        return switch (type) {
            case FISH -> VANILLA_FISH_WEIGHT;
            case JUNK -> VANILLA_JUNK_WEIGHT;
            case TREASURE -> VANILLA_TREASURE_WEIGHT;
        };
    }
}