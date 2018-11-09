package umoo.wang.mybatis.enhancer;

import org.springframework.boot.context.properties.ConfigurationProperties;
import umoo.wang.mybatis.enhancer.interceptor.MethodInterceptor;

import java.util.Properties;

/**
 * Created by Mekki on 2018/11/9.
 */
@ConfigurationProperties(prefix = "mybatis-enhancer")
public class MybatisEnhancerProperties {

    private Properties extrasProperties;

    private Class<? extends MethodInterceptor>[] commonInterceptors;

    public Class<? extends MethodInterceptor>[] getCommonInterceptors() {
        return commonInterceptors;
    }

    public void setCommonInterceptors(Class<? extends MethodInterceptor>[] commonInterceptors) {
        this.commonInterceptors = commonInterceptors;
    }

    public Properties getExtrasProperties() {
        return extrasProperties;
    }

    public void setExtrasProperties(Properties extrasProperties) {
        this.extrasProperties = extrasProperties;
    }
}
