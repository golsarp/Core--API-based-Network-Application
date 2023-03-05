import java.io.Serializable;

@SuppressWarnings("serial")
public class TCPPayload implements Serializable {
	private int phase;
	private int type ;
	private int size;
	private String app;
	private int stringIndicator;
	
	

	public TCPPayload(int phase ,int type , int size , String app) {
		
		this.setPhase(phase);
		this.setType(type);
		this.setSize(size);
		this.setApp(app);
		stringIndicator = 1;
		
		
		
	}
	//this part returns the string section of TCP payload
	public String toString() {
		if(stringIndicator == 1) {
			return  app;
		}
		return "error";
	}
    
	
	public int getPhase() {
		return phase;
	}

	public void setPhase(int phase) {
		this.phase = phase;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	/*
	public boolean isStringIndicator() {
		return stringIndicator;
	}
     */
	
	public void setStringIndicator(int stringIndicator) {
		this.stringIndicator = stringIndicator;
	}
	
	
}
