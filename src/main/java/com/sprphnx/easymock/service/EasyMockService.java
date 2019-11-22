package com.sprphnx.easymock.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.sprphnx.easymock.model.MockedServices;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class EasyMockService {
	
	private static final String PATH_REST = "/rest";
	private static final String PATH_SOAP = "/soap";
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

	public boolean saveMockResposeFile(MultipartFile file) {
        String folder="";
		String fileType = file.getContentType();
		
		log.info(fileType);
		if(!(XML.equals(fileType)||JSON.equals(fileType))) {
			throw new RuntimeException("Invalid file format");
		}
		
		if(XML.equals(fileType)) {
			folder = PATH_SOAP;
		}
		
		if(JSON.equals(fileType)) {
			folder = PATH_REST;
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

	public MockedServices getAllMockServices() {
		MockedServices response = new MockedServices();
		Resource resource = resourceLoader.getResource(CLASSPATH_MOCK_RESPONSES);
		try {
			String absolutePath = resource.getFile().getAbsolutePath();
			response.setSoapServices(listFilesUsingDirectoryStream(absolutePath+PATH_SOAP,true));
			response.setRestServices(listFilesUsingDirectoryStream(absolutePath+PATH_REST,true));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return response;
	}
	
	private String identifyMockServiceResponseFilePath(String serviceName, String applicationJsonValue) {
		String path = null;

		switch (applicationJsonValue) {
		case MediaType.TEXT_XML_VALUE:
			path = CLASSPATH_MOCK_RESPONSES+PATH_SOAP+"/"+serviceName+".xml";
			break;
		case MediaType.APPLICATION_JSON_VALUE:
			path = CLASSPATH_MOCK_RESPONSES+PATH_REST+"/"+serviceName+".json";
			break;
		default:
			return null;
		}
		return path;
	}
	
	private Set<String> listFilesUsingDirectoryStream(String dir, boolean removeExtension) throws IOException {
	    Set<String> fileList = new HashSet<>();
	    try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir))) {
	        for (Path path : stream) {
	            if (!Files.isDirectory(path)) {
	                String fileName = path.getFileName()
	                    .toString();
					fileList.add((removeExtension?removeFileExtension(fileName):fileName));
	            }
	        }
	    }
	    return fileList;
	}

	private String removeFileExtension(String fileName) {
		return fileName.substring(0, fileName.lastIndexOf('.'));
	}
	
}