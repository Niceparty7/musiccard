package ${package.Service};

import ${package.Entity}.${entity};
import ${package.Mapper}.${entity}Mapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ${entity}Service {

    @Resource
    private ${entity}Mapper mapper;

    public ${entity} getById(Long id) {
        ${entity} entity = mapper.getById(id);
        if (entity == null) {
            throw new RuntimeException("${entity}不存在: " + id);
        }
        return entity;
    }

    public ${entity} extractById(Long id) {
        return mapper.extractById(id);
    }

    public Long update(${entity} entity) {
        int time = (int) (System.currentTimeMillis() / 1000);
        entity.setUpdateTime(time);
        mapper.update(entity);
        return entity.getId();
    }

    public Long insert(${entity} entity) {
        int time = (int) (System.currentTimeMillis() / 1000);
        entity.setCreateTime(time);
        entity.setUpdateTime(time);
        mapper.insert(entity);
        return entity.getId();
    }

    public int delete(Long id) {
        int time = (int) (System.currentTimeMillis() / 1000);
        return mapper.delete(id, time);
    }

    public List<${entity}> getAll() {
        return mapper.getAll();
    }
}
