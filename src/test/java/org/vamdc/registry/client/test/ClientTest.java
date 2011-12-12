package org.vamdc.registry.client.test;

import java.net.URL;

import org.vamdc.registry.client.Registry;
import org.vamdc.registry.client.Registry.Service;
import org.vamdc.registry.client.RegistryCommunicationException;
import org.vamdc.registry.client.RegistryFactory;

import junit.framework.TestCase;

public class ClientTest extends TestCase {
	
	private Registry client = getClient();
	
	public void testGetClient() {
		try {
			checkIVOAIDs(client);
		} catch (RegistryCommunicationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}	
	}
	
	public void testGetCapabilities(){
		try {
			checkCapabilityURLs();
		} catch (RegistryCommunicationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}	
	}
	
	public void testGetAvailability(){
		try{
			System.out.println("Retrieving availability URLs");
			for (String service:client.getIVOAIDs(Service.VAMDC_TAP)){
				URL caps = client.getAvailabilityURL(service);
				assertNotNull(caps);
				assertTrue(caps.toString().contains("availability"));
				System.out.println(caps.toString());
			}
		}catch (RegistryCommunicationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	private void checkCapabilityURLs() throws RegistryCommunicationException {
		System.out.println("Retrieving capability URLs");
		for (String service:client.getIVOAIDs(Service.VAMDC_TAP)){
			URL caps = client.getCapabilitiesURL(service);
			assertNotNull(caps);
			assertTrue(caps.toString().contains("capabilities"));
			System.out.println(caps.toString());
		}
	}

	private void checkIVOAIDs(Registry client)
			throws RegistryCommunicationException {
		assertNotNull(client.getIVOAIDs(Service.VAMDC_TAP));
		System.out.println("Retrieving IVOAIDs");
		for (String service:client.getIVOAIDs(Service.VAMDC_TAP)){
			assertNotNull(service);
			assertTrue(service.contains("ivo://vamdc/"));
			System.out.println(service);
		}
	}

	private Registry getClient() {
		Registry client = RegistryFactory.getClient();
		assertNotNull(client);
		return client;
	}

	
}
