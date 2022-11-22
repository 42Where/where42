package openproject.where42.token;

import lombok.NoArgsConstructor;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.AlgorithmParameterSpec;

@NoArgsConstructor
public class AES {

	public static String aes128SecretKey = "0123456789abcdef";
	public static byte[] aes128ivBytes = new byte[16];

	public String encoding(String encodeData) {
		try {
			byte[] textBytes = encodeData.getBytes("UTF-8");
			AlgorithmParameterSpec ivSpec = new IvParameterSpec(aes128ivBytes);
			SecretKeySpec newKey = new SecretKeySpec(aes128SecretKey.getBytes("UTF-8"), "AES");
			Cipher cipher = null;
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);
			return String.valueOf(Base64Utils.encodeToString(cipher.doFinal(textBytes)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String decoding(String decodeData) {
		try {
			byte[] textBytes = Base64Utils.decode(decodeData.getBytes());
			AlgorithmParameterSpec ivSpec = new IvParameterSpec(aes128ivBytes);
			SecretKeySpec newKey = new SecretKeySpec(aes128SecretKey.getBytes("UTF-8"), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);
			return new String(cipher.doFinal(textBytes), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
