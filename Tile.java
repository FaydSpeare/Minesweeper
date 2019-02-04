public class Tile {

    private int number;
    private boolean isBomb;
    private boolean visible;
    private boolean flagged;

    public Tile(boolean isBomb){
        this.isBomb = isBomb;

        if(isBomb){
            number = 0;
        }

        this.visible = false;
        this.flagged = false;
    }

    public boolean isBomb() {
        return isBomb;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    public boolean isFlagged() {
        return flagged;
    }
}

