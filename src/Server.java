
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

public class Server {

    SSLServerSocket serverSock = null;
    SSLSocket socket = null;
    PrintWriter out = null;

    public static void main(String[] args) {

        new Server().startServer();
    }

    public void startServer() {
        final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);

        Runnable serverTask = new Runnable() {
            @Override
            public void run() {
                try {

                    //load server private key
                    KeyStore serverKeys = KeyStore.getInstance("JKS");
                    serverKeys.load(new FileInputStream("C:\\Program Files (x86)\\Java\\jre1.8.0_45\\lib\\security\\plainserver.jks"), "password".toCharArray());
                    KeyManagerFactory serverKeyManager = KeyManagerFactory.getInstance("SunX509");
                    //System.out.println(KeyManagerFactory.getDefaultAlgorithm());
                    //System.out.println(serverKeyManager.getProvider());
                    serverKeyManager.init(serverKeys, "password".toCharArray());
                    //load client public key
                    KeyStore clientPub = KeyStore.getInstance("JKS");
                    clientPub.load(new FileInputStream("C:\\Program Files (x86)\\Java\\jre1.8.0_45\\lib\\security\\clientpub.jks"), "password".toCharArray());
                    TrustManagerFactory trustManager = TrustManagerFactory.getInstance("SunX509");
                    trustManager.init(clientPub);
                    //use keys to create SSLSoket
                    SSLContext ssl = SSLContext.getInstance("TLSv1.2");
                    ssl.init(serverKeyManager.getKeyManagers(), trustManager.getTrustManagers(), SecureRandom.getInstance("SHA1PRNG"));
                    serverSock = (SSLServerSocket) ssl.getServerSocketFactory().createServerSocket(8889);
                    serverSock.setNeedClientAuth(true);
                    System.out.println("Waiting for clients to connect...");

                    while (true) {
                        socket = (SSLSocket) serverSock.accept();
                        clientProcessingPool.submit(new ClientTask(socket));
                    }
                } catch (IOException e) {
                    System.err.println("Unable to process client request");
                    e.printStackTrace();
                } catch (KeyStoreException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                } catch (CertificateException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnrecoverableKeyException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KeyManagementException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        Thread serverThread = new Thread(serverTask);
        serverThread.start();

    }

    private class ClientTask implements Runnable {

        private final Socket clientSocket;

        private ClientTask(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                System.out.println("Got a client !");
                
                // Do whatever required to process the client's request
                //send data
                printSocketInfo(socket);
                
                BufferedWriter w = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader r = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String m = r.readLine();
                String parsed = requestParser(m);

                w.write("HTTP/1.0 200 OK");
                w.newLine();
                w.write("Content-Type: text/html");
                w.newLine();
                w.newLine();
                w.write("<html><title>Procesando su solicitud!</title></html>");
                w.newLine();
                w.write("<html><body>"+ parsed +"</body></html>");
                w.newLine();
                w.flush();
                w.close();
                r.close();
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    //Palabras reservadas '?', 'HTTP', tamanio default de un request 14
    private String requestParser(String m) {
        if (m.length() == 14) {
            return "codigo=null";
        } else {
            String parsed = "";
            int posPregunta = m.indexOf("?");
            int posFinal = m.indexOf("HTTP");
            parsed = m.substring(posPregunta + 1, posFinal - 1);
            return parsed;
        }
    }


    private static void printSocketInfo(SSLSocket s) {
        System.out.println("Socket class: " + s.getClass());
        System.out.println("   Remote address = "
                + s.getInetAddress().toString());
        System.out.println("   Remote port = " + s.getPort());
        System.out.println("   Local socket address = "
                + s.getLocalSocketAddress().toString());
        System.out.println("   Local address = "
                + s.getLocalAddress().toString());
        System.out.println("   Local port = " + s.getLocalPort());
        System.out.println("   Need client authentication = "
                + s.getNeedClientAuth());
        SSLSession ss = s.getSession();
        System.out.println("   Cipher suite = " + ss.getCipherSuite());
        System.out.println("   Protocol = " + ss.getProtocol());
    }

    private static void printServerSocketInfo(SSLServerSocket s) {
        System.out.println("Server socket class: " + s.getClass());
        System.out.println("   Socker address = "
                + s.getInetAddress().toString());
        System.out.println("   Socker port = "
                + s.getLocalPort());
        System.out.println("   Need client authentication = "
                + s.getNeedClientAuth());
        System.out.println("   Want client authentication = "
                + s.getWantClientAuth());
        System.out.println("   Use client mode = "
                + s.getUseClientMode());
    }

}
