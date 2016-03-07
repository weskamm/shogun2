package de.terrestris.shogun2.util.interceptor;

import org.springframework.stereotype.Component;

import de.terrestris.shogun2.util.model.Response;

@Component
public interface WcsResponseInterceptorInterface {

	public Response interceptGetCapabilities(Response response);

	public Response interceptDescribeCoverage(Response response);

	public Response interceptGetCoverage(Response response);

}
