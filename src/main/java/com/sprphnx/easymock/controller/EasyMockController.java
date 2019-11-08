package com.sprphnx.easymock.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sprphnx.easymock.service.EasyMockService;



@RestController
@RequestMapping("/mock")
public class EasyMockController {
	
	@Autowired
	EasyMockService service;
	
	@RequestMapping(value = "/rest-service/{serviceName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String jsonResponse(@PathVariable String serviceName) throws IOException {
		return service.getMockResponse(serviceName, MediaType.APPLICATION_JSON_VALUE);
	}
	
	@RequestMapping(value = "/soap-service/{serviceName}", method = RequestMethod.GET, produces = MediaType.TEXT_XML_VALUE)
	public String xmlResponse(@PathVariable String serviceName) throws IOException {
		return service.getMockResponse(serviceName, MediaType.TEXT_XML_VALUE);
	}
	
    @PostMapping("upload-mock-response-file")
	public Boolean uploadMockResponseFile(@RequestParam("file") MultipartFile file) {
	    return service.saveMockResposeFile(file);
	}

}