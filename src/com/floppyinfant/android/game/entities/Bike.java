package com.floppyinfant.android.game.entities;

import org.andengine.entity.Entity;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.floppyinfant.android.game.manager.ResourceManager;


/**
 * Player Entity<br />
 * <br />
 * 
 * <b>OBJECTS:</b><br />
 * Sprite<br />
 * TiledSprite.setCurrentTileIndex() // e.g. toggle button<br />
 * AnimatedSprite.animate()<br />
 * ButtonSprite // TouchArea<br />
 * <br />
 * 
 * <b>EntityModifiers:</b><br />
 * SequenceEntityModifier(modifier, ...)<br />
 * ParallelEntityModifier(modifier, ...)<br />
 * LoopEntityModifier(modifier [, ...])<br />
 * <br />
 * 
 * <b>Modifiers:</b><br />
 * MoveModifier, MoveByModifier, MoveXModifier, MoveYModifier<br />
 * JumpModifier<br />
 * PathModifier, CardinalSplineMoveModifier<br />
 * <br />
 * RotationModifier, RotationByModifier, RotationAtModifier<br />
 * ScaleModifier, ScaleAtModifier<br />
 * SkewModifier<br />
 * <br />
 * AlphaModifier, FadeInModifier, FadeOutModifier<br />
 * ColorModifier<br />
 * <br />
 * DelayModifier<br />
 * <br />
 * 
 * <b>Ease Functions:</b><br />
 * new *Modifier(..., org.andengine.util.modifier.ease.*.getInstance())<br />
 * <br />
 * 
 * <b>Modifiers Listeners:</b><br />
 * new IEntityModifierListener() {onModifierStarted(){...}; onModifierFinished(){...}}<br />
 * modifier.addModifierListener(listener);<br />
 * <br />
 * 
 * @author TM
 *
 */
public class Bike extends AnimatedSprite implements ICollidableEntity {
	
	private ResourceManager res = ResourceManager.getInstance();
	
	private Body mBody;
	
	private String mType;
		
	/**
	 * 
	 * 
	 * @param x
	 * @param y
	 * @param texture
	 */
	public Bike(float x, float y, ITiledTextureRegion texture) {
		super(x, y, texture, ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		
		
		/* MultiFixtureBody */
		// @see Learning AndEngine, Ch. 8
		/*
		// BodyDef
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody; // body.setType()
		bodyDef.position.x = x / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		bodyDef.position.y = y / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		
		// Body
		Body body = res.physicsWorld.createBody(bodyDef);
		
		// FixtureDef
		FixtureDef headFixtureDef = PhysicsFactory.createFixtureDef(1f, 0f, 1f); // Sensor ?
		
		// Shape
		CircleShape circle = new CircleShape();
		circle.setRadius(32 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
		circle.setPosition(new Vector2(0, 12 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT));
		
		headFixtureDef.shape = circle;
		
		// Fixture
		body.createFixture(headFixtureDef);
		
		// next FixtureDef .. body.createFixture(fixtureDef)
		
		body.setLinearDamping(1f);
		body.setFixedRotation(true);
		body.setUserData(this);
		//body.setTransform(x, y, 0);
		
		res.physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body));
		setBody(body);
		
		// AnimatedSprite
		// TODO animate(): number of tiles ?
		animate(10);
		*/
	}
	
	// -------------------------------------------------------------------------
	
	@Override
	public void setBody(Body body) {
		mBody = body;
	}


	@Override
	public Body getBody() {
		return mBody;
	}


	@Override
	public String getType() {
		return mType;
	}
	
	/* *************************************************************************
	 * Modifier
	 */
	
	public void gas(float impulse) {
		
	}
	
	public void move(float x, float y) {
		clearEntityModifiers();
		registerEntityModifier(new MoveModifier(1, getX(), getY(), x, y));
	}
	
	public void wheely() {
		
	}
	
	public void bunnyHop() {
		
	}
	
	// -------------------------------------------------------------------------
	
	public void onCollision() {
		// for n-seconds
		// blink alpha
		// invincible
		// set life--
	}
	
	public void die() {
		// TODO
		
		ResourceManager.getInstance().activity.runOnUpdateThread(new Runnable() {
			
			@Override
			public void run() {
				//player.detachSelf();
			}
		});
	}
	
	/* *************************************************************************
	 * Collision Detection
	 */
	
	/**
	 * called by ContactListener
	 */
	public boolean isBodyContacted(Body pBody, Contact pContact) {
		if (pContact.getFixtureA().getBody().equals(pBody) || pContact.getFixtureB().getBody().equals(pBody)) {
			return true;
		}
		return false;
	}
	
	/**
	 * called by ContactListener
	 */
	public boolean areBodiesContacted(Body pBody1, Body pBody2, Contact pContact) {
		if (pContact.getFixtureA().getBody().equals(pBody1) || pContact.getFixtureB().getBody().equals(pBody1)) {
			if (pContact.getFixtureA().getBody().equals(pBody2) || pContact.getFixtureB().getBody().equals(pBody2)) {
				return true;
			}
		}
		return false;
	}
	
	/* *************************************************************************
	 * Overriden
	 */
	
	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		super.onManagedUpdate(pSecondsElapsed);
	}
	
	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
	}

}
