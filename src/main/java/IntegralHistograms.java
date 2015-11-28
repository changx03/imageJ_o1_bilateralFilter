
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;

public class IntegralHistograms {

    float[] originalImg;
    double[][] pointHisto;
    double[] histoRanges;
    int numOfBins;
    int width;
    int height;
    int bitDepth;
    int numOfGreylevels;
    double interval;

    public IntegralHistograms(float[] image, Rectangle rec, int depth, int numOfBins){
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
        interval = numOfGreylevels / (double) numOfBins;

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

    private int getBinNumber(float intensityLevel) {
        int binNumber = (int) Math.floor((intensityLevel -0.001) / interval);
        
        if (intensityLevel <= histoRanges[1]) {
            return 0;
        } else if (binNumber >= numOfBins - 1 && histoRanges[numOfBins - 1] < intensityLevel) {
            return numOfBins - 1;
        } else {
            return binNumber;
        }
    }

    private void computePointHistogram() {
        // origin
        int BinNo = getBinNumber(originalImg[0]);
        pointHisto[0][BinNo]++;
        // first row
        for (int x = 1; x < width; x++) {
            BinNo = getBinNumber(originalImg[x]);
            pointHisto[x] = pointHisto[x - 1].clone();
            pointHisto[x][BinNo]++;
        }
        // first column
        for (int y = 1; y < height; y++) {
            BinNo = getBinNumber(originalImg[y * width]);
            pointHisto[y * width] = pointHisto[(y -1) * width].clone();
            pointHisto[y * width][BinNo]++;
        }
        // H(x,y|b) = H(x-1,y|b) + H(x,y-1|b) - H(x-1,y-1|b) + I(x,y)
        for (int y = 1; y < height; y++) {
            for (int x = 1; x < width; x++) {
                BinNo = getBinNumber(originalImg[y * width + x]);
                for (int b = 0; b < numOfBins; b++) {
                    pointHisto[y * width + x][b] = pointHisto[y * width + x - 1][b] + pointHisto[(y -1) * width + x][b] - pointHisto[(y -1) * width + x -1][b];
                }
                pointHisto[y * width + x][BinNo]++;
            }
        }
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
        for (int b = 0; b < numOfBins; b++) {
            pHisto[b] = rb[b] - lb[b] - rt[b] + lt[b];
        }
        return pHisto;
    }
}
