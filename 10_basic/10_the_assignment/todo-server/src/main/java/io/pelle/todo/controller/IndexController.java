package io.pelle.todo.controller;

import javax.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class IndexController {

  @RequestMapping("/")
  public RedirectView index(HttpServletRequest servletRequest) {

    String url = "";

    String host = servletRequest.getHeader("Host");
    String scheme = servletRequest.getHeader("X-Forwarded-Proto");

    if (!StringUtils.isEmpty(host) && !StringUtils.isEmpty(scheme)) {
      url = String.format("%s://%s/%s/", scheme, host, servletRequest.getServletPath());
    }

    return new RedirectView(url + "index.html", true);
  }
}
