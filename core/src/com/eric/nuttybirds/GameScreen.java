package com.eric.nuttybirds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


/**
 * Created by erica_000 on 1/4/2018.
 */

public class GameScreen extends ScreenAdapter implements InputProcessor {
    private static final float WORLD_WIDTH = 1200;
    private static final float WORLD_HEIGHT = 1600;
    private ShapeRenderer shapeRenderer;
    private Viewport viewport;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private final NuttyGame game;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private Vector3 screenCoordinates = new Vector3();
    private boolean mouseDown = false;

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

        Gdx.input.setInputProcessor(this);
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
        camera.update();
        tiledMapRenderer.setView(camera);
//        batch.setProjectionMatrix(camera.projection);
//        batch.setTransformMatrix(camera.view);
        tiledMapRenderer.render();

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

    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        mouseDown = true;
        String msg = String.format("Mouse Down: %d,%d", screenX, screenY);
        Gdx.app.log("MyTag", msg);
        System.out.println("Mouse down");

        Vector3 newCamCoord = camera.unproject(screenCoordinates.set(screenX, screenY, 0));
        camera.position.set(newCamCoord);
        System.out.format("New Coords: %f,%f\n", camera.position.x, camera.position.y);
//        camera.position.add(5, 0, 0);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
//        camera.unproject(screenCoordinates.set(screenX, screenY, 0));
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
//        Gdx.app.log("MyTag", "Mouse Moved");
//        camera.unproject(screenCoordinates.set(screenX, screenY, 0));
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
