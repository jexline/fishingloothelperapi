package com.exline.fishingloothelperapi.registry;

import com.exline.fishingloothelperapi.fishing.FishingEntry;
import com.exline.fishingloothelperapi.fishing.FishingEntry.FishingType;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class FishRegistry {

    private static final List<FishingEntry> ENTRIES = new ArrayList<>();

    private static void register(Supplier<? extends Item> item, int weight, FishingType type) {
        ENTRIES.add(new FishingEntry(item, weight, type, holder -> true));
    }

    private static void register(Supplier<? extends Item> item, int weight, FishingType type, ResourceKey<Biome>[] biomes) {
        Predicate<Holder<Biome>> biomePredicate = holder -> {
            if (biomes.length == 0) {return true;}

            for (ResourceKey<Biome> biome : biomes) {
                if (holder.is(biome)) {
                    return true;
                }
            }
            return false;
        };

        ENTRIES.add(new FishingEntry(item, weight, type, biomePredicate));
    }

    private static void register(Supplier<? extends Item> item, int weight, FishingType type, TagKey<Biome>[] biomeTags) {
        Predicate<Holder<Biome>> biomePredicate = holder -> {
            if (biomeTags.length == 0) {
                return true;
            }

            for (TagKey<Biome> tag : biomeTags) {
                if (holder.is(tag)) {
                    return true;
                }
            }
            return false;
        };

        ENTRIES.add(new FishingEntry(item, weight, type, biomePredicate));
    }

    //Register Custom Item as Fishing Loot, Junk or Treasure in one or more biomes
    @SafeVarargs
    public static void registerFishingLoot(Supplier<? extends Item> item, int weight, FishingType fishingType, ResourceKey<Biome>... biomes) {
        register(item, weight, fishingType, biomes);
    }
    //Register Custom Item as Fishing Loot, Junk or Treasure in one or more biome tags
    @SafeVarargs
    public static void registerFishingLoot(Supplier<? extends Item> item, int weight, FishingType fishingType, TagKey<Biome>... biomeTags) {
        register(item, weight, fishingType, biomeTags);
    }
    //Register Custom Item as Fishing Loot, Junk or Treasure in all biomes
    public static void registerFishingLoot(Supplier<? extends Item> item, int weight, FishingType fishingType) {
        register(item, weight, fishingType);
    }

    //Register Vanilla Item as Fishing Loot, Junk or Treasure, in one or more biomes
    @SafeVarargs
    public static void registerFishingLoot(Item item, int weight, FishingType fishingType, ResourceKey<Biome>... biomes) {
        register(() -> item, weight, fishingType, biomes);
    }
    //Register Vanilla Item as Fishing Loot, Junk or Treasure, in one or more biome tags
    @SafeVarargs
    public static void registerFishingLoot(Item item, int weight, FishingType fishingType, TagKey<Biome>... biomeTags) {
        register(() -> item, weight, fishingType, biomeTags);
    }
    //Register Vanilla Item as Fishing Loot, Junk or Treasure, in any biome
    public static void registerFishingLoot(Item item, int weight, FishingType fishingType) {
        register(() -> item, weight, fishingType);
    }

    public static List<FishingEntry> getEntries(FishingType type) {
        return ENTRIES.stream()
                .filter(entry -> entry.type() == type)
                .toList();
    }
}