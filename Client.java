

import java.net.*;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;

public class Client {

    public static void main(String args[]) throws Exception {
		if (args.length != 3) {
			System.err.println("Usage: Client Host, Port or userid");
			System.exit(-1);
		}        
        Socket s = new Socket(args[0], Integer.parseInt(args[1]));
        ObjectOutputStream dout = new ObjectOutputStream(s.getOutputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String str = "";
        System.out.print("Enter a Message: ");
        str = br.readLine();
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        BigInteger x = new BigInteger(1, messageDigest.digest(str.getBytes()));
        ObjectInputStream objIn = null;
        try {
            objIn = new ObjectInputStream(new FileInputStream(args[2] + ".prv"));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return;
        }
        RSAPrivateKey key = (RSAPrivateKey) objIn.readObject();
        BigInteger d = key.getPrivateExponent();
        BigInteger n = key.getModulus();
        BigInteger y = x.modPow(d, n); 
        clientData cd = new clientData(args[2], str, y);
        dout.writeObject(cd);
        dout.flush();
        dout.close();
        s.close();
    }
}

