package XMLUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;

public class XMLUtil
{

    private static  DocumentBuilderFactory dFactory;
    private static DocumentBuilder builder;
    private static Document doc;
    static {
        try {
            dFactory = DocumentBuilderFactory.newInstance();
            builder = dFactory.newDocumentBuilder();
            doc = builder.parse(new File("config.xml"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String[] getHdfsConf() throws Exception {

        String[] strings=new String[2];

        NodeList nl = doc.getElementsByTagName("ip");
        Node classNode = nl.item(0).getFirstChild();
        String ip = classNode.getNodeValue();
        strings[0]=ip;

        nl=doc.getElementsByTagName("port");
        classNode=nl.item(0).getFirstChild();
        String port=classNode.getNodeValue();
        strings[1]=port;

        return strings;
    }

    public static String getRocktMQConf() throws Exception {
        return getContent("RocketMQ_config");
    }

    public static Object getFileProcessClass(String tagName) throws Exception {

        NodeList nl=doc.getElementsByTagName(tagName);
        Node classNode=nl.item(0).getFirstChild();
        String className=classNode.getNodeValue();

        Class c=Class.forName(className);
        Object obj=c.newInstance();

        return obj;
    }

    public static String getTemDir() throws Exception {
       return getContent("temDir");
    }

    public static String getSensiveWordsDir() throws Exception {
       return getContent("sensiveWordsDir");
    }

    public static String getReplacePicPath() throws Exception {
        return getContent("replacePic");
    }

    public static String getContent(String tag) throws Exception {
        NodeList nl = doc.getElementsByTagName(tag);
        Node classNode = nl.item(0).getFirstChild();
        return classNode.getNodeValue();
    }



}
