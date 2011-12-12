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
	
	
	/**
	 * Get a set of Restrictable keywords for a database
	 * @param ivoaid IVOA identifier of the desired tap service
	 * @return a set of org.vamdc.dictionary.Restrictable keywords supported by the node.
	 * @throws RegistryCommunicationException
	 */
	public Set<Restrictable> getRestrictables(String ivoaid) throws RegistryCommunicationException;
	
	/**
	 * Get a tap URL for the specified IVOA identifier
	 * @param ivoaid ivoa identifier of the desired tap service
	 * @return tapservice access URL or null if something went wrong.
	 */
	public URL getTapBaseURL(String ivoaid) throws RegistryCommunicationException;
	
}
