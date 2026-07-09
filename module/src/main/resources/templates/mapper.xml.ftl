<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<#assign obj = entity?uncap_first>

<mapper namespace="${package.Mapper}.${entity}Mapper">

    <select id="getAll${entity}"
            resultType="${package.Entity}.${entity}">

        select *

        from ${table.name}

        where is_deleted = 0

        <if test="keyword!=null and keyword!=''">
            and music_name like concat('%', <#noparse>#{keyword}</#noparse>, '%')
        </if>

        order by id

        limit <#noparse>#{offSet}</#noparse>, <#noparse>#{pageSize}</#noparse>

    </select>

    <insert id="insert"
            useGeneratedKeys="true"
            keyProperty="id">

        insert into ${table.name}

        <trim prefix="("
              suffix=")"
              suffixOverrides=",">

            <#list table.fields as field>
                <#if !field.keyFlag>
                    <if test="${obj}.${field.propertyName}!=null<#if field.propertyType?ends_with('String')> and ${obj}.${field.propertyName}!=''</#if>">
                        ${field.columnName},
                    </if>
                </#if>
            </#list>

        </trim>

        <trim prefix="values("
              suffix=")"
              suffixOverrides=",">

            <#list table.fields as field>
                <#if !field.keyFlag>
                    <if test="${obj}.${field.propertyName}!=null<#if field.propertyType?ends_with('String')> and ${obj}.${field.propertyName}!=''</#if>">
                        <#noparse>#{</#noparse>${obj}.${field.propertyName}<#noparse>}</#noparse>,
                    </if>
                </#if>
            </#list>

        </trim>

    </insert>

    <update id="update">

        update ${table.name}

        <set>

            <#list table.fields as field>
                <#if !field.keyFlag>
                    <if test="${obj}.${field.propertyName}!=null<#if field.propertyType?ends_with('String')> and ${obj}.${field.propertyName}!=''</#if>">
                        ${field.columnName}=<#noparse>#{</#noparse>${obj}.${field.propertyName}<#noparse>}</#noparse>,
                    </if>
                </#if>
            </#list>

        </set>

        where id=<#noparse>#{</#noparse>${obj}.id<#noparse>}</#noparse>

    </update>

    <update id="delete">

        update ${table.name}

        set is_deleted = 1,
        update_time = <#noparse>#{time}</#noparse>

        where id = <#noparse>#{id}</#noparse>
        and is_deleted = 0

    </update>

    <select id="countTotal"
            resultType="java.lang.Long">

        select count(*)

        from ${table.name}

        where is_deleted = 0

        <if test="keyword!=null and keyword!=''">
            and music_name like concat('%', <#noparse>#{keyword}</#noparse>, '%')
        </if>

    </select>

</mapper>