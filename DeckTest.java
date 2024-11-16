import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class DeckTest{
    private Deck deck;

    @Before
    public void setUp(){
        deck = new Deck(1);
    }

    @Test
    public void testInitialSizeZero(){
        assertEquals(0, deck.size());
    }

    @Test
    public void testAddCardIncreasesSize() {
        // Test that adding a card increases the size of the deck
        deck.addCard(5);
        assertEquals(1, deck.size());
    }

    @Test
    public void testDrawCardRemovesCard() {
        // Test that drawing a card removes it from the deck
        deck.addCard(5);
        int drawnCard = deck.drawCard();
        assertEquals(5, drawnCard);
        assertEquals(0, deck.size());
    }

    @Test
    public void testIsEmptyReturnsTrueForEmptyDeck() {
        // Test that the deck is empty initially
        assertTrue(deck.isEmpty());
    }

    @Test
    public void testIsEmptyReturnsFalseAfterAddingCard() {
        // Test that the deck is not empty after adding a card
        deck.addCard(5);
        assertFalse(deck.isEmpty());
    }

    @Test
    public void testGetId() {
        // Test that the ID is correctly set in the constructor
        assertEquals(1, deck.getId());
    }

    // @Test
    // public void testLockAndUnlock() {
    //     // Test that the lock and unlock methods work correctly
    //     deck.lock();
    //     // At this point, no other thread can acquire the lock, so the lock is successfully held
    //     assertTrue(deck.lock.tryLock() == false); // lock should not be available
    //     deck.unlock();
    //     // After unlocking, the lock should be available
    //     assertTrue(deck.lock.tryLock() == true);  // lock should be available now
    // }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testDrawCardThrowsExceptionWhenEmpty() {
        // Test that drawing a card from an empty deck throws an exception
        deck.drawCard();
    }


}