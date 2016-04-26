package com.floppyinfant.android.game.entities;

import org.andengine.entity.IEntity;

import com.badlogic.gdx.physics.box2d.Body;

/**
 * @see Learning AndEngine, PacktPub
 * 
 * @author TM
 *
 */
public interface ICollidableEntity extends IEntity {
	public void setBody(Body body);
	public Body getBody();
	public String getType();
}
