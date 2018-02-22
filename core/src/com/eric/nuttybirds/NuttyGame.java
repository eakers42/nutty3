package com.eric.nuttybirds;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.eric.nuttybirds.config.EnemyData;
import com.eric.nuttybirds.config.EnemyManager;
import com.eric.nuttybirds.config.EnemyManagerSerializer;
import com.eric.nuttybirds.config.LevelData;
import com.eric.nuttybirds.config.LevelManager;

import java.util.HashSet;
import java.util.Set;

public class NuttyGame extends Game {
    private final AssetManager assetManager = new AssetManager();


	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));

        EnemyManager enemyManager;
        LevelManager levels;
        Json json = new Json();
        Set<String> externalAssets = new HashSet<String>();
        
        // Load some initial assets for testing
        assetManager.load(Constants.TILED_MAP_NAME, TiledMap.class);
        assetManager.load("pete.tmx", TiledMap.class);
        assetManager.load("mine_strip25.png", Texture.class);
        assetManager.load("pete.png", Texture.class);
        assetManager.load("acorn.png", Texture.class);
        assetManager.load("floor.png", Texture.class);
        assetManager.load("space_titles128x128_png.png", Texture.class);

        assetManager.finishLoading();
        
        // Set up the external assets
        externalAssets.add(Constants.TILED_MAP_NAME);
        externalAssets.add("mine_strip25.png");
        externalAssets.add("space_titles128x128_png.png");

        json.setSerializer(EnemyManager.class, new EnemyManagerSerializer());
        
        enemyManager = json.fromJson(EnemyManager.class, Gdx.files.internal("EnemyData.json").readString());
        EnemyData mine = enemyManager.get("Mine1");
        Gdx.app.debug("JSON", "Stuff");

        levels = json.fromJson(LevelManager.class, Gdx.files.internal("LevelData001.json").readString());
        levels.setEnemyManager(enemyManager);
        levels.setExternalAssets(externalAssets);
        LevelData ld = levels.getLevel(0);
        Set<String> assets = ld.getAssets();
        Gdx.app.debug("JSON", "Stuff");
		
		LoadingScreen loadScreen = new LoadingScreen(this, levels.getLevel(0));
		setScreen(loadScreen);
	}

	public AssetManager getAssetManager() {
	    return this.assetManager;
    }
}
