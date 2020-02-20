package ayonix;



import java.awt.geom.Point2D;
import java.awt.Rectangle;
import java.util.Vector;

public class FaceID
{
        static
        {
        	//System.loadLibrary("API-MS-Win-Core-Kernel32-Private-L1-1-1");
//        	System.loadLibrary("FreeImage");
//        	 System.loadLibrary("AyonixFaceID");   
//        	System.loadLibrary("AyonixJavaSDK");
        	
        	//System.loadLibrary("API-MS-Win-Core-Kernel32-Private-L1-1-1");

        	//System.setProperty("java.library.path", "C:\\Users\\ramazan\\eclipse-workspace\\ayonixtest\\dll");
        	
        	
        	System.loadLibrary("FreeImage");

        	//System.loadLibrary("AyonixJavaSDK");

        	System.loadLibrary("AyonixFaceID");   

        	System.loadLibrary("AyonixJavaSDK");
               
                
        }
        
        private long engine;
        
        public native int[] GetVersion();
        
        public FaceID(String engineFolder)
        {
                engine = InitEngine(engineFolder);
        }
        
        public void Close()
        {
                if(engine != 0)
                {
                        FinalizeEngine(engine);
                        engine = 0;
                }
        }
        
        public native AyonixImage LoadImage(String location);
        public native AyonixImage LoadImage(byte[] encoded);
        
        public AyonixFace[] DetectFaces(AyonixImage img)
        {
                return DetectFaces(engine, img, 64, new Rectangle(0, 0, img.getWidth(), img.getHeight()));
        }
        
        public AyonixFace[] DetectFaces(AyonixImage img, int minsize, Rectangle roi)
        {
                return DetectFaces(engine, img, minsize, roi);
        }
        
        public AyonixFace MarkFace(AyonixImage img, Point2D.Float lEye, Point2D.Float rEye)
        {
                return MarkFace(engine, img, lEye, rEye);
        } 
        
        public void PreprocessFace(AyonixFace face)
        {
                PreprocessFace(engine, face);
        }
        
        public byte[] CreateAfid(AyonixFace face)
        {
                return CreateAfid(engine, face);
        }
        
        public byte[] UpdateAfid(byte[] oldAfid, AyonixFace face)
        {
                return UpdateAfid(engine, face, oldAfid);
        }
        
        public byte[] MergeAfids(byte[] afid1, byte[] afid2)
        {
                return MergeAfids(afid1, afid2);
        }
        
        public void MatchAfids(byte[] afid_query, Vector<byte[]> afids_db, float[] scores, int[] indexes)
        {
                MatchAfids(engine, afid_query, afids_db, scores, indexes);
        }
        
        private native long InitEngine(String engineFolder);
        private native void FinalizeEngine(long engine);
        private native AyonixFace[] DetectFaces(long engine, AyonixImage img, int minsize, Rectangle roi);
        private native AyonixFace MarkFace(long engine, AyonixImage img, Point2D.Float lEye, Point2D.Float rEye);
        private native void PreprocessFace(long engine, AyonixFace face);
        private native byte[] CreateAfid(long engine, AyonixFace face);
        private native byte[] UpdateAfid(long engine, AyonixFace face, byte[] oldAfid);
        private native byte[] MergeAfids(long engine, byte[] afid1, byte[] afid2);
        private native void MatchAfids(long engine, byte[] afid, Vector<byte[]> afids, float[] scores, int[] indexes);
}

