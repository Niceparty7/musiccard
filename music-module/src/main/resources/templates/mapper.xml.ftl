<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="${package.Mapper}.${entity}Mapper">

    <!-- 插入数据 -->
    <insert id="insert" useGeneratedKeys="true" keyProperty="id" parameterType="${package.Entity}.${entity}">
        INSERT INTO ${table.name}
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <#list table.fields as field>
            <#-- 所有字段都可插入，空值判断 -->
                <#if field.columnType?contains("char") || field.columnType?contains("varchar")>
                    <if test="${field.propertyName} != null and ${field.propertyName} != ''">
                        ${field.columnName}<#if field_has_next>,</#if>
                    </if>
                <#else>
                    <if test="${field.propertyName} != null">
                        ${field.columnName}<#if field_has_next>,</#if>
                    </if>
                </#if>
            </#list>
        </trim>
        VALUES
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <#list table.fields as field>
                <#if field.columnType?contains("char") || field.columnType?contains("varchar")>
                    <if test="${field.propertyName} != null and ${field.propertyName} != ''">
                        <#noparse>#{</#noparse>${field.propertyName}<#noparse>}</#noparse><#if field_has_next>,</#if>
                    </if>
                <#else>
                    <if test="${field.propertyName} != null">
                        <#noparse>#{</#noparse>${field.propertyName}<#noparse>}</#noparse><#if field_has_next>,</#if>
                    </if>
                </#if>
            </#list>
        </trim>
    </insert>

    <!-- 更新数据（根据主键） -->
    <update id="update" parameterType="${package.Entity}.${entity}">
        UPDATE ${table.name}
        <set>
            <#list table.fields as field>
            <#-- 排除主键字段 -->
                <#if !(field.keyFlag?? && field.keyFlag == true)>
                    <#if field.columnType?contains("char") || field.columnType?contains("varchar")>
                        <if test="${field.propertyName} != null and ${field.propertyName} != ''">
                            ${field.columnName} = <#noparse>#{</#noparse>${field.propertyName}<#noparse>}</#noparse>,
                        </if>
                    <#else>
                        <if test="${field.propertyName} != null">
                            ${field.columnName} = <#noparse>#{</#noparse>${field.propertyName}<#noparse>}</#noparse>,
                        </if>
                    </#if>
                </#if>
            </#list>
        </set>
        WHERE
        <#list table.fields as field>
            <#if field.keyFlag?? && field.keyFlag == true>
                ${field.columnName} = <#noparse>#{</#noparse>${field.propertyName}<#noparse>}</#noparse>
            </#if>
        </#list>
    </update>

</mapper>
