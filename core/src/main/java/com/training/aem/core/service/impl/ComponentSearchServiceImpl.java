package com.training.aem.core.service.impl;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.gson.Gson;
import com.training.aem.core.service.ComponentSearchService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Component(service = ComponentSearchService.class, immediate = true)
public class ComponentSearchServiceImpl implements ComponentSearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentSearchServiceImpl.class);
    public static final String SAMPLE_SERVICE = "systemUser";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    QueryBuilder queryBuilder;

    /**
     * Retrieves the component search count for a given resource path and parent path.
     *
     * @param resourcePath The path to the resource.
     * @param parentPath The parent path for the search.
     * @return A JSON representation of the component search count based on the given paths.
     *         Returns an empty string if the resource path or parent path is blank or null.
     */
    @Override
    public String getComponentSearchCount(final String resourcePath, final String parentPath) {
        try (final ResourceResolver resolver = getResourceResolver(SAMPLE_SERVICE)) {
            if (resolver != null) {
                final Map<String, String> query = getQuery(resourcePath, parentPath);
                final List<Resource> resulSet = executeQuery(query, resolver);
                final Map<String, Integer> countList = countList(resulSet, resolver);
                return new Gson().toJson(countList);
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * Counts the occurrences of resources grouped by their paths.
     *
     * This method takes a list of resources and counts the occurrences of each unique path.
     * The count is represented as a map where the keys are the unique resource paths,
     * and the values are the number of occurrences for each path in the provided list.
     *
     * @param results A list of Resource objects to count.
     * @param resolver The ResourceResolver used to adapt resources to pages and perform operations.
     * @return A map where keys are unique resource paths and values are the count of occurrences for each path.
     *         An empty map is returned if the provided list of resources is null or empty.
     */
    protected Map<String, Integer> countList(final List<Resource> results, final ResourceResolver resolver) {
        if (resolver != null) {
            final PageManager pm = resolver.adaptTo(PageManager.class);
            return results.stream()
                    .map(pm::getContainingPage)
                    .filter(page -> page != null)
                    .map(Page::getPath)
                    .collect(Collectors.groupingBy(path -> path, Collectors.summingInt(path -> 1)));
        }
        return new HashMap<>();
    }

    /**
     * Method to execute the query builder query in order to retrieve all the
     * paths where sling resource type is as set in the query
     *
     * @param queryMap
     * @return List of search result resources
     */
    protected List<Resource> executeQuery(final Map<String, String> queryMap, final ResourceResolver resolver) {
        if (resolver != null) {
            final Session session = resolver.adaptTo(Session.class);
            Query q = queryBuilder.createQuery(PredicateGroup.create(queryMap), session);
            SearchResult result = q.getResult();

            return result.getHits().stream()
                    .map(hit -> {
                        try {
                            return hit.getResource();
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(resource -> resource != null)
                    .collect(Collectors.toList());
        }
        return Collections.EMPTY_LIST;

    }

    /**
     * Constructs a query to retrieve components based on the provided resource path and parent path.
     *
     * @param resourcePath The resource path to filter components by.
     * @param parentPath   The parent path to filter components by.
     * @return A {@link Map} representing the query parameters, with keys such as "path" and "type" to filter the components.
     * The query may include additional parameters specific to the application's component search criteria.
     * For example:
     * - "path" parameter: The resource path for filtering components.
     * - "type" parameter: The component type or category to filter by.
     * - Additional custom parameters based on the application's search requirements.
     * @throws IllegalArgumentException if either {@code resourcePath} or {@code parentPath} is null or empty.
     */

    protected Map<String, String> getQuery(final String resourcePath,
                                         final String parentPath) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("path", parentPath);
        queryParams.put("property", "sling:resourceType");
        queryParams.put("property.value", resourcePath);

        return queryParams;
    }

    /**
     * Returns {@link ResourceResolver} of the given sub-service. It returns null in case given
     * {@link ResourceResolverFactory} and subService are null.
     *
     * @param subService {@link String} sub-service defined in Sling Apache User Mapping configuration
     * @return {@link ResourceResolver}
     */
    protected ResourceResolver getResourceResolver(final String subService) {
        ResourceResolver resourceResolver = null;
        if (null != resourceResolverFactory && null != subService) {
            try {
                final Map<String, Object> authInfo = new HashMap<>();
                authInfo.put(ResourceResolverFactory.SUBSERVICE, subService);
                resourceResolver = resourceResolverFactory.getServiceResourceResolver(authInfo);
            } catch (final LoginException loginException) {
                LOGGER.error(
                        "MadisonUtil getResourceResolver() : Exception while getting resource resolver for subservice {} : {}",
                        subService, loginException);
            }
        }
        return resourceResolver;
    }

    /**
     * Sets the QueryBuilder instance to be used by this class.
     *
     * The QueryBuilder is responsible for constructing queries used for searching resources.
     *
     * @param queryBuilder The QueryBuilder instance to be set. Should not be null.
     */
    public void setQueryBuilder(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }
    /**
     * Sets the {@code ResourceResolverFactory} to be used by this class for obtaining
     * {@code ResourceResolver} instances.
     *
     * @param resourceResolverFactory The {@code ResourceResolverFactory} to set. It should not be {@code null}.
     * @throws IllegalArgumentException if {@code resourceResolverFactory} is {@code null}.
     */
    public void setResourceResolverFactory(ResourceResolverFactory resourceResolverFactory) {
        this.resourceResolverFactory = resourceResolverFactory;
    }
}
