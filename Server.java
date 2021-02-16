

import java.net.*;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {


    public static void main(String args[]) throws IOException{
		if (args.length != 1) {
			System.err.println("Usage: Server Port");
			System.exit(-1);
		}
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(Integer.parseInt(args[0]));
        } catch (IOException e) {
            e.printStackTrace();

        }
        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            // new thread for a client
            new EchoThread(socket).start();
            
        }
    }
}

class EchoThread extends Thread {

    protected Socket socket;

    public EchoThread(Socket clientSocket) {
        this.socket = clientSocket;
    }

    public void run() {
        ObjectInputStream inp = null;
        BufferedReader brinp = null;
        DataOutputStream out = null;
        clientData cd = null;
        try {
            inp = new ObjectInputStream(socket.getInputStream());
            brinp = new BufferedReader(new InputStreamReader(inp));
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            return;
        }
        try {
            cd = (clientData) inp.readObject();
        } catch (IOException ex) {
            Logger.getLogger(EchoThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(EchoThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        String message, client = "";
        BigInteger y;
        client = cd.userid;
        message = cd.message;
        y = cd.y;
        ObjectInputStream objIn = null;
        try {
            objIn = new ObjectInputStream(new FileInputStream(client + ".pub"));
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        RSAPublicKey key = null;
        try {
            key = (RSAPublicKey) objIn.readObject();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (ClassNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        BigInteger e = key.getPublicExponent();
        BigInteger n = key.getModulus();
        BigInteger z = y.modPow(e, n); 
        MessageDigest messageDigest=null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            System.out.println(ex.getMessage());
        }
        BigInteger x = new BigInteger(1, messageDigest.digest(message.getBytes()));
        if(x.compareTo(z)==0){
            System.out.println(client + ": " + message);
        }
        else{
            System.out.println(client + ": [signature not verified]");            
        }
    }
}
