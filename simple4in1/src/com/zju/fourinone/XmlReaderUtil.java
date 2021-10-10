package com.zju.fourinone;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * xml文件读取
 */
public class XmlReaderUtil {
    public List<Properties> getXmlPropsByFile(String filePath, String PROPSROW_DESC, String KEY_DESC) {
        List<Properties> props = new ArrayList<>();
        try {
            XmlHandlerUtil handler = new XmlHandlerUtil();
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
            LogUtil.severe("[XmlUtil] [getXmlPropsByFile] " + e.getClass()+": " + e.getMessage());
        }
        return props;
    }
}
