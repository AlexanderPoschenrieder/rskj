package co.rsk.core;

public class DisabledWalletException extends RuntimeException {
    public DisabledWalletException() {
        super("The local wallet feature is disabled");
    }
}
