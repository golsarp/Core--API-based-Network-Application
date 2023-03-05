import java.io.IOException;
import java.net.SocketException;
import java.util.Scanner;

public class ClientMain {

	public static void main(String args[]) throws IOException, ClassNotFoundException
    {
		
		
		
			
		
        ConnectionToServer connectionToServer = new ConnectionToServer(ConnectionToServer.DEFAULT_SERVER_ADDRESS, ConnectionToServer.DEFAULT_SERVER_PORT);
        connectionToServer.Connect();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Succesfully connected to server ");
        System.out.println("Enter a valid username in order to get acess ");
        System.out.println("For the password you have 3 attempts ");
        String message = scanner.nextLine();
        while (!message.equalsIgnoreCase("QUIT"))
        {
        	
        	String server_response = connectionToServer.SendForAnswer(message).toString();
            System.out.println("Response from server: " + server_response);
            
           
            message = scanner.nextLine();
        }
        connectionToServer.Disconnect();
    
		 }
		
        
	
	
}
