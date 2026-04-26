package com.deltasf.createpropulsion.particles.plasma;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;

import net.minecraft.client.particle.ParticleEngine.SpriteParticleRegistration;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import com.deltasf.createpropulsion.particles.ParticleTypes;

public class PlasmaParticleData implements ParticleOptions, ICustomParticleDataWithSprite<PlasmaParticleData> {
    public PlasmaParticleData() {}

    @Override
    public ParticleType<?> getType(){
        return ParticleTypes.getPlasmaType();
    }

    public MapCodec<PlasmaParticleData> getCodec(ParticleType<PlasmaParticleData> type) {
        return MapCodec.unit(new PlasmaParticleData());
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, PlasmaParticleData> getStreamCodec() {
        return StreamCodec.unit(new PlasmaParticleData());
    }

    @Override
    public SpriteParticleRegistration<PlasmaParticleData> getMetaFactory() {
        return PlasmaParticle.Factory::new;
    }
}
