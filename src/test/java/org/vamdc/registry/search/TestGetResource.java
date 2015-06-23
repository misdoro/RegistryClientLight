package org.vamdc.registry.search;

import org.vamdc.registry.search.RegistryClientFactory;

import net.ivoa.wsdl.registrysearch.ErrorResp;
import net.ivoa.wsdl.registrysearch.NotFoundResp;
import net.ivoa.wsdl.registrysearch.RegistrySearchPortType;
import net.ivoa.xml.voresource.v1.Resource;
import junit.framework.TestCase;

public class TestGetResource extends TestCase {

	public final static String REGISTRY_URL="http://registry.vamdc.eu/registry-12.07/services/RegistryQueryv1_0";
	public void testGetResource() {
		RegistrySearchPortType search = RegistryClientFactory.getSearchPort(REGISTRY_URL);
		Resource result = null;
		try {
			//result = search.getResource("ivo://vamdc/basecol/tap-xsams");
			result = search.getResource("ivo://vamdc/xsams2sme");
		} catch (ErrorResp e) {
			fail (e.getMessage());
		} catch (NotFoundResp e) {
			fail (e.getMessage());
		} catch (Exception e){
			e.printStackTrace();
			fail (e.getMessage());
		}
		
		assertNotNull(result);
		assertNotNull(result.getTitle());
		//assertTrue(result.getTitle().contains("BASECOL"));
		System.out.println(result);
	}

}
