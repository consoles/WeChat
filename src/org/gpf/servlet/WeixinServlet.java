package org.gpf.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.DocumentException;
import org.gpf.util.CheckUtil;
import org.gpf.util.MessageUtil;
import org.gpf.util.WeatherUtil;
/**
 * 微信消息的接收和响应
 */
public class WeixinServlet extends HttpServlet {
	
	private static final long serialVersionUID = -3087246693383158316L;
	public static String WEATHER_API = "http://flash.weather.com.cn/wmaps/xml/hubei.xml";

	/**
	 * 接收微信服务器发送的4个参数并返回echostr
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// 接收微信服务器以Get请求发送的4个参数
		String signature = request.getParameter("signature");
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		String echostr = request.getParameter("echostr");
		
		PrintWriter out = response.getWriter();
		if (CheckUtil.checkSignature(signature, timestamp, nonce)) {
			out.print(echostr);		// 校验通过，原样返回echostr参数内容
		}
	}

	/**
	 * 接收并处理微信客户端发送的请求
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/xml;charset=utf-8");
		PrintWriter out = response.getWriter();
		try {
			Map<String, String> map = MessageUtil.xmlToMap(request);
			String toUserName = map.get("ToUserName");
			String fromUserName = map.get("FromUserName");
			String msgType = map.get("MsgType");
			String content = map.get("Content");
			
			String message = null;
			if (MessageUtil.MESSAGE_TEXT.equals(msgType)) {				// 对文本消息进行处理
				
				if(content.matches("^[1][0-6]$") || content.matches("^\\d{1}$")){
					int index = Integer.parseInt(content);
					message = MessageUtil.initText(fromUserName, toUserName, WeatherUtil.getWeather(WEATHER_API).get(index).toString());
				}else if ("?".equals(content) || "？".equals(content)) {
					message = MessageUtil.initText(fromUserName, toUserName, MessageUtil.menuText());
				}else if("图文消息".equals(content)){
					message = MessageUtil.initNewsMessage(fromUserName, toUserName);
				}else if("图片消息".equals(content)){
					message = MessageUtil.initImageMessage(fromUserName, toUserName);
				}else if("音乐".equals(content)){
					message = MessageUtil.initMusicMessage(fromUserName, toUserName);
				}else {
					message = MessageUtil.initText(fromUserName, toUserName, "没有您的城市/::D/::D/::D，您输入的内容是：\n" + content + "\n欢迎使用本订阅号。" + "\n当前时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + "<a href='http://www.cnblogs.com/happyfans/'>阅读几篇文章吧</a>");
				}
				
			}else if (MessageUtil.MESSAGE_EVENT.equals(msgType)) {	// 事件推送
				String eventType = map.get("Event");	// 到底是什么推送事件
				if (MessageUtil.MESSAGE_SUBSCRIBE.equals(eventType)) {
					message = MessageUtil.initText(fromUserName, toUserName, MessageUtil.menuText());
				} else if(MessageUtil.MESSAGE_CLICK.equals(eventType)){
					
					message = MessageUtil.initText(fromUserName, toUserName,"主菜单");
				} else if(MessageUtil.MESSAGE_VIEW.equals(eventType)){
					String url = map.get("EventKey");
					message = MessageUtil.initText(fromUserName, toUserName,url);
				} else if (MessageUtil.MESSAGE_SCANCODE.equals(eventType)) {
					String key = map.get("EventKey");
					message = MessageUtil.initText(fromUserName, toUserName,key);
				}
			} else if (MessageUtil.MESSAGE_LOCATION.equals(msgType)) {
				String label = map.get("Label");
				message = MessageUtil.initText(fromUserName, toUserName,label);
			}
			System.out.println(message);			
			out.print(message);							// 将回应发送给微信服务器
		} catch (DocumentException e) {
			e.printStackTrace();
		}finally{
			out.close();
		}
	}

}
