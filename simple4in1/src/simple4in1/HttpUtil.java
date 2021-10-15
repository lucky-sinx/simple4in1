package simple4in1;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * http页面访问工具
 */
public class HttpUtil {

    /**
     * 启动HttpServer
     *
     * @param maxConnections: 最大连接数，<=0取默认值
     */
    public static void startHttpServer(String host, int port, int maxConnections) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(host, port), maxConnections);
        httpServer.createContext("/", new SearchHandler());
        httpServer.createContext("/download", new DownloadHandler());
        httpServer.createContext("/login", new LoginHandler());
        httpServer.setExecutor(Executors.newCachedThreadPool());
        httpServer.start();
    }
}

/**
 * Handler
 * Content-Type对照表: https://tool.oschina.net/commons
 * Content-Type常用格式类型:
 * text/html                           : HTML格式
 * text/plain                          : 纯文本格式
 * text/xml                            : XML格式
 * image/gif                           : gif图片格式
 * image/jpeg                          : jpg图片格式
 * image/png                           : png图片格式
 * application/x-download              : ???
 * application/xhtml+xml               : XHTML格式
 * application/xml                     : XML数据格式
 * application/atom+xml                : Atom XML聚合格式
 * application/json                    : JSON数据格式
 * application/pdf                     : pdf格式
 * application/msword                  : Word文档格式
 * application/octet-stream            : 二进制流数据（如常见的文件下载）
 * application/x-www-form-urlencoded   : <form encType=””>中默认的encType，form表单数据被编码为key/value格式发送到服务器（表单默认的提交数据的格式）
 * multipart/form-data                 : 需要在表单中进行文件上传时，就需要使用该格式
 * sendResponseHeaders参数:
 * responseLength – if > 0, specifies a fixed response body length and that exact number of bytes must be written to the stream acquired from getResponseBody(),
 * or else if equal to 0, then chunked encoding is used, and an arbitrary number of bytes may be written.
 * if <= -1, then no response body length is specified and no response body may be written.
 */
    abstract class Handler implements HttpHandler {
    public abstract void handle(HttpExchange exchange) throws IOException;

    /**
     * NotFound: 404
     *
     * @param exchange: 请求
     * @throws IOException: IOException
     */
    void notFound(HttpExchange exchange) throws IOException {
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.set("Content-Type", "text/html");
        File responseFile = new File("err404.jsp");
        FileInputStream responseFileInputStream = new FileInputStream(responseFile);
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, responseFile.length());
        OutputStream os = exchange.getResponseBody();
        byte[] buffer = new byte[]{0};
        while (responseFileInputStream.read(buffer, 0, buffer.length) != -1) {
            os.write(buffer);
        }
        responseFileInputStream.close();
        os.close();
    }

    /**
     * Unauthorized: 401
     *
     * @param exchange: 请求
     * @throws IOException: IOException
     */
    void unauthorized(HttpExchange exchange) throws IOException {
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.set("Content-Type", "text/html");
        File responseFile = new File("err401.jsp");
        FileInputStream responseFileInputStream = new FileInputStream(responseFile);
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_UNAUTHORIZED, responseFile.length());
        OutputStream os = exchange.getResponseBody();
        byte[] buffer = new byte[]{0};
        while (responseFileInputStream.read(buffer, 0, buffer.length) != -1) {
            os.write(buffer);
        }
        responseFileInputStream.close();
        os.close();
    }
}

/**
 * 文件访问Handler
 */
class SearchHandler extends Handler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String filePath = exchange.getRequestURI().getPath();
        File file = new File(filePath);
        if (!file.exists()) {
            notFound(exchange);
            return;
        }
        FileInputStream fileInputStream = new FileInputStream(file);
        String requestMethod = exchange.getRequestMethod();
        if (requestMethod.equalsIgnoreCase("GET")) {
            Headers responseHeaders = exchange.getResponseHeaders();
            responseHeaders.set("Content-Type", "text/plain");
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, file.length());
            OutputStream os = exchange.getResponseBody();
            byte[] buffer = new byte[]{0};
            while (fileInputStream.read(buffer, 0, buffer.length) != -1) {
                os.write(buffer);
            }
            fileInputStream.close();
            os.close();

        }
    }
}

/**
 * 文件下载Handler
 */
class DownloadHandler extends Handler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String filePath = exchange.getRequestURI().getPath().substring("/download/".length());
        File file = new File(filePath);
        if (!file.exists()) {
            notFound(exchange);
            return;
        }
        FileInputStream fileInputStream = new FileInputStream(file);
        String requestMethod = exchange.getRequestMethod();
        if (requestMethod.equalsIgnoreCase("GET")) {
            Headers responseHeaders = exchange.getResponseHeaders();
            responseHeaders.set("Content-Type", "application/x-download");
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, file.length());
            OutputStream os = exchange.getResponseBody();
            byte[] buffer = new byte[]{0};
            while (fileInputStream.read(buffer, 0, buffer.length) != -1) {
                os.write(buffer);
            }
            fileInputStream.close();
            os.close();
        }
    }
}

/**
 * 登录Handler
 */
class LoginHandler extends Handler {
    private static final byte[] codes = new byte[256];

    static {
        for (int i = 0; i < 256; i++) codes[i] = -1;
        for (int i = 'A'; i <= 'Z'; i++) codes[i] = (byte) (i - 'A');
        for (int i = 'a'; i <= 'z'; i++) codes[i] = (byte) (26 + i - 'a');
        for (int i = '0'; i <= '9'; i++) codes[i] = (byte) (52 + i - '0');
        codes['+'] = 62;
        codes['/'] = 63;
    }

    /**
     * 请求头Authorization字段解析
     *
     * @param data: 要解析的数据
     * @return: 解析的结果
     */
    private static byte[] decode(char[] data) {
        int len = ((data.length + 3) / 4) * 3;
        if (data.length > 0 && data[data.length - 1] == '=') --len;
        if (data.length > 1 && data[data.length - 2] == '=') --len;
        byte[] out = new byte[len];
        int shift = 0, accum = 0, index = 0;
        for (char datum : data) {
            int value = codes[datum & 0xFF];
            if (value >= 0) {
                accum <<= 6;
                shift += 6;
                accum |= value;
                if (shift >= 8) {
                    shift -= 8;
                    out[index++] = (byte) ((accum >> shift) & 0xff);
                }
            }
        }
        if (index != out.length) throw new Error("miscalculated data length!");
        return out;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean isWebUser = false;
        Headers requestHeaders = exchange.getRequestHeaders();
        List<String> auth = requestHeaders.get("Authorization");
        Headers responseHeaders = exchange.getResponseHeaders();
        OutputStream os = exchange.getResponseBody();
        if (auth != null) {
            String user = new String(decode(auth.get(0).split(" ")[1].toCharArray()));
            String[] webUsers = Config.getWebUsers();
            for (String webUser : webUsers) {
                if (user.equals(webUser)) {
                    isWebUser = true;
                    break;
                }
            }

            if (isWebUser) {
                responseHeaders.set("Content-Type", "text/plain");
                String response = "login success!";
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
                os.write(response.getBytes());
                os.close();
                return;
            }
        } else {
            responseHeaders.set("WWW-Authenticate", "Basic realm='Fttp Admin'");
        }
        unauthorized(exchange);
    }
}
