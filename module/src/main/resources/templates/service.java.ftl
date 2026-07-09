package ${package.Service};

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import ${package.Entity}.${entity};
import ${package.Mapper}.${entity}Mapper;

import java.util.List;

/**
* ${table.comment!""}
*
* @author ${author}
*/
@Service
public class ${entity}Service {

@Resource
private ${entity}Mapper ${entity?uncap_first}Mapper;

public ${entity} getById(Long id) {
if (id == null) {
throw new RuntimeException("id cannot be null!");
}

${entity} ${entity?uncap_first} = ${entity?uncap_first}Mapper.getById(id);

if (${entity?uncap_first} == null) {
throw new RuntimeException("${entity?lower_case} is null!");
}

return ${entity?uncap_first};
}

public ${entity} extractById(Long id) {
if (id == null) {
throw new RuntimeException("id cannot be null!");
}

${entity} ${entity?uncap_first} = ${entity?uncap_first}Mapper.extractById(id);

if (${entity?uncap_first} == null) {
throw new RuntimeException("${entity?lower_case} is null!");
}

return ${entity?uncap_first};
}

public List<${entity}> getAll${entity}(Integer page, Integer pageSize, String keyword) {
return ${entity?uncap_first}Mapper.getAll${entity}(
(page - 1) * pageSize,
pageSize,
keyword
);
}

public Long countTotal(String keyword) {
return ${entity?uncap_first}Mapper.countTotal(keyword);
}

public Long create(
<#list table.fields as field>
    <#if !field.keyFlag>
        ${field.propertyType} ${field.propertyName}<#if field_has_next>,</#if>
    </#if>
</#list>
) {

int timeStamp = (int) (System.currentTimeMillis() / 1000);

${entity} ${entity?uncap_first} = new ${entity}()
<#list table.fields as field>
    <#if !field.keyFlag>
        <#if field.propertyName == "createTime">
            .setCreateTime(timeStamp)
        <#elseif field.propertyName == "updateTime">
            .setUpdateTime(timeStamp)
        <#elseif field.propertyName == "isDeleted">
            .setIsDeleted(0)
        <#else>
            .set${field.propertyName?cap_first}(${field.propertyName})
        </#if>
    </#if>
</#list>
;

<#list table.fields as field>
    <#if !field.keyFlag && field.propertyType == "String" && field.propertyName != "musicDesc" && field.propertyName != "albumTitle" && field.propertyName != "releaseDate">
        if (${field.propertyName} == null) {
        throw new RuntimeException("${field.propertyName} cannot be null!");
        }
    </#if>
</#list>

${entity?uncap_first}Mapper.insert(${entity?uncap_first});

return ${entity?uncap_first}.getId();
}

public Long update(
Long id,
<#list table.fields as field>
    <#if !field.keyFlag>
        ${field.propertyType} ${field.propertyName}<#if field_has_next>,</#if>
    </#if>
</#list>
) {

if (id == null) {
throw new RuntimeException("id cannot be null!");
}

int timeStamp = (int) (System.currentTimeMillis() / 1000);

${entity} ${entity?uncap_first} = new ${entity}()
.setId(id)
<#list table.fields as field>
    <#if !field.keyFlag>
        <#if field.propertyName == "createTime">
            .setCreateTime(timeStamp)
        <#elseif field.propertyName == "updateTime">
            .setUpdateTime(timeStamp)
        <#elseif field.propertyName == "isDeleted">
            .setIsDeleted(0)
        <#else>
            .set${field.propertyName?cap_first}(${field.propertyName})
        </#if>
    </#if>
</#list>
;

if (${entity?uncap_first}Mapper.extractById(id) == null) {
throw new RuntimeException("cannot find the id");
}

return (long) ${entity?uncap_first}Mapper.update(${entity?uncap_first});
}

public Long edit(
Long id,
<#list table.fields as field>
    <#if !field.keyFlag>
        ${field.propertyType} ${field.propertyName}<#if field_has_next>,</#if>
    </#if>
</#list>
) {

Long res;

if (id != null) {
res = update(
id,
<#list table.fields as field>
    <#if !field.keyFlag>
        ${field.propertyName}<#if field_has_next>,</#if>
    </#if>
</#list>
);

if (res == 0) {
throw new RuntimeException("update fail!");
}
} else {
res = create(
<#list table.fields as field>
    <#if !field.keyFlag>
        ${field.propertyName}<#if field_has_next>,</#if>
    </#if>
</#list>
);

if (res == null) {
throw new RuntimeException("create fail!");
}
}

return res;
}

public Integer delete(Long id) {
if (id == null) {
throw new RuntimeException("id cannot be null!");
}
int timeStamp = (int) (System.currentTimeMillis() / 1000);
return ${entity?uncap_first}Mapper.delete(timeStamp, id);
}

}