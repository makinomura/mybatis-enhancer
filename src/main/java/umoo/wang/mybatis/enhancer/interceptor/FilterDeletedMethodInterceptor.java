package umoo.wang.mybatis.enhancer.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.mybatis.mapper.entity.Example;
import umoo.wang.mybatis.enhancer.MybatisEnhancerProperties;
import umoo.wang.mybatis.enhancer.service.BaseService;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

/**
 * Created by Mekki on 2018/11/2.
 * 过滤掉DELETE_FLAG不为0的已删除数据
 */
@SuppressWarnings("unchecked")
public class FilterDeletedMethodInterceptor<T> implements MethodInterceptor<T> {
    private Logger logger = LoggerFactory.getLogger(FilterDeletedMethodInterceptor.class);

    private Method deleteFlagMethod;
    private Boolean hasDeleteFlag;//是否有删除标注位

    private Class<T> entityClass;//实体类
    private String deletedKey;//删除标志位字段名
    private Integer deletedValue;//数据为有效数据是标志位的值

    /**
     * 处理实体类查询条件
     *
     * @param record
     */
    public void processEntityQueryParameter(T record) {

        try {
            deleteFlagMethod.invoke(record, deletedValue);
        } catch (Exception e) {
            logger.error(e.getClass().getName(), e);
        }
    }

    /**
     * 获取删除标志位方法
     *
     * @param clazz
     */
    private void setDeleteFlagMethod(Class<?> clazz) {

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(clazz);

            Optional<Method> o = Arrays.stream(beanInfo.getPropertyDescriptors()).filter(p -> Objects.equals(p.getName(), deletedKey))
                    .map(PropertyDescriptor::getWriteMethod).findAny();

            if (o.isPresent()) {
                deleteFlagMethod = o.get();
                hasDeleteFlag = true;
            } else {
                hasDeleteFlag = false;
            }
        } catch (IntrospectionException e) {
            logger.error(e.getClass().getName(), e);
            hasDeleteFlag = false;
        }
    }

    /**
     * 处理Example查询条件
     *
     * @param example
     */
    public void processExampleQueryParameter(Example example) {

        if (example.getOredCriteria().isEmpty()) {
            example.createCriteria().andEqualTo(deletedKey, deletedValue);
        } else {
            example.getOredCriteria().forEach(c -> c.andEqualTo(deletedKey, deletedValue));
        }
    }

    @Override
    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;

        setDeleteFlagMethod(entityClass);
    }

    @Override
    public void setProperties(MybatisEnhancerProperties properties) {

        Properties ep = properties.getExtrasProperties();
        if (ep != null) {
            deletedKey = ep.getProperty("deletedKey");
            deletedValue = Integer.valueOf(ep.getProperty("deletedValue"));
        } else {
            deletedKey = "deleteFlag";
            deletedValue = 0;
        }
    }

    @Override
    public boolean before(BaseService<T> thisObject, Method method, Object[] arguments, ResultHolder resultHolder) {

        if (!hasDeleteFlag) {
            return true;
        }

        MethodUtil.ServiceMethodInfo mi = MethodUtil.describe(method);

        if (mi.isSelect) {
            if (method.getName().equals("selectAll")) { //selectAll 转换为 selectByExample
                Example example = new Example(entityClass);

                processExampleQueryParameter(example);
                resultHolder.setResult(thisObject.selectByExample(example));
                return false;
            }

            for (Object argument : arguments) {
                if (argument instanceof Example) {
                    processExampleQueryParameter((Example) argument);
                } else if (argument.getClass().equals(entityClass)) {
                    processEntityQueryParameter((T) argument);
                }
            }
        }

        if (mi.isUpdate) {
            for (Object argument : arguments) {
                if (argument instanceof Example) {
                    processExampleQueryParameter((Example) argument);
                }
            }
        }

        return true;
    }

}
