public class Card {
    private final int value;

    public Card (int value){
        this.value = value;
    }

    public synchronized int getValue(){
        return value;
    }
}
