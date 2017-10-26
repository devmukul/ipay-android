package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Pay;

import java.util.ArrayList;
import java.util.HashMap;

import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class PayPropertyConstants {
    public static final String TOP_UP = "TopUp";
    public static final String MAKE_PAYMENT = "Make Payment";
    public static final String REQUEST_PAYMENT = "Request Payment";
    public static final String PAY_BY_QR_CODE = "Pay by QR Code";


    public static ArrayList<PayDashBoardIconProperty> getPayIconDataSet() {
        ArrayList<PayDashBoardIconProperty> dataList = new ArrayList<>();
        dataList.add(new PayDashBoardIconProperty(MAKE_PAYMENT, "http://national500apps.com/uploads/Android/Bangladesh%20ICTD%20Apps/DESCO%20Bill%20Check/DESCO.png", Constants.SERVICE_ID_MAKE_PAYMENT, ""));
        dataList.add(new PayDashBoardIconProperty(TOP_UP, "http://www.theindependentbd.com/assets/news_images/Grameen-Phone2.jpg", Constants.SERVICE_ID_TOP_UP, "017"));
        dataList.add(new PayDashBoardIconProperty(PAY_BY_QR_CODE, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT7r4rP14V19wOHBaO23dVMU9Sjo0JdsXANCYVfxqXTRSQINLZSBw", Constants.SERVICE_ID_MAKE_PAYMENT, ""));

        if (ProfileInfoCacheManager.isBusinessAccount())
            dataList.add(new PayDashBoardIconProperty(REQUEST_PAYMENT, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT7r4rP14V19wOHBaO23dVMU9Sjo0JdsXANCYVfxqXTRSQINLZSBw", Constants.SERVICE_ID_REQUEST_PAYMENT, ""));

        return dataList;
    }
}
