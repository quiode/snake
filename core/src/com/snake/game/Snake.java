package com.snake.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

public class Snake extends ApplicationAdapter {
    private final Vector3 touchPos = new Vector3();
    // assets
    private Texture snakeHeadImage;
    private Texture snakeBodyImage;
    private Texture appleImage;
    private Sound eatSound;
    private Music music;
    // camera and spritebatch
    private OrthographicCamera camera;
    private SpriteBatch batch;
    // objects
    private Rectangle snakeHead;
    private Rectangle apple;
    // score
    private int score;

    @Override
    public void create() {
        // load the images for the snake and the apple, 64x64 pixels each
        snakeHeadImage = new Texture(Gdx.files.internal("snake_head.png"));
        snakeBodyImage = new Texture(Gdx.files.internal("snake_body.png"));
        appleImage = new Texture(Gdx.files.internal("apple.png"));

        // load the eat sound effect and the background music
        eatSound = Gdx.audio.newSound(Gdx.files.internal("apple_bite.ogg"));
        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));

        // start music
        music.setLooping(true);
        music.setVolume(0.1f);
        music.play();

        // camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 400);

        // sprite
        batch = new SpriteBatch();

        // snake head
        snakeHead = new Rectangle();
        snakeHead.width = 64;
        snakeHead.height = 64;
        snakeHead.x = camera.viewportWidth / 2 - snakeHead.width / 2;
        snakeHead.y = camera.viewportHeight / 2 - snakeHead.height / 2;

        // apple
        spawnApple();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 1, 0, 1);

        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(snakeHeadImage, snakeHead.x, snakeHead.y);
        batch.draw(appleImage, apple.x, apple.y);
        batch.end();

        // react to input
        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            snakeHead.x = touchPos.x - snakeHead.width / 2;
            snakeHead.y = touchPos.y - snakeHead.height / 2;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            snakeHead.x -= 200 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            snakeHead.x += 200 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            snakeHead.x -= 200 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            snakeHead.x += 200 * Gdx.graphics.getDeltaTime();

        // place snakehead between screen limits
        if (snakeHead.x < 0) snakeHead.x = 0;
        if (snakeHead.x > camera.viewportWidth - snakeHead.width) snakeHead.x = camera.viewportWidth - snakeHead.width;

        // collision detection
        if (apple.overlaps(snakeHead)) {
            eatSound.play();
            spawnApple();
            score++;
        }
    }

    private void spawnApple() {
        apple = new Rectangle();
        apple.width = 64;
        apple.height = 64;
        apple.x = MathUtils.random(0, camera.viewportWidth - apple.width);
        apple.y = MathUtils.random(0, camera.viewportHeight - apple.height);
        if (apple.overlaps(snakeHead)) spawnApple();
    }

    @Override
    public void dispose() {
        appleImage.dispose();
        snakeBodyImage.dispose();
        snakeHeadImage.dispose();
        eatSound.dispose();
        music.dispose();
        batch.dispose();
    }
}
