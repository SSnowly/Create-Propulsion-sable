package com.deltasf.createpropulsion.registries;

import com.deltasf.createpropulsion.CreatePropulsion;
import com.deltasf.createpropulsion.utility.BurnableItem;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PropulsionItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreatePropulsion.ID);

    public static final DeferredHolder<Item, BurnableItem> PINE_RESIN =
        ITEMS.register("pine_resin", () -> new BurnableItem(new Item.Properties(), 1200));

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}
