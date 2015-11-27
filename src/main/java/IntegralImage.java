
import java.awt.Point;
import java.awt.Rectangle;

public class IntegralImage {

    double[] integral;
    int height;
    int width;

    IntegralImage(float[] image, Rectangle rec) {
        if (image == null || rec.height <= 0 || rec.width <= 0) {
            throw new IllegalArgumentException();
        }

        height = rec.height;
        width = rec.width;
        integral = new double[height * width];

        integral[0] = image[0];
        // compute the first row(y <= 0) and column(x <= 0)
        for (int x = 1; x < width; x++) {
            integral[x] = integral[x - 1] + image[x];
        }
        for (int y = 1; y < height; y++) {
            integral[y * width] = integral[(y - 1) * width] + image[y * width];
        }
        // i(x, y) := i(x-1, y) + i(x, y-1) - i(x-1, y-1) + f(x, y) 
        // where i is getIntegralImg image, and f is original image
        for (int y = 1; y < height; y++) {
            for (int x = 1; x < width; x++) {
                integral[y * width + x] = integral[(y - 1) * width + x]
                        + integral[y * width + x - 1]
                        - integral[(y - 1) * width + x - 1]
                        + image[y * width + x];
            }
        }

    }

    double getSum(Point leftTop, Point rightBottom) {
        Point rightTop = new Point(rightBottom.x, leftTop.y);
        Point leftBottom = new Point(leftTop.x, rightBottom.y);

        // 0-1
        // 2-3
        double[] p = new double[4];
        p[3] = integral[rightBottom.y * width + rightBottom.x];
        if (leftTop.x < 0 || leftTop.y < 0) {
            p[0] = 0;
        } else {
            p[0] = integral[leftTop.y * width + leftTop.x];
        }
        if (rightTop.x < 0 || rightTop.y < 0) {
            p[1] = 0;
        } else {
            p[1] = integral[rightTop.y * width + rightTop.x];
        }
        if (leftBottom.x < 0 || leftBottom.y < 0) {
            p[2] = 0;
        } else {
            p[2] = integral[leftBottom.y * width + leftBottom.x];
        }

        double sum = p[3] - p[1] - p[2] + p[0];
        return sum;
    }

}
