import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Properties;
import java.util.Scanner;

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


public class Attacker {

	public static PuPair getPublic()throws IOException{
		BufferedReader pu = new BufferedReader(new FileReader(new File("Public.txt")));
		pu.readLine();
		BigInteger e = new BigInteger(pu.readLine());
		pu.readLine();
		BigInteger n = new BigInteger(pu.readLine());
		return new PuPair(e,n);
	}
	private static BigInteger sqrt(BigInteger n) {
		  BigInteger a = BigInteger.ONE;
		  BigInteger b = new BigInteger(n.shiftRight(5).add(new BigInteger("8")).toString());
		  while(b.compareTo(a) >= 0) {
		    BigInteger mid = new BigInteger(a.add(b).shiftRight(1).toString());
		    if(mid.multiply(mid).compareTo(n) > 0) b = mid.subtract(BigInteger.ONE);
		    else a = mid.add(BigInteger.ONE);
		  }
		  return a.subtract(BigInteger.ONE);
		}
	public static BigInteger factorizeN(BigInteger n) {
		BigInteger x=sqrt(n);
      for(BigInteger i = new BigInteger("2"); i.compareTo(x)<=0;i=i.add(new BigInteger("1"))) {
          if(n.mod(i).equals( new BigInteger("0"))) {
              BigInteger factor1 = i;
              BigInteger factor2 = n.divide(i);
              if(factor1.isProbablePrime(1) && factor2.isProbablePrime(1))
              {
	                BigInteger phi=((factor1.subtract(new BigInteger("1")) ).multiply(factor2.subtract(new BigInteger("1"))));
	                return phi;
              }
          }
      }
		return null;
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
		 //  Message messages[] = emailFolder.getMessages();  
		   System.out.println(messages.length);
		   String m=null;
		   for (int i = messages.length-1; i >messages.length-2&&messages.length!=0; i--) {  
		    Message message = messages[i];  
		    System.out.println("---------------------------------");  
		    System.out.println("Email Number " + (i + 1));  
		    System.out.println("Subject: " + message.getSubject());  
		    System.out.println("From: " + message.getFrom()[0]);  
		    System.out.println("Text: " + message.getContent().toString());
			m=messages[messages.length-1].getContent().toString();
		   }  
		   //5) close the store and folder objects  
		   emailFolder.close(false);  
		   emailStore.close();  
		   return m;

		  
		  } catch (NoSuchProviderException e) {e.printStackTrace();}   
		  catch (MessagingException e) {e.printStackTrace();}  
		  catch (IOException e) {e.printStackTrace();}  
		  return null;
		 } 
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
	/*	Scanner sc=new Scanner(System.in);  
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
			System.out.println("key "+DatatypeConverter.printBase64Binary(enc_key));*/
			////////////////////////////////////Attack///////////////////////////////////////////////////////////
			PuPair pu = getPublic();
			BigInteger e=pu.e;
			BigInteger n=pu.n;
			if(n.isProbablePrime(1))
				System.out.println("n must be composite number");
			else
			{
				long startTime = System.nanoTime();
				BigInteger phi=factorizeN(n);
				BigInteger d=e.modInverse(phi);
				long endTime = System.nanoTime();
				System.out.println("Attack time :"+(endTime-startTime));
				if(d.equals(null))
					System.out.println("can't find d");
				else
				{
					System.out.println("d = "+d);
					//byte[] dec=new BigInteger(enc_key).modPow(privateKey, n).toByteArray();
					//SecretKey att_k = new SecretKeySpec(decryptedKey,0, decryptedKey.length,"DES");
					//System.out.println("crackedkey  "+DatatypeConverter.printBase64Binary(att_k.getEncoded()));
				}
		
			}
		}
	//}

}
