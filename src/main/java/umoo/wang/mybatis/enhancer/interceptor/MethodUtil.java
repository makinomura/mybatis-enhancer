package umoo.wang.mybatis.enhancer.interceptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Mekki on 2018/11/9.
 */
public class MethodUtil {
    private static List<String> allMethods = Arrays.asList("select", "selectByPrimaryKey", "selectOne", "selectAll", "selectByExample", "selectCountByExample", "selectCount", "selectByExampleAndRowBounds", "selectByRowBounds", "existsWithPrimaryKey",
            "delete", "deleteByPrimaryKey", "deleteByExample",
            "insert", "insertSelective",
            "updateByPrimaryKey", "updateByPrimaryKeySelective", "updateByExample", "updateByExampleSelective");

    public static boolean isSelect(Method method) {
        return allMethods.stream().filter(item -> item.contains("select") || item.equals("existsWithPrimaryKey")).anyMatch(item -> item.equals(method.getName()));
    }

    public static boolean isDelete(Method method) {
        return allMethods.stream().filter(item -> item.contains("delete")).anyMatch(item -> item.equals(method.getName()));
    }

    public static boolean isInsert(Method method) {
        return allMethods.stream().filter(item -> item.contains("insert")).anyMatch(item -> item.equals(method.getName()));
    }

    public static boolean isUpdate(Method method) {
        return allMethods.stream().filter(item -> item.contains("update")).anyMatch(item -> item.equals(method.getName()));
    }

    public static boolean isByExample(Method method) {
        return allMethods.stream().filter(item -> item.contains("Example")).anyMatch(item -> item.equals(method.getName()));
    }

    public static boolean isByPrimaryKey(Method method) {
        return allMethods.stream().filter(item -> item.contains("PrimaryKey")).anyMatch(item -> item.equals(method.getName()));
    }

    public static boolean isSelective(Method method) {
        return allMethods.stream().filter(item -> item.contains("Selective")).anyMatch(item -> item.equals(method.getName()));
    }

    /**
     * 描述一个方法
     * @param method
     * @return
     */
    public static ServiceMethodInfo describe(Method method) {
        return new ServiceMethodInfo(method.getName(),
                isSelect(method),
                isDelete(method),
                isInsert(method),
                isUpdate(method),
                isByExample(method),
                isByPrimaryKey(method),
                isSelective(method));
    }

    /**
     * BaseService方法描述
     */
    public static class ServiceMethodInfo {
        public final String methodName;
        public final boolean isSelect;
        public final boolean isDelete;
        public final boolean isInsert;
        public final boolean isUpdate;
        public final boolean isByExample;
        public final boolean isByPrimaryKey;
        public final boolean isSelective;

        public ServiceMethodInfo(String methodName, boolean isSelect, boolean isDelete, boolean isInsert, boolean isUpdate, boolean isByExample, boolean isByPrimaryKey, boolean isSelective) {
            this.methodName = methodName;
            this.isSelect = isSelect;
            this.isDelete = isDelete;
            this.isInsert = isInsert;
            this.isUpdate = isUpdate;
            this.isByExample = isByExample;
            this.isByPrimaryKey = isByPrimaryKey;
            this.isSelective = isSelective;
        }
    }
}
