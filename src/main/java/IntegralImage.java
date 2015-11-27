
import java.awt.Point;
import java.awt.Rectangle;

public class IntegralImage {

    float[] originImg;
    double[] integralImg;
    int height;
    int width;

    // distance norm for polynomial filters f(k) = 1 - k^(n)
    double[] zIntegral;
    double[] z2Integral;

    /**
     * IntegralImage
     *
     * @param image the input image
     * @param rec the rectangle object contains the height and width
     */
    public IntegralImage(float[] image, Rectangle rec) {
        if (image == null || rec.height <= 0 || rec.width <= 0) {
            throw new IllegalArgumentException();
        }

        height = rec.height;
        width = rec.width;
        originImg = new float[height * width];
        integralImg = new double[height * width];
        originImg = image.clone();

        // update integral image
        integralImg = computeIntegralImage(originImg);

        // optional integral images
        zIntegral = null;
        z2Integral = null;
    }

    private double[] computeIntegralImage(float[] image) {
        double[] iIntegralImg = new double[width * height];
        iIntegralImg[0] = image[0];
        // compute the first row(y <= 0) and column(x <= 0)
        for (int x = 1; x < width; x++) {
            iIntegralImg[x] = iIntegralImg[x - 1] + image[x];
        }
        for (int y = 1; y < height; y++) {
            iIntegralImg[y * width] = iIntegralImg[(y - 1) * width] + image[y * width];
        }
        // i(x, y) := i(x-1, y) + i(x, y-1) - i(x-1, y-1) + f(x, y) 
        // where i is getIntegralImg image, and f is original image
        for (int y = 1; y < height; y++) {
            for (int x = 1; x < width; x++) {
                iIntegralImg[y * width + x] = iIntegralImg[(y - 1) * width + x]
                        + iIntegralImg[y * width + x - 1]
                        - iIntegralImg[(y - 1) * width + x - 1]
                        + image[y * width + x];
            }
        }
        return iIntegralImg;
    }

    /**
     * InitializeIntegralImgsForPolynomial
     *
     * create 2 integral images for O(1) polynomial filter f(k) = 1 - k^2
     */
    public void InitializeIntegralImgsForPolynomial() {
        float[] zImg = new float[width * height];
        float[] z2Img = new float[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float pointNorm = (float) computePointNorm(new Point(x, y));
                zImg[y * width + x] = pointNorm * originImg[y * width + x];
                z2Img[y * width + x] = pointNorm * pointNorm * originImg[y * width + x];
            }
        }
        zIntegral = computeIntegralImage(zImg);
        z2Integral = computeIntegralImage(z2Img);
    }

    public boolean isPolyIntegralImgsExist() {
        if (zIntegral != null && z2Integral != null) {
            return true;
        } else {
            return false;
        }
    }

    private static double computePointNorm(Point p) {
        return Math.sqrt(p.x * p.x + p.y * p.y);
    }

    /**
     * getSum
     *
     * compute the local same from iIntegralImg image
     *
     * @param leftTop the left top corner above the kernel
     * @param rightBottom the right bottom corner inside the kernel
     */
    public double getSum(Point leftTop, Point rightBottom) {
        Point rightTop = new Point(rightBottom.x, leftTop.y);
        Point leftBottom = new Point(leftTop.x, rightBottom.y);

        // 0-1
        // 2-3
        double[] p = new double[4];
        p[3] = integralImg[rightBottom.y * width + rightBottom.x];
        if (leftTop.x < 0 || leftTop.y < 0) {
            p[0] = 0;
        } else {
            p[0] = integralImg[leftTop.y * width + leftTop.x];
        }
        if (rightTop.x < 0 || rightTop.y < 0) {
            p[1] = 0;
        } else {
            p[1] = integralImg[rightTop.y * width + rightTop.x];
        }
        if (leftBottom.x < 0 || leftBottom.y < 0) {
            p[2] = 0;
        } else {
            p[2] = integralImg[leftBottom.y * width + leftBottom.x];
        }
        double sum = p[3] - p[1] - p[2] + p[0];
        return sum;
    }
}
