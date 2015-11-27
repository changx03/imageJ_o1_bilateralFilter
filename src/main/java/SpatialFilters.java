
import java.awt.Point;


public class SpatialFilters {

    public static double[] integralMean(IntegralImage integral, int radius){
        int height = integral.height;
        int width = integral.width;
        double[] outImg = new double[height * width];
        double normFactor = (double)(4.0 * radius * radius + 4.0 * radius + 1); 
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x - radius >= 0 && y - radius >= 0 && x + radius < width && y + radius < height) {
                    double newPixelValue = integral.getSum(new Point(x - radius -1, y - radius -1), new Point(x + radius, y + radius)) / normFactor;
                    outImg[y * width + x] = newPixelValue;
                } else {
                    outImg[y * width + x] = 0;
                }
            }
        }
        return outImg;
    }
    
    public static double[] integralTriangular(IntegralImage integral, int radius) {
        int height = integral.height;
        int width = integral.width;
        double[] outImg = new double[height * width];

        int k = 2 * radius + 1;
        double normFactor = 0;
        for(int i = 1; i <= k; i += 2) {
            normFactor += i * i;
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x - radius >= 0 && y - radius >= 0 && x + radius < width && y + radius < height) {
                    double newPixelValue = integral.originImg[y * width + height];
                    for(int r = 1; r <= radius; r++) {
                    newPixelValue += integral.getSum(new Point(x - r -1, y - r -1), new Point(x + r, y + r));
                    }
                    outImg[y * width + x] = newPixelValue / normFactor;
                } else {
                    outImg[y * width + x] = 0;
                }
            }
        }
        return outImg;
    }
    
    
}
