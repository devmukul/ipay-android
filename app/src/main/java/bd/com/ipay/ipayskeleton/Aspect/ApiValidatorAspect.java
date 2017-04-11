package bd.com.ipay.ipayskeleton.Aspect;

import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.ToastandLogger.LoggerUtilities;

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

        LoggerUtilities.logDebug("Aspect", "Masud aspected something here");

        try {
            GenericHttpResponse mGenericHttpResponse = (GenericHttpResponse) args[0];

            if (mGenericHttpResponse != null && mGenericHttpResponse.isUpdateNeeded()) {
                DialogUtils.showAppUpdateRequiredDialog(mGenericHttpResponse.getContext());
            } else {
                result = joinPoint.proceed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
