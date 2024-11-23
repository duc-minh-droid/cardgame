import org.junit.Test;
import static org.junit.Assert.*;

public class CardTest {

    @Test
    public void testCardValue() {
        // Create a card with a specific value
        Card card = new Card(10);

        // Verify that the value is set correctly
        assertEquals(10, card.getValue());
    }

    @Test
    public void testEqualityOfCards() {
        // Create two cards with the same value
        Card card1 = new Card(5);
        Card card2 = new Card(5);

        // Create another card with a different value
        Card card3 = new Card(7);

        // Verify equality based on the value
        assertEquals(card1.getValue(), card2.getValue());
        assertNotEquals(card1.getValue(), card3.getValue());
    }

    @Test
    public void testBoundaryValues() {
        // Test minimum and maximum boundaries for card values
        Card minCard = new Card(Integer.MIN_VALUE);
        Card maxCard = new Card(Integer.MAX_VALUE);

        // Verify that the values are set correctly
        assertEquals(Integer.MIN_VALUE, minCard.getValue());
        assertEquals(Integer.MAX_VALUE, maxCard.getValue());
    }
}

