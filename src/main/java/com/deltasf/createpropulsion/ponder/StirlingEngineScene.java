package com.deltasf.createpropulsion.ponder;

import com.deltasf.createpropulsion.heat.burners.AbstractBurnerBlock;
import com.deltasf.createpropulsion.heat.burners.solid.SolidBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class StirlingEngineScene {
    public static void stirlingEngine(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("stirling_engine", "Powering a Stirling Engine");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        BlockPos burnerPos = util.grid().at(2, 1, 2);
        BlockPos enginePos = util.grid().at(2, 2, 2);
        BlockPos shaftPos = util.grid().at(3, 2, 2);
        BlockPos leverPos = util.grid().at(1, 2, 2);

        Selection burnerSel = util.select().position(burnerPos);
        Selection engineSel = util.select().position(enginePos);
        Selection shaftSel = util.select().position(shaftPos);
        Selection leverSel = util.select().position(leverPos);

        scene.world().showSection(burnerSel, Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(engineSel, Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(shaftSel, Direction.WEST);
        scene.idle(15);

        scene.overlay().showText(70)
            .sharedText("solid_burner.intro")
            .pointAt(util.vector().blockSurface(burnerPos, Direction.WEST))
            .placeNearTarget();
        scene.idle(80);

        scene.overlay().showControls(util.vector().blockSurface(burnerPos, Direction.WEST), Pointing.LEFT, 40)
            .rightClick()
            .withItem(new ItemStack(Items.COAL));
        scene.idle(20);

        scene.world().modifyBlock(burnerPos, s -> s.setValue(SolidBurnerBlock.LIT, true), false);
        scene.world().modifyBlock(burnerPos, s -> s.setValue(AbstractBurnerBlock.HEAT, HeatLevel.KINDLED), false);
        scene.world().setKineticSpeed(engineSel.add(shaftSel), 96);
        scene.effects().indicateSuccess(enginePos);
        scene.idle(10);

        scene.overlay().showText(70)
            .sharedText("burner.activation")
            .pointAt(util.vector().centerOf(enginePos))
            .placeNearTarget();
        scene.idle(80);

        scene.world().showSection(leverSel, Direction.EAST);
        scene.idle(20);

        scene.overlay().showText(70)
            .colored(PonderPalette.RED)
            .sharedText("burner.redstone_thermostat")
            .pointAt(util.vector().blockSurface(enginePos, Direction.WEST))
            .placeNearTarget();
        scene.idle(80);

        scene.world().toggleRedstonePower(leverSel);
        scene.effects().indicateRedstone(leverPos);
        scene.world().setKineticSpeed(engineSel.add(shaftSel), 0);
        scene.idle(40);

        scene.world().toggleRedstonePower(leverSel);
        scene.effects().indicateRedstone(leverPos);
        scene.world().setKineticSpeed(engineSel.add(shaftSel), 96);
        scene.effects().indicateSuccess(enginePos);
        scene.idle(60);

        scene.markAsFinished();
    }
}
