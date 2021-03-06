package sh.pd.lx.fast.easyfast.compent;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author kangnan.chang
 */
public class Mapper<T> {

    final Class<T> entityClass;
    final String tableName;

    /**
     * @Id property
     */
   public final AccessibleProperty id;

    /**
     * all properties including @Id, key is property name (NOT column name)
     */
   public final List<AccessibleProperty> allProperties;

    /**
     * lower-case property name -> AccessibleProperty
     */
    final Map<String, AccessibleProperty> allPropertiesMap;

    public final List<AccessibleProperty> properties;


    public final RowMapper<T> rowMapper;

    public Mapper(Class<T> clazz) throws Exception {
        List<AccessibleProperty> accessibleProperties = getProperties(clazz);
        Field[] declaredFields = clazz.getDeclaredFields();
        if (ArrayUtil.isNotEmpty(declaredFields)) {
            for (Field field : declaredFields) {
                String fieldName = field.getName();
                for (AccessibleProperty property : accessibleProperties) {
                    String fName = property.propertyName;
                    if (fieldName.equals(fName)) {
                        Column fieldAnnotation = field.getAnnotation(Column.class);
                        if (fieldAnnotation == null || fieldAnnotation.name().isEmpty()) {
                            property.columnName = property.propertyName;
                        }else {
                            property.columnName = fieldAnnotation.name();
                        }
                        Id id = field.getAnnotation(Id.class);
                        if (id != null) {
                            property.isId=true;
                        }
                    }
                }
            }
        }
        //??????ID????????????
        AccessibleProperty[] ids = accessibleProperties.stream().filter(AccessibleProperty::isId).toArray(AccessibleProperty[]::new);
        if(ArrayUtil.isEmpty(ids)){throw new RuntimeException("Require one @Id");}
        this.id = ids[0];
        this.allProperties = accessibleProperties;
        this.allPropertiesMap = buildPropertiesMap(this.allProperties);
        this.entityClass = clazz;
        this.tableName = getTableName(clazz);
        this.properties = accessibleProperties.stream().filter(AccessibleProperty::isNotId).collect(Collectors.toList());
        //jdbcTemplate ??????rowMapper
        this.rowMapper = new BeanPropertyRowMapper<>(this.entityClass);
    }

    /**
     * ??????ID???
     * @param bean
     * @return
     * @throws ReflectiveOperationException
     */
    Object getIdValue(Object bean) throws ReflectiveOperationException {
        return this.id.getter.invoke(bean);
    }

    /**
     * ????????????column??????map
     * @param props
     * @return
     */
    Map<String, AccessibleProperty> buildPropertiesMap(List<AccessibleProperty> props) {
        Map<String, AccessibleProperty> map = new HashMap<>();
        for (AccessibleProperty prop : props) {
            map.put(underscoreName(prop.propertyName), prop);
        }
        return map;
    }

    /**
     *  ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     *  ?????????HelloWorld->HELLO_WORLD->hello_world
     *  @param name ???????????????????????????????????????
     *  @return ????????????????????????????????????????????????
     */
    public static String underscoreName(String name) {
        StringBuilder result = new StringBuilder();
        if (name != null && name.length() > 0) {
            // ?????????????????????????????????
            result.append(name.substring(0, 1).toUpperCase());
            // ????????????????????????
            for (int i = 1; i < name.length(); i++) {
                String s = name.substring(i, i + 1);
                // ?????????????????????????????????
                if (s.equals(s.toUpperCase()) && !Character.isDigit(s.charAt(0))) {
                    result.append("_");
                }
                // ??????????????????????????????
                result.append(s.toUpperCase());
            }
        }
        return result.toString().toLowerCase();
    }



    /**
     * ????????????
     * @param clazz
     * @return
     */
    private String getTableName(Class<?> clazz) {
        Table table = clazz.getAnnotation(Table.class);
        if (table != null) {
            return table.name();
        }
        return clazz.getSimpleName();
    }

    /**
     * ??????lombok????????????
     * @param clazz
     * @return
     * @throws Exception
     */
    private List<AccessibleProperty> getProperties(Class<?> clazz) throws Exception {
        List<AccessibleProperty> properties = new ArrayList<>();
        BeanInfo info = Introspector.getBeanInfo(clazz);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            // ??????getClass():
            if (pd.getName().equals("class")) {
                continue;
            }
            String fieldName = pd.getDisplayName();
            Method getter = null;
            Method setter = null;
            Method[] declaredMethods = clazz.getDeclaredMethods();
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getterName = "get" + firstLetter + fieldName.substring(1);
            String setterName = "set" + firstLetter + fieldName.substring(1);
            for (Method method : declaredMethods) {
                String name = method.getName();
                if (name.equals(getterName)) {
                    getter = method;
                }
                if (name.equals(setterName)) {
                    setter = method;
                }
            }
            pd.setReadMethod(getter);
            pd.setWriteMethod(setter);
            if (getter != null && setter != null) {
                properties.add(new AccessibleProperty(pd));
            } else {
                throw new RuntimeException("Property " + pd.getName() + " is not read/write.");
            }
        }
        return properties;
    }
}
