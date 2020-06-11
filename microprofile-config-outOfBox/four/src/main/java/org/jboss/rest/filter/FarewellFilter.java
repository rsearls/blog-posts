package org.jboss.rest.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FarewellFilter implements Filter {
    private String phrase = "All I hear are crickets";

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
        
        String p = fConfig.getInitParameter("farewell-phrase");
        if (p != null || !p.isEmpty()){
            this.phrase = p;
        }

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        ServletOutputStream out = response.getOutputStream();
        CharResponseWrapper responseWrapper = new CharResponseWrapper(
                (HttpServletResponse) response);

        chain.doFilter(request, (ServletResponse)responseWrapper);

        out.print(responseWrapper.toString() + "\n-- " + phrase + " --\n");
        out.close();
    }

    @Override
    public void destroy() {
        //we can close resources here
    }



    class CharResponseWrapper extends HttpServletResponseWrapper {
        private CharArrayWriter output;

        public String toString() {
            return output.toString();
        }

        public CharResponseWrapper(HttpServletResponse response) {
            super(response);
            output = new CharArrayWriter();
        }

        public PrintWriter getWriter() {
            return new PrintWriter(output);
        }
    }
}
