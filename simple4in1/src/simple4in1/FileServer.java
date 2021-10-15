package simple4in1;

public class FileServer extends FileUtil {
    private String host;

    public FileServer(String host) {
        this.host = host;
    }

    public void startFileServer(FileServer this) {
        RMIService.startFileServer(this);
    }
}
