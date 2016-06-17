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

    boolean canGo = false;
    private boolean hasPlayed = false;
    private boolean hasPlayedJumpSound = false;

    public static final String TAG = "Cowboy#";


    private Scene scene;
    private Sound biteSound;
    private Sound screamSound;
    private Sound jumpSound;
    private Music gameSound;
    private Music footstepSound;

    @Override
    public EngineOptions onCreateEngineOptions() {
        Camera camera = new Camera(0,0, CAMERA_WIDTH, CAMERA_HEIGHT);
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
        scene.setOnSceneTouchListener(this);
        gameSound.play();
        footstepSound.play();
        pOnCreateSceneCallback.onCreateSceneFinished(scene);
    }

    @Override
    public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws IOException {

        final float positionX = CAMERA_WIDTH * 0.5f;
        final float positionY = CAMERA_HEIGHT * 0.5f;

        backgroundSprite = new Sprite(positionX, positionY, backgroundTextureRegion, mEngine.getVertexBufferObjectManager());

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
                Log.d(TAG, "onManagedUpdate: ###"+this.getX()+"and Y "+ this.getY());
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

        scene.attachChild(backgroundSprite);
        scene.attachChild(cowboyAnimatedSprite);
        scene.attachChild(catAnimatedSprite);
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
