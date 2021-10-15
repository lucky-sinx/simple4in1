package simple4in1;

import java.util.*;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

public class XmlHandlerUtil extends DefaultHandler {
    private boolean textFlag = false;
    private List<Properties> props;
    private Properties curProps;
    private String curKey;
    private String PROPSROW_DESC;
    private String KEY_DESC;
    private String curPROPSROW_DESC;
    private String curKEY_DESC;

    @Override
    public void startElement(String uri, String sName, String qName, Attributes attrs) {
        if (qName.equals("PROPSTABLE")) {
            props = new ArrayList<>();
        } else if (qName.equals("PROPSROW")) {
            curPROPSROW_DESC = attrs.getValue("DESC");
            curProps = new Properties();
        } else {
            curKEY_DESC = attrs.getValue("DESC");
            curKey = qName;
            textFlag = true;
        }
    }

    @Override
    public void characters(char[] data, int start, int length) {
        String content = new String(data, start, length);
        if (textFlag) {
            if (KEY_DESC == null || (curKEY_DESC != null && curKEY_DESC.equals(KEY_DESC)))
                curProps.put(curKey, content.trim());
        }
    }

    @Override
    public void endElement(String uri, String sName, String qName) {
        if (qName.equals("PROPSROW")) {
            if (PROPSROW_DESC == null || (curPROPSROW_DESC != null && curPROPSROW_DESC.equals(PROPSROW_DESC)))
                props.add(curProps);
        } else if (!qName.equals("PROPSTABLE")) {
            textFlag = false;
        }
    }

    public List<Properties> getProps() {
        return props;
    }

    public void setPROPSROW_DESC(String PROPSROW_DESC) {
        this.PROPSROW_DESC = PROPSROW_DESC;
    }

    public void setKEY_DESC(String KEY_DESC) {
        this.KEY_DESC = KEY_DESC;
    }
}