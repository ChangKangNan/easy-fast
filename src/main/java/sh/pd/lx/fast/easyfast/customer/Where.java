package sh.pd.lx.fast.easyfast.customer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ckn
 * @date 2022/6/15
 */
public class Where extends CriteriaQuery{
    Where(From from,String clause, Object... params) {
        this.mainTableName=from.mainTableName;
        this.select=from.select;
        this.where=clause;
        List<Object> p=new ArrayList<>();
        for (Object param : params) {
            p.add(param);
        }
        this.whereParams=p;
    }
    Where(LeftJoinOn leftJoinOn,String clause, Object... params) {
        this.select=leftJoinOn.select;
        this.mainTableName=leftJoinOn.mainTableName;
        this.leftSqlConditions=leftJoinOn.leftSqlConditions;
        this.where=clause;
        List<Object> p=new ArrayList<>();
        for (Object param : params) {
            p.add(param);
        }
        this.whereParams=p;
    }
}
