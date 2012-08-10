package org.vamdc.registry.client;

import java.net.URL;

import org.vamdc.registry.client.impl.RegistryCachedImpl;

public final class RegistryFactory {

	public final static String DEVEL_REGISTRY_ENDPOINT="http://casx019-zone1.ast.cam.ac.uk/registry/services/RegistryQueryv1_0";
	public final static String REGISTRY_11_12="http://registry.vamdc.eu/registry-11.12/services/RegistryQueryv1_0";
	public final static String REGISTRY_12_07="http://registry.vamdc.eu/registry-12.07/services/RegistryQueryv1_0";
	
	

	public static Registry getClient() throws RegistryCommunicationException{
		return new RegistryCachedImpl(REGISTRY_12_07);
	}
	

	public static Registry getClient(URL registryURL) throws RegistryCommunicationException{
		return new RegistryCachedImpl(registryURL.toString());
	}
	

	public static Registry getClient(String registryURL) throws RegistryCommunicationException{
		return new RegistryCachedImpl(registryURL);
	}
	
	
}
