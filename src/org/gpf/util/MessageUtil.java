package org.gpf.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.ClientProtocolException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.gpf.po.Image;
import org.gpf.po.ImageMessage;
import org.gpf.po.Music;
import org.gpf.po.MusicMessage;
import org.gpf.po.News;
import org.gpf.po.NewsMessage;
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
	public static final String MESSAGE_NEWS = "news";
	public static final String MESSAGE_MUSIC = "music";
	public static final String MESSAGE_SCANCODE = "scancode_push";
	
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
	 * 图文消息转化为xml
	 * @param textMessage
	 * @return
	 */
	public static String newsMessageToXML(NewsMessage newsMessage){
		
		XStream xstream = new XStream();			  // 使用XStream将实体类的实例转换成xml格式	
		xstream.alias("xml", newsMessage.getClass()); // 将xml的默认根节点替换成“xml”
		xstream.alias("item", new News().getClass());
		return xstream.toXML(newsMessage);
		
	}
	
	/**
	 * 图片消息转化为xml
	 * @param textMessage
	 * @return
	 */
	public static String imageMessageToXML(ImageMessage imageMessage){
		
		XStream xstream = new XStream();			  // 使用XStream将实体类的实例转换成xml格式	
		xstream.alias("xml", imageMessage.getClass()); // 将xml的默认根节点替换成“xml”
		return xstream.toXML(imageMessage);
		
	}
	
	/**
	 * 音乐消息转化为xml
	 * @param imageMessage
	 * @return
	 */
	public static String musicMessageToXML(MusicMessage musicMessage){
		
		XStream xstream = new XStream();			  // 使用XStream将实体类的实例转换成xml格式	
		xstream.alias("xml", musicMessage.getClass()); // 将xml的默认根节点替换成“xml”
		return xstream.toXML(musicMessage);
		
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
	
	/**
	 * 组装图文消息
	 * @param fromUserName
	 * @param toUserName
	 * @return
	 */
	public static String initNewsMessage(String fromUserName,String toUserName){
		
		String message = null;
		List<News> newslList = new ArrayList<News>();
		NewsMessage newsMessage = new NewsMessage();
		News news = new News();
		news.setTitle("慕课介绍");
		news.setDescription("三点法第三  第三方三点飞屌丝方法三点飞三点 浮动是飞三点飞三点 发送的飞三点飞三点方法的是 发送的飞三点飞三点方法");
		news.setPicUrl("http://7xlan5.com1.z0.glb.clouddn.com/images/9-cells.jpg");
		news.setUrl("http://www.imooc.com");
		newslList.add(news);
		newsMessage.setFromUserName(toUserName);
		newsMessage.setToUserName(fromUserName);
		newsMessage.setCreateTime(new Date().getTime());
		newsMessage.setMsgType(MESSAGE_NEWS);
		newsMessage.setArticles(newslList);
		newsMessage.setArticleCount(newslList.size());
		message = newsMessageToXML(newsMessage);
		
		return message;
	}
	
	/**
	 * 组装图片消息
	 * @param fromUserName
	 * @param toUserName
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static String initImageMessage(String fromUserName,String toUserName) throws ClientProtocolException, IOException{
		
		String message = null;
		
		Image image = new Image();
		image.setMediaId("hYGDuLUjC8Cv5p3CxMcCrYh-Lh-X6-nkFsGNseYHIK7H6xIfeKimhPoz6wRKvcYC");
		ImageMessage imageMessage = new ImageMessage();
		
		imageMessage.setCreateTime(1444492282);
		imageMessage.setFromUserName(toUserName);
		imageMessage.setMsgType(MESSAGE_IMAGE);
		imageMessage.setImage(image);
		imageMessage.setToUserName(fromUserName);
		
		message = imageMessageToXML(imageMessage);
		return message;
	}
	
	/**
	 * 组装音乐消息
	 * @param fromUserName
	 * @param toUserName
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String initMusicMessage(String fromUserName,String toUserName) throws ClientProtocolException, IOException{
		
		String message = null;
		Music music = new Music();
		music.setDescription("一直很安静");
		music.setHQMusicUrl("http://7xlan5.com1.z0.glb.clouddn.com/images/%E8%91%A3%E8%B4%9E%20-%20%E6%A2%A6%E5%A4%AA%E6%99%9A.mp3");
		music.setMusicUrl("http://7xlan5.com1.z0.glb.clouddn.com/images/勿忘勿失.mp3");
		music.setThumbMediaId("szM5akH13oYo6v0-QeUtNkZI1_965DPJlNGC6Fg0uMc_glGf_K4JIPq8D8tOcrs4");
		music.setTitle("仙剑奇侠传");
		MusicMessage musicMessage = new MusicMessage();
		musicMessage.setCreateTime(new Date().getTime());
		musicMessage.setFromUserName(toUserName);
		musicMessage.setMsgType(MESSAGE_MUSIC);
		musicMessage.setMusic(music);
		musicMessage.setToUserName(fromUserName);
		
		message = musicMessageToXML(musicMessage);
		return message;
	}
}