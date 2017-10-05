package co.rsk.net.sync;

import co.rsk.net.MessageChannel;

public interface SyncInformation {
    boolean isKnownBlock(byte[] hash);

    // eventually inline ConnectionPointFinder in the class that uses it
    ConnectionPointFinder getConnectionPointFinder();

    boolean hasLowerDifficulty(MessageChannel peer);
}