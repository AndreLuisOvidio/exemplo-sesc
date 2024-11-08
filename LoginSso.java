
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class LoginSso {


	public static void main(String[] args) {
		String token = "2884ecbb1b459ef5df2205f8c6687916eef22cf11d029c3753bca9a8510359f0";

		String usuarioId = "16978";

		String auth = criptografaAES(usuarioId, token);

		long unixTime = System.currentTimeMillis() / 1000L;
		String tokenCripto = criptografaAES(Long.toString(unixTime), token);

		String url = "https://sescms.mentorweb.ws/sescmsMWFlutterWeb/#/loginSso?auth="+auth+"&token="+tokenCripto;
		System.out.println(url);
	}

	public static String criptografaAES(String valor, String chave) {
		String arqCriptografado = "";
		try {
			byte[] senha = geraHash(chave, EduTipoAlgoritmoHash.MD5);
			arqCriptografado = fromHex(encode(nullPadString(valor, StandardCharsets.UTF_8), senha));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return arqCriptografado;
	}

	public static String descriptografaAES(String valor, String chave) {
		String descripto = "";
		try {
			String senha = fromHex(geraHash(chave, EduTipoAlgoritmoHash.MD5));

			if (!isPar(length(valor))) {
				return null;
			}
			descripto = new String(decode(toHex(valor), toHex(senha)), StandardCharsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return descripto;
	}

	private static byte[] nullPadString(String original, Charset charset) {
		charset = charset == null ? StandardCharsets.UTF_8 : charset;

		byte[] retorno = original.getBytes(charset);
		int remain = retorno.length % 16;
		if (remain != 0) {
			remain = 16 - remain;
			for (int i = 0; i < remain; i++) {
				retorno = add(retorno, (byte) 0);
			}
		}
		return retorno;
	}

	public static String fromHex(byte[] hexadecimal) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < hexadecimal.length; i++) {
			sb.append(Integer.toString((hexadecimal[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}

	private static byte[] encode(byte[] input, byte[] key) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(input);
		return encrypted;
	}

	public static byte[] toHex(String valor) {
		int len = valor.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(valor.charAt(i), 16) << 4) + Character.digit(valor.charAt(i + 1), 16));
		}
		return data;
	}

	private static byte[] decode(byte[] input, byte[] key) throws NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {

		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] decrypted = cipher.doFinal(input);
		return decrypted;
	}

	public static boolean isPar(Integer valor) {
		if ((valor & 1) == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static int length(String str) {
		return str == null ? 0 : str.length();
	}

	public static byte[] add(byte[] array, byte element) {
		byte[] newArray = (byte[]) copyArrayGrow1(array, Byte.TYPE);
		newArray[newArray.length - 1] = element;
		return newArray;
	}

	private static Object copyArrayGrow1(Object array, Class newArrayComponentType) {
		if (array != null) {
			int arrayLength = Array.getLength(array);
			Object newArray = Array.newInstance(array.getClass().getComponentType(), arrayLength + 1);
			System.arraycopy(array, 0, newArray, 0, arrayLength);
			return newArray;
		}
		return Array.newInstance(newArrayComponentType, 1);
	}

	public static byte[] geraHash(String senha, EduTipoAlgoritmoHash algoritmo) throws NoSuchAlgorithmException{
		MessageDigest md = MessageDigest.getInstance(algoritmo.getCodigo());
		return md.digest(senha.getBytes());
	}

	public static String geraHashToString(String senha, EduTipoAlgoritmoHash algoritmo) throws NoSuchAlgorithmException{
		return createDigestString(geraHash(senha, algoritmo));
	}

	private static String createDigestString(byte fileDigest[]) {
		StringBuffer checksumSb = new StringBuffer();
		for (int i = 0; i < fileDigest.length; i++) {
			String hexStr = Integer.toHexString(0xff & fileDigest[i]);
			if (hexStr.length() < 2) {
				checksumSb.append("0");
			}
			checksumSb.append(hexStr);
		}
		return checksumSb.toString();
	}

	public enum EduTipoAlgoritmoHash {
		SHA1("SHA-1"),
		SHA256("SHA-256"),
		MD5("MD5");

		private EduTipoAlgoritmoHash(String codigo) {
			setCodigo(codigo);
		}

		public String getCodigo() {
			return codigo;
		}

		private void setCodigo(String codigo) {
			this.codigo = codigo;
		}

		private String codigo;
	}
}