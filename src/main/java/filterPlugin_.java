
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import java.awt.Rectangle;

public class filterPlugin_ implements PlugInFilter {

    // image properties
    private ImagePlus image;
    private String imageTitle;
    private Rectangle rec;
    private int bitDepth;

    // parameters from dialog
    private final String[] filterChoices = {
        "Integral arithmetic mean",
        "Integral triangular filter",
        "Integral polynomial filter",
        "Convolutional polynomial filter",
        "Integral Hisotram Intensity"
    };

    private enum Filters {

        INT_MEAN, INT_TRI, INT_POLY, CON_POLY, INT_HIS_INTEN
    };
    private Filters filter;
    private int radius;
    private int numOfBins;  // number of bins for local histogram
    private double alpha;   // only used in Guassian intensity difference

    @Override
    public int setup(String string, ImagePlus ip) {
        this.image = ip;
        imageTitle = image.getShortTitle();
        if (!showDialog()) {
            return DONE;
        }
        return IJ.setupDialog(image, DOES_8G + DOES_16 + SUPPORTS_MASKING);
    }

    private boolean showDialog() {
        GenericDialog gd = new GenericDialog("Integral Filters");
        gd.addChoice("Selected Filter", filterChoices, filterChoices[0]);
        gd.addNumericField("window radius", 2, 0);
        gd.addNumericField("Number of bins", 32, 0);
        gd.addNumericField("Intensity alpha", 0.2, 2);

        gd.showDialog();
        if (gd.wasCanceled()) {
            return false;
        }
        // get entered values
        String selectedChoice = gd.getNextChoice();
        if (selectedChoice.equals(filterChoices[0])) {
            filter = Filters.INT_MEAN;
        } else if (selectedChoice.equals(filterChoices[1])) {
            filter = Filters.INT_TRI;
        } else if (selectedChoice.equals(filterChoices[2])) {
            filter = Filters.INT_POLY;
        } else if (selectedChoice.equals(filterChoices[3])) {
            filter = Filters.CON_POLY;
        } else if (selectedChoice.equals(filterChoices[4])) {
            filter = Filters.INT_HIS_INTEN;
        }

        radius = (int) gd.getNextNumber();
        numOfBins = (int) gd.getNextNumber();
        alpha = gd.getNextNumber();
        return true;
    }

    @Override
    public void run(ImageProcessor ip) {
        FloatProcessor fp = null;
        fp = ip.toFloat(0, fp);
        bitDepth = ip.getBitDepth();
        rec = ip.getRoi();
        float[] pixels = (float[]) fp.getPixels();  // oringal image
        IntegralImage integral = new IntegralImage(pixels, rec);// getIntegralImg image

        // display getIntegralImg image in new window
//        show32bitImage(integral.integralImg, rec, "IntegralSum");
//        integral.initialIntegralImgs4Polynomial();
//        show32bitImage(integral.zIntegral, rec, "zIntegral");
//        show32bitImage(integral.z2Integral, rec, "z2Integral");
        
        String outputImageTitle;
        double[] outImg;
        SpatialFilters sf = new SpatialFilters(integral);
        switch (filter) {
            case INT_MEAN:  // integral arithmetic mean filter
                outImg = sf.integralMean(radius);
                outputImageTitle = imageTitle + "_integralMeanR" + radius;
                break;
            case INT_TRI:   // triangular filter
                outImg = sf.integralTriangular(radius);
                outputImageTitle = imageTitle + "_integralTriR" + radius;
                break;
            case INT_POLY:  // polynomial filter
                outImg = sf.integralPolynomial(radius);
                outputImageTitle = imageTitle + "_integralPolyR" + radius;
                break;
            case CON_POLY:
                outImg = sf.convolutionalPolynomial(radius);
                outputImageTitle = imageTitle + "_convoPolyR" + radius;
                break;
            case INT_HIS_INTEN:
                IntegralHistograms ih = new IntegralHistograms(pixels, rec, bitDepth, numOfBins);
                sf.SetIntegralHistograms(ih);
                outImg = sf.integralHistoIntensityGaussian(radius, alpha);
                outputImageTitle = imageTitle + "_intHistoIntensityR" + radius + "a" + alpha;
                break;
            default:
                outImg = new double[rec.width * rec.height];
                outputImageTitle = "null";
                break;
        }
        show8bitImage(outImg, rec, outputImageTitle);
    }

    private void show8bitImage(double[] image, Rectangle rec, String imageTitle) {
        ByteProcessor bp = new ByteProcessor(rec.width, rec.height);

        for (int y = 0; y < rec.height; y++) {
            for (int x = 0; x < rec.width; x++) {
                bp.putPixelValue(x, y, image[y * rec.width + x]);
            }
        }
        ImagePlus imagePlus = new ImagePlus(imageTitle, bp);
        imagePlus.show();
    }

    private void show32bitImage(double[] image, Rectangle rec, String imageTitle) {
        FloatProcessor fp = new FloatProcessor(rec.width, rec.height);

        for (int y = 0; y < rec.height; y++) {
            for (int x = 0; x < rec.width; x++) {
                fp.putPixelValue(x, y, image[y * rec.width + x]);
            }
        }
        ImagePlus imagePlus = new ImagePlus(imageTitle, fp);
        imagePlus.show();
    }

    // Calculate the getIntegralImg image from a float array with given height and width
    public static void main(String[] args) {
        // set the plugins.dir property to make the plugin appear in the Plugins menu
        Class<?> clazz = filterPlugin_.class;
        String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
        String pluginsDir = url.substring(5, url.length() - clazz.getName().length() - 6);
        System.setProperty("plugins.dir", pluginsDir);

        // start ImageJ
        new ImageJ();

//        // open the sample
//        ImagePlus image = IJ.openImage("http://www.cosy.sbg.ac.at/~pmeerw/Watermarking/lena_gray.gif");
//        image.show();
//        // run the plugin
//        IJ.runPlugIn(clazz.getName(), "");
    }
}
