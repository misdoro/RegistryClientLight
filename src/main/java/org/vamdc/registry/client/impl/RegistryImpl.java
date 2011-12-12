package org.vamdc.registry.client.impl;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import net.ivoa.wsdl.registrysearch.RegistrySearchPortType;
import net.ivoa.wsdl.registrysearch.v1.XQuerySearchResponse;
import net.ivoa.xml.voresource.v1.Resource;

import org.vamdc.dictionary.Restrictable;
import org.vamdc.registry.client.Registry;
import org.vamdc.registry.client.RegistryCommunicationException;
import org.vamdc.registry.search.RegistryClientFactory;

public class RegistryImpl implements Registry{

	private RegistrySearch search;
	private RegistryCommunicationException storedException = null;
	
	public RegistryImpl(String registryEndpoint) {
		RegistrySearchPortType searchPort = RegistryClientFactory.getSearchPort(registryEndpoint);
		try {
			this.search = new RegistrySearch(searchPort);
		} catch (RegistryCommunicationException e) {
			storedException = e;
		}

		
	}

	public Set<String> getIVOAIDs(Service standard)
			throws RegistryCommunicationException {
		verifyStoredException();
		return Collections.unmodifiableSet(search.getIvoaIDs());
	}

	private void verifyStoredException() throws RegistryCommunicationException {
		if (storedException!=null)
			throw storedException;
	}

	public URL getCapabilitiesURL(String ivoaid)
			throws RegistryCommunicationException {
		verifyStoredException();
		return search.capabilityURLs.get(ivoaid);
	}

	public URL getAvailabilityURL(String ivoaid)
			throws RegistryCommunicationException {
		verifyStoredException();
		return search.availabilityURLs.get(ivoaid);
	}

	public Resource getResourceMetadata(String ivoaid) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Restrictable> getRestrictables(String ivoaid)
			throws RegistryCommunicationException {
		// TODO Auto-generated method stub
		return null;
	}

	public URL getTapBaseURL(String ivoaid)
			throws RegistryCommunicationException {
		// TODO Auto-generated method stub
		return null;
	}

}
