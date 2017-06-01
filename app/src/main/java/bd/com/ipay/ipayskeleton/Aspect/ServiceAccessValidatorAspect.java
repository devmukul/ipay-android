package bd.com.ipay.ipayskeleton.Aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Arrays;

import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Logger;

@Aspect
public class ServiceAccessValidatorAspect {


    @Around("execution(* on*Click*(..)) && @annotation(bd.com.ipay.ipayskeleton.Aspect.ValidateAccess)")
    public Object aspectserviceValidator(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object result = null;
        ValidateAccess myAnnotation = method.getAnnotation(ValidateAccess.class);

        int[] serviceIds = myAnnotation.value();

        Logger.logW("ServiceIds", Arrays.toString(serviceIds));

        if (!ACLCacheManager.hasServicesAccessibility(serviceIds)) {
            MyApplication application = MyApplication.getMyApplicationInstance();
            if (application != null) {
                DialogUtils.showServiceNotAllowedDialog(application);
            }
        } else {
            result = joinPoint.proceed();
        }
        return result;
    }
}
