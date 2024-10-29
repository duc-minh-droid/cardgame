import java.util.concurrent.locks.*;

public class GameStatus {
    private static final Lock lock = new ReentrantLock();
    private static boolean gameWon = false;
    private static int winnerId = -1; // To store the ID of the winning player

    public static boolean isGameWon() {
        lock.lock();
        try {
            return gameWon;
        } finally {
            lock.unlock();
        }
    }

    public static void declareWinner(int playerId) {
        lock.lock();
        try {
            if (!gameWon) {
                gameWon = true;
                winnerId = playerId; // Store the winner's ID
                System.out.println("Player " + winnerId + " won!");
            }
        } finally {
            lock.unlock();
        }
    }

    public static int getWinnerId() {
        lock.lock();
        try {
            return winnerId;
        } finally {
            lock.unlock();
        }
    }
}
