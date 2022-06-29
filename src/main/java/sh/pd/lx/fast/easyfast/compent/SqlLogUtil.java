package sh.pd.lx.fast.easyfast.compent;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author ckn
 * @date 2022/6/13
 */
public class SqlLogUtil {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static SqlLogUtil getInstance() {
        return new SqlLogUtil();
    }

   public void log(String sql, Object[] parameters, String action) {
        if (parameters == null || ArrayUtil.isEmpty(parameters)) {
            logger.info("Executing SQL " + action + " [" + sql + "]");
        } else {
            String[] split = sql.split("\\?");
            StringBuilder sqlBuilder = new StringBuilder();
            for (int i = 0; i < parameters.length; i++) {
                sqlBuilder.append(split[i]);
                sqlBuilder.append(Action.Blank);
                if (parameters[i] instanceof String || parameters[i] instanceof Date) {
                    sqlBuilder.append("'");
                }
                if (parameters[i] instanceof Date) {
                    sqlBuilder.append(DateUtil.format((Date) parameters[i], "yyyy-MM-dd"));
                } else {
                    sqlBuilder.append(parameters[i]);
                }
                if (parameters[i] instanceof String || parameters[i] instanceof Date) {
                    sqlBuilder.append("'");
                }
                sqlBuilder.append(Action.Blank);
                if(StrUtil.equals(action,"INSERT") && i == (parameters.length-1)){
                    sqlBuilder.append(split[i+1]);
                }
            }
            if(parameters.length<split.length){
                sqlBuilder.append(split[parameters.length]);
            }
            logger.info("Executing SQL " + action + " [" + sqlBuilder.toString() + "]");
        }
    }
    public void info(String msg){
        logger.info(msg);
    }
}
