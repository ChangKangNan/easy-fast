package sh.pd.lx.fast.easyfast.customer;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import sh.pd.lx.fast.easyfast.compent.Action;
import sh.pd.lx.fast.easyfast.compent.SqlLogUtil;

import java.util.*;

/**
 * @author ckn
 * @date 2022/6/15
 */
public class CriteriaQuery {
    static JdbcTemplate jdbcTemplate;
    /**
     * SOURCE
     */
    final String sourceTemplate = "jdbcTemplate";
    protected String mainTableName;
    protected List<String> select = new ArrayList<>();
    protected Map<String, String> leftSqlConditions = new HashMap<>();
    protected String where;
    protected List<Object> whereParams = new ArrayList<>();

    CriteriaQuery() {
        if (jdbcTemplate == null) {
            jdbcTemplate = SpringUtil.getBean(sourceTemplate);
        }
    }

    public <T> List<T> list(Class<T> clazz) {
        String selectSql = sql();
        Object[] selectParams = params();
        SqlLogUtil.getInstance().log(selectSql,selectParams,"QUERY");
        RowMapper<T> rowMapper = new BeanPropertyRowMapper<>(clazz);
        List<T> query = jdbcTemplate.query(selectSql, selectParams, rowMapper);
        if(CollUtil.isNotEmpty(query)){
            return query;
        }
        return null;
    }

    public <T> T fetchOne(Class<T> clazz) {
        String selectSql = sql();
        Object[] selectParams = params();
        SqlLogUtil.getInstance().log(selectSql,selectParams,"QUERY");
        RowMapper<T> rowMapper = new BeanPropertyRowMapper<>(clazz);
        List<T> list = jdbcTemplate.query(selectSql, selectParams, rowMapper);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    String sql() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("SELECT ");
        List<String> selectFields=new ArrayList<>();
        if(MapUtil.isNotEmpty(leftSqlConditions)){
            for (String s : select) {
                String[] split = s.split("\\.");
                if(split.length==1){
                    s=mainTableName+"."+s;
                }
                selectFields.add(s);
            }
        }else {
            selectFields=select;
        }
        sb.append((selectFields == null ? "*" : String.join(", ", selectFields)));
        sb.append(" FROM ");
        sb.append(mainTableName);
        sb.append(Action.Blank);
        if (MapUtil.isNotEmpty(leftSqlConditions)) {
            Set<String> set = leftSqlConditions.keySet();
            for (String key : set) {
                String leftSql = leftSqlConditions.get(key);
                sb.append(leftSql);
                sb.append(Action.Blank);
            }
        }
        sb.append(Action.Blank);
        if (where != null) {
            sb.append(" WHERE ").append(String.join(" ", where));
        }
        String s = sb.toString();
        return s;
    }

    Object[] params() {
        List<Object> params = new ArrayList<>();
        if (where != null) {
            for (Object obj : whereParams) {
                if (obj == null) {
                    params.add(null);
                } else {
                    params.add(obj);
                }
            }
        }
        return params.toArray();
    }
}
