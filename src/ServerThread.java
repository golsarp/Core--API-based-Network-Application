import java.io.BufferedReader;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Scanner;

/*
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
*/




public class ServerThread extends Thread {
	
	//protected DataInputStream is;
	//protected BufferedReader is;
	protected ObjectInputStream is;
    protected ObjectOutputStream os;
    protected Socket s;
    private String line = new String();
    private String lines = new String();
    private TCPPayload payload = null;
    TCPPayload message ;

    private HashMap<String,String> users = new HashMap<String,String>();
    private HashMap<String,String> tokens = new HashMap<String,String>();
    

    /**
     * Creates a server thread on the input socket
     *
     * @param s input socket to create a thread on
     */
    public ServerThread(Socket s) throws IOException
    {
        this.s = s;
      
    }
   
    
    public void Map( ) throws FileNotFoundException {
    	
    	String path = "src/Clients.txt";
    	File file = new File(path);
    	Scanner scanner = new Scanner(file);
    	String username;
    	String password;
    	while(scanner.hasNext()) {
    		username = scanner.nextLine();
    		password = scanner.nextLine();
    		users.put(username, password);
    		
    	}
    	System.out.println(users.toString());
    }
    
    public String Hash(String username) {
    	String result = username.concat("23");
    	return result;
    }

    
    
    public TCPPayload authenticate (ObjectOutputStream os , ObjectInputStream is, TCPPayload payload) throws ClassNotFoundException, IOException {
    	
    	String input = payload.getApp();
    	//System.out.println("input :" + input);
    	//username part is succesfull
    	if(users.containsKey(input)) {
    		//direk fonksiyonu çağır 
    		
    		String username = input;
    		String m = "Enter password for username " + username ;
    		// bunu return et 
    		TCPPayload not_fail = new TCPPayload(0,1, m.length(),m);
    		os.writeObject(not_fail);
    		os.flush();
    		return not_fail;
    		
    	// username part is not succesfull
    	}else {
    		String fail = "Invalid Username , Socket closed";
    		TCPPayload fail_payload = new TCPPayload(0,2, fail.length(),fail);
    		os.writeObject(fail_payload);
    		os.flush();
    		return fail_payload;
    		
    	}
    	
    	
    }
    
    public TCPPayload login (ObjectOutputStream os , ObjectInputStream is, TCPPayload payload, String pass_in, String username) throws ClassNotFoundException, IOException {
    	
    	String pass = users.get(username);
		int life = 3;
		int count = 0;
	
		
		while(count!= 3) {
			count++;
			if(pass_in.equalsIgnoreCase(pass)) {
				String token = Hash(username);
				//save usernames and tokens 
				tokens.put(username, token);
    			String succes_message = "Pass correct, Authentication is completed. \nYour token is : " + token + "\n Enter your token to start Query Phase";
    			TCPPayload succes = new TCPPayload(0,3,succes_message.length(),succes_message);
    			return succes;
    		}
			    
			if(count== 3) {
				String password_fail = "All attempts used , connection terminated !!";
				TCPPayload pass_fail = new TCPPayload(0, 2, password_fail.length(),password_fail);
				os.writeObject(pass_fail);
				os.flush();
				break;
			}
			
				String auth_challenge = "Password incorrect , " + (life - count) + " attempts remaining";
				TCPPayload wrong = new TCPPayload(0, 1, auth_challenge.length(),auth_challenge);
				os.writeObject(wrong);
				os.flush();
				///////////
				payload = (TCPPayload)is.readObject();
				pass_in = payload.getApp();
			    ////////////
				
			
			
			
			
		}
		
		
    	
    	return payload;
    }
    
    
    public TCPPayload CheckToken(ObjectOutputStream os , ObjectInputStream is, String token , String Username ) throws IOException {
    	
    	
    	if(token.equalsIgnoreCase(tokens.get(Username)) ) {
    		String tokenMes = " \n Token and username matched, enter requests with format journal or article SearchString (issn num for journals, num-num) token username: \n If you enter your token or username wrong connection will terminate \n Type quit after your have no requests ";
    		TCPPayload token_succ = new TCPPayload(1,3,tokenMes.length(),tokenMes);
    		os.writeObject(token_succ);
    		os.flush();
    		return token_succ;
    		
    	}else {
    		String tokenMes = "Token and username did not match , connection terminated";
    		TCPPayload token_fail = new TCPPayload(1,2,tokenMes.length(),tokenMes);
    		os.writeObject(token_fail);
    		os.flush();
    		return token_fail;
    		
    	}
    	
    	
    	
    	
    	
    }
    
    public boolean checker(String token , String username  ) {
    	if(token.equalsIgnoreCase(tokens.get(username))) {
    		return true;
    	}else {
    		return false;
    	}
    }
    
    
    public void  dataRequests(ObjectOutputStream os , ObjectInputStream is, TCPPayload payload) throws ClassNotFoundException, IOException {
    	//get type and string
    	payload = (TCPPayload)is.readObject();
    	TCPPayload dataMessage = null ;
    	
    	String[] splitted  = payload.getApp().split("\\s+");
    	TCPPayload check = null;
    	System.out.println(tokens.toString());
    	
    	boolean indicator ;
    	while(true) {
    		indicator = checker(splitted[2], splitted[3]);
    		if(indicator == false) {
    			
    			break;
    		}
    		
    		
    		if(splitted[0].equalsIgnoreCase("article")) {
    			//System.out.println("article Data");
    			//System.out.println("splitted[1]" + splitted[1]);
    			dataMessage = getArticle(splitted[1]);
    			os.writeObject(dataMessage);
        		os.flush();
    		}
    		//do it for article 
    		

    		if(splitted[0].equalsIgnoreCase("journal")) {
    			//System.out.println("journal Data");
    			//System.out.println("splitted[1]" + splitted[1]);
    			dataMessage = getJournal(splitted[1]);
    			os.writeObject(dataMessage);
        		os.flush();
    		}
    		
    		
    		
    		
    	
    		
    		payload = (TCPPayload)is.readObject();
    		splitted  = payload.getApp().split("\\s+");
    		
    	}
    	//for articles
    	//https://core.ac.uk:443/api-v2/articles/search/Blockchain?apiKey=20hIsS1F5j4D2C2iXrg4Wxf7VTp4Xt1j
    	//for journals with issn number 
    	//https://core.ac.uk/api-v2/journals/get/1453-1305?apiKey=20hIsS1F5j4D2C2iXrg4Wxf7VTp4Xt1j
    	
    	
    }
    
    
    public TCPPayload getJournal(String search) throws IOException{
    	TCPPayload res;
    	URL url = new URL("https://core.ac.uk/api-v2/journals/get/"+search+"?apiKey=20hIsS1F5j4D2C2iXrg4Wxf7VTp4Xt1j");
    	System.out.println("URL" + url);
    	HttpURLConnection conn = (HttpURLConnection)url.openConnection();
    	conn.setRequestMethod("GET");
    	conn.connect();
    	int responsecode = conn.getResponseCode();
    	if(responsecode != 200)
    		throw new RuntimeException("HttpResponseCode: " +responsecode);
    	else {
    		
    		Scanner sc = new Scanner(url.openStream());
    		String inline = "";
    		while(sc.hasNext())
    		{
    		inline+=sc.nextLine();
    		}
    		System.out.println("\nJSON data in string format");
    		System.out.println("inline" + inline);
    		sc.close();
    		
    		res = new TCPPayload(1,3,inline.length(),inline);
    		return res;
    	}
    
    }
    
    
    
    public TCPPayload getArticle(String search ) throws IOException {
    	TCPPayload res;
    	URL url = new URL("https://core.ac.uk:443/api-v2/articles/search/"+search+"?apiKey=20hIsS1F5j4D2C2iXrg4Wxf7VTp4Xt1j");
    	System.out.println("URL" + url);
    	HttpURLConnection conn = (HttpURLConnection)url.openConnection();
    	conn.setRequestMethod("GET");
    	conn.connect();
    	int responsecode = conn.getResponseCode();
    	if(responsecode != 200)
    		throw new RuntimeException("HttpResponseCode: " +responsecode);
    	else {
    		
    		Scanner sc = new Scanner(url.openStream());
    		String inline = "";
    		while(sc.hasNext())
    		{
    		inline+=sc.nextLine();
    		}
    		//System.out.println("\nJSON data in string format");
    		//System.out.println("inline" + inline);
    		sc.close();
    		
    		res = new TCPPayload(1,3,inline.length(),inline);
    		return res;
    		
    		/*
    		JSONParser parse = new JSONParser();
    		JSONObject jobj = (JSONObject)parse.parse(inline);
    		JSONArray jsonarr_1 = (JSONArray) jobj.get("results");
    		int n = jsonarr_1.size();
    		for(int i =0 ;i<n;i++) {
    			JSONObject jsonobj_1 = (JSONObject)jsonarr_1.get(i);
    			
    		}
    		*/
    		
    	}
    	
    }
    
    
    
    /**
     * The server thread, echos the client until it receives the QUIT string from the client
     */
  
	public void run() 
    {
        try
        {   
        	
        	 os = new ObjectOutputStream(s.getOutputStream());
        	 is = new ObjectInputStream(s.getInputStream());
            //is = new DataInputStream(s.getInputStream());
           

        }
        catch (IOException e)
        {
            System.err.println("Server Thread. Run. IO error in server thread");
        }

        try
        {
            //line = is.readLine();
            
        	payload = (TCPPayload)is.readObject();
        	
        	String username  = payload.getApp();
           
            while (!payload.getApp().equalsIgnoreCase("quit"))
            {
		        lines = "Client messaged : " + payload.getApp() + " at  : " + Thread.currentThread().getId();
            	//lines =  payload.getApp();
            	
                
                System.out.println("Client " + s.getRemoteSocketAddress() + " sent :  " + lines);
                //authenticate client here 
                message  = authenticate(os ,is,payload);
                if(message.getType() == 2 ) {
                	// olmalı 
                	s.close();
                	break;
                }
                
                payload = (TCPPayload)is.readObject();
                
                //username exists , login phase 
                if(message.getType() == 1) {
                	
                	
                	TCPPayload login_res = login(os,is ,payload,payload.getApp(),username);
                	os.writeObject(login_res);
                    os.flush();
                    //check token and username 
                    if(login_res.getType() == 3) {
                    	//ask for data input 
                    	//get token 
                    	payload = (TCPPayload)is.readObject();
                    	String token = payload.getApp();
                    	TCPPayload token_res = CheckToken(os,is,token,username);
                    	//System.out.println("phase" + token_res.getPhase() + "type " + token_res.getType());
                    	//get Data from API
                    	if(token_res.getPhase() == 1 && token_res.getType() == 3) {
                    		//System.out.println("queryyy");
                    		dataRequests(os,is,payload);
                    		//getJournal("chain");
                    		
                    	}
                    	  
                    	
                    	
                    	
                    }
                   
                }
             
                
            }
        }
        
        catch(SocketTimeoutException e) {
        	
        	System.out.println("Client timed out at inet adress" + s.getRemoteSocketAddress());
        	
        
        	
        }
        catch (IOException e)
        {
            line = this.getName(); //reused String line for getting thread name
            System.err.println("Server Thread. Run. IO Error/ Client " + line + " terminated abruptly");
        }
        
      
        
        catch (NullPointerException e)
        {
            line = this.getName(); //reused String line for getting thread name
            System.err.println("Server Thread. Run.Client " + line + " Closed");
        } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		//} catch (ParseException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		} 
        
		
        {
            try
            {
                System.out.println("Closing the connection");
                if (is != null)
                {
                    is.close();
                    System.err.println(" Socket Input Stream Closed");
                }

                if (os != null)
                {
                    os.close();
                    System.err.println("Socket Out Closed");
                }
                if (s != null)
                {
                    s.close();
                    System.err.println("Socket Closed");
                }

            }
            catch (IOException ie)
            {
                System.err.println("Socket Close Error");
            }
            
           
        }//end finally
    }

}
