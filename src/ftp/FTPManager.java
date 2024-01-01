package ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class FTPManager {
    //private static final String ftp_server = "192.168.206.130";
    public static String ftp_server = null;  //由连接场景的输入来设置要连接的FTP服务器地址

    private static final int ftp_port = 21;  //FTP服务器端口号
    private static final String ftp_user = "sc";  //FTP服务器事先注册好的用户
    private static final String ftp_passwd = "123";  //FTP服务器用户密码
    private static final int timeout = 5000;  //连接超时时间
    private static final String encoding = "utf-8";  //编码

    public static FTPManager ftpManager=new FTPManager();  //公共ftpManager，就不用多次连接FTP服务器

    public static FTPClient client;

    public FTPManager() {
        client = new FTPClient();
    }

    public void connect() throws Exception {
        client.connect(ftp_server, ftp_port);  //连接FTP服务器
        client.login(ftp_user, ftp_passwd);  //登录
        client.setConnectTimeout(timeout);  //设置超时时间
        client.setControlEncoding(encoding);  //设置编码
        client.enterLocalPassiveMode();  //被动模式传输
        client.setBufferSize(2048);  //设置缓冲区大小

        if (client.getReplyCode()==230){  //成功连接FTP服务器会返回230
            System.out.println("【FTP连接】状态码"+client.getReplyCode());
            System.out.println("成功连接FTP服务器！");
        }
    }

    //返回FTP服务器某目录下的所有文件或文件夹
    public FTPFile[] listFiles(String path) throws Exception {
        return client.listFiles(path);
    }

    //注销
    public void disconnect() throws Exception {
        if (client.isConnected()) {  //如果FTP还在连接
            client.abort();  //中断当前的FTP传输
            client.logout();  //注销
            //client.disconnect();
        }
    }
}
