package bd.com.ipay.ipayskeleton.DrawerFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.analytics.Tracker;

import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class AboutPrivacyFragment extends Fragment {

    private TextView mTermsView;
    private Tracker mTracker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTracker = Utilities.getTracker(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_privacy_policy) );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_about_terms_services, container, false);

        getActivity().setTitle(R.string.privacy_policy);

        mTermsView = (TextView) v.findViewById(R.id.terms_of_service);

        mTermsView.setText(Html.fromHtml(" \n" +
                "<div class=\"page-container\">\n" +
                "<div class=\"page-content-wrapper\">\n" +
                "<div class=\"page-content\">\n" +
                "<div class=\"container\">\n" +
                "<div class=\"page-content-inner\">\n" +
                "<div class=\"row portlet light\">\n" +
                "<div class=\"portlet-body\">\n" +
                "<div class=\"col-md-10 col-md-offset-1\">\n" +
                "<h2>\n" +
                "<strong>Privacy Policy</strong>\n" +
                "</h2>\n" +
                "<p class=\"lead\">This version of the Privacy Policy will be applicable from April 21, 2016.</p>\n" +
                "<p>Your consent to this Privacy Policy is exerted when you sign up for, access, or use the Services provided by iPay. You agree to allow us to use and disclose your personal information entered into our system according\n" +
                "to the description of this Privacy Policy.\n" +
                "</p>\n" +
                "<br>\n" +
                "<h4><strong>1. Overview</strong></h4>\n" +
                "<p>iPay Application may request you to provide information about yourself, including your National ID/ Passport, Tax Identification Number (TIN), and/or bank account details for operating the Services in iPay and for reducing\n" +
                "the risk of fraudulent activities. We process your information very seriously and use this information conforming to the terms of this Privacy Policy. The term \"information\" refers to any confidential and/or individually\n" +
                "identifiable information or other information related to users of iPay Services in this Privacy Policy.\n" +
                "</p>\n" +
                "<p>We will not rent or sell your information to third parties for their marketing purposes without your explicit consent. In case we need to convey your information to any third party for completing your requested iPay\n" +
                "service, we will share the required information under strict restriction. If you use any third party website via iPay or use third party website to access iPay services, you will need to review the privacy statements\n" +
                "of those websites. iPay will not be responsible for the information practice of the third parties.\n" +
                "\n" +
                "</p>\n" +
                "<p>\n" +
                "<strong>Changes to this Privacy Policy:</strong> All future changes to this Privacy Policy will be posted on our website and will be applicable after it had been posted. It will be considered that you have consented\n" +
                "to the updated Privacy Policy. Please check iPay website regularly for the latest version of the Privacy Policy. If you have any disagreement with the updated Privacy Policy, then you may close your account at any\n" +
                "time.\n" +
                "</p>\n" +
                "<br>\n" +
                "<h4><strong>2. Information</strong></h4>\n" +
                "<p>\n" +
                "<strong>2.1 Basic Information</strong>\n" +
                "<br>In order to sign up for iPay account or use iPay Services, you must provide your name, phone number, email address, photograph and other required identification information. For adding or withdrawing money to\n" +
                "your iPay account from your personal bank account, you must provide bank account information which will be verified by iPay. We may also request other verification documents before processing any transaction which\n" +
                "is not consistent with your regular transaction or is of a very high amount. You agree to cooperate iPay in these verification process by providing the required information.\n" +
                "<br> Public information about your business size, customer base and usual transactions might be collected from social media platform, other iPay account holders or other sources for accessing goodwill, trustworthiness\n" +
                "and solvency check of your account and business.\n" +
                "\n" +
                "</p>\n" +
                "<p>\n" +
                "<strong>2.2 Device Identification</strong>\n" +
                "<br> Device sign on data (device ID, geolocation, etc) will be used by iPay to provide smooth experience of iPay services. Internet address (IP address) and other identifying information about the computer or device\n" +
                "you use to access your iPay account will be tracked for helping us detect possible instances of unauthorized transactions.\n" +
                "</p>\n" +
                "<p>\n" +
                "<strong>2.3 Transaction Information</strong>\n" +
                "<br> For complying with anti-money laundering and counter terrorism obligations under the rules and regulations of the Govt. of Peoples Republic of Bangladesh, your commercial and/or personal identification information\n" +
                "shall be recorded if you send or receive transaction of high amount or frequency. If you send or receive high overall payment volumes through the iPay Services or are suspected of being engaged in any suspicious\n" +
                "transaction, we may conduct a background check on your business by gathering information about you and your business,its directors, shareholders and partners (if legally permitted). iPay, at its sole discretion,\n" +
                "reserves the right to periodically retrieve and review a business and/or consumer information and is authorized to close an account based on information obtained during these reviews.\n" +
                "</p>\n" +
                "<p>\n" +
                "<strong>\n" +
                "2.4 Web Catches and Website Traffic\n" +
                "</strong>\n" +
                "<br> For ensuring smooth and efficient iPay services, your IP address, browser, and time related information for accessing iPay website will be stored in our server; web address of sites that you routed you to or\n" +
                "from iPay website may also be received. Session cookies, web cookies, pixel tags, web beacons or other similar technologies will be used to place small text or image files in your computer for facilitating user\n" +
                "friendly performance of iPay application.\n" +
                "</p>\n" +
                "<p>\n" +
                "<strong>\n" +
                "2.5 Contacting Prospective Users\n" +
                "</strong>\n" +
                "<br> When an iPay user attempts to send iPay service request to users who have not signed up in iPay, iPay may retain the service request and information provided regarding the prospective user for contacting them\n" +
                "for marketing purpose. For example, a text message may be sent to their number notifying them about the iPay service you wanted to share with them.\n" +
                "</p>\n" +
                "<br>\n" +
                "<h4><strong>3. Use of Collected Information</strong></h4>\n" +
                "<p>\n" +
                "<strong>\n" +
                "3.1 Internal Use\n" +
                "</strong>\n" +
                "<br> Your information is collected, stored and processed in servers located in Bangladesh, which is used to provide you with a smooth, safe, efficient and customized experience of iPay application. The provided\n" +
                "information will be used to process your iPay transactions, troubleshoot problems, resolve disputes, provide customer support service and customize your iPay user experience.\n" +
                "</p>\n" +
                "<p>\n" +
                "<strong>\n" +
                "3.2 Communicating with You\n" +
                "</strong>\n" +
                "<br> We will also communicate with you using the information that you provided to complete processing of iPay services or verify any important information or actions like investigating suspicious or illegal transaction,\n" +
                "responding to customer service requests, complaints or claims. Promotional offers, service updates and other marketing information will also be provided to you through the contact information that you provided.\n" +
                "</p>\n" +
                "<p>\n" +
                "<strong>\n" +
                "3.3 Information Shared with Other iPay Users\n" +
                "</strong>\n" +
                "<br> Your basic information like name, phone number, email address, transaction amount and transaction time, etc will be shared with the other iPay User or prospective user to whom you send iPay service request.\n" +
                "The information shared may vary with the service you request and the type of iPay account you maintain; additional information about your business may be revealed if you are making transactions through a business\n" +
                "account. When payments for goods or purchases are made through iPay, Buyer’s and Seller’s address and payment details may be revealed for aiding in delivery and dispute resolution.\n" +
                "</p>\n" +
                "<p>\n" +
                "<strong>\n" +
                "3.4 Information Shared with Third Party\n" +
                "</strong>\n" +
                "<br> Several third party like the bank, mobile operator and other service providers will need access to specific information for completion of your requested iPay services. We will only disclose the required information\n" +
                "to third parties.\n" +
                "</p>\n" +
                "<p>\n" +
                "<strong>\n" +
                "3.5 Your Use of iPay Information\n" +
                "</strong>\n" +
                "<br> You will receive information of other iPay users when carrying out any transaction with them. You may only use this information for facilitating iPay service related communication, action or transaction; this\n" +
                "information will not be used for any other purpose without taking explicit consent of the iPay user by disclosing the purpose adequately.\n" +
                "</p>\n" +
                "<p>\n" +
                "<strong>\n" +
                "3.6 Surveys and Questionnaires\n" +
                "</strong>\n" +
                "<br> When you answer the optional questionnaire or survey, information provided in them may be used for improving iPay services, marketing and/or advertising purpose as described in the survey.\n" +
                "</p>\n" +
                "<p>\n" +
                "<strong>\n" +
                "3.7 Audit and Legal Compliance\n" +
                "</strong>\n" +
                "<br> Your information and transaction history may be viewed by the auditors for confirming the accuracy of our record. However, auditors will not have the right of using your personally identifiable information\n" +
                "for any other purpose. Information related to illegal or suspicious activity carried out from your account needs to be made available to law enforcing body, Bangladesh Bank or any other applicable jurisdiction when\n" +
                "requested.\n" +
                "</p>\n" +
                "<p>\n" +
                "<strong>\n" +
                "3.8 Mergers or Acquisition\n" +
                "</strong>\n" +
                "<br> If iPay merges with other company or is acquired by another company, the successor company will have access to your information maintained by iPay.\n" +
                "</p>\n" +
                "<br>\n" +
                "<h4><strong>4. Your Information is Secured</strong></h4>\n" +
                "<p>iPay maintains your information with the highest level of integrity. Our firewalls, data encryption and computer and network safeguards protect your information and balance around the clock. Only authorized employees\n" +
                "can access our office, network and files for completing the actions needed for smooth functioning of the system.\n" +
                "</p>\n" +
                "<p>You can always review and change your personal information from the account setting page of your iPay account. You can also close your account at any time if there is no investigation being conducted against it. After\n" +
                "you close your account, we will mark your account status as 'Closed' and keep your information in our database for future reference.\n" +
                "</p>\n" +
                "</div>\n" +
                "</div>\n" +
                "</div>\n" +
                "\n" +
                "</div>\n" +
                "</div>\n" +
                "</div>\n" +
                "</div>\n" +
                "</div>"));


        return v;
    }
}
