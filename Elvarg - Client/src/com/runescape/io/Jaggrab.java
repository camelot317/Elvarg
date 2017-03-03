package com.runescape.io;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.seven.util.FileUtils;

import com.runescape.Client;
import com.runescape.Configuration;
import com.runescape.sign.SignLink;

/**
 * A class representing the Jaggrab
 * update-server system for a 317 client.
 * 
 * This system also supports preloadable
 * files, basically files which haven't
 * been packed into the main cache files.
 * 
 * @author Professor Oak
 */
public class Jaggrab {


	//Files that will always be updated & downloaded from the update-server
	//NOTE: These exact files must be defined in the update-server's PRELOAD_FILES
	//array aswell to prevent CRC-checking issues.
	private static final String[] PRELOAD_FILES = {
			"sprites.idx", "sprites.dat", 
			"obj.idx", "obj.dat"
	};

	//Archive CRCs
	public static final int TITLE_CRC = 1;
	public static final int CONFIG_CRC = 2;
	public static final int INTERFACE_CRC = 3;
	public static final int MEDIA_CRC = 4;
	public static final int UPDATE_CRC = 5;
	public static final int TEXTURES_CRC = 6;
	public static final int CHAT_CRC = 7;
	public static final int SOUNDS_CRC = 8;

	//CRCs
	public static final int TOTAL_ARCHIVE_CRCS = 9;
	public static final int[] CRCs = new int[TOTAL_ARCHIVE_CRCS + PRELOAD_FILES.length];


	/**
	 * Requests the crc table from the update-server
	 * and puts them into our array for future
	 * use.
	 * 
	 * Crcs are used for checking if files are
	 * up to date, and for other things aswell.
	 */
	public static void requestCrcs() {
		int delay = 5;
		int failedAttempts = 0;
		while(!crcsLoaded()) {	
			try (DataInputStream in = openRequest("crc" + (int) (Math.random() * 99999999D) + "-" + 317)) {

				Client.instance.drawLoadingText(20, "Requesting CRCs..");
				
				//Get incoming data
				Buffer buffer = new Buffer(readFile(in));

				for (int index = 0; index < CRCs.length; index++) {
					CRCs[index] = buffer.readInt();
				}
				
				int expected = buffer.readInt();
				int calculated = 1234;
				for (int index = 0; index < CRCs.length; index++) {
					calculated = (calculated << 1) + CRCs[index];
				}

				if (expected != calculated) {
					//Check sum error
					resetCrcs();
				}

			} catch (Exception ex) {
				ex.printStackTrace();
				resetCrcs();
			}

			//Didn't load properly
			if (!crcsLoaded()) {
				failedAttempts++;
				for (int remaining = delay; remaining > 0; remaining--) {
					if (failedAttempts >= 10) {
						Client.instance.drawLoadingText(10, "Game updated - please reload page");
						remaining = 10;
					} else {
						Client.instance.drawLoadingText(10, "CRC Error - Will retry in " + remaining + " secs.");
					}
					try {
						Thread.sleep(1000L);
					} catch (InterruptedException ex) {
					}
				}

				delay *= 2;
				if (delay > 60) {
					delay = 60;
				}
				
				failedAttempts = 0;
				delay = 4;

				//Configuration.JAGCACHED_ENABLED = !Configuration.JAGCACHED_ENABLED;
			}
		}
	}

	/**
	 * Preloads all our files.
	 */
	public static void preload() {
		for(int i = 0; i < PRELOAD_FILES.length; i++) {
			preload(i, PRELOAD_FILES[i]);
		}
	}

	/**
	 * Preloads a file from the update-server
	 * if we dont have it or it's outdated (compares CRC).
	 * 
	 * Sends a request to the server and starts
	 * downloading the file.
	 * 
	 * @param i				The index of the preload file
	 * @param fileName		The name of the file to be preloaded
	 */
	private static void preload(int i, String fileName) {
		int failedAttempts = 0;
		int delay = 5;
		boolean exists = false;
		byte[] buffer = null;

		//Check if file already exists...
		File file = new File(SignLink.findcachedir() + fileName);
		if(file.exists() && !file.isDirectory()) {
			exists = true;
			buffer = FileUtils.readFile(SignLink.findcachedir() + fileName);
		}
		
		//Check if the file is "updated" by comparing crc..
		if(buffer != null) {
			if (!compareCrc(buffer, CRCs[TOTAL_ARCHIVE_CRCS + i])) {
				buffer = null;
			}
		}

		//We already had the updated file!
		if(buffer != null) {
			return;
		}

		//Let's download the file.
		while(buffer == null) {
			Client.instance.drawLoadingText(20, "Requesting " + fileName);

			try {

				//Try to get the file..
				buffer = readFile(openRequest("preload/" + fileName));
				
				//Compare crc again...
				if (!compareCrc(buffer, CRCs[TOTAL_ARCHIVE_CRCS + i])) {
					buffer = null;
				}

			} catch(Exception e) {
				e.printStackTrace();
			}

			if (buffer == null) {
				failedAttempts++;
				for (int remaining = delay; remaining > 0; remaining--) {
					if (failedAttempts >= 10) {
						Client.instance.drawLoadingText(20, "Game updated - please reload page");
						remaining = 10;
					} else {
						Client.instance.drawLoadingText(20, "Preload Error - Will retry in " + remaining + " secs.");
					}
					try {
						Thread.sleep(1000L);
					} catch (InterruptedException ex) {
					}
				}

				delay *= 2;
				if (delay > 60) {
					delay = 60;
				}
				
				delay = 4;
				failedAttempts = 0;
				
				//Configuration.JAGCACHED_ENABLED = !Configuration.JAGCACHED_ENABLED;
			}
		}
		
		//Write the downloaded file..
		if(buffer != null && !exists) {
			try {
				FileUtils.writeFile(buffer, file.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	
	/**
	 * Sends a request to the update-server
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public static DataInputStream openRequest(String request) throws IOException {
		if (!Configuration.JAGCACHED_ENABLED) {
			if (SignLink.mainapp != null) {
				return SignLink.openUrl(request);
			}
			return new DataInputStream(new URL(Client.instance.getCodeBase(), request).openStream());
		}
		if (Client.instance.jaggrab != null) {
			try {
				Client.instance.jaggrab.close();
			} catch (Exception _ex) {
			}
			Client.instance.jaggrab = null;
		}
		Client.instance.jaggrab = Client.instance.openSocket(Configuration.JAGGRAB_PORT);
		Client.instance.jaggrab.setSoTimeout(10000);
		InputStream inputstream = Client.instance.jaggrab.getInputStream();
		OutputStream outputstream = Client.instance.jaggrab.getOutputStream();
		outputstream.write(("JAGGRAB /" + request + "\n\n").getBytes());
		return new DataInputStream(inputstream);
	}

	/**
	 * Gets the current buffer from the update-server
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static byte[] readFile(DataInputStream in) throws IOException {
		
		//Read incoming file size..
		int size = in.readInt();
		if(size <= 0) {
			return null;
		}
				
		//Read incoming file..
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		byte[] data = new byte[size];
		int read;
		while ((read = in.read(data)) != -1) {
		    buffer.write(data, 0, read);
		}
		
		return buffer.toByteArray();
	}

	/**
	 * Compares crcs to check if a file's buffer is
	 * up to date or not.
	 * 
	 * @param buffer
	 * @param expectedCrc
	 * @return
	 */
	private static boolean compareCrc(byte[] buffer, int expectedCrc) {
		Client.instance.indexCrc.reset();
		Client.instance.indexCrc.update(buffer);
		int crc = (int) Client.instance.indexCrc.getValue();		
		return crc == expectedCrc;
	}

	/**
	 * Checks if we have succesfully loaded all our crcs.
	 * @return
	 */
	private static boolean crcsLoaded() {
		for(int i = 0; i < CRCs.length; i++) {
			if(CRCs[i] == -1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Resets the crc table
	 */
	private static void resetCrcs() {
		for(int i = 0; i < CRCs.length; i++) {
			CRCs[i] = -1;
		}
	}

	static {
		resetCrcs();
	}
}
