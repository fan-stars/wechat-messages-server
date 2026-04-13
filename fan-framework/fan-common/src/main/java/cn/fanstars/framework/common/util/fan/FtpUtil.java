package cn.fanstars.framework.common.util.fan;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * FTP工具类
 * <p>
 * 提供FTP连接、文件下载、目录创建等操作
 *
 * @author fanstars
 */
@Slf4j
@Getter
public class FtpUtil {

    private final FTPClient ftpClient;

    public FtpUtil() {
        this.ftpClient = new FTPClient();
    }

    /**
     * 连接并登录FTP服务器
     */
    public void connectAndLogin(String server, int port, String user, String pass) throws IOException {
        ftpClient.connect(server, port);
        ftpClient.login(user, pass);
        ftpClient.enterLocalPassiveMode(); // 被动模式，解决NAT/防火墙环境下连接问题
    }

    /**
     * 列出FTP目录下的文件
     */
    public FTPFile[] listFiles(String remoteDir) throws IOException {
        return ftpClient.listFiles(remoteDir);
    }

    /**
     * 下载FTP目录下的所有文件到本地
     */
    public void downloadFiles(String remoteDir, String localDir) throws IOException {
        FTPFile[] files = ftpClient.listFiles(remoteDir);
        for (FTPFile file : files) {
            if (file.isFile()) {
                String remoteFilePath = remoteDir + "/" + file.getName();
                String localFilePath = localDir + "/" + file.getName();
                try (OutputStream outputStream = Files.newOutputStream(Paths.get(localFilePath))) {
                    ftpClient.retrieveFile(remoteFilePath, outputStream);
                }
            }
        }
    }

    /**
     * 重命名FTP文件
     */
    public boolean rename(String oldName, String newName) throws IOException {
        return ftpClient.rename(oldName, newName);
    }

    /**
     * 移动单个文件到目标目录
     */
    public boolean moveFile(String remoteFilePath, String targetDir) throws IOException {
        ensureDirectoryExists(targetDir);
        return ftpClient.rename(remoteFilePath, targetDir);
    }

    /**
     * 移动目录下所有文件到目标目录
     */
    public boolean moveFiles(String remoteDir, String targetDir) throws IOException {
        FTPFile[] files = ftpClient.listFiles(remoteDir);
        ensureDirectoryExists(targetDir);
        for (FTPFile file : files) {
            if (file.isFile()) {
                String fileName = file.getName();
                if (fileName.startsWith("/")) {
                    fileName = fileName.substring(1); // 去除路径前的斜杠
                }
                String remoteFilePath = remoteDir + "/" + fileName;
                String backupFilePath = targetDir + "/" + fileName;
                return ftpClient.rename(remoteFilePath, backupFilePath);
            }
        }
        return false;
    }

    /**
     * 确保FTP目录存在，不存在则创建
     */
    public void ensureDirectoryExists(String dirPath) throws IOException {
        String[] directories = dirPath.split("/");
        StringBuilder pathBuilder = new StringBuilder();

        for (String dir : directories) {
            if (!dir.isEmpty()) {
                pathBuilder.append("/").append(dir);
                String currentPath = pathBuilder.toString();
                if (!ftpClient.changeWorkingDirectory(currentPath)) {
                    // 目录不存在则创建
                    if (!ftpClient.makeDirectory(currentPath)) {
                        throw new IOException("创建FTP目录失败: " + currentPath);
                    }
                }
            }
        }
    }

    /**
     * 删除FTP文件
     */
    public boolean deleteFile(String remoteFilePath) throws IOException {
        return ftpClient.deleteFile(remoteFilePath);
    }

    /**
     * 检查FTP目录是否存在
     */
    public boolean directoryExists(String pathname) {
        try {
            String[] directories = ftpClient.listNames(pathname);
            return (directories != null && directories.length > 0);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 递归创建多级FTP目录（支持"/a/b/c"格式）
     *
     * @param pathname 完整路径（如"/2025/05"）
     */
    public boolean makeDirectory(String pathname) throws IOException {
        if (pathname == null || pathname.trim().isEmpty()) {
            return false;
        }
        // 规范化路径：去除首尾斜杠
        String normalizedPath = pathname.replaceAll("^/+", "").replaceAll("/+$", "");
        if (normalizedPath.isEmpty()) {
            return false;
        }

        String[] dirs = normalizedPath.split("/");
        String currentPath = "";
        boolean success = true;

        for (String dir : dirs) {
            currentPath += "/" + dir;
            // 目录已存在则跳过
            if (directoryExists(currentPath)) {
                continue;
            }
            // 创建目录
            if (!ftpClient.makeDirectory(currentPath)) {
                int replyCode = ftpClient.getReplyCode();
                String replyMsg = ftpClient.getReplyString();
                log.warn("创建目录失败: {}, 响应码: {}, 响应消息: {}", currentPath, replyCode, replyMsg);
                success = false;
                break;
            }
        }
        return success;
    }

    /**
     * 断开FTP连接
     */
    public void disconnect() throws IOException {
        if (ftpClient.isConnected()) {
            ftpClient.logout();
            ftpClient.disconnect();
        }
    }

}
