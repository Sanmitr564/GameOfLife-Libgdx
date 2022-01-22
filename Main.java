public class Main{
    public static void main(String[] args){
        Grid board = new Grid("lifeNums.txt");
        board.readFile();
        System.out.println("Generation 0");
        board.print();
        for(int i = 0; i<5; i++){
            board.updateOrganism();
            System.out.println("Generation " + (i+1));
            board.print();
        }
        System.out.println("Total alive: " + board.totalAlive());
        System.out.println("Alive in row 10: " + board.row10Alive());
        System.out.println("Alive in column 10: " + board.col10Alive());
    }
}