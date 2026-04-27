package com.deltasf.createpropulsion.ponder;

import com.deltasf.createpropulsion.heat.burners.AbstractBurnerBlock;
import com.deltasf.createpropulsion.registries.PropulsionBlocks;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;

import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class StirlingEngineScene {

    public static void stirlingEngine(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("stirling_engine", "Powering a Stirling Engine");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        Selection burner = util.select().position(2, 1, 2);
        Selection engine = util.select().position(2, 2, 2);
        Selection output = util.select().fromTo(2, 3, 2, 4, 3, 2);

        scene.world().showSection(burner, Direction.DOWN);
        scene.world().showSection(engine, Direction.DOWN);
        scene.world().showSection(output, Direction.WEST);
        scene.world().setKineticSpeed(engine, 0);
        scene.world().setKineticSpeed(output, 0);
        scene.idle(10);

        scene.world().setBlock(util.grid().at(2, 1, 2), PropulsionBlocks.SOLID_BURNER.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH), true);
        scene.world().setBlock(util.grid().at(2, 2, 2), PropulsionBlocks.STIRLING_ENGINE_BLOCK.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH), true);
        scene.idle(20);

        scene.overlay().showText(80)
                .sharedText("stirling_engine.intro")
                .placeNearTarget()
                .pointAt(util.vector().topOf(util.grid().at(2, 2, 2)));
        scene.idle(100);

        scene.world().modifyBlock(util.grid().at(2, 1, 2),
                state -> state.setValue(AbstractBurnerBlock.HEAT, HeatLevel.KINDLED), false);
        scene.effects().indicateSuccess(util.grid().at(2, 1, 2));
        scene.idle(10);
        scene.world().setKineticSpeed(engine, 128);
        scene.world().setKineticSpeed(output, 128);
        scene.effects().rotationSpeedIndicator(util.grid().at(2, 2, 2));
        scene.effects().rotationSpeedIndicator(util.grid().at(3, 3, 2));

        scene.overlay().showText(80)
                .attachKeyFrame()
                .sharedText("stirling_engine.text_1")
                .placeNearTarget()
                .pointAt(util.vector().blockSurface(util.grid().at(2, 2, 2), Direction.EAST));
        scene.idle(90);

        scene.world().modifyBlock(util.grid().at(2, 1, 2),
                state -> state.setValue(AbstractBurnerBlock.HEAT, HeatLevel.NONE), false);
        scene.world().setKineticSpeed(engine, 0);
        scene.world().setKineticSpeed(output, 0);
        scene.effects().indicateSuccess(util.grid().at(2, 1, 2));
        scene.idle(10);

        scene.overlay().showText(80)
                .sharedText("stirling_engine.text_2")
                .placeNearTarget()
                .pointAt(util.vector().topOf(util.grid().at(2, 2, 2)));
        scene.idle(90);
        scene.markAsFinished();
    }
}
