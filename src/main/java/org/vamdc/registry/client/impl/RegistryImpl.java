package org.vamdc.registry.client.impl;

import java.net.URL;
import java.util.Collection;
import java.util.Set;

import net.ivoa.xml.voresource.v1.Resource;

import org.vamdc.dictionary.Restrictable;
import org.vamdc.registry.client.Registry;
import org.vamdc.registry.client.RegistryCommunicationException;

public class RegistryImpl implements Registry{

	public Collection<String> getIVOAIDs(Service standard)
			throws RegistryCommunicationException {
		// TODO Auto-generated method stub
		return null;
	}

	public URL getCapabilitiesURL(String ivoaid)
			throws RegistryCommunicationException {
		// TODO Auto-generated method stub
		return null;
	}

	public URL getAvailabilityURL(String ivoaid)
			throws RegistryCommunicationException {
		// TODO Auto-generated method stub
		return null;
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
