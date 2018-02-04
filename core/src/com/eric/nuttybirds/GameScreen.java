package com.eric.nuttybirds;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Created by erica_000 on 1/4/2018.
 */

public class GameScreen extends ScreenAdapter implements GestureDetector.GestureListener {
    private static final float WORLD_WIDTH = 1200;
    private static final float WORLD_HEIGHT = 1600;
    private static final float MAP_WIDTH = 2560;
    private static final float MAP_HEIGHT = 5120;

    private float viewportWidth = WORLD_WIDTH;
    private float viewportHeight = WORLD_HEIGHT;

    private ShapeRenderer shapeRenderer;
    private Viewport viewport;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private final NuttyGame game;



    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private Vector3 screenCoordinates = new Vector3();
    private Vector2 flingVelocity = new Vector2();
    private int flingVelocityCounter = 0;
    private boolean mouseDown = false;

    private Texture mineTexture;

    // Sprites
    private List<SpinningMine> mineList = new ArrayList<SpinningMine>();


    public GameScreen(NuttyGame game) {
        this.game = game;
    }

    @Override
    public void  resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void show() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        camera = new OrthographicCamera();
        //camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        //camera.update();
//        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply(true);
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        tiledMap = game.getAssetManager().get(Constants.TILED_MAP_NAME);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, batch);
        tiledMapRenderer.setView(this.camera);

        mineTexture = game.getAssetManager().get("mine_strip25.png");

        Gdx.input.setInputProcessor(new GestureDetector(this));
    }

    @Override
    public void render(float delta) {
        update(delta);
        clearScreen();
        draw();
        drawDebug();
    }

    private void drawDebug() {
        shapeRenderer.setProjectionMatrix(camera.projection);
        shapeRenderer.setTransformMatrix(camera.view);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (SpinningMine mine : this.mineList) {
            mine.drawDebug(shapeRenderer);
        }

        shapeRenderer.end();
    }

    private void draw() {
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            camera.position.x -=4;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.position.x += 4;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            camera.position.y +=4;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.position.y -= 4;
        }


        TiledMapTileLayer tileLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        float mapWidth = tileLayer.getWidth() * tileLayer.getTileWidth();
        float mapHeight = tileLayer.getHeight() * tileLayer.getTileHeight();
        float minX = WORLD_WIDTH / 2;
        float maxX = mapWidth - minX;
        float minY = WORLD_HEIGHT / 2;
        float maxY = mapHeight - minY;

        if(flingVelocityCounter > 0) {
            camera.translate(flingVelocity);
        }


        camera.position.x = MathUtils.clamp(camera.position.x, minX, maxX);
        camera.position.y = MathUtils.clamp(camera.position.y, minY, maxY);
        camera.update();
        tiledMapRenderer.setView(camera);

        // Draw the mines after the map so everything shows above the map
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);
        tiledMapRenderer.render();

        batch.begin();
        for (SpinningMine mine : this.mineList) {
            mine.draw(batch);
        }
        batch.end();

        if (mouseDown) {
            mouseDown = false;
            System.out.format("Draw::Cam Coords: %f,%f", camera.position.x, camera.position.y);
        }
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(Color.WHITE.r, Color.WHITE.g, Color.WHITE.b, Color.WHITE.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void update(float delta) {
        if (MathUtils.random() < 0.05 && mineList.size() < 50) {
            // Add a new mine
            float x = MathUtils.random(100f, MAP_WIDTH - 100);
            float y = MAP_HEIGHT;
            SpinningMine mine = new SpinningMine(this, mineTexture, x, y);
            this.mineList.add(mine);
        }

        Iterator<SpinningMine> iter = mineList.iterator();
        while (iter.hasNext()) {
            SpinningMine mine = iter.next();
            mine.update(delta);
            if (mine.getY() < 0) {
                iter.remove();
            }
        }

        // Update fling
        if(flingVelocityCounter > 0) {
            if(flingVelocityCounter < 5) {
                flingVelocity.scl(0.5f);
            }
            flingVelocityCounter--;
        }
    }


//    @Override
//    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
//        mouseDown = true;
//        String msg = String.format("Mouse Down: %d,%d", screenX, screenY);
//        Gdx.app.log("MyTag", msg);
//        System.out.println("Mouse down");
//
//        Vector3 newCamCoord = camera.unproject(screenCoordinates.set(screenX, screenY, 0));
//        camera.position.set(newCamCoord);
//        System.out.format("New Coords: %f,%f\n", camera.position.x, camera.position.y);
//        return true;
//    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public void setTiledMap(TiledMap tiledMap) {
        this.tiledMap = tiledMap;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
//        this.flingVelocity.set(velocityX, velocityY);
//        Gdx.app.debug("FLING", "FLING: " + velocityX + " " + velocityY);
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        Gdx.app.debug("PAN", "PAN: " + deltaX + " " + deltaY);
        this.flingVelocity.set(-deltaX, deltaY);
        this.flingVelocityCounter = 30;
        return true;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        System.out.format("Zoom: %f  Initial: %f  Distance: %f", camera.zoom, initialDistance, distance);
        float update = MathUtils.clamp((initialDistance - distance), -1, 1);
        update *= 0.005;
        camera.zoom += update;
        camera.zoom = MathUtils.clamp((float)camera.zoom + update, 0.5f, 2.5f);
        System.out.format("Update: %f  New Zoom: %f", update, camera.zoom);
        return true;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {
        Gdx.app.debug("PINCH STOP", "PINCH STOP");
    }
}
