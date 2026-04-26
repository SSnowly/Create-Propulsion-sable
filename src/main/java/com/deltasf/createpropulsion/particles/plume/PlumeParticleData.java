package com.deltasf.createpropulsion.particles.plume;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;

import net.minecraft.client.particle.ParticleEngine.SpriteParticleRegistration;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import com.deltasf.createpropulsion.particles.ParticleTypes;

public class PlumeParticleData implements ParticleOptions, ICustomParticleDataWithSprite<PlumeParticleData> {
    public PlumeParticleData() {}

    @Override
    public ParticleType<?> getType(){
        return ParticleTypes.getPlumeType();
    }

    public MapCodec<PlumeParticleData> getCodec(ParticleType<PlumeParticleData> type) {
		return MapCodec.unit(new PlumeParticleData());
	}

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, PlumeParticleData> getStreamCodec() {
        return StreamCodec.unit(new PlumeParticleData());
    }

    @Override
	public SpriteParticleRegistration<PlumeParticleData> getMetaFactory() {
        return PlumeParticle.Factory::new;
	}
}
