package com.training.aem.core.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

import java.util.HashMap;
import java.util.Map;

public final class ResourceResolverUtils {

    public  static ResourceResolver getServiceResolver(final String subService,
                                                       final ResourceResolverFactory resolverFactory) {
        ResourceResolver resourceResolver = null;
        if ((null != resolverFactory) && StringUtils.isNotBlank(subService)) {
            try {
                final Map<String, Object> authMap = new HashMap<>();
                authMap.put(ResourceResolverFactory.SUBSERVICE, subService);
                resourceResolver = resolverFactory.getServiceResourceResolver(authMap);
            } catch (LoginException e) {
             //   Logger logger = null;
//                if (logger.isErrorEnabled()) {
//                    logger.error("Utils :: getResolver :: Login Exception while getting the resource resolver "
//                            + "from resourceResolverFactory");
//                }
            }
        }
        return resourceResolver;
    }
}