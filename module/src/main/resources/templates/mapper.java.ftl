package ${package.Mapper};

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import ${package.Entity}.${entity};

import java.util.List;

/**
* ${table.comment!""}
*
* @author ${author}
*/
@Mapper
public interface ${entity}Mapper {

@Select("<#noparse>select * from ${table.name} where id = #{id} and is_deleted = 0</#noparse>")
${entity} getById(@Param("id") Long id);

@Select("<#noparse>select * from ${table.name} where id = #{id}</#noparse>")
${entity} extractById(@Param("id") Long id);

List<${entity}> getAll${entity}(
@Param("offset") Integer offset,
@Param("pageSize") Integer pageSize,
@Param("keyword") String keyword);

Integer update(@Param("${entity?uncap_first}") ${entity} ${entity?uncap_first});

Long insert(@Param("${entity?uncap_first}") ${entity} ${entity?uncap_first});

Integer delete(
@Param("time") Integer time,
@Param("id") Long id);

Long countTotal(@Param("keyword") String keyword);

}