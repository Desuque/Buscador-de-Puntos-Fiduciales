import org.opencv.core.Point;

import static java.lang.StrictMath.pow;
import static java.lang.StrictMath.sqrt;

class PuntoAuxiliar {

    private static final int MAX_DIFF = 10;
    private Point _point;
    private int _radius;
    private String _image;
    private boolean _processed;

    PuntoAuxiliar(Point point, int radius, String image) {
        this._point = point;
        this._radius = radius;
        this._image = image;
        this._processed = false;
    }

    boolean isProcessed(){
        return this._processed;
    }

    void process(){
        this._processed = true;
    }

    boolean validate(PuntoAuxiliar point) {
        double coordX = point.getX();
        double coordY = point.getY();

        return sqrt(pow(this._point.x - coordX, 2) + pow(this._point.y - coordY, 2)) < MAX_DIFF;
    }

    public double getX() {
        return this._point.x;
    }

    public double getY() {
        return this._point.y;
    }

    String filename(){
        return this._image;
    }

    int radius() {
        return this._radius;
    }
}
