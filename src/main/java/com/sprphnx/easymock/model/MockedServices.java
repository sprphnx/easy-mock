package com.sprphnx.easymock.model;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MockedServices {
	
	Set<String> restServices;
	Set<String> soapServices;
	

}
