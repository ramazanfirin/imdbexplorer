package ayonix;



import java.awt.geom.Point2D;
import java.awt.Rectangle;

public class AyonixFace
{
        private Rectangle location;
        private Rectangle mugshotLocation;
        private byte[] mugshotData;
        
        private boolean isValid;
        private float quality;
        private float gender;
        private int age;
        private float[] rpy = new float[3];
        
        private byte[] reserved;

        private AyonixFace() {}   // prevent user creation

        public Rectangle getLocation() { return location; }
        public Rectangle getMugshotLocation() { return mugshotLocation; }
        
        public boolean isValid() { return isValid; }
        public float getQuality() { return quality; }
        public float getGender() { return gender; }
        public int getAge() { return age; }
        public float[] getAngles() { return rpy; }
}

