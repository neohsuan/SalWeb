package gov.vghks.sal.util;

import java.util.Calendar;
import java.util.Date;
import java.security.SecureRandom;

public class ClsRsa {

	private int publicKey;
	private int privateKey;
	private int modulus;

	private String strlLngEncrypted;
	private int gloPubK;
	private int gloPriK;
	private int gloMou;
	private int rsaKey[] = new int[3];
	private String DateFormat = "yyyyMMddHHmmss";
	private String Cry(String a) {
		publicKey = rsaKey[0];
		privateKey = rsaKey[1];
		modulus = rsaKey[2];
		int lLngIndex = 0;
		int lLngMaxIndex = 0;
		int lBytAscii = 0;
		strlLngEncrypted = "";
		lLngMaxIndex = a.length();
		if (lLngMaxIndex < 0) {
		} else {
			for (int i = 0; i < lLngMaxIndex; i++) {
				lBytAscii = (int) a.charAt(i);
				int lLngResult = 0;
				int lLngMod = 0;
				if ((publicKey % 2) == 0) {
					lLngResult = 1;
					for (lLngIndex = 1; lLngIndex < publicKey / 2; lLngIndex++) {
						lLngMod = ((lBytAscii * lBytAscii)) % modulus;
						lLngResult = (lLngMod * lLngResult) % modulus;
					}
				} else {
					lLngResult = lBytAscii;
					for (lLngIndex = 1; lLngIndex <= publicKey / 2; lLngIndex++) {
						lLngMod = ((lBytAscii * lBytAscii)) % modulus;
						lLngResult = (lLngMod * lLngResult) % modulus;

					}

				}
				String strAdd0 = "";
				strAdd0 = Integer.toHexString(lLngResult).toUpperCase();

				// 下面是填0的動作
				if (strAdd0.length() > 4) {
				} else {
					int add0In = 4 - (strAdd0.length());
					for (int j = 1; j <= add0In; j++) {
						strAdd0 = "0" + strAdd0;
					}
				}

				strlLngEncrypted += strAdd0;

			}

		}

		return strlLngEncrypted;

	}

	private String Dec(String a) {
		publicKey = rsaKey[0];
		privateKey = rsaKey[1];
		modulus = rsaKey[2];
		int lLngMaxIndex = a.length();
		int lBytAscii = 0;
		int lLngIndex = 0;
		String transback = "";
		// 接下來跑幾次迴圈，根據所傳入的id長度
		for (int i = 1; i < lLngMaxIndex; i += 4) {
			int lLngResult = 0;
			int lLngMod = 0;
			lBytAscii = Integer.parseInt(a.substring(i - 1, i + 3), 16);
			if ((privateKey % 2) == 0) {
				lLngResult = 1;
				for (lLngIndex = 1; lLngIndex < privateKey / 2; lLngIndex++) {
					lLngMod = ((lBytAscii * lBytAscii)) % modulus;
					lLngResult = (lLngMod * lLngResult) % modulus;
				}
			} else {
				lLngResult = lBytAscii;

				for (lLngIndex = 1; lLngIndex <= privateKey / 2; lLngIndex++) {
					lLngMod = ((lBytAscii * lBytAscii)) % modulus;
					lLngResult = (lLngMod * lLngResult) % modulus;
				}
				transback = transback + (char) lLngResult;

			}

		}

		return transback;

	}

	public int[] generateKey(String a) {

		int lLngPhi = 0;
		int p = 0;
		int q = 0;
		
		boolean finalFlag = false;
		SecureRandom random = new SecureRandom();
		while (!finalFlag) {
			do {
				p = 1 + (int) (random.nextLong());
				q = 1 + (int) (random.nextLong());

				while (!(Isprime(p))) {
					p = 1 + (int) (random.nextLong());
				}
				while (!(Isprime(q))) {
					q = 1 + (int) (random.nextLong());
				}
				// 求得質數p和q

				modulus = p * q;

				gloMou = modulus;

				privateKey = (int) (Math
						.round((random.nextLong() * (modulus - 2)) / 1) + 2);
				gloPriK = privateKey;

				lLngPhi = ((p - 1) * (q - 1)) / 1;

				publicKey = Euler(lLngPhi, privateKey);
				gloPubK = publicKey;

				// finalFlag =TestCrypt(255);
			} while (publicKey == 0 || publicKey == 1);

			rsaKey[0] = gloPubK;
			rsaKey[1] = gloPriK;
			rsaKey[2] = gloMou;
			finalFlag = TestCrypt(255);
			if (finalFlag) {
				String password = Cry(a);
				String decodeword = Dec(password);
				// System.out.println("a=" + a);
				// System.out.println("password=" + password);
				// System.out.println("decodeword=" + decodeword);
				if (!(a.equals(decodeword))) {
					finalFlag = false;
				} else {
					// insertDb(strlLngEncrypted);
				}

			}

		}
		return rsaKey;

	}

	private boolean TestCrypt(int pBytData) {
		boolean ans = false;
		int lStrCrypted = 0;
		lStrCrypted = reCrypt(255, gloPubK);
		int compare = reCrypt(lStrCrypted, gloPriK);

		// System.out.println("compare=" + compare);

		ans = (compare == pBytData);
		return ans;
	}

	private int reCrypt(int reCrypt, int pKey) {
		int lLngIndex = 0;
		strlLngEncrypted = "";
		int lLngResult = 0;
		int lLngMod = 0;
		pKey = gloPubK;
		modulus = gloMou;
		if ((pKey % 2) == 0) {
			lLngResult = 1;
			for (lLngIndex = 1; lLngIndex < pKey / 2; lLngIndex++) {
				lLngMod = ((reCrypt * reCrypt)) % modulus;
				lLngResult = (lLngMod * lLngResult) % modulus;
			}
		} else {
			lLngResult = reCrypt;
			for (lLngIndex = 1; lLngIndex <= pKey / 2; lLngIndex++) {
				lLngMod = ((reCrypt * reCrypt)) % modulus;
				lLngResult = (lLngMod * lLngResult) % modulus;

			}

		}
		return lLngResult;
	}

	private int Euler(int pLngPHI, int pLngKey) {
		int lLngR[] = new int[3];
		int lLngP[] = new int[3];
		int lLngQ[] = new int[3];
		int lLngCounter = 0;
		int lLngResult = 0;
		int euler = 0;
		lLngR[1] = pLngPHI;
		lLngR[0] = pLngKey;
		lLngP[1] = 0;
		lLngP[0] = 1;
		lLngQ[1] = 2;
		lLngQ[0] = 0;
		lLngCounter = -1;
		while ((lLngR[0] != 0)) {
			lLngR[2] = lLngR[1];
			lLngR[1] = lLngR[0];
			lLngP[2] = lLngP[1];
			lLngP[1] = lLngP[0];
			lLngQ[2] = lLngQ[1];
			lLngQ[1] = lLngQ[0];

			lLngCounter = lLngCounter + 1;

			lLngR[0] = lLngR[2] % lLngR[1];
			lLngP[0] = ((Math.round((lLngR[2] / lLngR[1])) * lLngP[1]))
					+ lLngP[2];
			lLngQ[0] = ((Math.round((lLngR[2] / lLngR[1])) * lLngQ[1]))
					+ lLngQ[2];

		}

		lLngResult = (pLngKey * lLngP[1]) - (pLngPHI * lLngQ[1]);
		if (lLngResult > 0) {
			euler = lLngP[1];
		} else {
			euler = Math.abs(lLngP[1]) + pLngPHI;
		}
		publicKey = euler;
		return publicKey;

	}

	private boolean Isprime(int n) {
		boolean ans = true;
		if ((n % 2 == 0) | (n == 1)) {
			ans = false;
			return ans;
		}
		for (int i = 3; i <= Math.sqrt(n); i += 2) {
			if (n % i == 0) {
				ans = false;
				break;
			}
		}
		return ans;
	}

	public int[] getRsaKey() {
		return rsaKey;
	}

	public void setRsaKey(int[] is) {
		rsaKey = is;
	}

	public String DecodePass(String a) {
		publicKey = rsaKey[0];
		privateKey = rsaKey[1];
		modulus = rsaKey[2];
		int lLngMaxIndex = a.length();
		int lBytAscii = 0;
		int lLngIndex = 0;
		String transback = "";
		// 接下來跑幾次迴圈，根據所傳入的id長度
		for (int i = 1; i < lLngMaxIndex; i += 4) {
			int lLngResult = 0;
			int lLngMod = 0;
			lBytAscii = Integer.parseInt(a.substring(i - 1, i + 3), 16);
			if ((privateKey % 2) == 0) {
				lLngResult = 1;
				for (lLngIndex = 1; lLngIndex < privateKey / 2; lLngIndex++) {
					lLngMod = ((lBytAscii * lBytAscii)) % modulus;
					lLngResult = (lLngMod * lLngResult) % modulus;
				}
			} else {
				lLngResult = lBytAscii;

				for (lLngIndex = 1; lLngIndex <= privateKey / 2; lLngIndex++) {
					lLngMod = ((lBytAscii * lBytAscii)) % modulus;
					lLngResult = (lLngMod * lLngResult) % modulus;
				}
				transback = transback + (char) lLngResult;

			}

		}
		try {
			String[] uid = transback.split("&");
			Calendar c = Calendar.getInstance();
			c.add(Calendar.HOUR, -1);
			Date overTime = c.getTime();
			Date loginTime = new java.text.SimpleDateFormat(DateFormat).parse(uid[1]);
			//System.out.println(new java.text.SimpleDateFormat(DateFormat).format(loginTime)+" "+new java.text.SimpleDateFormat(DateFormat).format(overTime));
			//System.out.println(loginTime.getTime()+" "+overTime.getTime());
			if (loginTime.getTime()>=overTime.getTime()) {
				return uid[0];
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String Crypt(String uid) {
		String a = uid
				+ "&"
				+ new java.text.SimpleDateFormat(DateFormat)
						.format(new java.util.Date());
						/*		Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR, -2);
		String a = uid
		+ "&"
		+ new java.text.SimpleDateFormat("yyyyMMddhhmm")
				.format(c.getTime());*/
		publicKey = rsaKey[0];
		privateKey = rsaKey[1];
		modulus = rsaKey[2];
		int lLngIndex = 0;
		int lLngMaxIndex = 0;
		int lBytAscii = 0;
		strlLngEncrypted = "";
		lLngMaxIndex = a.length();
		if (lLngMaxIndex < 0) {
		} else {
			for (int i = 0; i < lLngMaxIndex; i++) {
				lBytAscii = (int) a.charAt(i);
				int lLngResult = 0;
				int lLngMod = 0;
				if ((publicKey % 2) == 0) {
					lLngResult = 1;
					for (lLngIndex = 1; lLngIndex < publicKey / 2; lLngIndex++) {
						lLngMod = ((lBytAscii * lBytAscii)) % modulus;
						lLngResult = (lLngMod * lLngResult) % modulus;
					}
				} else {
					lLngResult = lBytAscii;
					for (lLngIndex = 1; lLngIndex <= publicKey / 2; lLngIndex++) {
						lLngMod = ((lBytAscii * lBytAscii)) % modulus;
						lLngResult = (lLngMod * lLngResult) % modulus;

					}

				}
				String strAdd0 = "";
				strAdd0 = Integer.toHexString(lLngResult).toUpperCase();

				// 下面是填0的動作
				if (strAdd0.length() > 4) {
				} else {
					int add0In = 4 - (strAdd0.length());
					for (int j = 1; j <= add0In; j++) {
						strAdd0 = "0" + strAdd0;
					}
				}

				strlLngEncrypted += strAdd0;

			}

		}

		return strlLngEncrypted;

	}

	public static void main(String[] args) {
		String oo = "1234567890";
		System.out.println("原文:" + oo);
		ClsRsa cls = new ClsRsa();
		int keys1[] = cls.generateKey("1234567890");
		cls.setRsaKey(keys1);
		 String cryptStr = cls.Crypt(oo);
		 System.out.println("密文:" + cryptStr);
		 String ooStr = cls.DecodePass(cryptStr);
		 System.out.println("解密:" + ooStr);

	}

}
