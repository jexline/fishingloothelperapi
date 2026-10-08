package com.exline.fishingloothelperapi.loot;

import com.exline.fishingloothelperapi.fishing.FishingEntry;
import com.exline.fishingloothelperapi.fishing.FishingManager;
import com.exline.fishingloothelperapi.fishing.FishingEntry.FishingType;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

/*
This checks the result of generated fishing loot and determines whether the item is a fish or junk.
It also gets the player’s fishing location and the biome they are fishing in,
then uses FishingManager.javato select a random registered FishingEntry that matches that fishing type and biome.
If it matches, the vanilla loot is replaced with the added loot.
 */

public class FishingLootModifier extends LootModifier {

    public static final MapCodec<FishingLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
            LootModifier.codecStart(instance)
                    .apply(instance, FishingLootModifier::new)
    );

    public FishingLootModifier(Optional<Holder<LootItemCondition>> condition, int priority) {
        super(condition, priority);
    }

    @Override
    public @NonNull MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (generatedLoot.size() != 1) {
            return generatedLoot;
        }

        ItemStack stack = generatedLoot.getFirst();
        FishingType type = getFishingType(stack);

        if (type == null) {
            return generatedLoot;
        }

        var origin = context.getOptional(LootContextParams.ORIGIN);

        if (origin == null) {
            return generatedLoot;
        }

        BlockPos pos = BlockPos.containing(origin);
        Holder<Biome> biome = context.getLevel().getBiome(pos);

        FishingEntry entry = FishingManager.getRandomEntry(type, context.getRandom(), biome);

        if (entry == null) {
            return generatedLoot;
        }

        generatedLoot.clear();
        generatedLoot.add(new ItemStack(entry.item().get()));

        return generatedLoot;
    }

    private static FishingType getFishingType(ItemStack stack) {
        Item item = stack.getItem();

        if (item == Items.COD ||
                item == Items.SALMON ||
                item == Items.TROPICAL_FISH ||
                item == Items.PUFFERFISH) {
            return FishingType.FISH;
        }

        if (item == Items.LILY_PAD ||
                item == Items.LEATHER_BOOTS ||
                item == Items.LEATHER ||
                item == Items.BONE ||
                item == Items.POTION ||
                item == Items.STRING ||
                item == Items.BOWL ||
                item == Items.STICK ||
                item == Items.INK_SAC ||
                item == Items.TRIPWIRE_HOOK ||
                item == Items.ROTTEN_FLESH ||
                item == Items.BAMBOO) {
            return FishingType.JUNK;
        }

        if (item == Items.FISHING_ROD) {
            if (stack.has(net.minecraft.core.component.DataComponents.ENCHANTMENTS)) {
                return FishingType.TREASURE;
            }

            return FishingType.JUNK;
        }

        if (item == Items.NAME_TAG ||
                item == Items.SADDLE ||
                item == Items.BOW ||
                item == Items.ENCHANTED_BOOK ||
                item == Items.NAUTILUS_SHELL) {
            return FishingType.TREASURE;
        }

        return null;
    }
}