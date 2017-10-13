package co.rsk.rpc.modules;

import co.rsk.core.Wallet;
import org.ethereum.core.Account;
import org.ethereum.core.Transaction;
import org.ethereum.facade.Ethereum;
import org.ethereum.rpc.TypeConverter;
import org.ethereum.rpc.Web3;
import org.ethereum.rpc.exception.JsonRpcInvalidParamException;
import org.ethereum.vm.GasCost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.util.Arrays;

public class PersonalModule {

    private static final Logger LOGGER = LoggerFactory.getLogger("web3");

    private final Ethereum eth;
    private final Wallet wallet;

    public PersonalModule(Ethereum eth, Wallet wallet) {
        this.eth = eth;
        this.wallet = wallet;
    }

    public String newAccountWithSeed(String seed) {
        String s = null;
        try {
            byte[] address = this.wallet.addAccountWithSeed(seed);
            return s = TypeConverter.toJsonHex(address);
        } finally {
            LOGGER.debug("personal_newAccountWithSeed(*****): {}", s);
        }
    }

    public String newAccount(String passphrase) {
        String s = null;
        try {
            byte[] address = this.wallet.addAccount(passphrase);
            return s = TypeConverter.toJsonHex(address);
        } finally {
            LOGGER.debug("personal_newAccount(*****): {}", s);
        }
    }

    public String[] listAccounts() {
        String[] ret = null;
        try {
            return ret = wallet.getAccountAddresses().stream().map(TypeConverter::toJsonHex).toArray(String[]::new);
        } finally {
            LOGGER.debug("personal_listAccounts(): {}", Arrays.toString(ret));
        }
    }

    public String importRawKey(String key, String passphrase) {
        String s = null;
        try {
            byte[] address = this.wallet.addAccountWithPrivateKey(Hex.decode(key), passphrase);
            return s = TypeConverter.toJsonHex(address);
        } finally {
            LOGGER.debug("personal_importRawKey(*****): {}", s);
        }
    }

    public String sendTransaction(Web3.CallArguments args, String passphrase) throws Exception {
        String s = null;
        try {
            return s = sendTransaction(args, getAccount(args.from, passphrase));
        } finally {
            LOGGER.debug("eth_sendTransaction(" + args + "): " + s);
        }
    }

    public boolean unlockAccount(String address, String passphrase, String duration) {
        long dur = (long) 1000 * 60 * 30;
        if (duration != null && duration.length() > 0) {
            try {
                dur = convertFromJsonHexToLong(duration);
            } catch (Exception e) {
                throw new JsonRpcInvalidParamException("Can't parse duration param", e);
            }
        }

        return this.wallet.unlockAccount(TypeConverter.stringHexToByteArray(address), passphrase, dur);
    }

    public boolean lockAccount(String address) {
        return this.wallet.lockAccount(TypeConverter.stringHexToByteArray(address));
    }

    public String dumpRawKey(String address) throws Exception {
        String s = null;
        try {
            Account account = wallet.getAccount(TypeConverter.stringHexToByteArray(convertFromJsonHexToHex(address)));
            if (account == null)
                throw new Exception("Address private key is locked or could not be found in this node");

            return s = TypeConverter.toJsonHex(Hex.toHexString(account.getEcKey().getPrivKeyBytes()));
        } finally {
            LOGGER.debug("personal_dumpRawKey(*****): {}", s);
        }
    }

    private Account getAccount(String from, String passphrase) {
        return wallet.getAccount(TypeConverter.stringHexToByteArray(from), passphrase);
    }

    private String sendTransaction(Web3.CallArguments args, Account account) throws Exception {
        if (account == null)
            throw new Exception("From address private key could not be found in this node");

        String toAddress = args.to != null ? Hex.toHexString(TypeConverter.stringHexToByteArray(args.to)) : null;

        BigInteger accountNonce = args.nonce != null ? TypeConverter.stringNumberAsBigInt(args.nonce) : (eth.getWorldManager().getPendingState().getRepository().getNonce(account.getAddress()));
        BigInteger value = args.value != null ? TypeConverter.stringNumberAsBigInt(args.value) : BigInteger.ZERO;
        BigInteger gasPrice = args.gasPrice != null ? TypeConverter.stringNumberAsBigInt(args.gasPrice) : BigInteger.ZERO;
        BigInteger gasLimit = args.gas != null ? TypeConverter.stringNumberAsBigInt(args.gas) : BigInteger.valueOf(GasCost.TRANSACTION);

        if (args.data != null && args.data.startsWith("0x"))
            args.data = args.data.substring(2);

        Transaction tx = Transaction.create(toAddress, value, accountNonce, gasPrice, gasLimit, args.data);

        tx.sign(account.getEcKey().getPrivKeyBytes());

        eth.submitTransaction(tx);

        return TypeConverter.toJsonHex(tx.getHash());
    }

    private String convertFromJsonHexToHex(String x) throws Exception {
        if (!x.startsWith("0x"))
            throw new Exception("Incorrect hex syntax");
        return x.substring(2);
    }

    private long convertFromJsonHexToLong(String x) throws Exception {
        if (!x.startsWith("0x"))
            throw new Exception("Incorrect hex syntax");
        return Long.parseLong(x.substring(2), 16);
    }
}