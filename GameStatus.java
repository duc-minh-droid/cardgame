import java.util.concurrent.locks.*;

public class GameStatus {
    private static final Lock lock = new ReentrantLock();
    private static volatile boolean gameWon = false;
    private static int winningPlayerId = -1; // To store the ID of the winning player

    // public static boolean isGameWon() {
    //     lock.lock();
    //     try {
    //         return gameWon;
    //     } finally {
    //         lock.unlock();
    //     }
    // }

    // public static int getWinningPlayerId() {
    //     lock.lock();
    //     try {
    //         return winningPlayerId;
    //     } finally {
    //         lock.unlock();
    //     }
    // }

    // public static void declareWinner(int playerId) {
    //     lock.lock();
    //     try {
    //         if (!gameWon) {
    //             gameWon = true;
    //             winningPlayerId = playerId;
    //         }
    //     } finally {
    //         lock.unlock();
    //     }
    // }

    // public static int getWinnerId() {
    //     lock.lock();
    //     try {
    //         return winnerId;
    //     } finally {
    //         lock.unlock();
    //     }
    // }

    public static synchronized boolean isGameWon() {
        return gameWon;
    }

    public static synchronized int getWinningPlayerId() {
        return winningPlayerId;
    }

    public static synchronized void declareWinner(int playerId) {
        if (!gameWon) {
            gameWon = true;
            winningPlayerId = playerId;
            System.out.println("Player " + playerId + " has won the game!");
            GameStatus.class.notifyAll();  // Notify all threads waiting on GameStatus
        }
    }
}
