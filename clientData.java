import java.io.*;
import java.math.BigInteger;

class clientData implements Serializable{
    String userid;
    String message;
    BigInteger y;

    public clientData(String userid, String message, BigInteger y) {
        this.userid = userid;
        this.message = message;
        this.y = y;
    }
    
}