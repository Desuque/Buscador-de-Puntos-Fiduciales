import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.TimeUnit;

class Procesador {

    // Constantes obtenidas para la correcta obtención de los Puntos Fiduciales
    private static final double SLICE_THICKNESS = 1.82;
    private static final int BLURREO = 75;
    private static final int SCALE = 8;
    private String _path;
    private List<PuntoAuxiliar> _puntos;
    private List<PuntoFiduciario> _puntosFiduciarios;


    // Carga de librerías y creación de atributos.
    Procesador(String path){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        this._path = path;
        this._puntos = new ArrayList<PuntoAuxiliar>();
        this._puntosFiduciarios = new ArrayList<PuntoFiduciario>();
    }


    // Generación de los puntos fiduciales
    void process() {

        // Obtención de los archivos.
        File folder = new File(this._path);
        File[] listOfFiles = folder.listFiles();
        this.sortFiles(listOfFiles);

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                if (i > 0 & i % 10 == 0) {
                    // Procesamiento de cada imagen.
                    System.out.println("Procesando: " + String.format("%.2f", i * 100.0 / 120.0) + "%");
                }
                this.detectCenters(Imgcodecs.imread(folder.getName() + "/" + listOfFiles[i].getName()), listOfFiles[i].getName());
            } else if (listOfFiles[i].isDirectory()) {
                System.err.println("No se detectaron imágenes.");
            }
        }
        System.out.println("");
        try { TimeUnit.SECONDS.sleep(2); } catch (InterruptedException ignored) {}

        // Con todos los puntos marcados, los agruparemos por cercanía en base a una constante obtenida que maximiza el rendimiento.
        // De esta manera, todos los puntos en distintas imágenes que estén separados entre sí a una distancia máxima definida en la constante
        // MAX_DIFF de las clases PuntoFiduciario y PuntoAuxiliar, serán considerados como representantes de un mismo Punto Fiducidiario pero,
        // justamente, en distintas cortes encefálicos.
        List<PuntoAuxiliar> aux = new ArrayList<PuntoAuxiliar>();
        while (!allProcessed(this._puntos)) {

            for (int i = 0; i < this._puntos.size(); i++) {
                if (!this._puntos.get(i).isProcessed()) {
                    if (aux.isEmpty()) {
                        aux.add(this._puntos.get(i));
                        this._puntos.get(i).process();
                    } else {
                        if (aux.get(0).validate(this._puntos.get(i))) {
                            aux.add(this._puntos.get(i));
                            this._puntos.get(i).process();
                        }
                    }
                }
            }

            // Generaremos cada Punto Fiduciario en base a esta lista de puntos obtenida.
            this._puntosFiduciarios.add(new PuntoFiduciario(aux, SCALE, SLICE_THICKNESS));
            aux.clear();
        }
    }

    private boolean allProcessed(List<PuntoAuxiliar> points) {
        for (int i = 0; i < points.size(); i++) {
            if (!points.get(i).isProcessed()) {
                return false;
            }
        }
        return true;
    }

    private void sortFiles(File[] files) {
        int length = files.length;

        for (int iter1 = 0 ; iter1 < length ; iter1++){
            for (int iter2 = 0 ; iter2 < (length - 1) ; iter2++){
                if (files[iter1].getName().compareTo(files[iter2].getName()) < 0){
                    File temp = files[iter1];
                    files[iter1] = files[iter2];
                    files[iter2] = temp;
                }
            }
        }
    }


    // Detectamos el centro de cada círculo en las imágenes (puntos fiduciarios) gracias a la librería OpenCV.
    private void detectCenters(Mat img, String file){

        int OLD_RESOLUTION = img.cols();
        // Reescalamos la imagen para un mejor trabajo de la misma.
        Imgproc.resize(img, img, new Size(OLD_RESOLUTION * SCALE, OLD_RESOLUTION * SCALE));
        Mat gray = new Mat();
        // La llevamos a escala de grises para la utilización de la herramiente HoughCircles.
        Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
        // Se le aplica previo un blurreo a la imagen para la refinación de los círculos.
        Imgproc.GaussianBlur(gray, gray, new Size(BLURREO, BLURREO), 5);

        Mat circles = new Mat();
        // La función HoughCircles generará como resultado una lista con los puntos encontrados.
        Imgproc.HoughCircles(gray, circles, Imgproc.HOUGH_GRADIENT, 1.0,
                (double)gray.rows()/16, 100.0, 30.0, 1, 30);
        for (int x = 0; x < circles.cols(); x++) {
            double[] c = circles.get(0, x);
            // Extraemos el centro y el radio de cada círculo obtenido anteriormente.
            Point center = new Point(Math.round(c[0]), Math.round(c[1]));
            int radius = (int) Math.round(c[2]);

            // Agregamos a la lista total de puntos, cada uno de los nuevos obtenidos.
            _puntos.add(new PuntoAuxiliar(center, radius, file));
        }
    }

    // Carga de Puntos Fiduciarios.
    void savePoints(String output) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(output, "UTF-8");

        for (int i = 0 ; i < this._puntosFiduciarios.size() ; i++){
            writer.println("x:" + this._puntosFiduciarios.get(i).getX() + "; y:" + this._puntosFiduciarios.get(i).getY() + "; z:" + this._puntosFiduciarios.get(i).getZ());
        }

        writer.close();
    }

    List<PuntoFiduciario> getPoints(){
        return this._puntosFiduciarios;
    }

    // Muestra el archivo en el que mejor se aprecia cada uno de los Puntos Fiduciarios.
    void showBestImages() {
        for (int i = 0 ; i < this._puntosFiduciarios.size() ; i++){
            this._puntosFiduciarios.get(i).graph(i + 1, this._path, SCALE, BLURREO);
        }
    }
}
