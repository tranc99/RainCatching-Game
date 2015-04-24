package com.badlogic.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * Created by ten on 4/24/15.
 */
public class EndScreen implements Screen {
    final Drop game;
    Music endMusic;

    OrthographicCamera camera;

    public EndScreen(final Drop drop) {
        game = drop;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        endMusic = Gdx.audio.newMusic(Gdx.files.internal("Canon.mp3"));
        endMusic.setLooping(true);

    }

    @Override
    public void show() {
        endMusic.play();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.9f, 0.4f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.font.draw(game.batch, "GAME OVER!!!", 200, 250);
        game.font.draw(game.batch, "Tap to return!", 200, 150);
        game.batch.end();

        if (Gdx.input.isTouched()) {
            game.setScreen(new MainMenuScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        endMusic.dispose();
    }
}
