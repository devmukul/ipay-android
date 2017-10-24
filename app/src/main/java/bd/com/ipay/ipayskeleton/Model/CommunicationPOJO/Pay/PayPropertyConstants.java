package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Pay;

import java.util.ArrayList;
import java.util.HashMap;

import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class PayPropertyConstants {
    public static final String GP_TOP_UP = "GP";
    public static final String BANGLALINK_TOP_UP = "Banglalink";
    public static final String ROBI_TOP_UP = "Robi ";
    public static final String AIRTEL_TOP_UP = "Airtel";
    public static final String TELETALK_TOP_UP = "Teletalk";
    public static final String MAKE_PAYMENT = "Make Payment";
    public static final String REQUEST_PAYMENT = "Request Payment";
    public static final String PAY_BY_QR_CODE = "Pay by QR Code";


    public static ArrayList<PayDashBoardIconProperty> getPayIconDataSet() {
        ArrayList<PayDashBoardIconProperty> dataList = new ArrayList<>();
        dataList.add(new PayDashBoardIconProperty(MAKE_PAYMENT, "http://national500apps.com/uploads/Android/Bangladesh%20ICTD%20Apps/DESCO%20Bill%20Check/DESCO.png", Constants.SERVICE_ID_MAKE_PAYMENT, ""));
        dataList.add(new PayDashBoardIconProperty(GP_TOP_UP, "http://www.theindependentbd.com/assets/news_images/Grameen-Phone2.jpg", Constants.SERVICE_ID_TOP_UP, "017"));
        dataList.add(new PayDashBoardIconProperty(BANGLALINK_TOP_UP, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRHzVgtSznu_ju5818fAeDkmzIwNhNRQCgF8LFMUx3ZppBqI5s7oQ", Constants.SERVICE_ID_TOP_UP, "019"));
        dataList.add(new PayDashBoardIconProperty(AIRTEL_TOP_UP, "http://www.freelogovectors.net/wp-content/uploads/2014/05/airtel-logo.jpg", Constants.SERVICE_ID_TOP_UP, "016"));
        dataList.add(new PayDashBoardIconProperty(ROBI_TOP_UP, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRkIt4KwOt5XAbJTnKaHJGr43YGpuw7wvUnpcFLlNd2nsDXeGyv", Constants.SERVICE_ID_TOP_UP, "018"));
        dataList.add(new PayDashBoardIconProperty(TELETALK_TOP_UP, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRkIt4KwOt5XAbJTnKaHJGr43YGpuw7wvUnpcFLlNd2nsDXeGyv", Constants.SERVICE_ID_TOP_UP, "015"));
        dataList.add(new PayDashBoardIconProperty(PAY_BY_QR_CODE, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT7r4rP14V19wOHBaO23dVMU9Sjo0JdsXANCYVfxqXTRSQINLZSBw", Constants.SERVICE_ID_MAKE_PAYMENT, ""));

        if(ProfileInfoCacheManager.isBusinessAccount())
            dataList.add(new PayDashBoardIconProperty(REQUEST_PAYMENT, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT7r4rP14V19wOHBaO23dVMU9Sjo0JdsXANCYVfxqXTRSQINLZSBw", Constants.SERVICE_ID_REQUEST_PAYMENT, ""));

        return dataList;
    }
}
