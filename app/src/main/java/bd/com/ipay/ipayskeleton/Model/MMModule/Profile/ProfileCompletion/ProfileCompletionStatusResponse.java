package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.ProfileCompletion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static bd.com.ipay.ipayskeleton.Model.MMModule.Profile.ProfileCompletion.PropertyConstants.*;

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

    private double basicInfoCompletionSum = 0;
    private double addressCompletionSum = 0;
    private double identificationCompletionSum = 0;
    private double linkBankCompletionSum = 0;

    private Map<String, PropertyDetails> basicInfoCompletionDetails = new TreeMap<>();
    private Map<String, PropertyDetails> addressCompletionDetails = new TreeMap<>();
    private Map<String, PropertyDetails> identificationCompletionDetails = new TreeMap<>();
    private Map<String, PropertyDetails> linkBankCompletionDetails = new TreeMap<>();

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
        return (int) Math.round(basicInfoCompletionSum / basicInfoItemCount);
    }

    public int getAddressCompletionPercentage() {
        return (int) Math.round(addressCompletionSum / addressItemCount);
    }

    public int getIdentificationCompletionPercentage() {
        return (int) Math.round(identificationCompletionSum / identificationItemCount);
    }

    public int getLinkBankCompletionPercentage() {
        return (int) Math.round(linkBankCompletionSum / linkBankItemCount);
    }

    public double getPropertyCompletionPercentage(int threshold, int value) {
        if (threshold >= value)
            return 100;
        else
            return (double) value / threshold * 100;
    }

    public void analyzeProfileCompletionData() {

        // Iterate the completionStatusList
        for (CompletionStatus mCompletionStatus : completionStatusList) {

            PropertyDetails propertyDetails = new PropertyDetails(mCompletionStatus.getValue(),
                    mCompletionStatus.getThreshold(), mCompletionStatus.getTag(), mCompletionStatus.getProperty());

            if (mCompletionStatus.getTag() == TAG_POSITION_BASIC_INFO) {

                basicInfoItemCount++;
                String propertyName = mCompletionStatus.getProperty();
                double propertyCompletionPercentage = getPropertyCompletionPercentage(mCompletionStatus.getThreshold(), mCompletionStatus.getValue());

                basicInfoCompletionSum = basicInfoCompletionSum + propertyCompletionPercentage;

                if (propertyDetails.propertyTitle != null)
                    basicInfoCompletionDetails.put(propertyDetails.getPropertyName(), propertyDetails);

            } else if (mCompletionStatus.getTag() == TAG_POSITION_ADDRESS) {

                addressItemCount++;
                String propertyName = mCompletionStatus.getProperty();
                double propertyCompletionPercentage = getPropertyCompletionPercentage(mCompletionStatus.getThreshold(), mCompletionStatus.getValue());

                addressCompletionSum = addressCompletionSum + propertyCompletionPercentage;

                if (propertyDetails.propertyTitle != null)
                    addressCompletionDetails.put(propertyDetails.getPropertyName(), propertyDetails);

            } else if (mCompletionStatus.getTag() == TAG_POSITION_IDENTIFICATION) {

                identificationItemCount++;
                String propertyName = mCompletionStatus.getProperty();
                double propertyCompletionPercentage = getPropertyCompletionPercentage(mCompletionStatus.getThreshold(), mCompletionStatus.getValue());

                identificationCompletionSum = identificationCompletionSum + propertyCompletionPercentage;

                if (propertyDetails.propertyTitle != null)
                    identificationCompletionDetails.put(propertyDetails.getPropertyName(), propertyDetails);

            } else if (mCompletionStatus.getTag() == TAG_POSITION_LINK_BANK) {

                linkBankItemCount++;
                String propertyName = mCompletionStatus.getProperty();
                double propertyCompletionPercentage = getPropertyCompletionPercentage(mCompletionStatus.getThreshold(), mCompletionStatus.getValue());

                linkBankCompletionSum = linkBankCompletionSum + propertyCompletionPercentage;

                if (propertyDetails.propertyTitle != null)
                    linkBankCompletionDetails.put(propertyDetails.getPropertyName(), propertyDetails);
            }
        }
    }

    public static class PropertyDetails implements Comparable<PropertyDetails>{
        private String propertyName;
        private String propertyTitle;
        private int value;
        private int threshold;
        private int tag;

        public PropertyDetails(int value, int threshold, int tag, String propertyName) {
            this.value = value;
            this.threshold = threshold;
            this.tag = tag;
            this.propertyName = propertyName;
            this.propertyTitle = PROPERTY_NAME_TO_TITLE_MAP.get(propertyName);
        }

        public boolean isCompleted() {
            return value >= threshold;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public String getPropertyTitle() {
            return propertyTitle;
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
