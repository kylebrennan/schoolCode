package cu.cs.cpsc215.project4;

import java.io.Serializable;

public class Contact implements Serializable{
	private static final long serialVersionUID = 1L;
	private String name;
	private String postAddress;
	private String email;
	private String phone;

	public Contact(String name, String post, String email, String phone){
		this.name = name;
		this.postAddress = post;
		this.email = email;
		this.phone = phone;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setPostAddress(String post) {
		this.postAddress = post;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getName(){
		return name;
	}
	
	public String getPostalAddress(){
		return postAddress;
	}
	
	public String getEmail(){
		return email;
	}
	
	public String getPhone(){
		return phone;
	}
}
