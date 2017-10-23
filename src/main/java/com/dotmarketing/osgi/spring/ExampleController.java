package com.dotmarketing.osgi.spring;


import com.dotmarketing.util.VelocityUtil;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.StringReader;
import java.io.StringWriter;


@EnableWebMvc
@Configuration
@RequestMapping("/content")
@Controller
public class ExampleController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public String helloWorld() throws ParseException {
        return "Example - 6";
    }

    @RequestMapping(value = "/velocity")
    @ResponseBody
    public String executeInDotCMSContext(HttpServletRequest request, HttpServletResponse response) throws ParseException {
        RuntimeServices rs = RuntimeSingleton.getRuntimeServices();
        StringReader sr = new StringReader("$text.get(\"com.dotcms.repackage.javax.portlet.title.site-browser\")");
        SimpleNode sn = rs.parse(sr, "Variable");

        Template t = new Template();
        t.setData(sn);
        t.initDocument();

        VelocityContext vc = (VelocityContext) VelocityUtil.getWebContext(request, response).getVelocityContext();
        StringWriter sw = new StringWriter();
        t.merge(vc, sw);

        return sw.toString();
    }



}