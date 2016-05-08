package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.ProfileCompletion;

import java.util.HashMap;
import java.util.List;

public class ProfileCompletionStatusResponse {

    private String message;
    private List<CompletionStatus> completionStatusList;
    private List<String> tagList;
    private int completionPercentage;

    private int basicInfoCompletionPercentage;
    private int addressCompletionPercentage;
    private int identificationCompletionPercentage;
    private int linkBankCompletionPercentage;

    private int basicInfoItemCount = 0;
    private int addressItemCount = 0;
    private int identificationItemCount = 0;
    private int linkBankItemCount = 0;

    private int basicInfoCompletionSum = 0;
    private int addressCompletionSum = 0;
    private int identificationCompletionSum = 0;
    private int linkBankCompletionSum = 0;

    private HashMap<String, Integer> propertiesOfBasicInfo = new HashMap<String, Integer>();
    private HashMap<String, Integer> propertiesOfAddress = new HashMap<String, Integer>();
    private HashMap<String, Integer> propertiesOfIdentification = new HashMap<String, Integer>();
    private HashMap<String, Integer> propertiesOfLinkBank = new HashMap<String, Integer>();

    public ProfileCompletionStatusResponse() {
    }

    private void setPropertyOfBasicInfo(String propertyName, int propertyCompletionPercentage) {
        propertiesOfBasicInfo.put(propertyName, propertyCompletionPercentage);
    }

    private void setPropertyOfAddress(String propertyName, int propertyCompletionPercentage) {
        propertiesOfAddress.put(propertyName, propertyCompletionPercentage);
    }

    private void setPropertyOfIdentification(String propertyName, int propertyCompletionPercentage) {
        propertiesOfIdentification.put(propertyName, propertyCompletionPercentage);
    }

    private void setPropertyOfLinkBank(String propertyName, int propertyCompletionPercentage) {
        propertiesOfLinkBank.put(propertyName, propertyCompletionPercentage);
    }

    public String getMessage() {
        return message;
    }

    public List<CompletionStatus> getCompletionStatusList() {
        return completionStatusList;
    }

    public int getCompletionPercentage() {
        return completionPercentage;
    }

    public int getBasicInfoCompletionPercentage() {
        return (int) (basicInfoCompletionSum / basicInfoItemCount);
    }

    public int getAddressCompletionPercentage() {
        return (int) (addressCompletionSum / addressItemCount);
    }

    public int getIdentificationCompletionPercentage() {
        return (int) (identificationCompletionSum / identificationItemCount);
    }

    public int getLinkBankCompletionPercentage() {
        return (int) (linkBankCompletionSum / linkBankItemCount);
    }

    public HashMap<String, Integer> getBasicInfoProperties() {
        return propertiesOfBasicInfo;
    }

    public HashMap<String, Integer> getAddressProperties() {
        return propertiesOfAddress;
    }

    public HashMap<String, Integer> getIdentificationProperties() {
        return propertiesOfIdentification;
    }

    public HashMap<String, Integer> getLinkBankProperties() {
        return propertiesOfLinkBank;
    }

    public int getPropertyCompletionPercentage(int threshold, int value) {
        if (threshold > value)
            return (int) (value / threshold) * 100;
        else return 100;
    }

    public void analyzeProfileCompletionData() {

        // Iterate the completionStatusList
        for (CompletionStatus mCompletionStatus : completionStatusList) {

            if (mCompletionStatus.getTag() == PropertyConstants.TAG_POSITION_BASIC_INFO) {

                basicInfoItemCount++;
                String propertyName = mCompletionStatus.getProperty();
                int propertyCompletionPercentage = getPropertyCompletionPercentage(mCompletionStatus.getThreshold(), mCompletionStatus.getValue());

                basicInfoCompletionSum = basicInfoCompletionSum + propertyCompletionPercentage;
                setPropertyOfBasicInfo(propertyName, propertyCompletionPercentage);

            } else if (mCompletionStatus.getTag() == PropertyConstants.TAG_POSITION_ADDRESS) {

                addressItemCount++;
                String propertyName = mCompletionStatus.getProperty();
                int propertyCompletionPercentage = getPropertyCompletionPercentage(mCompletionStatus.getThreshold(), mCompletionStatus.getValue());

                addressCompletionSum = addressCompletionSum + propertyCompletionPercentage;
                setPropertyOfAddress(propertyName, propertyCompletionPercentage);

            } else if (mCompletionStatus.getTag() == PropertyConstants.TAG_POSITION_IDENTIFICATION) {

                identificationItemCount++;
                String propertyName = mCompletionStatus.getProperty();
                int propertyCompletionPercentage = getPropertyCompletionPercentage(mCompletionStatus.getThreshold(), mCompletionStatus.getValue());

                identificationCompletionSum = identificationCompletionSum + propertyCompletionPercentage;
                setPropertyOfIdentification(propertyName, propertyCompletionPercentage);

            } else if (mCompletionStatus.getTag() == PropertyConstants.TAG_POSITION_LINK_BANK) {

                linkBankItemCount++;
                String propertyName = mCompletionStatus.getProperty();
                int propertyCompletionPercentage = getPropertyCompletionPercentage(mCompletionStatus.getThreshold(), mCompletionStatus.getValue());

                linkBankCompletionSum = linkBankCompletionSum + propertyCompletionPercentage;
                setPropertyOfLinkBank(propertyName, propertyCompletionPercentage);

            }
        }
    }
}
