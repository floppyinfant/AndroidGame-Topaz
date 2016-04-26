package com.floppyinfant.android.game.entities;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.entity.Entity;
import org.andengine.entity.particle.BatchedSpriteParticleSystem;
import org.andengine.entity.particle.Particle;
import org.andengine.entity.particle.ParticleSystem;
import org.andengine.entity.particle.emitter.PointParticleEmitter;
import org.andengine.entity.particle.initializer.AccelerationParticleInitializer;
import org.andengine.entity.particle.initializer.AlphaParticleInitializer;
import org.andengine.entity.particle.initializer.BlendFunctionParticleInitializer;
import org.andengine.entity.particle.initializer.ColorParticleInitializer;
import org.andengine.entity.particle.initializer.ExpireParticleInitializer;
import org.andengine.entity.particle.initializer.GravityParticleInitializer;
import org.andengine.entity.particle.initializer.RotationParticleInitializer;
import org.andengine.entity.particle.initializer.ScaleParticleInitializer;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.particle.modifier.ColorParticleModifier;
import org.andengine.entity.particle.modifier.IParticleModifier;
import org.andengine.entity.particle.modifier.RotationParticleModifier;
import org.andengine.entity.particle.modifier.ScaleParticleModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.sprite.UncoloredSprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.bitmap.BitmapTextureFormat;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.debug.Debug;

import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.opengl.GLES20;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.floppyinfant.android.game.manager.ResourceManager;

/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * @see ResourceManager!
 * This is just copied from there.
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * @author TM
 *
 */
public class EntityFactory {
	
	private static EntityFactory INSTANCE = new EntityFactory();
	
	private ResourceManager res = ResourceManager.getInstance();
	
	// -------------------------------------------------------------------------
	
	/**
	 * Singleton
	 */
	private EntityFactory() {
		
	}
	
	public static EntityFactory getInstance() {
		return INSTANCE;
	}
	
	/**
	 * setup
	 */
	/*
	public void setup(BaseGameActivity activity) {
		// set instance variables
		vbom = activity.getVertexBufferObjectManager();
	}
	*/
	
	// -------------------------------------------------------------------------
	
	/**
	 * factory method
	 */
	public Player createPlayer(float x, float y, ITiledTextureRegion texture) {
		
		Player player = new Player(x, y, texture);
		
		
		
		/* MultiFixtureBody */
		// @see AndEngine Cookbook, Ch. 6
		/*
		Rectangle nonbouncyBoxRect = new Rectangle(0f, 0f, 100f, 100f, res.vbom);
		nonbouncyBoxRect.setColor(0f, 0f, 0f);
		nonbouncyBoxRect.setAnchorCenter(
			((nonbouncyBoxRect.getWidth() / 2) - nonbouncyBoxRect.getX()) / nonbouncyBoxRect.getWidth(),
			((nonbouncyBoxRect.getHeight() / 2) - nonbouncyBoxRect.getY()) / nonbouncyBoxRect.getHeight()
		);
		//mScene.attachChild(nonbouncyBoxRect);
		
		Rectangle bouncyBoxRect = new Rectangle(0f, -55f, 90f, 10f, res.vbom);
		bouncyBoxRect.setColor(0f, 0.75f, 0f);
		bouncyBoxRect.setAnchorCenter(
			((bouncyBoxRect.getWidth() / 2) - bouncyBoxRect.getX()) / bouncyBoxRect.getWidth(),
			((bouncyBoxRect.getHeight() / 2) - bouncyBoxRect.getY()) / bouncyBoxRect.getHeight()
		);
		//mScene.attachChild(bouncyBoxRect);
		
		Body multiFixtureBody = res.physicsWorld.createBody(new BodyDef());
		multiFixtureBody.setType(BodyType.DynamicBody);
		
		FixtureDef nonbouncyBoxFixtureDef =   PhysicsFactory.createFixtureDef(20, 0.0f, 0.5f);
		final PolygonShape nonbouncyBoxShape = new PolygonShape();
		nonbouncyBoxShape.setAsBox(
			(nonbouncyBoxRect.getWidth() / 2f) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
		    (nonbouncyBoxRect.getHeight() / 2f) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 
		    new Vector2(
		    	nonbouncyBoxRect.getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
		    	nonbouncyBoxRect.getY() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT), 
		    0f);
		nonbouncyBoxFixtureDef.shape = nonbouncyBoxShape;
		multiFixtureBody.createFixture(nonbouncyBoxFixtureDef);
		res.physicsWorld.registerPhysicsConnector(new PhysicsConnector(nonbouncyBoxRect, multiFixtureBody));
		
		FixtureDef bouncyBoxFixtureDef = PhysicsFactory.createFixtureDef(20,  1f, 0.5f);
		final PolygonShape bouncyBoxShape = new PolygonShape();
		bouncyBoxShape.setAsBox(
			(bouncyBoxRect.getWidth() / 2f) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
		    (bouncyBoxRect.getHeight() / 2f) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 
		    new Vector2(
		    	bouncyBoxRect.getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
		    	bouncyBoxRect.getY() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT), 
		    0f);
		bouncyBoxFixtureDef.shape = bouncyBoxShape;
		multiFixtureBody.createFixture(bouncyBoxFixtureDef);
		res.physicsWorld.registerPhysicsConnector(new PhysicsConnector(bouncyBoxRect, multiFixtureBody));
		
		multiFixtureBody.setTransform(
				400f / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 
				240f / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 
				0f);
		*/
		
		
		
		/* MultiFixtureBody */
		// @see Learning AndEngine, Ch. 8
		
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
		body.setUserData(player);
		//body.setTransform(x, y, 0);
		
		res.physicsWorld.registerPhysicsConnector(new PhysicsConnector(player, body));
		player.setBody(body);
		
		
		
		// AnimatedSprite
		// TODO animate(): number of tiles ?
		player.animate(10);
		
		return player;
	}
	
	
	public static Entity createEnemy() {
		
		return null;
		
	}
	
	
	/* *************************************************************************
	 * Factories
	 * 
	 * @see /AndroidGame/src/com/floppyinfant/android/game/manager/ResourceManager.java
	 */
	
	/**
	 * Factory for Sprites in a PhysicsWorld
	 * 
	 * OBJECTS:<br />
	 * Sprite<br />
	 * TiledSprite<br />
	 * AnimatedSprite<br />
	 * <br />
	 * 
	 * METHODS:<br />
	 * TiledSprite.setCurrentTileIndex()<br />
	 * AnimatedSprite.animate()<br />
	 * <br />
	 * 
	 * NOTE:<br />
	 * Texture size must not exceed 1024x1024 px<br />
	 * <br />
	 * 
	 * @return Sprite
	 */
	public static Sprite createSprite(float x, float y, ITextureRegion texture, BodyType BODY_TYPE, FixtureDef FIXTURE_DEF) {
		// Physics DynamicBodies moved by forces
		
		Sprite sprite = new Sprite(x, y, texture, ResourceManager.getInstance().vbom);
		sprite.setScale(0.4f);
		
		Body body = PhysicsFactory.createBoxBody(ResourceManager.getInstance().physicsWorld, sprite, BODY_TYPE, FIXTURE_DEF);
		//this.attachChild(sprite);
		ResourceManager.getInstance().physicsWorld.registerPhysicsConnector(new PhysicsConnector(sprite, body, true, true));
		
		return sprite;
	}
	
	public static TiledSprite createTiledSprite() {
		TiledSprite sprite = null;
		
		// TODO
		
		return sprite;
	}
	
	public static AnimatedSprite createAnimatedSprite() {
		
		AnimatedSprite sprite = null;
		
		// TODO
		
		return sprite;
	}
	
	// -------------------------------------------------------------------------
	
	
	
	// -------------------------------------------------------------------------
	
	
	
	// -------------------------------------------------------------------------
	
	/**
	 * Physics
	 * 
	 * @see /AndroidGame/src/com/floppyinfant/android/game/scenes/levels/physics/AbstractGameScene.java
	 */
	public static void createPhysicsBody() {
		// TODO move to scene
		
		/* StaticBodies */
		/*
		// Entities
		Rectangle ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2, ResourceManager.getInstance().vbom);
		Rectangle roof = new Rectangle(0, 0, CAMERA_WIDTH, 2, ResourceManager.getInstance().vbom);
		Rectangle left = new Rectangle(0, 0, 2, CAMERA_HEIGHT, ResourceManager.getInstance().vbom);
		Rectangle right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT, ResourceManager.getInstance().vbom);
		// FixtureDef
		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		// BodyDef
		// StaticBodies
		PhysicsFactory.createBoxBody(ResourceManager.getInstance().physicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(ResourceManager.getInstance().physicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(ResourceManager.getInstance().physicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(ResourceManager.getInstance().physicsWorld, right, BodyType.StaticBody, wallFixtureDef);
		// attach Entities to Scene
		this.attachChild(ground);
		this.attachChild(roof);
		this.attachChild(left);
		this.attachChild(right);
		// apply Physics
		this.registerUpdateHandler(ResourceManager.physicsWorld);
		*/
		
		
		/* KinematicBodies */
		/*
		// KinematicBodies are not moved by forces, but by their set velocities
		final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		Sprite sprite = new Sprite(CAMERA_WIDTH, CAMERA_HEIGHT / 5, ResourceManager.schneckiTextureRegion, ResourceManager.getInstance().vbom);
		sprite.setScale(0.4f);
		Body body = PhysicsFactory.createBoxBody(ResourceManager.physicsWorld, sprite, BodyType.KinematicBody, FIXTURE_DEF);
		this.attachChild(sprite);
		ResourceManager.physicsWorld.registerPhysicsConnector(new PhysicsConnector(sprite, body, true, true));
		// move the kinematic body
		body.setLinearVelocity(-1.5f, 0f);
		body.setAngularVelocity((float) (-Math.PI));
		*/
		
		
		/* DynamicBodies */
		/* 
		// DynamicBodies are moved by forces
		final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		VertexBufferObjectManager vertexBufferObjectManager = ResourceManager.getInstance().engine.getVertexBufferObjectManager();
		Sprite sprite = new Sprite(pSceneTouchEvent.getX(), pSceneTouchEvent.getY(), ResourceManager.eselTextureRegion, vertexBufferObjectManager);
		sprite.setScale(0.4f);
		Body body = PhysicsFactory.createBoxBody(ResourceManager.physicsWorld, sprite, BodyType.DynamicBody, FIXTURE_DEF);
		this.attachChild(sprite);
		ResourceManager.getInstance().physicsWorld.registerPhysicsConnector(new PhysicsConnector(sprite, body, true, true));
		*/
		
	}
}
