package org.vamdc.registry.client;


public class RegistryCommunicationException extends Exception{
	public RegistryCommunicationException(String string) {
		super(string);
	}

	public RegistryCommunicationException(String string, Throwable e) {
		super(string, e);
	}

	private static final long serialVersionUID = 472086748698824500L;
	
	
}
