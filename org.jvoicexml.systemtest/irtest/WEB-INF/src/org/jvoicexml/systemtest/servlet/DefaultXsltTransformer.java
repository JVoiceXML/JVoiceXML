package org.jvoicexml.systemtest.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

/**
 * 负责使用指定的xslt文件转换，指定尾缀的xml文件。 xml文件尾缀和xslt的关系在web.xml中配置。
 * 
 * @author lancer
 */
public class DefaultXsltTransformer extends HttpServlet {

    final static private Logger LOGGER = Logger.getLogger(DefaultXsltTransformer.class);

    final static private boolean debug = false;

    final static private String TXML_SUFFIX = "txml";
    final static private String VXML_SUFFIX = "vxml";
    final static private String IRCGI_SUFFIX = "ircgi";
    final static private String JSP_SUFFIX = "jsp";
    /**
	 * 
	 */
    private static final long serialVersionUID = 2961564659647125289L;

    public DefaultXsltTransformer() {
        super();
        LOGGER.debug("DefaultXsltTransformer be initialed **********************");
    }

    @Override
    public void service(ServletRequest arg0, ServletResponse arg1) throws ServletException, IOException {

        HttpServletRequest req = (HttpServletRequest) arg0;
        HttpServletResponse resp = (HttpServletResponse) arg1;
        if (debug) {
            LOGGER.debug("getContextPath " + req.getContextPath());
            LOGGER.debug("getPathInfo " + req.getPathInfo());
            LOGGER.debug("getPathTranslated " + req.getPathTranslated());
            LOGGER.debug("getRequestURI " + req.getRequestURI());
            LOGGER.debug("getRequestURL " + req.getRequestURL());
            LOGGER.debug("getServletPath " + req.getServletPath());
        }
        String reqURI = req.getRequestURI();
        LOGGER.debug("do service : " + reqURI);

        if (reqURI.endsWith("." + TXML_SUFFIX)) {
            txmlService(req, resp, req.getServletPath());
        } else if (reqURI.endsWith("." + VXML_SUFFIX)) {
            String path = req.getServletPath().replace(VXML_SUFFIX, TXML_SUFFIX);
            txmlService(req, resp, path);
        } else if (reqURI.endsWith("." + IRCGI_SUFFIX)) {
            ircgiService(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    private void ircgiService(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getSession().getServletContext();
        String rootPath = context.getRealPath("/");
        String relativelyPath = req.getServletPath();
        try {
            File source = toFile(rootPath, relativelyPath);

            File xsltFile = toFile(rootPath, getInitParameter(IRCGI_SUFFIX));

            String jspPath = relativelyPath.replace(IRCGI_SUFFIX, JSP_SUFFIX);

            File jspFile = toFile(rootPath, jspPath);

            if (jspFile.exists()) {
                jspFile.delete();
            }

            transfer(jspFile, source, xsltFile);
            forward(req, resp, jspPath);
        } catch (Exception e) {
            LOGGER.error("Can not finish transformer.", e);
            super.service(req, resp);
        }

    }

    private void txmlService(HttpServletRequest req, HttpServletResponse resp, String relativelyPath)
            throws ServletException, IOException {
        ServletContext context = req.getSession().getServletContext();
        String rootPath = context.getRealPath("/");

        String xslt = getInitParameter(TXML_SUFFIX);
        try {

            File dataFile = toFile(rootPath, relativelyPath);
            File xsltFile = toFile(rootPath, xslt);

            resp.setContentType("text/xml");
            OutputStream outStream = resp.getOutputStream();
            InputStream dataStream = new FileInputStream(dataFile);
            InputStream styleStream = new FileInputStream(xsltFile);
            transfer(outStream, dataStream, styleStream);
        } catch (Exception e) {
            LOGGER.error("Can not finish transformer.", e);
            super.service(req, resp);
        }

    }

    private File toFile(String root, String relativelyPath) throws IOException {
        LOGGER.debug("path = " + relativelyPath);

        if (relativelyPath == null) {
            throw new IOException("file name is null.");
        }

        File file = new File(root, relativelyPath);

        return file;
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp, String relativelyURI)
            throws ServletException, IOException {

        ServletContext context = req.getSession().getServletContext();
        RequestDispatcher rd = context.getRequestDispatcher("/" + relativelyURI);
        rd.forward(req, resp);
    }

    private void transfer(File outFile, File dataFile, File styleFile) throws IOException, TransformerException {
        OutputStream outStream = new FileOutputStream(outFile);
        InputStream dataStream = new FileInputStream(dataFile);
        InputStream styleStream = new FileInputStream(styleFile);
        transfer(outStream, dataStream, styleStream);
    }

    private void transfer(OutputStream out, InputStream dataStream, InputStream styleStream) throws IOException,
            TransformerException {

        Source data = new StreamSource(dataStream);
        Source style = new StreamSource(styleStream);
        Result output = new StreamResult(out);

        Transformer xslt = TransformerFactory.newInstance().newTransformer(style);
        xslt.transform(data, output);
    }
}