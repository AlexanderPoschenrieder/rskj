package co.rsk.rpc.modules;

import co.rsk.config.RskSystemProperties;
import org.ethereum.rpc.Web3;

public interface PersonalModule {
    String dumpRawKey(String address) throws Exception;

    String importRawKey(String key, String passphrase);

    void init(RskSystemProperties properties);

    String[] listAccounts();

    boolean lockAccount(String address);

    String newAccountWithSeed(String seed);

    String newAccount(String passphrase);

    String sendTransaction(Web3.CallArguments args, String passphrase) throws Exception;

    boolean unlockAccount(String address, String passphrase, String duration);
}
