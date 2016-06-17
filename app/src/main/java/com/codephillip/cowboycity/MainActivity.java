package com.codephillip.cowboycity;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.color.Color;

import java.io.IOException;

public class MainActivity extends BaseGameActivity {

    private static final int CAMERA_WIDTH = 800;
    private static final int CAMERA_HEIGHT = 480;

    private Scene scene;

    @Override
    public EngineOptions onCreateEngineOptions() {
        Camera camera = new Camera(0,0, CAMERA_WIDTH, CAMERA_HEIGHT);
        return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), camera);
    }

    @Override
    public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException {
        pOnCreateResourcesCallback.onCreateResourcesFinished();
    }

    @Override
    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws IOException {
        scene = new Scene();
        scene.setBackground(new Background(Color.RED));
        pOnCreateSceneCallback.onCreateSceneFinished(scene);
    }

    @Override
    public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws IOException {
        pOnPopulateSceneCallback.onPopulateSceneFinished();
    }
}
