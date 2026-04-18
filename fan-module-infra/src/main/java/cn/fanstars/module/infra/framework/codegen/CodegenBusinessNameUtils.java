package cn.fanstars.module.infra.framework.codegen;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 代码生成业务名（{@code businessName}）工具：支持多级目录（路径多级、Java 包多级）。
 *
 * @author fan
 */
public final class CodegenBusinessNameUtils {

    private CodegenBusinessNameUtils() {
    }

    /**
     * 规范为文件路径形式：使用 {@code /} 分隔多级，统一 {@code \}、{@code .} 为层级分隔并去除空白段。
     */
    public static String normalizeToPath(String raw) {
        if (StrUtil.isBlank(raw)) {
            return "";
        }
        String s = StrUtil.trim(raw).replace('\\', '/');
        s = StrUtil.replace(s, ".", "/");
        List<String> segments = StrUtil.splitTrim(s, '/').stream()
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toList());
        return CollUtil.isEmpty(segments) ? "" : CollUtil.join(segments, "/");
    }

    /**
     * 将路径形式的业务名转为 Java 包名片段（{@code /} → {@code .}）。
     */
    public static String toPackage(String pathForm) {
        if (StrUtil.isBlank(pathForm)) {
            return "";
        }
        return pathForm.replace('/', '.');
    }

}
