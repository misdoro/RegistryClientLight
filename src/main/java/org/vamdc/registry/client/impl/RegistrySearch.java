package org.vamdc.registry.client.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBElement;

import org.vamdc.dictionary.Restrictable;
import org.vamdc.registry.client.RegistryCommunicationException;
import org.vamdc.registry.client.VamdcTapService;
import org.vamdc.xml.vamdc_tap.v1.VamdcTap;
import org.w3c.dom.Node;

import net.ivoa.wsdl.registrysearch.ErrorResp;
import net.ivoa.wsdl.registrysearch.OpUnsupportedResp;
import net.ivoa.wsdl.registrysearch.RegistrySearchPortType;
import net.ivoa.wsdl.registrysearch.v1.XQuerySearch;
import net.ivoa.wsdl.registrysearch.v1.XQuerySearchResponse;
import net.ivoa.xml.voresource.v1.AccessURL;
import net.ivoa.xml.voresource.v1.Capability;
import net.ivoa.xml.voresource.v1.Resource;
import net.ivoa.xml.voresource.v1.Service;

public class RegistrySearch {

	private final static String STD_VOSI_CAPABILITIES="ivo://ivoa.net/std/VOSI#capabilities";
	private final static String STD_VOSI_AVAILABILITY="ivo://ivoa.net/std/VOSI#availability";
	private final static String STD_VAMDC_TAP="ivo://vamdc/std/VAMDC-TAP";
	private final static String STD_XSAMS_CONSUMER="ivo://vamdc/std/XSAMS-consumer";

	private static String vamdcTapSearchQuery  = "declare namespace ri='http://www.ivoa.net/xml/RegistryInterface/v1.0'; " +
			"declare namespace vs='http://www.ivoa.net/xml/VODataService/v1.0'; " +
			"declare namespace vr='http://www.ivoa.net/xml/VOResource/v1.0'; " +
			"declare namespace xsi='http://www.w3.org/2001/XMLSchema-instance'; " + 
			"for $x in //ri:Resource " + 
			"where $x/capability[@standardID='"+STD_VAMDC_TAP+"'] " +
			"and $x/@status='active' " +
			"and ($x/@xsi:type='vs:CatalogService' or $x/@xsi:type='vr:Service')" +
			"return $x";
	
	private static String consumerSearchQuery  = "declare namespace ri='http://www.ivoa.net/xml/RegistryInterface/v1.0'; " +
			"declare namespace vr='http://www.ivoa.net/xml/VOResource/v1.0'; " +
			"declare namespace xsi='http://www.w3.org/2001/XMLSchema-instance'; " + 
			"for $x in //ri:Resource " + 
			"where $x/capability[@standardID='"+STD_XSAMS_CONSUMER+"'] " +
			"and $x/@status='active' " +
			"and $x/@xsi:type='vr:Service'" +
			"return $x";
	
	
	private Set<String> tapIvoaIDs = new HashSet<String>();
	private Set<String> consumerIvoaIDs = new HashSet<String>();
	
	private RegistrySearchPortType searchPort;
	
	Map<String,URL> capabilityURLs = new HashMap<String,URL>();
	Map<String,URL> availabilityURLs = new HashMap<String,URL>();
	Map<String,URL> vamdcTapURLs = new HashMap<String,URL>();
	Map<String,URL> consumerURLs = new HashMap<String,URL>();
	Map<String,String> consumerNumberOfInputs = new HashMap<String,String>();
	Map<String,List<VamdcTapService>> mirrors = new HashMap<String,List<VamdcTapService>>();
	
	Map<String,Set<Restrictable>> vamdcTapRestrictables = new HashMap<String,Set<Restrictable>>();
	
	Map<String,List<String>> vamdcTapProcessors = new HashMap<String,List<String>>();
	
	Map<String,Resource> resultResources = new HashMap<String,Resource>();

	public Set<String> getTapIvoaIDs() {
		return tapIvoaIDs;
	}


	public Set<String> getConsumerIvoaIDs() {
		return consumerIvoaIDs;
	}


	RegistrySearch(RegistrySearchPortType searchPort) throws RegistryCommunicationException{
		this.searchPort = searchPort;
		XQuerySearch vamdcTapSearch = new XQuerySearch();
		vamdcTapSearch.setXquery(vamdcTapSearchQuery);
		
		List<Object> vamdcTapServices = tryRegistrySearch(searchPort, vamdcTapSearch);
		treatRegistryResponse(vamdcTapServices);
		
		XQuerySearch xsamsConsumerSearch = new XQuerySearch();
		xsamsConsumerSearch.setXquery(consumerSearchQuery);
		
		List<Object> consumers = tryRegistrySearch(searchPort,xsamsConsumerSearch);
		treatRegistryResponse(consumers);
		

	}


	private void treatRegistryResponse(List<Object> searchResult) throws RegistryCommunicationException {
		if (searchResult==null || searchResult.size()==0)
			throw new RegistryCommunicationException("The registry returned no results");
		for (Object element:searchResult){
			JAXBElement<?> obj = (JAXBElement<?>)element;
			Service srv = (Service) obj.getValue();
			
			extractServiceEndpoints(srv);
			
			resultResources.put(srv.getIdentifier(), srv);				
		}
	}


	private List<Object> tryRegistrySearch(RegistrySearchPortType searchPort,
			XQuerySearch xQuerySearch) throws RegistryCommunicationException {
		try {
			XQuerySearchResponse xqResp = searchPort.xQuerySearch(xQuerySearch);
			return xqResp.getAny();
		} catch (ErrorResp e) {
			throw new RegistryCommunicationException("The registry returned an error",e);
		} catch (OpUnsupportedResp e) {
			throw new RegistryCommunicationException("The registry said that the operation is unsupported",e);
		}
	}
	
	private String getProcessorNumberOfInputs(String processorId) throws RegistryCommunicationException{

		String numberOfInputsQuery = "declare namespace ri='http://www.ivoa.net/xml/RegistryInterface/v1.0'; " +
				" declare namespace vr='http://www.ivoa.net/xml/VOResource/v1.0'; " +
				" declare namespace xsi='http://www.w3.org/2001/XMLSchema-instance'; " + 
				" for $x in //ri:Resource " + 
				" where $x/identifier='"+processorId+"' " +
				" return $x/capability[@standardID='ivo://vamdc/std/XSAMS-consumer']/numberOfInputs"; 
		
		XQuerySearch testSearch = new XQuerySearch();
		testSearch.setXquery(numberOfInputsQuery);
		List<Object> results = tryRegistrySearch(searchPort, testSearch);
		return ((Node)results.get(0)).getTextContent();
	}


	private void extractServiceEndpoints(Service srv) {
		String ivoaid = srv.getIdentifier();
		URL consumerURL=null;
		Set<Restrictable> keywords = null;
		List<String> processors=null;
		
		List<AccessURL> caps = null;
		List<AccessURL> avail = null;
		List<AccessURL> taps = null;
		
		int mirrorCount=0;
		for (Capability cap:srv.getCapability()){
			if (STD_VOSI_CAPABILITIES.equals(cap.getStandardID())){
				mirrorCount = cap.getInterface().get(0).getAccessURL().size();
			}
		}
		
		try {
			for (Capability cap:srv.getCapability()){
				if (STD_VOSI_CAPABILITIES.equals(cap.getStandardID()))
					caps = cap.getInterface().get(0).getAccessURL();
				else if (STD_VOSI_AVAILABILITY.equals(cap.getStandardID()))
					avail = cap.getInterface().get(0).getAccessURL();
				else if (STD_VAMDC_TAP.equals(cap.getStandardID())){
					taps = cap.getInterface().get(0).getAccessURL();
					VamdcTap capability = (VamdcTap)cap;
					keywords = extractRestrictables(ivoaid, capability);
					processors = extractProcessors(ivoaid,capability);
				}else if (STD_XSAMS_CONSUMER.equals(cap.getStandardID())){
					consumerURL = new URL(cap.getInterface().get(1).getAccessURL().get(0).getValue());
				}
			}
		} catch (MalformedURLException e) {
		}finally{
			if (checkList(caps, mirrorCount) 
					&& checkList(avail,mirrorCount) 
					&& checkList(taps,mirrorCount) 
					&& keywords!=null){
				List<VamdcTapService> mirrorList = new ArrayList<VamdcTapService>(mirrorCount);
				for (int i=0;i<mirrorCount;i++){
					try {
						mirrorList.add(
								new VamdcTapService(ivoaid, 
								new URL(taps.get(i).getValue()), 
								new URL(caps.get(i).getValue()),
								new URL(avail.get(i).getValue()))
						);
					} catch (MalformedURLException e) {}
				}
				mirrors.put(ivoaid, mirrorList);
				capabilityURLs.put(ivoaid, mirrorList.get(0).CapabilitiesEndpoint);
				availabilityURLs.put(ivoaid, mirrorList.get(0).AvailabilityEndpoint);
				vamdcTapURLs.put(ivoaid, mirrorList.get(0).TAPEndpoint);
				vamdcTapRestrictables.put(ivoaid, keywords);
				vamdcTapProcessors.put(ivoaid, processors);
				tapIvoaIDs.add(ivoaid);
			}else if (consumerURL!=null){
				consumerURLs.put(ivoaid, consumerURL);
				consumerIvoaIDs.add(ivoaid);
			}
			

		}
	}


	private static boolean checkList(List<AccessURL> caps, int mirrorCount) {
		return caps!=null && caps.size()==mirrorCount;
	}

	private List<String> extractProcessors(String ivoaid,VamdcTap capability){
		List<String> apps = capability.getApplication();
		if (apps!=null && apps.size()>0){
			return Collections.unmodifiableList(apps);
		}
		return Collections.emptyList();
	}

	private Set<Restrictable> extractRestrictables(String ivoaid,VamdcTap capability) {
		Set<Restrictable> result = new HashSet<Restrictable>();
		List<String> unknownKeywords = new ArrayList<String>();
		
		for (String keyword:capability.getRestrictable()){
			try{
				Restrictable rest = Restrictable.valueOfIgnoreCase(keyword);
				result.add(rest);
			}catch(IllegalArgumentException e){
				unknownKeywords.add(keyword);
			}
		}
		
		if (unknownKeywords.size()>0){
			System.err.println("Unknown keywords for ivoaid "+ivoaid);
			for (String keyword:unknownKeywords){
				System.err.println(keyword);
			}
		}
		
		if (result.size()>0)
			return EnumSet.copyOf(result);
		return EnumSet.noneOf(Restrictable.class);
	}

}
