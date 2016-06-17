package com.codephillip.cowboycity;

import android.view.MotionEvent;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.color.Color;
import org.andengine.util.modifier.IModifier;

import java.io.IOException;

public class MainActivity extends BaseGameActivity implements IOnSceneTouchListener {

    private static final int CAMERA_WIDTH = 800;
    private static final int CAMERA_HEIGHT = 480;

    private BitmapTextureAtlas cowboyTextureAtlas;
    private ITiledTextureRegion cowboyTiledTextureRegion;
    private AnimatedSprite cowboyAnimatedSprite;

    boolean canGo = false;

    public static final String TAG = "Cowboy#";


    private Scene scene;

    @Override
    public EngineOptions onCreateEngineOptions() {
        Camera camera = new Camera(0,0, CAMERA_WIDTH, CAMERA_HEIGHT);
        return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), camera);
    }

    @Override
    public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        cowboyTextureAtlas = new BitmapTextureAtlas(mEngine.getTextureManager(), 640, 320, TextureOptions.BILINEAR);
        cowboyTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(cowboyTextureAtlas, this, "walkman640x320.png", 0, 0, 8, 1);
        cowboyTextureAtlas.load();

        pOnCreateResourcesCallback.onCreateResourcesFinished();
    }

    @Override
    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws IOException {
        scene = new Scene();
        scene.setOnSceneTouchListener(this);

        cowboyAnimatedSprite = new AnimatedSprite(120, 120, cowboyTiledTextureRegion, mEngine.getVertexBufferObjectManager()){


            @Override
            protected void onManagedUpdate(float pSecondsElapsed) {
                super.onManagedUpdate(pSecondsElapsed);
//                Log.d(TAG, "onManagedUpdate: counting#");
            }
        };
        cowboyAnimatedSprite.animate(50);

        scene.attachChild(cowboyAnimatedSprite);
        scene.setBackground(new Background(Color.CYAN));
        scene.registerUpdateHandler(new IUpdateHandler() {
            @Override
            public void reset() {}
            @Override
            public void onUpdate(float pSecondsElapsed) {
                // TODO Auto-generated method stub
                //moves character 2 steps forward along x-axis
//                if (canGo){
//                    cowboyAnimatedSprite.setPosition(cowboyAnimatedSprite.getX() + 2, cowboyAnimatedSprite.getY());
//                }
                //moves character 8 steps upward along y-axis and back
//                if (canGo){
//                    cowboyAnimatedSprite.setPosition(cowboyAnimatedSprite.getX(), cowboyAnimatedSprite.getY()+30);
////                    cowboyAnimatedSprite.setPosition(cowboyAnimatedSprite.getX(), cowboyAnimatedSprite.getY()-8);
//                }

                if (canGo){
                    final float duration = 1;
//                    final float duration = 3;
                    final float fromX = cowboyAnimatedSprite.getX();
                    final float toX = cowboyAnimatedSprite.getX();
                    final float fromY = cowboyAnimatedSprite.getY();
                    final float toY = cowboyAnimatedSprite.getY() + 80;


                    //you can move in on axis using MoveYModifier or MoveXModifier
//                    MoveYModifier mod1=new MoveYModifier(constanttime,fromY,toY);
//                    sprite.registerEntityModifier(mod1);

                    final MoveModifier downMoveModifier = new MoveModifier(0.4f, fromX, toY, toX, fromY, new IEntityModifier.IEntityModifierListener() {
                        @Override
                        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
                            cowboyAnimatedSprite.stopAnimation(0);
                        }

                        @Override
                        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                            cowboyAnimatedSprite.animate(50);
                        }
                    });

                    final MoveModifier upMoveModifier = new MoveModifier(0.2f, fromX, fromY, toX, toY, new IEntityModifier.IEntityModifierListener() {
                        @Override
                        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
                            cowboyAnimatedSprite.stopAnimation(0);
                        }

                        @Override
                        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                            cowboyAnimatedSprite.registerEntityModifier(downMoveModifier);
                        }
                    });

                    cowboyAnimatedSprite.registerEntityModifier(upMoveModifier);
                }
            }
        });
        pOnCreateSceneCallback.onCreateSceneFinished(scene);
    }

    @Override
    public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws IOException {
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
