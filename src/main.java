import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

import java.security.InvalidKeyException;
import java.security.Key;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;
import java.security.spec.*;
import javax.crypto.spec.*;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.KeyPair;
public class main {

	public static byte[] enc(Cipher cipher,byte[] email,SecretKey k) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
		cipher.init(Cipher.ENCRYPT_MODE, k);
        byte[] result = cipher.doFinal(email);
        return result;
	}
	public static byte[] dec(Cipher cipher,byte[] enc_email,SecretKey k) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
		cipher.init(Cipher.DECRYPT_MODE, k);
        byte[] original = cipher.doFinal(enc_email);
        return original;
	}
	public static void main(String[] args) throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException{
		// TODO Auto-generated method stub
		/////////////////////// email to send //////////////////////////////////////////////
		byte[] msg = "I hope this mail finds you well :D ".getBytes();
		/////////////////////////////////////////////////////////////////////////////////////
		byte[] encrypted_msg,decrypted_msg;
         /////////////////////////////////////pair keys////////////////////////////////////////////////
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
 		//keyGen.initialize(keylength);
        KeyPair pair = keyGen.generateKeyPair();
 		PrivateKey privateKey = pair.getPrivate();
 		PublicKey publicKey = pair.getPublic();
 		System.out.println("Public     :: " + publicKey.getEncoded());
        System.out.println("public :: " + publicKey);
        System.out.println("Private    :: " + privateKey.getEncoded());
        System.out.println("Private :: " + privateKey);
       
         //////////////////////////////////////Session Key/////////////////////////////////////////////
		KeyGenerator kg = KeyGenerator.getInstance("DES");
		kg.init(56);
		SecretKey  k = kg.generateKey();
		System.out.println("Secret Key value     :: " + DatatypeConverter.printBase64Binary(k.getEncoded()));
		///////////////////////////////////////////Email Encryption//////////////////////////////////////////
		Cipher cipher = Cipher.getInstance("DES");
		encrypted_msg=enc(cipher,msg,k);
		System.out.println("Encrypted email    :: " +new String(encrypted_msg));
		
		///////////////////////////////////////////key encryption////////////////////////////////////////
		SecretKey encryptedKey=k;
		//encryptedKey==RSA_enc(k,publicKey);
		/////////////////////////////////////////////Send email/////////////////////////////////////
		 String sent_msg;
		 sent_msg=encryptedKey.toString()+new String(encrypted_msg);
		 System.out.println("Sent msg :: "+sent_msg);
		 ////////////////////////////////////////////Receive email/////////////////////////////////
		 
		 ////////////////////////////////////////////Key Decryption/////////////////////////////////////////
		 SecretKey ecryptedKey=k;
		//encryptedKey==RSA_enc(k,publicKey);
		///////////////////////////////////////////Email Decryption///////////////////////////////////////////
		decrypted_msg=dec(cipher,encrypted_msg,k);
		System.out.println("Decrypted email    :: " +new String(decrypted_msg));
		
		       
	}

}
