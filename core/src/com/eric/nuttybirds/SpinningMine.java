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
        // Image outline
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(this.collisionRectangle.x, this.collisionRectangle.y,
                this.collisionRectangle.width, this.collisionRectangle.height);
        // Collision circle
        shapeRenderer.setColor(Color.BLUE);
        float circleX = this.collisionRectangle.x + this.collisionRectangle.width / 2;
        float circleY = this.collisionRectangle.y + this.collisionRectangle.height / 2;
        shapeRenderer.circle(circleX, circleY, 36);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpinningMine that = (SpinningMine) o;

        if (Float.compare(that.x, x) != 0) return false;
        if (Float.compare(that.y, y) != 0) return false;
        if (Float.compare(that.xSpeed, xSpeed) != 0) return false;
        if (Float.compare(that.ySpeed, ySpeed) != 0) return false;
        if (Float.compare(that.animationTimer, animationTimer) != 0) return false;
        if (gameScreen != null ? !gameScreen.equals(that.gameScreen) : that.gameScreen != null)
            return false;
        if (collisionRectangle != null ? !collisionRectangle.equals(that.collisionRectangle) : that.collisionRectangle != null)
            return false;
        return flying != null ? flying.equals(that.flying) : that.flying == null;
    }

    @Override
    public int hashCode() {
        int result = gameScreen != null ? gameScreen.hashCode() : 0;
        result = 31 * result + (collisionRectangle != null ? collisionRectangle.hashCode() : 0);
        result = 31 * result + (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        result = 31 * result + (xSpeed != +0.0f ? Float.floatToIntBits(xSpeed) : 0);
        result = 31 * result + (ySpeed != +0.0f ? Float.floatToIntBits(ySpeed) : 0);
        result = 31 * result + (animationTimer != +0.0f ? Float.floatToIntBits(animationTimer) : 0);
        result = 31 * result + (flying != null ? flying.hashCode() : 0);
        return result;
    }

    public void setY(float y) {
        this.y = y;
    }

}
