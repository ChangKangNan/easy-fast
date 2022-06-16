package sh.pd.lx.fast.easyfast.compent;

/**
 * @author ckn
 * @date 2022/6/7
 */
public class Condition {
    public String link;
    public String columnName;
    public Object value;

    public Condition(String columnName, Object value,String link) {
        this.link = link;
        this.columnName = columnName;
        this.value = value;
    }
}
