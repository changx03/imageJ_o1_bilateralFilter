
import java.awt.Point;


public class SpatialFilters {
    int height;
    int width;
    IntegralImage integral;
    
    public SpatialFilters(IntegralImage integral) {
        height = integral.height;
        width = integral.width;
        this.integral = integral;
    }

    public double[] integralMean(int radius){
        double[] outImg = new double[height * width];
        double normFactor = (double)(4.0 * radius * radius + 4.0 * radius + 1); 
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x - radius >= 0 && y - radius >= 0 && x + radius < width && y + radius < height) {
                    double newPixelValue = integral.getSum(integral.integralImg, new Point(x - radius -1, y - radius -1), new Point(x + radius, y + radius)) / normFactor;
                    outImg[y * width + x] = newPixelValue;
                } else {
                    outImg[y * width + x] = 0;
                }
            }
        }
        return outImg;
    }
    
    public double[] integralTriangular(int radius) {
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
                    newPixelValue += integral.getSum(integral.integralImg, new Point(x - r -1, y - r -1), new Point(x + r, y + r));
                    }
                    outImg[y * width + x] = newPixelValue / normFactor;
                } else {
                    outImg[y * width + x] = 0;
                }
            }
        }
        return outImg;
    }
    
    public double[] integralPolynomial(int radius) {
        double[] outImg = new double[height * width];
        
        // compute integral images for zI(z) and z^2I(z)
        if (!integral.isPolyIntegralImgsExist()) {
            integral.initialIntegralImgs4Polynomial();
        }
        // weight formula: f(k) = r^2 + 1 - k^2
        double normFactor = 0;
        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                double pNorm = IntegralImage.computePointNorm(new Point(x, y));
                normFactor += radius * radius + 1 - pNorm * pNorm;
            }
        }
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x - radius >= 0 && y - radius >= 0 && x + radius < width && y + radius < height) {
                    double pNorm = IntegralImage.computePointNorm(new Point(x, y));
                    Point leftTop = new Point(x - radius -1, y - radius -1);
                    Point rightBot = new Point(x + radius, y + radius);
                    double p1 = (radius * radius + 1 - pNorm * pNorm) * integral.getSum(integral.integralImg, leftTop, rightBot);
                    double p2 = 2 * pNorm * integral.getSum(integral.zIntegral, leftTop, rightBot);
                    double p3 = integral.getSum(integral.z2Integral, leftTop, rightBot);
                    double pixVal = (p1 + p2 - p3) / normFactor;
                    outImg[y * width + x] = pixVal;
                }
            }
        }
        return outImg;
    }
}
