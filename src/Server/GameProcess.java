package Server;

import java.security.NoSuchAlgorithmException;

public class GameProcess implements Task {
    public int lowX, lowY, endX, endY;
    public boolean[][] gen;
    public boolean[][] nextGen;
    public GameProcess(int lowX,int endX, int lowY, int endY , boolean[][] gen) {
        this.lowX = lowX;
        this.endX = endX;
        this.lowY = lowY;
        this.endY = endY;
        this.gen = gen;
        this.nextGen =  new boolean[endX-lowX][endY-lowY];
    }

    @Override
    public Object execute() {
        try {
            System.out.println("Start");
            return nextGenTable();
        }catch (IllegalArgumentException | NoSuchAlgorithmException e){
            return null;
        }
    }

    public Object nextGenTable() throws NoSuchAlgorithmException {
        return findNextGen();
    }

    public boolean[][] findNextGen() {

        int xNext = 0;
        for (int x = lowX; x < endX; x++){
            int yNext = 0;
            for (int y = lowY;y < endY;y++){
                int countNeigh = countNeighbors(x,y);
                nextGen[xNext][yNext] = gen[x][y];
                nextGen[xNext][yNext] = (countNeigh == 3) ? true : nextGen[xNext][yNext];
                nextGen[xNext][yNext] = (countNeigh < 2 || countNeigh>3) ? false : nextGen[xNext][yNext];
                yNext++;
            }
            xNext++;
        }
        return nextGen;
    }

    int countNeighbors(int x, int y){
        int count = 0;
        for (int x1 = -1; x1 < 2; x1++)
            for(int y1 = -1; y1 < 2; y1++){
                int nx = x + x1;
                int ny = y + y1;
                if(nx >= lowX && ny >= lowY && nx<endX && ny< endY ) {
                    count+=(gen[nx][ny])? 1:0;
                } else continue;
            }
        if(gen[x][y]) count--;
        return count;
    }
}
