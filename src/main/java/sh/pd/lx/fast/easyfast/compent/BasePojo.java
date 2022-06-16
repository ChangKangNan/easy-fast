package sh.pd.lx.fast.easyfast.compent;

import cn.hutool.core.util.ClassUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ckn
 * @date 2022/6/7
 */
public class BasePojo<T>{
    Class<T> classObj;
    String fieldName;
    List<Condition> conditionList;

    public BasePojo() {
        this.classObj = (Class<T>) ClassUtil.getTypeArgument(this.getClass());
        this.conditionList=new ArrayList<>();
    }

    public BasePojo<T> field(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    public Criteria<T> equal(Object... value) {
        if(value != null && value.length!=0){
            if(value.length==1){
                this.conditionList.add(new Condition(this.fieldName, value[0], Link.Equal.expression));
            }else {
                this.conditionList.add(new Condition(this.fieldName, value, Link.In.expression));
            }
        }
        return new Criteria<>(this);
    }

    public EasyDao<T> dao() {
        return new Criteria<>(this).dao();
    }
}
