import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

public class DeckTest{
    private Deck deck;


    @Before
    public void initialisation() throws IOException{
        deck = new Deck(1);
    }

    @Test
    public void testAddCard() {
        Card card1 = new Card(5);
        Card card2 = new Card(10);
        deck.addCard(card1);
        deck.addCard(card2);
        assertEquals(2, deck.size());
    }

    @Test
    public void testDrawCard() {
        Card card1 = new Card(5);
        Card card2 = new Card(6);
        deck.addCard(card1);
        deck.addCard(card2);

        Card drawnCard = deck.drawCard();
        assertEquals(5, drawnCard.getValue());
        assertEquals(1, deck.size());
    }

    @Test
    public void testIsEmpty() {
        assertTrue(deck.isEmpty());
        deck.addCard((new Card(3)));
        assertFalse(deck.isEmpty());
    }

    @Test
    public void testGetId() {
        // Test that the ID is correctly set in the constructor
        assertEquals(1, deck.getId());
    }

    @Test
    public void testLogDeckContents() {
        deck.addCard(new Card(3));
        deck.addCard(new Card(5));
        deck.logDeckContents();
        
        File logFile = new File("gameOutput/deck1_output.txt");
        assertTrue(logFile.exists());
        assertTrue(logFile.length() > 0);
    }


}