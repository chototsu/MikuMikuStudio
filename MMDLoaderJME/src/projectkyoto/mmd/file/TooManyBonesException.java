/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectkyoto.mmd.file;

/**
 *
 * @author kobayasi
 */
public class TooManyBonesException extends RuntimeException{

    public TooManyBonesException(Throwable thrwbl) {
        super(thrwbl);
    }

    public TooManyBonesException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public TooManyBonesException(String string) {
        super(string);
    }

    public TooManyBonesException() {
    }
}
