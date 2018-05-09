
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
//Public Key
class PuPair{
	BigInteger e, n; 
	public PuPair(BigInteger e1, BigInteger n1){
		e=e1;n=n1;
	}
}
//Private Key
class PrPair{
	BigInteger d, n;
	public PrPair(BigInteger d1, BigInteger n1){
		d=d1;n=n1;
	}
}
//RSA Implementation
public class RSA {
	//Convert Byte array to string
	public static String btos(byte [] ba){
		String s = "";
		System.out.println(ba.length);
		for(byte b: ba)
		{
			s+=Byte.toString(b);
		}
		return s;
	}
	//Generate Random Object
	private static SecureRandom random = new SecureRandom();
	//No. of bits of n 
	private int bits;
	//Constructor
	public RSA(int b){
		bits = b;
		System.out.println("RSA "+bits+" bits");
	}
	//Generate new Private, Public Pair
	public void generate() throws IOException{
		BigInteger n;
		BigInteger d;
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
		System.out.println("e: "+e);
		d = e.modInverse(phi);
		System.out.println("d: "+d);
	    //Write Public Key to File
	    BufferedWriter pu = new BufferedWriter(new FileWriter("Public.txt", false));
	    pu.write("e:");
	    pu.newLine();
	    pu.append(e.toString());
	    pu.newLine();
	    pu.append("n:");
	    pu.newLine();
	    pu.append(n.toString());
	    pu.close();
	    //Write Private Key to File
	    BufferedWriter pr = new BufferedWriter(new FileWriter("Private.txt", false));
	    pr.write("d:");
	    pr.newLine();
	    pr.append(d.toString());
	    pr.newLine();
	    pr.append("n:");
	    pr.newLine();
	    pr.append(n.toString());
	    pr.close();
		
	}
	//Ecrypt Big Integer
	public BigInteger encrypt(BigInteger k, BigInteger e,BigInteger N){
		return k.modPow(e, N);	
	}
	//Encrypt Byte Array
	public byte[] encrypt(byte[] k,BigInteger e,BigInteger N){
		return (new BigInteger(k)).modPow(e, N).toByteArray();	
	}
	//Decrypt Big Integer
	public BigInteger decrypt(BigInteger k, BigInteger d, BigInteger N){
		return k.modPow(d, N);	
	}
	//Decrypt Byte Array
	public byte[] decrypt(byte[] k, BigInteger d, BigInteger N){
		return (new BigInteger(k)).modPow(d, N).toByteArray();	
	}
	
	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		//Different no. of key bits
		//64 - 128 - 256 - 512 - [[1024]] - 2048 - 3072 - 15360**
		//KS bits < N bits
		RSA rsa = new RSA(1024);
		//rsa.generate();
		KeyGenerator kg = KeyGenerator.getInstance("DES");
		kg.init(56);
		
		SecretKey  sk = kg.generateKey();
				
		while(new BigInteger(sk.getEncoded()).compareTo(BigInteger.ZERO)<0){
			sk = kg.generateKey();
		}
		
		 //Read Public Key
		BufferedReader pu = new BufferedReader(new FileReader(new File("Public.txt")));
		String st;
		st = pu.readLine();
		BigInteger e = new BigInteger(pu.readLine());
		st = pu.readLine();
		BigInteger n = new BigInteger(pu.readLine());
		
		byte[] msg = sk.getEncoded();
		
		long startTime = System.nanoTime();
		//Encrypt
		byte[] enc = rsa.encrypt(msg,e,n);
		long endTime = System.nanoTime();
		//Read Private Key
		BufferedReader pr = new BufferedReader(new FileReader("Private.txt"));
		
		st = pr.readLine();
		BigInteger d = new BigInteger(pr.readLine());
		st = pr.readLine();
		BigInteger n2 = new BigInteger(pr.readLine());
		//Decrypt
		byte[] dec = rsa.decrypt(enc,d,n2);
		
		Key k = new SecretKeySpec(dec,0, dec.length,"DES");
		byte[] k2 = k.getEncoded();
		
		System.out.println("Original  Key bytes: "+ RSA.btos(msg));
		System.out.println("Decrypted key bytes: "+ RSA.btos(dec));
		long duration = (endTime - startTime);
		System.out.println("Time : "+duration);

	}

}
