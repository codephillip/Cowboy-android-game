package com.codephillip.cowboycity;

import android.util.Log;
import android.view.MotionEvent;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.JumpModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.modifier.IModifier;

import java.io.IOException;

public class MainActivity extends BaseGameActivity implements IOnSceneTouchListener {

    private static final int CAMERA_WIDTH = 800;
    private static final int CAMERA_HEIGHT = 480;

    private BitmapTextureAtlas cowboyTextureAtlas;
    private ITiledTextureRegion cowboyTiledTextureRegion;
    private AnimatedSprite cowboyAnimatedSprite;

    private BitmapTextureAtlas backgroundTextureAtlas;
    private ITextureRegion backgroundTextureRegion;
    private Sprite backgroundSprite;

    private BitmapTextureAtlas catTextureAtlas;
    private ITiledTextureRegion catTiledTextureRegion;
    private AnimatedSprite catAnimatedSprite;

    private BitmapTextureAtlas frontBitmapTextureAtlas;
    private BitmapTextureAtlas midBitmapTextureAtlas;
//    private BuildableBitmapTextureAtlas backBitmapTextureAtlas;
    private BitmapTextureAtlas backBitmapTextureAtlas;
    private ITextureRegion frontTextureRegion;
    private ITextureRegion midTextureRegion;
    private ITextureRegion backTextureRegion;

    Sprite frontBackgroundSprite;

    private Camera camera;

    boolean canGo = false;
    private boolean hasPlayed = false;
    private boolean hasPlayedJumpSound = false;

    private static final float SCROLL_FACTOR = 10;

    public static final String TAG = "Cowboy#";


    private Scene scene;
    private Sound biteSound;
    private Sound screamSound;
    private Sound jumpSound;
    private Music gameSound;
    private Music footstepSound;

    @Override
    public EngineOptions onCreateEngineOptions() {
        camera = new Camera(0,0, CAMERA_WIDTH, CAMERA_HEIGHT){

        //PARALLAX CODE
          /* Boolean value which will determine whether
             * to increase or decrease x coordinate */
        boolean incrementX = true;

            /* On camera update... */
        @Override
        public void onUpdate(float pSecondsElapsed) {

				/* Obtain the current camera X coordinate */
            final float currentCenterX = this.getCenterX();

				/* Value which will be used to offset the camera */
            float offsetCenterX = 0;

				/* If incrementX is true... */
//                if(incrementX){
//
//					/* offset the camera's x coordinate according to time passed */
//                    offsetCenterX = currentCenterX + pSecondsElapsed * SCROLL_FACTOR;
//
//					/* If the new offset coordinate is greater than the max X limit */
//                    if(offsetCenterX >= CAMERA_MAX_CENTER_X){
//
//						/* Set to decrement the camera's X coordinate next */
//                        incrementX = false;
//                    }
//                } else {
//					/* If increment is equal to false, decrement X coordinate */
//                    offsetCenterX = currentCenterX - pSecondsElapsed * SCROLL_FACTOR;
//
//					/* If the new offset coordinate is less than the min X limit */
//                    if(offsetCenterX <= CAMERA_MIN_CENTER_X){
//
//						/* Set to increment the camera's X coordinate next */
//                        incrementX = true;
//                    }
//                }


                /* If incrementX is true... */
            if(false){

					/* offset the camera's x coordinate according to time passed */
                offsetCenterX = currentCenterX + pSecondsElapsed * SCROLL_FACTOR;

					/* If the new offset coordinate is greater than the max X limit */
//                    if(offsetCenterX >= CAMERA_MAX_CENTER_X){
//
//						/* Set to decrement the camera's X coordinate next */
//                        incrementX = false;
//                    }
            } else {
//					/* If increment is equal to false, decrement X coordinate */
                offsetCenterX = currentCenterX - pSecondsElapsed * SCROLL_FACTOR;
//
//					/* If the new offset coordinate is less than the min X limit */
//                    if(offsetCenterX <= CAMERA_MIN_CENTER_X){
//
//						/* Set to increment the camera's X coordinate next */
//                        incrementX = true;
//                    }
            }

				/* Apply the offset position to the camera */
            this.setCenter(offsetCenterX, this.getCenterY());

            super.onUpdate(pSecondsElapsed);
        }
    };


        EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), camera);
        engineOptions.getAudioOptions().setNeedsSound(true);
        engineOptions.getAudioOptions().setNeedsMusic(true);
        return engineOptions;
    }

    @Override
    public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

        backgroundTextureAtlas = new BitmapTextureAtlas(mEngine.getTextureManager(), 1024, 1024, TextureOptions.DEFAULT);
        backgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(backgroundTextureAtlas, this, "parallax_background_layer_back.png", 0, 0);
        backgroundTextureAtlas.load();

        cowboyTextureAtlas = new BitmapTextureAtlas(mEngine.getTextureManager(), 640, 320, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        cowboyTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(cowboyTextureAtlas, this, "walkman640x320.png", 0, 0, 8, 1);
        cowboyTextureAtlas.load();

        catTextureAtlas = new BitmapTextureAtlas(mEngine.getTextureManager(), 384, 384, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        catTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(catTextureAtlas,this, "runningcat_reverse384.png", 0, 0, 2, 4);
        catTextureAtlas.load();

        frontBitmapTextureAtlas = new BitmapTextureAtlas(mEngine.getTextureManager(), 640, 187, TextureOptions.DEFAULT);
        frontTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(frontBitmapTextureAtlas, this, "parallax_background_layer_front.png", 0, 0);
        frontBitmapTextureAtlas.load();

        midBitmapTextureAtlas = new BitmapTextureAtlas(mEngine.getTextureManager(), 640, 85, TextureOptions.DEFAULT);
        midTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(midBitmapTextureAtlas, this, "parallax_background_layer_mid.png", 0, 0);
        midBitmapTextureAtlas.load();

        backBitmapTextureAtlas = new BitmapTextureAtlas(mEngine.getTextureManager(), 256, 128, TextureOptions.DEFAULT);
//        backBitmapTextureAtlas = new BuildableBitmapTextureAtlas(mEngine.getTextureManager(), 256, 128, TextureOptions.DEFAULT);
//        try {
//            backBitmapTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
//                    0, 0, 0));
//        } catch (ITextureAtlasBuilder.TextureAtlasBuilderException e) {
//            e.printStackTrace();
//        }
        backTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(backBitmapTextureAtlas, this, "cloud.png", 0, 0);

        /* Build the texture atlas. Since we're loading only a
		 * single image into the texture atlas, we need not
		 * worry about padding or spacing values */
//        try {
//            backBitmapTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
//                    0, 0, 0));
//        } catch (ITextureAtlasBuilder.TextureAtlasBuilderException e) {
//            e.printStackTrace();
//        }
        backBitmapTextureAtlas.load();


        try {
            biteSound = SoundFactory.createSoundFromAsset(getEngine().getSoundManager(), this, "mfx/lion.ogg");
            screamSound = SoundFactory.createSoundFromAsset(getEngine().getSoundManager(), this, "mfx/scream1.ogg");
            jumpSound = SoundFactory.createSoundFromAsset(getEngine().getSoundManager(), this, "mfx/jump3.ogg");
            gameSound = MusicFactory.createMusicFromAsset(mEngine.getMusicManager(), this, "mfx/gamemenu.mp3");
//            gameSound = MusicFactory.createMusicFromAsset(mEngine.getMusicManager(), this, "mfx/gameSound.mp3");
            footstepSound = MusicFactory.createMusicFromAsset(getEngine().getMusicManager(), this, "mfx/footsteps.ogg");
//            footstepSound = SoundFactory.createSoundFromAsset(getEngine().getSoundManager(), this, "mfx/footsteps.ogg");
            gameSound.setVolume(20.0f);
            biteSound.setVolume(1.0f);
            footstepSound.setVolume(0.5f);
            footstepSound.setLooping(true);
            gameSound.setLooping(true);
        } catch (IOException e) {
            e.printStackTrace();
        }


        pOnCreateResourcesCallback.onCreateResourcesFinished();
    }

    @Override
    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws IOException {
        /**
         * Transferred the code to onPopulateScene()
         * */
        scene = new Scene();

        final float textureHeight = frontTextureRegion.getHeight();

        /* Create the hill which will appear to be the furthest
		 * into the distance. This Sprite will be placed higher than the
		 * rest in order to retain visibility of it */
        Sprite backBackgroundSprite = new Sprite(CAMERA_WIDTH * 0.5f, textureHeight * 0.5f + 250, backTextureRegion,
                mEngine.getVertexBufferObjectManager());

		/* Create the hill which will appear between the furthest and closest
		 * hills. This Sprite will be placed higher than the closest hill, but
		 * lower than the furthest hill in order to retain visibility */
        Sprite midBackgroundSprite = new Sprite(CAMERA_WIDTH * 0.5f, textureHeight * 0.5f + 75, midTextureRegion,
                mEngine.getVertexBufferObjectManager());

		/* Create the closest hill which will not be obstructed by any other hill
		 * Sprites. This Sprite will be placed at the bottom of the Scene since
		 * nothing will be covering its view */
        frontBackgroundSprite = new Sprite(CAMERA_WIDTH * 0.5f, textureHeight * 0.5f + 35, frontTextureRegion,
                mEngine.getVertexBufferObjectManager());

        final float positionX = CAMERA_WIDTH * 0.5f;
        final float positionY = CAMERA_HEIGHT * 0.5f;

        backgroundSprite = new Sprite(positionX, positionY, backgroundTextureRegion, mEngine.getVertexBufferObjectManager());


        /* Create the ParallaxBackground, setting the color values to represent
		 * a blue sky */
        ParallaxBackground background = new ParallaxBackground(0.3f, 1f, 0.9f) {

            /* We'll use these values to calculate the
             * parallax value of the background */
            float cameraPreviousX = 0;
            float parallaxValueOffset = 0;

            /* onUpdates to the background, we need to calculate new
             * parallax values in order to apply movement to the background
             * objects (the hills in this case) */
            @Override
            public void onUpdate(float pSecondsElapsed) {

				/* Obtain the camera's current center X value */
                final float cameraCurrentX = camera.getCenterX();

				/* If the camera's position has changed since last
				 * update... */
                if (cameraPreviousX != cameraCurrentX) {

					/* Calculate the new parallax value offset by
					 * subtracting the previous update's camera x coordinate
					 * from the current update's camera x coordinate */
                    parallaxValueOffset +=  cameraCurrentX - cameraPreviousX;

					/* Apply the parallax value offset to the background, which
					 * will in-turn offset the positions of entities attached
					 * to the background */
                    this.setParallaxValue(parallaxValueOffset);

					/* Update the previous camera X since we're finished with this
					 * update */
                    cameraPreviousX = cameraCurrentX;
                }

                super.onUpdate(pSecondsElapsed);
            }
        };


        background.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(0, backgroundSprite));
        background.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(5, backBackgroundSprite));
        background.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(10, midBackgroundSprite));
        background.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(15, frontBackgroundSprite));

		/* Set & Enabled the background */
        scene.setBackground(background);
        scene.setBackgroundEnabled(true);

        scene.setOnSceneTouchListener(this);
        gameSound.play();
        footstepSound.play();
        pOnCreateSceneCallback.onCreateSceneFinished(scene);
    }

    @Override
    public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws IOException {

//        final float positionX = CAMERA_WIDTH * 0.5f;
//        final float positionY = CAMERA_HEIGHT * 0.5f;
//
//        backgroundSprite = new Sprite(positionX, positionY, backgroundTextureRegion, mEngine.getVertexBufferObjectManager());

        cowboyAnimatedSprite = new AnimatedSprite(120, 100, cowboyTiledTextureRegion, mEngine.getVertexBufferObjectManager()){

            @Override
            protected void onManagedUpdate(float pSecondsElapsed) {
                super.onManagedUpdate(pSecondsElapsed);
//                footstepSound.play();
//                Log.d(TAG, "onManagedUpdate: counting#");
            }
        };
        cowboyAnimatedSprite.animate(50);

        catAnimatedSprite = new AnimatedSprite(600, 90, catTiledTextureRegion, mEngine.getVertexBufferObjectManager()){
            @Override
            protected void onManagedUpdate(float pSecondsElapsed) {

                /* Obtain the current x/y values */
                final float currentX = this.getX();
                final float currentY = this.getY();

				/* If the two rectangle's are colliding, set this rectangle's color to GREEN */
//                Log.d(TAG, "onManagedUpdate: ###"+this.getX()+"and Y "+ this.getY());
                if(this.collidesWith(cowboyAnimatedSprite) && this.getX() >= 180){
                    Log.d(TAG, "onManagedUpdate: COLLISTION");
                    if (!hasPlayed){
                        biteSound.play();
//                        screamSound.play();
                        hasPlayed = true;
                    }
                }
                else {
                    hasPlayed = false;
                }

                super.onManagedUpdate(pSecondsElapsed);

            }
        };

        final MoveXModifier moveXModifier = new MoveXModifier(1.7f, 900, -170);
        catAnimatedSprite.registerEntityModifier(new LoopEntityModifier(moveXModifier));

        catAnimatedSprite.animate(100);

//        scene.attachChild(backgroundSprite);
        scene.attachChild(cowboyAnimatedSprite);
        frontBackgroundSprite.attachChild(catAnimatedSprite);
//        scene.setBackground(new Background(Color.CYAN));

        final float fromX = cowboyAnimatedSprite.getX();
        final float toX = cowboyAnimatedSprite.getX();
        final float fromY = cowboyAnimatedSprite.getY();
        final float toY = cowboyAnimatedSprite.getY();

        scene.registerUpdateHandler(new IUpdateHandler() {
            @Override
            public void reset() {}
            @Override
            public void onUpdate(float pSecondsElapsed) {
                //moves character 2 steps forward along x-axis
//                if (canGo){
//                    cowboyAnimatedSprite.setPosition(cowboyAnimatedSprite.getX() + 2, cowboyAnimatedSprite.getY());
//                }
                //moves character 8 steps upward along y-axis and back
//                if (canGo){
//                    cowboyAnimatedSprite.setPosition(cowboyAnimatedSprite.getX(), cowboyAnimatedSprite.getY()+30);
////                    cowboyAnimatedSprite.setPosition(cowboyAnimatedSprite.getX(), cowboyAnimatedSprite.getY()-8);
//                }

                final float innitialYPosition = 100;
//                if (pSecondsElapsed < 0.2){
//                    innitialYPosition = cowboyAnimatedSprite.getY();
//                }

                if (canGo){
                    final float duration = 1;
//                    final float duration = 3;
//                    final float fromX = cowboyAnimatedSprite.getX();
//                    final float toX = cowboyAnimatedSprite.getX();
//                    final float fromY = cowboyAnimatedSprite.getY();
//                    final float toY = cowboyAnimatedSprite.getY();
//                    final float toY = cowboyAnimatedSprite.getY() + 140;


                    //you can move in on axis using MoveYModifier or MoveXModifier
//                    MoveYModifier mod1=new MoveYModifier(constanttime,fromY,toY);
//                    sprite.registerEntityModifier(mod1);

//                    final MoveModifier downMoveModifier = new MoveModifier(0.8f, fromX, fromY, toX, innitialYPosition, new IEntityModifier.IEntityModifierListener() {
//                        @Override
//                        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
////                            cowboyAnimatedSprite.setCurrentTileIndex(4);
//                        }
//
//                        @Override
//                        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
////                            cowboyAnimatedSprite.stopAnimation(2);
////                            cowboyAnimatedSprite.setCurrentTileIndex(2);
//                            cowboyAnimatedSprite.animate(50);
//                        }
//                    });
//
//                    final MoveModifier upMoveModifier = new MoveModifier(0.4f, fromX, fromY, toX, toY, new IEntityModifier.IEntityModifierListener() {
//                        @Override
//                        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
//                            cowboyAnimatedSprite.stopAnimation(0);
//                        }
//
//                        @Override
//                        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
//                            cowboyAnimatedSprite.registerEntityModifier(downMoveModifier);
//                        }
//                    });

                    //-140 means jump upward, positive move downward
                    JumpModifier jumpModifier = new JumpModifier(0.6f, fromX, toX, fromY, toY, -140, new IEntityModifier.IEntityModifierListener() {
                        @Override
                        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
                            cowboyAnimatedSprite.stopAnimation(0);
                            Log.d(TAG, "onModifierStarted: JUMP");
                            if (hasPlayedJumpSound){
                                footstepSound.pause();
                                jumpSound.stop();
                                jumpSound.play();
                                hasPlayedJumpSound = false;
                            }
                            else {
                                hasPlayedJumpSound = true;
                            }
                        }

                        @Override
                        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                            cowboyAnimatedSprite.animate(50);
                            cowboyAnimatedSprite.setY(innitialYPosition);
                            footstepSound.play();
                        }
                    });

                    cowboyAnimatedSprite.registerEntityModifier(jumpModifier);
//                    cowboyAnimatedSprite.registerEntityModifier(upMoveModifier);
                }
            }
        });

        pOnPopulateSceneCallback.onPopulateSceneFinished();
    }

    @Override
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {

        if (pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN)
            canGo = true;
        if (pSceneTouchEvent.getAction() == MotionEvent.ACTION_UP)
            canGo = false;
        return false;
    }
}
