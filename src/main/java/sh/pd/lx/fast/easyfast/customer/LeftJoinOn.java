package sh.pd.lx.fast.easyfast.customer;

import java.util.Map;

/**
 * @author ckn
 * @date 2022/6/15
 */
public class LeftJoinOn extends CriteriaQuery{

    public LeftJoinOn(From from,String joinTable,String filedFrom,String filedRight) {
        this.select=from.select;
        this.mainTableName=from.mainTableName;
        this.leftSqlConditions.put(joinTable,"left join"+" "+joinTable+" on "+mainTableName+"."+filedFrom+"="+joinTable+"."+filedRight);
    }

    public Where where(String clause, Object... args){
        return new Where(this,clause,args);
    }

}
