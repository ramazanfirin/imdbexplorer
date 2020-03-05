package com.mastertek.service;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mastertek.config.ApplicationProperties;
import com.mastertek.web.rest.util.FaceUtil;

import ayonix.AyonixFace;
import ayonix.AyonixImage;
import ayonix.FaceID;

@Service
public class AyonixEngineService{

	private final Logger log = LoggerFactory.getLogger(AyonixEngineService.class);
	
	FaceID sdk;
	
	private ApplicationProperties applicationProperties;
	
	public AyonixEngineService(ApplicationProperties applicationProperties) {
		super();
		// TODO Auto-generated constructor stub
		this.applicationProperties = applicationProperties;
	}

	@PostConstruct
	public void init() {
		//if(!"tomcat".equals(applicationProperties.getEnvironment()))
			sdk = new FaceID("C:\\Program Files\\Ayonix Corporation\\Ayonix FaceID SDK v6\\data\\engine");
		
		System.out.println("bitti");
	}
	
	@PreDestroy
	public void destroy() {
		sdk.Close();
	}
	
	public byte[] createTemplate(AyonixFace face) {
		return sdk.CreateAfid(face);
	}
	
	public void preProcess(AyonixFace face) {
		sdk.PreprocessFace(face);
	}
	
	public List<AyonixFace> analyze(String path) throws Exception  {
		
			List<AyonixFace> result = new ArrayList<AyonixFace>();
			
			AyonixFace[] faces = detectFaces(path);
			for (int i = 0; i < faces.length; i++) {
				result.add(faces[i]);
			}
			
			return result;
	}
	
	public byte[] getAfid(byte[] bytes) {
		AyonixImage img = sdk.LoadImage(bytes);
		AyonixFace[] faces = sdk.DetectFaces(img);
		if(faces.length==0)
			throw new RuntimeException("No face detected");
		if(faces.length>1)
			throw new RuntimeException("more face than 1");
		
		
		sdk.PreprocessFace(faces[0]);
		byte[] afid = sdk.CreateAfid(faces[0]);
		if(afid == null)
			throw new RuntimeException("afid not detected");
		
		return afid;
		
	}
	
	public byte[] getAfid(String path) {
		AyonixImage img = sdk.LoadImage(path);
		AyonixFace[] faces = sdk.DetectFaces(img);
		if(faces.length==0)
			throw new RuntimeException("No face detected");
		if(faces.length>1)
			throw new RuntimeException("more face than 1");
		
		
		sdk.PreprocessFace(faces[0]);
		byte[] afid = sdk.CreateAfid(faces[0]);
		if(afid == null)
			throw new RuntimeException("afid not detected");
		
		return afid;
		
	}
	
	public AyonixFace[] detectFaces(String path) throws Exception {
		
		 AyonixImage img =null;
		 if(FaceUtil.isUrl(path))
			 img= sdk.LoadImage(FaceUtil.getBytesOfImage(path));
		 else
			 img = sdk.LoadImage(path);
		 
		 AyonixFace[] faces = sdk.DetectFaces(img,48,new Rectangle(img.getWidth(), img.getHeight()));
		 return faces;
	}

	public AyonixFace[] detectFaces(byte[] bytes) throws Exception {
		
		 AyonixImage img =null;
		 img= sdk.LoadImage(bytes);
		  
		 AyonixFace[] faces = sdk.DetectFaces(img,48,new Rectangle(img.getWidth(), img.getHeight()));
		 return faces;
	}
	
	public AyonixFace[] detectFaceFromResizedImage(String path) throws IOException {
		BufferedImage image = ImageIO.read(new File(path));
        int type = image.getType() == 0? BufferedImage.TYPE_INT_ARGB : image.getType();
        BufferedImage imageNew = resizeImage700(image, type);
        
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(imageNew, "jpg", bos );
        byte [] data = bos.toByteArray();
        AyonixImage img = sdk.LoadImage(data);
        //faces = sdk.DetectFaces(img);
        AyonixFace[] faces = sdk.DetectFaces(img,48,new Rectangle(img.getWidth(), img.getHeight()));
        
        return faces;
	}
	
	
	
	public float match(byte[] afid1,byte[] afid2) {
		
		try {
			float[] scores = new float[1];
			
			int[] indexes = new int[1];
			
			Vector<byte[]> afids = new Vector<byte[]>();
			afids.add(afid2);
			
			sdk.MatchAfids(afid1, afids, scores, indexes);
			return scores[0];
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return 0;
		}
		
	}
	
	public void match(byte[] afid1,Vector<byte[]> afids,float[] scores,int[] indexes) {
		
		try {
			sdk.MatchAfids(afid1, afids, scores, indexes);
			//return scores[0];
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//return 0;
			e.printStackTrace();
		}
		
	}
	
	 private static BufferedImage resizeImage(BufferedImage originalImage, int type){
     	BufferedImage resizedImage = new BufferedImage(originalImage.getWidth()*2, originalImage.getHeight()*2, type);
     	Graphics2D g = resizedImage.createGraphics();
     	g.drawImage(originalImage, 0, 0, originalImage.getWidth()*2, originalImage.getHeight()*2, null);
     	g.dispose();
     		
     	return resizedImage;
         }
	 // gurkan
	 private static BufferedImage resizeImage700(BufferedImage originalImage, int type){
	     	BufferedImage resizedImage = new BufferedImage(originalImage.getWidth()*7, originalImage.getHeight()*7, type);
	     	Graphics2D g = resizedImage.createGraphics();
	     	g.drawImage(originalImage, 0, 0, originalImage.getWidth()*7, originalImage.getHeight()*7, null);
	     	g.dispose();
	     		
	     	return resizedImage;
	         }
}
