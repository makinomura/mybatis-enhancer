package umoo.wang.mybatis.enhancer.service;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import umoo.wang.mybatis.enhancer.interceptor.MethodInterceptor;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Mekki on 2018/10/31.
 */
@Component
@Aspect
public class BaseServiceAspect {

    /**
     * 环切方法
     *
     * @param point 切入点
     * @param <T>   当前实体类
     * @return
     * @throws Throwable
     */
    @SuppressWarnings("unchecked")
    @Around("execution(* BaseService.*(..))")
    public <T> Object doAroundAdvice(ProceedingJoinPoint point) throws Throwable {
        Object result;

        Object[] arguments = point.getArgs();
        if (point.getSignature() instanceof MethodSignature) {
            MethodSignature ms = (MethodSignature) point.getSignature();

            Method method = ms.getMethod();//当前执行的方法
            BaseService<T> thisObject = (BaseService<T>) point.getTarget();

            MethodInterceptor.ResultHolder resultHolder = new MethodInterceptor.ResultHolder(method.getReturnType());

            List<MethodInterceptor<T>> methodInterceptors = thisObject.methodInterceptors;

            for (MethodInterceptor<T> methodInterceptor : methodInterceptors) {
                if (!methodInterceptor.before(thisObject, method, arguments, resultHolder)) {
                    return resultHolder.getResult();
                }
            }

            result = point.proceed();

            for (MethodInterceptor<T> methodInterceptor : methodInterceptors) {
                methodInterceptor.after(thisObject, method, arguments, result);
            }

            return result;
        }

        return point.proceed();
    }
}
