package com.qq.routercenter.client.bus;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class RouteFileManager {
	public static void write(Object cacheObj, OutputStream os) {
		try{
			JAXBContext jaxbContext = JAXBContext.newInstance(cacheObj.getClass());
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(cacheObj, os);
		}catch(JAXBException e){
			throw new RouteFileException(e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T read(Class<T> objClass, InputStream is){
		try{
			JAXBContext jaxbContext = JAXBContext.newInstance(objClass);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			return (T) jaxbUnmarshaller.unmarshal(is);
		}catch(JAXBException e){
			throw new RouteFileException(e.getMessage());
		}
	}
}
