package com.training.aem.core.servlets;

import com.training.aem.core.service.ComponentSearchService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
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

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComponentSearchServletTest {

    @Spy
    @InjectMocks
    private ComponentSearchServlet servlet;

    @Mock
    ComponentSearchService componentSearchService;
    @Mock
    PrintWriter writer;
@Mock
SlingHttpServletRequest request;
    @Mock
    private SlingHttpServletResponse response;



    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        writer = mock(PrintWriter.class);
        componentSearchService = mock(ComponentSearchService.class);
    }

//    @Test
//    public void testDoGetWhenParamsAreValidThenReturnComponentSearchCount() throws Exception {
//        String resourcePath = "/content/aem-training/en";
//        String parentPath = "/content/aem-training";
//        String expectedCount = "5";
//
//   //     MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(resourcePath, parentPath);
//        StringWriter stringWriter = new StringWriter();
//        PrintWriter writer = new PrintWriter(stringWriter);
//        when(response.getWriter()).thenReturn(writer);
//        when(componentSearchService.getComponentSearchCount(resourcePath, parentPath)).thenReturn(expectedCount);
//
//        servlet.doGet(request, response);
//
//        assertEquals(expectedCount, stringWriter.toString());
//    }

    @Test
    public void testDoGetWhenParamsAreEmptyThenReturnComponentSearchCount() throws Exception {
        when(request.getParameter("resourcePath")).thenReturn("/path/to/resource");
        when(request.getParameter("parentPath")).thenReturn("/path/to/parent");
        when(response.getWriter()).thenReturn(writer);
    //    doNothing().when(servlet).validate("/path/to/resource","/path/to/parent");
   //     when(componentSearchService.getComponentSearchCount("/path/to/resource", "/path/to/parent")).thenReturn("2");
        servlet.doGet(request, response);

        verify(response).getWriter();
      //  verify(writer).write("Component search count result");
    //    verify(writer).close();
    }
}