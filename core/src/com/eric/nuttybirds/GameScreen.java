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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.eric.nuttybirds.config.EnemyData;
import com.eric.nuttybirds.config.EnemyManager;
import com.eric.nuttybirds.config.EnemyManagerSerializer;
import com.eric.nuttybirds.config.LevelData;
import com.eric.nuttybirds.config.LevelManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.badlogic.gdx.utils.JsonWriter.OutputType.json;


/**
 * Created by erica_000 on 1/4/2018.
 */

public class GameScreen extends ScreenAdapter implements GestureDetector.GestureListener {
    private static final float WORLD_WIDTH = 1200;
    private static final float WORLD_HEIGHT = 1600;
    private static final float MAP_WIDTH = 2560;
    private static final float MAP_HEIGHT = 5120;
    private static final float MIN_ZOOM = 0.35f;

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
    private boolean zooming = false;
    private Vector3 centerPoint;
    private boolean centering = false;
    private float centerSpeed = 50.0f;
    private boolean centerZooming = false;
    private float centerZoom = 0;
    private float centerZoomSpeed = 0.05f;
    private int centerSteps = 50;
    private int centerZoomSteps = 50;

    private Stage hudStage;
    private Skin hudSkin;
    private Hud hud;
    private Vector2 cameraPosition = new Vector2();
    private OrthographicCamera hudCamera;

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
        camera = new OrthographicCamera();
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        tiledMap = game.getAssetManager().get(Constants.TILED_MAP_NAME);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, batch);
        tiledMapRenderer.setView(this.camera);
        TiledMapTileLayer tileLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        float mapWidth = tileLayer.getWidth() * tileLayer.getTileWidth();
        float mapHeight = tileLayer.getHeight() * tileLayer.getTileHeight();

        camera.position.set(mapWidth / 2, mapHeight / 2, 0);
        Gdx.app.debug("MAP", "W/H: " + mapWidth + "/" + mapHeight);

        hudSkin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        hudStage = new Stage(new ExtendViewport(300, 500));
//        Label pauseLabel = new Label("Paused", hudSkin, "title");
//        pauseLabel.setPosition(150, 100, Align.center);
        hud = new Hud(this.mineList, this.camera, mapWidth, mapHeight);
        hud.setColor(Color.FIREBRICK.r, Color.FIREBRICK.g, Color.FIREBRICK.b, 0.6f);
        hud.setWidth(80);
        hud.setHeight(120);
        hud.setPosition(260, 60, Align.center);
        hudStage.addActor(hud);
//        hudStage.addActor(pauseLabel);

        //camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        //camera.update();
//        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport = new ExtendViewport(mapWidth, mapHeight, camera);
        viewport.apply();




        mineTexture = game.getAssetManager().get("mine_strip25.png");

        GestureDetector gd = new GestureDetector(this);
        gd.setLongPressSeconds(0.7f);
        gd.setTapSquareSize(40);
        Gdx.input.setInputProcessor(gd);


    }

    @Override
    public void render(float delta) {
        update(delta);
        clearScreen();
        draw();
        drawDebug();
        hudStage.act(delta);
        hudStage.draw();
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
            camera.translate(flingVelocity);
        }

        if(centering) {
            Gdx.app.debug("CENTER", "Steps: " + centerSteps);
            if(this.centerSteps <= 0) {
                Gdx.app.debug("CENTER", "Moving to center");
                this.camera.position.set(this.centerPoint);
                centering = false;
            }
            else {
                Vector3 diff = this.centerPoint.cpy();
                diff.sub(this.camera.position);
                float length = diff.len() / this.centerSteps;
                diff.nor();
                diff.scl(length);
                this.camera.translate(diff);
//                Gdx.app.debug("CENTER", "Diff: " + diff.toString());
                this.centerSteps--;
//                Gdx.app.debug("CENTER", "Length: " + length + " Speed: " + centerSpeed);
//                if(length <= centerSpeed) {
//                    this.camera.position.set(this.centerPoint);
//                }
//                else {
//                    diff.nor();
//                    Vector3 dir = diff.scl(centerSpeed);
//                    Gdx.app.debug("CENTER", "Direction: " + dir.toString());
//                    this.camera.translate(dir);
//                }


            }
        }

        if(centerZooming) {
            Gdx.app.debug("CENTER ZOOM", "Steps: " + centerZoomSteps);
            if(this.centerZoomSteps <= 0) {
                this.camera.zoom = centerZoom;
                centerZooming = false;
            }
            else {
                float dir = centerZoom - this.camera.zoom;
                Gdx.app.debug("CENTER ZOOM", "Dir: " + dir);
                dir /= this.centerZoomSteps;
                Gdx.app.debug("CENTER ZOOM", "Adj: " + dir);
//                dir *= centerZoomSpeed;
                this.camera.zoom += dir;
                this.centerZoomSteps--;
            }
        }

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
        float minX = mapWidth * this.camera.zoom / 2;
        float maxX = mapWidth - minX;
        float minY = mapHeight * this.camera.zoom / 2;
        float maxY = mapHeight - minY;


        camera.position.x = MathUtils.clamp(camera.position.x, minX, maxX);
        camera.position.y = MathUtils.clamp(camera.position.y, minY, maxY);
        this.cameraPosition.set(this.camera.position.x, this.camera.position.y);
        camera.update();
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
        if(count == 2) {
            Vector3 longPoint = new Vector3(x, y, 0);
            this.centerPoint = this.camera.unproject(longPoint);
            this.centering = true;

            this.centerZoom = MIN_ZOOM;
            this.centerZooming = true;
            this.centerSteps = 30;
            this.centerZoomSteps = 30;
        }
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
//        Gdx.app.debug("PAN", "PAN: " + deltaX + " " + deltaY);
        this.centering = false;
        this.centerZooming = false;
        this.flingVelocity.set(-deltaX * this.camera.zoom, deltaY * this.camera.zoom);
        this.flingVelocityCounter = 30;
        return true;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
//        if(zooming) {
        System.out.format("Zoom: %f  Initial: %f  Distance: %f", camera.zoom, initialDistance, distance);
        float update = MathUtils.clamp((initialDistance - distance), -1, 1);
        update *= 0.005;
        camera.zoom += update;
        camera.zoom = MathUtils.clamp((float) camera.zoom + update, MIN_ZOOM, 1f);
        System.out.format("Update: %f  New Zoom: %f", update, camera.zoom);
        return true;
//        }
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
//        if(zooming == false) {
//            zooming = true;
//            Gdx.app.debug("PINCH", "Initial: " + initialPointer1.toString() + " " + initialPointer2);
//            Gdx.app.debug("PINCH", "Final: " + pointer1.toString() + " " + pointer2.toString());
//            // The distance calculations are squared distances
//            float initialDistance = initialPointer1.dst2(initialPointer2);
//            float finalDistance = pointer1.dst2(pointer2);
//            Gdx.app.debug("PINCH", "Distance: Initial: " + initialDistance + " Final: " + finalDistance);
//
//            Vector2 shortVec1 = pointer1;
//            Vector2 shortVec2 = pointer2;
//
//            // Center based on the points that are closer to each other. If it started close and moved away,
//            // use the initial distance. If it started large and ended closer, user the final distance.
//            Vector3 center = new Vector3((initialPointer1.x + pointer1.x) / 2, (initialPointer1.y + pointer1.y) / 2, 0);
//            centerPoint = this.camera.unproject(center);
//            centering = true;
////            if (initialDistance < finalDistance) {
////                shortVec1 = initialPointer1;
////                shortVec2 = initialPointer2;
////            }
//            Gdx.app.debug("PINCH", "Center: " + centerPoint.toString());
//            //this.camera.position.set(this.camera.unproject(center));
////            Gdx.app.debug("PINCH", "Unprojected: " + this.camera.position);
//        }

        // Average the two short vectors
//        center = shortVec1.add(shortVec2);
//        center = center.scl(0.5f);

        // Determine the ratio of initial to final distance to determine by how much to zoom
//        float zoomRatio = initialDistance / finalDistance;
//        zoomRatio *= 0.001;
//        Gdx.app.debug("PINCH","Zoom Update: " + zoomRatio);
//        this.camera.zoom += MathUtils.clamp(zoomRatio, -0.05f, 0.05f);
//        this.camera.zoom = MathUtils.clamp(this.camera.zoom, 0.5f, 1.5f);

        return true;
    }

    @Override
    public void pinchStop() {
        Gdx.app.debug("PINCH STOP", "PINCH STOP");
        zooming = false;
    }
}
