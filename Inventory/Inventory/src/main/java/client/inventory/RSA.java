package client.inventory;

import javax.crypto.Cipher;
import java.io.*;
import java.security.*;
import java.security.spec.*;

/*
    RSA class is responsible for loading the encryption and decryption keys and using them.
    Public key is for encryption. Private key is for decryption. Both keys are held by a KeyPair object.

    When sending data between client and server - we use the client's public key for encryption on the client-side
    and on the server-side we will decrypt the data using the client's private key.

    When sending data between server and client - we use the server's public key for encryption on the server-side
    and on the client-side we will decrypt the data using the server's private key.
 */
public class RSA {

    private final KeyPair keyPair;

    /*
        This constructor is used for loading the keys.
     */
    public RSA(String path, String publickey, String privatekey, String algorithm)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException
    {
        keyPair = loadKeyPair(path, publickey, privatekey, algorithm);
    }

    /*
        Copy constructor.
     */
    public RSA(RSA rsa)
    {
        keyPair = new KeyPair(rsa.getKeyPair().getPublic(), rsa.getKeyPair().getPrivate());
    }

    /*
        Loads keys from memory.
     */
    private KeyPair loadKeyPair(String path, String publickey, String privatekey, String algorithm)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException
    {
        // Reads public key.
        File filePublicKey = new File(path + publickey);
        FileInputStream fis = new FileInputStream(path + publickey);
        byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
        fis.read(encodedPublicKey);
        fis.close();

        // Reads private key.
        File filePrivateKey = new File(path + privatekey);
        fis = new FileInputStream(path + privatekey);
        byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
        fis.read(encodedPrivateKey);
        fis.close();

        // Generates KeyPair.
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
                encodedPublicKey);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
                encodedPrivateKey);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        return new KeyPair(publicKey, privateKey);
    }

    /*
        Encrypts a string parameter and returns a byte array.
     */
    public byte[] encrypt(String parameter) throws Exception
    {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
        return cipher.doFinal(parameter.getBytes());
    }

    /*
        Decrypts a byte array and returns its string value.
     */
    public String decrypt(byte[] cipherText) throws Exception
    {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
        byte[] decryptedText = cipher.doFinal(cipherText);
        return new String(decryptedText);
    }

    /*
        Gets both encryption and decryption keys.
     */
    public KeyPair getKeyPair()
    {
        return keyPair;
    }
}