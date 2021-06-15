package com.chentong.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
//import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.util.Random;

/**
 * Create a project by LibGDX
 * Change "compile" to "implementation" in the file build.gradle
 * Delete all the import without using !!!!!
 *
 * Bg music : http://www.aigei.com/music/class/naruto/
 * Bg image : https://unsplash.com/
 */

public class FlyBird extends ApplicationAdapter {

	private SpriteBatch batch;
	private Texture gameLogo;
	private Texture background;
	private int gameState = 0;
	private Music bgMusic;
	//private ShapeRenderer shapeRendererBird; // Show Circle or Rectangle Shape

	// Core
	private int score = 0;
	private int scoringTube = 0; // define which tube is for scoring
	private Music getScoreMusic;
	private BitmapFont fontScore;
	//public static int bestScore = 0;
	private int bestScore = 0;
	private BitmapFont fontBestScore;
	private int gameLevel = 1; // Game level 1 2 3
	private BitmapFont fontGameLevel;

	// Texture Bird
	private Texture[] birds;
	private int flapState = 0; // display which bird on the screen
    private int countsShowBird = 0;
    private float birdY = 0;
	private float velocity = 0;
	private float gravity = 2; // Value constant
	private Circle birdCircle;

	// Texture Tube
	private Texture topTube;
	private Texture bottomTube;
	private Random randomGenerator;
	private float gap = 600; // Define how easy of this game
	private int tubeMargin = 200; // value constant
	private float tubeVelocity = 4;
	private float[] tubeX;
	private float[] tubeOffset;
	private int numberTubes = 4;
    private float distanceBetweenTubes;

    // Rectangle Tube
    private Rectangle[] topRectangles;
    private Rectangle[] bottomRectangles;

    // Stage : Button
	private Stage stage;
	private Texture gameOverTexture;
	private Button buttonGameOver;
	private Texture upButtonTexture;
	private Texture downButtonTexture;
	private Button buttonRestart;

	@Override
	public void create() {
		batch = new SpriteBatch(); // to manage and animate the Sprite
		gameLogo = new Texture("gamelogo.png");
		background = new Texture("bgGame.png");
		//shapeRendererBird = new ShapeRenderer();

		fontScore = new BitmapFont();
		fontScore.setColor(Color.WHITE);
		fontScore.getData().setScale(6);

		fontBestScore = new BitmapFont();
		fontBestScore.setColor(Color.YELLOW);
		fontBestScore.getData().setScale(6);

		fontGameLevel = new BitmapFont();
		fontGameLevel.setColor(Color.RED);
		fontGameLevel.getData().setScale(8);

		bgMusic = Gdx.audio.newMusic(Gdx.files.internal("bgMusic.mp3"));
		bgMusic.setLooping(true);
		bgMusic.play();
		bgMusic.setVolume(30);
		getScoreMusic = Gdx.audio.newMusic(Gdx.files.internal("getScore.mp3"));

		birds = new Texture[2]; // List array of images
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");
		birdCircle = new Circle(); // for checking collision

		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		randomGenerator = new Random();
		tubeX = new float[numberTubes];
		tubeOffset = new float[numberTubes];
		distanceBetweenTubes = Gdx.graphics.getWidth() * 4 /5;

		topRectangles = new Rectangle[numberTubes];
		bottomRectangles = new Rectangle[numberTubes];

		initBirdTubes();

		stage = new Stage();
		// 使用伸展视口（StretchViewport）创建舞台
		//stage = new Stage(new StretchViewport(700, 350));
		// 将输入处理设置到舞台（必须设置, 否则点击按钮没效果）
		Gdx.input.setInputProcessor(stage);

		gameOverTexture = new Texture(Gdx.files.internal("gameover2.png"));
		Button.ButtonStyle styleGameOver = new Button.ButtonStyle();
		styleGameOver.up = new TextureRegionDrawable(new TextureRegion(gameOverTexture));
		buttonGameOver = new Button(styleGameOver);

		upButtonTexture = new Texture(Gdx.files.internal("up.png"));
		downButtonTexture = new Texture(Gdx.files.internal("up.png"));
		Button.ButtonStyle styleRestart = new Button.ButtonStyle();
		styleRestart.up = new TextureRegionDrawable(new TextureRegion(upButtonTexture));
		styleRestart.down = new TextureRegionDrawable(new TextureRegion(downButtonTexture));
		buttonRestart = new Button(styleRestart);
	}

	/**
	 * Set the height of bird and all the positions of tubes
	 */
	public void initBirdTubes() {
		birdY = Gdx.graphics.getHeight()*4/5;
		for(int i=0; i<numberTubes; i++){
			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - tubeMargin); // between -0.5 and +0.5 !!!!!
			tubeX[i] = Gdx.graphics.getWidth()*3/2 + i * distanceBetweenTubes; //add distance between tube with Width/2
			topRectangles[i] = new Rectangle();
			bottomRectangles[i] = new Rectangle();
		}
	}

	@Override
	public void render() {
		// clear the screen
		//Gdx.gl.glClearColor(0, 0, 0, 0);
		//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin(); // start display the sprite, prepare the screen
		// display full of the screen, background image First !!!! order !!!!
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		/**
		 * change bird every 200 seconds, show animation of bird
		 */
		if(countsShowBird < 3) {
			countsShowBird++;
		} else {
			countsShowBird = 0;
			if(flapState == 0){
				flapState = 1;
			} else {
				flapState = 0;
			}
		}

		batch.draw(birds[flapState], Gdx.graphics.getWidth()/2 - birds[flapState].getWidth()/2, birdY);
		birdCircle.set(Gdx.graphics.getWidth()/2, birdY + birds[flapState].getHeight()/2, birds[flapState].getWidth()/2);
		/**
		 * Check collision while the game is alive
		 */
		if(gameState == 1) {
			for(int n=0; n< numberTubes; n++){
				if(Intersector.overlaps(birdCircle, topRectangles[n]) || Intersector.overlaps(birdCircle, bottomRectangles[n])){
					Gdx.app.log("Collision check ", "  Game Over 2");
					gameState = 2;
				}
			}
		}
		/**
		 * 判断游戏的3种状态  设置游戏场景元素
		 */
		if(gameState == 0){
			/**
			 * 	Waiting to start the game just for the first time, wake up the game !
			 */
			batch.draw(gameLogo, Gdx.graphics.getWidth()/2 - gameLogo.getWidth()/2, Gdx.graphics.getHeight()/2 + birds[flapState].getHeight());
			if(Gdx.input.justTouched()) {
				gameState = 1;
			}
		} else if(gameState ==1) { // The game is active only for gameState = 1

			stage.clear(); // clear the button on the stage !!!

			if(Gdx.input.justTouched()) {
				velocity = -30; // Bird go up after touching
			}
			/**
			 * 依次添加tube到界面上，当移动到左边后增加距离，如此反复循环
			 * 当tube被返回到最右边位置后，将重新计算它的便宜的位置，避免与初始的值一致
			 * 画Tube时从标准的位置出发，加上上下值的偏移量即可，上下同时计算正负
			 */
			// Check one tube and then passe next one
			if(tubeX[scoringTube] < Gdx.graphics.getWidth()/2 - topTube.getWidth()) {
				Gdx.app.log("score", String.valueOf(score));
				score++;
				getScoreMusic.play();
				getScoreMusic.setVolume(40);
				if(scoringTube < numberTubes - 1){
					scoringTube++;
				} else {
					scoringTube = 0;
				}
				if(score > bestScore){
					bestScore = score;
				}
				/**
				 * Set the 3 levels of game
				 */
				if(score > 10 && score < 20){
					gameLevel = 2;
					gap = 500;
					tubeVelocity = 5;
				}
				if(score >= 20 && score < 30) {
					gameLevel = 3;
					gap = 400;
					tubeVelocity = 6;
				}
				if(score >= 30 && score < 40) {
					gameLevel = 4;
					gap = 400;
					tubeVelocity = 7;
				}
				if(score >= 40) {
					gameLevel = 5;
					gap = 400;
					tubeVelocity = 8;
				}
			}
			// Draw all tubes on the screen
			for(int j=0; j < numberTubes; j++){
				if(tubeX[j] < -topTube.getWidth()) {
					tubeX[j] += numberTubes * distanceBetweenTubes;
					tubeOffset[j] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - tubeMargin); // between -0.5 and +0.5 !!!!!
				} else {
					tubeX[j] = tubeX[j] - tubeVelocity; // Tube moving
				}
				batch.draw(topTube, tubeX[j], Gdx.graphics.getHeight()/2 + gap/2 + tubeOffset[j]);
				batch.draw(bottomTube, tubeX[j], Gdx.graphics.getHeight()/2 - gap/2 - bottomTube.getHeight() + tubeOffset[j]);
				topRectangles[j] = new Rectangle(tubeX[j], Gdx.graphics.getHeight()/2 + gap/2 + tubeOffset[j], topTube.getWidth(), topTube.getHeight());
				bottomRectangles[j] = new Rectangle(tubeX[j], Gdx.graphics.getHeight()/2 - gap/2 - bottomTube.getHeight() + tubeOffset[j], bottomTube.getWidth(), bottomTube.getHeight());
			}

			if(birdY > 0){ // above the screen
				velocity = velocity + gravity;
				birdY = birdY - velocity;
				if(birdY > Gdx.graphics.getHeight()){
					birdY = Gdx.graphics.getHeight();
				}
			} else { // below the screen : game over 1; do not change the height of Bird !!!!
				gameState = 2;
			}
			// Draw the 2 scores above tube
			fontBestScore.draw(batch, String.valueOf("Best: " +bestScore), Gdx.graphics.getWidth()/10, Gdx.graphics.getHeight()*9/10 - 50);
			fontScore.draw(batch, String.valueOf("Score: "+ score), Gdx.graphics.getWidth()/10, Gdx.graphics.getHeight()*8/10 - 50);

			fontGameLevel.draw(batch, String.valueOf("Level - "+ gameLevel), Gdx.graphics.getWidth()/2 - 200, Gdx.graphics.getHeight() - 50);

		} else if(gameState == 2) { // TO DO : restart the game, stop the button listener !
			/**
			 * 绘制button一定要在最后 显示在界面的最上层, use the size of stage to display these button
			 */
			buttonGameOver.setSize(700, 250);
			buttonGameOver.setPosition(stage.getWidth()/2 - 350, stage.getHeight()/2);
			stage.addActor(buttonGameOver);

			buttonRestart.setSize(200, 120);
			buttonRestart.setPosition(stage.getWidth()/2 - 100, stage.getHeight()/2 - 100);
			buttonRestart.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					Gdx.app.log("Button clicked", "Restart the game !");
					gameState = 1;
					velocity = 0;
					score = 0;
					gameLevel = 1;
					scoringTube = 0;
					initBirdTubes();
				}
			});
			stage.addActor(buttonRestart);

			stage.act();
			stage.draw();
		}

		/*
		// Draw circle (x, y, radius) & rectangle (x, y, width, height)
		shapeRendererBird.begin(ShapeRenderer.ShapeType.Filled);
		shapeRendererBird.setColor(Color.RED);
		shapeRendererBird.circle(birdCircle.x, birdCircle.y, birdCircle.radius);
		for(int k=0; k < numberTubes; k++){
			shapeRendererBird.rect(tubeX[k], Gdx.graphics.getHeight()/2 + gap/2 + tubeOffset[k], topTube.getWidth(), topTube.getHeight());
			shapeRendererBird.rect(tubeX[k], Gdx.graphics.getHeight()/2 - gap/2 - bottomTube.getHeight() + tubeOffset[k], bottomTube.getWidth(), bottomTube.getHeight());
		}
		shapeRendererBird.end();
		*/
		batch.end();
	}

	/**
	 * Dispose all the resources after ending the game
	 */
	@Override
	public void dispose() {
		batch.dispose();
		if (upButtonTexture != null) {
			upButtonTexture.dispose();
		}
		if (downButtonTexture != null) {
			downButtonTexture.dispose();
		}
		if (stage != null) {
			stage.dispose();
		}
	}

}
