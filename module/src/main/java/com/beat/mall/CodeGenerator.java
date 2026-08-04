package com.beat.mall;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.TemplateType;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 这是个示例性质的代码生成器，大部分时候都是根据自己的业务场景来写代码生成器的。
 */
public class CodeGenerator {

    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir"); // 项目主目录
        String moduleJava = projectPath + "/module/src/main/java"; // module模块的java源代码目录
        String moduleResources = projectPath + "/module/src/main/resources"; // module模块的资源文件目录
        String appJava = projectPath + "/app/src/main/java"; // app模块的java源代码目录
        String consoleJava = projectPath + "/console/src/main/java"; // console模块的java源代码目录

        // 使用 FastAutoGenerator 快速配置代码生成器
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/musiccard?serverTimezone=GMT%2B8",
                        "root", "123456") // 配置数据源，也就是要连接的数据库（根据自己项目实际情况修改）
                .globalConfig(builder -> {
                    builder.author("yhp") // 设置作者
                            .outputDir(moduleJava) // 输出目录
                            .disableOpenDir(); // 禁止自动打开输出目录
                }).packageConfig(builder -> {
                    builder.parent("com.beat.mall.module") // 设置父包名
                            .entity("entity") // 设置 Entity 类包名
                            .mapper("mapper") // 设置 Mapper 接口包名
                            .service("service") // 设置 Service 类包名
                            .xml("mybatis.mapper") // 设置 Mapper XML 文件包名
                            .pathInfo(Collections.singletonMap(OutputFile.xml,
                                    moduleResources + "/mybatis/mapper")); // 设置 Mapper XML文件路径
                }).strategyConfig(builder -> {
                    builder.addInclude("music_tag_relation") // 设置：根据表（category）生成代码
                            // Entity 类生成策略
                            .entityBuilder().enableLombok() // 启用 Lombok
                            .enableTableFieldAnnotation() // 启用字段注解
                            .enableFileOverride() // 覆盖已有文件
                            // Mapper 生成策略
                            .mapperBuilder().enableBaseResultMap()  // 生成通用的 resultMap
                            .enableBaseColumnList() // 生成通用的 SQL 片段
                            .enableFileOverride() // 覆盖已有文件
                            // Service 类生成策略
                            .serviceBuilder().formatServiceFileName("%sService").enableFileOverride();
                }).templateConfig(builder -> {
                    builder.disable(TemplateType.CONTROLLER) // 禁用默认的 Controller 类生成
                            .serviceImpl(""); // 不生成默认的 Service 实现
                }).injectionConfig(builder -> {
                    builder.customFile(new ArrayList<>() {{
                        add(new CustomFile.Builder() // 自定义app模块中的 Controller 类生成
                                .fileName("Controller.java").templatePath("/templates/appController.java.ftl").
                                filePath(appJava + "/com/beat/mall/app/controller/") // 确保路径存在
                                .enableFileOverride().build());
                        add(new CustomFile.Builder() // 自定义console模块中的 Controller 类生成
                                .fileName("Controller.java").templatePath("/templates/consoleController.java.ftl").
                                packageName("").filePath(consoleJava + "/com/beat/mall/console/controller/") // 确保路径存在
                                .enableFileOverride().build());
                    }});
                }).templateEngine(new FreemarkerTemplateEngine()) // 使用 Freemarker 模板引擎
                .execute(); // 执行生成
    }
}

