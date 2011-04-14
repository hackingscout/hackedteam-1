package com.ht.RCSAndroidGUI.test;

import java.util.Arrays;

import com.ht.RCSAndroidGUI.Debug;
import com.ht.RCSAndroidGUI.crypto.Crypto;
import com.ht.RCSAndroidGUI.crypto.CryptoException;
import com.ht.RCSAndroidGUI.crypto.Encryption;
import com.ht.RCSAndroidGUI.crypto.Keys;
import com.ht.RCSAndroidGUI.utils.Utils;

import junit.framework.TestCase;

public class CryptoTest extends TestCase {
	Debug debug = new Debug("CryptoTest");

	protected void setUp() throws Exception {
		//Debug.disable();
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public final void testDecryptName() {
		fail("Not yet implemented"); // TODO
	}

	public final void testEncryptName() {
		fail("Not yet implemented"); // TODO
	}

	public final void testGetNextMultiple() {
		fail("Not yet implemented"); // TODO
	}

	public final void testMakeKey() {
		fail("Not yet implemented"); // TODO
	}

	public final void testMultiple() {
		Encryption encryption = new Encryption(Keys.self().getAesKey());
		for (int i = 0; i < 1024; i++) {

			final int n = encryption.getNextMultiple(i);
			assertTrue(n >= 0);
			assertTrue(n % 16 == 0);

		}
	}

	public final void testAes() throws Exception {
		// #ifdef DEBUG
		debug.info("-- RijndaelTest --");
		// #endif

		// i valori seguenti sono stati presi dal paper che descriveva il
		// rijandael per aes
		final byte[] key = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05,
				0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f };
		final byte[] plain = new byte[] { 0x00, 0x11, 0x22, 0x33, 0x44, 0x55,
				0x66, 0x77, (byte) 0x88, (byte) 0x99, (byte) 0xaa, (byte) 0xbb,
				(byte) 0xcc, (byte) 0xdd, (byte) 0xee, (byte) 0xff };
		final byte[] cyphered = new byte[] { 0x69, (byte) 0xc4, (byte) 0xe0,
				(byte) 0xd8, 0x6a, 0x7b, 0x04, 0x30, (byte) 0xd8, (byte) 0xcd,
				(byte) 0xb7, (byte) 0x80, 0x70, (byte) 0xb4, (byte) 0xc5, 0x5a };

		// generazione delle chiave
		Crypto crypto = new Crypto(key);

		// cifratura
		final byte[] buffer = crypto.encrypt(plain);

		// verifico che la cifratura sia conforme a quanto atteso
		assertTrue(Arrays.equals(buffer, cyphered));

		// decifro
		crypto.decrypt(cyphered, buffer);

		// verifico che la decifratura sia conforme a quanto atteso
		assertTrue(Arrays.equals(buffer, plain));

		// se arrivo qui e- perche- le assert non sono fallite, quindi
		// restituisco true
		return;

	}

	public final void testScramble() {
		String ret = Encryption.encryptName("KiodoGay", 0xb0);
		String expected = "pKdTdlYz";
		assertEquals(ret, expected);

		ret = Encryption.encryptName("BrunelloBrunilde", 0xb0);
		expected = "RbF5OQQdRbF5KQTO";
		assertEquals(ret, expected);

		ret = Encryption.encryptName("Zeno", 0xb0);
		expected = "kO5d";
		assertEquals(ret, expected);

		ret = Encryption.encryptName("Xeno", 0xb0);
		expected = "8O5d";
		assertEquals(ret, expected);

		ret = Encryption.encryptName("10401349w298238402834923.mob", 0xb0);
		expected = "mVHVmoHh9ZhnZonHVZnoHhZo.udD";
		assertEquals(ret, expected);

		ret = Encryption.encryptName("*.mob", 0xb0);
		expected = "*.udD";
		assertEquals(ret, expected);
	}

	public final void testCBC() throws CryptoException {
		// #ifdef DEBUG
		debug.info("-- CBCTest --");
		// #endif

		final byte[] key = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05,
				0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f };
		byte[] plain = new byte[] { 0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66,
				0x77, (byte) 0x88, (byte) 0x99, (byte) 0xaa, (byte) 0xbb,
				(byte) 0xcc, (byte) 0xdd, (byte) 0xee, (byte) 0xff };
		final byte[] cyphered = new byte[] { 0x69, (byte) 0xc4, (byte) 0xe0,
				(byte) 0xd8, 0x6a, 0x7b, 0x04, 0x30, (byte) 0xd8, (byte) 0xcd,
				(byte) 0xb7, (byte) 0x80, 0x70, (byte) 0xb4, (byte) 0xc5, 0x5a };

		final Encryption enc = new Encryption(key);

		byte[] buffer = enc.encryptData(plain);
		assertTrue(Arrays.equals(buffer, cyphered));

		buffer = enc.decryptData(cyphered);
		assertTrue(Arrays.equals(buffer, plain));

		plain = new byte[1024];
		buffer = enc.encryptData(plain);
		assertTrue(!Arrays.equals(buffer, plain));
		buffer = enc.decryptData(buffer);
		assertTrue(Arrays.equals(buffer, plain));

	}

	public final void testCBC32() throws CryptoException {
		// #ifdef DEBUG
		debug.info("-- CBCTest --");
		// #endif

		String pl = "0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f";
		String cyph = "03a9c8fe778fb8a8668359542ad4d58413de91874e97dbedb518847a49cc0aaa";

		final byte[] key = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05,
				0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f };
		byte[] plain = Utils.hexStringToByteArray(pl);
		final byte[] cyphered = Utils.hexStringToByteArray(cyph);

		final Encryption enc = new Encryption(key);

		byte[] buffer = enc.encryptData(plain);
		assertTrue(Arrays.equals(buffer, cyphered));

		buffer = enc.decryptData(cyphered);
		assertTrue(Arrays.equals(buffer, plain));

		plain = new byte[1024];
		buffer = enc.encryptData(plain);
		assertTrue(!Arrays.equals(buffer, plain));
		buffer = enc.decryptData(buffer);
		assertTrue(Arrays.equals(buffer, plain));

	}

	public final void testEncryptDataByteArray() throws CryptoException {
		// #ifdef DEBUG
		debug.info("-- EncryptTest --");
		// #endif

		final byte[] key = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05,
				0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f };

		final Encryption enc = new Encryption(key);

		// 1
		// #ifdef DEBUG
		debug.info("1");
		// #endif
		byte[] plain = new byte[1];
		Arrays.fill(plain, (byte) 0x0f);
		byte[] buffer = enc.encryptData(plain);
		assertTrue(!Arrays.equals(buffer, plain));
		assertTrue(buffer.length == 16);

		buffer = enc.decryptData(buffer, 1, 0);
		assertTrue(buffer.length == 1);

		assertTrue(Arrays.equals(buffer, plain));

		// 1
		// #ifdef DEBUG
		debug.info("12");
		// #endif
		plain = new byte[12];
		Arrays.fill(plain, (byte) 0x0f);
		buffer = enc.encryptData(plain);
		assertTrue(!Arrays.equals(buffer, plain));
		assertTrue(buffer.length == 16);

		buffer = enc.decryptData(buffer, plain.length, 0);
		assertTrue(buffer.length == plain.length);
		assertTrue(Arrays.equals(buffer, plain));

		// 1
		// #ifdef DEBUG
		debug.info("16");
		// #endif
		plain = new byte[16];
		Arrays.fill(plain, (byte) 0x0f);
		buffer = enc.encryptData(plain);
		assertTrue(!Arrays.equals(buffer, plain));
		assertTrue(buffer.length == 16);

		buffer = enc.decryptData(buffer, plain.length, 0);
		assertTrue(buffer.length == 16);
		assertTrue(Arrays.equals(buffer, plain));

		// 1024
		// #ifdef DEBUG
		debug.info("1024");
		// #endif
		plain = new byte[1024];
		Arrays.fill(plain, (byte) 0x0f);
		buffer = enc.encryptData(plain);
		assertTrue(!Arrays.equals(buffer, plain));
		assertTrue(buffer.length == plain.length);

		buffer = enc.decryptData(buffer, plain.length, 0);
		assertTrue(buffer.length == plain.length);
		assertTrue(Arrays.equals(buffer, plain));

		return;
	}

	public final void testEncryptDataByteArrayInt() {
		fail("Not yet implemented"); // TODO
	}

	public final void testPadByteArrayIntInt() {
		fail("Not yet implemented"); // TODO
	}

	public final void testPadByteArrayIntIntBoolean() {
		fail("Not yet implemented"); // TODO
	}

	public final void testXor() {
		fail("Not yet implemented"); // TODO
	}

}
