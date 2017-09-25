package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;

import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.*;

public class ProfileCompletionStatusResponse {

    private String message;
    private List<CompletionStatus> completionStatusList;
    private List<String> tagList;
    private List<Integer> tagwiseScorePercentage;
    private int completionPercentage;
    private boolean completedMandetoryFields;

    private int basicInfoItemCount = 0;
    private int addressItemCount = 0;
    private int identificationItemCount = 0;
    private int addBankItemCount = 0;

    private double basicInfoCompletionSum = 0;
    private double addressCompletionSum = 0;
    private double identificationCompletionSum = 0;
    private double addBankCompletionSum = 0;

    private final List<PropertyDetails> basicInfoCompletionDetails = new ArrayList<>();
    private final List<PropertyDetails> addressCompletionDetails = new ArrayList<>();
    private final List<PropertyDetails> identificationCompletionDetails = new ArrayList<>();
    private final List<PropertyDetails> addBankCompletionDetails = new ArrayList<>();
    private final List<PropertyDetails> otherCompletionDetails = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public List<PropertyDetails> getBasicInfoCompletionDetails() {
        return basicInfoCompletionDetails;
    }

    public List<PropertyDetails> getAddressCompletionDetails() {
        return addressCompletionDetails;
    }

    public List<PropertyDetails> getIdentificationCompletionDetails() {
        return identificationCompletionDetails;
    }

    public List<PropertyDetails> getAddBankCompletionDetails() {
        return addBankCompletionDetails;
    }

    public List<PropertyDetails> getOtherCompletionDetails() {
        return otherCompletionDetails;
    }

    public List<CompletionStatus> getCompletionStatusList() {
        return completionStatusList;
    }

    public int getCompletionPercentage() {
//        double totalCompletionSum = basicInfoCompletionSum + addressCompletionSum + identificationCompletionSum + addBankCompletionSum;
//        double totalItemCount = basicInfoItemCount + addressItemCount + identificationItemCount + addBankItemCount;
//        return (int) Math.round(totalCompletionSum / totalItemCount);
        return completionPercentage;
    }

    public int getBasicInfoCompletionPercentage() {
        return (int) Math.round(basicInfoCompletionSum / basicInfoItemCount);
    }

    public int getAddressCompletionPercentage() {
        return (int) Math.round(addressCompletionSum / addressItemCount);
    }

    public int getIdentificationCompletionPercentage() {
        return (int) Math.round(identificationCompletionSum / identificationItemCount);
    }

    public int getAddBankCompletionPercentage() {
        return (int) Math.round(addBankCompletionSum / addBankItemCount);
    }

    private double getPropertyCompletionPercentage(int threshold, int value) {
        if (value >= threshold)
            return 100;
        else
            return (double) value / threshold * 100;
    }

    public boolean isCompletedMandetoryFields() {
        return completedMandetoryFields;
    }

    public void analyzeProfileCompletionData() {

        // Iterate the completionStatusList
        for (CompletionStatus mCompletionStatus : completionStatusList) {

            PropertyDetails propertyDetails = new PropertyDetails(mCompletionStatus.getValue(),
                    mCompletionStatus.getThreshold(), mCompletionStatus.getTag(), mCompletionStatus.getProperty());
            double propertyCompletionPercentage = getPropertyCompletionPercentage(mCompletionStatus.getThreshold(), mCompletionStatus.getValue());

            if (mCompletionStatus.getTag() == TAG_POSITION_BASIC_INFO) {

                basicInfoItemCount++;
                basicInfoCompletionSum = basicInfoCompletionSum + propertyCompletionPercentage;

                if (propertyDetails.getPropertyTitle() != null)
                    basicInfoCompletionDetails.add(propertyDetails);

            } else if (mCompletionStatus.getTag() == TAG_POSITION_BUSINESS_ADDRESS) {

                addressItemCount++;
                addressCompletionSum = addressCompletionSum + propertyCompletionPercentage;

                if (propertyDetails.getPropertyTitle() != null)
                    addressCompletionDetails.add(propertyDetails);

            } else if (mCompletionStatus.getTag() == TAG_POSITION_BUSINESS_DOCUMENTS) {
                identificationItemCount++;
                identificationCompletionSum = identificationCompletionSum + propertyCompletionPercentage;

                if (propertyDetails.getPropertyTitle() != null)
                    identificationCompletionDetails.add(propertyDetails);


            } else if (mCompletionStatus.getTag() == TAG_POSITION_BUSINESS_INFO) {

                basicInfoItemCount++;
                basicInfoCompletionSum = basicInfoCompletionSum + propertyCompletionPercentage;

                if (propertyDetails.getPropertyTitle() != null)
                    basicInfoCompletionDetails.add(propertyDetails);


            } else if (mCompletionStatus.getTag() == TAG_POSITION_PERSONAL_ADDRESS  ) {

                addressItemCount++;
                addressCompletionSum = addressCompletionSum + propertyCompletionPercentage;

                if (propertyDetails.getPropertyTitle() != null)
                    addressCompletionDetails.add(propertyDetails);

            } else if (mCompletionStatus.getTag() == TAG_POSITION_IDENTIFICATION) {

                identificationItemCount++;
                identificationCompletionSum = identificationCompletionSum + propertyCompletionPercentage;

                if (propertyDetails.getPropertyTitle() != null)
                    identificationCompletionDetails.add(propertyDetails);

            } else if (mCompletionStatus.getTag() == TAG_POSITION_ADD_BANK) {

                addBankItemCount++;
                addBankCompletionSum = addBankCompletionSum + propertyCompletionPercentage;

                if (propertyDetails.getPropertyTitle() != null)
                    addBankCompletionDetails.add(propertyDetails);
            }  else {
                if (propertyDetails.getPropertyTitle() != null)
                    otherCompletionDetails.add(propertyDetails);
            }
        }
    }

    public class PropertyDetails implements Comparable<PropertyDetails>{
        private final String propertyName;
        private final int value;
        private final int threshold;
        private final int tag;

        public PropertyDetails(int value, int threshold, int tag, String propertyName) {
            this.value = value;
            this.threshold = threshold;
            this.tag = tag;
            this.propertyName = propertyName;
        }

        public boolean isCompleted() {
            return value >= threshold;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public String getPropertyTitle() {
            if (propertyName.equals(PERSONAL_ADDRESS) && ProfileInfoCacheManager.isBusinessAccount()) return "Add Business DBContactNode's Address";
            else if (propertyName.equals(BASIC_PROFILE) && ProfileInfoCacheManager.isBusinessAccount()) return "Complete Business DBContactNode Information";
            return PROPERTY_NAME_TO_TITLE_MAP.get(propertyName);
        }

        public int getValue() {
            return value;
        }

        public int getThreshold() {
            return threshold;
        }

        public int getTag() {
            return tag;
        }

        public Integer getPropertyIcon() {
            return PROPERTY_NAME_TO_ICON_MAP.get(propertyName);
        }

        public String getActionName() {
            return PROPERTY_NAME_TO_ACTION_NAME_MAP.get(propertyName);
        }

        /**
         * Keep the incomplete property first
         */
        @Override
        public int compareTo(PropertyDetails another) {
            if (!this.isCompleted())
                return -1;
            if (!another.isCompleted())
                return 1;
            return 0;
        }
    }
}
