package com.deltasf.createpropulsion.ponder;

import javax.annotation.Nonnull;

import com.deltasf.createpropulsion.CreatePropulsion;
import com.deltasf.createpropulsion.registries.PropulsionBlocks;

import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class DeltaPonderPlugin implements PonderPlugin {
    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        final PonderSceneRegistrationHelper<Block> HELPER = helper.withKeyFunction(BuiltInRegistries.BLOCK::getKey);
        //Burners
        HELPER.forComponents(PropulsionBlocks.SOLID_BURNER.get()).addStoryBoard("solid_burner", BurnerScenes::solidBurner);
        HELPER.forComponents(PropulsionBlocks.LIQUID_BURNER.get()).addStoryBoard("liquid_burner", BurnerScenes::liquidBurner);
        //Stirling engine
        HELPER.forComponents(PropulsionBlocks.STIRLING_ENGINE_BLOCK.get()).addStoryBoard("stirling_engine", StirlingEngineScene::stirlingEngine);
        //Transmission
        HELPER.forComponents(PropulsionBlocks.REDSTONE_TRANSMISSION_BLOCK.get())
                .addStoryBoard("redstone_transmission", TransmissionScenes::directControl)
                .addStoryBoard("redstone_transmission", TransmissionScenes::incrementalControl);
    }

    @Override
	public String getModId() {
		return CreatePropulsion.ID;
	}

	@Override
	public void registerScenes(@Nonnull PonderSceneRegistrationHelper<ResourceLocation> helper) {
		register(helper);
	}
}