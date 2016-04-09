package com.mystic.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component("zipUtils")
public class ZipUtils {
	private ZipFile zipFile;
	private StringBuilder stringBuilder;
	private File data;
	private ZipOutputStream output;
	
	private static final Logger LOG = Logger.getLogger(ZipUtils.class.getName());

	@PostConstruct
	void init() {
		createZipFile();
		stringBuilder = new StringBuilder();
		try {
			zipFile = new ZipFile(data);
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "[ReadZip][File Not Found]");
		}
	}

	/**
	 * Read Zip File Content
	 * @param zipPath
	 * @return
	 */
	StringBuilder readZipFile() {
		try {
			ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(data));
			ZipEntry entry = zipInputStream.getNextEntry();
			while(entry != null) {
				byte[] buf = new byte[(int) zipFile.entries().nextElement().getSize()];
				int len = zipInputStream.read(buf);
	            stringBuilder.append(new String(buf, 0, len));
				entry = zipInputStream.getNextEntry();
			} 
			zipInputStream.close();
		} catch (FileNotFoundException e) {
			LOG.log(Level.SEVERE, "[ReadZip][File Not Found]");
		} catch (IOException e) { 
			LOG.log(Level.SEVERE, "[ReadZip][Failed To Read Zip File]");
		}
		return stringBuilder;
	}
	
	/**
	 * Write Mock Into Zip File
	 * @param mock
	 */
	public void writeMockIntoZipFile(String mock) {
		if(stringBuilder.length() > 0) {
			stringBuilder.append("\n" + mock);
		}else {
			stringBuilder.append(mock);
		}
		
		byte[] bytes = stringBuilder.toString().getBytes();
		try {
			output = new ZipOutputStream(new FileOutputStream(data));
			ZipEntry entry = new ZipEntry("mystic.txt"); 
			output.putNextEntry(entry);
			output.write(bytes);
			output.closeEntry();
			output.close();
		} catch (FileNotFoundException e) {
			LOG.log(Level.SEVERE, "[ReadZip][File Not Found]");
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "[ReadZip][Failed To Open Zip File]");
		} 
	}
	
	/**
	 * Append Mocks
	 * @param mock
	 */
	void appendNewMock(String mock) {
		stringBuilder.append(mock  + "\n");
	}
	
	/**
	 * Repository To Save Mock Data
	 */
	private void createZipFile() {
		String TEMP_DIR = System.getProperty("java.io.tmpdir");
		String PATH_MOCK_FILE = System.getProperty("mystic.data");
		
		if (PATH_MOCK_FILE == null) {
			PATH_MOCK_FILE = TEMP_DIR;
			data = new File(PATH_MOCK_FILE);
		} else {
			data = new File(PATH_MOCK_FILE);
			if (!data.exists()) {
				data.mkdirs();
			}
		}
		
		data = new File(data.getAbsolutePath().concat("/mystic.zip")); 
		try {
			if(!data.exists()) { 
				ZipOutputStream out = new ZipOutputStream(new FileOutputStream(data));
				ZipEntry entry = new ZipEntry("mystic.txt"); 
				out.putNextEntry(entry);
				out.closeEntry();
				out.close();
			}
			
		} catch (FileNotFoundException e) {
			LOG.log(Level.SEVERE, "[ReadZip][File Not Found]");
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "[ReadZip][Failed To Create Zip File]");
		}
	}
}
