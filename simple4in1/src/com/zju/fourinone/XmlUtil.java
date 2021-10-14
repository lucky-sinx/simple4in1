package com.zju.fourinone;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * xml文件读取工具
 */
public class XmlUtil {
    private static final XmlReader XML_READER = new XmlReader();

    public static List<Properties> getXmlPropsByFile(String filePath, String PROPSROW_DESC, String KEY_DESC) {
        return XML_READER.getXmlPropsByFile(filePath, PROPSROW_DESC, KEY_DESC);
    }
}

class XmlHandler extends DefaultHandler {
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

    List<Properties> getProps() {
        return props;
    }

    void setPROPSROW_DESC(String PROPSROW_DESC) {
        this.PROPSROW_DESC = PROPSROW_DESC;
    }

    void setKEY_DESC(String KEY_DESC) {
        this.KEY_DESC = KEY_DESC;
    }
}

class XmlReader {
    List<Properties> getXmlPropsByFile(String filePath, String PROPSROW_DESC, String KEY_DESC) {
        List<Properties> props = new ArrayList<>();
        try {
            XmlHandler handler = new XmlHandler();
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            InputSource src = new InputSource(new FileInputStream(filePath));
            if (PROPSROW_DESC != null)
                handler.setPROPSROW_DESC(PROPSROW_DESC);
            if (KEY_DESC != null)
                handler.setKEY_DESC(KEY_DESC);
            saxParser.parse(src, handler);
            props = handler.getProps();
        } catch (IOException | ParserConfigurationException | SAXException e) {
            LogUtil.severe(String.format("[XmlUtil] [getXmlPropsByFile] read xml file(%s) fail\n%s",
                    filePath, e.getMessage()));
        }
        return props;
    }
}
