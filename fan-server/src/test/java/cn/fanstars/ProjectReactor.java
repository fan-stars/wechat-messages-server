package cn.fanstars;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.fanstars.framework.common.util.collection.SetUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static java.io.File.separator;

/**
 * 项目修改器，一键替换 Maven 的 groupId、artifactId，项目的 package 等
 * <p>
 * 通过修改 groupIdNew、artifactIdNew、projectBaseDirNew 三个变量
 *
 * @author 繁星源码
 */
@Slf4j
public class ProjectReactor {

    private static final String GROUP_ID = "cn.fanstars.base";
    private static final String ARTIFACT_ID = "fan";
    private static final String PACKAGE_NAME = "cn.fanstars";
    private static final String TITLE = "基础管理系统";
    private static final String PROJECT_BASE_DIR = getProjectBaseDir();

    // ========== 配置，需要你手动修改 ==========
    static final String GROUP_ID_NEW = "cn.fanstars.base";
    static final String ARTIFACT_ID_NEW = "fan";
    static final String PACKAGE_NAME_NEW = "cn.fanstars";
    static final String TITLE_NEW = "基础管理系统";

    static final String PROJECT_NAME_NEW;
    static final String PROJECT_BASE_DIR_NEW;

    static {
        String[] groupIdNews = GROUP_ID_NEW.split("\\.");
        PROJECT_NAME_NEW = ARTIFACT_ID_NEW + "-" + groupIdNews[groupIdNews.length - 1];
        PROJECT_BASE_DIR_NEW = PROJECT_BASE_DIR.substring(0, PROJECT_BASE_DIR.lastIndexOf(separator) + 1) + PROJECT_NAME_NEW;
    }

    /**
     * 白名单文件，不进行重写，避免出问题
     */
    private static final Set<String> WHITE_FILE_TYPES = SetUtils.asSet("gif", "jpg", "svg", "png", // 图片
            "eot", "woff2", "ttf", "woff",  // 字体
            "xdb"); // IP 库

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        String projectBaseDir = getProjectBaseDir();
        log.info("[main][原项目路劲改地址 ({})]", projectBaseDir);
        log.info("[main][新项目路劲地址为 ({})]", PROJECT_BASE_DIR_NEW);

//        log.info("[main][检测新项目目录 ({})是否存在]", PROJECT_BASE_DIR_NEW);
//        if (FileUtil.exist(PROJECT_BASE_DIR_NEW)) {
//            log.error("[main][新项目目录检测 ({})已存在，请更改新的目录！程序退出]", PROJECT_BASE_DIR_NEW);
//            return;
//        }
        // 如果新目录中存在 PACKAGE_NAME，ARTIFACT_ID 等关键字，路径会被替换，导致生成的文件不在预期目录
//        if (StrUtil.containsAny(PROJECT_BASE_DIR_NEW, PACKAGE_NAME, ARTIFACT_ID, StrUtil.upperFirst(ARTIFACT_ID))) {
//            log.error("[main][新项目目录 `projectBaseDirNew` 检测 ({}) 存在冲突名称「{}」或者「{}」，请更改新的目录！程序退出]",
//                    PROJECT_BASE_DIR_NEW, PACKAGE_NAME, ARTIFACT_ID);
//            return;
//        }
//        log.info("[main][完成新项目目录检测，新项目路径地址 ({})]", PROJECT_BASE_DIR_NEW);
        // 获得需要复制的文件
        log.info("[main][开始获得需要重写的文件，预计需要 10-20 秒]");
        Collection<File> files = listFiles(projectBaseDir);
        log.info("[main][需要重写的文件数量：{}，预计需要 15-30 秒]", files.size());
        // 写入文件
        files.forEach(file -> {
            // 如果是白名单的文件类型，不进行重写，直接拷贝
            String fileType = getFileType(file);
            if (WHITE_FILE_TYPES.contains(fileType)) {
                copyFile(file, projectBaseDir, PROJECT_BASE_DIR_NEW, PACKAGE_NAME_NEW, ARTIFACT_ID_NEW);
                return;
            }
            // 如果非白名单的文件类型，重写内容，在生成文件
            String content = replaceFileContent(file, GROUP_ID_NEW, ARTIFACT_ID_NEW, PACKAGE_NAME_NEW, TITLE_NEW);
            writeFile(file, content, projectBaseDir, PROJECT_BASE_DIR_NEW, PACKAGE_NAME_NEW, ARTIFACT_ID_NEW);
        });
        log.info("[main][重写完成]共耗时：{} 秒", (System.currentTimeMillis() - start) / 1000);
    }

    private static String getProjectBaseDir() {
        String baseDir = System.getProperty("user.dir");
        if (StrUtil.isEmpty(baseDir)) {
            throw new NullPointerException("项目基础路径不存在");
        }
        return baseDir;
    }

    static Collection<File> listFiles(String projectBaseDir) {
        Collection<File> files = FileUtil.loopFiles(projectBaseDir);
        // 移除 IDEA、Git 自身的文件、Node 编译出来的文件
        files = files.stream()
                .filter(file -> !file.getPath().contains(separator + "target" + separator)
                        && !file.getPath().contains(separator + "node_modules" + separator)
                        && !file.getPath().contains(separator + ".idea" + separator)
                        && !file.getPath().contains(separator + ".git" + separator)
                        && !file.getPath().contains(separator + "dist" + separator)
                        && !file.getPath().contains(".iml")
                        && !file.getPath().contains(".html.gz"))
                .collect(Collectors.toList());
        return files;
    }

    private static String replaceFileContent(File file, String groupIdNew,
                                             String artifactIdNew, String packageNameNew,
                                             String titleNew) {
        String content = FileUtil.readString(file, StandardCharsets.UTF_8);
        // 如果是白名单的文件类型，不进行重写
        String fileType = getFileType(file);
        if (WHITE_FILE_TYPES.contains(fileType)) {
            return content;
        }
        // 执行文件内容都重写
        return content.replaceAll(GROUP_ID, groupIdNew)
                .replaceAll(PACKAGE_NAME, packageNameNew)
                .replaceAll(ARTIFACT_ID, artifactIdNew) // 必须放在最后替换，因为 ARTIFACT_ID 太短！
                .replaceAll(StrUtil.upperFirst(ARTIFACT_ID), StrUtil.upperFirst(artifactIdNew))
                .replaceAll(TITLE, titleNew);
    }

    private static void writeFile(File file, String fileContent, String projectBaseDir,
                                  String projectBaseDirNew, String packageNameNew, String artifactIdNew) {
        String newPath = buildNewFilePath(file, projectBaseDir, projectBaseDirNew, packageNameNew, artifactIdNew);
        FileUtil.writeUtf8String(fileContent, newPath);
    }

    private static void copyFile(File file, String projectBaseDir,
                                 String projectBaseDirNew, String packageNameNew, String artifactIdNew) {
        String newPath = buildNewFilePath(file, projectBaseDir, projectBaseDirNew, packageNameNew, artifactIdNew);
        FileUtil.copyFile(file, new File(newPath));
    }

    private static String buildNewFilePath(File file, String projectBaseDir,
                                           String projectBaseDirNew, String packageNameNew, String artifactIdNew) {
        return file.getPath().replace(projectBaseDir, projectBaseDirNew) // 新目录
                .replace(PACKAGE_NAME.replaceAll("\\.", Matcher.quoteReplacement(separator)),
                        packageNameNew.replaceAll("\\.", Matcher.quoteReplacement(separator)))
                .replace(ARTIFACT_ID, artifactIdNew) //
                .replaceAll(StrUtil.upperFirst(ARTIFACT_ID), StrUtil.upperFirst(artifactIdNew));
    }

    private static String getFileType(File file) {
        return file.length() > 0 ? FileTypeUtil.getType(file) : "";
    }

}
