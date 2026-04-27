package com.deltasf.createpropulsion.compat.kubejs;

import com.deltasf.createpropulsion.thruster.ThrusterFuelManager;

import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;

public class PropulsionKubeJSPlugin implements KubeJSPlugin {
    @Override
    public void registerBindings(BindingRegistry bindings) {
        bindings.add("ThrusterFuelManager", ThrusterFuelManager.class);
    }
}
