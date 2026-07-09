package ${package.Entity};

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ${entity} {

<#list table.fields as field>
    private ${field.propertyType} ${field.propertyName};
</#list>

}