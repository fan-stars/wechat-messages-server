package cn.fanstars;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public class FanProjectReactor {

    /**
     * 主函数，用于执行项目文件清理和更新操作
     * @param args 命令行参数数组
     */
    public static void main(String[] args) {
        // 获取项目基础目录
        File projectBaseDir = new File(ProjectReactor.PROJECT_BASE_DIR_NEW);
        File[] projectRootFiles = projectBaseDir.listFiles();
        if (projectRootFiles == null || projectRootFiles.length == 0) {
            log.info("[main][项目目录为空，请检查项目目录是否正确！程序退出]");
            return;
        }
        // 删除项目根目录下的文件
        removeFile(projectRootFiles);

        // 遍历所有项目文件并进行处理
        Collection<File> files = ProjectReactor.listFiles(ProjectReactor.PROJECT_BASE_DIR_NEW);
        files.forEach(file -> {
            String fileName = file.getName();
            // 更新文件中的artifactId
            updateArtifactId(file, fileName);
            // 删除以"《繁星"开头并以"入门》.md"结尾的文件
            if (fileName.startsWith("《繁星") && fileName.endsWith("入门》.md")) {
                boolean delete = FileUtil.del(file);
                log.info("fileName: {}, delete: {}", fileName, delete);
            } else {
                updateFileContent(file, fileName);
            }
        });

        // 更新框架模块名称
        updateFrameworkModuleName(projectRootFiles);
    }

    /**
     * 更新框架模块名称，将符合特定命名规则的模块重命名，并更新项目中所有引用该模块名的地方。
     *
     * @param projectRootFiles 项目的根目录文件数组，用于查找框架模块所在的目录
     */
    private static void updateFrameworkModuleName(File[] projectRootFiles) {
        // 查找以 ARTIFACT_ID_NEW + "-framework" 开头的项目根目录作为框架模型目录
        File frameworkModelDir = null;
        for (File projectRootFile : projectRootFiles) {
            if (projectRootFile.getName().startsWith(ProjectReactor.ARTIFACT_ID_NEW + "-framework")) {
                frameworkModelDir = projectRootFile;
                break;
            }
        }
        if (frameworkModelDir == null) {
            return;
        }

        // 获取框架模型目录下的所有子目录
        File[] frameworkDirs = frameworkModelDir.listFiles();
        if (frameworkDirs == null || frameworkDirs.length == 0) {
            return;
        }

        String ARTIFACT_ID_NEW = ProjectReactor.ARTIFACT_ID_NEW;
        Map<String, String> moduleNameMaps = new HashMap<>();

        // 遍历框架目录中的模块，对符合条件的模块进行重命名操作
        for (File frameworkDir : frameworkDirs) {
            String fileName = frameworkDir.getName();
            if (!fileName.startsWith(ARTIFACT_ID_NEW)) {
                continue;
            }
            String moduleNewName = "";
            if (fileName.startsWith(ARTIFACT_ID_NEW + "-spring-boot-starter")) {
                moduleNewName = fileName.replace("spring-boot-starter", "framework");
                FileUtil.rename(frameworkDir, moduleNewName, false);
                log.info("fileName: {}, rename: {}", fileName, moduleNewName);
                moduleNameMaps.put(fileName, moduleNewName);
            }
        }

        // 收集项目中所有文件并替换其中被修改模块的旧名称为新名称
        Collection<File> files = ProjectReactor.listFiles(ProjectReactor.PROJECT_BASE_DIR_NEW);
        for (File file : files) {
            String fileName = file.getName();
            String content = FileUtil.readString(file, StandardCharsets.UTF_8);
            boolean flag = false;

            // 检查当前文件是否包含需要替换的模块名称
            for (Map.Entry<String, String> entry : moduleNameMaps.entrySet()) {
                String moduleName = entry.getKey();
                String moduleNewName = entry.getValue();
                if (!content.contains(moduleName)) {
                    continue;
                }
                log.info("fileName: {} contain: {}, moduleNewName: {}, start write", fileName, moduleName, moduleNewName);
                content = content.replaceAll(moduleName, moduleNewName);
                flag = true;
            }

            // 如果有内容发生更改，则重新写入文件
            if (flag) {
                FileUtil.writeUtf8String(content, file);
            }
        }
    }

    /**
     * 删除指定项目根目录文件数组中的特定文件和目录
     *
     * @param projectRootFiles 项目根目录下的文件数组
     */
    private static void removeFile(File[] projectRootFiles) {
        // 定义需要删除的文件或目录名称集合
        Set<String> delFileOrDirNames = new HashSet<>(Arrays.asList(".gitee", ".github", ".image", "README.md", "LICENSE",
                ProjectReactor.ARTIFACT_ID_NEW + "-ui"));

        // 遍历项目根目录下的所有文件
        for (File projectRootFile : projectRootFiles) {
            String fileName = projectRootFile.getName();
            // 如果文件名在删除列表中，则执行删除操作
            if (delFileOrDirNames.contains(fileName)) {
                boolean delete = FileUtil.del(projectRootFile);
                log.info("fileName: {}, delete: {}", fileName, delete);
            // 特殊处理sql目录，只保留mysql子目录
            } else if (fileName.equals("sql") && projectRootFile.isDirectory()) {
                File[] sqlDirs = projectRootFile.listFiles();
                assert sqlDirs != null;
                // 遍历sql目录下的所有子目录
                for (File sqlDir : sqlDirs) {
                    String sqlDirName = sqlDir.getName();
                    // 删除非mysql的其他数据库目录
                    if (!sqlDirName.equals("mysql")) {
                        boolean delete = FileUtil.del(sqlDir);
                        log.info("fileName: {}, delete: {}", sqlDirName, delete);
                    }
                }
            }
        }
    }

    /**
     * 更新文件中的artifactId
     *
     * @param file 需要更新的文件对象
     * @param fileName 文件名称
     */
    private static void updateArtifactId(File file, String fileName) {
        // 只处理pom.xml文件
        if (fileName.equals("pom.xml")) {
            String content = FileUtil.readString(file, StandardCharsets.UTF_8);
            String artifactIdXml = "<artifactId>%s</artifactId>";
            String artifactIdText = String.format(artifactIdXml, ProjectReactor.ARTIFACT_ID_NEW);
            String artifactIdTextNew = String.format(artifactIdXml, ProjectReactor.PROJECT_NAME_NEW);

            // 检查文件内容是否包含指定的artifactId，如果包含则进行替换
            if (content.contains(artifactIdText)) {
                log.info("fileName: {} contain: {}, start write", fileName, ProjectReactor.ARTIFACT_ID_NEW);
                content = content.replaceAll(artifactIdText, artifactIdTextNew);
                FileUtil.writeUtf8String(content, file);
            }
        }
    }

    /**
     * 更新文件内容
     *
     * @param file 需要更新内容的文件对象
     * @param fileName 文件名称，用于日志输出
     */
    private static void updateFileContent(File file, String fileName) {
        // 读取文件内容
        String content = FileUtil.readString(file, StandardCharsets.UTF_8);
        Map<String, String> maps = new TreeMap<>();
        maps.put("繁星", "繁星");

        // 遍历替换规则，对文件内容进行替换
        for (Map.Entry<String, String> entry : maps.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            // 如果文件内容不包含需要替换的字符串，则直接返回
            if (!content.contains(key)) {
                return;
            }
            log.info("fileName: {} contain: {}, start write", fileName, key);
            // 执行字符串替换并写入文件
            content = content.replaceAll(key, value);
            FileUtil.writeUtf8String(content, file);
        }
    }

    /**
     * 更新模块名称
     * <p>
     * 该方法会遍历项目根目录下的所有文件，对符合特定命名规则的模块进行重命名操作，
     * 并更新所有文件中对这些模块名称的引用。
     *
     * @param projectRootFiles 项目根目录下的文件数组
     */
    private static void updateModuleName(File[] projectRootFiles) {
        String ARTIFACT_ID_NEW = ProjectReactor.ARTIFACT_ID_NEW;
        Map<String, String> moduleNameMaps = new HashMap<>();

        // 遍历项目根目录文件，识别并重命名符合规则的模块
        for (File projectRootFile : projectRootFiles) {
            String fileName = projectRootFile.getName();
            if (!fileName.startsWith(ARTIFACT_ID_NEW)) {
                continue;
            }
            String moduleNewName = "";
            if (fileName.startsWith(ARTIFACT_ID_NEW + "-module")) {
                moduleNewName = fileName.replace("module", "service");
            } else if (fileName.startsWith(ARTIFACT_ID_NEW + "-server")) {
                moduleNewName = fileName.replace("server", "application");
            }
            if (StringUtils.hasLength(moduleNewName)) {
                FileUtil.rename(projectRootFile, moduleNewName, false);
                log.info("fileName: {}, rename: {}", fileName, moduleNewName);
                moduleNameMaps.put(fileName, moduleNewName);
            }
        }

        // 遍历所有项目文件，更新模块名称的引用
        Collection<File> files = ProjectReactor.listFiles(ProjectReactor.PROJECT_BASE_DIR_NEW);
        for (File file : files) {
            String fileName = file.getName();
            String content = FileUtil.readString(file, StandardCharsets.UTF_8);
            boolean flag = false;

            // 检查并替换文件中包含的旧模块名称
            for (Map.Entry<String, String> entry : moduleNameMaps.entrySet()) {
                String moduleName = entry.getKey();
                String moduleNewName = entry.getValue();
                if (!content.contains(moduleName)) {
                    continue;
                }
                log.info("fileName: {} contain: {}, start write", fileName, moduleName);
                content = content.replaceAll(moduleName, moduleNewName);
                flag = true;
            }

            // 如果文件内容有修改，则写回文件
            if (flag) {
                FileUtil.writeUtf8String(content, file);
            }
        }
    }

}
