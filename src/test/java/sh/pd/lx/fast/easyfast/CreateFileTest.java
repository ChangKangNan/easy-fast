package sh.pd.lx.fast.easyfast;


import sh.pd.lx.fast.easyfast.bean.FileConfig;
import sh.pd.lx.fast.easyfast.util.CreateTemplateFile;

/**
 * @author ckn
 * @date 2021/10/18
 */
public class CreateFileTest {
    public static void main(String[] args) throws Exception{
        FileConfig config = new FileConfig();
        //数据库连接
        config.setDBInfo("jdbc:mysql://kaifa.mysql.guo-kai.com:3306/gk-ims?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useInformationSchema=true","gkims-kaifa","PGrsByizeD357ajR","com.mysql.jdbc.Driver");
        //是否生成表前缀
        config.setIgnorePrefix(true);
        //是否覆盖原文件,默认false
        config.setReplaceFile(true);
        //文件生成的包路径
        config.setBasePackage("sh.pd.lx.fast.easyfast");
        config.setCreateTables("test_user");
        CreateTemplateFile.createTemplates(config);
    }
}
