package org.vamdc.registry.client;

import java.net.URL;

/**
 * A class providing a set of fields to distinguish and quickly access VAMDC-TAP mirrors
 * 
 */
public class VamdcTapService {
	public final String ivoaID;
	public final URL TAPEndpoint;
	public final URL CapabilitiesEndpoint;
	public final URL AvailabilityEndpoint;
	
	public VamdcTapService(String ivoaID,URL tap, URL capabilities, URL availability){
		this.ivoaID = ivoaID;
		this.TAPEndpoint=tap;
		this.CapabilitiesEndpoint = capabilities;
		this.AvailabilityEndpoint = availability;
	}
	
}
