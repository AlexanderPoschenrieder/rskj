package co.rsk.core;

import org.ethereum.core.Account;

import java.util.List;

public interface Wallet {
    List<byte[]> getAccountAddresses();

    String[] getAccountAddressesAsHex();

    byte[] addAccount();

    byte[] addAccount(String passphrase);

    Account getAccount(byte[] address);

    Account getAccount(byte[] address, String passphrase);

    boolean unlockAccount(byte[] address, String passphrase, long duration);

    boolean unlockAccount(byte[] address, String passphrase);

    boolean lockAccount(byte[] address);

    byte[] addAccountWithSeed(String seed);

    byte[] addAccountWithPrivateKey(byte[] privateKeyBytes);

    byte[] addAccountWithPrivateKey(byte[] privateKeyBytes, String passphrase);
}
