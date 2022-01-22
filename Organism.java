public class Organism{
    private int neighbors;
    private int r;
    private int c;
    boolean alive;

    public Organism(){
        neighbors = 0;
        r = 0;
        c = 0;
        alive = false;
    }

    public Organism(int neighbors){
        this.neighbors = neighbors;
        r = 0;
        c = 0;
        alive = false;
    }

    public Organism(int neighbors, int r, int c, boolean alive){
        this.neighbors = neighbors;
        this.r = r;
        this.c = c;
        this.alive = alive;
    }

    public Organism(int r, int c, boolean alive){
        neighbors = 0;
        this.r = r;
        this.c = c;
        this.alive = alive;
    }

    public Organism(int r, int c){
        neighbors = 0;
        this.r = r;
        this.c = c;
        alive = false;
    }

    /*
    public int getNeighbors(){
    return neighbors;
    }

    public void setNeighbors(int n){
    neighbors = n;
    }

    public int getRow(){
    return r;
    }

    public int getCol(){
    return c;
    }
    */

    public boolean isAlive(){
        return alive;
    }

    public void setAlive(boolean b){
        alive = b;
    }

    public void updateLife(){
        if(neighbors == 3)
            alive = true;
        else if(neighbors<2 || neighbors>3)
            alive = false;
    }

    public void setRow(int r){
        this.r = r;
    }

    public void setCol(int c){
        this.c = c;
    }

    public void updateNeighbors(Organism[][] arr){
        neighbors = 0;
        for(int i = -1; i<2; i++){
            for(int j = -1; j<2; j++){
                if(arr[r+i][c+j].isAlive() && !(i==0 && j==0)){
                    neighbors++;
                }
            }
        }
    }

    public void update(Organism[][] arr){
        updateNeighbors(arr);
        updateLife();
    }
}