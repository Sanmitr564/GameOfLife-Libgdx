
public class GLOBAL{
    public static final int WORLD_WIDTH = 800;
    public static final int WORLD_HEIGHT = 800;

    public static int UPDATE_SPEED = 30;
    public static boolean IS_PLAYING = false;

    public static Organism[][][] snapshots = new Organism[2][22][22];
    public static int generation = 0;
    public static int totalGenerations = -1;
    
    public static final int INITIAL_SIDE_LENGTH = 20;
    public static int SIDE_LENGTH = 20;
    public static int ARRAY_LENGTH = SIDE_LENGTH+2;

    public static float SQUARE_SIZE = (float)((double)WORLD_WIDTH/SIDE_LENGTH)-2;

    public static int OFFSET = (SIDE_LENGTH-INITIAL_SIDE_LENGTH)/2;
}