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
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.search.FromTerm;
import javax.mail.search.SearchTerm;
import javax.xml.bind.DatatypeConverter;

public class Receiver {
	public static byte[] dec(Cipher cipher,byte[] enc_email,SecretKey k) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
		cipher.init(Cipher.DECRYPT_MODE, k);
        byte[] original = cipher.doFinal(enc_email);
        return original;
	}	
	public static PrPair getPrivate() throws IOException{
		BufferedReader pr = new BufferedReader(new FileReader("Private.txt"));
		pr.readLine();
		BigInteger d = new BigInteger(pr.readLine());
		pr.readLine();
		BigInteger n = new BigInteger(pr.readLine());
		return new PrPair(d,n);
	}
	public static String receiveEmail(String user, String password,String from) {  
		  try {  
		   //1) get the session object  
		   Properties properties = new Properties();  
		   properties.setProperty("mail.store.protocol", "imaps");
		   Session emailSession = Session.getDefaultInstance(properties);  
		     
		   //2) create the POP3 store object and connect with the pop server  
		   Store emailStore = emailSession.getStore("imaps");  
		   emailStore.connect("imap.gmail.com",user, password);  
		  
		   //3) create the folder object and open it  
		   Folder emailFolder = emailStore.getFolder("INBOX");  
		   emailFolder.open(Folder.READ_ONLY);  
		  
		   SearchTerm sender = new FromTerm(new InternetAddress(from));
		   Message messages[] = emailFolder.search(sender);
		   
		   //4) retrieve the messages from the folder in an array and print it  
		   String m=null;
		   for (int i = messages.length-1; i >messages.length-2&&messages.length!=0; i--) {  
		    Message message = messages[i];  
		    System.out.println("---------------------------------");  
		    System.out.println("Subject: " + message.getSubject());  
		    System.out.println("From: " + message.getFrom()[0]);  
		    System.out.println("Text: " + message.getContent().toString());
			m=messages[messages.length-1].getContent().toString();
		   }  
		   //5) close the store and folder objects  
		   emailFolder.close(false);  
		   emailStore.close();  
		   return m;

		  
		  } catch (NoSuchProviderException e) {System.out.println(e.getMessage());}   
		  catch (MessagingException e) {System.out.println(e.getMessage());}  
		  catch (IOException e) {System.out.println(e.getMessage());}  
		  return null;
		 }  
	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException,  IllegalBlockSizeException, BadPaddingException, IOException {
		// TODO Auto-generated method stub
	////////////////////////////////////////////Receive email/////////////////////////////////
	Scanner sc=new Scanner(System.in);  
	System.out.println("Enter your email: ");
	String email=sc.nextLine();  
	System.out.println("Enter your password: ");
	String password=sc.nextLine();  
	System.out.println("From: ");
	String from=sc.nextLine(); 
	String received_msg=receiveEmail(email,password,from);
	if(received_msg!=null)
	{
		String parts[] = received_msg.split("\n", 2);
		parts[0]=parts[0].trim();
	
		int key_len=Integer.parseInt(parts[0]);
		System.out.println("len "+key_len);
	
		String sent_key2=parts[1].substring(0,key_len);
		String sent_msg2=parts[1].substring(key_len,parts[1].length());
	
		byte[] enc_key = DatatypeConverter.parseBase64Binary(sent_key2);
		System.out.println("key "+DatatypeConverter.printBase64Binary(enc_key));
		
		byte[] encryptedMessage = DatatypeConverter.parseBase64Binary(sent_msg2);
		System.out.println("msg "+DatatypeConverter.printBase64Binary(encryptedMessage));
		////////////////////////////////////////////Key Decryption/////////////////////////////////////////
		PrPair pr = getPrivate();
		RSA rsa = new RSA(64);
		//rsa.generate();
		byte[] decryptedKey = rsa.decrypt(enc_key,pr.d,pr.n);
		SecretKey ks = new SecretKeySpec(decryptedKey,0, decryptedKey.length,"DES");
		System.out.println("decryptedkey  "+DatatypeConverter.printBase64Binary(ks.getEncoded()));
		
		///////////////////////////////////////////Email Decryption///////////////////////////////////////////
		Cipher cipher = Cipher.getInstance("DES");
		byte[] decrypted_msg;
		try {
			decrypted_msg = dec(cipher,encryptedMessage,ks);
			System.out.println("Decrypted email    :: " +new String(decrypted_msg));
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
	           System.out.println(e.getMessage());
		}
	}
	else
	{
		System.out.println("No emails matched your search");
	}
	}

}
