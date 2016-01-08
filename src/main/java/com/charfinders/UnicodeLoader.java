package com.charfinders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class UnicodeLoader {

	private static final String UNICODE_TABLE_FILENAME = "unicode-table.txt";
	
	private static final String UNICODEDATA_URL = "http://www.unicode.org/Public/8.0.0/ucd/UnicodeData.txt";	

	private static final String TMP_DIR = System.getProperty("java.io.tmpdir", "/tmp");

	public static String loadCharTable() throws IOException {
		if(Files.exists(tmpFilePath())){
			String load = localLoad();
			return !load.isEmpty() ? load : HTTPLoad();
		} else {
			String httpLoad = HTTPLoad();			
			Files.write(tmpFilePath(), httpLoad.getBytes());
			return httpLoad;
		}				
	}

	private static Path tmpFilePath() {
		return Paths.get(TMP_DIR, UNICODE_TABLE_FILENAME);
	}

	private static String localLoad() throws IOException {		
		return new String(Files.readAllBytes(tmpFilePath()));	
	}

	private static String HTTPLoad() throws IOException {
		HttpGet httpget = new HttpGet(UNICODEDATA_URL);
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			return EntityUtils.toString(httpclient.execute(httpget).getEntity());
		} finally {
			httpclient.close();
		}
	}
}
