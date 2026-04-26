package com.deltasf.createpropulsion.registries;

import com.deltasf.createpropulsion.heat.IHeatConsumer;
import com.deltasf.createpropulsion.heat.IHeatSource;
import com.deltasf.createpropulsion.CreatePropulsion;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;

public class PropulsionCapabilities {
    public static final BlockCapability<IHeatSource, Direction> HEAT_SOURCE =
        BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(CreatePropulsion.ID, "heat_source"), IHeatSource.class);

    public static final BlockCapability<IHeatConsumer, Direction> HEAT_CONSUMER =
        BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(CreatePropulsion.ID, "heat_consumer"), IHeatConsumer.class);
}
