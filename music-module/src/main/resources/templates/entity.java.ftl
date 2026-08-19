package ${package.Entity};

<#-- 导入常用注解和类 -->
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("${table.name}")
public class ${entity} {

<#-- 遍历字段生成属性 -->
<#list table.fields as field>
    <#if field.keyFlag?? && field.keyFlag == true>
    @TableId(type = IdType.AUTO, value = "${field.columnName}")
    </#if>
    private ${field.propertyType} ${field.propertyName};
</#list>

}
