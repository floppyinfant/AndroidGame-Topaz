package com.floppyinfant.android.game.scenes.levels.physics;

import java.util.Locale;

import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.entity.particle.BatchedSpriteParticleSystem;
import org.andengine.entity.particle.Particle;
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
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.UncoloredSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.PinchZoomDetector;
import org.andengine.input.touch.detector.PinchZoomDetector.IPinchZoomDetectorListener;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.hardware.SensorManager;
import android.opengl.GLES20;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.floppyinfant.android.game.entities.ICollidableEntity;
import com.floppyinfant.android.game.manager.ResourceManager;
import com.floppyinfant.android.game.manager.SceneManager;
import com.floppyinfant.android.game.scenes.ManagedScene;

public abstract class AbstractGameScene extends ManagedScene implements IAccelerationListener, IOnSceneTouchListener, IPinchZoomDetectorListener, ContactListener {
	
	protected long startTime = System.currentTimeMillis();
	
	public HUD mHUD;
	public AbstractGameScene mScene = this;
	
	// TODO Player mPlayer
	protected Sprite mPlayer;
	
	// Loading Scene
	private Scene mLoadingScene;
	private Text mLoadingText;
	
	/* ZoomCamera */
	protected PinchZoomDetector mPinchZoomDetector;
	protected float mInitialPinchZoomFactor;
	protected static final float MIN_ZOOM_FACTOR = 0.125f;
	protected static final float MAX_ZOOM_FACTOR = 2.0f;
	
	/* ParticleSystem */
	protected PointParticleEmitter mParticleEmitter;
	protected BatchedSpriteParticleSystem mParticleSystem;

	/* AnalogOnScreenControl */
	protected AnalogOnScreenControl mController;
	
	
	/* *************************************************************************
	 * Constructors
	 */
	
	public AbstractGameScene() {
		// Let the Scene Manager know that we want to show a Loading Scene for at least 2 seconds.
		this(2f);
	}
	
	/**
	 * Initialization
	 * 
	 * Code from BaseGameActivity::onCreateScene(...)
	 * Code from BaseGameActivity::onPopulateScene(...)
	 * moved here and to GameScene::onLoadScene()
	 * 
	 * @param pLoadingScreenMinimumSecondsShown
	 */
	public AbstractGameScene(float pLoadingScreenMinimumSecondsShown) {
		super(pLoadingScreenMinimumSecondsShown);
		
		/* Scaling */
		// Scale the Game Scene according to the Camera's scale factor.
		// TODO Scaling
		this.setScale(res.cameraScaleX, res.cameraScaleY);
//		this.setPosition(0, res.cameraHeight / 2f);
		
	}
	
	/* *************************************************************************
	 * 1) LoadingScreen
	 *    a) load and show
	 *    b) hide and unload
	 *    
	 * 2) Scene
	 *    a) load
	 *    b) show
	 *    c) hide
	 *    d) unload
	 */
	
	/**
	 * Setup and return the loading screen/ scene.
	 */
	@Override
	public Scene onLoadingScreenLoadAndShown() {
		
		mLoadingScene = new Scene();
		mLoadingScene.setBackgroundEnabled(true);
		
		mLoadingText = new Text(0, 0, res.fontHarting96, "Loading...", res.engine.getVertexBufferObjectManager());
		//mLoadingText.setPosition(mLoadingText.getWidth() / 2f, res.cameraHeight - mLoadingText.getHeight() / 2f);
		mLoadingText.setPosition(res.cameraWidth / 2f, res.cameraHeight / 2f);
		mLoadingScene.attachChild(mLoadingText);
		
		return mLoadingScene;
	}
	
	/**
	 * Detach the loading screen resources.
	 */
	@Override
	public void onLoadingScreenUnloadAndHidden() {
		
		mLoadingText.detachSelf();
		mLoadingText = null;
		
		mLoadingScene = null;
	}
	
	// -------------------------------------------------------------------------
	
	/**
	 * Load the resources to be used in the Game Scenes.
	 * (Populate Scene)
	 * 
	 * Code from BaseGameActivity::onCreateScene(...)
	 * Code from BaseGameActivity::onPopulateScene(...)
	 * moved here and to Scene::Constructor
	 * 
	 */
	@Override
	public void onLoadScene() {
		
		/* Resources */
		res.loadWorld001();
		
		/* setup | initialize */
		createBackground();
		createHUD();
		createControls();
		createParticleSystem();
		createPhysicsWorld();
		
		/* Entities */
		// @see GameScene.onLoadScene()

		// use, modify: A particleSystem is an entity! You can register modifiers, etc.
		mParticleEmitter.setCenter(res.cameraWidth * 0.5f, res.cameraHeight * 0.5f);
		//mParticleSystem.setParticlesSpawnEnabled(false); // turn on/off
		//particleSystem.removeParticleInitializer(pi);
		//particleSystem.removeParticleModifier(pm);
		
	}
	
	@Override
	public void onShowScene() {
		res.engine.getCamera().setHUD(mHUD);
	}
	
	@Override
	public void onHideScene() {
		res.engine.getCamera().setHUD(null);
	}
	
	/**
	 * MUST be done on Update Thread!
	 * 
	 * @see Learning AndEngine, Packt, Ch. 7, p. 163
	 */
	@Override
	public void onUnloadScene() {
		
		// detach and unload the scene.
		res.engine.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				
				setIgnoreUpdate(true);
			    
				// clean up acceleration sensor
				if (res.sensor != null) {
					res.sensor = null;
				}
				
				// clean up physics world
				unregisterUpdateHandler(res.physicsWorld);
			    res.physicsWorld.clearForces();
			    res.physicsWorld.clearPhysicsConnectors();
			    while (res.physicsWorld.getBodies().hasNext()) {
			    	res.physicsWorld.destroyBody(res.physicsWorld.getBodies().next());
			    }
			    
			    // clean up camera
			    res.camera.reset();
			    res.camera.setHUD(null);
			    res.camera.setChaseEntity(null);
			    
			    // clean up scene
				mScene.detachChildren();
				mScene.clearEntityModifiers();
				mScene.clearTouchAreas();
				mScene.clearUpdateHandlers();
				
				// clean resources
				res.unloadWorld001Resources();
				
			}
		});
	}
	
	/* *************************************************************************
	 * Helper functions to structure the code
	 */
	
	/**
	 * Background
	 * Description see MainActivity
	 */
	private void createBackground() {
		
		/* Background */
		Sprite background = new Sprite(
				res.cameraWidth / 2f, 
				res.cameraHeight / 2f, 
				res.backgroundMarTextureRegion, 
				res.vbom);
		// TODO Scaling
		background.setScale(res.cameraScaleX, res.cameraScaleY);
		this.attachChild(background);
	}
	
	/** 
	 * HUD (heads up display)
	 */
	private void createHUD() {
		
		mHUD = new HUD();
		mHUD.setScaleCenter(0f, 0f);
		// TODO Scaling
		mHUD.setScale(res.cameraScaleX, res.cameraScaleY);
		
		
		// Setup the HUD Buttons
		ButtonSprite MainMenuButton = new ButtonSprite(
				0f, 
				0f, 
				res.buttonOptionsTiledTextureRegion.getTextureRegion(0), 
				res.buttonOptionsTiledTextureRegion.getTextureRegion(1), 
				res.engine.getVertexBufferObjectManager());
		// TODO Scaling
		MainMenuButton.setScale(1 / res.cameraScaleX, 1 / res.cameraScaleY);
		MainMenuButton.setPosition((MainMenuButton.getWidth() * MainMenuButton.getScaleX()) / 2f, (MainMenuButton.getHeight() * MainMenuButton.getScaleY()) / 2f);
		MainMenuButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				res.clickSound.play();
				SceneManager.getInstance().showMainMenu();
			}
		});
//		Text MainMenuButtonText = new Text(MainMenuButton.getWidth() / 2, MainMenuButton.getHeight() / 2,res.fontDefault32Bold, "MENU", res.engine.getVertexBufferObjectManager());
//		MainMenuButton.attachChild(MainMenuButtonText);
		mHUD.attachChild(MainMenuButton);
		mHUD.registerTouchArea(MainMenuButton);
		
		
		ButtonSprite OptionsButton = new ButtonSprite(0f,0f, 
				res.buttonTiledTextureRegion.getTextureRegion(0), 
				res.buttonTiledTextureRegion.getTextureRegion(1), 
				res.engine.getVertexBufferObjectManager());
		// TODO Scaling
		OptionsButton.setScale(1 / res.cameraScaleX, 1 / res.cameraScaleY);
		OptionsButton.setPosition(res.cameraWidth - ((OptionsButton.getWidth() * OptionsButton.getScaleX()) / 2f), (OptionsButton.getHeight() * OptionsButton.getScaleY()) / 2f);
		OptionsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				res.clickSound.play();
				SceneManager.getInstance().showOptionsLayer(true);
			}
		});
		Text OptionsButtonText = new Text(0, 0, res.fontDefault32Bold, "OPTIONS", res.engine.getVertexBufferObjectManager());
		OptionsButtonText.setPosition((OptionsButton.getWidth()) / 2, (OptionsButton.getHeight()) / 2);
		OptionsButton.attachChild(OptionsButtonText);
		mHUD.attachChild(OptionsButton);
		mHUD.registerTouchArea(OptionsButton);
		
		
		/* Text Time */
		Font font = res.fontDefault24Normal;
		font.prepareLetters("Time: 1234567890".toCharArray());
		
		final String TIME_PREFIX = "Time: ";
		final String TIME_FORMAT = "00:00:00";
		final int TIME_MAX_CHAR_COUNT = TIME_PREFIX.length() + TIME_FORMAT.length();
		
		Text timeText = new Text(0, 0, font, TIME_PREFIX + TIME_FORMAT, TIME_MAX_CHAR_COUNT, res.engine.getVertexBufferObjectManager()) {

			//long startTime = System.currentTimeMillis();
			long lastSecond = 0;
			
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				long second = System.currentTimeMillis();
				if (lastSecond != second) {
					lastSecond = second;
					
					long now = System.currentTimeMillis() - startTime;
					long hour = now / (1000*60*60);
					long min = (now % (1000*60*60)) / (1000*60);
					long sec = ((now % (1000*60*60)) % (1000*60)) / 1000;
					
					// build a new String with the current time
					String timeSuffix = String.format(Locale.US, "%02d:%02d:%02d", hour, min, sec);
					this.setText(TIME_PREFIX + timeSuffix);
					this.setX(res.cameraWidth - this.getWidth() / 2f);
				}
				super.onManagedUpdate(pSecondsElapsed);
			}
		};
		
		timeText.setColor(0, 1.0f, 1.0f);
		timeText.setY(res.cameraHeight - timeText.getHeight() / 2);
		mHUD.attachChild(timeText);
		
	}

	/**
	 * ParticleSystems<br />
	 * <br />
	 * 
	 * OBJECTS:<br />
	 * ParticleEmitter<br />
	 * ParticleSystem<br />
	 * ParticleInitializer<br />
	 * Particlemodifier<br />
	 * <br />
	 *  
	 * ITextureRegion: size <= 32x32px<br />
	 * <br />
	 * 
	 * OBJECT ParticleEmitter:<br />
	 * PointParticleEmitter<br />
	 * CircleOutlineParticleEmitter<br />
	 * CircleParticleEmitter<br />
	 * RectangleOutlineParticleEmitter<br />
	 * RectangleParticleEmitter<br />
	 * <br />
	 * 
	 * OBJECT ParticleSystem:<br />
	 * BatchedSpriteParticleSystem<br />
	 * SpriteParticleSystem // limited, overhead<br />
	 * <br />
	 * 
	 * OBJECT ParticleInitializer:<br />
	 * ColorParticleInitializer<br />
	 * AlphaParticleInitializer<br />
	 * BlendFunctionParticleInitializer<UncoloredSprite>(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)<br />
	 * RotationParticleInitializer<br />
	 * ScaleParticleInitializer<br />
	 * GravityParticleInitializer<br />
	 * AccelerationParticleInitializer<br />
	 * VelocityParticleInitializer // individual particles move at a constant speed when spawned<br />
	 * ExpireParticleInitializer // MUST HAVE (use this initializer or the OffCameraExpireParticleModifier to limit the lifetime of particles, because the particles count in the ParticleSystem is limited: 'old' particles have to be destroyed that new ones can be spawned)<br />
	 * <br />
	 * 
	 * OBJECT ParticleModifier: // modify individual particles in their lifetime<br />
	 * ColorParticleModifier<br />
	 * AlphaParticleModifier<br />
	 * RotationParticleModifier<br />
	 * ScaleParticleModifier<br />
	 * OffCameraExpireParticleModifier<br />
	 * IParticleModifier<UncoloredSprite><br />
	 * <br />
	 * 
	 * METHODS:<br />
	 * mParticleEmitter.setCenter(x, y)<br />
	 * <br />
	 * 
	 * mParticleSystem.setParticlesSpawnEnabled(false)<br />
	 * mParticleSystem.addParticleInitializer(...)<br />
	 * mParticleSystem.removeParticleInitializer(...)<br />
	 * mParticleSystem.addParticleModifier(...)<br />
	 * mParticleSystem.removeParticleModifier(...)<br />
	 * <br />
	 * 
	 * mParticleSystem.registerEntityModifier(...) // the ParticleSystem is an entity<br />
	 * ...
	 * 
	 */
	private void createParticleSystem() {
		
		// ITextureRegion
		ITextureRegion particleTextureRegion = res.particleTextureRegion;
		
		// ParticleEmitter
		mParticleEmitter = new PointParticleEmitter(res.cameraWidth * 0.5f, res.cameraHeight * 0.5f);
		
		// ParticleSystem
		float minSpawnRate = 10;
		float maxSpawnRate = 20;
		int maxParticlesCount = 200;
		mParticleSystem = new BatchedSpriteParticleSystem(mParticleEmitter, minSpawnRate, maxSpawnRate, maxParticlesCount, particleTextureRegion, res.engine.getVertexBufferObjectManager());
		
		// ParticleInitializer
		mParticleSystem.addParticleInitializer(new ColorParticleInitializer<UncoloredSprite>(0, 1, 0, 0, 0, 0.3f));
		mParticleSystem.addParticleInitializer(new AlphaParticleInitializer<UncoloredSprite>(0, 0.8f));
		mParticleSystem.addParticleInitializer(new BlendFunctionParticleInitializer<UncoloredSprite>(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)); // (GLES20.GL_SRC_ALPHA, GLES20.GL_ONE)
		mParticleSystem.addParticleInitializer(new RotationParticleInitializer<UncoloredSprite>(0.0f, 360.0f));
		mParticleSystem.addParticleInitializer(new ScaleParticleInitializer<UncoloredSprite>(0.5f, 1.5f));
		mParticleSystem.addParticleInitializer(new GravityParticleInitializer<UncoloredSprite>());
		mParticleSystem.addParticleInitializer(new AccelerationParticleInitializer<UncoloredSprite>(-25, 25, 25, 50));
		mParticleSystem.addParticleInitializer(new VelocityParticleInitializer<UncoloredSprite>(-25, 25, 25, 50)); // individual particles move at a constant speed when spawned
		mParticleSystem.addParticleInitializer(new ExpireParticleInitializer<UncoloredSprite>(10, 15)); // MUST HAVE (use this initializer or the OffCameraExpireParticleModifier to limit the lifetime of particles, because the particles count in the ParticleSystem is limited: 'old' particles have to be destroyed that new ones can be spawned) 
		
		// ParticleModifier (modify individual particles in their lifetime)
		float fromTime = 0f;
		float toTime = 3.0f;
		mParticleSystem.addParticleModifier(new ColorParticleModifier<UncoloredSprite>(fromTime, toTime, 1, 1, 0, 0.5f, 0, 0));
		mParticleSystem.addParticleModifier(new ColorParticleModifier<UncoloredSprite>(4, 6, 1, 1, 0.5f, 1, 0, 1));
		mParticleSystem.addParticleModifier(new AlphaParticleModifier<UncoloredSprite>(0, 1, 0, 1));
		mParticleSystem.addParticleModifier(new AlphaParticleModifier<UncoloredSprite>(5, 6, 1, 0));
		mParticleSystem.addParticleModifier(new RotationParticleModifier<UncoloredSprite>(2, 4, 0f, 180f));
		mParticleSystem.addParticleModifier(new ScaleParticleModifier<UncoloredSprite>(0, 5, 1.0f, 2.0f));
		//mParticleSystem.addParticleModifier(new OffCameraExpireParticleModifier<UncoloredSprite>(mCamera); // alternative to ExpireParticleInitializer
		mParticleSystem.addParticleModifier(new IParticleModifier<UncoloredSprite>() {
			
			@Override
			public void onInitializeParticle(Particle<UncoloredSprite> pParticle) {
				// customized modifications on initialization (particle spawned)
			}
			
			@Override
			public void onUpdateParticle(Particle<UncoloredSprite> pParticle) {
				// customized modifications on every update to the particle
//				Entity entity = pParticle.getEntity();
//				float currentY = entity.getY();
//				float currentVelocityY = pParticle.getPhysicsHandler().getVelocityY();
//				float currentAccelerationY = pParticle.getPhysicsHandler().getAccelerationY();
//				if (currentY < 20 && currentVelocityY != 0 && currentAccelerationY != 0) {
//					// restrict movement on the y-axis: simulates landing on the ground, if the particle is close to the bottom of the scene
//					pParticle.getPhysicsHandler().setVelocityY(0);
//					pParticle.getPhysicsHandler().setAccelerationY(0);
//				}
			}
		});
		
		// make visible (attach to scene)
		this.attachChild(mParticleSystem);
		
		// use, modify: A particleSystem is an entity! You can register modifiers, etc.
		//mParticleEmitter.setCenter(CAMERA_WIDTH * 0.5f, CAMERA_HEIGHT * 0.5f);
		//mParticleSystem.setParticlesSpawnEnabled(false); // turn on/off
		//particleSystem.removeParticleInitializer(pi);
		//particleSystem.removeParticleModifier(pm);
		
	}
	
	/**
	 *  <b>Physics</b><br />
	 *  <b>Box2D (Physics Engine)</b><br />
	 *  <br />
	 *  
	 *  @see http://www.box2d.org/manual.html
	 *  @see https://code.google.com/p/box2d-editor/
	 *  @see https://www.codeandweb.com/physicseditor
	 *  @see https://www.iforce2d.net/rube/
	 *
	 *  
	 *  <b>PhysicsWorld</b><br />
	 *  FixedStepPhysicsWorld<br />
	 *  .registerPhysicsConnector(new PhysicsConnector(entity, body))<br />
	 *  <br />
	 *  
	 *  <b>Shapes</b><br />
	 *  CircleShape<br />
	 *  .setRadius()<br />
	 *  .setPosition(new Vector2())<br />
	 *  Rectangle<br />
	 *  PolygonShape<br />
	 *  .set(vertices); Vector2[] vertices<br />
	 *  .setAsBox<br />
	 *  <br />
	 *  
	 *  <b>Bodies</b><br />
	 *  physicsWorld.createBody(bodyDef);<br />
	 *  .setType()<br />
	 *  .setLinearDamping(float)<br />
	 *  .setFixedRotation(true|false)<br />
	 *  .setUserData(entity)<br />
	 *  .setTransform(x,y,rotation)<br />
	 *  <br />
	 *  
	 *  <b>BodyTypes</b><br />
	 *  DynamicBody // is moved by forces<br />
	 *  .applyForce(force, point)<br />
	 *  .applyLinearImpulse(impulse, point)<br />
	 *  .applyAngularImpulse(impulse)<br />
	 *  .applyTorque(torque)<br />
	 *  KinematicBody // is not moved by forces, but its set velocities<br />
	 *  .setLinearVelocity(x,y)<br />
	 *  .setAngularVelocity(r)<br />
	 *  StaticBody<br />
	 *  <br />
	 *  
	 *  <b>BodyDef</b><br />
	 *  new BodyDef();<br />
	 *  .type<br />
	 *  .position.x = (x / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);<br />
	 *  .position.y = (y / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);<br />
	 *  <br />
	 *  
	 *  <b>FixtureDef</b><br />
	 *  PhysicsFactory.createFixtureDef(density, elasticity, friction, isSensor, category, categoryMask, groupID);<br />
	 *  .shape<br />
	 *  .isSensor<br />
	 *  <br />
	 *  
	 *  <b>Fixtures</b><br />
	 *  body.createFixture(fixtureDef)<br />
	 *  <br />
	 *  
	 *  <b>Constraints</b><br />
	 *  <br />
	 *  
	 *  <b>Joints</b><br />
	 *  JointTypes:<br />
	 *  revolute<br />
	 *  prismatic<br />
	 *  distance<br />
	 *  mouse<br />
	 *  pulley<br />
	 *  weld<br />
	 *  line<br />
	 *  <br />
	 *  limits:<br />
	 *  .enableLimit<br />
	 *  .lowerTranslation<br />
	 *  .upperTranslation<br />
	 *  <br />
	 *  motors:<br />
	 *  .enableMotor<br />
	 *  .motorSpeed<br />
	 *  .maxMotorForce<br />
	 *  <br />
	 *  
	 *  <b>Contacts</b><br />
	 *  <br />
	 *  
	 *  <b>Collision Detection</b><br />
	 *  a) Entity collisions<br />
	 *  <code>Entity.onManagedUpdate(){if(this.collidesWith(enemy)){...}}</code><br />
	 *  <br />
	 *  <code>new ICollisionCallback(){onCollision(){...}}</code><br />
	 *  <code>registerUpdateHandler(new CollisionHandler(collisionCallback, player, enemy)</code><br />
	 *  <br />
	 *  
	 *  b) pixel-perfect collisions<br />
	 *  <br />
	 *  
	 *  c) physics engine collisions<br />
	 *  <code>physicsWorld.setContactListener(new ContactListener(){@Override ...});</code><br />
	 *  <code>entity.isBodyContacted(body, contact){if(contact.getFixture().getBody().equals(body))return true; return false;}</code><br />
	 *  <br />
	 *  
	 */
	private void createPhysicsWorld() {
		
		res.physicsWorld = new FixedStepPhysicsWorld(res.STEPS_PER_SECOND, new Vector2(0f, SensorManager.GRAVITY_EARTH), false, 8, 3);
		
		// Entities & Shapes
		Rectangle ground = new Rectangle(0, res.cameraHeight - 2, res.cameraWidth, 2, res.vbom);
		Rectangle roof = new Rectangle(0, 0, res.cameraWidth, 2, res.vbom);
		Rectangle left = new Rectangle(0, 0, 2, res.cameraHeight, res.vbom);
		Rectangle right = new Rectangle(res.cameraWidth - 2, 0, 2, res.cameraHeight, res.vbom);
		
		// FixtureDef
		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		
		// BodyDef
		PhysicsFactory.createBoxBody(res.physicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(res.physicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(res.physicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(res.physicsWorld, right, BodyType.StaticBody, wallFixtureDef);
		
		// attach Entities to Scene
		this.attachChild(ground);
		this.attachChild(roof);
		this.attachChild(left);
		this.attachChild(right);
		
		// apply Physics
		this.registerUpdateHandler(res.physicsWorld);
		
		// Collision Detection
		res.physicsWorld.setContactListener(this);
	}
	
	/**
	 * <b>Controller</b><br />
	 * <br />
	 * 
	 * <b>Acceleration Sensor</b><br />
	 * <br />
	 * 
	 * <b>Touch</b><br />
	 * setOnSceneTouchListener(this)<br />
	 * implement IOnSceneTouchListener{onSceneTouchEvent()}<br />
	 * <br />
	 * 
	 * setTouchAreaBindingOnActionDownEnabled(true)<br />
	 * ButtonSprite(){onAreaTouched(...)}<br />
	 * mScene.registerTouchArea(buttonSprite);<br />
	 * <br />
	 * 
	 * PinchZoomCamera<br />
	 * implement IPinchZoomDetectorListener{onPinchZoom();onPinchZoomStarted();onPinchZoomFinished()}<br />
	 * <br />
	 * 
	 * <b>Control</b><br />
	 * AnalogOnScreenControl<br />
	 * DigitalOnScreenControl<br />
	 * @see AndEngine Cookbook, Chapter 4
	 * @see /AndEngineExamples/src/org/andengine/examples/AnalogOnScreenControlExample.java
	 * 
	 */
	private void createControls() {
		
		/* Acceleration Sensor */
		// @see MainActivity.onAccelerationChenged()
		
		/* onAreaTouched */
		// @see Entity
		
		/* Touch */
		this.setOnSceneTouchListener(this);					// implements IOnSceneTouchListener {onSceneTouchEvent(...)}
		this.setOnSceneTouchListenerBindingOnActionDownEnabled(true);
		this.setTouchAreaBindingOnActionDownEnabled(true);	// needed for ButtonSprite(){onAreaTouched(...)} // mScene.registerTouchArea(buttonSprite);
		this.setTouchAreaBindingOnActionMoveEnabled(true);
		
		/* Pinch-zoom */
		mPinchZoomDetector = new PinchZoomDetector(this); 	// implements IPinchZoomDetectorListener {onPinchZoom(...)...}
		mPinchZoomDetector.setEnabled(false);				// TODO PinchZoomDetector.setEnabled()
		
		/* AnalogOnScreenControl */
		// TODO texture resources for AnalogOnScreenControl
		/*
		mController = new AnalogOnScreenControl(pX, pY, res.camera, pControlBaseTextureRegion, pControlKnobTextureRegion, 0.1f, res.vbom, new IAnalogOnScreenControlListener() {

			@Override
			public void onControlChange(BaseOnScreenControl pBaseOnScreenControl, float pValueX, float pValueY) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onControlClick(AnalogOnScreenControl pAnalogOnScreenControl) {
				// TODO Auto-generated method stub
				
			}
			
		});
		mController.refreshControlKnobPosition();
		this.setChildScene(mController);
		*/
	}

	/* *************************************************************************
	 * Collision Detection
	 * Helper Methods
	 */
	
	/**
	 * @see AndEngine Cookbook
	 * 
	 * @param pBody
	 * @param pContact
	 * @return
	 */
	public boolean isBodyContacted(Body pBody, Contact pContact) {
		if(pContact.getFixtureA().getBody().equals(pBody) ||
				pContact.getFixtureB().getBody().equals(pBody))
			return true;
		return false;
	}
	
	/**
	 * @see AndEngine Cookbook
	 * 
	 * @param pBody1
	 * @param pBody2
	 * @param pContact
	 * @return
	 */
	public boolean areBodiesContacted(Body pBody1, Body pBody2, Contact pContact) {
		if(pContact.getFixtureA().getBody().equals(pBody1) ||
				pContact.getFixtureB().getBody().equals(pBody1))
			if(pContact.getFixtureA().getBody().equals(pBody2) ||
					pContact.getFixtureB().getBody().equals(pBody2))
				return true;
		return false;
	}

	/**
	 * @see Learning AndEngine
	 */
	private boolean checkContact(Contact contact, String typeA, String typeB) {
		
		// check type for a save cast
		if (contact.getFixtureA().getBody().getUserData() instanceof ICollidableEntity && contact.getFixtureB().getBody().getUserData() instanceof ICollidableEntity) {
			ICollidableEntity ceA = (ICollidableEntity) contact.getFixtureA().getBody().getUserData();
			ICollidableEntity ceB = (ICollidableEntity) contact.getFixtureB().getBody().getUserData();
			
			if (typeA.equals(ceA.getType()) && typeB.equals(ceB.getType()) || typeA.equals(ceB.getType()) && typeB.equals(ceA.getType())) {
				return true;	
			}
		}
		
		return false;
	}

}
