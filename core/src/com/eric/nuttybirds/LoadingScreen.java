package com.eric.nuttybirds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


/**
 * Created by erica_000 on 1/4/2018.
 */

public class LoadingScreen extends ScreenAdapter {
    private static final float WORLD_WIDTH = 300;
    private static final float WORLD_HEIGHT = 400;

    private Stage stage;
    private Skin skin;
    private ProgressBar progressBar;
    private float progress = 0;
    private float comDelta = 0;

    private final NuttyGame game;
    private OrthographicCamera camera;

    public LoadingScreen(NuttyGame game) {
        this.game = game;
    }

    @Override
    public void  resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        camera.update();

        stage = new Stage(new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera));
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        progressBar = new ProgressBar(0, 1, 0.05f, false, skin);
        progressBar.setPosition(150, 200, Align.center);
        progressBar.setSize(progressBar.getPrefWidth(), progressBar.getPrefHeight());
        progressBar.setAnimateDuration(0.1f);
        stage.addActor(progressBar);

        game.getAssetManager().load(Constants.TILED_MAP_NAME, TiledMap.class);
        game.getAssetManager().load("pete.tmx", TiledMap.class);
        game.getAssetManager().load("mine_strip25.png", Texture.class);
        game.getAssetManager().load("pete.png", Texture.class);
        game.getAssetManager().load("badlogic.jpg", Texture.class);
        game.getAssetManager().load("acorn.png", Texture.class);
        game.getAssetManager().load("space_titles128x128_png.png", Texture.class);
        game.getAssetManager().load("floor.png", Texture.class);
        game.getAssetManager().load("acorn.wav", Sound.class);
        game.getAssetManager().load("jump.wav", Sound.class);
        game.getAssetManager().load("peteTheme.mp3", Music.class);

    }

    @Override
    public void render(float delta) {
        update(delta);
        clearScreen();
        draw(delta);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    private void update(float delta) {
        comDelta += delta;

        Gdx.app.debug("GAME", "Update: " + delta + " " + game.getAssetManager().getProgress());
        System.out.println("Entered update: " + game.getAssetManager().getProgress() + " " + comDelta);
        if (game.getAssetManager().update() && comDelta > 1.25) {
            game.setScreen(new UIScreen(game));
        }
        else {
            progress = game.getAssetManager().getProgress();
            progressBar.setValue(progress);
            System.out.println("PROGRESS: " + Float.toString(progress));
        }
    }


    private void clearScreen() {
        Gdx.gl.glClearColor(Color.WHITE.r, Color.WHITE.g, Color.WHITE.b, Color.WHITE.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void draw(float delta) {
        stage.act(delta);
        stage.draw();
    }
}
