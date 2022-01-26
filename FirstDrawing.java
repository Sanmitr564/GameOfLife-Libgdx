import com.badlogic.gdx.ApplicationAdapter; 
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer; 
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle; 
import com.badlogic.gdx.math.Circle; 
import com.badlogic.gdx.Input.Keys; 
import com.badlogic.gdx.math.Vector2; 
import com.badlogic.gdx.math.MathUtils; 
import com.badlogic.gdx.math.Intersector; 
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.*;

import java.util.Arrays;

//NOTE: Always reset the JVM before compiling (it is the small loop arrow in the
//bottom right corner of the project window)!! 

public class FirstDrawing extends ApplicationAdapter 
{
    private OrthographicCamera camera; //the camera to our world
    private Viewport viewport; //maintains the ratios of your world
    private ShapeRenderer renderer; //used to draw textures and fonts 
    private BitmapFont font; //used to draw fonts (text)
    private SpriteBatch batch; //also needed to draw fonts (text)

    private Grid board;
    private boolean isPlaying;
    private int timer;

    @Override//called once when we start the game
    public void create(){

        camera = new OrthographicCamera(); 
        viewport = new FitViewport(GLOBAL.WORLD_WIDTH, GLOBAL.WORLD_HEIGHT, camera); 
        renderer = new ShapeRenderer(); 
        font = new BitmapFont(); 
        batch = new SpriteBatch();//if you want to use images instead of using ShapeRenderer 

        board = new Grid("lifeNums.txt");
        isPlaying = false;
        timer = 1;
        //board.readFile();

    }

    @Override//called 60 times a second
    public void render(){
        preRender();
        drawBoard();
        if(GLOBAL.generation > GLOBAL.totalGenerations){
            GLOBAL.totalGenerations = GLOBAL.generation;
            setSnapshot();
        }
        click();
        control();
        timer++;
        //TODO comment out when complete
        System.out.print(GLOBAL.generation);
        System.out.print(" " + GLOBAL.snapshots.length);
        System.out.println(" " + GLOBAL.SQUARE_SIZE);
    }

    @Override
    public void resize(int width, int height){
        viewport.update(width, height, true); 
    }

    @Override
    public void dispose(){
        renderer.dispose(); 
        batch.dispose(); 
    }

    public void preRender(){
        viewport.apply(); 

        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float delta = Gdx.graphics.getDeltaTime();//1/60 

        //draw everything on the screen
        renderer.setProjectionMatrix(viewport.getCamera().combined);
    }

    private void drawBoard(){
        renderer.begin(ShapeType.Filled);
        for(int y = 0; y<GLOBAL.SIDE_LENGTH; y++){
            for(int x = 0; x<GLOBAL.SIDE_LENGTH; x++){
                renderer.setColor(Color.WHITE);
                //TODO un-comment when done testing
                if(board.getAlive(GLOBAL.SIDE_LENGTH-(y)-GLOBAL.OFFSET,x+1-GLOBAL.OFFSET)){
                    renderer.setColor(Color.RED);
                }
                renderer.rect(1+x*(GLOBAL.SQUARE_SIZE+2), 1+y*(GLOBAL.SQUARE_SIZE+2), GLOBAL.SQUARE_SIZE, GLOBAL.SQUARE_SIZE);
            }
        }
        renderer.end();
    }

    private void click(){
        int mouseX = -1;
        int mouseY = -1;
        //Vector2 mouseClick = new Vector2(-1,-1);
        if(Gdx.input.justTouched()){
            mouseX = Gdx.input.getX();
            mouseY = Gdx.input.getY();
        }
        Vector2 mouseClick = viewport.unproject(new Vector2(mouseX,mouseY));

        if(mouseClick.x>0 && mouseClick.x<800){
            int r = (int)(1+GLOBAL.SIDE_LENGTH-(int)mouseClick.y/(GLOBAL.SQUARE_SIZE+2)) - GLOBAL.OFFSET;
            int c = (int)(1+(int)mouseClick.x/(GLOBAL.SQUARE_SIZE+2))-GLOBAL.OFFSET;

            board.changeAlive(r,c);

            //System.out.println(r + " " + c);
        }
    }

    private void control(){
        if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)){
            board.updateOrganism();
            GLOBAL.generation++;
            GLOBAL.IS_PLAYING = false;
        }else if(GLOBAL.IS_PLAYING && timer%GLOBAL.UPDATE_SPEED==0){
            board.updateOrganism();
            GLOBAL.generation++;
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)){
            GLOBAL.generation -= (GLOBAL.generation==0) ? 0 : 1;
            setGrid();
            GLOBAL.IS_PLAYING = false;
        }
       
        if(Gdx.input.isKeyJustPressed(Input.Keys.R)){
            board.reset();
        }
        
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
            GLOBAL.IS_PLAYING = !GLOBAL.IS_PLAYING;
        }
        
        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
            if(Gdx.input.isKeyJustPressed(Input.Keys.UP)){
                GLOBAL.UPDATE_SPEED -= (GLOBAL.UPDATE_SPEED == 1) ? 0 : 1;
            }else if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)){
                GLOBAL.UPDATE_SPEED += 5;
            }
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.UP)){
            GLOBAL.UPDATE_SPEED -= GLOBAL.UPDATE_SPEED<6 ? 0 : 5;
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)){
            GLOBAL.UPDATE_SPEED += 5;
        }

        if(Gdx.input.isKeyJustPressed(Keys.MINUS)){
            sizeDecrease();
        }else if(Gdx.input.isKeyJustPressed(Keys.EQUALS)){
            sizeIncrease();
        }
    }

    private void setGrid(){
        board.setGrid(GLOBAL.snapshots[GLOBAL.generation]);
    }

    //TODO replace with array clone
    private void setSnapshot(){
        if (GLOBAL.generation == GLOBAL.snapshots.length - 1) {
            int length = GLOBAL.snapshots.length;
            Organism[][][] temp = GLOBAL.snapshots;
            GLOBAL.snapshots = new Organism[length*2][GLOBAL.ARRAY_LENGTH][GLOBAL.ARRAY_LENGTH];
            for(int r = 0; r<temp.length; r++){
                GLOBAL.snapshots[r] = temp[r];
            }
        }
        for(int r = 1; r<board.getGrid().length-1; r++){
            for(int c = 1; c<board.getGrid()[r].length-1; c++){
                GLOBAL.snapshots[GLOBAL.generation][r][c] = new Organism(r,c,board.getGrid()[r][c].isAlive());
            }
        }
    }

    private void sizeDecrease(){
        GLOBAL.SIDE_LENGTH -= GLOBAL.SIDE_LENGTH>1 ? 1:0;
        GLOBAL.SQUARE_SIZE = ((float)GLOBAL.WORLD_WIDTH/GLOBAL.SIDE_LENGTH)-2;
        GLOBAL.OFFSET = (GLOBAL.SIDE_LENGTH-GLOBAL.INITIAL_SIDE_LENGTH)/2;
    }

    //TODO make array reassign when increasing size
    private void sizeIncrease(){
        GLOBAL.SIDE_LENGTH++;
        GLOBAL.SQUARE_SIZE = ((float)GLOBAL.WORLD_WIDTH/GLOBAL.SIDE_LENGTH)-2;
        GLOBAL.OFFSET = (GLOBAL.SIDE_LENGTH-GLOBAL.TOTAL_SIDE_LENGTH)/2;
        if(GLOBAL.SIDE_LENGTH > GLOBAL.TOTAL_SIDE_LENGTH-1) {
            GLOBAL.TOTAL_SIDE_LENGTH *= 2;
            Organism[][][] tempSnapshot = new Organism[GLOBAL.snapshots.length][GLOBAL.snapshots[0].length][GLOBAL.snapshots[0][0].length];
            //TODO add a loop to initialize tempSnapshot
            initializeTempSnapshot(tempSnapshot);
            //Organism[][] tempBoard = board.getGrid().clone();
            int length = GLOBAL.snapshots.length;
            GLOBAL.snapshots = new Organism[length*2][GLOBAL.TOTAL_SIDE_LENGTH][GLOBAL.TOTAL_SIDE_LENGTH];
            board.resetGrid(GLOBAL.TOTAL_SIDE_LENGTH);
            for (int o1 = 0; o1 < GLOBAL.snapshots.length; o1++) {
                for (int o2 = 0; o2 < GLOBAL.snapshots[o1].length; o2++) {
                    for (int o3 = 0; o3 < GLOBAL.snapshots[o1][o2].length; o3++) {
                        GLOBAL.snapshots[o1][o2][o3] = new Organism(o2, o3, false);
                    }
                }
            }

            for (int o1 = 0; o1 < GLOBAL.snapshots.length / 2; o1++) {
                for (int o2 = GLOBAL.snapshots[o1].length / 4; o2 < GLOBAL.snapshots[o1].length * 3 / 4; o2++) {
                    for (int o3 = GLOBAL.snapshots[o1][o2].length / 4; o3 < GLOBAL.snapshots[o1][o2].length * 3 / 4; o3++) {
                        GLOBAL.snapshots[o1] = tempSnapshot[o1];
                    }
                }
            }

            board.setGrid(GLOBAL.snapshots[GLOBAL.snapshots.length / 2]);
            GLOBAL.OFFSET = (GLOBAL.SIDE_LENGTH-GLOBAL.TOTAL_SIDE_LENGTH)/2;

        }
    }
    //FIXME
    // - use expanding arrays to change size of play field
    // - don't make array smaller, only change rendered field
    private void initializeTempSnapshot(Organism[][][] a){
        for (int o1 = 0; o1 < GLOBAL.snapshots.length; o1++) {
            for (int o2 = 0; o2 < GLOBAL.snapshots[o1].length; o2++) {
                for (int o3 = 0; o3 < GLOBAL.snapshots[o1][o2].length; o3++) {
                    a[o1] = GLOBAL.snapshots[o1].clone();
                }
            }
        }
    }
}
