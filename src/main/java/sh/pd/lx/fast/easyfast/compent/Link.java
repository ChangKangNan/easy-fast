package sh.pd.lx.fast.easyfast.compent;

/**
 * @author ckn
 * @date 2022/6/7
 */
public enum Link {
    Equal("equal"," = "),
    NotEqual("notEqual"," != "),
    Greater("greater"," > "),
    GreaterOrEqual("greaterOrEqual"," >= "),
    Less("less"," < "),
    LessOrEqual("lessOrEqual"," <= "),
    Like("like"," like "),
    NotLike("notLike"," NOT LIKE "),
    Between("between"," BETWEEN "),
    NotBetween("notBetween"," NOT BETWEEN "),
    IsNull("isNull"," IS NULL "),
    In("in"," IN "),
    NotIn("notIn"," NOT IN "),
    IsNotNull("isNotNull"," IS NOT NULL "),
    OrderBy("orderBy"," ORDER BY ");
    public String name;
    public String expression;

    Link(String name,String expression) {
        this.name=name;
        this.expression = expression;
    }
}
