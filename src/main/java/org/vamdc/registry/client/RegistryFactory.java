package org.vamdc.registry.client;

import java.net.URL;

import org.vamdc.registry.client.impl.RegistryImpl;

public final class RegistryFactory {

	public final static String DEFAULT_REGISTRY_ENDPOINT="http://casx019-zone1.ast.cam.ac.uk/registry/services/RegistryQueryv1_0";
	

	public static Registry getClient() throws RegistryCommunicationException{
		return new RegistryImpl(DEFAULT_REGISTRY_ENDPOINT);
	}
	

	public static Registry getClient(URL registryURL) throws RegistryCommunicationException{
		return new RegistryImpl(registryURL.toString());
	}
	

	public static Registry getClient(String registryURL) throws RegistryCommunicationException{
		return new RegistryImpl(registryURL);
	}
	
	
}
