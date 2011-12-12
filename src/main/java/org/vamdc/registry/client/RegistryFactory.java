package org.vamdc.registry.client;

import java.net.MalformedURLException;
import java.net.URL;

public final class RegistryFactory {

	/**
	 * Get a registry client with the default registry URL
	 * @return an implementation of the Registry interface
	 */
	public static Registry getClient(){
		return null;
	}
	
	/**
	 * Get a registry client with custom registry URL
	 * @param serviceURL
	 * @return
	 */
	public static Registry getClient(URL serviceURL){
		return null;
	}
	
	/**
	 * Get a registry client with custom registry URL
	 * @param serviceURL
	 * @return
	 */
	public static Registry getClient(String serviceURL){
		try {
			return getClient(new URL(serviceURL));
		} catch (MalformedURLException e) {
			return null;
		}
	}
	
	
}
