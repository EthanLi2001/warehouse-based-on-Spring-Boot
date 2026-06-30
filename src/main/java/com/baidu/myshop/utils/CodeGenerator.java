package com.baidu.myshop.utils;

//import com.baomidou.mybatisplus.annotation.IdType;
//import com.baomidou.mybatisplus.core.toolkit.StringPool;
//import com.baomidou.mybatisplus.generator.AutoGenerator;
//import com.baomidou.mybatisplus.generator.InjectionConfig;
//import com.baomidou.mybatisplus.generator.config.*;
//import com.baomidou.mybatisplus.generator.config.po.TableInfo;
//import com.baomidou.mybatisplus.generator.config.rules.DateType;
//import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
//import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
//import org.apache.commons.lang3.StringUtils;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;

public class CodeGenerator {
//    //包名,配置项目的全名称：groupId.artifactId名称，在微服务中也可以只用groupId
//    public static final String PACKAGE_NAME = "com.baidu.myshop";
//
//    public static void main(String[] args)  throws Exception{
//        String[] tables = new String[] {"cart","role","t_order","t_order_detail","t_product","user"};//表名数组
//        String[] tablePrefixs = new String[] {"t_"}; //表示表的公共头部数组
//        executeCode(PACKAGE_NAME,tables,tablePrefixs); //调用executeCode方法
//    }
//
//    private static void executeCode(String pack,String[] tables,String[] tablePrefixs) throws Exception{
//        // 代码生成器
//        AutoGenerator mpg = new AutoGenerator();
//
//        // 全局配置
//        GlobalConfig gc = new GlobalConfig();
//        // 是否覆盖已有文件
//        gc.setFileOverride(true);
//        // 生成文件的输出目录
//
//        //生成文件的输出目录，方式一：
//        //这是工作空间的路径，如果只有一个项目，并且是工作空间，可以使用这个路径
//        // String projectPath =System.getProperty("user.dir");
//
//
//        //生成文件的输出目录，方式二：
//        // 如果是工作空间，可以给“”,如果是子项目，则使用项目名称
//        File dir = new File("myshop");
//        String projectPath = dir.getCanonicalPath(); //获取子项目的路径
//        System.out.println("projectPath--------------"+projectPath);
//
//
//        gc.setOutputDir(projectPath + "/src/main/java");
//        //设置bean命名规范
//        gc.setEntityName("%s");
//        // 开发人员
//        gc.setAuthor("xiaoye");
//        // 是否打开输出目录
//        gc.setOpen(false);
//        // 开启 BaseResultMap
//        gc.setBaseResultMap(true);
//
//        //设置service接口名称，如果不设置，则service接口名称前都会带有一个I
//        gc.setServiceName("%sService");
//
//        // 指定生成的主键的ID类型
//        gc.setIdType(IdType.AUTO);
//        // 时间类型对应策略: 只使用 java.util.date 代替
//        gc.setDateType(DateType.ONLY_DATE);
//        mpg.setGlobalConfig(gc);
//
//        // 数据源配置
//        DataSourceConfig config= new DataSourceConfig();
//        // 从试图获取
//        config.setUrl("jdbc:mysql://localhost:3306/gbs02?useSSL=false&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai");
//        config.setDriverName("com.mysql.cj.jdbc.Driver");
//        config.setUsername("root");
//        config.setPassword("123456");
//        mpg.setDataSource(config);
//
//        // 包配置
//        PackageConfig pc = new PackageConfig();
//        // 父包名。如果为空，将下面子包名必须写全部， 否则就只需写子包名
//        pc.setParent(pack);
//        // Entity包名
//        pc.setEntity("pojo"); //配置实体类所在的包
//        pc.setMapper("dao"); //设置dao接口所在的包
//        mpg.setPackageInfo(pc);
//
//        // 自定义配置
//        InjectionConfig cfg = new InjectionConfig() {
//            @Override
//            public void initMap() {
//                // to do nothing
//            }
//        };
//        List<FileOutConfig> focList = new ArrayList<>();
//        focList.add(new FileOutConfig("/templates/mapper.xml.ftl") {
//            public String outputFile(TableInfo tableInfo) {
//                // 自定义输入文件名称
//                if (StringUtils.isEmpty(pc.getModuleName())) {
//                    return projectPath + "/src/main/resources/mappers/" + tableInfo.getXmlName() + StringPool.DOT_XML;
//                }else {
//                    return projectPath + "/src/main/resources/mappers/" + pc.getModuleName() + "/" + tableInfo.getXmlName() + StringPool.DOT_XML;
//                }
//            }
//        });
//        cfg.setFileOutConfigList(focList);
//        mpg.setCfg(cfg);
//        mpg.setTemplate(new TemplateConfig().setXml(null));
//
//        // 策略配置
//        StrategyConfig strategy = new StrategyConfig();
//        // 数据库表映射到实体的命名策略: 下划线转驼峰命名
//        strategy.setNaming(NamingStrategy.underline_to_camel);
//        // 数据库表字段映射到实体的命名策略: 下划线转驼峰命名
//        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
//        // 【实体】是否为lombok模型（默认 false）
//        strategy.setEntityLombokModel(true);
//        // 需要包含的表名，允许正则表达式（与exclude二选一配置）
//        strategy.setInclude(tables);
//        // 驼峰转连字符
//        strategy.setControllerMappingHyphenStyle(true);
//        // 表前缀
//        strategy.setTablePrefix(tablePrefixs);
//        mpg.setStrategy(strategy);
//        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
//        mpg.execute();
//    }

}
