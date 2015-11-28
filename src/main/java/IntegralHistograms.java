
import java.awt.Point;
import java.awt.Rectangle;

public class IntegralHistograms {

    float[] originalImg;
    double[][] pointHisto;
    double[] histoRanges;
    int numOfBins;
    int width;
    int height;
    int bitDepth;
    int numOfGreylevels;

    public IntegralHistograms(float[] image, Rectangle rec, int depth, int numOfBins) {
        this.numOfBins = numOfBins;
        width = rec.width;
        height = rec.height;
        bitDepth = depth;
        numOfGreylevels = getGreyLevelsByDepth(bitDepth);
        originalImg = new float[width * height];
        originalImg = image.clone();
        pointHisto = new double[width * height][this.numOfBins];

        computeHistogramRanges();
        computePointHistogram();
    }

    private void computeHistogramRanges() {
        histoRanges = new double[numOfBins + 1];
        double interval = numOfGreylevels / (double) numOfBins;

        // histoRanges[x-1] < I(x) <= histoRanges[x+1]
        histoRanges[0] = -1.0;
        for (int i = 1; i < histoRanges.length; i++) {
            histoRanges[i] = i * interval;
        }
    }

    public static int getGreyLevelsByDepth(int depth) {
        int greyLevels = (int) Math.round(Math.pow(2, depth));
        return greyLevels;
    }

    private void computePointHistogram() {

    }

    public double[] localHistogram(Point leftTop, Point rightBottom) {
        double[] pHisto = new double[numOfBins];
        double[] lt;    // left top point-wise histogram
        double[] lb;    // left bottom point-wise histogram
        double[] rt;    // right top point-wise histogram
        double[] rb;    // right bottom point-wise histogram
        Point rightTop = new Point(rightBottom.x, leftTop.y);
        Point leftBottom = new Point(leftTop.x, rightBottom.y);
        // test invalid pointer
        rb = pointHisto[rightBottom.y * width + rightBottom.x];
        if (leftTop.x < 0 || leftTop.y < 0) {
            lt = new double[numOfBins];
        } else {
            lt = pointHisto[leftTop.y * width + leftTop.x];
        }
        if (rightTop.x < 0 || rightTop.y < 0) {
            rt = new double[numOfBins];
        } else {
            rt = pointHisto[rightTop.y * width + rightTop.x];
        }
        if (leftBottom.x < 0 || leftBottom.y < 0) {
            lb = new double[numOfBins];
        } else {
            lb = pointHisto[leftBottom.y * width + leftBottom.x];
        }
        // compute the new logal histogram
        for (int i = 0; i < numOfBins; i++) {
            pHisto[i] = rb[i] - lb[i] - rt[i] + lt[i];
        }
        return pHisto;
    }
}
