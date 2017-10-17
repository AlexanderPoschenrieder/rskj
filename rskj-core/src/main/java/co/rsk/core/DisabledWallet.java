package co.rsk.core;

import org.ethereum.core.Account;

import java.util.List;

public class DisabledWallet implements Wallet {
    @Override
    public List<byte[]> getAccountAddresses() {
        throw new DisabledWalletException();
    }

    @Override
    public byte[] addAccount() {
        throw new DisabledWalletException();
    }

    @Override
    public byte[] addAccount(String passphrase) {
        throw new DisabledWalletException();
    }

    @Override
    public Account getAccount(byte[] address) {
        throw new DisabledWalletException();
    }

    @Override
    public Account getAccount(byte[] address, String passphrase) {
        throw new DisabledWalletException();
    }

    @Override
    public boolean unlockAccount(byte[] address, String passphrase, long duration) {
        throw new DisabledWalletException();
    }

    @Override
    public boolean unlockAccount(byte[] address, String passphrase) {
        throw new DisabledWalletException();
    }

    @Override
    public boolean lockAccount(byte[] address) {
        throw new DisabledWalletException();
    }

    @Override
    public byte[] addAccountWithSeed(String seed) {
        throw new DisabledWalletException();
    }

    @Override
    public byte[] addAccountWithPrivateKey(byte[] privateKeyBytes) {
        throw new DisabledWalletException();
    }

    @Override
    public byte[] addAccountWithPrivateKey(byte[] privateKeyBytes, String passphrase) {
        throw new DisabledWalletException();
    }
}
