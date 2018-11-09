package umoo.wang.mybatis.enhancer;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Mekki on 2018/11/9.
 */
@Configuration
@ComponentScan(basePackages = "umoo.wang.mybatis.enhancer")
@EnableConfigurationProperties(MybatisEnhancerProperties.class)
public class MybatisEnhancerAutoConfiguration {
}
