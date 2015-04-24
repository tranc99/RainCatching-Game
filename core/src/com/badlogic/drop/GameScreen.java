package com.badlogic.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

/**
 * Created by ten on 4/24/15.
 */
public class GameScreen implements Screen {

    final Drop game;
    Texture dropImage;
    Texture bucketImage;
    Sound dropSound;
    Music rainMusic;
    Sound dropPing;
    Sound applause;
    OrthographicCamera camera;
    Rectangle bucket;
    Array<Rectangle> rainDrops;
    long lastDropTime;
    int dropsGathered;
    int livesRemaining = 10;

    public GameScreen(Drop game) {
        this.game = game;

        // load the images for the droplet and the bucket, 64x64 pixels each
        dropImage = new Texture(Gdx.files.internal("droplet.png"));
        bucketImage = new Texture(Gdx.files.internal("bucket.png"));

        // load the drop sound effect and the rain music, then begin playing the rain music
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        dropPing = Gdx.audio.newSound(Gdx.files.internal("glass_ping.mp3"));
        applause = Gdx.audio.newSound(Gdx.files.internal("light_applause.mp3"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        rainMusic.setLooping(true);

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        // create a Rectangle to logically represent the bucket
        bucket = new Rectangle();
        bucket.x = 800 / 2 - 64 /2; // center the bucket horizontally
        bucket.y = 20; // near the bottom of the screen

        bucket.width = 64;
        bucket.height = 64;

        // create the raindrops array and spawn the first raindrop
        rainDrops = new Array<Rectangle>();
        spawnRaindrop();
    }

    private void spawnRaindrop() {
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0, 800 - 64);
        raindrop.y = 480;
        raindrop.width = 64;
        raindrop.height = 64;
        rainDrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void show() {
        rainMusic.play();
    }

    @Override
    public void render(float delta) {
        // clear the screen with a light green color
        Gdx.gl.glClearColor(0.6f, 0.9f, 0.6f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Tell the camera to update its matrices
        camera.update();

        // tell the SpriteBatch to render in the coordinate system of the camera
        game.batch.setProjectionMatrix(camera.combined);

        // begin a new batch and draw the bucket and all drops
        game.batch.begin();
        game.font.draw(game.batch, "Score: " + dropsGathered * 5000, 0, 480);
        game.font.draw(game.batch, "Drops Collected: " + dropsGathered, 0, 420);
        game.font.draw(game.batch, "Lives Remaining: " + livesRemaining, 0, 460);
        game.batch.draw(bucketImage, bucket.x, bucket.y);
        for (Rectangle raindrop : rainDrops) {
            game.batch.draw(dropImage, raindrop.x, raindrop.y);
        }
        game.batch.end();

        // process user input
        if(Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucket.x = touchPos.x - 64 / 2;
        }

        // check if we need to spawn a new raindrop
        if(TimeUtils.nanoTime() - lastDropTime > 1000000000) {
            spawnRaindrop();
        }

        // if the user is out of lives, end the game
        if(livesRemaining <= 0) {
            game.setScreen(new EndScreen(game));
            dispose();
        }

        // move the raindrops and update their positions
        Iterator<Rectangle> iter = rainDrops.iterator();
        while(iter.hasNext()) {
            Rectangle raindrop = iter.next();
            raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
            if (raindrop.y + 64 < 0) {
                dropPing.play();
                iter.remove();
                livesRemaining--;
            }
            if (raindrop.overlaps(bucket)) {
                dropsGathered++;
                dropSound.play();
                iter.remove();
                if (dropsGathered % 25 == 0) {
                    applause.play();
                    spawnRaindrop();
                    spawnRaindrop();
                    spawnRaindrop();
                }
            }

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
        dropImage.dispose();
        bucketImage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
        dropPing.dispose();
    }
}
