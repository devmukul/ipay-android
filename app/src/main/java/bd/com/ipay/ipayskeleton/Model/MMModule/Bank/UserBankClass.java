package bd.com.ipay.ipayskeleton.Model.MMModule.Bank;

public class UserBankClass {
    public long bankAccountId;
    public long bankId;
    public long accountId;
    public int accountType;
    public String accountName;
    public String accountNumber;
    public Integer accountStatus;
    public Integer verificationStatus;

    public long getBankAccountId() {
        return bankAccountId;
    }

    public long getBankId() {
        return bankId;
    }

    public long getAccountId() {
        return accountId;
    }

    public int getAccountType() {
        return accountType;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public Integer getAccountStatus() {
        return accountStatus;
    }

    public Integer getVerificationStatus() {
        return verificationStatus;
    }
}
