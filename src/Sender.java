import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.bind.DatatypeConverter;


public class Sender {
	public static byte[] enc(Cipher cipher,byte[] email,SecretKey k) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
		cipher.init(Cipher.ENCRYPT_MODE, k);
	    byte[] result = cipher.doFinal(email);
	    return result;
	}
	public static PuPair getPublic()throws IOException{
		BufferedReader pu = new BufferedReader(new FileReader(new File("Public.txt")));
		pu.readLine();
		BigInteger e = new BigInteger(pu.readLine());
		pu.readLine();
		BigInteger n = new BigInteger(pu.readLine());
		return new PuPair(e,n);
	}
	public static void send(String username,String password,String to,String sub,String em)
	{
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(to));
			message.setSubject(sub);
			message.setText(em);

			Transport.send(message);

			System.out.println("Your message has been sent");

		} catch (MessagingException e) {
			//throw new RuntimeException(e);
           System.out.println(e.getMessage());
		}
	}
	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
		// TODO Auto-generated method stub
	/////////////////////// email to send //////////////////////////////////////////////
	Scanner sc=new Scanner(System.in);  
	System.out.println("Enter your email: ");
	String email=sc.nextLine();  
	System.out.println("Enter your password: ");
	String password=sc.nextLine();  
	System.out.println("To: ");
	String to=sc.nextLine(); 
	System.out.println("Subject: ");
	String subject=sc.nextLine(); 
	System.out.println("Text: ");
	String text=sc.nextLine();
	byte[] msg = text.getBytes("UTF-8");

	//////////////////////////////////////Session Key/////////////////////////////////////////////
	KeyGenerator kg = KeyGenerator.getInstance("DES");
	SecretKey  k = kg.generateKey();
	
	//Make sure the key has a positive value for RSA encryption
	while(new BigInteger(k.getEncoded()).compareTo(BigInteger.ZERO)<0){
	k = kg.generateKey();
	}
	
	System.out.println("Secret Key value     :: " + DatatypeConverter.printBase64Binary(k.getEncoded()));
	///////////////////////////////////////////Email Encryption//////////////////////////////////////////
	Cipher cipher = Cipher.getInstance("DES");
	byte[] encrypted_msg=enc(cipher,msg,k);
	
	///////////////////////////////////////////key encryption////////////////////////////////////////
	RSA rsa = new RSA();
	byte[] keyBytes = k.getEncoded();
	PuPair pu = getPublic();
	byte[] encryptedKey = rsa.encrypt(keyBytes,pu.e,pu.n);
	
	/////////////////////////////////////////////Send email/////////////////////////////////////
	System.out.println("encryptedKey "+DatatypeConverter.printBase64Binary(encryptedKey));
	String sent_key = DatatypeConverter.printBase64Binary(encryptedKey);
	System.out.println("Sent key :: "+sent_key);
	
	System.out.println("encrypted_msg "+DatatypeConverter.printBase64Binary(encrypted_msg));
	String sent_msg = DatatypeConverter.printBase64Binary(encrypted_msg);
	System.out.println("Sent msg2 :: "+sent_msg);
	String to_send=sent_key.length()+"\n"+sent_key+"\n"+sent_msg;
	send(email,password,to,subject,to_send);
	}

}
