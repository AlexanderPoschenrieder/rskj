package co.rsk.rpc.modules;

import co.rsk.core.Wallet;
import org.ethereum.core.Account;
import org.ethereum.core.PendingState;
import org.ethereum.core.Transaction;
import org.ethereum.facade.Ethereum;
import org.ethereum.rpc.TypeConverter;
import org.ethereum.rpc.Web3;
import org.ethereum.rpc.exception.JsonRpcInvalidParamException;
import org.ethereum.vm.GasCost;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.util.Arrays;

import static org.ethereum.rpc.TypeConverter.stringHexToByteArray;

public class EthModuleWithWallet extends EthModule {
    private final Wallet wallet;

    public EthModuleWithWallet(Ethereum eth, Wallet wallet) {
        super(eth);
        this.wallet = wallet;
    }

    @Override
    public String sendTransaction(Web3.CallArguments args) {
        Account account = this.getAccount(args.from);
        String s = null;
        try {
            if (account == null)
                throw new JsonRpcInvalidParamException("From address private key could not be found in this node");

            String toAddress = args.to != null ? Hex.toHexString(stringHexToByteArray(args.to)) : null;

            BigInteger value = args.value != null ? TypeConverter.stringNumberAsBigInt(args.value) : BigInteger.ZERO;
            BigInteger gasPrice = args.gasPrice != null ? TypeConverter.stringNumberAsBigInt(args.gasPrice) : BigInteger.ZERO;
            BigInteger gasLimit = args.gas != null ? TypeConverter.stringNumberAsBigInt(args.gas) : BigInteger.valueOf(GasCost.TRANSACTION_DEFAULT);

            if (args.data != null && args.data.startsWith("0x"))
                args.data = args.data.substring(2);

            // TODO inject PendingState through constructor if necessary
            PendingState pendingState = eth.getWorldManager().getPendingState();
            synchronized (pendingState) {
                BigInteger accountNonce = args.nonce != null ? TypeConverter.stringNumberAsBigInt(args.nonce) : (pendingState.getRepository().getNonce(account.getAddress()));
                Transaction tx = Transaction.create(toAddress, value, accountNonce, gasPrice, gasLimit, args.data);
                tx.sign(account.getEcKey().getPrivKeyBytes());
                eth.submitTransaction(tx.toImmutableTransaction());
                s = TypeConverter.toJsonHex(tx.getHash());
            }
            return s;
        } finally {
            LOGGER.debug("eth_sendTransaction({}): {}", args, s);
        }
    }

    @Override
    public String[] accounts() {
        String[] s = null;
        try {
            return s = wallet.getAccountAddressesAsHex();
        } finally {
            LOGGER.debug("eth_accounts(): " + Arrays.toString(s));
        }
    }

    private Account getAccount(String address) {
        return this.wallet.getAccount(stringHexToByteArray(address));
    }
}