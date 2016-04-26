package com.floppyinfant.android.game.scenes.levels.rube;

import org.andengine.extension.rubeloader.factory.EntityFactory;

/**
 * Extend EntityFactory if you need more fine-grained control over creation of entities
 * that are to be connected to bodies. Basic EntityFactory creates UncoloredSprite for
 * every Entity (which you might not like).
 *
 * EntityFactory::produce() might be a good place to create textured polygons if you need them.
 */
public class RubeEntityFactory extends EntityFactory {
	
}
