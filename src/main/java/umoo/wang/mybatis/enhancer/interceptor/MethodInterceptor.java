package umoo.wang.mybatis.enhancer.interceptor;

import org.springframework.util.ClassUtils;
import umoo.wang.mybatis.enhancer.MybatisEnhancerProperties;
import umoo.wang.mybatis.enhancer.service.BaseService;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Created by Mekki on 2018/10/31.
 * BaseService拦截器，拦截Method调用
 *
 * @param <T>
 */
public interface MethodInterceptor<T> {

    /**
     * 设置实体类
     *
     * @param entityClass
     */
    default void setEntityClass(Class<T> entityClass) {
    }

    /**
     * 设置配置
     *
     * @param properties
     */
    default void setProperties(MybatisEnhancerProperties properties) {
    }

    /**
     * 查询执行前
     *
     * @param thisObject   拦截的service
     * @param method       执行的方法
     * @param arguments    方法参数
     * @param resultHolder false时返回的结果
     * @return 是否继续执行查询
     */
    default boolean before(BaseService<T> thisObject, Method method, Object[] arguments, ResultHolder resultHolder) {
        return true;
    }

    /**
     * 查询执行后
     *
     * @param thisObject 拦截的service
     * @param method     执行的方法
     * @param arguments  方法参数
     * @param result     查询结果
     */
    default void after(BaseService<T> thisObject, Method method, Object[] arguments, Object result) {
    }

    /**
     * 查询结果容器
     */
    class ResultHolder {
        Type type; //结果类型
        Object result; //结果

        public ResultHolder(Type type) {
            this.type = type;
        }

        public Object getResult() {
            return result;
        }

        public void setResult(Object result) {
            if (Objects.equals(type, result.getClass()) || ((Class<?>) type).isAssignableFrom(result.getClass()) || Objects.equals(ClassUtils.resolvePrimitiveIfNecessary((Class<?>) type), ClassUtils.resolvePrimitiveIfNecessary(result.getClass()))) {
                this.result = result;
            } else {
                throw new IllegalArgumentException("result type is illegal");
            }
        }
    }
}