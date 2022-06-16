package sh.pd.lx.fast.easyfast.customer;

import java.util.List;

/**
 * @author ckn
 * @date 2022/6/15
 */
public class From extends CriteriaQuery{
    From(String tableName, List<String> queryFields){
        this.select=queryFields;
        this.mainTableName=tableName;
    }

    public LeftJoinOn leftJoin(String joinTable,String joinTableKey,String mainTableKey) {
        return new LeftJoinOn(this,joinTable,joinTableKey,mainTableKey);
    }

    public Where where(String clause, Object... args){
        return new Where(this,clause,args);
    }
}
