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
    int generation;

    @Override//called once when we start the game
    public void create(){

        camera = new OrthographicCamera(); 
        viewport = new FitViewport(GLOBAL.WORLD_WIDTH, GLOBAL.WORLD_HEIGHT, camera); 
        renderer = new ShapeRenderer(); 
        font = new BitmapFont(); 
        batch = new SpriteBatch();//if you want to use images instead of using ShapeRenderer 

        board = new Grid(/*"lifeNums.txt"*/);
        isPlaying = false;
        timer = 1;
        //board.readFile();

    }

    @Override//called 60 times a second
    public void render(){
        preRender();
        drawBoard();
        if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)){
            board.updateOrganism();
            generation++;
            GLOBAL.IS_PLAYING = false;
        }else if(GLOBAL.IS_PLAYING && timer%GLOBAL.UPDATE_SPEED==0){
            board.updateOrganism();
            generation++;
        }
        click();
        control();
        timer++;
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
        for(int y = 0; y<20; y++){
            for(int x = 0; x<20; x++){
                renderer.setColor(Color.WHITE);
                if(board.getAlive(20-y,x+1)){
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
        Vector2 mouseClick = new Vector2(-1,-1);
        if(Gdx.input.justTouched()){
            mouseX = Gdx.input.getX();
            mouseY = Gdx.input.getY();
        }
        mouseClick = viewport.unproject(new Vector2(mouseX,mouseY));

        if(mouseClick.x>0 && mouseClick.x<800){
            int r = 20-(int)mouseClick.y/(GLOBAL.SQUARE_SIZE+2);
            int c = 1+(int)mouseClick.x/(GLOBAL.SQUARE_SIZE+2);

            board.changeAlive(r,c);

            //System.out.println(r + " " + c);
        }
    }

    private void control(){
        if(Gdx.input.isKeyJustPressed(Input.Keys.R)){
            board.reset();
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
            GLOBAL.IS_PLAYING = !GLOBAL.IS_PLAYING;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
            if(Gdx.input.isKeyJustPressed(Input.Keys.UP)){
                GLOBAL.UPDATE_SPEED -= 5;
            }else if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)){
                GLOBAL.UPDATE_SPEED += 5;
            }
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.UP)){
            GLOBAL.UPDATE_SPEED -= 5;
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)){
            GLOBAL.UPDATE_SPEED += 5;
        }
    }
}
