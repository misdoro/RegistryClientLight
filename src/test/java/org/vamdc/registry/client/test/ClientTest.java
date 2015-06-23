package org.vamdc.registry.client.test;

import static org.junit.Assert.*;

import java.net.URL;
import java.util.List;
import java.util.Set;

import net.ivoa.xml.voresource.v1.Resource;

import org.junit.Test;
import org.vamdc.dictionary.Restrictable;
import org.vamdc.registry.client.Registry;
import org.vamdc.registry.client.Registry.Service;
import org.vamdc.registry.client.RegistryCommunicationException;
import org.vamdc.registry.client.RegistryFactory;
import org.vamdc.registry.client.VamdcTapService;

public class ClientTest{
	public final static String UnknownHostURL="http://randomgarbageasdfkasdkjhfqwerjgflkesjtghvszdckxlaelfjhs.com/registry/services/RegistryQueryv1_0";
	public final static String UnreachURL="http://255.255.255.255/registry/services/RegistryQueryv1_0";

	private Registry client = getClient();

	@Test
	public void testUnknownHost(){
		Registry client = null;
		try {
			client = RegistryFactory.getClient(UnknownHostURL);
		} catch (RegistryCommunicationException e) {
			e.printStackTrace();
			assertNotNull(e.getMessage());
			assertNotNull(e.getCause());
			assertTrue(e.getCause() instanceof java.net.UnknownHostException);
		} catch (Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertNull(client);
	}
	
	@Test
	public void testUnreachHost(){
		Registry client = null;
		try {
			client = RegistryFactory.getClient(UnreachURL);
		} catch (RegistryCommunicationException e) {
			e.printStackTrace();
			assertNotNull(e.getMessage());
			assertNotNull(e.getCause());
			assertTrue(e.getCause() instanceof java.net.SocketException);
		} catch (Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertNull(client);
	}
	
	@Test
	public void testGetClient() {
		try {
			checkIVOAIDs(client);
		} catch (RegistryCommunicationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}	
	}

	@Test
	public void testConsumers() {
		try {
			checkConsumerIDs(client);
		} catch (RegistryCommunicationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}	
	}

	@Test
	public void testGetCapabilities(){
		try {
			checkCapabilityURLs();
		} catch (RegistryCommunicationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}	
	}

	@Test
	public void testGetAvailability(){
		try{
			checkAvailabiltyURLs();
		}catch (RegistryCommunicationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetResource(){
		System.out.println("Retrieving database names");
		for (String service:client.getIVOAIDs(Service.VAMDC_TAP)){
			Resource res = client.getResourceMetadata(service);
			assertNotNull(res);
			System.out.println();
			System.out.println(service);
			System.out.println(res.getTitle());
			System.out.println(res.getCuration().getContact().get(0).getEmail());
			System.out.println(res.getCuration().getPublisher().getValue());

		}
	}

	@Test
	public void testGetRestrictables(){
		System.out.println("Retrieving supported restrictables");
		for (String service:client.getIVOAIDs(Service.VAMDC_TAP)){
			Set<Restrictable> restricts =client.getRestrictables(service);
			assertNotNull(restricts);
			assertTrue(restricts.size()>0);
			System.out.println();
			System.out.println(service);
			for (Restrictable keyword:restricts){
				System.out.println(keyword.name()+"("+keyword.getInfo()+")");
			}
		}
	}
	
	@Test
	public void findMirrors(){
		System.out.println("Looking for mirrors");
		int mirrorsCount=0;
		for (String service:client.getIVOAIDs(Service.VAMDC_TAP)){
			List<VamdcTapService> mirrors=client.getMirrors(service);
			assertTrue(mirrors.size()>0);
			mirrorsCount = Math.max(mirrorsCount, mirrors.size());
			if (mirrors.size()>1){
				System.out.println("Found mirrors for "+service);
				System.out.println("Main URL: "+mirrors.get(0).TAPEndpoint);
				for (int i=1;i<mirrors.size();i++){
					System.out.println("Mirror "+i+": "+mirrors.get(i).TAPEndpoint);
					assertEquals(service,mirrors.get(i).ivoaID);
					assertFalse(mirrors.get(0).TAPEndpoint.equals(mirrors.get(i).TAPEndpoint));
				}
			}
		}
		assertTrue(mirrorsCount>=1);
	}
	
	@Test
	public void findWrongMirrors(){
		System.out.println("Looking for mirrors of a wrong ivoaid");
		for (VamdcTapService none:client.getMirrors("wrong")){
			fail("We should have an empty list here");
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

	private void checkConsumerIDs(Registry client)
			throws RegistryCommunicationException {
		assertNotNull(client.getIVOAIDs(Service.CONSUMER));
		System.out.println("Retrieving IVOAIDs");
		for (String service:client.getIVOAIDs(Service.CONSUMER)){
			assertNotNull(service);
			assertTrue(service.contains("ivo://vamdc/"));
			System.out.println(service);
		}
	}

	private Registry getClient() {
		Registry client = null;
		try {
			client = RegistryFactory.getClient(RegistryFactory.REGISTRY_12_07);
		} catch (RegistryCommunicationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertNotNull(client);
		return client;
	}


}
