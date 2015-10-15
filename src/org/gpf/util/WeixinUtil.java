package org.gpf.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.gpf.menu.Button;
import org.gpf.menu.ClickButton;
import org.gpf.menu.Menu;
import org.gpf.menu.ViewButton;
import org.gpf.po.AccessToken;
import org.gpf.trans.Data;
import org.gpf.trans.Parts;
import org.gpf.trans.Symbols;
import org.gpf.trans.TransResult;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

@SuppressWarnings("deprecation")
public class WeixinUtil {

	static final String APPID = "wx51dd645678862b2a";
	static final String APPSECRET = "429d87cc6a85c3eb29898a92160034f1";
	static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	static final String UPLOAD_URL = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";
	static final String CREATE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
	static final String QUERY_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=ACCESS_TOKEN";
	static final String DELETE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN";
	
	/**
	 * get请求
	 * @param url
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static JSONObject doGetStr(String url) throws ClientProtocolException, IOException{
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		JSONObject jsonObject = null;
		HttpResponse response = httpClient.execute(httpGet);
		HttpEntity entity = response.getEntity();
		if (entity!=null) {
			String result = EntityUtils.toString(entity,"utf-8");
			jsonObject = JSONObject.parseObject(result);
		}
		return jsonObject;
	}
	
	/**
	 * post方法
	 * @param url
	 * @param outStr
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static JSONObject doPostStr(String url,String outStr) throws ClientProtocolException, IOException{
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new StringEntity(outStr, "utf-8"));
		HttpResponse response = httpClient.execute(httpPost);
		String result = EntityUtils.toString(response.getEntity(),"utf-8");
		return JSONObject.parseObject(result);
	}
	
	/**
	 * 获取access token
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static AccessToken getAccessToken() throws ClientProtocolException, IOException{
		AccessToken accessToken = new AccessToken();
		String url = ACCESS_TOKEN_URL.replace("APPID", APPID).replace("APPSECRET", APPSECRET);
		JSONObject jsonObject = doGetStr(url);
		if (jsonObject != null) {
			accessToken.setExpriesIn(jsonObject.getIntValue("expires_in"));
			accessToken.setToken(jsonObject.getString("access_token"));
		}
		return accessToken;
	}
	
	@Test
	public void testAccessToken() throws Exception{
		AccessToken accessToken = WeixinUtil.getAccessToken();
		System.out.println("token:" + accessToken.getToken());
		System.out.println("有效时间：" + accessToken.getExpriesIn());
		
		// 测试图片上传
		String path = "s14.jpg";
		String media_id = WeixinUtil.upload(path, accessToken.getToken(), "image");
		System.out.println(media_id);
		
		// 测试缩略图
		path = "测试号.jpg";
		String thumb_id = WeixinUtil.upload(path, accessToken.getToken(), "thumb");
		System.out.println(thumb_id);
		
		// 测试自定义菜单
		String menu = JSONObject.toJSONString(WeixinUtil.initMenu());
		int result = WeixinUtil.createMenu(accessToken.getToken(), menu);
		if (result == 0) {
			System.out.println("创建菜单成功！");
		} else {
			System.out.println("错误码" + result);
		}
		
		// 测试菜单查询
		System.out.println(WeixinUtil.queryMenu(accessToken.getToken()));
		
		// 测试菜单删除
		System.out.println(WeixinUtil.deleteMenu(accessToken.getToken()));
		
//		// 测试百度词典
//		System.out.println(WeixinUtil.translate("足球"));
//		
//		// 测试百度翻译
//		System.out.println(WeixinUtil.translateFull("见到你真好"));
		
		System.out.println(WeixinUtil.translate("见到你真好"));
	}
	
	public static String upload(String filePath,String accessToken,String type) throws IOException{
		File file = new File(filePath);
		if (!file.isFile() || !file.exists()) {
			throw new IOException("文件不存在！");
		}
		
		String url = UPLOAD_URL.replace("ACCESS_TOKEN", accessToken).replace("TYPE", type);
		
		URL urlObject = new URL(url);
		// 连接
		HttpURLConnection conn = (HttpURLConnection) urlObject.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		
		// 设置请求头
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.setRequestProperty("Charset", "utf-8");
		
		// 设置边界
		String BOUNDARY = "--------" + System.currentTimeMillis();
		conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
		
		StringBuilder sb = new StringBuilder();
		sb.append("--");
		sb.append(BOUNDARY);
		sb.append("\r\n");
		sb.append("Content-Disposition:form-data;name=\"file\",filename=\"" + file.getName() +"\"\r\n");
		sb.append("Content-Type:application/octet-stream\r\n\r\n");
		
		byte[] head = sb.toString().getBytes("utf-8");
		
		// 获得输出流并输出表头
		OutputStream out = new DataOutputStream(conn.getOutputStream());
		out.write(head);
		
		// 文件正文部分(将文件以流的方式推入到url中) 
		DataInputStream in = new DataInputStream(new FileInputStream(file));
		int bytes = 0;
		byte[] bufferOut = new byte[1024];
		while ((bytes = in.read(bufferOut)) != -1) {
			out.write(bufferOut,0,bytes);
		}
		in.close();
		
		// 结尾部分
		byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");
		out.write(foot);
		
		out.flush();
		out.close();
		
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = null;
		String result = null;
		
		// 读取url响应
		reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line = null;
		while ((line = reader.readLine()) != null) {
			buffer.append(line);
		}
		if (result == null) {
			result = buffer.toString();
		}
		reader.close();
		
		JSONObject jsonObject = JSONObject.parseObject(result);
		System.out.println(jsonObject);
		String typeName = "media_id";
		if(!"image".equals(type)){
			typeName = type + "_media_id";
		}
		String media_id = jsonObject.getString(typeName);
		
		return media_id;
	}
	
	/**
	 * 组装菜单
	 * @return
	 */
	public static Menu initMenu(){
		Menu menu = new Menu();
		ClickButton button11 = new ClickButton();
		button11.setName("点击菜单");
		button11.setType("click");
		button11.setKey("11");
		
		ViewButton button21 = new ViewButton();
		button21.setName("view菜单");
		button21.setType("view");
		button21.setUrl("http://www.imooc.com");
		
		ClickButton button31 = new ClickButton();
		button31.setName("扫码");
		button31.setType("scancode_push");
		button31.setKey("31");
		
		ClickButton button32 = new ClickButton();
		button32.setName("地理位置");
		button32.setType("location_select");
		button32.setKey("41");
		
		Button button = new Button();
		button.setName("二级菜单");
		button.setSub_button(new Button[]{button31,button32});
		
		menu.setButton(new Button[]{button11,button21,button});
		
		return menu;
	}
	
	public static int createMenu(String token,String menu) throws ClientProtocolException, IOException{
		String url = CREATE_MENU_URL.replace("ACCESS_TOKEN", token);
		int result = 0;
		JSONObject jsonObject = doPostStr(url, menu);
		if (jsonObject != null) {
			result = jsonObject.getIntValue("errcode");
		}
		return result;
	}
	
	public static JSONObject queryMenu(String token) throws ClientProtocolException, IOException{
		String url = QUERY_MENU_URL.replace("ACCESS_TOKEN", token);
		JSONObject jsonObject = doGetStr(url);
		return jsonObject;
	}
	
	public static int deleteMenu(String token) throws ClientProtocolException, IOException{
		String url = DELETE_MENU_URL.replace("ACCESS_TOKEN", token);
		JSONObject jsonObject = doGetStr(url);
		int result = jsonObject.getIntValue("errcode");
		return result;
		
	}
	
	public static String translate(String source) throws Exception{
		String url = "http://openapi.baidu.com/public/2.0/translate/dict/simple?client_id=V7wojvRxG9EQlYeeCoQD9Xwf&q=KEYWORD&from=auto&to=auto";
		url = url.replace("KEYWORD", URLEncoder.encode(source,"utf-8"));
		JSONObject jsonObject = doGetStr(url);
		String errno = jsonObject.getString("errno");
		Object obj = jsonObject.get("data");
		StringBuilder dst = new StringBuilder();
		if ("0".equals(errno) && !"[]".equals(obj.toString())) {
			TransResult transResult = JSONObject.toJavaObject(jsonObject, TransResult.class);
			Data data = transResult.getData();
			Symbols symbols = data.getSymbols()[0];
			
			String ph_zh = symbols.getPh_zh() == null?"":"中文拼音:" + symbols.getPh_zh();
			String ph_en = symbols.getPh_en() == null?"":"英式音标:" + symbols.getPh_en();
			String ph_am = symbols.getPh_am() == null?"":"美式音标:" + symbols.getPh_am();
			dst.append(ph_zh + ph_en + ph_am);
			
			Parts[] parts = symbols.getParts();
			String pat = null;
			for (Parts p : parts) {
				pat = (p.getParts()!=null && "".equals(p.getParts()))?"["+p.getParts() +"]":"";
				String[] means = p.getMeans();
				for (String mean : means) {
					dst.append(mean + ";");
				}
			}
		}else {
			dst.append(translateFull(source));
		}
		return dst.toString();
	}
	
	public static String translateFull(String source) throws  Exception{
		String url = "http://openapi.baidu.com/public/2.0/bmt/translate?client_id=V7wojvRxG9EQlYeeCoQD9Xwf&q=KEYWORD&from=auto&to=auto";
		url = url.replace("KEYWORD", URLEncoder.encode(source,"utf-8"));
		JSONObject jsonObject = doGetStr(url);
		StringBuilder dst = new StringBuilder();
		List<Map> list = (List<Map>) jsonObject.get("trans_result");
		for (Map map : list) {
			dst.append(map.get("dst"));
		}
		return dst.toString();
	}
}
