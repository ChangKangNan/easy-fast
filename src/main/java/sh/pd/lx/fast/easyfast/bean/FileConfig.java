package sh.pd.lx.fast.easyfast.bean;

import cn.hutool.core.collection.CollUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * @author kangnan.chang
 */
public class FileConfig {

    /**
     * 模板文件生成的包路径 xxx.xxx.xxx
     */
    private String basePackage;

    /**
     * 需要生成模型的表名
     */
    private Set<String> createTables = CollUtil.newHashSet("all");
    /**
     * 生成文件时候是否过滤表前缀信息，ord_orders = orders
     */
    private Boolean prefix = Boolean.FALSE;
    /**
     * 是否通过前缀信息生成不同的文件目录,ord_orders 会为将orders生成的模板存储在ord目录下
     */
    private Boolean prefixFileDir = Boolean.FALSE;
    /**
     * 过滤指定前缀
     */
    private String prefixName;
    /**
     * 下划线转驼峰 product_sku = ProductSku
     */
    private Boolean underline2CamelStr = Boolean.TRUE;
    /**
     * 是否要强制创建文件,使用此设置会覆盖旧的文件
     */
    private Boolean replaceFile = Boolean.TRUE;
    /**
     * 是否使用Lombok插件,极力推荐,会有很不错的代码简洁度提升
     * 需配置,本框架已经引用
     * <dependency>
     * <groupId>org.projectlombok</groupId>
     * <artifactId>lombok</artifactId>
     * <version>1.18.6</version>
     * </dependency>
     * <p>
     * 配置后需要IDE需要安装lombok插件
     */
    private Boolean useLombok = Boolean.FALSE;

    /**
     * 是否使用Swagger2文档
     * 需配置,本框架已经引用
     * <dependency>
     *  <groupId>io.springfox</groupId>
     *  <artifactId>springfox-swagger2</artifactId>
     *  <version>2.9.2</version>
     * </dependency>
     */
    private Boolean useDTOSwagger2 = Boolean.FALSE;

    /**
     * 指定在哪个模块下创建模板文件,如果项目中使用了多模块,需要指定模块名称
     */
    private String childModuleName = "";

    /**
     * 数据库连写信息
     */
    private String driverClass;
    private String url;
    private String user;
    private String password;

    /**
     * 需要生成的表名称
     *
     * @param tables 多个表用逗号隔开,如果需要生成数据库中所有的表,参数为all
     */
    public void setCreateTables(String... tables) {
        createTables = new HashSet<>();
        for (String table : tables) {
            createTables.add(table);
        }
    }


    /**
     * 如果是多模块项目,需要使用此项
     *
     * @param childModuleName 指定在哪个模块下创建模板文件
     */
    public void setChildModuleName(String childModuleName) {
        this.childModuleName = childModuleName;
    }

    /**
     * 是否使用了Lombok插件
     * @param useLombok 不进行设置的话默认false
     */
    public void setUseLombok(Boolean useLombok) {
        this.useLombok = useLombok;
    }

    /**
     * 是否在DTO上生成Swagger2注解
     * @param useDTOSwagger2 不进行设置的话默认false
     */
    public void setUseDTOSwagger2(Boolean useDTOSwagger2) {
        this.useDTOSwagger2 = useDTOSwagger2;
    }

    /**
     * 是否覆盖旧文件
     * @param replaceFile 不设置的话默认为false
     */
    public void setReplaceFile(Boolean replaceFile) {
        this.replaceFile = replaceFile;
    }

    /**
     * 是否对字段和生成的对象进行下划线转换,如 product_sku = ProductSku
     * @param underline2CamelStr 不设置的话默认为true
     */
    public void setUnderline2CamelStr(Boolean underline2CamelStr) {
        this.underline2CamelStr = underline2CamelStr;
    }

    /**
     * 生成模板的包路径
     * @param basePackage 包路径地址 xxx.xxx.xxx
     */
    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    /**
     * 设置数据库连接信息
     * @param url 数据库连接
     * @param user 用户名
     * @param password 密码
     * @param driverClass 数据库驱动
     */
    public void setDBInfo(String url, String user, String password, String driverClass) {
        this.url = url + "&useInformationSchema=true";
        this.user = user;
        this.password = password;
        this.driverClass = driverClass;
    }

    /**
     * 是否过滤表前缀信息
     * @param prefix 生成文件时候是否过滤表前缀信息，ord_orders = orders
     */
    public void setIgnorePrefix(Boolean prefix) {
        this.prefix = prefix;
    }


    public Set<String> getCreateTables() {
        return createTables;
    }

    public Boolean getPrefix() {
        return prefix;
    }

    public Boolean getPrefixFileDir() {
        return prefixFileDir;
    }

    public String getPrefixName() {
        return prefixName;
    }

    public Boolean getUnderline2CamelStr() {
        return underline2CamelStr;
    }

    public Boolean getReplaceFile() {
        return replaceFile;
    }

    public Boolean getUseLombok() {
        return useLombok;
    }

    public Boolean getUseDTOSwagger2() {
        return useDTOSwagger2;
    }

    public String getChildModuleName() {
        return childModuleName;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }


}
