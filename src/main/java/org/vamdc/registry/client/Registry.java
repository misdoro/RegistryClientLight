package org.vamdc.registry.client;

import java.net.URL;
import java.util.Collection;
import java.util.Set;

import net.ivoa.xml.voresource.v1.Resource;

import org.vamdc.dictionary.Restrictable;

public interface Registry {

	public enum Status{
		OK,
		NetworkError,
		QueryError,
		
	}
	
	public enum Service{
		VAMDC_TAP("ivo://vamdc/std/VAMDC-TAP"),
		CONSUMER("ivo://vamdc/std/XSAMS-CONSUMER"),
		;
		
		private final String stdID;
		
		Service(String standardID){
			this.stdID=standardID;
		}
		
		String getStandardID(){
			return stdID;
		}
	}
	
	
	/**
	 * Get all registered IVOA identifiers
	 * @return a collection of IVOA identifiers from the registry
	 */
	public Collection<String> getIVOAIDs(Service standard) throws RegistryCommunicationException;
	

	
	public URL getCapabilitiesURL(String ivoaid) throws RegistryCommunicationException;
	
	public URL getAvailabilityURL(String ivoaid) throws RegistryCommunicationException;
	
	public Resource getResourceMetadata(String ivoaid) throws RegistryCommunicationException;;
	
	public Set<Restrictable> getRestrictables(String ivoaid) throws RegistryCommunicationException;
	
	public URL getVamdcTapURL(String ivoaid) throws RegistryCommunicationException;
	
}
