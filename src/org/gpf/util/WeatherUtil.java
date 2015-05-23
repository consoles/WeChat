package org.gpf.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.gpf.po.Weather;

public class WeatherUtil {

	@SuppressWarnings("unchecked")
	public static List<Weather> getWeather(String url) throws DocumentException, IOException{
		
		URL u = new URL(url);
		InputStream is = u.openStream();
		
		SAXReader reader = new SAXReader();
		Document document = reader.read(is);
		Element hubei = document.getRootElement();				// 获取根元素湖北
		Iterator<Element> iterator = hubei.elementIterator();	// 获取迭代器

		List<Weather> weathers = new ArrayList<Weather>(); 
		Weather weather = null;
		while (iterator.hasNext()) {
			Element city = iterator.next();
			List<Attribute> cityAttrs = city.attributes();		// 得到city节点的所有属性
			weather = new Weather();
			for (Attribute attr : cityAttrs) {
				String attrName = attr.getName();
				String attrValue = attr.getValue();
				if ("cityname".equals(attrName)) 
					weather.setCityname(attrValue);
				else if ("stateDetailed".equals(attrName)) 
					weather.setStateDetailed(attrValue);
				else if ("tem1".equals(attrName)) 
					weather.setHighTemp(Integer.parseInt(attrValue));
				else if ("tem2".equals(attrName)) 
					weather.setLowTemp(Integer.parseInt(attrValue));
				else if ("temNow".equals(attrName)) 
					weather.setAvgTemp(Integer.parseInt(attrValue));
				else if ("windState".equals(attrName)) 
					weather.setWindState(attrValue);
				else if ("windDir".equals(attrName)) 
					weather.setWindDir(attrValue);
				else if ("windPower".equals(attrName)) 
					weather.setWindPower(attrValue);
				else if ("humidity".equals(attrName)) 
					weather.setHumidity(attrValue);
				else if ("time".equals(attrName)) 
					weather.setTime(attrValue);
			}
			weathers.add(weather);
		}
		return weathers;
	}
}
