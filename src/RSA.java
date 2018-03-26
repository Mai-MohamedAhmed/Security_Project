
import java.math.BigInteger;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Random;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class RSA {
	public static String btos(byte [] ba){
		String s = "";
		for(byte b: ba)
		{
			s+=Byte.toString(b);
		}
		return s;
	}

	private static SecureRandom random = new SecureRandom();
	private BigInteger publicKey;
	private BigInteger privateKey;
	private BigInteger n;
	public RSA(int bits){
		System.out.println("RSA "+bits+" 	bits");
		BigInteger p = BigInteger.probablePrime(bits/2, random);
		BigInteger q = BigInteger.probablePrime(bits/2, random);
		System.out.println("p: "+p+"\nq: "+q);
		n = p.multiply(q);
		System.out.println("n: "+n);
		BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
		
		BigInteger e = new BigInteger(phi.bitLength(),random);
		//1<e<phi(n) , gcd(phi(n),e)=1
		//compareTo returns: -1 -> less than | 0 -> equal | 1 -> greater than
		while(e.compareTo(BigInteger.ONE)<=0 ||
				e.compareTo(phi) >= 0 ||
				!e.gcd(phi).equals(BigInteger.ONE)){
			e = new BigInteger(phi.bitLength(),random);
		}
		publicKey = e;
		System.out.println("e: "+e);
		privateKey = e.modInverse(phi);
		System.out.println("d: "+privateKey);
	}
	
	public BigInteger encrypt(BigInteger k){
		return k.modPow(publicKey, n);	
	}
	
	public byte[] encrypt(byte[] k){
		return (new BigInteger(k)).modPow(publicKey, n).toByteArray();	
	}
	
	public BigInteger decrypt(BigInteger k){
		return k.modPow(privateKey, n);	
	}
	
	public byte[] decrypt(byte[] k){
		return (new BigInteger(k)).modPow(privateKey, n).toByteArray();	
	}
	
	public static void main(String[] args) throws NoSuchAlgorithmException {
		//64 - 128 - 256 - 512 - [[1024]] - 2048 - 3072 - 15360**
		//KS bits < N bits
		RSA rsa = new RSA(64);
		BigInteger m = new BigInteger(31,new Random());
		
		KeyGenerator kg = KeyGenerator.getInstance("DES");
		kg.init(56);
		
		SecretKey  sk = kg.generateKey();
		while(new BigInteger(sk.getEncoded()).compareTo(BigInteger.ZERO)<0){
			sk = kg.generateKey();
		}
		//string from key
		//SecretKey sk = KeyGenerator.getInstance("DES").generateKey();
		//String s = Base64.getEncoder().encodeToString(sk.getEncoded());
	
		byte[] msg = sk.getEncoded();
		byte[] enc = rsa.encrypt(msg);
		byte[] dec = rsa.decrypt(enc);
		
		Key k = new SecretKeySpec(dec,0, dec.length,"DES");
		//String sd = Base64.getEncoder().encodeToString(k.getEncoded());
		byte[] k2 = k.getEncoded();
		System.out.println("Original  Key bytes: "+ RSA.btos(msg));
		System.out.println("Decrypted key bytes: "+ RSA.btos(dec));
		System.out.println("Decrypted Key: "+ RSA.btos(k2));
		
		
		
		
		//KeyGenerator kg=KeyGenerator.getInstance("DES");
		//Key key=kg.generateKey();
		//String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
		//System.out.println(key);
		//System.out.println(encodedKey);
		/*
		System.out.println("M: "+m);
		long startTime = System.nanoTime();
		BigInteger c = rsa.encrypt(m);
		long endTime = System.nanoTime();
		System.out.println("c: "+c);
		BigInteger d = rsa.decrypt(c);
		System.out.println("d: "+d);
		long duration = (endTime - startTime);
		System.out.println("Time : "+duration);
		*/
	}

}
