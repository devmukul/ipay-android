package bd.com.ipay.ipayskeleton.Aspect;

import android.app.Dialog;
import android.view.View;
import android.widget.RadioGroup;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Arrays;

import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLManager;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
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

        if (!ACLManager.hasServicesAccessibility(serviceIds)) {
            if (joinPoint.getArgs()[0] instanceof View) {
                View view = (View) joinPoint.getArgs()[0];
                DialogUtils.showServiceNotAllowedDialog(view.getContext());
            } else if (joinPoint.getArgs()[0] instanceof Dialog) {
                Dialog dialog = (Dialog) joinPoint.getArgs()[0];
                DialogUtils.showServiceNotAllowedDialog(dialog.getContext());
            }
        } else {
            result = joinPoint.proceed();
        }
        return result;
    }

    @Around("execution(* addContact(..)) && @annotation(bd.com.ipay.ipayskeleton.Aspect.ValidateAccess)")
    public Object aspectserviceValidatorOnAddContact(ProceedingJoinPoint joinPoint) throws Throwable {

        Object result = null;

        if (ACLManager.hasServicesAccessibility(ServiceIdConstants.ADD_CONTACTS)) {
            result = joinPoint.proceed();
        }
        return result;
    }

    @Around("execution(* onCheckedChanged(..)) && @annotation(bd.com.ipay.ipayskeleton.Aspect.ValidateAccess)")
    public Object aspectserviceValidatorOnRadioGroupCheckedChanged(ProceedingJoinPoint joinPoint) throws Throwable {

        Object result = null;

        RadioGroup radioGroup = (RadioGroup) joinPoint.getArgs()[0];
        final int checkedId = radioGroup.getCheckedRadioButtonId();

        Logger.logW("Aspect", checkedId + "");
        switch (radioGroup.getId()) {
            case R.id.transaction_history_type_radio_group:
                switch (checkedId) {
                    case R.id.radio_button_pending:
                        if (ACLManager.hasServicesAccessibility(ServiceIdConstants.PENDING_TRANSACTION)) {
                            result = joinPoint.proceed();
                        } else {
                            DialogUtils.showServiceNotAllowedDialog(radioGroup.getContext());
                        }
                        break;
                    case R.id.radio_button_completed:
                        if (ACLManager.hasServicesAccessibility(ServiceIdConstants.COMPLETED_TRANSACTION)) {
                            result = joinPoint.proceed();
                        } else {
                            DialogUtils.showServiceNotAllowedDialog(radioGroup.getContext());
                        }
                        break;
                }
                break;
            case R.id.contact_type_radio_group:
                if (ACLManager.hasServicesAccessibility(ServiceIdConstants.GET_CONTACTS)) {
                    result = joinPoint.proceed();
                } else {
                    DialogUtils.showServiceNotAllowedDialog(radioGroup.getContext());
                }
                break;
            default:
                result = joinPoint.proceed();
                break;
        }
        return result;
    }
}
