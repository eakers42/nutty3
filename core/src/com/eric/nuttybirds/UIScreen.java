package com.eric.nuttybirds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Created by erica_000 on 1/29/2018.
 */

public class UIScreen extends ScreenAdapter {

    private Stage stage;
    private Skin skin;
    private float counter = 0;
    private ProgressBar progressBar;
    private NuttyGame game;
    private Camera camera;

    public UIScreen(NuttyGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera(600, 800);
        camera.position.set(300, 400, 0);
        camera.update();
        stage = new Stage(new FitViewport(600, 800, camera));
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        final TextButton button = new TextButton("Click Me", skin, "round");
        button.setWidth(400);
        button.setHeight(40);
        button.setPosition(Gdx.graphics.getWidth() /2 - 200f, Gdx.graphics.getHeight()/2 - 20f);
        button.addListener(new ActorGestureListener(){
            @Override
            public void tap(InputEvent event, float x, float y, int count, int buttonNumber){
                button.setText("You clicked the button");
                game.setScreen(new GameScreen(game));
            }
        });

        final TextButton button2 = new TextButton("Another Click", skin, "round");
//        button2.setWidth(400);
//        button2.setHeight(40);
        button2.setPosition(Gdx.graphics.getWidth() / 2, 450, Align.center);

        progressBar = new ProgressBar(0, 100, 1, false, skin);
//        progressBar.setPosition(320, 350, Align.center);
//        progressBar.setSize(progressBar.getPrefWidth(), progressBar.getPrefHeight());
//        progressBar.setAnimateDuration(0.5f);

        Button btn = new Button(skin, "left");
        btn.setPosition(200, 100, Align.center);

        Button btn2 = new Button(skin, "right");
        btn2.setPosition(400, 100, Align.center);

        Button btn3 = new Button(skin, "sound");
        btn3.setPosition(200, 150, Align.center);

        Button btn4 = new Button(skin, "toggle");
        btn4.setPosition(400, 150, Align.center);

        Button btn7 = new Button(skin, "music");
        btn7.setPosition(200, 250, Align.center);

        Button btn8 = new Button(skin, "default");
        btn8.setPosition(400, 250, Align.center);

        TextButton btn5 = new TextButton("Toggle", skin, "toggle");
        btn5.setPosition(200, 200, Align.center);

        TextButton btn6 = new TextButton("Radio", skin, "radio");
        btn6.setPosition(400, 210, Align.center);

        TextField tf = new TextField("Text Field", skin);
        tf.setPosition(300, 300, Align.center);
        tf.setHeight(40);

        Window window = new Window("Drag Me", skin);
        window.setPosition(300, 600, Align.center);

        stage.addActor(window);
        stage.addActor(button);
        stage.addActor(button2);
//        stage.addActor(progressBar);
        stage.addActor(btn);
        stage.addActor(btn2);
        stage.addActor(btn3);
        stage.addActor(btn4);
        stage.addActor(btn5);
        stage.addActor(btn6);
        stage.addActor(btn7);
        stage.addActor(btn8);
        stage.addActor(tf);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render(float delta) {
        counter += delta * 5;
        if(counter >= 100) {
            counter = 0;
        }
        //System.out.println("PBAR: " + MathUtils.floor(counter));
        progressBar.setValue(MathUtils.floor(counter));

        Gdx.gl.glClearColor(Color.GRAY.r, Color.GRAY.g, Color.GRAY.b, Color.GRAY.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }
}
