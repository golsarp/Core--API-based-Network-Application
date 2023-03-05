import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionToServer {
	
	public static final String DEFAULT_SERVER_ADDRESS = "localhost";
    public static final int DEFAULT_SERVER_PORT = 4444;
    private Socket s;
    //private BufferedReader br;
    protected ObjectInputStream is;
	protected ObjectOutputStream os;
	
	
    //protected BufferedReader is;
    //protected PrintWriter os;

    protected String serverAddress;
    protected int serverPort;

    /**
     *
     * @param address IP address of the server, if you are running the server on the same computer as client, put the address as "localhost"
     * @param port port number of the server
     */
    public ConnectionToServer(String address, int port)
    {
        serverAddress = address;
        serverPort    = port;
    }

    /**
     * Establishes a socket connection to the server that is identified by the serverAddress and the serverPort
     */
    public void Connect()
    {
        try
        {
        
            s=new Socket(serverAddress, serverPort);
            
            //br= new BufferedReader(new InputStreamReader(System.in));
            /*
            Read and write buffers on the socket
             */
            //is = new DataInputStream(s.getInputStream());
            os = new ObjectOutputStream(s.getOutputStream());
            is = new ObjectInputStream(s.getInputStream());
			
			
			
            //is = new BufferedReader(new InputStreamReader(s.getInputStream()));
            //os = new PrintWriter(s.getOutputStream());

            System.out.println("Successfully connected to " + serverAddress + " on port " + serverPort);
        }
        catch (IOException e)
        {
            //e.printStackTrace();
            System.err.println("Error: no server has been found on " + serverAddress + "/" + serverPort);
        }
    }

    /**
     * sends the message String to the server and retrives the answer
     * @param message input message string to the server
     * @return the received server answer
     * @throws ClassNotFoundException 
     */
   
    public TCPPayload SendForAnswer(String message) throws ClassNotFoundException 
    {
    	TCPPayload from = null;
       // String response = new String();
        try
        {
            /*
            Sends the message to the server via PrintWriter
             */
        	//default TCPPayload
        	TCPPayload send_to_server = new TCPPayload(0, 0, message.length(), message);
        	os.writeObject(send_to_server);
           // os.println(message);
            os.flush();
            /*
            Reads a line from the server via Buffer Reader
             */
            //response = is.readLine();
            from = (TCPPayload) is.readObject();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.out.println("ConnectionToServer. SendForAnswer. Socket read Error");
        }
        return from;
    }


    /**
     * Disconnects the socket and closes the buffers
     */
    public void Disconnect()
    {
        try
        {
            is.close();
            os.close();
            //br.close();
            s.close();
            System.out.println("ConnectionToServer. SendForAnswer. Connection Closed");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
