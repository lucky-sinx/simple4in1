package simple4in1;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * 理解：
 * =ConfigContext
 */
public class Config {
    private final static String configFile = "config.xml";

    private static String getConfig(String name, String prop) {
        XmlReaderUtil xmlUtil = new XmlReaderUtil();
        List<Properties> propsList = xmlUtil.getXmlPropsByFile(configFile, name, null);
        String res = null;
        if (propsList != null && propsList.size() > 0) {
            Properties props = propsList.get(0);
            res = props.getProperty(prop);
        }

        return res;
    }

    public static String getParkHost() {
        return getConfig("PARK", "HOST");
    }

    public static int getParkPort() {
        return Integer.parseInt(getConfig("PARK", "PORT"));
    }

    public static String getParkName() {
        return getConfig("PARK", "NAME");
    }

    public static Long getHeartbeatTime(){return Long.parseLong(getConfig("PARK","HEARTBEATTIME"));}

    public static String getWorkerHost() {
        return getConfig("WORKER", "HOST");
    }

    public static int getWorkerPort() {
        return Integer.parseInt(getConfig("WORKER", "PORT"));
    }

    public static String getWebHost() {
        return getConfig("WEB", "HOST");
    }

    public static int getWebPort() {
        return Integer.parseInt(getConfig("WEB", "PORT"));
    }

    public static String[] getWebUsers() {
        return getConfig("WEB", "USERS").split(",");
    }

    public static String getFileSystemLBHost() {
        return getConfig("FILESYSTEM", "LBHOST");
    }

    public static int getFileSystemLBPort() {
        return Integer.parseInt(getConfig("FILESYSTEM", "PORT"));
    }

    public static String getFileSystemLBName() {
        return getConfig("FILESYSTEM", "LBNAME");
    }

    public static List<String> getFileSystemServers() {
        return Arrays.asList(getConfig("FILESYSTEM", "SERVERS").split(","));
    }

}
