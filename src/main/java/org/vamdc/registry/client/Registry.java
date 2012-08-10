package org.vamdc.registry.client;

import java.net.URL;
import java.util.Collection;
import java.util.List;
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
		CONSUMER("ivo://vamdc/std/XSAMS-consumer"),
		;
		
		private final String stdID;
		
		Service(String standardID){
			this.stdID=standardID;
		}
		
		public String getStandardID(){
			return stdID;
		}
	}
	
	
	/**
	 * Get all registered IVOA identifiers
	 * @return a collection of IVOA identifiers from the registry
	 */
	public Collection<String> getIVOAIDs(Service standard);
	

	
	public URL getCapabilitiesURL(String ivoaid);
	
	public URL getAvailabilityURL(String ivoaid);
	
	public Resource getResourceMetadata(String ivoaid);
	
	public Set<Restrictable> getRestrictables(String ivoaid);
	
	public URL getVamdcTapURL(String ivoaid);
	
	public List<VamdcTapService> getMirrors(String ivoaid);
	
}
