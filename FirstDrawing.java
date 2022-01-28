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
// - bottom right corner of the project window)!!

public class FirstDrawing extends ApplicationAdapter {
    private OrthographicCamera camera; //the camera to our world
    private Viewport viewport; //maintains the ratios of your world
    private ShapeRenderer renderer; //used to draw textures and fonts 
    private BitmapFont font; //used to draw fonts (text)
    private SpriteBatch batch; //also needed to draw fonts (text)

    private Grid board;
    private boolean isPlaying;
    private int timer;

    @Override//called once when we start the game
    public void create() {

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
    public void render() {
        preRender();
        drawBoard();
        if (GLOBAL.generation > GLOBAL.totalGenerations) {
            GLOBAL.totalGenerations = GLOBAL.generation;
            setSnapshot();
        }
        click();
        control();
        timer++;
        //TODO comment out when complete
        //System.out.print(GLOBAL.generation);
        //System.out.print(" " + GLOBAL.snapshots.length);
        //System.out.println(" " + GLOBAL.SQUARE_SIZE);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        renderer.dispose();
        batch.dispose();
    }

    public void preRender() {
        viewport.apply();

        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float delta = Gdx.graphics.getDeltaTime();//1/60 

        //draw everything on the screen
        renderer.setProjectionMatrix(viewport.getCamera().combined);
    }

    private void drawBoard() {
        renderer.begin(ShapeType.Filled);
        for (int y = 0; y < GLOBAL.SIDE_LENGTH; y++) {
            for (int x = 0; x < GLOBAL.SIDE_LENGTH; x++) {
                renderer.setColor(Color.WHITE);
                //TODO un-comment when done testing
                if (board.getAlive(GLOBAL.SIDE_LENGTH - (y) - GLOBAL.OFFSET, x + 1 - GLOBAL.OFFSET)) {
                    renderer.setColor(Color.RED);
                }
                renderer.rect(1 + x * (GLOBAL.SQUARE_SIZE + 2), 1 + y * (GLOBAL.SQUARE_SIZE + 2), GLOBAL.SQUARE_SIZE, GLOBAL.SQUARE_SIZE);
            }
        }
        renderer.end();
    }

    private void click() {
        int mouseX = -1;
        int mouseY = -1;
        //Vector2 mouseClick = new Vector2(-1,-1);
        if (Gdx.input.justTouched()) {
            mouseX = Gdx.input.getX();
            mouseY = Gdx.input.getY();
            isPlaying = false;
        }
        Vector2 mouseClick = viewport.unproject(new Vector2(mouseX, mouseY));

        if (mouseClick.x > 0 && mouseClick.x < 800) {
            int r = (int) (1 + GLOBAL.SIDE_LENGTH - (int) mouseClick.y / (GLOBAL.SQUARE_SIZE + 2)) - GLOBAL.OFFSET;
            int c = (int) (1 + (int) mouseClick.x / (GLOBAL.SQUARE_SIZE + 2)) - GLOBAL.OFFSET;

            board.changeAlive(r, c);

            //System.out.println(r + " " + c);
        }
    }

    private void control() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            board.updateOrganism();
            GLOBAL.generation++;
            isPlaying = false;
        } else if (isPlaying && timer % GLOBAL.UPDATE_SPEED == 0) {
            board.updateOrganism();
            GLOBAL.generation++;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            GLOBAL.generation -= (GLOBAL.generation == 0) ? 0 : 1;
            setGrid();
            isPlaying = false;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            board.reset();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            isPlaying = !isPlaying;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                GLOBAL.UPDATE_SPEED -= (GLOBAL.UPDATE_SPEED == 1) ? 0 : 1;
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                GLOBAL.UPDATE_SPEED += 5;
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            GLOBAL.UPDATE_SPEED -= GLOBAL.UPDATE_SPEED < 6 ? 0 : 5;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            GLOBAL.UPDATE_SPEED += 5;
        }

        if (Gdx.input.isKeyJustPressed(Keys.MINUS)) {
            sizeDecrease();
        } else if (Gdx.input.isKeyJustPressed(Keys.EQUALS)) {
            sizeIncrease();
        }
    }

    private void setGrid() {
        board.setGrid(GLOBAL.snapshots[GLOBAL.generation]);
    }

    //TODO replace with array clone
    private void setSnapshot() {
        if (GLOBAL.generation == GLOBAL.snapshots.length - 1) {
            int length = GLOBAL.snapshots.length;
            Organism[][][] temp = GLOBAL.snapshots;
            GLOBAL.snapshots = new Organism[length * 2][GLOBAL.ARRAY_LENGTH][GLOBAL.ARRAY_LENGTH];
            for (int r = 0; r < temp.length; r++) {
                GLOBAL.snapshots[r] = temp[r];
            }
            /*
            for (int gen = temp.length; gen  < length*2; gen++){
                for(int row = 0; row < GLOBAL.snapshots[gen].length; row++){
                    for(int col = 0; col < GLOBAL.snapshots[gen][row].length; col++){
                        GLOBAL.snapshots[gen][row][col] = new Organism(row, col);
                    }
                }
            }

             */
        }
        //NOTE replace uninitialized edges if doesn't work
        for (int r = 0; r < board.getGrid().length; r++) {
            for (int c = 0; c < board.getGrid()[r].length; c++) {
                GLOBAL.snapshots[GLOBAL.generation][r][c] = new Organism(r, c, board.getGrid()[r][c].isAlive());
            }
        }
    }

    private void sizeDecrease() {
        GLOBAL.SIDE_LENGTH -= GLOBAL.SIDE_LENGTH > 1 ? 1 : 0;
        GLOBAL.SQUARE_SIZE = ((float) GLOBAL.WORLD_WIDTH / GLOBAL.SIDE_LENGTH) - 2;
        GLOBAL.OFFSET = (GLOBAL.SIDE_LENGTH - GLOBAL.TOTAL_SIDE_LENGTH) / 2;
    }

    //TODO make array reassign when increasing size
    private void sizeIncrease() {
        GLOBAL.SIDE_LENGTH++;
        GLOBAL.SQUARE_SIZE = ((float) GLOBAL.WORLD_WIDTH / GLOBAL.SIDE_LENGTH) - 2;
        GLOBAL.OFFSET = (GLOBAL.SIDE_LENGTH - GLOBAL.TOTAL_SIDE_LENGTH) / 2;
        if (GLOBAL.SIDE_LENGTH > GLOBAL.TOTAL_SIDE_LENGTH - 1) {
            //NOTE
            // - Use global.arraylength to set lengths not total side length
            // - update global.arraylength after setting tempsnapshot to current arraylength
            // - The first and last columns and rows of the organism 2d arrays are null
            // - use expanding arrays to change size of play field
            // - don't make array smaller, only change rendered field
            Organism[][][] tempSnapshot = GLOBAL.snapshots;
            GLOBAL.TOTAL_SIDE_LENGTH *= 2;
            GLOBAL.ARRAY_LENGTH = GLOBAL.TOTAL_SIDE_LENGTH + 2;
            int length = GLOBAL.snapshots.length;
            GLOBAL.snapshots = new Organism[length][GLOBAL.ARRAY_LENGTH][GLOBAL.ARRAY_LENGTH];
            GLOBAL.OFFSET = (GLOBAL.SIDE_LENGTH - GLOBAL.TOTAL_SIDE_LENGTH) / 2;
            for (int gen = 0; gen < GLOBAL.snapshots.length; gen++) {
                for (int row = 0; row < GLOBAL.snapshots[gen].length; row++) {
                    for (int col = 0; col < GLOBAL.snapshots[gen][row].length; col++) {
                        GLOBAL.snapshots[gen][row][col] = new Organism(row, col);
                    }
                }
            }

            for (int gen = 0; gen < GLOBAL.generation; gen++) {

                for (int row = 1; row < tempSnapshot[gen].length - 1; row++) {
                    for (int col = 1; col < tempSnapshot[gen][row].length - 1; col++) {
                        GLOBAL.snapshots[gen][row + GLOBAL.TOTAL_SIDE_LENGTH / 4][col + GLOBAL.TOTAL_SIDE_LENGTH / 4].setAlive(tempSnapshot[gen][row][col].isAlive());
                        //GLOBAL.snapshots[gen][row+GLOBAL.TOTAL_SIDE_LENGTH/4][col+GLOBAL.TOTAL_SIDE_LENGTH/4].addC(GLOBAL.TOTAL_SIDE_LENGTH/4);
                        //GLOBAL.snapshots[gen][row+GLOBAL.TOTAL_SIDE_LENGTH/4][col+GLOBAL.TOTAL_SIDE_LENGTH/4].addR(GLOBAL.TOTAL_SIDE_LENGTH/4);
                    }
                }
            }
            for (int row = 1; row < tempSnapshot[GLOBAL.generation].length - 1; row++) {
                for (int col = 1; col < tempSnapshot[GLOBAL.generation][row].length - 1; col++) {
                    GLOBAL.snapshots[GLOBAL.generation][row + GLOBAL.TOTAL_SIDE_LENGTH / 4][col + GLOBAL.TOTAL_SIDE_LENGTH / 4].setAlive(board.getGrid()[row][col].isAlive());
                }
            }

            board.resetGrid(GLOBAL.ARRAY_LENGTH);
            board.setGrid(GLOBAL.snapshots[GLOBAL.generation]);
        }
    }
}