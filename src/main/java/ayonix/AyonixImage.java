package ayonix;



public class AyonixImage
{
        private boolean isLoaded;
        private int width;
        private int height;
        private boolean isColor;
        private int step;
        private byte[] data;
        
        public AyonixImage(int width, int height, boolean isColor, int step, byte[] data)
        {
                if(width < 0 || height < 0 || step < width || data == null)
                        throw new IllegalArgumentException();
                
                this.width = width;
                this.height = height;
                this.isColor = isColor;
                this.step = step;
                this.data = new byte[height*step];
                
                System.arraycopy(data, 0, this.data, 0, height*step);
                
                isLoaded = true;
        }
        
        public int getWidth() { return width; }
        public int getHeight() { return height; }
        public int getStep() { return step; }
        public boolean isColor() { return isColor; }
        public boolean isLoaded() { return isLoaded; }
        public byte[] getData() { return data; }
}

