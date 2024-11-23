import org.junit.Test;
import static org.junit.Assert.*;

public class CardTest {
    
    @Test
    public void testCardConstrutor() {
        Card card = new Card(10);

        assertNotNull(card);
        assertEquals(10, card.getValue());
    }
}
