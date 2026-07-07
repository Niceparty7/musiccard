package top.yuhanpeng.musiccard.console.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        int time = (int) (System.currentTimeMillis() / 1000);
        this.strictInsertFill(metaObject,
                "createTime",
                Integer.class,
                time);
        this.strictInsertFill(metaObject,
                "updateTime",
                Integer.class,
                time);
        this.strictInsertFill(metaObject,
                "isDeleted",
                Integer.class,
                0);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        int time = (int) (System.currentTimeMillis() / 1000);
        this.setFieldValByName("updateTime",
                time,
                metaObject);
    }
}