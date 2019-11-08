package com.sprphnx.easymock.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class EasyMockService {
	
	private static final String CLASSPATH_MOCK_RESPONSES = "classpath:mock-responses";
	private static final String XML = "application/xml";
	private static final String JSON = "application/json";	
	
	@Autowired
	ResourceLoader resourceLoader;
	
	public String getMockResponse(String serviceName, String applicationJsonValue) {
		try {
			String path = identifyMockServiceResponseFilePath(serviceName, applicationJsonValue);
			Resource resource = resourceLoader.getResource(path);
			InputStream inputStream = resource.getInputStream();
			byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
			return new String(bdata, StandardCharsets.UTF_8);
		} catch (IOException e) {
			log.error(e);
		}
		return null;
	}

	private String identifyMockServiceResponseFilePath(String serviceName, String applicationJsonValue) {
		String path = null;

		switch (applicationJsonValue) {
		case MediaType.TEXT_XML_VALUE:
			path = CLASSPATH_MOCK_RESPONSES+"/soap/"+serviceName+".xml";
			break;
		case MediaType.APPLICATION_JSON_VALUE:
			path = CLASSPATH_MOCK_RESPONSES+"/rest/"+serviceName+".json";
			break;
		default:
			return null;
		}
		return path;
	}
	
	public boolean saveMockResposeFile(MultipartFile file) {
        String folder="";
		String fileType = file.getContentType();
		
		log.info(fileType);
		if(!(XML.equals(fileType)||JSON.equals(fileType))) {
			throw new RuntimeException("Invalid file format");
		}
		
		if(XML.equals(fileType)) {
			folder = "/soap";
		}
		
		if(JSON.equals(fileType)) {
			folder = "/rest";
		}
		
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
		
        try {
    		Resource resource = resourceLoader.getResource(CLASSPATH_MOCK_RESPONSES);
    		String absolutePath = resource.getFile().getAbsolutePath();
        	
        	InputStream inputStream = file.getInputStream();
            Files.copy(inputStream, Paths.get(absolutePath,folder).resolve(filename),
                       StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e) {
            log.error(e);
            return false;
        }
        
        return true;
    }
	
}