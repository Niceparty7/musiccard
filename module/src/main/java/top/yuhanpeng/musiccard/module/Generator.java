package top.yuhanpeng.musiccard.module;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;

import java.util.Collections;

public class Generator {

    public static void main(String[] args) {

        FastAutoGenerator.create(
                        "jdbc:mysql://localhost:3306/musiccard",
                        "root",
                        "123456"
                )
                .templateConfig(builder -> {
                    builder
                            .entity("/templates/entity.java.ftl")
                            .mapper("/templates/mapper.java.ftl")
                            .xml("/templates/mapper.xml.ftl")
                            .service("/templates/service.java.ftl")
                            .serviceImpl("");
                })

                .templateEngine(new FreemarkerTemplateEngine())

                .globalConfig(builder -> {
                    builder.author("YHP")
                            .outputDir("E:/idea/ideaProject/musiccard/module/src/main/java");
                })

                .packageConfig(builder -> {
                    builder.parent("top.yuhanpeng.musiccard.module");
                    builder.pathInfo(Collections.singletonMap(
                            OutputFile.xml,
                            "E:/idea/ideaProject/musiccard/module/src/main/resources/mapper"
                    ));
                })

                .strategyConfig(builder -> {
                    builder.addInclude("user")
                            .entityBuilder()
                            .enableLombok()
                            .logicDeleteColumnName("is_deleted")
                            .addTableFills(
                                    new Column("create_time", FieldFill.INSERT),
                                    new Column("update_time", FieldFill.INSERT_UPDATE)
                            );
                    builder.controllerBuilder()
                            .enableRestStyle();
                    builder.mapperBuilder()
                            .enableMapperAnnotation();
                })
                .execute();
    }
}