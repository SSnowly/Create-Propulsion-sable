package com.deltasf.createpropulsion;

import com.deltasf.createpropulsion.registries.*;

import com.deltasf.createpropulsion.compat.computercraft.CCProxy;
import com.deltasf.createpropulsion.events.ModCapabilityEvents;
import com.deltasf.createpropulsion.network.PropulsionPackets;
import com.deltasf.createpropulsion.particles.ParticleTypes;
import com.simibubi.create.compat.Mods;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(CreatePropulsion.ID)
public class CreatePropulsion {
    public static final String ID = "createpropulsion";

    public CreatePropulsion(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(ModCapabilityEvents::registerCapabilities);
        //Content
        ParticleTypes.register(modBus);
        PropulsionBlocks.register(modBus);
        PropulsionBlockEntities.register(modBus);
        PropulsionItems.register(modBus);
        PropulsionFluids.register();
        PropulsionPartialModels.register();
        PropulsionCreativeTab.register(modBus);
        PropulsionPackets.register();
        PropulsionDisplaySources.register();
        PropulsionSableBridge.init();

        //Compat
        Mods.COMPUTERCRAFT.executeIfInstalled(() -> CCProxy::register);

        //Config
        modContainer.registerConfig(ModConfig.Type.SERVER, PropulsionConfig.SERVER_SPEC, ID + "-server.toml");
        modContainer.registerConfig(ModConfig.Type.CLIENT, PropulsionConfig.CLIENT_SPEC, ID + "-client.toml");
        PropulsionDefaultStress.init(PropulsionConfig.SERVER_SPEC);
    }
}
