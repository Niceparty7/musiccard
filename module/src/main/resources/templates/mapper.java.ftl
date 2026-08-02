package ${package.Mapper};

import ${package.Entity}.${entity};
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ${entity}Mapper {

    @Select("SELECT * FROM ${table.name} WHERE id = <#noparse>#{id}</#noparse> AND is_deleted = 0")
    ${entity} getById(@Param("id") Long id);

    @Select("SELECT * FROM ${table.name} WHERE id = <#noparse>#{id}</#noparse>")
    ${entity} extractById(@Param("id") Long id);

    int update(${entity} entity);

    int insert(${entity} entity);

    @Update("UPDATE ${table.name} SET is_deleted = 1, update_time = <#noparse>#{time}</#noparse> WHERE id = <#noparse>#{id}</#noparse>")
    int delete(@Param("id") Long id, @Param("time") int time);

    @Select("SELECT * FROM ${table.name} WHERE is_deleted = 0")
    List<${entity}> getAll();

}