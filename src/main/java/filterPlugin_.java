
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import java.awt.Point;
import java.awt.Rectangle;

public class filterPlugin_ implements PlugInFilter {

    private ImagePlus imp;

    @Override
    public int setup(String string, ImagePlus ip) {
        this.imp = imp;
        return IJ.setupDialog(imp, DOES_8G + DOES_16 + SUPPORTS_MASKING);
    }

    @Override
    public void run(ImageProcessor ip) {
        FloatProcessor fp = null;
        fp = ip.toFloat(0, fp);
        Rectangle rec = ip.getRoi();
        float[] pixels = (float[]) fp.getPixels();  // oringal image
        IntegralImage integral = new IntegralImage(pixels, rec);// getIntegralImg image

        // display getIntegralImg image in new window
//        show32bitImage(integral.integralImg, rec, "IntegralSum");
//        integral.initialIntegralImgs4Polynomial();
//        show32bitImage(integral.zIntegral, rec, "zIntegral");
//        show32bitImage(integral.z2Integral, rec, "z2Integral");

        // integral arithmetic mean filter
//        int r = 5;
//        SpatialFilters sf = new SpatialFilters(integral);
//        double[] meanImg = sf.integralMean(r);
//        show8bitImage(meanImg, rec, "integralMeanR" + r);
        
        // triangular filter
//        int radius = 5;
//        SpatialFilters sf = new SpatialFilters(integral);
//        double[] triSmoothImg = sf.integralTriangular(radius);
//        show8bitImage(triSmoothImg, rec, "integralTriangleR" + radius);
        
        // polynomial filter
        int radius = 2;
        SpatialFilters sf = new SpatialFilters(integral);
        double[] convoPoly = sf.convolutionalPolynomial(radius);
        show8bitImage(convoPoly, rec, "convolutionalPolySmoothR" + radius);
        
        double[] polySmoothImg = sf.integralPolynomial(radius);
        show8bitImage(polySmoothImg, rec, "integralPolySmoothR" + radius);
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
