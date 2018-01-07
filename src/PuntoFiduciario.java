import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.StrictMath.pow;
import static java.lang.StrictMath.sqrt;

class PuntoFiduciario {

    private static final int MAX_DIFF = 10;
    private Point3D _point;
    private String _bestResolution;
    private List<String> _files;

    class AuxCoordZ {
        double _height;
        int _radius;
        double _pond;

        AuxCoordZ(double height, int radius){
            this._height = height;
            this._radius = radius;
            this._pond = 0;
        }
    }

    PuntoFiduciario(List<PuntoAuxiliar> points, int scale, double thickness) {
        int maxRadious = 0;
        double coordX = 0;
        double coordY = 0;
        List<AuxCoordZ> coordZ = new ArrayList<AuxCoordZ>();
        this._files = new ArrayList<String>();

        for (int i = 0 ; i < points.size() ; i++){
            PuntoAuxiliar point = points.get(i);
            // Agregamos todos los archivos en donde se ve el punto al atributo _files.
            this._files.add(point.filename());

            // El archivo donde el radio será máximo quedará almacenado como el archivo _bestResolution.
            if (point.radius() >= maxRadious){
                this._bestResolution = point.filename();
                maxRadious = point.radius();
            }

            coordX += point.getX();
            coordY += point.getY();

            coordZ.add(new AuxCoordZ(Integer.valueOf(point.filename().substring(6,12)) * thickness,point.radius()));
        }

        this.ponderar(coordZ);

        // Generamos el punto en base a las coordenadas X, Y y a la ponderación de Z.
        this._point = new Point3D((coordX/points.size())/scale, (coordY/points.size())/scale, this.heightZ(coordZ));
    }

    double heightZ(List<AuxCoordZ> coordZ) {
        double acum = 0.0;

        for (int i = 0 ; i < coordZ.size() ; i++){
            acum += coordZ.get(i)._height * coordZ.get(i)._pond;
        }

        return acum;
    }

    // Se genera un valor para ponderar el parámetro Z en base al tamaño del punto en la imagen del corte. Mientras más grande sea
    // el punto en esa imagen, más se considerará su valor en Z. Para ello, se utiliza el radio del mismo.
    void ponderar(List<AuxCoordZ> coordZ) {
        double acum = 0.0;

        for (int i = 0 ; i < coordZ.size() ; i++){
            acum += coordZ.get(i)._radius;
        }

        for (int i = 0 ; i < coordZ.size() ; i++){
            coordZ.get(i)._pond = coordZ.get(i)._radius/acum;
        }
    }

    double getX() {
        return this._point.x;
    }

    double getY() {
        return this._point.y;
    }

    double getZ() {
        return this._point.z;
    }

    String bestImage() { return this._bestResolution; }

    // Graficador de Puntos Fiduciales. Mismo procesamiento para la obtención, si cada punto valida con el punto evaluado, se agrega
    // sobre la imagen y se gráfico.
    void graph(int point, String folder, int scale, int blurreo) {
        Mat img = Imgcodecs.imread(folder + "/" + this._bestResolution);

        int OLD_RESOLUTION = img.cols();
        Imgproc.resize(img, img, new Size(OLD_RESOLUTION * scale, OLD_RESOLUTION * scale));
        Mat gray = new Mat();
        Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(gray, gray, new Size(blurreo, blurreo), 5);

        Mat circles = new Mat();
        Imgproc.HoughCircles(gray, circles, Imgproc.HOUGH_GRADIENT, 1.0,
                (double)gray.rows()/16, // change this value to detect circles with different distances to each other
                100.0, 30.0, 1, 30); // change the last two parameters
        // (min_radius & max_radius) to detect larger circles
        for (int x = 0; x < circles.cols(); x++) {
            double[] c = circles.get(0, x);
            Point center = new Point(Math.round(c[0]), Math.round(c[1]));
            int radius = (int) Math.round(c[2]);
            if (this.validate(new PuntoAuxiliar(new Point(center.x/scale, center.y/scale), radius, ""))) {
                Imgproc.circle(img, center, 1, new Scalar(0, 100, 100), 3, 8, 0);
                Imgproc.circle(img, center, radius, new Scalar(255, 0, 255), 3, 8, 0);
            }
        }

        Imgproc.resize(img, img, new Size(512,512));
        HighGui.imshow("Punto Fiduciario " + point + " del Archivo " + folder + "/" + this._bestResolution, img);
        HighGui.waitKey();
    }

    // Si la distancia entre el punto y el punto a validar es menor a MAX_DIFF, se devuelve true.
    boolean validate(PuntoAuxiliar point) {
        double coordX = point.getX();
        double coordY = point.getY();

        return sqrt(pow(abs(this._point.x - coordX), 2) + pow(abs(this._point.y - coordY), 2)) < MAX_DIFF;
    }
}
