package com.qq.routercenter.service.config;

public class Configuration {
	private static PropertiesParser props;
	static {
		String configMode = System.getProperty("CONFIG_MODE");
		String configFile;
		if(configMode != null && !"".equals(configMode)){
			configFile = "routercenter-" + configMode.toLowerCase() + ".properties";
		}else{
			configFile = "routercenter.properties";
		}
		props = new PropertiesParser(configFile);
	}
	
	public static String getProperty(String key){
		return props.getProperty(key);
	}
}
