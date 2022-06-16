<#assign tableName = table.tableName/>
<#assign className = table.pojoName/>
<#assign propertys = table.columns/>
package ${table.pojoPackagePath}.pojo;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;
import lombok.experimental.Accessors;
<#list table.packages as package>
${package}
</#list>

/**
* @author kangnan.chang
*/

@Accessors(chain=true)
@Table(name = "${tableName}")
@Entity
@Data
public class ${className} {
<#list propertys as p>

<#if p.key==true>
    @Id
</#if>
    @Column(name = "${p.columnName}")
    private ${p.propertyType} ${p.propertyName};

</#list>

}
