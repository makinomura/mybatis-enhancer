package umoo.wang.mybatis.enhancer.interceptor;


import umoo.wang.mybatis.enhancer.service.BaseService;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * Created by Mekki on 2018/11/5.
 * 通用拦截，默认拦截增删改，全部启用可能影响性能
 */
@SuppressWarnings("unchecked")
public interface EnhancedMethodInterceptor<T> extends MethodInterceptor<T> {
    default boolean enableUpdate() {
        return true;
    } //是否启用更新拦截

    default boolean enableInsert() {
        return true;
    } //是否启用新增拦截

    default boolean enableDelete() {
        return true;
    } //是否启用删除拦截


    default boolean onUpdating(T newValue, List<T> itemsWillUpdate) {
        return true;
    } //更新前调用的方法

    default boolean onInserting(T itemWillInsert) {
        return true;
    } //新增前调用的方法

    default boolean onDeleting(List<T> itemsWillDelete) {
        return true;
    } //删除前调用的方法

    default void onUpdated(List<T> itemsUpdated) {
    } //更新完成调用的方法

    default void onInserted(T itemInserted) {
    } //新增完成调用的方法

    default void onDeleted(List<T> itemDeleted) {
    } //删除完成调用的方法

    @Override
    default boolean before(BaseService<T> thisObject, Method method, Object[] arguments, ResultHolder resultHolder) {
        MethodUtil.ServiceMethodInfo mi = MethodUtil.describe(method);

        if (mi.isDelete && enableDelete()) {
            if (mi.isByExample) {
                Object Example = arguments[0];
                List<T> items = thisObject.selectByExample(Example);

                if (items.size() != 0 && onDeleting(items)) {
                    resultHolder.setResult(thisObject.deleteByExample(Example));
                    onDeleted(items);
                } else {
                    resultHolder.setResult(0);
                }
            } else if (mi.isByPrimaryKey) {
                Object pk = arguments[0];
                T item = thisObject.selectByPrimaryKey(pk);
                List<T> items = Collections.singletonList(item);

                if (item != null && onDeleting(items)) {
                    resultHolder.setResult(thisObject.deleteByPrimaryKey(pk));
                    onDeleted(items);
                } else {
                    resultHolder.setResult(0);
                }
            } else {
                T record = (T) arguments[0];
                List<T> items = thisObject.select(record);

                if (items.size() != 0 && onDeleting(items)) {
                    resultHolder.setResult(thisObject.delete(record));
                    onDeleted(items);
                } else {
                    resultHolder.setResult(0);
                }

                return false;
            }
        } else if (mi.isInsert && enableInsert()) {
            if (!onInserting(((T) arguments[0]))) {
                resultHolder.setResult(0);
                return false;
            }
        } else if (mi.isUpdate && enableUpdate()) {
            T record = (T) arguments[0];

            if (mi.isByExample) {
                if (!onUpdating(record, thisObject.selectByExample(arguments[1]))) {
                    resultHolder.setResult(0);
                    return false;
                }
            } else if (mi.isByPrimaryKey) {
                if (!onUpdating(record, thisObject.select((record)))) {
                    resultHolder.setResult(0);
                    return false;
                }
            }
        }

        return true;
    }

    default void after(BaseService<T> thisObject, Method method, Object[] arguments, Object result) {

        MethodUtil.ServiceMethodInfo mi = MethodUtil.describe(method);

        if (mi.isUpdate && enableUpdate()) {
            Integer effectRows = (Integer) result;
            T record = ((T) arguments[0]);

            if (effectRows != 0) {
                if (mi.isByExample) {
                    Object example = arguments[1];
                    onUpdated(thisObject.selectByExample(example));
                } else {
                    onUpdated(Collections.singletonList(record));
                }
            }
        } else if (mi.isInsert && enableInsert()) {
            Integer effectRows = (Integer) result;
            T record = ((T) arguments[0]);

            if (effectRows != 0) {
                if (mi.isSelective) {
                    onInserted(thisObject.selectOne(record));
                } else {
                    onInserted(record);
                }
            }
        }
    }
}
