import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TestPlayer {

    private Player player;
    private Deck deck;
    private CardGameM game;

    @Before
    public void setUp() {
        deck = new Deck(1);
        game = mock(CardGameM.class);
        player = new Player(1, deck, game); // Player ID 1
    }

    @Test
    public void testAddCard() {
        Card card = new Card(7);
        player.addCard(card);

        // Verify that the card was added to the player's hand
        assertEquals(1, player.hand.size());
        assertEquals(7, player.hand.get(0).getValue());
    }

    @Test
    public void testCheckWinningHand() {
        player.addCard(new Card(1));
        player.addCard(new Card(1));
        player.addCard(new Card(1));
        player.addCard(new Card(1));

        // Verify that the player's hand is a winning hand
        assertTrue(player.checkWinningHand());
        
        player.addCard(new Card(2));
        // Verify that the player's hand is no longer a winning hand
        assertFalse(player.checkWinningHand());
    }

    @Test
    public void testPlayTurnDrawCard() {
        Card cardInDeck = new Card(4);
        deck.addCard(cardInDeck);

        Deck nextPlayerDeck = new Deck(2);
        when(game.getNextPlayer(player)).thenReturn(new Player(2, nextPlayerDeck, game));

        player.addCard(new Card(5));
        player.addCard(new Card(5));
        player.addCard(new Card(7));
        player.addCard(new Card(5));

        player.playTurn();

        // Verify the drawn card is added to the player's hand
        assertEquals(5, player.hand.size());
        assertTrue(player.hand.contains(cardInDeck));

        // Verify a card is discarded to the next player's deck
        assertEquals(1, nextPlayerDeck.size());
    }

    @Test
    public void testPlayTurnEmptyDeck() {
        Deck nextPlayerDeck = new Deck(2);
        when(game.getNextPlayer(player)).thenReturn(new Player(2, nextPlayerDeck, game));

        player.addCard(new Card(4));
        player.addCard(new Card(4));
        player.addCard(new Card(3));
        player.addCard(new Card(4));

        player.playTurn();

        // Since the deck is empty, no card is drawn
        assertEquals(4, player.hand.size());

        // Verify a card is still discarded to the next player's deck
        assertEquals(1, nextPlayerDeck.size());
    }

    @Test
    public void testWinningNotification() {
        AtomicInteger winningPlayer = new AtomicInteger(0);
        when(game.winningPlayer).thenReturn(winningPlayer);

        player.addCard(new Card(3));
        player.addCard(new Card(3));
        player.addCard(new Card(3));
        player.addCard(new Card(3));

        player.playTurn();

        // Verify that the player sets the winningPlayer atomic variable
        assertEquals(2, winningPlayer.get()); // Player ID 1 + 1
    }
}


