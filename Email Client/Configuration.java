package cu.cs.cpsc215.project4;

import java.io.Serializable;

public class Configuration implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String smtpHost;
    private int smtpPort;
    private String smtpPrimaryEmail;
    private String imapHost;
    
    public Configuration(String host, int port, String email, String imap){
    	smtpHost = host;
    	smtpPort = port;
    	smtpPrimaryEmail = email;
    	imapHost = imap;
    }

	public String getSmtpHost() {
		return smtpHost;
	}

	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}

	public int getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(int smtpPort) {
		this.smtpPort = smtpPort;
	}

	public String getSmtpPrimaryEmail() {
		return smtpPrimaryEmail;
	}

	public void setSmtpPrimaryEmail(String smtpPrimaryEmail) {
		this.smtpPrimaryEmail = smtpPrimaryEmail;
	}
	
	public String getImapHost(){
		return imapHost;
	}
	
	public void setImapHost(String host){
		this.imapHost = host;
	}
}
