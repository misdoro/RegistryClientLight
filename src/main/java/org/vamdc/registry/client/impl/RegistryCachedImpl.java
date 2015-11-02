package org.vamdc.registry.client.impl;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.ivoa.wsdl.registrysearch.RegistrySearchPortType;
import net.ivoa.xml.voresource.v1.Resource;

import org.vamdc.dictionary.Restrictable;
import org.vamdc.registry.client.Registry;
import org.vamdc.registry.client.RegistryCommunicationException;
import org.vamdc.registry.client.VamdcTapService;
import org.vamdc.registry.search.RegistryClientFactory;

/**
 * Provides the cached implementation of the registry client, 
 * all data is retrieved during the initialization and then cached responses are given.
 * @author doronin
 *
 */

public class RegistryCachedImpl implements Registry{

	private RegistrySearch search;
	
	public RegistryCachedImpl(String registryEndpoint) throws RegistryCommunicationException{
		
		try{
			RegistrySearchPortType searchPort = RegistryClientFactory.getSearchPort(registryEndpoint);
		
			this.search = new RegistrySearch(searchPort);
		}catch(RegistryCommunicationException e){
			throw e;
		}catch(Exception e){
			Throwable ecause=null;
			if ((ecause=e.getCause())!=null)
				throw new RegistryCommunicationException("Communication error: "+ecause.getMessage(),ecause);
			else
				throw new RegistryCommunicationException("Communication error: "+e.getMessage(),e);
		}
		
	}
	
	@Override
	public Set<String> getIVOAIDs(Service standard){
		switch(standard){
		case VAMDC_TAP:
			return Collections.unmodifiableSet(search.getTapIvoaIDs());
		case CONSUMER:
			return Collections.unmodifiableSet(search.getConsumerIvoaIDs());
		default:
			return Collections.emptySet();
		}
	}
	
	@Override
	public Set<String> getInactiveIVOAIDs(Service standard){
		switch(standard){
		case VAMDC_TAP:
			return Collections.unmodifiableSet(search.getTapInactiveIvoaIDs());
		case CONSUMER:
			return Collections.unmodifiableSet(search.getConsumerInactiveIvoaIDs());
		default:
			return Collections.emptySet();
		}
	}
	
	@Override
	public URL getCapabilitiesURL(String ivoaid){
		return search.capabilityURLs.get(ivoaid);
	}
	
	@Override
	public URL getAvailabilityURL(String ivoaid){
		return search.availabilityURLs.get(ivoaid);
	}
	
	@Override
	public Resource getResourceMetadata(String ivoaid) {
		return search.resultResources.get(ivoaid);
	}
	
	@Override
	public Set<Restrictable> getRestrictables(String ivoaid){
		Set<Restrictable> ret=search.vamdcTapRestrictables.get(ivoaid);
		if (ret!=null)
			return Collections.unmodifiableSet(ret);
		return Collections.emptySet();
	}
	
	@Override
	public URL getVamdcTapURL(String ivoaid){
		return search.vamdcTapURLs.get(ivoaid);
	}

	@Override
	public List<VamdcTapService> getMirrors(String ivoaid) {
		List<VamdcTapService> ret=search.mirrors.get(ivoaid);
		if (ret!=null)
			return Collections.unmodifiableList(ret);
		return Collections.emptyList();
	}

	@Override
	public List<String> getProcessors(String ivoaid) {
		return search.vamdcTapProcessors.get(ivoaid);
	}
	
	
}
