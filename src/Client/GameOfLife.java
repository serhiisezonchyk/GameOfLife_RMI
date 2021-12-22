package Client;

import Server.Compute;
import Server.GameProcess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.Random;
import java.util.Scanner;

public class GameOfLife {
    int AREA_SIZE = 8;
    int FIELD_SIZE = 805;
    int POINT_RADIUS = FIELD_SIZE/AREA_SIZE;
    boolean[][] gen = new boolean[AREA_SIZE][AREA_SIZE];
    boolean[][] saveGen = new boolean[AREA_SIZE][AREA_SIZE];
    JMenuItem runBut = new JMenuItem("Run!");
    Random rd = new Random();
    JFrame frame;
    Canvas canvasPanel;
    boolean isPlay = false;
    int counter = 1;
    int servCount = 1;

    public Compute comp;
    String name = "rmi://localhost/Compute";

    public Compute comp1;
    String name1 = "rmi://localhost/Compute1";

    public Compute comp2;
    String name2 = "rmi://localhost/Compute2";

    public Compute comp3;
    String name3 = "rmi://localhost/Compute3";

    boolean [][] gen1;
    boolean [][] gen2;
    boolean [][] gen3;
    boolean [][] gen4;
    boolean [][] genTest;
    long timeStart;
    float fulTime = 0;
    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        System.setProperty("java.security.policy", "C:\\Users\\Сергей\\IdeaProjects\\MOIL3\\src\\Server\\rmi.policy\\");
//        if(System.getSecurityManager() == null) {
//            System.setSecurityManager(new RMISecurityManager());
//        }
        new GameOfLife().go();
    }
    void go(){
        frame = new JFrame("l3 MOI Game Of Life");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(FIELD_SIZE +15,FIELD_SIZE+63);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        JMenuBar mb = new JMenuBar();
        JMenu menu, menuServ;
        JMenuItem clearBut, randomBut, setSize, saveMat, readMat, serv1,serv2,serv3,serv4;
        menuServ = new JMenu("Servers");
        serv1 = new JMenuItem("1");
        serv1.addActionListener(e -> {
            servCount = 1;
        });
        serv2 = new JMenuItem("2");
        serv2.addActionListener(e -> {
            servCount = 2;
        });
        serv3 = new JMenuItem("3");
        serv3.addActionListener(e -> {
            servCount = 3;
        });
        serv4 = new JMenuItem("4");
        serv4.addActionListener(e -> {
            servCount = 4;
        });
        menuServ.add(serv1);
        menuServ.add(serv2);
        menuServ.add(serv3);
        menuServ.add(serv4);
        menu = new JMenu("Config");
        saveMat = new JMenuItem("Save matrix");
        saveMat.addActionListener(e -> {
            try(FileWriter writer = new FileWriter("D:\\matrix1000.txt")){
                for (int i = 0; i < AREA_SIZE; ++i) {
                    for (int j = 0; j < AREA_SIZE; ++j) {
                        writer.write(gen[i][j]+ "  ");
                    }
                    writer.write("\r\n");
                    System.out.println();
                }
            } catch(IOException ex){
                System.out.println(ex.getMessage());
            }
        });
        readMat = new JMenuItem("Read matrix");
        readMat.addActionListener(e -> {
            File file;
            if(AREA_SIZE == 100)
                file = new File("D:\\matrix.txt");
            else
                file = new File("D:\\matrix1000.txt");

            try {
                Scanner sizeScanner = new Scanner(file);
                String[] temp = sizeScanner.nextLine().split(" ");
                sizeScanner.close();
                int nMatrix = temp.length;
                System.out.println(nMatrix);
                Scanner scanner = new Scanner(file);
                AREA_SIZE = (nMatrix+1)/2;
                for (int i = 0; i < AREA_SIZE; i++) {
                    String[] numbers = scanner.nextLine().split(" ");
                    for (int j = 0; j < AREA_SIZE; j++) {
                        gen[i][j] = Boolean.parseBoolean(numbers[j]);
                    }
                }
                scanner.close();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        });
        clearBut = new JMenuItem("Clear area");
        clearBut.addActionListener(e -> {
            setClear();
        });
        randomBut = new JMenuItem("Set random lives");
        randomBut.addActionListener(e -> {
            setRandom();
        });
        runBut.addActionListener(e -> {
            isPlay = !isPlay;
            if(!isPlay){
                JOptionPane.showMessageDialog(frame, "Stoped by user " + ((System.currentTimeMillis() - timeStart)/1000f) );
            }
            runBut.setText(isPlay? "Stop!" : "Run!");
            counter = 1;
            fulTime = 0;
            try{
            switch (servCount){
                case 2:{
                    comp=(Compute) Naming.lookup(name);
                    comp1=(Compute) Naming.lookup(name1);
                break;}
                case 3:{
                    comp=(Compute) Naming.lookup(name);
                    comp1=(Compute) Naming.lookup(name1);
                    comp2=(Compute) Naming.lookup(name2);
                    break;}
                case 4:{
                    comp=(Compute) Naming.lookup(name);
                    comp1=(Compute) Naming.lookup(name1);
                    comp2=(Compute) Naming.lookup(name2);
                    comp3=(Compute) Naming.lookup(name3);
                    break;}
                default:{
                    comp=(Compute) Naming.lookup(name);
                    break;}
            }

                new Thread(()->{
                    timeStart = System.currentTimeMillis();
                    while(true){
                        if(isPlay){
                            System.out.println("start");
                            System.out.println(counter + " " + counter%3);
                            if(counter%3 == 0){
                                boolean isCycle = isCycle(gen,saveGen);
                                if(isCycle){
                                    isPlay = false;
                                    counter = 1;
                                    break;
                                }
                            }else if (counter%3 == 1){
                                for(int x = 0 ;x < AREA_SIZE; x++)
                                    for(int y = 0 ; y< AREA_SIZE;y++){
                                        saveGen[x][y] = gen[x][y];
                                    }
                            }
                            counter++;

                            if(!allIsDead()){
                                isPlay = false;
                                counter = 1;
                                break;
                            }
                            switch (servCount){
                                case 2:{
                                    try {
                                        start(2);
                                    } catch (RemoteException ex) {
                                        ex.printStackTrace();
                                    }
                                    break;}
                                case 3:{
                                    try {
                                        start(3);
                                    } catch (RemoteException ex) {
                                        ex.printStackTrace();
                                    }
                                    break;}
                                case 4:{
                                    try {
                                        start(4);
                                    } catch (RemoteException ex) {
                                        ex.printStackTrace();
                                    }
                                    break;}
                                default:{
                                    try {
                                        start(1);
                                    } catch (RemoteException ex) {
                                        ex.printStackTrace();
                                    }
                                    break;}
                            }

                        }
                    }
                }).start();
            } catch(Exception e1) {
                System.err.println("Compute exception: " + e1.getMessage());
                e1.printStackTrace();
            }
        });
        menu.add(saveMat);
        menu.add(readMat);
        menu.add(menuServ);
        menu.add(clearBut);
        menu.add(randomBut);
        menu.add(runBut);
        setSize = new JMenuItem("Set size of area x*x");
        setSize.addActionListener(e -> {
            SizeDialog sd =new SizeDialog(frame, this);
            sd.setVisible(true);
        });
        menu.add(setSize);
        mb.add(menu);
        frame.setJMenuBar(mb);
        canvasPanel = new Canvas();
        canvasPanel.setBackground(Color.white);
        canvasPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(e.BUTTON1 == e.getButton()){
                    gen[e.getX()/POINT_RADIUS][e.getY()/POINT_RADIUS] = true;
                    System.out.println("{"+e.getX()/POINT_RADIUS + ", " + e.getY()/POINT_RADIUS+"}");
                    canvasPanel.repaint();
                }else {
                    gen[e.getX()/POINT_RADIUS][e.getY()/POINT_RADIUS] = false;
                    canvasPanel.repaint();
                }
            }
        });
        frame.getContentPane().add(BorderLayout.CENTER,canvasPanel);
        frame.setVisible(true);

    }
    public void start(int countServ) throws RemoteException {
        switch (countServ){
            case 2:{
                int xNext = AREA_SIZE/2;

                    GameProcess task = new GameProcess(0,xNext+1,0,AREA_SIZE,gen);
                    gen1 = (boolean[][]) comp.executeTask(task);

                    GameProcess task1 = new GameProcess(xNext -1, AREA_SIZE, 0, AREA_SIZE, gen);
                    gen2 = (boolean[][]) comp1.executeTask(task1);

                for(int x = 0 ;x < xNext; x++)
                    for(int y = 0 ; y< AREA_SIZE;y++){
                        gen[x][y] = gen1[x][y];
                    }

                int x1=1;
                for(int x = xNext ;x < AREA_SIZE; x++){
                    for(int y = 0 ; y< AREA_SIZE;y++){
                        gen[x][y] = gen2[x1][y];
                    }
                    x1++;
                }
                fulTime += (System.currentTimeMillis() - timeStart)/2000f;
                break;
            }
            case 3: {
                int xNext1 = AREA_SIZE/3, xNext2 = AREA_SIZE/3 * 2;
                GameProcess task = new GameProcess(0,xNext1+2,0,AREA_SIZE,gen);
                gen1 = (boolean[][]) comp.executeTask(task);

                GameProcess task1 = new GameProcess(xNext1, xNext2 + 2, 0, AREA_SIZE, gen);
                gen2 = (boolean[][]) comp1.executeTask(task1);

                GameProcess task2 = new GameProcess(xNext2, AREA_SIZE, 0, AREA_SIZE, gen);
                gen3 = (boolean[][]) comp2.executeTask(task2);


                for(int x = 0 ;x < xNext1+1; x++)
                    for(int y = 0 ; y< AREA_SIZE;y++){
                        gen[x][y] = gen1[x][y];
                    }

                int x1=1;
                for(int x = xNext1+1 ;x < xNext2+1; x++){
                    for(int y = 0 ; y< AREA_SIZE;y++){
                        gen[x][y] = gen2[x1][y];
                    }
                    x1++;
                }

                x1=1;
                for(int x = xNext2+1 ;x < AREA_SIZE; x++){
                    for(int y = 0 ; y< AREA_SIZE;y++){
                        gen[x][y] = gen3[x1][y];
                    }
                    x1++;
                }
                fulTime += (System.currentTimeMillis() - timeStart)/3000f;
                break;
            } case 4: {
                int xNext1 = AREA_SIZE/4, xNext2 = AREA_SIZE/4 * 2, xNext3 = AREA_SIZE/4 * 3;
                GameProcess task = new GameProcess(0,xNext1+2,0,AREA_SIZE,gen);
                gen1 = (boolean[][]) comp.executeTask(task);

                GameProcess task1 = new GameProcess(xNext1, xNext2 + 2, 0, AREA_SIZE, gen);
                gen2 = (boolean[][]) comp1.executeTask(task1);

                GameProcess task2 = new GameProcess(xNext2, xNext3 + 2, 0, AREA_SIZE, gen);
                gen3 = (boolean[][]) comp2.executeTask(task2);

                GameProcess task3 = new GameProcess(xNext3, AREA_SIZE, 0, AREA_SIZE, gen);
                gen4 = (boolean[][]) comp3.executeTask(task3);

                for(int x = 0 ;x < xNext1+1; x++)
                    for(int y = 0 ; y< AREA_SIZE;y++){
                        gen[x][y] = gen1[x][y];
                    }

                int x1=1;
                for(int x = xNext1+1 ;x < xNext2+1; x++){
                    for(int y = 0 ; y< AREA_SIZE;y++){
                        gen[x][y] = gen2[x1][y];
                    }
                    x1++;
                }

                x1=1;
                for(int x = xNext2+1 ;x < xNext3+1; x++){
                    for(int y = 0 ; y< AREA_SIZE;y++){
                        gen[x][y] = gen3[x1][y];
                    }
                    x1++;
                }
                x1=1;
                for(int x = xNext3+1 ;x < AREA_SIZE; x++){
                    for(int y = 0 ; y< AREA_SIZE;y++){
                        gen[x][y] = gen4[x1][y];
                    }
                    x1++;
                }
                fulTime += (System.currentTimeMillis() - timeStart)/4000f;
                break;}
            default:{

                try {
                    GameProcess task = new GameProcess(0,AREA_SIZE,0,AREA_SIZE,gen);
                    genTest = (boolean[][]) comp.executeTask(task);
                    fulTime += (System.currentTimeMillis() - timeStart)/1000f;
                } catch(Exception e) {
                    System.err.println("ComputePassword exception: " + e.getMessage());
                    e.printStackTrace();
                }
                for(int x = 0 ;x < AREA_SIZE; x++)
                    for(int y = 0 ; y< AREA_SIZE;y++){
                        gen[x][y] = genTest[x][y];
                    }

            break;}

        }

    }
    public class Canvas extends JPanel{
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            for(int x = 0;x < AREA_SIZE;x++){
                for(int y = 0;y < AREA_SIZE; y++){
                    if(gen[x][y]==false){
                        g.setColor(new Color(243, 243, 243));
                        g.fillOval(x*POINT_RADIUS, y* POINT_RADIUS, POINT_RADIUS, POINT_RADIUS);
                    }else {
                        g.setColor(Color.black);
                        g.fillOval(x*POINT_RADIUS, y* POINT_RADIUS, POINT_RADIUS, POINT_RADIUS);
                    }
                }
                canvasPanel.repaint();
            }
        }
    }
    public void setClear(){
        for(int x = 0;x < AREA_SIZE;x++){
            for(int y = 0;y < AREA_SIZE; y++){
                gen[x][y] = false;
            }
        }
        canvasPanel.repaint();
    }

    public void setRandom(){
        for(int x = 0;x < AREA_SIZE;x++){
            for(int y = 0;y < AREA_SIZE; y++){
                gen[x][y] = rd.nextBoolean();
            }
        }
        canvasPanel.repaint();
    }
    public void changeSize(int size){
        AREA_SIZE = size;
        POINT_RADIUS = FIELD_SIZE/AREA_SIZE;
        gen = new boolean[AREA_SIZE][AREA_SIZE];
        saveGen = new boolean[AREA_SIZE][AREA_SIZE];
        canvasPanel.repaint();
    }
    public boolean allIsDead(){
        for (int x = 0; x<AREA_SIZE; x++) {
            for (int y = 0; y < AREA_SIZE; y++) {
                if (gen[x][y] == true) {
                    return true;
                }
            }
        }
        JOptionPane.showMessageDialog(frame, "Pupulation is dead " + fulTime/1000f );
        runBut.setText("Run!");
        return false;
    }
    public boolean isCycle(boolean[][]gen, boolean[][] saveGen){
        for (int x = 0; x<AREA_SIZE; x++) {
            for (int y = 0; y < AREA_SIZE; y++) {
                if(gen[x][y] != saveGen[x][y]){
                    return false;
                }
            }
        }
        JOptionPane.showMessageDialog(frame, "Pupulation is cycle/static " + fulTime/1000f );
        runBut.setText("Run!");
        return true;
    }
}
