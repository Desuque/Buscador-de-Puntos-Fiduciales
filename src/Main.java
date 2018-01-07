import java.io.IOException;
import java.util.List;

public class Main {
	
	public static void main(String[] args) {
		Procesador proc = new Procesador(args[0]);
		proc.process();

		if (args.length < 2){
			proc.showBestImages();
		} else {
			try {
				proc.savePoints(args[1]);
			} catch (IOException ignored) {}

			List<PuntoFiduciario> points = proc.getPoints();

			for (int i = 0; i < points.size(); i++) {
				System.out.println("Punto " + (i + 1) + ":\t\tX: " + String.format("%.4f", points.get(i).getX()) + "\t\tY: " +
						String.format("%.4f", points.get(i).getY()) + "\t\tZ: " + String.format("%.4f", points.get(i).getZ()) + "\t\t\t\t\t\tMejor Resolución: " + points.get(i).bestImage());
			}
		}
		System.exit(0);
	}
}