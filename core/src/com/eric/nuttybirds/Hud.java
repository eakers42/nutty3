package com.eric.nuttybirds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.List;

/**
 * Created by erica_000 on 2/12/2018.
 */

public class Hud extends Actor {
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final List<SpinningMine> mineList;
    private final float mapWidth;
    private final float mapHeight;
    private final OrthographicCamera camera;

    public Hud(List<SpinningMine> mineList, OrthographicCamera camera, float mapWidth, float mapHeight) {
        this.mineList = mineList;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.camera = camera;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

//        batch.end();

        // remember SpriteBatch's current functions
        int srcFunc = batch.getBlendSrcFunc();
        int dstFunc = batch.getBlendDstFunc();

//        batch.begin();
        batch.end();
        
        Color color = new Color(getColor());
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        Gdx.gl.glLineWidth(2f);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLUE.r, Color.BLUE.g, Color.BLUE.b, parentAlpha);
        for(SpinningMine mine : this.mineList) {
            float xPos = this.getX() + mine.getX() * this.getWidth() / mapWidth;
            float yPos = this.getY() + mine.getY() * this.getHeight() / mapHeight;
            shapeRenderer.rect(xPos, yPos, 2, 2);
//            Gdx.app.debug("MINE", mine.getX() + " " + mine.getY() + " -> " + xPos + " "  + yPos);
        }
        shapeRenderer.end();

        float centerX = this.getX() + this.camera.position.x * this.getWidth() / this.mapWidth;
        float centerY = this.getY() + this.camera.position.y * this.getHeight() / this.mapHeight;
        float viewportWidth = this.mapWidth * this.camera.zoom * this.getWidth() / this.mapWidth;
        float viewportHeight = this.mapHeight * this.camera.zoom * this.getHeight() / this.mapHeight;

        float viewportLeftX = centerX - viewportWidth / 2;
        float viewportLeftY = centerY + viewportHeight / 2;
        float viewportRightX = viewportLeftX + viewportWidth;
        float viewportRightY = viewportLeftY - viewportHeight;
        Vector2 center = new Vector2(centerX, centerY);
        Vector2 left = new Vector2(center.x - 5, center.y - 5);
        Vector2 right = new Vector2(center.x + 5, center.y - 5);
        Gdx.app.debug("HUD", "CENTER: " + center.toString());
//        Gdx.app.debug("HUD", "LEFT: " + left.toString());
//        Gdx.app.debug("HUD", "RIGHT: " + right.toString());
        Gdx.app.debug("HUD", "CAMERA: " + this.camera.position.toString());
        Gdx.app.debug("HUD", "VIEWPORT WIDTH: " + viewportWidth);
        Gdx.app.debug("HUD", "VIEWPORT HEIGHT: " + viewportHeight);
        Gdx.app.debug("HUD", "VIEWPORT LEFT: " + viewportLeftX + " " + viewportLeftY);
        Gdx.app.debug("HUD", "VIEWPORT RIGHT: " + viewportRightX + " " + viewportRightY);

        shapeRenderer.setColor(Color.GREEN.r, Color.GREEN.g, Color.GREEN.b, parentAlpha);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.line(center, left);
        shapeRenderer.line(left, right);
        shapeRenderer.line(right, center);

        shapeRenderer.line(viewportLeftX, viewportLeftY, viewportRightX, viewportLeftY);
        shapeRenderer.line(viewportLeftX, viewportLeftY, viewportLeftX, viewportRightY);
        shapeRenderer.line(viewportLeftX, viewportRightY, viewportRightX, viewportRightY);
        shapeRenderer.line(viewportRightX, viewportLeftY, viewportRightX, viewportRightY);
        shapeRenderer.end();

//        Gdx.gl.glDisable(GL20.GL_BLEND);

//        batch.setBlendFunction(srcFunc, dstFunc);

//        batch.setColor(Color.WHITE);
        batch.begin();
    }

    @Override
    public void drawDebug(ShapeRenderer shapes) {
        super.drawDebug(shapes);
    }
}
