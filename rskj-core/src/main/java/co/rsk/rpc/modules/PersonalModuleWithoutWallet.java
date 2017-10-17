package co.rsk.rpc.modules;

import co.rsk.config.RskSystemProperties;
import co.rsk.core.DisabledWalletException;
import org.ethereum.rpc.Web3;

public class PersonalModuleWithoutWallet implements PersonalModule {
    @Override
    public void init(RskSystemProperties properties) {
        // Init steps are only needed when using a wallet.
        // This method is called from Web3Impl even if the wallet is disabled,
        // so we don't throw here.
    }

    @Override
    public String newAccountWithSeed(String seed) {
        throw new DisabledWalletException();
    }

    @Override
    public String newAccount(String passphrase) {
        throw new DisabledWalletException();
    }

    @Override
    public String[] listAccounts() {
        throw new DisabledWalletException();
    }

    @Override
    public String importRawKey(String key, String passphrase) {
        throw new DisabledWalletException();
    }

    @Override
    public String sendTransaction(Web3.CallArguments args, String passphrase) {
        throw new DisabledWalletException();
    }

    @Override
    public boolean unlockAccount(String address, String passphrase, String duration) {
        throw new DisabledWalletException();
    }

    @Override
    public boolean lockAccount(String address) {
        throw new DisabledWalletException();
    }

    @Override
    public String dumpRawKey(String address) {
        throw new DisabledWalletException();
    }
}