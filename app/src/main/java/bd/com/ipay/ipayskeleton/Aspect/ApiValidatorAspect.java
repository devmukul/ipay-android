package bd.com.ipay.ipayskeleton.Aspect;

import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;

@Aspect
public class ApiValidatorAspect {

    private final String POINTCUT_METHOD = "execution(* httpResponseReceiver*(..))";

    @Pointcut(POINTCUT_METHOD)
    public void validateApiVersion() {
    }

    @Around("validateApiVersion()")
    public Object aspectValidateApiVersion(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Object result = null;

        Log.d("Aspect", "Masud aspected something here");

        try {
            HttpResponseObject mHttpResponseObject = (HttpResponseObject) args[0];

            if (mHttpResponseObject != null && mHttpResponseObject.isUpdateNeeded()) {
                DialogUtils.showAppUpdateRequiredDialog(mHttpResponseObject.getContext());
            } else {
                result = joinPoint.proceed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
