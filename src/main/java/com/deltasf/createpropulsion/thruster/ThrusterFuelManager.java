package com.deltasf.createpropulsion.thruster;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.deltasf.createpropulsion.CreatePropulsion;
import com.deltasf.createpropulsion.PropulsionConfig;
import com.deltasf.createpropulsion.network.PropulsionPackets;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import com.deltasf.createpropulsion.network.SyncThrusterFuelsPacket;

public class ThrusterFuelManager extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final String DIRECTORY = "thruster_fuels";

    private static Map<Fluid, FluidThrusterProperties> fuelPropertiesMap = new HashMap<>();
    public static final TagKey<Fluid> FORGE_FUEL_TAG = TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath("forge", "fuel"));

    public static Map<Fluid, FluidThrusterProperties> getFuelPropertiesMap() { return fuelPropertiesMap; }

    public ThrusterFuelManager() {
        super(GSON, DIRECTORY);
    }

    @Nullable
    @SuppressWarnings("deprecation")
    public static FluidThrusterProperties getProperties(Fluid fluid) {
        if (fluid == null || fluid == Fluids.EMPTY) return null;
        if (fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA) {
            return FluidThrusterProperties.DEFAULT;
        }
        FluidThrusterProperties props = fuelPropertiesMap.get(fluid);
        if (props != null) {
            return props;
        }
        if (fluid.is(FORGE_FUEL_TAG)) return FluidThrusterProperties.DEFAULT;
        return null;
    }

    public static float getEfficiency(Fluid fluid) {
        if (fluid == null || fluid == Fluids.EMPTY) {
            return PropulsionConfig.FUEL_DEFAULT_EFFICIENCY.get().floatValue();
        }

        // Normalize flowing variants (e.g. flowing_lava) to their source fluid ids.
        fluid = FluidHelper.convertToStill(fluid);

        Map<ResourceLocation, Float> fluidEfficiencyOverrides = getConfiguredEfficiencyOverrides();

        ResourceLocation fluidId = BuiltInRegistries.FLUID.getKey(fluid);
        if (fluidId == null) {
            return PropulsionConfig.FUEL_DEFAULT_EFFICIENCY.get().floatValue();
        }

        return fluidEfficiencyOverrides.getOrDefault(fluidId, PropulsionConfig.FUEL_DEFAULT_EFFICIENCY.get().floatValue());
    }

    @Override
    protected void apply(@Nonnull Map<ResourceLocation, JsonElement> pObject, @Nonnull ResourceManager resourceManager, @Nonnull ProfilerFiller profiler) {
        //Parse datapacks
        profiler.push(CreatePropulsion.ID + ":Loading_thruster_fuels");
        fuelPropertiesMap = parseFuelProperties(pObject);
        profiler.pop();
        //Update clients (happens only on /reload as on server start server instance is still null)
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null && server.isRunning()) {
            PropulsionPackets.sendToAll(SyncThrusterFuelsPacket.create(fuelPropertiesMap));
        }
    }

    public static void updateClient(Map<ResourceLocation, FluidThrusterProperties> fuelMap) {
        Map<Fluid, FluidThrusterProperties> newClientMap = new HashMap<>();
        fuelMap.forEach((rl, props) -> {
            Fluid fluid = BuiltInRegistries.FLUID.get(rl);
            if (fluid != null) {
                newClientMap.put(fluid, props);
            }
        });
        fuelPropertiesMap = newClientMap;
    }

    private Map<Fluid, FluidThrusterProperties> parseFuelProperties(@Nonnull Map<ResourceLocation, JsonElement> pObject) {
        Map<Fluid, FluidThrusterProperties> newMap = new HashMap<>();

        for (Map.Entry<ResourceLocation, JsonElement> entry : pObject.entrySet()) {
            ResourceLocation file = entry.getKey();
            JsonElement json = entry.getValue();

            //Parse fuel def
            ThrusterFuelDefinition.CODEC.parse(JsonOps.INSTANCE, json)
                .resultOrPartial(error -> {LOGGER.error("[{}] Failed to parse thruster fuel definition from {}: {}", CreatePropulsion.ID, file, error);})
                .ifPresent(definition -> {
                    //There is a fuel that requires a mod but the mod is not present
                    if (definition.requiredMod().isPresent() && !ModList.get().isLoaded(definition.requiredMod().get())) {
                        return;
                    }
                    Fluid fluid = definition.getFluid();
                    //Fluid is not in registry
                    if (fluid == Fluids.EMPTY) {
                        return;
                    }
                    //Successfully load fuel
                    FluidThrusterProperties properties = new FluidThrusterProperties(
                        definition.thrustMultiplier(), 
                        definition.consumptionMultiplier());
                    newMap.put(fluid, properties);
                });
        }
        
        return newMap;
    }

    private static Map<ResourceLocation, Float> getConfiguredEfficiencyOverrides() {
        Map<ResourceLocation, Float> overrides = new HashMap<>();
        putEfficiencyOverride(overrides, "minecraft:lava", PropulsionConfig.FUEL_EFFICIENCY_LAVA.get());
        putEfficiencyOverride(overrides, "createpropulsion:turpentine", PropulsionConfig.FUEL_EFFICIENCY_TURPENTINE.get());
        putEfficiencyOverride(overrides, "createdieselgenerators:diesel", PropulsionConfig.FUEL_EFFICIENCY_CDG_DIESEL.get());
        putEfficiencyOverride(overrides, "createdieselgenerators:gasoline", PropulsionConfig.FUEL_EFFICIENCY_CDG_GASOLINE.get());
        putEfficiencyOverride(overrides, "createdieselgenerators:ethanol", PropulsionConfig.FUEL_EFFICIENCY_CDG_ETHANOL.get());
        putEfficiencyOverride(overrides, "createdieselgenerators:biodiesel", PropulsionConfig.FUEL_EFFICIENCY_CDG_BIODIESEL.get());
        putEfficiencyOverride(overrides, "createdieselgenerators:plant_oil", PropulsionConfig.FUEL_EFFICIENCY_CDG_PLANT_OIL.get());
        putEfficiencyOverride(overrides, "tfmg:diesel", PropulsionConfig.FUEL_EFFICIENCY_TFMG_DIESEL.get());
        putEfficiencyOverride(overrides, "tfmg:gasoline", PropulsionConfig.FUEL_EFFICIENCY_TFMG_GASOLINE.get());
        putEfficiencyOverride(overrides, "tfmg:kerosene", PropulsionConfig.FUEL_EFFICIENCY_TFMG_KEROSENE.get());
        putEfficiencyOverride(overrides, "tfmg:naphtha", PropulsionConfig.FUEL_EFFICIENCY_TFMG_NAPHTHA.get());
        putEfficiencyOverride(overrides, "immersiveengineering:biodiesel", PropulsionConfig.FUEL_EFFICIENCY_IE_BIODIESEL.get());
        putEfficiencyOverride(overrides, "immersiveengineering:ethanol", PropulsionConfig.FUEL_EFFICIENCY_IE_ETHANOL.get());
        putEfficiencyOverride(overrides, "immersiveengineering:plant_oil", PropulsionConfig.FUEL_EFFICIENCY_IE_PLANT_OIL.get());
        putEfficiencyOverride(overrides, "immersivepetroleum:diesel", PropulsionConfig.FUEL_EFFICIENCY_IP_DIESEL.get());
        putEfficiencyOverride(overrides, "immersivepetroleum:diesel_sulfur", PropulsionConfig.FUEL_EFFICIENCY_IP_DIESEL_SULFUR.get());
        putEfficiencyOverride(overrides, "immersivepetroleum:gasoline", PropulsionConfig.FUEL_EFFICIENCY_IP_GASOLINE.get());
        putEfficiencyOverride(overrides, "mekanism:hydrogen", PropulsionConfig.FUEL_EFFICIENCY_MEKANISM_HYDROGEN.get());
        putEfficiencyOverride(overrides, "mekanismgenerators:bioethanol", PropulsionConfig.FUEL_EFFICIENCY_MEKANISM_GENERATORS_BIOETHANOL.get());
        putEfficiencyOverride(overrides, "northstar:biofuel", PropulsionConfig.FUEL_EFFICIENCY_NORTHSTAR_BIOFUEL.get());
        putEfficiencyOverride(overrides, "northstar:hydrocarbon", PropulsionConfig.FUEL_EFFICIENCY_NORTHSTAR_HYDROCARBON.get());
        putEfficiencyOverride(overrides, "northstar:methane", PropulsionConfig.FUEL_EFFICIENCY_NORTHSTAR_METHANE.get());
        putEfficiencyOverride(overrides, "northstar:liquid_hydrogen", PropulsionConfig.FUEL_EFFICIENCY_NORTHSTAR_LIQUID_HYDROGEN.get());
        putEfficiencyOverride(overrides, "stellaris:fuel", PropulsionConfig.FUEL_EFFICIENCY_STELLARIS_FUEL.get());
        putEfficiencyOverride(overrides, "stellaris:diesel", PropulsionConfig.FUEL_EFFICIENCY_STELLARIS_DIESEL.get());
        return overrides;
    }

    private static void putEfficiencyOverride(Map<ResourceLocation, Float> map, String fluidId, double value) {
        ResourceLocation rl = ResourceLocation.tryParse(fluidId);
        if (rl == null) {
            LOGGER.warn("[{}] Invalid fluid id '{}' in hardcoded config mapping.", CreatePropulsion.ID, fluidId);
            return;
        }
        map.put(rl, (float) value);
    }
}
