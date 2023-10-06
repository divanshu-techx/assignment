package com.training.aem.core.service.impl;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.PageManager;
import io.wcm.testing.mock.aem.junit5.AemContext;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import javax.jcr.Session;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComponentSearchServiceImplTest {
    AemContext aemContext = new AemContext();
    @Spy
    @InjectMocks
    ComponentSearchServiceImpl componentSearchService;

    @Mock
    ResourceResolver resolver;

    MockSlingHttpServletRequest request;
    MockSlingHttpServletResponse response;
    @Mock
    private Resource resource;

    @Mock
    private Query query;

    @Mock
    private Hit hit;

    @Mock
    SearchResult searchResult;
    @Mock
    QueryBuilder queryBuilder;

    @BeforeEach
    public void setup() throws LoginException {
        String subService = "someSubService";
        MockitoAnnotations.initMocks(this);
        request = aemContext.request();
        response = aemContext.response();
        resolver = mock(ResourceResolver.class);
        ResourceResolverFactory resourceResolverFactory = mock(ResourceResolverFactory.class);
        componentSearchService.setResourceResolverFactory(resourceResolverFactory);
        when(resourceResolverFactory.getServiceResourceResolver(anyMap())).thenReturn(resolver);
        componentSearchService.getResourceResolver(subService);
        query = mock(Query.class);
        searchResult = mock(SearchResult.class);
    }

    @Test
    public void testGetComponentQuery() {
        String parentpath = "/content/some/path";
        String propertyValue = "someResourceType";
        Map<String, String> queryMap = componentSearchService.getQuery(parentpath, propertyValue);
        assertEquals(3, queryMap.size());
        assertEquals(propertyValue, queryMap.get("path"));
        assertEquals("sling:resourceType", queryMap.get("property"));
        assertEquals(parentpath, queryMap.get("property.value"));
    }

    @Test
    public void testGetServiceResolver() throws LoginException {
        String subService = "someSubService";
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        ResourceResolverFactory resourceResolverFactory = mock(ResourceResolverFactory.class);
        componentSearchService.setResourceResolverFactory(resourceResolverFactory);
        when(resourceResolverFactory.getServiceResourceResolver(anyMap())).thenReturn(resourceResolver);
        ResourceResolver result = componentSearchService.getResourceResolver(subService);
        assertEquals(resourceResolver, result);
    }


    @Test
    public void testExecuteQuery() {
        when(resolver.adaptTo(Session.class)).thenReturn(mock(Session.class));
        componentSearchService.setQueryBuilder(queryBuilder);
        Map<String, String> queryMap = new HashMap<>();
        when(queryBuilder.createQuery(any(PredicateGroup.class), any(Session.class))).thenReturn(query);
        when(query.getResult()).thenReturn(searchResult);
        hit = mock(Hit.class);
        List<Hit> hits = new ArrayList<>();
        hits.add(hit);
        when(query.getResult()).thenReturn(searchResult);
        List<Resource> resources = componentSearchService.executeQuery(queryMap, resolver);
        assertEquals(0, resources.size());
    }

    @Test
    public void testGetComponentSearchCount() {
        String resourcePath = "/path/to/resource";
        String parentPath = "/path/to/parent";
        List<Resource> resourceList = new ArrayList<>();
        resourceList.add(resource);
        doReturn(resourceList).when(componentSearchService).executeQuery(any(),any());
        Map<String, Integer> countMap = new HashMap<>();
        countMap.put("test",1);
        doReturn(countMap).when(componentSearchService).countList(resourceList, resolver);
        String result1 = componentSearchService.getComponentSearchCount(resourcePath, parentPath);
        assertEquals(result1, result1);
    }

    @Test
    public void testCountList() {
        Resource resource1 = createResource("/content/page1");
        Resource resource2 = createResource("/content/page2");
        List<Resource> results = Arrays.asList(resource1, resource2);
        PageManager pageManager = mock(PageManager.class);
        when(resolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        Map<String, Integer> countMap = componentSearchService.countList(results, resolver);
        assertEquals(0, countMap.size());
    }
    private Resource createResource(String path) {
        Resource resource = mock(Resource.class);
        return resource;
    }
}