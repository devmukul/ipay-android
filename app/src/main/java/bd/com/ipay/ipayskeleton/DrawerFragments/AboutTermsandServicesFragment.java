package bd.com.ipay.ipayskeleton.DrawerFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import bd.com.ipay.ipayskeleton.R;

public class AboutTermsandServicesFragment extends Fragment {

    private TextView mTermsView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_about_terms_services, container, false);

        getActivity().setTitle(R.string.terms_of_service);

        mTermsView = (TextView) v.findViewById(R.id.terms_of_service);

        mTermsView.setText(Html.fromHtml(" <div class=\"page-container\">\n" +
                "        <div class=\"page-content-wrapper\">\n" +
                "            <div class=\"page-content\">\n" +
                "                <div class=\"container\">\n" +
                "                    <div class=\"page-content-inner\">\n" +
                "                        <div class=\"row portlet light\">\n" +
                "                            <div class=\"portlet-body\">\n" +
                "                                <div class=\"col-md-10 col-md-offset-1\">\n" +
                "                                    <h2><strong>Terms of Service for using iPay</strong></h2>\n" +
                "\n" +
                "                                    <p class=\"lead\">YOU AGREE TO ACCEPT THIS TERMS OF SERVICE WHICH WILL BE APPLICABLE TO ALL USERS FROM MARCH 09, 2016</p>\n" +
                "\n" +
                "                                    <p>Welcome to iPay!\n" +
                "                                    </p>\n" +
                "\n" +
                "                                    <p>This Agreement is a contract between you and iPay Systems Ltd. (a.k.a. iPay), a Bangladeshi company, that governs your use of all iPay Services. You must agree with all of the terms and conditions of this Agreement\n" +
                "                                        including the Privacy Policy and other policies of iPay Systems Limited. You should read all of these terms carefully before accepting the agreement.</p>\n" +
                "\n" +
                "                                    <p>THIS AGREEMENT AND OTHER POLICIES OF IPAY MAY BE AMENDED AT ANY TIME BY POSTING A REVISED VERSION ON OUR WEBSITE. THE REVISED VERSION WILL BE EFFECTIVE AT THE TIME WE POST IT.</p>\n" +
                "\n" +
                "                                    <p>\n" +
                "                                        <strong>Your account may be suspended, closed or your access to iPay services and/or\n" +
                "                                        funds may be limited if you breach this agreement or any other policies you\n" +
                "                                        agreed with iPay Systems Ltd. It is your sole responsibility to understand and\n" +
                "                                        comply\n" +
                "                                        with any and all rules, regulation and laws of the jurisdictions applicable to\n" +
                "                                        you in connection with your use of iPay Services. You are committed to follow\n" +
                "                                        the Rules and Regulations of the Govt. of Peoples Republic of Bangladesh,\n" +
                "                                        Bangladesh Bank and\n" +
                "                                        other related Regulators when using services provided by iPay Systems Ltd. You\n" +
                "                                        will be accountable for any unlawful use of your iPay Account.</strong>\n" +
                "                                    </p>\n" +
                "\n" +
                "                                    <br>\n" +
                "                                    <h4><strong>1. Services and Eligibility.</strong></h4>\n" +
                "                                    <h5>\n" +
                "                                    <strong>1.1. iPay Services.</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>iPay Systems Ltd. is a complete payment solution of Bangladesh and beyond. iPay is a secure payment ecosystem that you may choose for your daily transactions. The transactions in iPay can be performed from mobile devices\n" +
                "                                        or personal computers connected to the internet. Using this system, you will be able to exchange money with other iPay members and make payments for purchases, utilities, and other services in a cashless form in\n" +
                "                                        compliance with local laws and regulations.</p>\n" +
                "\n" +
                "                                    <p>iPay Account holders have the sole responsibility of their account including but not limited to the products or services that they sell or buy using the iPay platform. You will use iPay with integrity when making or\n" +
                "                                        realizing any payment through iPay platform, iPay will not be liable for your transactions.</p>\n" +
                "\n" +
                "                                    <h5>\n" +
                "                                    <strong>1.2. Age Eligibility.</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>Individuals aged 18 years or older will be eligible for using iPay Services.</p>\n" +
                "\n" +
                "                                    <h5>\n" +
                "                                    <strong>1.3. Identification.</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>You must provide correct and updated information to iPay Systems Ltd for opening and maintaining your account. You need to upload a copy of your valid National ID/passport, Tax Identification Number (TIN) at the time\n" +
                "                                        of opening your iPay Account. \"Identification\" profile of your account in the Account Settings option should be updated with valid identification documents, address of residence and other required information as\n" +
                "                                        and when required.\n" +
                "                                    </p>\n" +
                "\n" +
                "                                    <dl style=\"margin-left: 35px;\">\n" +
                "                                        <dt>\n" +
                "                                        <strong>a. Your contact information</strong>\n" +
                "                                    </dt>\n" +
                "                                        <dd>\n" +
                "                                            <p>You should keep your mobile number and primary email address updated at your own responsibility for receiving electronic communications from iPay. iPay will consider to have provided any information to you effectively\n" +
                "                                                when we send email to your primary email address given in your iPay profile. You must ensure that the mobile number and/ or email address is not outdated, incorrect or blocked by your service provider. If\n" +
                "                                                your mobile number and/ or email become invalid and communications are returned to iPay, then we may consider your account as inactive and block any transaction as long as we are not provided with a valid\n" +
                "                                                mobile number and/ or email address.</p>\n" +
                "                                        </dd>\n" +
                "                                        <dt>\n" +
                "                                        <strong>b. Identity Verification</strong>\n" +
                "                                    </dt>\n" +
                "                                        <dd>\n" +
                "                                            <p>iPay may validate your identity and may take the assistance of third parties to make inquiries about the information you provide to iPay Systems Ltd. For the process of verifying information, you may need to\n" +
                "                                                provide further information or documentation, including but not limited to TIN/NID, or follow directed procedures to confirm ownership of your email address, bank account, etc. These information may be requested\n" +
                "                                                through third party if required.</p>\n" +
                "                                        </dd>\n" +
                "                                    </dl>\n" +
                "\n" +
                "                                    <h5>\n" +
                "                                    <strong>1.4. Ownership.</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>You must be the owner of the Account, and conduct business only on behalf of yourself only.\n" +
                "                                    </p>\n" +
                "                                    <br>\n" +
                "                                    <h4><strong>2. Financial Transaction.\n" +
                "                                </strong></h4>\n" +
                "                                    <h5>\n" +
                "                                    <strong>2.1. Transaction Limits.</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>We may, at our discretion, impose limits on the amount of money you can send, request or pay through the iPay as well as the amount of money you can add or withdraw from your iPay account. We may increase your transaction\n" +
                "                                        limits in tiers according to the extent your account is verified and the activity of your iPay account.</p>\n" +
                "\n" +
                "                                    <h5>\n" +
                "                                    <strong>2.2. Fees.</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>When you use iPay financial services, applicable fees will be charged from your account as and when applicable. You must have enough balance in your account for processing the transaction along with applicable fees\n" +
                "                                        and charges.</p>\n" +
                "\n" +
                "                                    <br>\n" +
                "                                    <h4><strong>3. Receiving Payment</strong></h4>\n" +
                "                                    <h5>\n" +
                "                                    <strong>3.1 No Extra Charges.</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>You agree that you will not include any extra charges for accepting iPay as a payment method.\n" +
                "                                    </p>\n" +
                "\n" +
                "                                    <h5>\n" +
                "                                    <strong>3.2 Personal Payments for Sales.</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>If you are selling goods or services, you may not ask the buyer to send you a Personal Payment as 'Send Money' for the purchased item.</p>\n" +
                "                                    <br>\n" +
                "                                    <h4><strong>4. Account Balances.</strong></h4>\n" +
                "\n" +
                "                                    <p>The balance you hold in your iPay account will be maintained with the highest level of integrity. We will keep your balance in a pooled account along with other iPay members’ balance, this balance will not be used for\n" +
                "                                        corporate or any operational expenses. You agree to maintain balance in your iPay account without any interest. iPay may receive interest on amounts that iPay holds on your behalf. You agree to assign your rights\n" +
                "                                        to iPay for any interest derived from your funds.</p>\n" +
                "                                    <br>\n" +
                "                                    <h4><strong>5. Account Closure.</strong></h4>\n" +
                "                                    <h5>\n" +
                "                                    <strong>5.1 Account Closing Steps.</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>You may close your Account at any time by following the instructions in your iPay Account. You must withdraw your balance prior to closing your Account. Any bonus balance or offer associated with your account will no\n" +
                "                                        longer be available for you.\n" +
                "                                    </p>\n" +
                "\n" +
                "                                    <h5>\n" +
                "                                    <strong>5.2 Limitations on Closing Your Account.</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>If you close your Account while we are conducting an investigation, we may hold your funds to protect iPay, Affiliates or a third party against the risk of fees, fines, penalties and other liability. You will remain\n" +
                "                                        liable for all obligations related to your Account even after the Account is closed.</p>\n" +
                "                                    <br>\n" +
                "\n" +
                "                                    <h4><strong>6. Unauthorized Transaction or Errors</strong></h4>\n" +
                "                                    <h5>\n" +
                "                                    <strong>6.1 Safeguard for Unauthorized Transaction or Errors</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>\n" +
                "                                        In order to protect your account from unauthorized transaction, iPay will reimburse the amount equivalent to unauthorized transaction or error that are eligible for the safeguard according to the procedure described below.\n" +
                "                                    </p>\n" +
                "\n" +
                "                                    <p>\n" +
                "                                        Transactions are considered unauthorized when iPay services are used to transfer money from your account without your authorization and you are not benefitted from the transaction. If you give your login information to any other person and they carry\n" +
                "                                        out transactions from your account without your permission, these transactions will not be safeguarded by iPay Systems Ltd.\n" +
                "                                    </p>\n" +
                "                                    <h5>\n" +
                "                                    <strong>6.2 Eligibility for Safeguard</strong>\n" +
                "                                </h5>\n" +
                "                                    <dl style=\"margin-left: 35px;\">\n" +
                "\n" +
                "                                        <dd>\n" +
                "                                            <p>\n" +
                "                                                <strong>a.</strong> To be eligible for the safeguard you must notify us within 5 days after any unauthorized transaction appears in your activity feed or transaction history. You should access your account\n" +
                "                                                regularly and review account activity for ensuring that unauthorized transactions are not being carried out from your account. If you enable the option of receiving email notification to your email address\n" +
                "                                                for activities of iPay account, you’ll also receive email alerts for having overview of your account activities.\n" +
                "                                            </p>\n" +
                "                                        </dd>\n" +
                "                                        <strong>b.</strong> Notify iPay immediately if you find that\n" +
                "                                        <br>\n" +
                "                                        <dd>\n" +
                "                                            <ul>\n" +
                "                                                <li>\n" +
                "                                                    Your iPay Mobile Application Activated phone is stolen or lost\n" +
                "                                                </li>\n" +
                "                                                <li>\n" +
                "                                                    Your iPay password or PIN has been revealed to anyone else\n" +
                "                                                </li>\n" +
                "                                                <li>\n" +
                "                                                    Your account has been accessed by anyone unauthorized\n" +
                "                                                </li>\n" +
                "                                                <li>\n" +
                "                                                    There had been any unauthorized transaction or error in your account activity or transaction history\n" +
                "                                                </li>\n" +
                "                                            </ul>\n" +
                "                                        </dd>\n" +
                "                                        <dd>\n" +
                "                                            <p>\n" +
                "                                                <strong>c.</strong> Send your notification with your name, phone number, email address, amount of the unauthorized transaction or error and explanation of why you identified the transaction as unauthorized.\n" +
                "                                                You can notify us by:\n" +
                "                                            </p>\n" +
                "                                            <ul>\n" +
                "                                                <li>\n" +
                "                                                    Writing to iPay Systems Ltd., C4 (4th floor), 6 Gulshan Avenue, Gulshan - 1, Dhaka- 1212, Bangladesh\n" +
                "                                                </li>\n" +
                "                                                <li>\n" +
                "                                                    Sending email to\n" +
                "                                                    <a href=\"mailto:support@ipay.com.bd?subject=Unauthorized%20Transaction%20or%20Error\">support@ipay.com.bd</a> with subject line 'Unauthorized Transaction or Error' or\n" +
                "                                                </li>\n" +
                "                                                <li>\n" +
                "                                                    Calling to +8801749763943\n" +
                "                                                </li>\n" +
                "\n" +
                "                                            </ul>\n" +
                "                                            <p>\n" +
                "                                                If you send us the notification by calling to the mobile number, we may ask for written complaint, question or additional information required for processing\n" +
                "                                            </p>\n" +
                "                                        </dd>\n" +
                "                                    </dl>\n" +
                "                                    <h5>\n" +
                "                                    <strong>6.3 iPay Errors</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>In case we detect any error in iPay transaction, we will rectify it as soon as possible. If less fund is transferred to your account than the actual amount, iPay will add the fund to your account. Similarly, if an excess\n" +
                "                                        fund is transferred to your account, the excess fund will be removed from your iPay account.\n" +
                "                                    </p>\n" +
                "                                    <h5>\n" +
                "                                    <strong>6.4 Your Errors</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>\n" +
                "                                        If you send the incorrect amount of money or make a transaction with wrong person due to typing mistake, haste or any other reason, iPay will not protect you with the safeguard for unauthorized transaction or error. You will be solely responsible for\n" +
                "                                        your error and will have to settle with the other person without requesting any involvement of iPay Systems Ltd.\n" +
                "                                    </p>\n" +
                "                                    <h5>\n" +
                "                                    <strong>6.5 iPay actions against your notification</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>\n" +
                "                                        We will investigate the reported unauthorized transaction or error to determine its eligibility for safeguard. We will conduct an investigation and determine the eligibility of safeguard; iPay’s decision about the eligibility will be considered final.\n" +
                "                                    </p>\n" +
                "\n" +
                "                                    <p>\n" +
                "                                        We will conduct our investigation as soon as possible from the date of receiving the notification or if we detect any suspicious activity from your account. We will send you an email with the decision of our investigation and will reimburse you for the\n" +
                "                                        error or unauthorized transaction.\n" +
                "                                    </p>\n" +
                "                                    <br>\n" +
                "                                    <h4><strong>7. Acceptable Use Policy</strong></h4>\n" +
                "\n" +
                "                                    <p>\n" +
                "                                        You will not abuse the payment platform of iPay by conducting any of the prohibited activities, unlawful activities or transactions requiring approval before taking the permission of iPay Systems Ltd.\n" +
                "                                    </p>\n" +
                "                                    <br>\n" +
                "                                    <h5>\n" +
                "                                    <strong>7.1 Prohibited Activities</strong>\n" +
                "                                </h5>\n" +
                "                                    <dl style=\"margin-left: 35px;\">\n" +
                "                                        <dt>\n" +
                "                                        <strong>a. General Prohibitions</strong>\n" +
                "                                    </dt>\n" +
                "                                        <dd>\n" +
                "                                            <p>You agree to avoid the following activities while using iPay</p>\n" +
                "                                            <ol type=\"I\">\n" +
                "                                                <li>\n" +
                "                                                    Breaching this agreement, privacy policy or any other agreement you have entered into with us\n" +
                "                                                </li>\n" +
                "                                                <li>Violating any law or regulation</li>\n" +
                "                                                <li>Violating copyright, trademark, patent of iPay or any other third party\n" +
                "                                                </li>\n" +
                "                                                <li>Engaging in potential suspicious or fraudulent activity or transaction\n" +
                "                                                </li>\n" +
                "                                                <li>Refusing to confirm or provide your identity or any other information requested for any investigation\n" +
                "                                                </li>\n" +
                "                                                <li>Providing misleading or false information</li>\n" +
                "                                                <li>Using iPay services in a way that may result in disputes, complaints, fines, penalties or any other liability to iPay, our users, any third party or you\n" +
                "                                                </li>\n" +
                "                                                <li>Distributing or disclosing user’s information for marketing purpose without the explicit consent of the user\n" +
                "                                                </li>\n" +
                "                                                <li>Collecting unsolicited payments through iPay</li>\n" +
                "                                                <li>Using the system in a manner which imposes unreasonable or disproportionately large load on our infrastructure\n" +
                "                                                </li>\n" +
                "                                                <li>Facilitating any virus, worm or Trojan horses or any other malware that is damaging and interfering to our system or data\n" +
                "                                                </li>\n" +
                "                                                <li>Undertaking any action that might hamper the services that we receive from our payment processors, banks, internet provider or other suppliers\n" +
                "                                                </li>\n" +
                "                                                <li>Monitoring or copying our website using proxy, robot or manual process without prior written permission of iPay\n" +
                "                                                </li>\n" +
                "\n" +
                "                                            </ol>\n" +
                "                                        </dd>\n" +
                "                                        <dt>\n" +
                "                                        <strong>b. Restricted trade and transactions</strong>\n" +
                "                                    </dt>\n" +
                "                                        <dd>\n" +
                "                                            <p>When you buy or sell any product through iPay make sure that you are not making any transaction involving:</p>\n" +
                "                                            <ol type=\"I\">\n" +
                "                                                <li>ammunition, firearms, combat or tactical knife, weapons or related accessories\n" +
                "                                                </li>\n" +
                "                                                <li>narcotics, drugs and tobacco products</li>\n" +
                "                                                <li>antic or obscene items</li>\n" +
                "                                                <li>stolen or counterfeit goods</li>\n" +
                "                                                <li>sexually oriented material or services</li>\n" +
                "                                                <li>items promoting violence, hate or racial intolerance</li>\n" +
                "                                                <li>multi-level marketing program</li>\n" +
                "                                                <li>off-shore banking, lottery or annuity</li>\n" +
                "                                                <li>debt settlement service, credit transaction or insurance activities</li>\n" +
                "                                                <li>offering or receiving payments for bribery or corruption</li>\n" +
                "                                                <li>check cashing or currency exchange business</li>\n" +
                "                                                <li>payment collection on behalf of a merchant</li>\n" +
                "\n" +
                "                                            </ol>\n" +
                "                                        </dd>\n" +
                "                                    </dl>\n" +
                "                                    <br>\n" +
                "                                    <h4><strong>8. Generic Terms</strong></h4>\n" +
                "                                    <h5>\n" +
                "                                    <strong>8.1 Indemnification.</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>\n" +
                "                                        You agree to indemnify iPay, our affiliates, employees, directors, agents and suppliers from any demand, claim, fine or other liability incurred by third party arising out of the breach of this agreement. The foregoing parties will not be liable for any\n" +
                "                                        lost profit or consequential damage arising out of unlawful use of iPay services. The mentioned parties will only be liable for the actual amount of direct damage if it is caused by iPay error.\n" +
                "                                    </p>\n" +
                "                                    <h5>\n" +
                "                                    <strong>8.2 Availability and nature of service</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>\n" +
                "                                        iPay is a payment platform. It does not provide banking, fiduciary or trustee services. iPay is not liable for the services and products that are paid for using iPay Services. We can not guarantee that the buyer or seller you are dealing with is authorized\n" +
                "                                        or will complete the transaction.\n" +
                "                                    </p>\n" +
                "\n" +
                "                                    <p>\n" +
                "                                        Since iPay services are dependent on many factors outside our control and we may need to suspend the online availability for up gradation and the maintenance of the system, we can not provide any warranty about the amount of time needed to complete any\n" +
                "                                        transaction. iPay will make reasonable effort to run smooth and prompt functioning of the system within our limitation.\n" +
                "                                    </p>\n" +
                "                                    <h5>\n" +
                "                                    <strong>8.3 Intellectual Property</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>\n" +
                "                                        “ipay.com.bd”, “ipaytk.com”, “iPay” and all other logos, custom graphics, trademarks and URL related to iPay services may not be copied or used without written consent of iPay Systems Limited. Any technology created or derived from iPay services are the\n" +
                "                                        exclusive property of iPay and its licensors.\n" +
                "                                    </p>\n" +
                "                                    <h5>\n" +
                "                                    <strong>8.4 Securing your Password</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>\n" +
                "                                        You should maintain the security of your iPay account by controlling and making sure that all IDs, passwords, PINS or other codes that you use for accessing iPay services are secured and not revealed to any other person.\n" +
                "                                    </p>\n" +
                "                                    <h5>\n" +
                "                                    <strong>8.5 Communication with you over Phone</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>\n" +
                "                                        You agree to receive phone calls and messages from iPay Systems Limited including autodialer and prerecorded messages regarding your use of iPay services or verification purpose. The phone number that you provide to iPay must be active and stay in your\n" +
                "                                        possession as we may request information for verification purpose by contacting you over the phone. Your voice and conversation with iPay over the phone may be recorded for resolving complaints, detecting fraud\n" +
                "                                        and ensuring security.\n" +
                "                                    </p>\n" +
                "                                    <h5>\n" +
                "                                    <strong>8.6 Tax</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>\n" +
                "                                        It is your responsibility to determine the taxes applicable to your transactions in iPay and you should pay the taxes to appropriate authority at your own responsibility. iPay is not responsible for determining, collecting or reporting taxations arising\n" +
                "                                        from your transactions.\n" +
                "                                    </p>\n" +
                "                                    <h5>\n" +
                "                                    <strong>8.7 Agreement Survival, Amendment and Termination</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>\n" +
                "                                        f any section of the agreement is rendered invalid, it will be changed or removed while the remaining sections will stay valid. iPay Systems Limited holds the right of making any amendment/correction of any terms of services from time to time in future.\n" +
                "                                        These modified amendments will be effective just after being posted in the website.\n" +
                "                                    </p>\n" +
                "\n" +
                "                                    <p>\n" +
                "                                        iPay at its sole discretion, reserves the right to terminate this agreement or any of its service for any reason at any time by refunding all unrestricted funds in your iPay account.\n" +
                "                                    </p>\n" +
                "                                    <h5>\n" +
                "                                    <strong>8.8 Assignment of Rights.</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>\n" +
                "                                        You may not assign or transfer your rights and obligations that you have entered into by signing this agreement to anyone else without iPay’s written permission. iPay reserves the right to assign or transfer this agreement or any obligation or right under\n" +
                "                                        this agreement at any time.\n" +
                "                                    </p>\n" +
                "                                    <h5>\n" +
                "                                    <strong>8.9 Translation of this Agreement</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>\n" +
                "                                        If you read any translated version of this agreement, it had been translated solely for your convenience of understanding the agreement. If any discrepancy arises between the translated agreement and this English agreement, the English version of the\n" +
                "                                        agreement will be enforced.\n" +
                "                                    </p>\n" +
                "                                    <h5>\n" +
                "                                    <strong>8.10 Instructions given by you on your account</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>\n" +
                "                                        When you give any authenticated instruction (verbal or written) to be carried out on your account, iPay will carry out the instruction accordingly. iPay will not be liable for the consequence of the acting on those instructions as those were delivered\n" +
                "                                        by you.\n" +
                "                                    </p>\n" +
                "                                    <h5>\n" +
                "                                    <strong>8.11 Authorization</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>\n" +
                "                                        When you add your bank account to iPay account for carrying out transaction between your iPay account and bank account, you are authorizing iPay to debit or credit fund from your iPay account to iPay’s official account according to your instruction and\n" +
                "                                        terms in this agreement. You also authorize iPay to take necessary steps and request addition information and/or document from you or your bank for verifying the credibility of your bank account.\n" +
                "                                    </p>\n" +
                "                                    <br>\n" +
                "                                    <h4><strong>9. Buyer’s and Seller’s Protection</strong></h4>\n" +
                "                                    <h5>\n" +
                "                                    <strong>9.1 Buyer’s Protection</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>For being eligible for iPay buyer’s protection you must have had made the complete payment of your purchase to the seller and have iPay account of good standing. You agree not to mediate the dispute of purchase through\n" +
                "                                        any other mean if you want the safeguard of Buyer’s Protection from iPay. You may seek iPay Buyer’s Protection if\n" +
                "                                    </p>\n" +
                "                                    <ul>\n" +
                "                                        <li>you have not received the item that you have paid for</li>\n" +
                "                                        <li>you received the wrong number of items</li>\n" +
                "                                        <li>the item you have received is significantly not as described by the seller</li>\n" +
                "                                    </ul>\n" +
                "                                    <h5>\n" +
                "                                    <strong>9.2 Seller’s Protection</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>For being eligible for iPay Seller’s protection the item must be delivered to the buyer and you must provide iPay with proof of delivery of the item. If you are holding a business account in iPay, you agree to settle\n" +
                "                                        the legitimate claim or dispute filed for purchases from you under iPay Buyer’s protection.</p>\n" +
                "                                    <br>\n" +
                "                                    <h4><strong>10. Action for your liability to iPay</strong></h4>\n" +
                "\n" +
                "                                    <p>You are responsible for all claims, fees, fines or other liabilities incurred by iPay, third party or user caused due to your breach of this agreement. You agree to reimburse the affected party for any liability caused\n" +
                "                                        due to your action.</p>\n" +
                "\n" +
                "                                    <h5>\n" +
                "                                    <strong>10.1 Reimbursement of the liabilities</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>The amount that you owe to iPay against any liability will be immediately removed from your iPay account balance. If you do not have enough balance, the existing balance of your account will be removed and you’ll need\n" +
                "                                        to reimburse the remaining amount to iPay by adding money to you iPay account as soon as possible.</p>\n" +
                "                                    <h5>\n" +
                "                                    <strong>10.2 Actions against Prohibited Activities</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>If we believe that you have engaged any of the prohibited activity mentioned in section 7.1, we may take any or all of the following actions:</p>\n" +
                "                                    <ol type=\"a\">\n" +
                "                                        <li>Suspend, close or limit your access to your iPay account or specific iPay service.\n" +
                "                                        </li>\n" +
                "                                        <li>Obtain Tk. 10,000 or more for each prohibited transaction or activity that you perform in iPay Systems.\n" +
                "                                        </li>\n" +
                "                                        <li>Refuse to provide iPay services to you at the time of detection of prohibited activity or anytime in future.\n" +
                "                                        </li>\n" +
                "                                        <li>Place your funds or specific transactions on hold for suspicion of an illegal transaction, fraudulent activity, etc.\n" +
                "                                        </li>\n" +
                "                                    </ol>\n" +
                "                                    <h5>\n" +
                "                                    <strong>10.3 Holds or Reserves</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>iPay may place hold or reserve on your account balance for security of our users’ account and iPay System. We will provide notice for our action and you will have opportunity for requesting restoration of your access\n" +
                "                                        if we deem it appropriate.</p>\n" +
                "                                    <h5>\n" +
                "                                    <strong>10.4 Transaction Profile Consistency</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>iPay will monitor activities and transaction in your profile and set privilege classes and status for your account. The privilege classes and account status will have preset Transaction Profile for each of them. Your\n" +
                "                                        daily and periodic transaction should be consistent with the Transaction Profile for your account class and status\n" +
                "                                    </p>\n" +
                "                                    <br>\n" +
                "                                    <h4><strong>11. Dispute Settlement Guide</strong></h4>\n" +
                "                                    <h5>\n" +
                "                                    <strong>11.1 Inform iPay First</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>In case any dispute arises between you and iPay, you will inform iPay immediately. We will try to address your concern and provide you with a neutral and cost effective mean of settling the dispute quickly. You can\n" +
                "                                        inform us by:</p>\n" +
                "                                    <ul>\n" +
                "                                        <li>writing to iPay Systems Ltd., C4 (4th floor), 6 Gulshan Avenue, Gulshan - 1, Dhaka- 1212, Bangladesh\n" +
                "                                        </li>\n" +
                "                                        <li>sending email to\n" +
                "                                            <a href=\"mailto:info@ipay.com.bd?subject=Initial%20Dispute%20Notification\">info@ipay.com.bd</a> with subject line \"Initial Dispute Notification\" or\n" +
                "                                        </li>\n" +
                "                                        <li>calling to +8801749763943</li>\n" +
                "                                    </ul>\n" +
                "                                    <h5>\n" +
                "                                    <strong>11.2 Arbitration</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>In the event of controversy or claim, any dispute arising out of or in connection with this agreement if not solved by mutual understanding shall be settled by arbitration. The arbitration shall be conducted by one\n" +
                "                                        or three arbitrators, who have at least ten years of experience in the particular field is also experienced as an arbitrator. The arbitration should be held in Bangladesh and the existing law of Bangladesh will\n" +
                "                                        be applicable in the arbitration.</p>\n" +
                "                                    <h5>\n" +
                "                                    <strong>11.3 Law and Jurisdiction of Dispute</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>\n" +
                "                                        If either party is not satisfied by the dispute resolution through the arbitrator, the dispute and solution provided by the arbitrator can be extended to any court of Bangladesh after following the dispute settlement guideline of section 11.1 and 11.2.\n" +
                "                                        You agree to obey the instructions of the regulators which may be changed from time to time on Anti-Money Laundering Act-2012, Counter Terrorism Act-2009, Counter Terrorism Act (amended) -2012, Counter Terrorism\n" +
                "                                        Act (amended)-2013 and other such act/regulations which to be issued from time to time in future.\n" +
                "                                    </p>\n" +
                "                                    <h5>\n" +
                "                                    <strong>11.4 Litigation filed improperly</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>\n" +
                "                                        All disputes and claims you have against iPay should be resolved in accordance with section 11 of this agreement. Any cases filed by breaching this agreement will be considered improper and iPay reserves the right to recover fees and cost associated with\n" +
                "                                        the case and lawyer’s charge of minimum 10,000 Taka for improperly filed claims. You will be liable for the fees and charge if you fail to withdraw the claim promptly after receiving written notifications from iPay.\n" +
                "                                    </p>\n" +
                "                                    <h5>\n" +
                "                                    <strong>11.5 Communicating with you</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>\n" +
                "                                        iPay will communicate with you through your primary mobile number and/or email address, which you should keep updated at all time. It will be considered that you have received the electronic communication after 24 hours of posting it on our website or\n" +
                "                                        to your email address. Postal mail will be considered received within 5 business days of sending the mail.\n" +
                "                                    </p>\n" +
                "                                    <h5>\n" +
                "                                    <strong>11.6 Communicating with us</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>You may send notices to iPay by:\n" +
                "                                    </p>\n" +
                "                                    <ul>\n" +
                "                                        <li>writing to iPay Systems Ltd., C4 (4th floor), 6 Gulshan Avenue, Gulshan - 1, Dhaka- 1212, Bangladesh\n" +
                "                                        </li>\n" +
                "                                        <li>sending email to\n" +
                "                                            <a href=\"mailto:info@ipay.com.bd?subject=Dispute%20Processing%20For%20iPay\">info@ipay.com.bd</a> with subject line \"Dispute Processing For iPay\" or\n" +
                "                                        </li>\n" +
                "                                        <li>calling to +8801749763943</li>\n" +
                "                                    </ul>\n" +
                "                                    <h5>\n" +
                "                                    <strong>11.7 Insolvency</strong>\n" +
                "                                </h5>\n" +
                "\n" +
                "                                    <p>iPay will be entitled to recover all associated cost according to this agreement even in case you are under the provision of any bankruptcy or insolvency law.</p>\n" +
                "\n" +
                "                                </div>\n" +
                "                            </div>\n" +
                "                        </div>\n" +
                "\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </div>"));


        return v;
    }
}