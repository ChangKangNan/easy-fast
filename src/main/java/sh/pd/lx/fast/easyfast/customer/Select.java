package sh.pd.lx.fast.easyfast.customer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ckn
 * @date 2022/6/15
 */
public class Select {
    private List<String> queryFields=new ArrayList<>();
    Select(String... selectFields) {
        for (String selectField : selectFields) {
            queryFields.add(selectField);
        }
    }

    public  From from(String tableName) {
        return new From(tableName,queryFields);
    }
}
