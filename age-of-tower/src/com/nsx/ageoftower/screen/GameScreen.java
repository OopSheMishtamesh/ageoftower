package com.nsx.ageoftower.screen;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;


import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.tiled.TileAtlas;
import com.badlogic.gdx.graphics.g2d.tiled.TileMapRenderer;
import com.badlogic.gdx.graphics.g2d.tiled.TiledLoader;
import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.TimeUtils;
import com.nsx.ageoftower.AgeOfTower;
import com.nsx.ageoftower.actors.Enemies;
import com.nsx.ageoftower.hud.AotHud;
import com.nsx.ageoftower.utils.AotGameEngine;
import com.nsx.ageoftower.utils.Level;
import com.nsx.ageoftower.utils.Wave;



public class GameScreen extends AbstractScreen{
	private OrthographicCamera camera;
	private Texture texture;
	private Sprite sprite;
	Texture Tower1Image; 

	AgeOfTower _mAot ;

	//tiled map
	TileMapRenderer tileMapRenderer;     
	TiledMap map;        
	TileAtlas atlas; 
	int TilePos[] ;
	int TowerPos[] ;
	int EnemiePos ;
	int newX ;
	int newY ;
	
	Image img;
	Enemies enemies ;
	World world;
	AotGameEngine _engine;
	AotHud _hud;
	private Level _level;
	//pathfinder

	//private GameMap map = new GameMap();
	long lastMoveTime;
	private Integer currentLvl;

	public GameScreen(AgeOfTower ageOfTower ){
		super(ageOfTower);
		_mAot = ageOfTower ; 
		
		//-- a supprimer lorsque tou sera rassemblé, issue:15
		TextureAtlas hudAtlas = new TextureAtlas(Gdx.files.internal("HUD/hud.pack"));
		_hud = new AotHud(new Skin(Gdx.files.internal("skin/default2.skin"),hudAtlas ));
		
     	_level = new Level();
     	
     	//-- a supprimer, pour test
     	_level.getWaves().add(new Wave());
     	_level.getWaves().add(new Wave());
    	_level.getWaves().add(new Wave());
     	_level.getWaves().add(new Wave());
    	_level.getWaves().add(new Wave());
     	_level.getWaves().add(new Wave());
     	
		_engine = new AotGameEngine(_hud,_level);

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();

		texture = new Texture(Gdx.files.internal("data/title.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		Tower1Image = new Texture(Gdx.files.internal("data/tower1.png"));

		TextureRegion region = new TextureRegion(texture, 0, 0, 512, 275);

		sprite = new Sprite(region);
		sprite.setSize(0.9f, 0.9f * sprite.getHeight() / sprite.getWidth());
		sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
		sprite.setPosition(-sprite.getWidth()/2, -sprite.getHeight()/2);

		//lecture tiled map
		String line= "";
		String file= "data/packer/level1.tmx";

		int i=0 ; 

		TilePos= new int[600];
		TowerPos= new int[600];

		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(Gdx.files.internal(file).read()));

			if (br == null)
				throw new FileNotFoundException("File not found: "	+ file);

			do {
				line = br.readLine();
				if (line != null) {
					if ( line.contains("<tile gid")){
						StringTokenizer fileT= new StringTokenizer(line ,"\"");
					try {
						fileT.nextToken();
						TilePos[i]=Integer.parseInt(fileT.nextToken());
						} catch (Exception e) {} 
						i++ ;
					}
				}
			} while (line != null);
			br.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}

		map = TiledLoader.createMap(Gdx.files.internal("data/packer/level1.tmx")); 
		atlas = new TileAtlas(map, Gdx.files.internal("data/packer"));     
		// Create the renderer      
		tileMapRenderer = new TileMapRenderer(map, atlas, 1, 1, 32,32);

		world = new World(new Vector2(0f, -1), true);
		enemies= new Enemies(world) ;
		
		_mStage = new Stage();
		
		_mStage.addActor(enemies);
		 
		_mStage.addActor(_hud);
		//_mStage.addListener(_hud);
	}

	@Override
	public void render(float delta) {
		_engine.update(delta);

		int TileTouch;
		int i;


		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		tileMapRenderer.render(camera);
		camera.update();

		//fpsLogger.log();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		sprite.draw(batch);

		 if(Gdx.input.justTouched()) {  
			   Vector3 touchPos = new Vector3(); 
		   touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		   TileTouch= (int)(touchPos.x/32) + ((int)((touchPos.y/32))*25);
		   if ( (TilePos[TileTouch]) == 12  )
		   		   TowerPos[TileTouch]= 1 ;
		 }

		 for (i=0 ; i<600 ; i++){
			 if ( TowerPos[i] == 1)
				  batch.draw(Tower1Image, (i%25)*32, (14*32)- ((int)i/25)*32,32,32);  
		 }
		batch.end();
		
        world.step(1/60f, i, i) ;
		enemies.nb= 0;
        
		for (Actor enemietmp : enemies.getChildren()) {
			EnemiePos= ((int)(enemietmp.getY()/28))*33 + (int)enemietmp.getX()/24 ;
			Vector2 coords= new Vector2(enemietmp.getX(), enemietmp.getY());
			enemietmp.localToStageCoordinates(coords);
			enemietmp.getStage().stageToScreenCoordinates(coords);
	
			if (enemietmp.getActions().size == 0){	
				newX=enemies.getNoeud()*32;
				enemies.NoeudPos++ ;
				newY=enemies.getNoeud()*32;
				enemietmp.addAction(parallel (
												Actions.moveTo(newX,newY, 2)
													)
								);
				enemies.NoeudPos-- ;
	
				if  (enemies.nb < (enemies.maxenemies -1)){
					enemies.nb ++ ;
				}else{
					enemies.nb= 0 ;
					if (enemies.NoeudPos < 30)
						enemies.NoeudPos= enemies.NoeudPos  + 2;
					else
						enemies.NoeudPos= 0;
					}
			}
		}
		super.render(delta);
	}
}