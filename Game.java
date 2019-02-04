import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

public class Game extends JPanel implements Runnable, MouseListener {

    private int SQUARE;
    private Tile[] tiles;

    private int height;
    private int width;
    private boolean playing = true;

    private Color[] colours = new Color[]{
            Color.WHITE,
            new Color(175,238,238),
            new Color(65,105,225),
            new Color(60,179,113),
            new Color(255,165,0),
            new Color(221,160,221),
            new Color(255,215,0)};

    Game(int width, int height, int size){
        this.width = width;
        this.height = height;
        this.SQUARE = size;

        JFrame frame = new JFrame();
        this.addMouseListener(this);
        frame.setSize(SQUARE*(width)+30,SQUARE*(height) + 80);
        frame.add(this);
        frame.setVisible(true);

        createGame(width, height);
        this.run();
    }

    private void createGame(int width, int height){
        tiles = new Tile[width*height];
        Random r = new Random();

        for(int i = 0; i < tiles.length; i++){


            if(r.nextDouble() > 0.8){
                tiles[i] = new Tile(true);
            }
            else {
                tiles[i] = new Tile(false);
            }
        }

        for(int i = 0; i < tiles.length; i++){
            Tile t = tiles[i];

            int count = 0;

            int column = i / width;
            int row = i % width;

            // to left
            if(row > 0){
                if(tiles[i-1].isBomb()){
                    count++;
                }
            }

            // to right
            if( row < width-1){
                if(tiles[i+1].isBomb()){
                    count++;
                }
            }

            // above
            if(i >= width){
                if(tiles[i-width].isBomb()){
                    count++;
                }

                if(row > 0){
                    if(tiles[i-1 - width].isBomb()){
                        count++;
                    }
                }

                // to right
                if( row < width-1){
                    if(tiles[i+1 - width].isBomb()){
                        count++;
                    }
                }
            }

            // below
            if(i < width*height - width){
                if(tiles[i+width].isBomb()){
                    count++;
                }

                if(row > 0){
                    if(tiles[i-1 + width].isBomb()){
                        count++;
                    }
                }

                // to right
                if( row < width-1){
                    if(tiles[i+1 + width].isBomb()){
                        count++;
                    }
                }
            }

            t.setNumber(count);
        }
    }

    public void paint(Graphics g){
        for(int i = 0; i < tiles.length; i++){
            Tile t = tiles[i];

            if(t == null){
                continue;
            }

            int column = i / width;
            int row = i % width;

            g.setColor(Color.BLACK);
            //g.fillRect(SQUARE*row, SQUARE*column, SQUARE, SQUARE);
            if(t.isFlagged()){

                g.setColor(Color.RED);
                g.fillRect(SQUARE*row + SQUARE/4, SQUARE*column + SQUARE/5, 3*SQUARE/8, SQUARE/5);

                g.setColor(Color.BLACK);
                g.fillRect(SQUARE*row + SQUARE/2, SQUARE*column + SQUARE/5, SQUARE/8, 3*SQUARE/5);
            }
            else if(t.isVisible()){
                Color c;
                if(t.isBomb()){

                    c = new Color(220,20,60);

                } else {
                    c = colours[t.getNumber()];
                }
                g.setColor(c);
                g.fillRect(SQUARE*row + SQUARE/26, SQUARE*column + SQUARE/26, SQUARE - SQUARE/13, SQUARE - SQUARE/13);

                g.setColor(Color.BLACK);

                if(!t.isBomb() && t.getNumber() > 0) {
                    g.setFont(new Font("TimesRoman", Font.BOLD, SQUARE/2));
                    g.drawString(Integer.toString(t.getNumber()), SQUARE * row + SQUARE / 2, SQUARE * column + SQUARE / 2);
                }
            }
            else {
                g.setColor(Color.lightGray);
                g.fillRect(SQUARE*row + SQUARE/32, SQUARE*column + SQUARE/32, SQUARE - SQUARE/16, SQUARE - SQUARE/16);
            }

        }
    }

    private void check(int i){
        ArrayList<Integer> list = new ArrayList<>();

        int column = i / width;
        int row = i % width;

        // to left
        if(row > 0){
            list.add(i-1);
        }

        // to right
        if( row < width-1){
            list.add(i+1);
        }

        // above
        if(i >= width){
            list.add(i-width);

            if(row > 0){
                list.add(i-1-width);
            }

            // to right
            if( row < width-1){
                list.add(i+1-width);
            }
        }

        // below
        if(i < width*height - width){
            list.add(i+width);

            if(row > 0){
                list.add(i-1+width);
            }

            // to right
            if( row < width-1){
                list.add(i+1+width);
            }
        }

        int bombs = tiles[i].getNumber();
        int count = 0;

        for(int surround: list){

            if(tiles[surround].isFlagged()){
                count++;
            }
        }

        if(count == bombs){
            for(int s: list){
                if(!tiles[s].isFlagged() && !tiles[s].isVisible()){
                    tiles[s].setVisible(true);
                    if(tiles[s].getNumber() == 0){
                        search(s);
                    }

                    if(tiles[s].isBomb()){
                        for(Tile tile: tiles){
                            tile.setVisible(true);
                            tile.setFlagged(false);
                        }
                        playing = false;
                    }
                }

            }
        }
    }

    private void search(int index){
        ArrayList<Integer> open = new ArrayList<>();
        ArrayList<Integer> closed = new ArrayList<>();
        open.add(index);

        while(!open.isEmpty()){

            int i = open.get(0);
            open.remove(0);
            closed.add(i);
            tiles[i].setVisible(true);

            if(tiles[i].getNumber() == 0){

                int column = i / width;
                int row = i % width;

                // to left
                if(row > 0){
                    if(!closed.contains(i-1)){
                        open.add(i-1);
                    }

                }

                // to right
                if( row < width-1){
                    if(!closed.contains(i+1)){
                        open.add(i+1);
                    }
                }

                // above
                if(i >= width){
                    if(!closed.contains(i-width)){
                        open.add(i-width);
                    }

                    if(row > 0){
                        if(!closed.contains(i-1-width)){
                            open.add(i-1-width);
                        }
                    }

                    // to right
                    if( row < width-1){
                        if(!closed.contains(i+1-width)){
                            open.add(i+1-width);
                        }
                    }

                }

                // below
                if(i <= width*height - width){
                    if(!closed.contains(i+width)){
                        open.add(i+width);
                    }

                    if(row > 0){
                        if(!closed.contains(i-1+width)){
                            open.add(i-1+width);
                        }
                    }

                    // to right
                    if( row < width-1){
                        if(!closed.contains(i+1+width)){
                            open.add(i+1+width);
                        }
                    }
                }

            }
        }

    }

    @Override
    public void run() {

        while(playing){

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            repaint();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(SwingUtilities.isRightMouseButton(e)){

            int i = e.getX()/SQUARE + width * (e.getY()/SQUARE);
            Tile t = tiles[i];

            if(t.isVisible() && !t.isFlagged()){

                check(i);

            } else {
                t.setFlagged(!t.isFlagged());
                t.setVisible(!t.isVisible());
            }



        } else {
            System.out.println(SwingUtilities.isRightMouseButton(e));
            //System.out.println(e.getX()/SQUARE + width * (e.getY()/SQUARE));
            Tile t = tiles[e.getX()/SQUARE + width * (e.getY()/SQUARE)];
            t.setVisible(true);

            if(t.isBomb()){
                for(Tile tile: tiles){
                    tile.setVisible(true);
                    tile.setFlagged(false);
                }
                playing = false;
            }
            else {
                if(t.getNumber() == 0){
                    search(e.getX()/SQUARE + width * (e.getY()/SQUARE));
                }
            }
        }

        boolean done = true;
        for(Tile t: tiles){
            if(t.isBomb() && t.isFlagged()){
                // g
            } else {
                done = false;
                break;
            }
            if(!t.isBomb() && !t.isFlagged()){
                // g
            } else {
                done = false;
                break;
            }
        }

        if(done){
            System.out.println("You win");
        }





    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

}
