package poc.jdbcservice;

public class CallParameters {

    private final String accountNumber;

    public CallParameters(String customerId) {
        this.accountNumber = customerId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    @Override
    public String toString() {
        return "CallParameters{" +
                "accountNumber=" + accountNumber +
                '}';
    }
}