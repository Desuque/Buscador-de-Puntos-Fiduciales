import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class Main {
	
	public static void main(String[] args) {

		if (args.length == 0){
			System.exit(0);
		}

		boolean showPoints = false;
		String picturesFolder = null;
		String outputFile = null;

		for (int i = 0; i < args.length ; i++){
			if (Objects.equals(args[i], "-f")){
				i++;
				picturesFolder = args[i];
			}

			if (Objects.equals(args[i], "-sp")){
				showPoints = true;
			}

			if (Objects.equals(args[i], "-o")){
				i++;
				outputFile = args[i];
			}
		}

		if (picturesFolder == null) {
			System.err.println("ERROR: No se especificó carpeta de imágenes.");
			System.exit(0);
		}

		if (outputFile == null) { outputFile = "output.txt"; }

		Procesador proc = new Procesador(picturesFolder);
		proc.process();

		try {
			proc.savePoints(outputFile);
		} catch (IOException ignored) {}

		List<PuntoFiduciario> points = proc.getPoints();

		for (int i = 0; i < points.size(); i++) {
			System.out.println("Punto " + (i + 1) + ":\t\tX: " + String.format("%.4f", points.get(i).getX()) + "\t\tY: " +
					String.format("%.4f", points.get(i).getY()) + "\t\tZ: " + String.format("%.4f", points.get(i).getZ()) + "\t\t\t\t\t\tMejor Resolución: " + points.get(i).bestImage());
		}

		if (showPoints) { proc.showBestImages(); }

		System.exit(0);
	}
}