package com.floppyinfant.android.game.scenes.levels.rube;

import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.scene.Scene;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.rubeloader.ITextureProvider;
import org.andengine.extension.rubeloader.RubeLoader;
import org.andengine.extension.rubeloader.def.RubeDef;
import org.andengine.extension.rubeloader.factory.EntityFactory;
import org.andengine.extension.rubeloader.factory.IEntityFactory;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.PinchZoomDetector;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.texturepack.TexturePack;
import org.andengine.util.texturepack.TexturePackLoader;
import org.andengine.util.texturepack.TexturePackTextureRegionLibrary;
import org.andengine.util.texturepack.exception.TexturePackParseException;

import android.content.res.Resources;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.floppyinfant.android.game.R;
import com.floppyinfant.android.game.manager.ResourceManager;
import com.floppyinfant.android.game.scenes.levels.rube.BodyDragger;

/**
 * AndEngineRubeLoader by nazgee
 * 
 * @see https://github.com/nazgee/AndEngineRubeLoaderExtension
 * @see https://github.com/nazgee/ae-stub/tree/rube-loader-example
 * @see http://www.andengine.org/forums/features/rube-loader-project-t10860.html
 * 
 */
public class RubeScene extends AbstractGameScene {
	
	ResourceManager res = ResourceManager.getInstance();
	
	protected RubeDef rd;
	
	/**
	 * 
	 * @param pResourceID - R.raw.<name>, a RUBE JSON file;
	 * CAVE: use full path-names for image-file-attribut in json
	 */
	public RubeScene(int pResourceID, String[] filenames) {
		
		/*
		// TexturePacker
		// TODO: move to ResourceManager::createAtlas()
		
		//BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		//res.atlas = new BuildableBitmapTextureAtlas(res.engine.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		
		// load and store textures
		//res.put("");
		// ...
		try {
			TexturePackLoader loader = new TexturePackLoader(ResourceManager.getInstance().activity.getAssets(), ResourceManager.getInstance().activity.getTextureManager());
			TexturePack tp = loader.loadFromAsset("tex/demo_tp.xml", "tex/");
			tp.loadTexture();
			res.tpl = tp.getTexturePackTextureRegionLibrary();
		} catch (TexturePackParseException e) {
			e.printStackTrace();
		}
		
		//res.tpl = ResourceManager.createTexturePackerLibrary("tex/demo_tp.xml");
		//res.textureMap = res.tpl.getSourceMapping();
		
		// populate HashMap used by EntityFactory (calling implemented get(string) of ITextureProvider)
		for (int i = 0; i < res.tpl.getIDMapping().size(); i++) {
			res.put(res.tpl.get(i).getSource(), res.tpl.get(i));
		}		
		// ---------------------------------------------------------------------
		*/
		
		// createResources
		res.loadRubeTextures(filenames);
		
		// createScene from RUBE
		rd = ResourceManager.loadRubeLevel(this, pResourceID);
		
		/**
		 * Extend EntityFactory if you need more fine-grained control over creation of entities
		 * that are to be connected to bodies. Basic EntityFactory creates UncoloredSprite for
		 * every Entity (which you might not like).
		 *
		 * EntityFactory::produce() might be a good place to create textured polygons if you need them.
		 */
//		IEntityFactory entityFactory = new EntityFactory(this, res, res.vbom);

		/**
		 * Loader is an object that will build RUBE worlds for you. If you are using
		 * custom IEntityFactory pass it via Loader's constructors. If you do not have your
		 * custom IEntityFactory use a default Loader() constructor (default EntityFactory will
		 * be created under the hood and used by Loader).
		 * 
		 * Under the hood it uses RubeParser to parser json files.
		 */
//		RubeLoader loader = new RubeLoader(entityFactory);

		/**
		 * This is how resource is converted to RubeDef (i.e. world is loaded).
		 * 
		 * RubeDef is an object that allows you to query for objects/entities/fixtures and much more.
		 * 
		 * It is extremely messy so far and is pending a rewrite (e.g. creation of fance getters) but
		 * I'm not able to do it right now. Sorry.
		 */
//		RubeDef rubeDef = loader.load(res.activity.getResources(), this, res, res.vbom, pResourceID);

		/**
		 * Remember to register your world! otherwise it will not be updated!
		 */
//		registerUpdateHandler(rubeDef.worldProvider.getWorld());

		// --------------------------------------------------------------------
		
		// populateScene

		/**
		 * This is just an example how you can use RubeDef to get into
		 * the guts of world you are working with
		 */
		Body    badgeBody   = rd.getBodyByName("badge");
		IEntity badgeEntity = rd.getImageByName("image0");

		// --------------------------------------------------------------------
		
		// Demo with Touch
		
		setTouchAreaBindingOnActionDownEnabled(true);
		setTouchAreaBindingOnActionMoveEnabled(true);
		
		registerTouchArea(badgeEntity);

		BodyDragger dragger = new BodyDragger();
		dragger.setPhysicsWorld(rd.worldProvider.getWorld());

		setOnSceneTouchListener(dragger);
		setOnAreaTouchListener(dragger);
		
	}
	
	

	/**
	 * @see com.floppyinfant.android.game.scenes.levels.rube.AbstractGameScene#onLoadScene()
	 */
	@Override
	public void onLoadScene() {
		super.onLoadScene();
		
		// get player from RUBE
		/*
		Body bikeBody = rd.getBodyByName("bikechassis");
		//nullPointerException
		
		Bike bike = (Bike) rd.getImageByName("bikachassis");
		if (bike != null && bikeBody != null) {
			bike.setBody(bikeBody);
			
			// camera
			//res.camera.setBounds(0, 0, mCameraWidth * 8, mCameraHeight * 2); // TODO: apply stitched background
			//res.camera.setBoundsEnabled(true);
			res.camera.setChaseEntity(bike);
		}
		
		
		//BodyDef bd = new BodyDef();
		
		//FixtureDef fd = PhysicsFactory.createFixtureDef(1.0f, 0.2f, 1.0f);
		
		//bikeBody.createFixture(fd);
		
		// implement controlls and behavior
		// contact listener
		
		// move player
		
		
		// Schnecki level:
		// parallax background
		// bound camera, chase entity
		// physics: obstacles, money ...
		// Endgegner
		*/
			
	}



	@Override
	public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAccelerationChanged(AccelerationData pAccelerationData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onPinchZoomStarted(PinchZoomDetector pPinchZoomDetector, TouchEvent pSceneTouchEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPinchZoom(PinchZoomDetector pPinchZoomDetector, TouchEvent pTouchEvent, float pZoomFactor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPinchZoomFinished(PinchZoomDetector pPinchZoomDetector, TouchEvent pTouchEvent, float pZoomFactor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beginContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}
	
}
