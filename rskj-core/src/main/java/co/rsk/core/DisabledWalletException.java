package co.rsk.core;

public class DisabledWalletException extends RuntimeException {
    DisabledWalletException() {
        super("The local wallet feature is disabled");
    }
}
