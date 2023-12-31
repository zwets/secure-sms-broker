package it.zwets.sms.crypto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * General helpers for PKI
 * @author zwets
 */
public class PkiUtils {

	/**
	 * Encrypt plaintext to ciphertext using a public key.
	 * 
	 * @param key an RSA public key
	 * @param plaintext the bytes to encode
	 * @return the encoded plaintext
	 * @throws RuntimeException for all underlying JCE exceptions
	 */
	public static byte[] encrypt(PublicKey key, byte[] plaintext)
	{
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key);  
			return cipher.doFinal(plaintext);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			throw new RuntimeException("PKI error during encryption: %s".formatted(e.getMessage()), e);
		}
	}

	/**
	 * Decrypt ciphertext to plaintext using a private key.
	 * 
	 * @param key an RSA private key
	 * @param ciphertext the ciphertext to decrypt
	 * @return the decoded ciphertext
	 */
	public static byte[] decrypt(PrivateKey key, byte[] ciphertext) 
	{
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
			cipher.init(Cipher.DECRYPT_MODE, key);  
			return cipher.doFinal(ciphertext);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			throw new RuntimeException("PKI error during decryption: %s".formatted(e.getMessage()), e);
		}
	}
	
	/**
	 * Read public key from file.
	 * 
	 * @param fileName file in DER format containing the the public key
	 * @return PublicKey object
	 * @throws RuntimeException for any of the underlying exceptions
	 */
	public static PublicKey readPublicKey(String fileName) {
		try {
			byte[] bytes = Files.readAllBytes(Path.of(fileName));
			X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(bytes, "RSA");
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return keyFactory.generatePublic(publicSpec);		
		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new RuntimeException("PKI error reading public key: %s".formatted(e.getMessage()), e);
		}
	}

	/**
	 * Read private key from file.
	 * 
	 * @param fileName file in DER format containing the private key
	 * @return the key object
	 * @throws RuntimeException for any of the underlying exceptions
	 */
	public static PrivateKey readPrivateKey(String fileName) {
		try {
			byte[] bytes = Files.readAllBytes(Path.of(fileName));
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes, "RSA");
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return keyFactory.generatePrivate(keySpec);		
		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new RuntimeException("PKI error reading private key: %s".formatted(e.getMessage()), e);
		}
	}

	/**
	 * Main function exercises the helper functions.
	 * @param args
	 */
	public static void main(String[] args)
	{
		String outFile = "/dev/stdout";

		if (args.length < 2 || !(args[0].equals("encrypt") || args[0].equals("decrypt"))) {
			System.err.println("Usage: PkiHelper encrypt KEYFILE [INFILE] | decrypt KEYFILE [INFILE]");
		}
		else {
			if (args.length == 3) {
				outFile = args[2];
			}
			try {
				if (args[0].equals("encrypt")) {
					PublicKey key = readPublicKey(args[1]);
					byte[] bytes = encrypt(key, Files.readAllBytes(Path.of("/dev/stdin")));
					Files.write(Path.of(outFile), bytes);
				}
				else if (args[0].equals("decrypt")) {
					PrivateKey key = readPrivateKey(args[1]);
					byte[] bytes = decrypt(key, Files.readAllBytes(Path.of("/dev/stdin")));
					Files.write(Path.of(outFile), bytes);
				}
			}
			catch (Exception e)
			{
			e.printStackTrace();
			}
		}
	}
}
