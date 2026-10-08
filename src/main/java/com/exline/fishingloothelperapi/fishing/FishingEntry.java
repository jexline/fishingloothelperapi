package com.exline.fishingloothelperapi.fishing;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;

import java.util.function.Predicate;
import java.util.function.Supplier;

public record FishingEntry(Supplier<? extends Item> item, int weight, FishingType type, Predicate<Holder<Biome>> biomePredicate) {
    public enum FishingType {
        FISH,
        JUNK,
        TREASURE
    }

    public FishingEntry {
        if (item == null) throw new IllegalArgumentException("Fishing entry item cannot be null");
        if (weight <= 0) throw new IllegalArgumentException("Fishing entry weight must be greater than 0");
        if (type == null) throw new IllegalArgumentException("Fishing entry type cannot be null");
        if (biomePredicate == null) throw new IllegalArgumentException("Fishing entry biome predicate cannot be null");
    }

    public boolean canCatch(Holder<Biome> biome) {
        return biomePredicate.test(biome);
    }
}