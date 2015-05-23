package org.gpf.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.gpf.po.TextMeaasge;

import com.thoughtworks.xstream.XStream;

/**
 * 实现消息的格式转换(Map类型和XML的互转)
 * @author gaopengfei
 * @date 2015-5-22 上午10:40:41
 */
public class MessageUtil {

	public static final String MESSAGE_TEXT = "text";
	public static final String MESSAGE_IMAGE = "image";
	public static final String MESSAGE_VOICE = "voice";
	public static final String MESSAGE_VIDEO = "video";
	public static final String MESSAGE_LOCATION = "location";
	public static final String MESSAGE_LINK = "link";
	public static final String MESSAGE_EVENT = "event";
	public static final String MESSAGE_SUBSCRIBE = "subscribe";
	public static final String MESSAGE_UNSUBSCRIBE = "unsubscribe";
	public static final String MESSAGE_CLICK = "CLICK";
	public static final String MESSAGE_VIEW = "VIEW";
	
	/**
	 * 将XML转换成Map集合
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String>xmlToMap(HttpServletRequest request) throws IOException, DocumentException{
		
		Map<String, String> map = new HashMap<String, String>();
		SAXReader reader = new SAXReader();			// 使用dom4j解析xml
		InputStream ins = request.getInputStream(); // 从request中获取输入流
		Document doc = reader.read(ins);
		
		Element root = doc.getRootElement(); 		// 获取根元素
		List<Element> list = root.elements();		// 获取所有节点
		
		for (Element e : list) {
			map.put(e.getName(), e.getText()); 
			System.out.println(e.getName() + "--->" + e.getText());
		}
		ins.close();
		return map;
	}
	
	/**
	 * 将文本消息对象转换成XML
	 */
	public static String textMessageToXML(TextMeaasge textMessage){
		
		XStream xstream = new XStream();			  // 使用XStream将实体类的实例转换成xml格式	
		xstream.alias("xml", textMessage.getClass()); // 将xml的默认根节点替换成“xml”
		return xstream.toXML(textMessage);
		
	}
	
	/**
	 * 主菜单
	 * @return
	 */
	public static String menuText(){
		StringBuilder sb = new StringBuilder();
		sb.append("欢迎您的关注，本服务号为您提供湖北的天气预报信息\n\n");
		sb.append("0. 十堰\n");
		sb.append("1. 神农架\n");
		sb.append("2. 恩施\n");
		sb.append("3. 襄阳\n");
		sb.append("4. 宜昌\n");
		sb.append("5. 荆门\n");
		sb.append("6. 荆州\n");
		sb.append("7. 随州\n");
		sb.append("8. 天门\n");
		sb.append("9. 潜江\n");
		sb.append("10. 仙桃\n");
		sb.append("11. 孝感\n");
		sb.append("12. 武汉\n");
		sb.append("13. 咸宁\n");
		sb.append("14. 鄂州\n");
		sb.append("15. 黄石\n");
		sb.append("16. 黄冈\n");
		sb.append("\n请按照数字回复,回复？显示此帮助信息。");
		return sb.toString();
	} 
	
	/**
	 * 拼接文本消息
	 * @return
	 */
	public static String initText(String fromUserName,String toUserName,String content){
		
		TextMeaasge text = new TextMeaasge();
		text.setFromUserName(toUserName); 		// 发送和回复是反向的
		text.setToUserName(fromUserName);
		text.setMsgType(MESSAGE_TEXT);
		text.setCreateTime(new Date().getTime());
		text.setContent(content);
		return textMessageToXML(text);
		
	}
	
}