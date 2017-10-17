package co.rsk.rpc.modules;

import org.ethereum.facade.Ethereum;
import org.ethereum.rpc.Web3;
import org.ethereum.rpc.exception.JsonRpcInvalidParamException;

import java.util.Arrays;

public class EthModuleWithoutWallet extends EthModule {

    public EthModuleWithoutWallet(Ethereum eth) {
        super(eth);
    }

    @Override
    public String[] accounts() {
        String[] accounts = {};
        LOGGER.debug("eth_accounts(): {}", Arrays.toString(accounts));
        return accounts;
    }

    @Override
    public String sendTransaction(Web3.CallArguments args) {
        LOGGER.debug("eth_sendTransaction({}): {}", args, null);
        throw new JsonRpcInvalidParamException("Local wallet is disabled in this node");
    }
}