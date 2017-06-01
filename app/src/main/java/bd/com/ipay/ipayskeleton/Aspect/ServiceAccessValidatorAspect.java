package bd.com.ipay.ipayskeleton.Aspect;

import android.view.View;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Arrays;

import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Logger;

@Aspect
public class ServiceAccessValidatorAspect {

    @Around("execution(* on*Click*(..)) && @annotation(bd.com.ipay.ipayskeleton.Aspect.ValidateAccess)")
    public Object aspectserviceValidatorOnClick(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object result = null;
        ValidateAccess myAnnotation = method.getAnnotation(ValidateAccess.class);

        int[] serviceIds = myAnnotation.value();

        Logger.logW("ServiceIds", Arrays.toString(serviceIds));

        if (!ACLCacheManager.hasServicesAccessibility(serviceIds)) {
            View view = (View) joinPoint.getArgs()[0];
            DialogUtils.showServiceNotAllowedDialog(view.getContext());
        } else {
            result = joinPoint.proceed();
        }
        return result;
    }
}
