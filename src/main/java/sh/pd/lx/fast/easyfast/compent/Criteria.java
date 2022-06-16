package sh.pd.lx.fast.easyfast.compent;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

/**
 * @author ckn
 * @date 2022/6/8
 */
public class Criteria<T> {
    List<Condition> conditionList;
    BasePojo<T> pojo;
    RowMapper<T> rowMapper;
    String fieldName;

    Criteria(BasePojo<T> pojo) {
        this.pojo = pojo;
        this.fieldName = pojo.fieldName;
        this.conditionList = pojo.conditionList;
        this.rowMapper = new BeanPropertyRowMapper<>(pojo.classObj);
    }

    public Criteria<T> in(Object... value) {
        this.conditionList.add(new Condition(this.fieldName, value, Link.In.expression));
        return this;
    }

    public Criteria<T> notIn(Object... value) {
        this.conditionList.add(new Condition(this.fieldName, value, Link.NotIn.expression));
        return this;
    }

    public Criteria<T> Equal(Object value) {
        this.conditionList.add(new Condition(this.fieldName, value, Link.Equal.expression));
        return this;
    }

    public Criteria<T> notEqual(Object value) {
        this.conditionList.add(new Condition(this.fieldName, value, Link.NotEqual.expression));
        return this;
    }

    public Criteria<T> greater(Object value) {
        this.conditionList.add(new Condition(this.fieldName, value, Link.Greater.expression));
        return this;
    }

    public Criteria<T> greaterOrEqual(Object value) {
        this.conditionList.add(new Condition(this.fieldName, value, Link.GreaterOrEqual.expression));
        return this;
    }

    public Criteria<T> less(Object value) {
        this.conditionList.add(new Condition(this.fieldName, value, Link.Less.expression));
        return this;
    }

    public Criteria<T> lessOrEqual(Object value) {
        this.conditionList.add(new Condition(this.fieldName, value, Link.LessOrEqual.expression));
        return this;
    }

    public Criteria<T> like(Object value) {
        this.conditionList.add(new Condition(this.fieldName, value, Link.Like.expression));
        return this;
    }

    public Criteria<T> notLike(Object value) {
        this.conditionList.add(new Condition(this.fieldName, value, Link.NotLike.expression));
        return this;
    }

    public Criteria<T> isNull() {
        this.conditionList.add(new Condition(this.fieldName, null, Link.IsNull.expression));
        return this;
    }

    public Criteria<T> isNotNull() {
        this.conditionList.add(new Condition(this.fieldName, null, Link.IsNotNull.expression));
        return this;
    }

    public Criteria<T> orderBy(String action) {
        this.conditionList.add(new Condition(this.fieldName, action, Link.OrderBy.expression));
        return this;
    }

    public EasyDao<T> dao() {
        return new EasyDao<T>(this);
    }

}
