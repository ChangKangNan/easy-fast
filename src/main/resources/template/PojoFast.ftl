package ${table.pojoPackagePath}.pojo.fast;
<#assign PojoName = table.pojoName/>
<#assign propertys = table.columns/>
<#list table.packages as package>
    ${package}
</#list>
import sh.pd.lx.fast.easyfast.compent.BasePojo;
import sh.pd.lx.fast.easyfast.compent.Criteria;
import sh.pd.lx.fast.easyfast.pojo.${PojoName};

/**
* @author kangnan.chang
*/

public class ${PojoName}Fast extends BasePojo<${PojoName}> {
    public static ${PojoName}Fast create() {return new ${PojoName}Fast();}
<#list propertys as p>
    public Criteria<${PojoName}> ${p.propertyName}(${p.propertyType}... ${p.propertyName}s) { return this.field("${p.propertyName}").equal(${p.propertyName}s);}
</#list>
}