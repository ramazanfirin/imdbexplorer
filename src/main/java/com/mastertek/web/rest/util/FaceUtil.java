package com.mastertek.web.rest.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Date;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FaceUtil {

	private static Logger log = LoggerFactory.getLogger(FaceUtil.class);
	
	
	public static Boolean isUrl(String path) {
		if(path.startsWith("http")) {
			return true;
		}else {
			return false;
		}
	}
	
	public static File getFile(String path) throws MalformedURLException {
		File file;
		if(isUrl(path)) {
			URL url = new URL(path);
			file = new File(url.getFile());
		}else
			file = new File(path);
		
		return file;
	}
	
	public static Instant getCreateDate(String path) throws MalformedURLException {
		File file = getFile(path);
		String name = file.getName();
		String[] arg = name.split("_");
		String date = arg[3].replace(".jpg", "").replace(".png", "");
		Date currentdate = new Date(new Long(date));
		return currentdate.toInstant();
	}
		
	
	public static String getSource(String path) throws MalformedURLException {
		File file = getFile(path);
		String name = file.getName();
		String[] arg = name.split("_");
		return arg[1];

	}
	
	public static byte[] getBytesOfImage(String urlText) throws Exception {
		InputStream is = null;
		URL url = new URL(urlText);
		try {
		  is = url.openStream ();
		  byte[] imageBytes = IOUtils.toByteArray(is);
		  return imageBytes;
		}
		catch (IOException e) {
		  throw new RuntimeException(e);
		}
		finally {
		  if (is != null) { is.close(); }
		}
		
    }
	
	public static BufferedImage test3(String path) {
    	BufferedImage image=null;
    	
    	
    	
    	try {
    		byte[] bFile = Files.readAllBytes(Paths.get(path));
    		InputStream in = new ByteArrayInputStream(bFile);
			image = ImageIO.read(in);
    		
			
			 return image;
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
	
	public static void deleteFile(String path) throws IOException {
		File file = new File(path);
		if(!file.exists())
			log.info(Paths.get(path).getFileName() + "|already deleted!");
		destroyFile(path);
		if(file.delete()){
			log.info(Paths.get(path).getFileName() + "|deleted!");
		}else{
			log.info(Paths.get(path).getFileName()+ "|delete error");
		}
	}
	
	public static void destroyFile(String path) throws IOException {
		FileWriter fileWriter = new FileWriter(path);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.print("deleted");
        printWriter.close();
	}
	
	
}
