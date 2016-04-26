package com.floppyinfant.android.game.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.util.Log;

import org.anddev.andengine.extension.svg.SVGDoc;
import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
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
import org.andengine.entity.primitive.DrawMode;
import org.andengine.entity.primitive.Mesh;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
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
import org.andengine.extension.physics.box2d.util.triangulation.EarClippingTriangulator;
import org.andengine.extension.physicseditor.physicsloader.PhysicsEditorLoader;
import org.andengine.extension.rubeloader.ITextureProvider;
import org.andengine.extension.rubeloader.RubeLoader;
import org.andengine.extension.rubeloader.def.RubeDef;
import org.andengine.extension.rubeloader.factory.EntityFactory;
import org.andengine.extension.rubeloader.factory.IEntityFactory;
import org.andengine.extension.svg.opengl.texture.atlas.bitmap.SVGBitmapTextureAtlasTextureRegionFactory;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.ITextureAtlas;
import org.andengine.opengl.texture.atlas.TextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.IBuildableTextureAtlas;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.atlas.source.ITextureAtlasSource;
import org.andengine.opengl.texture.bitmap.AssetBitmapTexture;
import org.andengine.opengl.texture.bitmap.BitmapTextureFormat;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;
import org.andengine.util.adt.list.ListUtils;
import org.andengine.util.debug.Debug;
import org.andengine.util.texturepack.TexturePack;
import org.andengine.util.texturepack.TexturePackLoader;
import org.andengine.util.texturepack.TexturePackTextureRegionLibrary;
import org.andengine.util.texturepack.exception.TexturePackParseException;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Shape;

// is this import really necessary ???
import com.floppyinfant.android.game.R;
import com.floppyinfant.android.game.libs.PhysicsEditorShapeLibrary;

//import org.iforce2d.AndEngineJb2dJson_Simple;	// uses another branch of AndEngine/ Box2D!
import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

/**
 * 
 * 
 * @author TM
 *
 */
public class ResourceManager implements ITextureProvider {
	
	// -------------------------------------------------------------------------
	// static members
	//
	// access directly 'ResourceManager.STEPS_PER_SECOND'
	// -------------------------------------------------------------------------
	
	public static final int DEBUG = 1;
	
	public static final int STEPS_PER_SECOND = 60;
	
	private static final ResourceManager INSTANCE = new ResourceManager();
	
	protected static final ResourceManager res = ResourceManager.getInstance();
	
	
	// -------------------------------------------------------------------------
	// non-static members
	//
	// access by 'ResourceManager.getInstance().engine'
	// -------------------------------------------------------------------------
	
	public BaseGameActivity activity;
	public Context context;					// activity.getApplicationContext()
	public Engine engine;					// activity.getEngine()
	public VertexBufferObjectManager vbom;	// engine.getVertexBufferObjectManager()
	public Camera camera;					// engine.getCamera()
	
	public float cameraWidth;
	public float cameraHeight;
	public float cameraScaleX;
	public float cameraScaleY;
	
	public PhysicsWorld physicsWorld; 		// TODO should be non static member (initialization?!)
	
	/* ParticleSystem */
	public PointParticleEmitter particleEmitter;
	public BatchedSpriteParticleSystem particleSystem;
	
	/* Acceleration Sensor */
	/** scene that implements IAccelerationListener */
	public IAccelerationListener sensor = null;
	
	/* Rotation Angles set by Accelerometer */
	public float yaw;
	public float pitch;
	public float roll;
	
	// -------------------------------------------------------------------------
	
	/** PureData */
	public PdUiDispatcher pd;
	
	// -------------------------------------------------------------------------
	
	/** 
	 * Get Textures from TexturePacker.
	 * Usage: tpl.get(<interface.RES_ID>);
	 */
	public TexturePackTextureRegionLibrary tpl;
	
	// -------------------------------------------------------------------------
	
	/**
	 * @see AndEngineRubeLoaderExample
	 */
	public HashMap<String, ITextureRegion> mTexturesMap = new HashMap<String, ITextureRegion>();
	
	/**
	 * Atlas
	 * Constraints: max size 1024 x 1024 px
	 */
	public BuildableBitmapTextureAtlas mBitmapTextureAtlas;
	
	// -------------------------------------------------------------------------
	
	/* Texture Regions | Tiled Texture Regions */
	public ITextureRegion sarahTextureRegion;
	
	public ITextureRegion schneckiTextureRegion;
	public ITiledTextureRegion schneckiTiledTextureRegion;	// TODO
	
	public ITextureRegion giraffeTextureRegion;
	public ITiledTextureRegion giraffeTiledTextureRegion;
	public ITextureRegion giraffeLayerTextureRegion;
	
	public ITextureRegion eselTextureRegion;
	public ITextureRegion eselTiledTextureRegion;	// TODO
	
	public ITextureRegion backgroundMarTextureRegion;
	public ITextureRegion backgroundSunTextureRegion;
	public ITextureRegion backgroundSunsetTextureRegion;
	
	// shared resources
	public ITextureRegion topazTextureRegion;
	public ITextureRegion flowerTextureRegion;		// TODO
	public ITextureRegion flowersTextureRegion;		// TODO
	
	public ITextureRegion winTextureRegion;
	public ITextureRegion looseTextureRegion;
	
	public ITextureRegion particleTextureRegion; 	// ParticleSystem
	
	// menu resources
	public ITextureRegion backgroundMenuTextureRegion;
	public ITextureRegion cloudTextureRegion;
	public ITiledTextureRegion buttonTiledTextureRegion;
	public ITiledTextureRegion buttonBackTiledTextureRegion;
	public ITiledTextureRegion buttonOptionsTiledTextureRegion;
	public ITiledTextureRegion buttonPlayTiledTextureRegion;
	
	// -------------------------------------------------------------------------
	
	/* FixtureDefs */
	public static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
	
	// -------------------------------------------------------------------------
	
	/* Fonts */
	public Font font;
	public Font fontDefault24Normal;
	public Font fontDefault32Bold;
	public Font fontDefault72Bold;
	public Font fontHarting96;
	
	// -------------------------------------------------------------------------
	
	/* Music */
	public Music music;
	
	/* Sounds */
	public Sound clickSound;
	
	// -------------------------------------------------------------------------
	
	
	/* *************************************************************************
	 * Initialization
	 */
	
	// private Constructor
	private ResourceManager() {
		// empty Constructor
	}
	
	/**
	 * Singleton Pattern
	 */
	public static ResourceManager getInstance() {
		/*
		if(INSTANCE == null){
			INSTANCE = new ResourceManager();
		}
		*/
		return INSTANCE;
	}

	/**
	 * Must be called in the onCreateResources()-method first,
	 * before method-calls to the static members are done.
	 * 
	 * @see onCreateResources()
	 */
	public synchronized void setup(BaseGameActivity pActivity, float pCameraWidth, float pCameraHeight, float pCameraScaleX, float pCameraScaleY) {
		this.activity = pActivity;
		this.context = pActivity.getApplicationContext();
		this.engine = pActivity.getEngine();
		this.vbom = pActivity.getVertexBufferObjectManager();	// engine.getVertexBufferObjectManager();
		this.camera = pActivity.getEngine().getCamera();		// engine.getCamera();
		
		// TODO Scaling
		this.cameraWidth = pCameraWidth;
		this.cameraHeight = pCameraHeight;
		this.cameraScaleX = pCameraScaleX;
		this.cameraScaleY = pCameraScaleY;
	}
	
	
	/* *************************************************************************
	 * public methods
	 */
	
	public static synchronized void loadMenuResources() {
		getInstance().loadMenuTextures();
		getInstance().loadSharedResources();
	}
	
	public static synchronized void loadWorld001() {
		getInstance().loadWorld001Textures();
		getInstance().loadSharedResources();
		
	}

	public static synchronized void loadWorld002() {
		getInstance().loadWorld002Textures();
		getInstance().loadSharedResources();
	}

	public static synchronized void loadWorld003() {
		getInstance().loadWorld003Textures();
		getInstance().loadSharedResources();
	}
	
	// -------------------------------------------------------------------------
	
	public static synchronized void unloadMenuResources() {
		getInstance().unloadMenuTextures();
	}
	
	public static synchronized void unloadSharedResources() {
		getInstance().unloadSharedTextures();
		getInstance().unloadSounds();
		getInstance().unloadFonts();
	}
	
	public static synchronized void unloadWorld001Resources() {
		getInstance().unloadWorld001Textures();
	}
	
	public static synchronized void unloadWorld002Resources() {
		getInstance().unloadWorld002Textures();
	}
	
	public static synchronized void unloadWorld003Resources() {
		getInstance().unloadWorld003Textures();
	}
	
	// -------------------------------------------------------------------------
	
	private void loadSharedResources(){
		loadSharedTextures();
		loadSounds();
		loadFonts();
	}
	
	
	/* *************************************************************************
	 * Textures
	 * 
	 * Textures must not exceed 1024x1024px.
	 * 
	 * Textures for repeating sprites must have a 
	 * dimension with a power of two (2^^x)
	 * which is required by OpenGLs wrap mode.
	 */

	/* not used */
	private void loadGameTextures() {
		
		//createBackground();
		
	}
	
	private void loadMenuTextures() {
		
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		if(backgroundMenuTextureRegion == null) {
			String filename = "bg_menu.png";
			int width = 11;
			int height = 490;
			BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), width, height);
			backgroundMenuTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(texture, context, filename);
			try {
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}
		
		if(cloudTextureRegion == null) {
			String filename = "cloud.png";
			int width = 266;
			int height = 138;
			BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), width, height);
			cloudTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(texture, context, filename);
			try {
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
			
			/*
			String filename = "sheep.png";
			int width = 196;
			int height = 138;
			BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), width, height);
			cloudTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(texture, context, filename);
			try {
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
			*/
			
		}

		if(buttonTiledTextureRegion == null) {
			String filename = "kaugummi.png"; 
			int width = 512;
			int height = 256;
			int cols = 2;
			int rows = 1;
			BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), width, height);
			buttonTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(texture, context, filename, cols, rows);
			try {
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}

		if(buttonBackTiledTextureRegion == null) {
			String filename = "btn_back.png"; 
			int width = 192;
			int height = 28;
			int cols = 2;
			int rows = 1;
			BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), width, height);
			buttonBackTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(texture, context, filename, cols, rows);
			try {
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}

		if(buttonOptionsTiledTextureRegion == null) {
			String filename = "btn_opt.png"; 
			int width = 102;
			int height = 51;
			int cols = 2;
			int rows = 1;
			BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), width, height);
			buttonOptionsTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(texture, context, filename, cols, rows);
			try {
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}

		if(buttonPlayTiledTextureRegion == null) {
			String filename = "btn_play.png"; 
			int width = 278;
			int height = 91;
			int cols = 2;
			int rows = 1;
			BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), width, height);
			buttonPlayTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(texture, context, filename, cols, rows);
			try {
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}
		
		// ---------------------------------------------------------------------
		
		if (schneckiTextureRegion == null) {
			String filename = "schnecki.png"; 
			int width = 190;
			int height = 257;
			BuildableBitmapTextureAtlas textureAtlas = new BuildableBitmapTextureAtlas(engine.getTextureManager(), width, height, BitmapTextureFormat.RGBA_8888, TextureOptions.BILINEAR);
			schneckiTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, context, filename);
			try {
				textureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
			} catch (TextureAtlasBuilderException e) {
				//e.printStackTrace();
				Debug.e(e);
			}
			textureAtlas.load();
		}
		
		if (giraffeTextureRegion == null) {
			String filename = "giraffe.png"; 
			int width = 240;
			int height = 281;
			BuildableBitmapTextureAtlas textureAtlas = new BuildableBitmapTextureAtlas(engine.getTextureManager(), width, height, BitmapTextureFormat.RGBA_8888, TextureOptions.BILINEAR);
			giraffeTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, context, filename);
			try {
				textureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
			} catch (TextureAtlasBuilderException e) {
				//e.printStackTrace();
				Debug.e(e);
			}
			textureAtlas.load();
		}

		if(eselTextureRegion == null) {
			String filename = "esel.png";
			int width = 182;
			int height = 258;
			BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), width, height);
			eselTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(texture, context, filename);
			try {
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
			
		}
		
	}
	
	private void unloadMenuTextures() {
		
		if(backgroundMenuTextureRegion != null) {
			if(backgroundMenuTextureRegion.getTexture().isLoadedToHardware()) {
				backgroundMenuTextureRegion.getTexture().unload();
				backgroundMenuTextureRegion = null;
			}
		}
		
		if(buttonTiledTextureRegion != null) {
			if(buttonTiledTextureRegion.getTexture().isLoadedToHardware()) {
				buttonTiledTextureRegion.getTexture().unload();
				buttonTiledTextureRegion = null;
			}
		}
		
		if(cloudTextureRegion != null) {
			if(cloudTextureRegion.getTexture().isLoadedToHardware()) {
				cloudTextureRegion.getTexture().unload();
				cloudTextureRegion = null;
			}
		}
		
		if(schneckiTextureRegion != null) {
			if(schneckiTextureRegion.getTexture().isLoadedToHardware()) {
				schneckiTextureRegion.getTexture().unload();
				schneckiTextureRegion = null;
			}
		}
		
		if(giraffeTextureRegion != null) {
			if(giraffeTextureRegion.getTexture().isLoadedToHardware()) {
				giraffeTextureRegion.getTexture().unload();
				giraffeTextureRegion = null;
			}
		}
		
		if(eselTextureRegion != null) {
			if(eselTextureRegion.getTexture().isLoadedToHardware()) {
				eselTextureRegion.getTexture().unload();
				eselTextureRegion = null;
			}
		}
		
	}

	// -------------------------------------------------------------------------
	
	private void loadSharedTextures(){
		
		// ---------------------------------------------------------------------
		// Sprite TextureRegions
		// ---------------------------------------------------------------------
		
		if (topazTextureRegion == null) {
			String filename = "topaz_red64.png"; 
			int width = 64;
			int height = 64;
			BuildableBitmapTextureAtlas textureAtlas = new BuildableBitmapTextureAtlas(engine.getTextureManager(), width, height, BitmapTextureFormat.RGBA_8888, TextureOptions.BILINEAR);
			topazTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, context, filename);
			try {
				textureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
			} catch (TextureAtlasBuilderException e) {
				//e.printStackTrace();
				Debug.e(e);
			}
			textureAtlas.load();
		}
		
		if (particleTextureRegion == null) {
			String filename = "particle_point.png"; 
			int width = 32;
			int height = 32;
			BuildableBitmapTextureAtlas textureAtlas = new BuildableBitmapTextureAtlas(engine.getTextureManager(), width, height, BitmapTextureFormat.RGBA_8888, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			particleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, context, filename);
			try {
				textureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
			} catch (TextureAtlasBuilderException e) {
				//e.printStackTrace();
				Debug.e(e);
			}
			textureAtlas.load();
		}
		
		if (winTextureRegion == null) {
			String filename = "level_cleared.png"; 
			int width = 334;
			int height = 401;
			BuildableBitmapTextureAtlas textureAtlas = new BuildableBitmapTextureAtlas(engine.getTextureManager(), width, height, BitmapTextureFormat.RGBA_8888, TextureOptions.BILINEAR);
			winTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, context, filename);
			try {
				textureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
			} catch (TextureAtlasBuilderException e) {
				//e.printStackTrace();
				Debug.e(e);
			}
			textureAtlas.load();
		}
		
		if (looseTextureRegion == null) {
			String filename = "you_loose.png"; 
			int width = 283;
			int height = 399;
			BuildableBitmapTextureAtlas textureAtlas = new BuildableBitmapTextureAtlas(engine.getTextureManager(), width, height, BitmapTextureFormat.RGBA_8888, TextureOptions.BILINEAR);
			looseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, context, filename);
			try {
				textureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
			} catch (TextureAtlasBuilderException e) {
				//e.printStackTrace();
				Debug.e(e);
			}
			textureAtlas.load();
		}
		
	}
	
	private void unloadSharedTextures(){
		
		if(topazTextureRegion != null) {
			if(topazTextureRegion.getTexture().isLoadedToHardware()) {
				topazTextureRegion.getTexture().unload();
				topazTextureRegion = null;
			}
		}

		if(winTextureRegion != null) {
			if(winTextureRegion.getTexture().isLoadedToHardware()) {
				winTextureRegion.getTexture().unload();
				winTextureRegion = null;
			}
		}

		if(looseTextureRegion != null) {
			if(looseTextureRegion.getTexture().isLoadedToHardware()) {
				looseTextureRegion.getTexture().unload();
				looseTextureRegion = null;
			}
		}
		
	}
	
	// -------------------------------------------------------------------------
	
	private void loadWorld001Textures() {

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		// ---------------------------------------------------------------------
		// ParallaxBackground
		// ---------------------------------------------------------------------
		
		
		
		// ---------------------------------------------------------------------
		// Background TextureRegions
		// ---------------------------------------------------------------------
		
		if (backgroundSunsetTextureRegion == null) {
			String filename = "bg_sunset.png"; 
			int width = 960;
			int height = 540;
			BuildableBitmapTextureAtlas textureAtlas = new BuildableBitmapTextureAtlas(engine.getTextureManager(), width, height, BitmapTextureFormat.RGBA_8888, TextureOptions.BILINEAR);
			backgroundSunsetTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, context, filename);
			try {
				textureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
			} catch (TextureAtlasBuilderException e) {
				//e.printStackTrace();
				Debug.e(e);
			}
			textureAtlas.load();
		}
		
		if (backgroundMarTextureRegion == null) {
			String filename = "bg_mar.png"; 
			int width = 960;
			int height = 540;
			BuildableBitmapTextureAtlas textureAtlas = new BuildableBitmapTextureAtlas(engine.getTextureManager(), width, height, BitmapTextureFormat.RGBA_8888, TextureOptions.BILINEAR);
			backgroundMarTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, context, filename);
			try {
				textureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
			} catch (TextureAtlasBuilderException e) {
				//e.printStackTrace();
				Debug.e(e);
			}
			textureAtlas.load();
		}
		
		// ---------------------------------------------------------------------
		// Sprite TextureRegions
		// ---------------------------------------------------------------------
		
		if (sarahTextureRegion == null) {
			String filename = "sarah.png"; 
			int width = 240;
			int height = 284;
			BuildableBitmapTextureAtlas textureAtlas = new BuildableBitmapTextureAtlas(engine.getTextureManager(), width, height, BitmapTextureFormat.RGBA_8888, TextureOptions.BILINEAR);
			sarahTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, context, filename);
			try {
				textureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
			} catch (TextureAtlasBuilderException e) {
				//e.printStackTrace();
				Debug.e(e);
			}
			textureAtlas.load();
		}
		
	}
	
	private void unloadWorld001Textures() {
		
		if (backgroundSunsetTextureRegion != null) {
			if (backgroundSunsetTextureRegion.getTexture().isLoadedToHardware()) {
				backgroundSunsetTextureRegion.getTexture().unload(); // remove the corresponding texture atlas from memory
				backgroundSunsetTextureRegion = null;
			}
		}

		if (backgroundMarTextureRegion != null) {
			if (backgroundMarTextureRegion.getTexture().isLoadedToHardware()) {
				backgroundMarTextureRegion.getTexture().unload(); // remove the corresponding texture atlas from memory
				backgroundMarTextureRegion = null;
			}
		}
		
		if (sarahTextureRegion != null) {
			if (sarahTextureRegion.getTexture().isLoadedToHardware()) {
				sarahTextureRegion.getTexture().unload(); // remove the corresponding texture atlas from memory
				sarahTextureRegion = null;
			}
		}
		
		// ... continue to unload all textures related to the game scene
		
		System.gc(); // once all textures have been unloaded, attempt to invoke the Garbage Collector
	}
	
	private void loadWorld002Textures() {

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		// ---------------------------------------------------------------------
		// Background TextureRegions
		// ---------------------------------------------------------------------

		if (backgroundSunTextureRegion == null) {
			String filename = "bg_sun.png"; 
			int width = 960;
			int height = 540;
			BuildableBitmapTextureAtlas textureAtlas = new BuildableBitmapTextureAtlas(engine.getTextureManager(), width, height, BitmapTextureFormat.RGBA_8888, TextureOptions.BILINEAR);
			backgroundSunTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, context, filename);
			try {
				textureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
			} catch (TextureAtlasBuilderException e) {
				//e.printStackTrace();
				Debug.e(e);
			}
			textureAtlas.load();
		}
		
		// ---------------------------------------------------------------------
		// Sprite TextureRegions
		// ---------------------------------------------------------------------
		
		if (giraffeLayerTextureRegion == null) {
			String filename = "giraffe_layer.png"; 
			int width = 720;
			int height = 540;
			BuildableBitmapTextureAtlas textureAtlas = new BuildableBitmapTextureAtlas(engine.getTextureManager(), width, height, BitmapTextureFormat.RGBA_8888, TextureOptions.BILINEAR);
			giraffeLayerTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, context, filename);
			try {
				textureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
			} catch (TextureAtlasBuilderException e) {
				//e.printStackTrace();
				Debug.e(e);
			}
			textureAtlas.load();
		}
		
		// ---------------------------------------------------------------------
		// AnimatedSprite TiledTextureRegions
		// ---------------------------------------------------------------------
		
		if (giraffeTiledTextureRegion == null) {
			String filename = "giraffe_sprite_sheet.png"; 
			int width = 1918;
			int height = 320;
			int cols = 7;
			int rows = 1;
			BuildableBitmapTextureAtlas bbTA = new BuildableBitmapTextureAtlas(engine.getTextureManager(), width, height, TextureOptions.BILINEAR);
			giraffeTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(bbTA, context, filename, cols, rows);
			try {
				bbTA.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
			} catch (TextureAtlasBuilderException e) {
				//e.printStackTrace();
				Debug.e(e);
			}
			bbTA.load();
		}
		
	}

	private void unloadWorld002Textures() {
		
		if (backgroundSunTextureRegion != null) {
			if (backgroundSunTextureRegion.getTexture().isLoadedToHardware()) {
				backgroundSunTextureRegion.getTexture().unload(); // remove the corresponding texture atlas from memory
				backgroundSunTextureRegion = null;
			}
		}
		
		if (giraffeTextureRegion != null) {
			if (giraffeTextureRegion.getTexture().isLoadedToHardware()) {
				giraffeTextureRegion.getTexture().unload(); // remove the corresponding texture atlas from memory
				giraffeTextureRegion = null;
			}
		}

		if (giraffeLayerTextureRegion != null) {
			if (giraffeLayerTextureRegion.getTexture().isLoadedToHardware()) {
				giraffeLayerTextureRegion.getTexture().unload(); // remove the corresponding texture atlas from memory
				giraffeLayerTextureRegion = null;
			}
		}
		
		if (giraffeTiledTextureRegion != null) {
			if (giraffeTiledTextureRegion.getTexture().isLoadedToHardware()) {
				giraffeTiledTextureRegion.getTexture().unload(); // remove the corresponding texture atlas from memory
				giraffeTiledTextureRegion = null;
			}
		}
		
		System.gc(); // once all textures have been unloaded, attempt to invoke the Garbage Collector
	}
	
	private void loadWorld003Textures() {

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		// ---------------------------------------------------------------------
		// Background TextureRegions
		// ---------------------------------------------------------------------
		
		if (backgroundMarTextureRegion == null) {
			String filename = "bg_mar.png"; 
			int width = 960;
			int height = 540;
			BuildableBitmapTextureAtlas textureAtlas = new BuildableBitmapTextureAtlas(engine.getTextureManager(), width, height, BitmapTextureFormat.RGBA_8888, TextureOptions.BILINEAR);
			backgroundMarTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, context, filename);
			try {
				textureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
			} catch (TextureAtlasBuilderException e) {
				//e.printStackTrace();
				Debug.e(e);
			}
			textureAtlas.load();
		}
		
		// ---------------------------------------------------------------------
		// Sprite TextureRegions
		// ---------------------------------------------------------------------
		
		// ---------------------------------------------------------------------
		// AnimatedSprite TiledTextureRegions
		// ---------------------------------------------------------------------
		
//		if (eselTiledTextureRegion == null) {
//			String filename = "esel_sprite_sheet.png"; //TODO
//			int width = 1918;	//TODO
//			int height = 320;	//TODO
//			int cols = 7;		//TODO
//			int rows = 1;		//TODO
//			BuildableBitmapTextureAtlas bbTA = new BuildableBitmapTextureAtlas(engine.getTextureManager(), width, height, TextureOptions.BILINEAR);
//			eselTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(bbTA, context, filename, cols, rows);
//			try {
//				bbTA.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
//			} catch (TextureAtlasBuilderException e) {
//				//e.printStackTrace();
//				Debug.e(e);
//			}
//			bbTA.load();
//		}
		
	}
	
	private void unloadWorld003Textures() {
		
		if (backgroundMarTextureRegion != null) {
			if (backgroundMarTextureRegion.getTexture().isLoadedToHardware()) {
				backgroundMarTextureRegion.getTexture().unload(); // remove the corresponding texture atlas from memory
				backgroundMarTextureRegion = null;
			}
		}
		
		System.gc(); // once all textures have been unloaded, attempt to invoke the Garbage Collector
	}
	

	/* *************************************************************************
	 * Multimedia
	 */
	
	private void loadSounds() {
		
		// --------------------------------------------------------------------- 
		// Music 
		// --------------------------------------------------------------------- 
		
		MusicFactory.setAssetBasePath("sfx/");
		
		if (music == null) {
			String filename = "zeigt_her_eure_fuesse.mp3";
			try {
				music = MusicFactory.createMusicFromAsset(engine.getMusicManager(), context, filename);
				music.setLooping(true);
			} catch (IllegalStateException e) {
				//e.printStackTrace();
				//Debug.e(e);
				Log.e("Music Load","Exception:" + e.getMessage());
			} catch (IOException e) {
				//e.printStackTrace();
				//Debug.e(e);
				Log.e("Music Load","Exception:" + e.getMessage());
			} 
		}
		 
		
		// --------------------------------------------------------------------- 
		// Sounds 
		// --------------------------------------------------------------------- 
		
		SoundFactory.setAssetBasePath("sfx/");
		
		if (clickSound == null) {
			String filename = "click.mp3"; 
			try {
				clickSound	= SoundFactory.createSoundFromAsset(engine.getSoundManager(), context, filename);			 
			} catch (final IOException e) {
				Log.v("Sounds Load","Exception:" + e.getMessage());
			}
		}
	}
	
	private void unloadSounds() {
		
		/* @see /AndroidGame/docs/cookbook/recipes/8987OS_01_Code/ResourceManager.java */
		if (!clickSound.isReleased()) {
			clickSound.release();
		}
		
		/*
		 *  Alternative:
		 *  @see /AndroidGame/docs/cookbook/recipes/8987OS_05_Code/ResourceManager.java 
		 */
//		if (clickSound != null) {
//			if(clickSound.isLoaded()) {
//				clickSound.stop();
//				engine.getSoundManager().remove(clickSound);
//				clickSound = null;
//			}
//		}
	}
	
	private void loadFonts() {
		
		FontFactory.setAssetBasePath("fonts/");
		
		// --------------------------------------------------------------------- 
		// Preset Fonts 
		// ---------------------------------------------------------------------
		
		// Stroke
		if (font == null) {
			font = FontFactory.createStroke(engine.getFontManager(), engine.getTextureManager(), 256, 256, Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD), 50, true, Color.WHITE_ABGR_PACKED_INT, 2, Color.BLACK_ABGR_PACKED_INT);
			font.load();
		}
				
		// Fonts without Stroke
		if (fontDefault24Normal == null) {
			fontDefault24Normal = FontFactory.create(engine.getFontManager(), engine.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL),  24f, true, Color.CYAN_ABGR_PACKED_INT);
			fontDefault24Normal.load();
		}
		
		if (fontDefault32Bold == null) {
			fontDefault32Bold = FontFactory.create(engine.getFontManager(), engine.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD),  32f, true, Color.WHITE_ARGB_PACKED_INT);
			fontDefault32Bold.load();
		}
		
		if (fontDefault72Bold == null) {
			fontDefault72Bold = FontFactory.create(engine.getFontManager(), engine.getTextureManager(), 512, 512, Typeface.create(Typeface.DEFAULT, Typeface.BOLD),  72f, true, Color.WHITE_ARGB_PACKED_INT);
			fontDefault72Bold.load();
		}
		
		// --------------------------------------------------------------------- 
		// True Type Fonts (custom font from assets)
		// --------------------------------------------------------------------- 
		
		if (fontHarting96 == null) {
			String filename = "harting.ttf"; 
			fontHarting96 = FontFactory.createFromAsset(engine.getFontManager(), engine.getTextureManager(), 256, 256, context.getAssets(), filename, 96f, true, Color.GREEN_ARGB_PACKED_INT);
			fontHarting96.load();
		}
		
//		Font fontHarting48;
//		if(fontHarting48 == null) {
//			String filename = "X_SCALE_by_Factor_i.ttf"; 
//			fontHarting48 = getFont(Typeface.createFromAsset(activity.getAssets(), filename), 48f, true);
//			fontHarting48.load();
//			
//		}
		
	}
	
	private void unloadFonts() {
		
		if (font != null) {
			font.unload();
			font = null;
		}
		
		if(fontDefault24Normal != null) {
			fontDefault24Normal.unload();
			fontDefault24Normal = null;
		}
		
		if(fontDefault32Bold != null) {
			fontDefault32Bold.unload();
			fontDefault32Bold = null;
		}
		
		if(fontDefault72Bold != null) {
			fontDefault72Bold.unload();
			fontDefault72Bold = null;
		}
		
		if(fontHarting96 != null) {
			fontHarting96.unload();
			fontHarting96 = null;
		}
	}
	
	
	/* *************************************************************************
	 * PureData
	 */
	
	/**
	 * PureData (PD)
	 * 
	 * @see PureData:,
	 * 		{@link http://puredata.info/},
	 * 		{@link http://en.flossmanuals.net/PureData/},
	 * 		{@link http://www.youtube.com/watch?v=yKK1lwddfyM&list=PL12DC9A161D8DC5DC}
	 * 
	 * @see libPd:,
	 * 		{@link http://puredata.info/community/projects/software/libpd},
	 * 		{@link http://puredata.info/downloads/libpd},
	 * 		{@link http://libpd.cc/},
	 * 		{@link https://github.com/libpd/pd-for-android},
	 * 		Making Musical Apps - Real-time audio synthesis on Android and iOS, Peter Brinkmann, O'Reilly,
	 * 		{@link http://shop.oreilly.com/product/0636920022503.do}
	 * 
	 * -------------------------------------------------------------------------
	 * 
	 * IMPLEMENTATION:
	 * Project > Properties > Android > add Libraries: 
	 * PdCore
	 * initPd(){PdAudio.initAudio(...); PdBase.setReceiver(new PdUiDispatcher()); PdBase.openPatch(...)}
	 * onResumeGame(){PdAudio.startAudio(this);}
	 * onPauseGame(){PdAudio.stopAudio();}
	 * 
	 * triggerNote(int n) {PdBase.sendFloat("midinote", n); PdBase.sendBang("trigger");}
	 */
	public void initPd() throws IOException {
		// configure the audio glue
		int sampleRate = AudioParameters.suggestSampleRate();
		int inChannels = 0;	// needs permission android.permission.RECORD_AUDIO
		int outChannels = 2;
		PdAudio.initAudio(sampleRate, inChannels, outChannels, 8, true);
		
		pd = new PdUiDispatcher();
		PdBase.setReceiver(pd);
		
		// load the patch from res/raw/patch.zip
		File dir = context.getFilesDir();
		IoUtils.extractZipResource(context.getResources().openRawResource(R.raw.patch), dir, true);
		File patchFile = new File(dir, "topaz.pd");
		PdBase.openPatch(patchFile.getAbsolutePath());
	}
	
	/**
	 * Events | Messages:
	 * PdBase.sendBang()
	 * PdBase.sendFloat()
	 * PdBase.sendList()
	 * PdBase.sendMessage()
	 * PdBase.sendSymbol()
	 * 
	 * MIDI-Events:
	 * PdBase.sendNoteOn()
	 * PdBase.sendControlChenge()
	 * PdBase.sendPitchBend()
	 * PdBase.sendProgramChange()
	 */
	public void triggerNote(int n) {
		// init synthesizer parameter
		PdBase.sendFloat("mod", 0);
		PdBase.sendFloat("cutoff", 100);
		PdBase.sendFloat("resonance", 5);
		
		PdBase.sendFloat("midinote", n);
		PdBase.sendBang("trigger");
	}
	
	// -------------------------------------------------------------------------
	
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
	public static ParticleSystem createParticleSystem() {
		
		// ITextureRegion
		ITextureRegion particleTextureRegion = ResourceManager.getInstance().particleTextureRegion;
		
		// ParticleEmitter
		getInstance().particleEmitter = new PointParticleEmitter(getInstance().cameraWidth * 0.5f, getInstance().cameraHeight * 0.5f);
		
		// ParticleSystem
		float minSpawnRate = 10;
		float maxSpawnRate = 20;
		int maxParticlesCount = 200;
		getInstance().particleSystem = new BatchedSpriteParticleSystem(getInstance().particleEmitter, minSpawnRate, maxSpawnRate, maxParticlesCount, particleTextureRegion, getInstance().engine.getVertexBufferObjectManager());
		
		// ParticleInitializer
		getInstance().particleSystem.addParticleInitializer(new ColorParticleInitializer<UncoloredSprite>(0, 1, 0, 0, 0, 0.3f));
		getInstance().particleSystem.addParticleInitializer(new AlphaParticleInitializer<UncoloredSprite>(0, 0.8f));
		getInstance().particleSystem.addParticleInitializer(new BlendFunctionParticleInitializer<UncoloredSprite>(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)); // (GLES20.GL_SRC_ALPHA, GLES20.GL_ONE)
		getInstance().particleSystem.addParticleInitializer(new RotationParticleInitializer<UncoloredSprite>(0.0f, 360.0f));
		getInstance().particleSystem.addParticleInitializer(new ScaleParticleInitializer<UncoloredSprite>(0.5f, 1.5f));
		getInstance().particleSystem.addParticleInitializer(new GravityParticleInitializer<UncoloredSprite>());
		getInstance().particleSystem.addParticleInitializer(new AccelerationParticleInitializer<UncoloredSprite>(-25, 25, 25, 50));
		getInstance().particleSystem.addParticleInitializer(new VelocityParticleInitializer<UncoloredSprite>(-25, 25, 25, 50)); // individual particles move at a constant speed when spawned
		getInstance().particleSystem.addParticleInitializer(new ExpireParticleInitializer<UncoloredSprite>(10, 15)); // MUST HAVE (use this initializer or the OffCameraExpireParticleModifier to limit the lifetime of particles, because the particles count in the ParticleSystem is limited: 'old' particles have to be destroyed that new ones can be spawned) 
		
		// ParticleModifier (modify individual particles in their lifetime)
		float fromTime = 0f;
		float toTime = 3.0f;
		getInstance().particleSystem.addParticleModifier(new ColorParticleModifier<UncoloredSprite>(fromTime, toTime, 1, 1, 0, 0.5f, 0, 0));
		getInstance().particleSystem.addParticleModifier(new ColorParticleModifier<UncoloredSprite>(4, 6, 1, 1, 0.5f, 1, 0, 1));
		getInstance().particleSystem.addParticleModifier(new AlphaParticleModifier<UncoloredSprite>(0, 1, 0, 1));
		getInstance().particleSystem.addParticleModifier(new AlphaParticleModifier<UncoloredSprite>(5, 6, 1, 0));
		getInstance().particleSystem.addParticleModifier(new RotationParticleModifier<UncoloredSprite>(2, 4, 0f, 180f));
		getInstance().particleSystem.addParticleModifier(new ScaleParticleModifier<UncoloredSprite>(0, 5, 1.0f, 2.0f));
		//mParticleSystem.addParticleModifier(new OffCameraExpireParticleModifier<UncoloredSprite>(mCamera); // alternative to ExpireParticleInitializer
		getInstance().particleSystem.addParticleModifier(new IParticleModifier<UncoloredSprite>() {
			
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
		
		return getInstance().particleSystem;
		
		// do in scene:
		// make visible (attach to scene)
		//this.attachChild(mParticleSystem);
		
		// a particleSystem is an entity! you can register modifiers ...
		//mParticleEmitter.setCenter(CAMERA_WIDTH * 0.5f, CAMERA_HEIGHT * 0.5f);
		
		//mParticleSystem.setParticlesSpawnEnabled(false); // turn on/off
		//particleSystem.removeParticleInitializer(pi);
		//particleSystem.removeParticleModifier(pm);	
	}
	
	public PhysicsWorld getPhysicsWorld() {

		if (getInstance().physicsWorld == null) {
			return createPhysicsWorld();
		} else {
			return getInstance().physicsWorld;
		}	
	}
	
	/**
	 * 
	 * @return FixedStepPhysicsWorld
	 */
	public static PhysicsWorld createPhysicsWorld() {
		return new FixedStepPhysicsWorld(STEPS_PER_SECOND, new Vector2(0f, SensorManager.GRAVITY_EARTH), false, 8, 3);
	}
	
	// -------------------------------------------------------------------------
	
	/**
	 * R.U.B.E. (Realy Useful Box2D Editor)
	 * Uses AndEngineRubeLoaderExtension from Nazgee.
	 * 
	 * Read a JSON-file from Rube Editor and 
	 * create Bodies, Fixtures, Shapes in the Physics World.
	 * 
	 * JSON files must be placed in res/raw directory!
	 * 
	 * @see https://github.com/nazgee/AndEngineRubeLoaderExtension
	 * @see https://github.com/nazgee/ae-stub/tree/rube-loader-example
	 * 
	 * @return a Game Level Scene
	 */
	public static RubeDef loadRubeLevel(Scene scene, int pResourceID) {
		
		// INFO
		// scale pixels to meters in the Physics World:
		// 1 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT
		// Y-axis is reversed: scene.setScale(1, -1);
		
		// ResourceManager must implement ITextureProvider and override its get() method
		// get() returns the TextureRegions from textureMap, 
		// so textureMap must be populated before (using atlas) 
		IEntityFactory entityFactory = new EntityFactory(scene, getInstance(), getInstance().vbom);
		
		// Loader
		RubeLoader loader = new RubeLoader(entityFactory);
		RubeDef rubeDef = loader.load(getInstance().activity.getResources(), scene, getInstance(), getInstance().vbom, pResourceID);
		
		scene.registerUpdateHandler(rubeDef.worldProvider.getWorld());
		
		// usage in GameScene:
		// Body
		//Body badgeBody = rubeDef.getBodyByName("badge");
		//IEntity badgeEntity = rubeDef.getImageByName("image0");
		
		return rubeDef;	
	}
	
	public void loadRubeTextures(String filenames[]) {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		mBitmapTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);

		for (String f : filenames) {
			loadAndStore(f);
		}
		
		try {
			mBitmapTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 1));
			mBitmapTextureAtlas.load();
		} catch (TextureAtlasBuilderException e) {
			Debug.e(e);
		}
	}
	
	private void loadAndStore(final String pName) {
		ITextureRegion region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, context, pName);
		mTexturesMap.put(pName, region);
	}

	@Override
	public ITextureRegion get(String pFileName) {
		return mTexturesMap.get(pFileName);
	}
	
	// -------------------------------------------------------------------------
	
	/**
	 * TexturePacker
	 * 
	 * No extension required; classes included in GLES2-AnchorCenter branch.
	 * @see org.andengine.util.texturepack
	 * @see /AndEngineExamples/src/org/andengine/examples/TexturePackExample.java
	 * 
	 * @param filename
	 * @return
	 */
	public static TexturePackTextureRegionLibrary createTexturePackerLibrary(String filename) {
		
		TexturePackTextureRegionLibrary tpl = null;
		
		try {
			TexturePackLoader loader = new TexturePackLoader(ResourceManager.getInstance().activity.getAssets(), ResourceManager.getInstance().activity.getTextureManager());
			TexturePack tp = loader.loadFromAsset(filename, filename.split("/")[0]); // TODO greedy split; split at last occurence of "/"; if "/" does not exist in filename ?!
			tp.loadTexture();
			tpl = tp.getTexturePackTextureRegionLibrary();
		} catch (TexturePackParseException e) {
			e.printStackTrace();
		}
		
		return tpl;
	}
	
	// -------------------------------------------------------------------------
	
	/* *************************************************************************
	 * Factories
	 */
	
	/**
	 * TextureAtlas<br />
	 * <br />
	 * 
	 * Constraints: max size is 1024 x 1024 px<br />
	 * <br />
	 * 
	 * <b>OBJECTS:</b><br />
	 * <br />
	 * 
	 * <b>OPTIONS:</b><br />
	 * BitmapTextureFormat.RGBA_8888  - 32bit with alpha-channel<br />
	 * BitmapTextureFormat.RGBA_4444  - 16bit with alpha-channel<br />
	 * BitmapTextureFormat.RGB_565    - 16bit without alpha; green-channel is more important for human eye (6bit)<br />
	 * <br />
	 * 
	 * <b>TextureOptions</b> contains 3 flags:<br />
	 * 1) interpolation<br />
	 *    TextureOptions.NEAREST<br />
	 *    TextureOptions.BILINEAR<br />
	 * <br />
	 * 2) alpha channel settings<br />
	 *    TextureOptions.PREMULTIPLYALPHA<br />
	 * 3) repeating<br />
	 *    TextureOptions.REPEATING<br />
	 * <br />
	 * eg.<br />
	 * TextureOptions.REPEATING_BILINEAR_PREMULTIPLYALPHA<br />
	 * <br />
	 * 
	 * @return
	 */
	public static ITextureRegion createTextureAtlas(int width, int height, ITextureRegion texture, String filename) {
		
		if (texture == null) {
			BuildableBitmapTextureAtlas atlas = new BuildableBitmapTextureAtlas(ResourceManager.getInstance().engine.getTextureManager(), width, height, BitmapTextureFormat.RGBA_8888, TextureOptions.BILINEAR);
			texture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, ResourceManager.getInstance().context, filename);
			
			try {
				atlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
				atlas.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}
		
		return texture;
	}
	
	public static void populateTextureAtlas(BuildableBitmapTextureAtlas atlas, HashMap<String, ITextureRegion> map) {
		
		for (String i : map.keySet()) {
			BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, ResourceManager.getInstance().context, i);
		}
	}
	
	public static void loadTextureAtlas(BuildableBitmapTextureAtlas atlas) {
		try {
			atlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
			atlas.load();
		} catch (TextureAtlasBuilderException e) {
			Debug.e(e);
		}
	}
	
	// -------------------------------------------------------------------------
	
	/**
	 * Factory for ITextureRegions<br />
	 * <br />
	 * 
	 * OBJECTS:<br />
	 * ITextureRegion<br />
	 * ITiledTextureRegion<br />
	 * <br />
	 * 
	 * NOTE:<br />
	 * Texture size must not exceed 1024x1024 px<br />
	 * <br />
	 * 
	 * @return
	 */
	public static ITextureRegion createTexture() {
		// TODO
		return null;
	}
	
	public static ITiledTextureRegion createTiledTexture() {
		// TODO
		return null;
	}
	
	/**
	 * Uses AndEngineSVGTextureRegionExtension by Nicolas Gramlich.
	 * 
	 * @see /AndEngineExamples/src/org/andengine/examples/SVGTextureRegionExample.java
	 * @see /AndroidGame/_sources/docs/cookbook/recipes/8987OS_09_Code/SVG Texture region code/WorkingWithSVG.java
	 * 
	 * @param w width
	 * @param h height
	 * @param filename
	 * @return the TextureRegion
	 */
	public ITextureRegion createTextureFromSVG(int width, int height, String filename) {
		
		SVGBitmapTextureAtlasTextureRegionFactory.setAssetBasePath("svg/");
		
		BuildableBitmapTextureAtlas atlas = new BuildableBitmapTextureAtlas(engine.getTextureManager(), width, height, TextureOptions.BILINEAR);
		
		ITextureRegion textureRegion = SVGBitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, filename, width, height);
		
		try {
			atlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
			atlas.load();
		} catch (TextureAtlasBuilderException e) {
			e.printStackTrace();
		}
		
		return textureRegion;
	}
	
	// -------------------------------------------------------------------------
	
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
	
	/**
	 * Physics<br />
	 * Box2D Bodies<br />
	 * <br />
	 * 
	 * StaticBody<br />
	 * KinematicBody<br />
	 * DynamicBody<br />
	 * <br />
	 * Category Filtered Body<br />
	 * Multiple Fixtures Body<br />
	 * Unique Body by specifying vertices<br />
	 * <br />
	 * 
	 * @see /AndroidGame/src/com/floppyinfant/android/game/scenes/levels/physics/AbstractGameScene.java
	 */
	public static Body createBody(Entity forEntity, BodyType type, FixtureDef fixtureDef, Shape shape) {
		
		Body body = null;
		
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
		
		
		/* Multiple Fixture Bodies */
		
		
		/* Unique Bodies by specifying vertices */
		
		
		return body;
	}
	
	/**
	 * 
	 * @see AndEngine Cookbook, Ch. 6, p. 203, "Creating unique bodies by specifyfing vertices"
	 * 
	 * @param vertices
	 * @return
	 */
	public Body createBody(Vector2[] vertices) {
		
		float x = 400f;
		float y = 260f;
		
		List<Vector2> list = new ArrayList<Vector2>();
		list.addAll((List<Vector2>) ListUtils.toList(vertices));
		
		List<Vector2> verticesTriangulated = new EarClippingTriangulator().computeTriangles(list);
		
		float[] MeshTriangles = new float[verticesTriangulated.size() * 3];
		for(int i = 0; i < verticesTriangulated.size(); i++) {
			MeshTriangles[i * 3] = verticesTriangulated.get(i).x;
			MeshTriangles[i * 3 + 1] = verticesTriangulated.get(i).y;
			verticesTriangulated.get(i).mul(1/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
		}
		// TODO return shape
		Mesh mesh = new Mesh(x, y, MeshTriangles, verticesTriangulated.size(), DrawMode.TRIANGLES, engine.getVertexBufferObjectManager());
		mesh.setColor(1f, 0f, 0f);
		//mScene.attachChild(mesh);
		
		// body
		FixtureDef uniqueBodyFixtureDef = PhysicsFactory.createFixtureDef(20f, 0.5f, 0.5f);
		Body body = PhysicsFactory.createTrianglulatedBody(physicsWorld, mesh, verticesTriangulated, BodyType.DynamicBody, uniqueBodyFixtureDef);
		//physicsWorld.registerPhysicsConnector(new PhysicsConnector(mesh, body));
		
		return body;
	}
	
	public Body createMultiFixtureBody() {

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
		return null;
	}
	
	public Fixture createFixture() {
		return null;
	}
	
	public void createJoint(Body a, Body b) {
		
	}
	
	// -------------------------------------------------------------------------
	
	/**
	 * <b>OBJECTS:</b><br />
	 * EntityBackground<br />
	 * SpriteBackground<br />
	 * RepeatingSpriteBackground<br />
	 * ParallaxBackground<br />
	 * AutoParallaxBackground<br />
	 * <br />
	 * 
	 * NOTE:<br />
	 * Repeating textures must have a dimension with a power of two (2^^x), which is required by OpenGLs wrap mode.<br />
	 * <br />
	 * 
	 */
	public static void createBackground() {

		// ---------------------------------------------------------------------
		// RepeatingSpriteBackground
		// ---------------------------------------------------------------------
		// Repeating textures must have a dimension with a power of two (2^^x), 
		// which is required by OpenGLs wrap mode.
		
		/*
		if (gameBackgroundRepeatingSpriteTextureRegion == null) {
			String filename = "gfx/bg_repeating.png"; 
			AssetBitmapTexture bitmapTexture = null;
			try {
				bitmapTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), filename, BitmapTextureFormat.RGB_565, TextureOptions.REPEATING_BILINEAR);
			} catch (IOException e) {
				//e.printStackTrace();
				Debug.e(e);
			}
			bitmapTexture.load();
			
			gameBackgroundRepeatingSpriteTextureRegion = TextureRegionFactory.extractFromTexture(bitmapTexture);
		}
		*/
	}
	
	/**
	 * 
	 * @param scene
	 * @param far
	 * @param mid
	 * @param close
	 */
	public static void createParallaxBackground(Scene scene, IEntity far, IEntity mid, IEntity close) {
		// ParallaxBackground bound to camera position
		ParallaxBackground background = new ParallaxBackground(0f, 0f, 1.0f) {
			float cameraPreviousX = 0;
			//float cameraPreviousY = 0;
			float parallaxValueOffset = 0;
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				float cameraCurrentX = ResourceManager.getInstance().engine.getCamera().getCenterX();
				// if the cameras position has changed since last update...
				if (cameraPreviousX != cameraCurrentX) {
					parallaxValueOffset += cameraCurrentX - cameraPreviousX;
					this.setParallaxValue(parallaxValueOffset);
				}
				cameraPreviousX = cameraCurrentX;
				super.onUpdate(pSecondsElapsed);
			}
		};
		
		background.attachParallaxEntity(new ParallaxEntity(5, far));
		background.attachParallaxEntity(new ParallaxEntity(10, mid));
		background.attachParallaxEntity(new ParallaxEntity(15, close));
		scene.setBackground(background);
		scene.setBackgroundEnabled(true);
	}
	
	/**
	 * 
	 * @param scene
	 * @param speed - changes per second
	 */
	public static void createAutoParallaxBackground(Scene scene, float speed) {
		// side-scrolling game
		AutoParallaxBackground background = new AutoParallaxBackground(0f, 0f, 1.0f, speed);
		scene.setBackground(background);
		scene.setBackgroundEnabled(true);
		
	}
	
	/**
	 * Generate a TMX Tiled Map from XML.
	 * include Project AndEngineTMXTiledMapExtension
	 * @see /AndEngineExamples/src/org/andengine/examples/TMXTiledMapExample.java
	 * 
	 * @param path
	 * @return
	 */
	public static TMXTiledMap createTMXTiledMap(String path) {
		
		// TODO map as member/ field
		TMXTiledMap map = null;
		
		try {
			TMXLoader loader = new TMXLoader(res.activity.getAssets(), res.engine.getTextureManager(), TextureOptions.BILINEAR_PREMULTIPLYALPHA, res.vbom);
			map = loader.loadFromAsset(path);
			map.setOffsetCenter(0, 0);
		} catch (final TMXLoadException e) {
			Debug.e(e);
		}
		
		return map;
	}
	
	// -------------------------------------------------------------------------
	
	public static Music createMusic(String filename) {
		MusicFactory.setAssetBasePath("sfx/");
		
		Music music = null;
		try {
			music = MusicFactory.createMusicFromAsset(ResourceManager.getInstance().engine.getMusicManager(), ResourceManager.getInstance().context, filename);
			music.setLooping(true);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return music;
	}

	public static Sound createSound(String filename) {
		SoundFactory.setAssetBasePath("sfx/");
		
		Sound snd = null;
		try {
			snd = SoundFactory.createSoundFromAsset(ResourceManager.getInstance().engine.getSoundManager(), ResourceManager.getInstance().context, filename);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return snd;
	}
	
	// -------------------------------------------------------------------------
	
	/**
	 * Factory for Fonts<br />
	 * <br />
	 * 
	 * OBJECTS:<br />
	 * Font<br />
	 * <br />
	 * 
	 * Factory-Methods:<br />
	 * create()<br />
	 * createStroke()<br />
	 * createFromAssets()<br />
	 * <br />
	 * 
	 * @param filename
	 * @return
	 */
	public static Font createFont(String filename, float size, int color) {
		Font font = null;
		
		if (filename == null) {
			// create
			font = FontFactory.create(ResourceManager.getInstance().engine.getFontManager(), ResourceManager.getInstance().engine.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL),  size, true, color);
			font.load();
		} else {
			// from asset
			FontFactory.setAssetBasePath("fonts/");
			font = FontFactory.createFromAsset(ResourceManager.getInstance().engine.getFontManager(), ResourceManager.getInstance().engine.getTextureManager(), 256, 256, ResourceManager.getInstance().context.getAssets(), filename, size, true, color);
			font.load();
		}
		
		return font;
	}
	
	
	/**
	 * 
	 * @param size
	 * @param color
	 * @return
	 */
	public static Font createSystemFont(float size, int color) {
		
		Font font = FontFactory.create(ResourceManager.getInstance().engine.getFontManager(), ResourceManager.getInstance().engine.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL),  size, true, color);
		font.load();
		
		return font;
	}
	
	/**
	 * 
	 * @param size
	 * @param color
	 * @return
	 */
	public static Font createSystemFontWithStroke(float size, int color) {
		
		Font font = FontFactory.createStroke(getInstance().activity.getFontManager(), getInstance().activity.getTextureManager(), 512, 256, Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD), 50, true, Color.WHITE_ABGR_PACKED_INT, 2, Color.BLACK_ABGR_PACKED_INT);
		font.prepareLetters("01234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ.,!?".toCharArray());
		font.load();
		
		return font;
	}
	
	/**
	 * 
	 * @param filename
	 * @param size
	 * @param color
	 * @return
	 */
	public static Font createTrueTypeFont(String filename, int size, int color) {
		
		FontFactory.setAssetBasePath("fonts/");
		
		Font font = FontFactory.createFromAsset(ResourceManager.getInstance().engine.getFontManager(), ResourceManager.getInstance().engine.getTextureManager(), 256, 256, ResourceManager.getInstance().context.getAssets(), filename, size, true, color);
		font.load();
		
		return font;
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param font
	 * @param text
	 * @return
	 */
	public static Text createText(int x, int y, Font font, String text) {
		Text txt = null;
		
		txt = new Text(x, y, font, text, new TextOptions(HorizontalAlign.CENTER), ResourceManager.getInstance().vbom);
		txt.setText(text);
		
		return txt;
	}
	
}
