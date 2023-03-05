import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Server {

	 private ServerSocket serverSocket;
	    public static final int DEFAULT_SERVER_PORT = 4444;
	    int timeout = 30;
	    InetAddress inetadress;
	    /**
	     * Initiates a server socket on the input port, listens to the line, on receiving an incoming
	     * connection creates and starts a ServerThread on the client
	     * @param port
	     * @throws SocketTimeoutException 
	     */
	    public Server(int port) throws IOException
	    {
	        try
	        {
	            serverSocket = new ServerSocket(port);
	            System.out.println("Oppened up a server socket on " + Inet4Address.getLocalHost());
	        }
	        catch (IOException e)
	        {
	            e.printStackTrace();
	            System.err.println("Server class.Constructor exception on oppening a server socket");
	        }
	        while (true)
	        {
	            ListenAndAccept();
	        }
	    }

	    /**
	     * Listens to the line and starts a connection on receiving a request from the client
	     * The connection is started and initiated as a ServerThread object
	     */
	    private void ListenAndAccept() throws IOException
	    {
	        Socket s;
	        try
	        {
	            s = serverSocket.accept();
	           s.setSoTimeout(timeout*1000);
	            inetadress = s.getInetAddress();
	            System.out.println("A connection was established with a client on the address of " + s.getRemoteSocketAddress());
	            ServerThread st = new ServerThread(s);
	            st.Map();
	            
	            st.start();

	        }

	        catch (SocketTimeoutException e)
	        {
	            e.printStackTrace();
	            
	            
	        }
	        
	    }

}
