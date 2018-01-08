# Buscador-de-Puntos-Fiduciales
Buscador de Puntos Fiduciales

Es necesario instalar:
* OpenCV 3.3.1
https://docs.opencv.org/3.3.1/

* JavaFX
http://download.eclipse.org/efxclipse/updates-released/3.0.0/site

Para correr el archivo .jar se debe especificar la ruta a OpenCV

Ejemplo:
java -jar -Djava.library.path=./opencv-3.3.1/build/lib ./tp.jar <paramatros>

Parametros:
* -f <folder> Especifica la carpeta de donde se obtendran las imagenes de los cortes encefalicos.
* -o <output> Especifica el archivo de salida donde se guardaran las 3 coordenadas de los puntos fiduciarios.
*-sp <showpoints> Muestra las imagenes de mejor resolucion de los puntos fiduciarios.
