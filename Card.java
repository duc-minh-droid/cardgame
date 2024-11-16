public class Card {
    private int denomination;

    public synchronized int getDenomination() {
        return denomination;
    }

    public void setDenomination(int denomination) {
        this.denomination = denomination;
    }

    /**
     * Creates a card object with specified denomination.
     * 
     * @param value Integer for the denomination of the card
     */
    public Card(int value){
        setDenomination(value);
    }

}
