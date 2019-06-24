package cn.im731.mall.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class FTPUtil {
    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpPort = PropertiesUtil.getProperty("ftp.server.port");
    private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static String ftpPassword = PropertiesUtil.getProperty("ftp.password");

    private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private String ip;
    private int port;
    private String user;
    private String password;
    private FTPClient ftpClient;


    public static boolean uploadFile(List<File> files) {
        FTPUtil ftpUtil = new FTPUtil(ftpIp, Integer.parseInt(ftpPort), ftpUser, ftpPassword);
        logger.info("开始连接FTP服务器");

        boolean result = ftpUtil.uploadFile("img", files);

        logger.info("FTP结束，结果：" + result);

        return result;

    }

    private boolean uploadFile(String remotePath, List<File> files) {
        boolean uploaded = true;
        FileInputStream fileInputStream = null;

        //连接FTP
        if (!connectServer(ip, port, user, password)) {
            return false;
        }
        try {
            ftpClient.changeWorkingDirectory(remotePath);
            ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            for (File file : files) {
                fileInputStream = new FileInputStream(file);
                ftpClient.storeFile(file.getName(), fileInputStream);
            }
        } catch (IOException e) {
            uploaded = false;
            logger.error("上传FTP文件异常", e);
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                uploaded = false;
                logger.error("关闭fileInputStream失败", e);
            }
            try {
                if (ftpClient != null) {
                    ftpClient.disconnect();
                }
            } catch (IOException e) {
                uploaded = false;
                logger.error("关闭ftpClient失败", e);
            }
        }
        return uploaded;
    }

    private boolean connectServer(String ip, int port, String user, String password) {
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user, password);
        } catch (IOException e) {
            logger.error("FTP连接失败", e);
        }

        return isSuccess;
    }


    public FTPUtil(String ip, int port, String user, String password) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    public static String getFtpIp() {
        return ftpIp;
    }

    public static String getFtpUser() {
        return ftpUser;
    }

    public static String getFtpPassword() {
        return ftpPassword;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
