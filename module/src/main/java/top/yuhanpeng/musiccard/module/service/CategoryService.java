package top.yuhanpeng.musiccard.module.service;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import top.yuhanpeng.musiccard.module.entity.Category;
import top.yuhanpeng.musiccard.module.mapper.CategoryMapper;

import java.util.List;

/**
 * 音乐类型表
 *
 * @author YHP
 */
@Service
public class CategoryService {
    @Resource
    private CategoryMapper categoryMapper;

    public Category getById(Long id) throws Exception {
        if (id == null) {
            throw new RuntimeException("id cannot be null!");
        }
        Category category = categoryMapper.getById(id);
        if (category == null) {
            throw new RuntimeException("category is null!");
        }
        return category;
    }

    public Category extractById(Long id) throws Exception {
        if (id == null) {
            throw new RuntimeException("id cannot be null!");
        }
        Category category = categoryMapper.extractById(id);
        if (category == null) {
            throw new RuntimeException("category is null!");
        }
        return category;
    }

    public List<Category> getAllCategory() {
        return categoryMapper.getAllCategory();
    }

    public Long create(String typeName, String typeImage, String typeDesc) throws Exception {
        if (typeName == null) {
            throw new RuntimeException("typeName cannot be null!");
        }
        if (typeImage == null) {
            throw new RuntimeException("typeImage cannot be null!");
        }
        int timeStamp = (int) (System.currentTimeMillis() / 1000);
        Category category = new Category()
                .setTypeName(typeName)
                .setTypeImage(typeImage)
                .setTypeDesc(typeDesc)
                .setCreateTime(timeStamp)
                .setUpdateTime(timeStamp)
                .setIsDeleted(0);
        categoryMapper.insert(category);
        return category.getId();
    }

    public Long update(Long id, String typeName, String typeImage, String typeDesc) throws Exception {
        if (id == null) {
            throw new RuntimeException("id cannot be null!");
        }
        int timeStamp = (int) (System.currentTimeMillis() / 1000);
        Category category = new Category()
                .setId(id)
                .setTypeName(typeName)
                .setTypeImage(typeImage)
                .setTypeDesc(typeDesc)
                .setUpdateTime(timeStamp)
                .setIsDeleted(0);
        if (extractById(id) == null) {
            throw new RuntimeException("cannot find the id");
        }
        return (long) categoryMapper.update(category);
    }

    public Long edit(Long id, String typeName, String typeImage, String typeDesc) throws Exception {
        Long res;
        if (id != null) {
            res = update(id, typeName, typeImage, typeDesc);
            if (res == 0) {
                throw new RuntimeException("update fail!");
            }
        } else {
            res = create(typeName, typeImage, typeDesc);
            if (res == null) {
                throw new RuntimeException("create fail!");
            }
        }
        return res;
    }

    public Integer delete(Long id) throws Exception {
        if (id == null) {
            throw new RuntimeException("id cannot be null!");
        }
        int timeStamp = (int) (System.currentTimeMillis() / 1000);
        return categoryMapper.delete(timeStamp, id);
    }
}