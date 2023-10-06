package it.zwets.sms.crypto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Vault that does PKI backed by a keystore file.
 * 
 * The keystore file and its entries must be created with the Java
 * keytool, as there is no programmatic way to add key pairs to a
 * Java keystore.
 * 
 * The <code>keytool -genkeypair</code> command generates a key pair
 * and stores it in a keystore.  Entries in a keystore are identified
 * by aliases (string values that we call "key IDs" below).
 * 
 * To create the keystore and add entries, use keytool as follows:
 * <pre>
 * keytool -genkeypair -keyalg RSA -keysize 2048 -validity 36500 \
 *    -storepass PASSWORD -keystore FILENAME -alias ALIAS -dname CN=ALIAS
 * </pre>
 * 
 * To extract the public key for the generated entry as a DER file, use:
 * <pre>
 * keytool -exportcert -keystore FILENAME -storepass 123456 -alias ALIAS2 |
 * openssl x509 -pubkey |
 * openssl rsa -RSAPublicKey_in -outform DER -pubout -out ALIAS.pub
 * </pre>
 * 
 * The resulting public key can be used by {@link PkiUtils#encrypt()} to
 * encrypt messages that can only be decrypted by the Vault.
 * 
 * @author zwets
 */
public class Vault {
	
	private static Logger LOG = LoggerFactory.getLogger(Vault.class);

	private String keyStoreFileName;
	private char[] keyStorePassword;

	/**
	 * Create or open the given keystore with the given password
	 * 
	 * @param fileName the path of the keystore to open
	 * @param password the password to use for the keystore
	 */
	Vault(String fileName, String password) {
		this.keyStoreFileName = fileName;
		this.keyStorePassword = password == null ? null : password.toCharArray();
	}

	/**
	 * Return the public key stored for the keyId.
	 * 
	 * @param keyId
	 * @return the public key associated with the key
	 * @throws RuntimeException for any underlying checked exception
	 */
	public PublicKey getPublicKey(String keyId) {
		return getKeyPair(keyId).getPublic();
	}
	
	/**
	 * Decrypt the cyphertext with the private key for keyId.
	 * 
	 * @param keyId the ID the key was stored under
	 * @param ciphertext the text to decode
	 * @return the plaintext
	 * @throws RuntimeException for any underlying checked exception
	 */
	public byte[] decrypt(String keyId, byte[] ciphertext) {
		return PkiUtils.decrypt(getPrivateKey(keyId), ciphertext);
	}

	private KeyStore getKeyStore() {
		LOG.info("Loading keystore: {}", keyStoreFileName);
		
		try {
			KeyStore keyStore = KeyStore.getInstance(new File(keyStoreFileName), keyStorePassword);

			if (LOG.isDebugEnabled()) {
				keyStore.aliases().asIterator().forEachRemaining((s) -> LOG.debug(" - alias: {}", s));
			}
			
			return keyStore;
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			LOG.error("Exception loading keystore {}: {}", keyStoreFileName, e.getMessage());
			throw new RuntimeException(e.getMessage(), e.getCause());
		}
	}

	private PrivateKeyEntry getEntry(String keyId) {
		try {
			return (PrivateKeyEntry) getKeyStore().getEntry(keyId, new PasswordProtection(keyStorePassword));
		} catch (NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException e) {
			LOG.error("Exception retrieving keystore entry '{}': {}", keyId, e.getMessage());
			throw new RuntimeException(e.getMessage(), e.getCause());
		}
	}
	
	private KeyPair getKeyPair(String keyId) {
		PrivateKeyEntry pke = getEntry(keyId);
		return new KeyPair(pke.getCertificate().getPublicKey(), pke.getPrivateKey());
	}

	private PrivateKey getPrivateKey(String keyId) {
		return getKeyPair(keyId).getPrivate();
	}
	
	public static void main(String[] args)
	{
		if (args.length != 3) {
			System.err.println("Usage: Vault KEYSTORE pubkey ALIAS | Vault KEYSTORE decrypt ALIAS");
		}
		else {
			String keyStore = args[0];
			String command = args[1];
			String keyId = args[2];
			
			try {
				Vault vault = new Vault(keyStore, "123456");
				if (command.equals("pubkey")) {
					PublicKey key = vault.getPublicKey(keyId);
					byte[] bytes = key.getEncoded();
					Files.write(Path.of("/dev/stdout"), bytes);
				}
				else if (command.equals("decrypt")) {
					byte[] bytes = vault.decrypt(keyId, Files.readAllBytes(Path.of("/dev/stdin")));
					Files.write(Path.of("/dev/stdout"), bytes);
				}
				else {
					System.err.println("Usage: Vault KEYSTORE pubkey ALIAS | Vault KEYSTORE decrypt ALIAS");
				}
			}
		    catch (Exception e)
		    {
		        e.printStackTrace();
		    }
		}
	}
}
