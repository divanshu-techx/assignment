package com.training.aem.core.servlets;

import com.training.aem.core.service.ComponentSearchService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;

import javax.servlet.Servlet;
import java.io.IOException;

@Component(service = { Servlet.class })
@SlingServletResourceTypes(
        resourceTypes="training-project/components/button",
        methods= HttpConstants.METHOD_GET,
        selectors = "compSearch",
        extensions="json")
@ServiceDescription("Component used on pages")
public class ComponentSearchServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Reference
    private transient ComponentSearchService componentSearchService;

    @Override
    protected void doGet(final SlingHttpServletRequest req,
                         final SlingHttpServletResponse resp) throws IOException {
        final String resourcePath = req.getParameter("resourcePath");
        final String parentPath = req.getParameter("parentPath");
        validate(resourcePath,parentPath);
        resp.getWriter().write(componentSearchService.getComponentSearchCount(resourcePath,parentPath));
    }

    /**
     * Method to validate the params are valid or not
     *
     * @param resourcePath {@link String} path of the resource to search
     * @param parentPath {@link String} path under which component search needs to be performed
     * throws {@link IllegalArgumentException} if the params are empty or null
     */
    protected void validate(final String resourcePath, final String parentPath) {
        if (StringUtils.isAllBlank(resourcePath,parentPath))
            throw new IllegalArgumentException("PLease provide valid arguments");
    }


}
