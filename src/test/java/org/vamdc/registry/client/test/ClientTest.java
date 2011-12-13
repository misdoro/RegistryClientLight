package org.vamdc.registry.client.test;

import java.net.URL;
import java.util.Set;

import net.ivoa.xml.voresource.v1.Resource;

import org.vamdc.dictionary.Restrictable;
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
			checkAvailabiltyURLs();
		}catch (RegistryCommunicationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testGetResource(){
		try{
			System.out.println("Retrieving database names");
			for (String service:client.getIVOAIDs(Service.VAMDC_TAP)){
				Resource res = client.getResourceMetadata(service);
				assertNotNull(res);
				System.out.println();
				System.out.println(service);
				System.out.println(res.getTitle());
				System.out.println(res.getCuration().getPublisher().getValue());
				
			}
		}catch (RegistryCommunicationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testGetRestrictables(){
		try{
			System.out.println("Retrieving supported restrictables");
			for (String service:client.getIVOAIDs(Service.VAMDC_TAP)){
				Set<Restrictable> restricts =client.getRestrictables(service);
				assertNotNull(restricts);
				assertTrue(restricts.size()>0);
				System.out.println();
				System.out.println(service);
				for (Restrictable keyword:restricts){
					System.out.println(keyword.name()+"("+keyword.info()+")");
				}
			}
		}catch (RegistryCommunicationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	private void checkAvailabiltyURLs() throws RegistryCommunicationException {
		System.out.println("Retrieving availability URLs");
		for (String service:client.getIVOAIDs(Service.VAMDC_TAP)){
			URL caps = client.getAvailabilityURL(service);
			assertNotNull(caps);
			assertTrue(caps.toString().contains("availability"));
			System.out.println(caps.toString());
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
		Registry client = null;
		try {
			client = RegistryFactory.getClient();
		} catch (RegistryCommunicationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertNotNull(client);
		return client;
	}

	
}
