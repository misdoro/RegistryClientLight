package org.vamdc.registry.client.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBElement;

import org.vamdc.dictionary.Restrictable;
import org.vamdc.registry.client.RegistryCommunicationException;
import org.vamdc.xml.vamdc_tap.v1.VamdcTap;

import net.ivoa.wsdl.registrysearch.ErrorResp;
import net.ivoa.wsdl.registrysearch.OpUnsupportedResp;
import net.ivoa.wsdl.registrysearch.RegistrySearchPortType;
import net.ivoa.wsdl.registrysearch.v1.XQuerySearch;
import net.ivoa.wsdl.registrysearch.v1.XQuerySearchResponse;
import net.ivoa.xml.voresource.v1.Capability;
import net.ivoa.xml.voresource.v1.Resource;
import net.ivoa.xml.voresource.v1.Service;

public class RegistrySearch {

	private final static String STD_VOSI_CAPABILITIES="ivo://ivoa.net/std/VOSI#capabilities";
	private final static String STD_VOSI_AVAILABILITY="ivo://ivoa.net/std/VOSI#availability";
	private final static String STD_VAMDC_TAP="ivo://vamdc/std/VAMDC-TAP";

	private static String searchQuery  = "declare namespace ri='http://www.ivoa.net/xml/RegistryInterface/v1.0'; " +
			"declare namespace vs='http://www.ivoa.net/xml/VODataService/v1.0'; " +
			"declare namespace xsi='http://www.w3.org/2001/XMLSchema-instance'; " + 
			"for $x in //ri:Resource " + 
			"where $x/capability[@standardID='"+STD_VAMDC_TAP+"'] " +
			"and $x/@status='active' " +
			"and $x/@xsi:type='vs:CatalogService'" +
			"return $x";

	private List<Object> searchResult;
	private Set<String> ivoaIDs = new HashSet<String>();

	Map<String,URL> capabilityURLs = new HashMap<String,URL>();
	Map<String,URL> availabilityURLs = new HashMap<String,URL>();
	Map<String,URL> vamdcTapURLs = new HashMap<String,URL>();
	
	Map<String,Set<Restrictable>> vamdcTapRestrictables = new HashMap<String,Set<Restrictable>>();
	
	Map<String,Resource> resultResources = new HashMap<String,Resource>();

	public Set<String> getIvoaIDs() {
		return ivoaIDs;
	}


	RegistrySearch(RegistrySearchPortType searchPort) throws RegistryCommunicationException{

		XQuerySearch xQuerySearch = new XQuerySearch();
		xQuerySearch.setXquery(searchQuery);

		try {
			XQuerySearchResponse xqResp = searchPort.xQuerySearch(xQuerySearch);
			this.searchResult = xqResp.getAny();
		} catch (ErrorResp e) {
			throw new RegistryCommunicationException("The registry returned an error",e);
		} catch (OpUnsupportedResp e) {
			throw new RegistryCommunicationException("The registry said that the operation is unsupported",e);
		}
		
		if (searchResult==null || searchResult.size()==0)
			throw new RegistryCommunicationException("The registry returned no results");
		for (Object element:searchResult){
			JAXBElement<?> obj = (JAXBElement<?>)element;
			Service srv = (Service) obj.getValue();
			
			extractServiceEndpoints(srv);
			
			resultResources.put(srv.getIdentifier(), srv);
			
			
		}
	}


	private void extractServiceEndpoints(Service srv) {
		String ivoaid = srv.getIdentifier();
		URL capabilitiesURL=null;
		URL availabilityURL=null;
		URL vamdcTapURL=null;
		Set<Restrictable> keywords = null;
		try {
			for (Capability cap:srv.getCapability()){

				if (STD_VOSI_CAPABILITIES.equals(cap.getStandardID()))
					capabilitiesURL=new URL(cap.getInterface().get(0).getAccessURL().get(0).getValue());
				else if (STD_VOSI_AVAILABILITY.equals(cap.getStandardID()))
					availabilityURL= new URL(cap.getInterface().get(0).getAccessURL().get(0).getValue());
				else if (STD_VAMDC_TAP.equals(cap.getStandardID())){
					vamdcTapURL=new URL(cap.getInterface().get(0).getAccessURL().get(0).getValue());
					VamdcTap capability = (VamdcTap)cap;
					keywords = extractRestrictables( capability);
					
				}
			}
		} catch (MalformedURLException e) {
			
		}finally{
			if (capabilitiesURL!=null && availabilityURL!=null && vamdcTapURL!=null && keywords!=null){
				capabilityURLs.put(ivoaid, capabilitiesURL);
				availabilityURLs.put(ivoaid, availabilityURL);
				vamdcTapURLs.put(ivoaid, vamdcTapURL);
				vamdcTapRestrictables.put(ivoaid, keywords);
				ivoaIDs.add(ivoaid);
				
			}
		}
	}


	private Set<Restrictable> extractRestrictables(VamdcTap capability) {
		Set<Restrictable> restrictables = new HashSet<Restrictable>();
		for (String keyword:capability.getRestrictable()){
			
			try{
				Restrictable rest = Restrictable.valueOfIgnoreCase(keyword);
				restrictables.add(rest);
			}catch(IllegalArgumentException e){
				System.out.println("Unknown keyword"+keyword);
			}
		}
		if (restrictables.size()>0)
			return EnumSet.copyOf(restrictables);
		return EnumSet.noneOf(Restrictable.class);
	}

}
