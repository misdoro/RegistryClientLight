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
	 * Get all registered IVOA identifiers of the service type Service (VAMDC-TAP or XSAMS-consumer)
	 * @return a collection of IVOA identifiers from the registry
	 */
	public Collection<String> getIVOAIDs(Service standard);
	
	/**
	 * Get the capabilities URL of the VAMDC-TAP service ivoaid
	 * Deprecated - use GetMirrors.
	 * @param ivoaid
	 * @return
	 */
	@Deprecated
	public URL getCapabilitiesURL(String ivoaid);
	
	/**
	 * Get the availability URL of the VAMDC-TAP service ivoaid.
	 * Deprecated - use GetMirrors.
	 * @param ivoaid
	 * @return
	 */
	@Deprecated
	public URL getAvailabilityURL(String ivoaid);
	
	/**
	 * Get the complete resource metadata of the registered service ivoaid
	 * @param ivoaid
	 * @return
	 */
	public Resource getResourceMetadata(String ivoaid);
	
	/**
	 * Get the restrictables of the VAMDC-TAP service ivoaid
	 * @param ivoaid
	 * @return
	 */
	public Set<Restrictable> getRestrictables(String ivoaid);
	
	/**
	 * Get the preferred processors list of the VAMDC-TAP service ivoaid
	 * @param ivoaid
	 * @return
	 */
	public List<String> getProcessors(String ivoaid);
	
	/**
	 * Get the VAMDC-TAP URL of the VAMDC-TAP service ivoaid
	 * @param ivoaid
	 * @return
	 */
	@Deprecated
	public URL getVamdcTapURL(String ivoaid);
	
	/**
	 * Get all the registered mirrors of the VAMDC-TAP service ivoaid
	 * @param ivoaid
	 * @return
	 */
	public List<VamdcTapService> getMirrors(String ivoaid);
	
}
