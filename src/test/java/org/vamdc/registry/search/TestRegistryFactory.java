package org.vamdc.registry.search;

import java.net.MalformedURLException;
import java.net.URL;

import org.vamdc.registry.search.RegistryClientFactory;

import net.ivoa.wsdl.registrysearch.RegistrySearchPortType;
import junit.framework.TestCase;

public class TestRegistryFactory extends TestCase {

	public final static String REGISTRY_URL="http://casx019-zone1.ast.cam.ac.uk:80/registry/services/RegistryQueryv1_0";
	
	public void testGetService() {
		RegistrySearchPortType port = RegistryClientFactory.getSearchPort();
		
		assertNotNull(port);
	}

	public void testGetServiceURL() {
		RegistrySearchPortType port=null;
		try {
			port = RegistryClientFactory.getSearchPort(new URL(REGISTRY_URL));
		} catch (MalformedURLException e) {
			fail("Malformed test URL "+REGISTRY_URL);
		}
		
		assertNotNull(port);
	}

	public void testGetServiceString() {
		RegistrySearchPortType port = RegistryClientFactory.getSearchPort(REGISTRY_URL);
		
		assertNotNull(port);
	}

}
