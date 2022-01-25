import chn.util.*;

import java.util.Arrays;

public class Grid{
    private Organism[][] grid;
    private FileInput in;

    public Grid(){
        grid = new Organism[GLOBAL.ARRAY_LENGTH][GLOBAL.ARRAY_LENGTH];
        for(int r = 0; r<grid.length; r++){
            for(int c = 0; c<grid[r].length; c++){
                grid[r][c] = new Organism(r,c);
            }
        }
    }

    public Grid(String in){
        grid = new Organism[GLOBAL.ARRAY_LENGTH][GLOBAL.ARRAY_LENGTH];
        this.in = new FileInput(in);
        for(int r = 0; r<grid.length; r++){
            for(int c = 0; c<grid[r].length; c++){
                grid[r][c] = new Organism(r,c);
            }
        }
        readFile();
    }

    private void readFile(){
        in.readInt();
        while(in.hasMoreTokens()){
            int r = in.readInt() + GLOBAL.OFFSET;
            int c = in.readInt() + GLOBAL.OFFSET;

            grid[r][c].setAlive(true);
        }
    }

    public void updateOrganism(){
        for(int r = 1; r<grid.length-1; r++){
            for(int c = 1; c<grid[r].length-1; c++){
                grid[r][c].updateNeighbors(grid);
            }
        }
        for(int r = 1; r<grid.length-1; r++){
            for(int c = 1; c<grid[r].length-1; c++){
                grid[r][c].updateLife();
            }
        }
    }

    public void print(){
        System.out.println("\t12345678901234567890");

        for(int r = 1; r<grid.length-1; r++){
            System.out.print(r + "\t");
            for(int c = 1; c<grid[r].length-1; c++){
                if(grid[r][c].isAlive())
                    System.out.print("*");
                else
                    System.out.print(" ");
            }
            System.out.println();
        }
    }

    public int totalAlive(){
        int sum = 0;
        for(int r = 1; r<grid.length-1; r++){
            for(int c = 1; c<grid[r].length-1; c++){
                if(grid[r][c].isAlive())
                    sum++;
            }
        }
        return sum;
    }

    public int row10Alive(){
        int sum = 0;
        for(int c = 1; c<grid[11].length; c++){
            if(grid[10][c].isAlive()){
                sum++;
            }
        }
        return sum;
    }

    public int col10Alive(){
        int sum = 0;
        for(int r = 1; r<grid[11].length; r++){
            if(grid[r][10].isAlive()){
                sum++;
            }
        }
        return sum;
    }

    public boolean getAlive(int r, int c){
        return grid[r][c].isAlive();
    }

    public void changeAlive(int r, int c){
        grid[r][c].setAlive(!grid[r][c].isAlive());
    }
    //TODO replace with enhanced for loop
    public void reset(){
        /*
        for(int r = 0; r<grid.length; r++){
            for(int c = 0; c<grid[r].length; c++){
                grid[r][c].setAlive(false);
            }
        }
        */
        for(Organism[] r : grid){
            for(Organism c : r){
                c.setAlive(false);
            }
        }
    }
    //TODO replace with array clone
    public void setGrid(Organism[][] o){

        for(int r = 1; r<o.length-1; r++){
            for(int c = 1; c<o[r].length-1; c++){
                grid[r][c].setAlive(o[r][c].isAlive());
            }
        }

        //grid = o.clone();
    }

    public Organism[][] getGrid(){
        return grid;
    }
}