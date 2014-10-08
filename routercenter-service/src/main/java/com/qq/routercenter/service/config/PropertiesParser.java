package com.qq.routercenter.service.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesParser {
	private Properties props;

	public PropertiesParser(String fileName) {
		try {
			props = getPropertiesFromClasspath(fileName);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Properties getPropertiesFromClasspath(String propFileName) throws IOException, FileNotFoundException {
		Properties props = new Properties();
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(propFileName);
		if (inputStream == null) {
			throw new FileNotFoundException("property file '" + propFileName + "' was not found in the classpath.");
		}
		try {
			props.load(inputStream);
		}
		finally {
			if (inputStream != null)
				inputStream.close();
		}
		return props;
	}

	@SuppressWarnings("unused")
	private Properties getPropertiesFromFile(String propFileName) throws IOException {
		Properties props = new Properties();
		File file = new File(propFileName);
		if (!file.exists()) {
			throw new FileNotFoundException("property file '" + file.getAbsolutePath() + "' not found in the classpath");
		}
		InputStream inputStream = new FileInputStream(file);
		try {
			props.load(inputStream);
		}
		finally {
			if (inputStream != null)
				inputStream.close();
		}
		return props;
	}

	public String getProperty(String key) {
		return props.getProperty(key);
	}

	public Map<String, String> getAllProperties() {
		Map<String, String> map = new HashMap<String, String>();
		Enumeration<?> enu = props.propertyNames();
		while (enu.hasMoreElements()) {
			String key = (String) enu.nextElement();
			String value = props.getProperty(key);
			map.put(key, value);
		}
		return map;
	}
}
