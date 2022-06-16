package sh.pd.lx.fast.easyfast.compent;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ckn
 * @date 2022/6/7
 */
public class EasyDao<T> {
    /**TRANSFER*/
    static JdbcTemplate jdbcTemplate;
    String tableName;
    Criteria<T> criteria;
    RowMapper<T> rowMapper;
    /**SOURCE*/
    final String sourceTemplate = "jdbcTemplate";
    /**SQL ACTION*/
    final String UPDATE="UPDATE";
    final String QUERY="QUERY";
    final String DELETE="DELETE";
    final String INSERT="INSERT";
    /**
     * class -> Mapper:
     */
    private static Map<Class<?>, Mapper<?>> classMapping = null;


    protected void init() {
        if(classMapping==null){
        jdbcTemplate = SpringUtil.getBean(sourceTemplate);
        List<Class<?>> classes = scanEntities(criteria.pojo.classObj.getPackage().getName());
        Map<Class<?>, Mapper<?>> classMapping = new HashMap<>();
        try {
            for (Class<?> clazz : classes) {
                Mapper<?> mapper = new Mapper<>(clazz);
                classMapping.put(clazz, mapper);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.classMapping = classMapping;
        }
    }

    public static List<Class<?>> scanEntities(String basePackage) {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
        List<Class<?>> classes = new ArrayList<>();
        Set<BeanDefinition> beans = provider.findCandidateComponents(basePackage);
        for (BeanDefinition bean : beans) {
            try {
                classes.add(Class.forName(bean.getBeanClassName()));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return classes;
    }
    public  String fetchUpdateSql(Mapper<?> mapper){
        String updateSQL = "UPDATE " + mapper.tableName + " SET "
                + String.join(", ",
                mapper.properties.stream().filter(AccessibleProperty::isNotId).map(p -> p.columnName + " = ?").toArray(String[]::new))
                + " WHERE " + mapper.id.columnName + " = ?";
        return updateSQL;
    }
    <T> Mapper<T> getMapper(Class<T> clazz) {
        Mapper<T> mapper = (Mapper<T>) this.classMapping.get(clazz);
        if (mapper == null) {
            throw new RuntimeException("Target class is not a registered com.entity: " + clazz.getName());
        }
        return mapper;
    }

    public void update(T bean) {
        try {
            final Mapper<?> mapper = getMapper(bean.getClass());
            String updateSQL=fetchUpdateSql(mapper);
            int n = 0;
            int dCount = 0;
            int size = mapper.properties.size()+1;
            Object[] args = new Object[size];
            for (AccessibleProperty prop : mapper.properties) {
                boolean notId = prop.isNotId();
                if (notId) {
                    Object invoke = prop.getter.invoke(bean);
                    if (ObjectUtil.isNotEmpty(invoke)) {
                        args[n] = invoke;
                        n++;
                    } else {
                        dCount++;
                        String replace = prop.columnName + " = ?,";
                        boolean contains = StrUtil.contains(updateSQL, replace);
                        String replacement = "";
                        if (contains) {
                            replacement = replace;
                        } else {
                            replacement = ", " + prop.columnName + " = ?";
                        }
                        String format = StrUtil.replace(updateSQL, replacement, "");
                        updateSQL = format;
                    }
                }
            }
            Object invoke = mapper.id.getter.invoke(bean);
            if (ObjectUtil.isEmpty(invoke)) {
                throw new RuntimeException("未传入主键!");
            }
            args[n] = invoke;
            Object[] argsFinal = new Object[size - dCount];
            for (int i = 0; i <argsFinal.length; i++) {
                argsFinal[i] = args[i];
            }
            if (args.length == 1) {
                throw new RuntimeException("无更新属性!");
            }
            jdbcTemplate.update(updateSQL, argsFinal);
            SqlLogUtil.getInstance().log(updateSQL,argsFinal,UPDATE);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
    /**
     * Update com.entity's updatable properties by id.
     *
     * @param bean Entity object.
     */
    public void updateOverwrite(T bean) {
        try {
            Mapper<?> mapper = getMapper(bean.getClass());
            String updateSQL=fetchUpdateSql(mapper);
            Object[] args = new Object[mapper.properties.size()+1];
            int n = 0;
            for (AccessibleProperty prop : mapper.properties) {
                boolean notId = prop.isNotId();
                if (notId) {
                    args[n] = prop.getter.invoke(bean);
                    n++;
                }
            }
            args[n] = mapper.id.getter.invoke(bean);
            jdbcTemplate.update(updateSQL, args);
            SqlLogUtil.getInstance().log(updateSQL,args,UPDATE);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取表名
     *
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

     EasyDao(Criteria<T> criteria) {
        this.criteria = criteria;
        init();
        tableName = getTableName(criteria.pojo.classObj);
        this.rowMapper = new BeanPropertyRowMapper<>(criteria.pojo.classObj);
    }

    public T unique() {
        String selectSql = packageSql();
        selectSql += " limit 0,1";
        SqlLogUtil.getInstance().log(selectSql,null,QUERY);
        try {
            T t = jdbcTemplate.queryForObject(selectSql, rowMapper);
            return t;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<T> fetchAll() {
        String selectSql = packageSql();
        SqlLogUtil.getInstance().log(selectSql,null,QUERY);
        List<T> list = jdbcTemplate.query(selectSql, new Object[]{}, rowMapper);
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        return list;
    }

    final String SELECT = "SELECT";
    final String FROM = "FROM";
    final String WHERE = "WHERE 1";

    String packageSql() {
        StringBuilder sb = new StringBuilder(128);
        sb.append(SELECT);
        sb.append(Action.Blank);
        sb.append("*");
        sb.append(Action.Blank);
        sb.append(FROM);
        sb.append(Action.Blank);
        sb.append(tableName);
        sb.append(Action.Blank);
        sb.append(WHERE);
        sb.append(Action.Blank);
        List<Condition> conditionList = criteria.conditionList;
        for (Condition condition : conditionList) {
            if (!condition.link.equals(Link.OrderBy.expression)) {
                sb.append(Way.AND);
                sb.append(Action.Blank);
                sb.append(condition.columnName);
                sb.append(Action.Blank);
                sb.append(condition.link);
                sb.append(Action.Blank);
                if (condition.value != null) {
                    if (ArrayUtil.isArray(condition.value)) {
                        sb.append(Action.LeftBracket);
                        Object[] wrap = ArrayUtil.wrap(condition.value);
                        for (Object o : wrap) {
                            if (o instanceof String || o instanceof Date) {
                                sb.append("'");
                            }
                            if (o instanceof Date) {
                                sb.append(DateUtil.format((Date) o, "yyyy-MM-dd"));
                            } else {
                                sb.append(o);
                            }
                            if (o instanceof String || o instanceof Date) {
                                sb.append("'");
                            }
                            sb.append(",");
                        }
                        sb = new StringBuilder(sb.substring(0, sb.length() - 1));
                        sb.append(Action.RightBracket);
                    } else {
                        if (condition.value instanceof String || condition.value instanceof Date) {
                            sb.append("'");
                        }
                        if (StrUtil.equalsAny(condition.link, Link.Like.name, Link.NotLike.name)) {
                            sb.append("%");
                        }
                        if (condition.value instanceof Date) {
                            sb.append(DateUtil.format((Date) condition.value, "yyyy-MM-dd"));
                        } else {
                            sb.append(condition.value);
                        }
                        if (StrUtil.equalsAny(condition.link, Link.Like.name, Link.NotLike.name)) {
                            sb.append("%");
                        }
                        if (condition.value instanceof String || condition.value instanceof Date) {
                            sb.append("'");
                        }
                    }
                }
            }
        }
        List<Condition> oderByConditions = conditionList.stream().filter(t -> t.link.equals(Link.OrderBy.expression)).distinct().collect(Collectors.toList());
        if(CollUtil.isNotEmpty(oderByConditions)){
            sb.append(Action.Blank);
            sb.append(Link.OrderBy.expression);
            for (Condition oderByCondition : oderByConditions) {
                sb.append(Action.Blank);
                sb.append(oderByCondition.columnName);
                sb.append(Action.Blank);
                sb.append(oderByCondition.value);
                sb.append(",");
            }
            sb = new StringBuilder(sb.substring(0, sb.length() - 1));
        }
        return sb.toString();
    }

    /**
     * Remove bean by id.
     */
    public  void deleteByPrimary(Object id) {
        Mapper<T> mapper = (Mapper<T>) getMapper(criteria.pojo.classObj);
        String deleteSQL = "DELETE FROM " + tableName + " WHERE  "+mapper.id.columnName + " = ?";
        SqlLogUtil.getInstance().log(deleteSQL,new Object[]{id},DELETE);
        jdbcTemplate.update(deleteSQL, id);
    }
    /**
     * ?包装返回值
     * @param n
     * @return
     */
    private String numOfQuestions(int n) {
        String[] qs = new String[n];
        return String.join(", ", Arrays.stream(qs).map((s) -> {
            return "?";
        }).toArray(String[]::new));
    }

    public  String fetchInsertSql(Mapper<?> mapper){
        String insertSQL = "INSERT INTO " + mapper.tableName + " ("
                + String.join(", ",  mapper.properties.stream().map(p -> p.columnName).toArray(String[]::new))
                + ") VALUES (" + numOfQuestions(mapper.properties.size()) + ")";
        return insertSQL;
    }
    public  void insert(T bean) {
        try {
            int rows;
            final Mapper<?> mapper = getMapper(bean.getClass());
            String insertSQL=fetchInsertSql(mapper);
            Object[] args = new Object[mapper.properties.size()];
            int n = 0;
            for (AccessibleProperty prop : mapper.properties) {
                Object invoke = prop.getter.invoke(bean);
                args[n] = invoke;
                n++;
            }
            SqlLogUtil.getInstance().log(insertSQL,args,INSERT);;
            if (mapper.id.isId()) {
                KeyHolder keyHolder = new GeneratedKeyHolder();
                String finalInsertSQL = insertSQL;
                rows = jdbcTemplate.update(new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps = connection.prepareStatement(finalInsertSQL,
                                Statement.RETURN_GENERATED_KEYS);
                        for (int i = 0; i < args.length; i++) {
                            ps.setObject(i + 1, args[i]);
                        }
                        return ps;
                    }
                }, keyHolder);
                if (rows == 1) {
                    mapper.id.setter.invoke(bean, keyHolder.getKey());
                }
            } else {
                rows = jdbcTemplate.update(insertSQL, args);
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
}

