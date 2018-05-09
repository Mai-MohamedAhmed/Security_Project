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
		if(x.mod(new BigInteger("2")).equals(new BigInteger("0")))
		{
			if(x.isProbablePrime(1))
			{
				BigInteger phi=((x.subtract(new BigInteger("1")) ).multiply(x.subtract(new BigInteger("1"))));
                return phi;
			}
			x=x.subtract(new BigInteger("1"));
		}
      for(BigInteger i = x; i.compareTo(new BigInteger("2"))>=0;i=i.subtract(new BigInteger("2"))) {
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
      if(n.mod(new BigInteger("2")).equals(new BigInteger("0")))
      {
    	  BigInteger factor1 = new BigInteger("2");
          BigInteger factor2 = n.divide(factor1);
          if(factor2.isProbablePrime(1))
          {
                BigInteger phi=factor2.subtract(new BigInteger("1"));
                return phi;
          }
      }
		return null;
  }
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
			////////////////////////////////////Attack///////////////////////////////////////////////////////////
			PuPair pu = getPublic();
			BigInteger e=pu.e;
			BigInteger n=pu.n;
			long startTime = System.nanoTime();
			BigInteger phi=factorizeN(n);
			BigInteger d=e.modInverse(phi);
			long endTime = System.nanoTime();
			System.out.println("Attack time :"+(endTime-startTime));
			System.out.println("n = "+n);
			System.out.println("d = "+d);
		}
	//}

}
