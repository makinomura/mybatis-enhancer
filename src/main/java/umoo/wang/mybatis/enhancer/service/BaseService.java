package umoo.wang.mybatis.enhancer.service;

import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;
import umoo.wang.mybatis.enhancer.MybatisEnhancerProperties;
import umoo.wang.mybatis.enhancer.interceptor.MethodInterceptor;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 2017/9/28.
 */
@Transactional
public abstract class BaseService<T> {
    @Autowired
    protected Mapper<T> mapper;
    protected List<MethodInterceptor<T>> methodInterceptors;
    protected Class<T> entityClass;
    private Logger logger = LoggerFactory.getLogger(BaseService.class);
    @Autowired
    private ApplicationContext ac;

    @Autowired
    private MybatisEnhancerProperties properties;

    @SuppressWarnings("unchecked")
    @PostConstruct
    private void init() {
        methodInterceptors = new ArrayList<>();

        if (properties.getCommonInterceptors() != null) {
            //添加通用拦截器
            for (Class<? extends MethodInterceptor> clazz : properties.getCommonInterceptors()) {
                try {
                    methodInterceptors.add(clazz.newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();

                    logger.error("unable to instantiate " + clazz.getName() + "!", e);
                }
            }
        }

        //获取实体类型
        ParameterizedType superClass = (ParameterizedType) this.getClass().getGenericSuperclass();
        entityClass = (Class<T>) superClass.getActualTypeArguments()[0];

        //获取所有拦截器
        String[] miNames = ac.getBeanNamesForType(ResolvableType.forClassWithGenerics(MethodInterceptor.class, entityClass));

        for (String miName : miNames) {
            methodInterceptors.add(ac.getBean(miName, MethodInterceptor.class));
        }

        methodInterceptors.sort(MethodInterceptor::compareTo);

        for (MethodInterceptor<T> mi : methodInterceptors) {
            mi.setProperties(properties);
            mi.setEntityClass(entityClass);
        }
    }


    public T selectOne(T record) {
        return mapper.selectOne(record);
    }

    public List<T> select(T record) {
        return mapper.select(record);
    }

    public List<T> selectAll() {
        return mapper.selectAll();
    }

    public int selectCount(T record) {
        return mapper.selectCount(record);
    }

    public T selectByPrimaryKey(Object key) {
        return mapper.selectByPrimaryKey(key);
    }

    public boolean existsWithPrimaryKey(Object key) {
        return mapper.existsWithPrimaryKey(key);
    }

    public int insert(T record) {
        return mapper.insert(record);
    }

    public int insertSelective(T record) {
        return mapper.insertSelective(record);
    }

    public int updateByPrimaryKey(T record) {
        return mapper.updateByPrimaryKey(record);
    }

    public int updateByPrimaryKeySelective(T record) {
        return mapper.updateByPrimaryKeySelective(record);
    }

    public int delete(T record) {
        return mapper.delete(record);
    }

    public int deleteByPrimaryKey(Object key) {
        return mapper.deleteByPrimaryKey(key);
    }

    public List<T> selectByExample(Object example) {
        return mapper.selectByExample(example);
    }

    public int selectCountByExample(Object example) {
        return mapper.selectCountByExample(example);
    }

    public int deleteByExample(Object example) {
        return mapper.deleteByExample(example);
    }

    public int updateByExample(T record, Object example) {
        return mapper.updateByExample(record, example);
    }

    public int updateByExampleSelective(T record, Object example) {
        return mapper.updateByExampleSelective(record, example);
    }

    public List<T> selectByExampleAndRowBounds(Object example, RowBounds rowBounds) {
        return mapper.selectByExampleAndRowBounds(example, rowBounds);
    }

    public List<T> selectByRowBounds(T record, RowBounds rowBounds) {
        return mapper.selectByRowBounds(record, rowBounds);
    }
}
