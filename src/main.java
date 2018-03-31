import javax.crypto.BadPaddingException;
import java.nio.ByteBuffer;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

import java.math.BigInteger;
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
	public static String btos(byte [] ba){
		String s = "";
		for(byte b: ba)
		{
			s+=Byte.toString(b);
		}
		return s;
	}
	public static void main(String[] args) throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException{
		// TODO Auto-generated method stub
		/////////////////////// email to send //////////////////////////////////////////////
		byte[] msg = "I hope this mail finds you well :D ".getBytes();
        //////////////////////////////////////Session Key/////////////////////////////////////////////
		KeyGenerator kg = KeyGenerator.getInstance("DES");
		kg.init(56);
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
		RSA rsa = new RSA(64);
		byte[] keyBytes = k.getEncoded();
		byte[] encryptedKey = rsa.encrypt(keyBytes);

		/////////////////////////////////////////////Send email/////////////////////////////////////
		ByteBuffer sent_msg = ByteBuffer.allocate((encryptedKey.length + encrypted_msg.length) + 4);
		sent_msg.putInt(encryptedKey.length);
		sent_msg.put(encryptedKey);
		sent_msg.put(encrypted_msg);
		System.out.println("Sent msg :: "+DatatypeConverter.printBase64Binary(sent_msg.array()));
		////////////////////////////////////////////Receive email/////////////////////////////////
		ByteBuffer received_msg = ByteBuffer.wrap(sent_msg.array());
	    int keyLength = received_msg.getInt();
	    byte[] enc_key = new byte[keyLength];
        received_msg.get(enc_key);
        byte[] encryptedMessage = new byte[received_msg.remaining()];
        received_msg.get(encryptedMessage);
        
		////////////////////////////////////////////Key Decryption/////////////////////////////////////////
		byte[] decryptedKey = rsa.decrypt(enc_key);
		SecretKey ks = new SecretKeySpec(decryptedKey,0, decryptedKey.length,"DES");
		System.out.println("decryptedkey  "+DatatypeConverter.printBase64Binary(ks.getEncoded()));

		///////////////////////////////////////////Email Decryption///////////////////////////////////////////
		byte[] decrypted_msg=dec(cipher,encryptedMessage,ks);
		System.out.println("Decrypted email    :: " +new String(decrypted_msg));
		
		       
	}

}
