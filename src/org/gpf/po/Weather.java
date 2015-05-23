package org.gpf.po;

public class Weather {

	private String cityname; 		// 城市名
	private String stateDetailed; 	// 天气详情
	private int lowTemp; 			// 最低温度
	private int highTemp;		 	// 最高温度
	private int avgTemp; 			// 平均温度
	private String windState; 		// 风
	private String windDir; 		// 风向
	private String windPower; 		// 风力
	private String humidity; 		// 湿度
	private String time; 			// 更新时间

	public String getCityname() {
		return cityname;
	}

	public void setCityname(String cityname) {
		this.cityname = cityname;
	}

	public String getStateDetailed() {
		return stateDetailed;
	}

	public void setStateDetailed(String stateDetailed) {
		this.stateDetailed = stateDetailed;
	}

	public int getLowTemp() {
		return lowTemp;
	}

	public void setLowTemp(int lowTemp) {
		this.lowTemp = lowTemp;
	}

	public int getHighTemp() {
		return highTemp;
	}

	public void setHighTemp(int highTemp) {
		this.highTemp = highTemp;
	}

	public int getAvgTemp() {
		return avgTemp;
	}

	public void setAvgTemp(int avgTemp) {
		this.avgTemp = avgTemp;
	}

	public String getWindState() {
		return windState;
	}

	public void setWindState(String windState) {
		this.windState = windState;
	}

	public String getWindDir() {
		return windDir;
	}

	public void setWindDir(String windDir) {
		this.windDir = windDir;
	}

	public String getWindPower() {
		return windPower;
	}

	public void setWindPower(String windPower) {
		this.windPower = windPower;
	}

	public String getHumidity() {
		return humidity;
	}

	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Override
	public String toString() {
		
		String weatherData = "城市名：" + cityname + ",天气详情：" + stateDetailed + "\n最低温度:" + lowTemp + "，最高温度：" + highTemp + ",平均温度：" + avgTemp
				+ "\n风：" + windState + "，风向：" + windDir + "，风力：" + windPower + "，湿度：" + humidity + "\n天气数据更新于" + time + "<a href='http://www.weather.com.cn/weather/101200501.shtml'>点击查看详情</a>";
		
		return weatherData;
	}

}
