package sh.pd.lx.fast.easyfast.customer;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.extra.spring.SpringUtil;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import sh.pd.lx.fast.easyfast.compent.SqlLogUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ckn
 * @date 2022/6/9
 */
public class EasyCustomer {

    final JdbcTemplate jdbcTemplate= SpringUtil.getBean("jdbcTemplate");

    private EasyCustomer(){}

    public static EasyCustomer create(){
        return new EasyCustomer();
    }
    /**
     * 通过 sql文件运行获得返回结果
     *
     * @param sqlPath
     * @param parameters
     * @param rowMapperClass
     * @return
     */
    public <E> List<E> selectByFile(String sqlPath, Map<String, Object> parameters, Class<E> rowMapperClass) {
        //处理参数
        Map<String, Object> parameterMap = new HashMap<>();
        if (CollUtil.isNotEmpty(parameters)) {
            parameters.forEach((k, v) -> {
                if (v instanceof String) {
                    parameterMap.put(k, "'" + v + "'");
                } else {
                    parameterMap.put(k, v);
                }
            });
        }
        ClassPathResource resource = new ClassPathResource(sqlPath);
        String sql = IoUtil.read(resource.getStream()).toString();
        if (CollUtil.isNotEmpty(parameters)) {
            for (String key : parameterMap.keySet()) {
                Object o = parameterMap.get(key);
                sql = sql.replace("#{" + key + "}", o + "");
            }
        }

        RowMapper<E> rowMapper = new BeanPropertyRowMapper<>(rowMapperClass);
        SqlLogUtil.getInstance().info("Executing SQL : ["+sql+"]");
        List<E> query = jdbcTemplate.query(sql, new Object[]{}, rowMapper);
        return query;
    }
     public  Select  select(String... selectFields){
        return new Select(selectFields);
     }
}
