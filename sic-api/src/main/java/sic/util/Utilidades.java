package sic.util;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.swing.ImageIcon;

public class Utilidades {   

    /**
     * Convierte un caracter de minusculas a mayusculas
     *
     * @param caracter Caracter para ser convertido
     * @return Devuelve el caracter ya convertido a mayusculas
     */
    public static char convertirAMayusculas(char caracter) {
        if ((caracter >= 'a' && caracter <= 'z') || caracter == 'ñ') {
            return (char) (((int) caracter) - 32);
        } else {
            return caracter;
        }
    }

    /**
     * Busca un archivo especificado en el directorio donde se encuentra el JAR
     *
     * @param archivo Nombre del archivo que se desea buscar
     * @return archivo encontrado
     * @throws java.io.FileNotFoundException
     * @throws java.net.URISyntaxException
     */
    public static File getArchivoDelDirectorioDelJAR(String archivo) throws FileNotFoundException, URISyntaxException {
        File fileBuscado = null;
        CodeSource codeSource = Utilidades.class.getProtectionDomain().getCodeSource();
        File jarFile = new File(codeSource.getLocation().toURI().getPath());
        File jarDir = jarFile.getParentFile();

        if (jarDir != null && jarDir.isDirectory()) {
            File propFile = new File(jarDir, archivo);
            fileBuscado = propFile.getAbsoluteFile();
        }

        return fileBuscado;
    }

    /**
     * Encripta el password con MD5
     *
     * @param password String a ser encriptado.
     * @return String encriptado con MD5.
     */
    public static String encriptarConMD5(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException ex) {
        }
        return null;
    }

    /**
     * Convierte el archivo en un array de bytes.
     *
     * @param archivo Archivo a ser convertido.
     * @return Array de byte representando al archivo.
     * @throws java.io.IOException
     */
    public static byte[] convertirFileIntoByteArray(File archivo) throws IOException {
        byte[] bArchivo = new byte[(int) archivo.length()];
        FileInputStream fileInputStream = new FileInputStream(archivo);
        fileInputStream.read(bArchivo);
        fileInputStream.close();
        return bArchivo;
    }
    
    public static ByteArrayInputStream convertirByteArrayToInputStream(byte[] byteArray) {
     return new ByteArrayInputStream(byteArray);
    }
    
    /**
     * Convierte un array de bytes en una Image.
     *
     * @param bytesArray Array de byte a ser convertido.
     * @return Array de bytes convertido en Image.
     */
    public static Image convertirByteArrayIntoImage(byte[] bytesArray) {
        if (bytesArray == null) {
            return null;
        } else {
            ImageIcon logoImageIcon = new ImageIcon(bytesArray);
            return logoImageIcon.getImage();
        }
    }

    /**
     * Valida el tamanio del archivo, teniendo en cuenta el tamanioValido.
     *
     * @param archivo Archivo a ser validado.
     * @param tamanioValido Tamanio maximo en bytes permitido para el archivo.
     * @return Retorna true en caso de que el tamanio sea válido, false en otro caso.
     * @throws FileNotFoundException En caso de que no se encuentre el archivo.
     */
    public static boolean esTamanioValido(File archivo, long tamanioValido) throws FileNotFoundException {
        if (archivo == null) {
            throw new FileNotFoundException();
        }

        if (archivo.length() > tamanioValido) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Trunca los decimales de un double, segun la cantidad que uno requiera
     *
     * @param valor para ser truncado
     * @param cantidadDecimales cantidad de decimales que debe mantener
     * @return numero truncado
     */
    public static double truncarDecimal(double valor, int cantidadDecimales) {
        if (valor > 0) {
            return (new BigDecimal(String.valueOf(valor)).setScale(cantidadDecimales, BigDecimal.ROUND_FLOOR)).doubleValue();
        } else {
            return (new BigDecimal(String.valueOf(valor)).setScale(cantidadDecimales, BigDecimal.ROUND_CEILING)).doubleValue();
        }
    }
    
    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        return new BigDecimal(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
    }

}
