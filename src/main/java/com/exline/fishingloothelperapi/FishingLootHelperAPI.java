package com.exline.fishingloothelperapi;

import com.exline.fishingloothelperapi.registry.ModLootModifiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(FishingLootHelperAPI.MOD_ID)
public class FishingLootHelperAPI {
    public static final String MOD_ID = "fishingloothelperapi";

    public FishingLootHelperAPI(IEventBus modEventBus) {
        ModLootModifiers.register(modEventBus);
    }
}