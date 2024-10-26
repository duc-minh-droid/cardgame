import java.util.concurrent.locks.*;

class GameStatus {
    private static final Lock lock = new ReentrantLock();
    private static boolean gameWon = false;

    public static boolean isGameWon() {
        lock.lock();
        try {
            return gameWon;
        } finally {
            lock.unlock();
        }
    }

    public static void declareWinner() {
        lock.lock();
        try {
            gameWon = true; // Set the gameWon flag to true to stop all player threads
            System.out.println("Game has a winner. Stopping all threads.");
        } finally {
            lock.unlock();
        }
    }
}
