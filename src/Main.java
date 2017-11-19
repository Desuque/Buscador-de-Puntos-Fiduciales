import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


public class Main {
	
	public static void main(String[] args) {
		System.out.println("Hola Mundo");
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		File folder = new File("src");
		File[] listOfFiles = folder.listFiles();

		    for (int i = 0; i < listOfFiles.length; i++) {
		      if (listOfFiles[i].isFile()) {
		        System.out.println("File " + listOfFiles[i].getName());
		      } else if (listOfFiles[i].isDirectory()) {
		        System.out.println("Directory " + listOfFiles[i].getName());
		      }
		    }
		
		Mat img = Imgcodecs.imread("src/output000079.jpg");
		
		circles(img);
    }
	
	public static void circles(Mat img){
		
		
		//BETA
		
		//Imgproc.resize(img, img, new Size(450,250));

	    Mat gray = new Mat();
	    Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
	    //Imgproc.blur(gray, gray, new Size(3, 3));

	    Mat edges = new Mat();
	    int lowThreshold = 50;
	    int ratio = 20;
	    Imgproc.Canny(img, edges, lowThreshold, lowThreshold * ratio);
		Imgcodecs.imwrite("salida.jpg", edges);
		
		
	    Mat circles = new Mat();
	    Vector<Mat> circlesList = new Vector<Mat>();
	    
        List<MatOfPoint> contours= new ArrayList<MatOfPoint>();
        Imgproc.findContours(gray, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        for (int idx = 0; idx < contours.size(); idx++) 
        {
              MatOfPoint contour = contours.get(idx);
              double contourarea = Imgproc.contourArea(contour);
      	    System.out.println("#contourarea " + contourarea);

        }

	    Imgproc.HoughCircles(edges, circles, Imgproc.CV_HOUGH_GRADIENT, 1, 600, 200, 20, 0, 0 );
	    //Imgproc.HoughCircles(edges, circles, Imgproc.CV_HOUGH_GRADIENT, 1,
	    		//1024/20, 200, 20, 0, 0);
	    //Imgproc.findContours(img, contours, gray, lowThreshold, Imgproc.CV_HOUGH_GRADIENT);

	    System.out.println("#rows " + circles.rows() + " #cols " + circles.cols());
	    double x = 0.0;
	    double y = 0.0;
	    int r = 0;

	    for( int i = 0; i < circles.rows(); i++ )
	    {
	    	double[] data = circles.get(i, 0);
	    	for(int j = 0 ; j < data.length ; j++)
	    	{
	    		x = data[0];
	    		y = data[1];
	    		r = (int) data[2];
	    	}
	    	Point center = new Point(x,y);
	    	System.out.println(center.x);
	    	System.out.println("y-R:" + (int)Math.abs(y-r));
	     
	    	Rect bbox = new Rect((int)Math.abs(x-r), (int)Math.abs(y-r), (int)2*r, (int)2*r);
	    	Mat croped_image = new Mat(img, bbox);
	    	//Imgproc.resize(croped_image, croped_image, new Size(160,160));
	    	circlesList.add(croped_image);

	    
	    }
	    Imgcodecs.imwrite("salida3.jpg", img);

	}

	//El algoritmo de arriba no reconoce los circulos porque son muy chicos!

}