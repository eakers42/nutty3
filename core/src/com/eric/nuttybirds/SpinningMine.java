package com.eric.nuttybirds;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by erica_000 on 1/19/2018.
 */

public class SpinningMine {
    public static final float MAX_X_SPEED = 5;
    public static final float MAX_Y_SPEED = 5;
    private static final float MAP_WIDTH = 2560;
    private static final float MAP_HEIGHT = 5120;
    public static final int WIDTH = 94;
    public static final int HEIGHT = 94;

    private final GameScreen gameScreen;

    private final Rectangle collisionRectangle = new Rectangle(0, 0, WIDTH, HEIGHT);

    private float x = 0;
    private float y = 0;
    private float xSpeed = 0;
    private float ySpeed = 0;

    private float animationTimer = 0;
    private final Animation<TextureRegion> flying;

    public SpinningMine(GameScreen gameScreen, Texture texture, float x, float y) {
        this.gameScreen = gameScreen;
        this.x = x;
        this.y = y;
        this.xSpeed = MathUtils.random(-5, 5);
        this.ySpeed = -(MathUtils.random(MAX_Y_SPEED - 1) + 1);

        TextureRegion[] regions;
        regions = new TextureRegion(texture).split(WIDTH, HEIGHT)[0];
        flying = new Animation(0.1f, regions);
        flying.setPlayMode(Animation.PlayMode.LOOP);
    }

    public void update(float delta) {
        this.x += this.xSpeed;
        this.y += this.ySpeed;

        TiledMapTileLayer tileLayer = (TiledMapTileLayer) gameScreen.getTiledMap().getLayers().get(0);
        float mapWidth = tileLayer.getWidth() * tileLayer.getTileWidth();
        if (this.x < 0 || this.x > (MAP_WIDTH - WIDTH / 2)) {
            // Reverse direction
            this.xSpeed = -this.xSpeed;
        }
        updateCollisionRectangle();

        animationTimer += delta;
    }

    private void updateCollisionRectangle() {
        this.collisionRectangle.setPosition(this.x, this.y);
    }

    public void draw(SpriteBatch batch) {
        TextureRegion tex = flying.getKeyFrame(animationTimer);
        batch.draw(tex, x, y);
    }

    public void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(this.collisionRectangle.x, this.collisionRectangle.y,
                this.collisionRectangle.width, this.collisionRectangle.height);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

}
