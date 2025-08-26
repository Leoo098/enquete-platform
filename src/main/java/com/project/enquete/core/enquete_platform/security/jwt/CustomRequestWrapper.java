package com.project.enquete.core.enquete_platform.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class CustomRequestWrapper extends HttpServletRequestWrapper {

    private final Map<String, String> customHeaders;

    public CustomRequestWrapper(HttpServletRequest request, String headerName, String headerValue) {
        super(request);
        this.customHeaders = new HashMap<>();
        this.customHeaders.put(headerName, headerValue);
    }

    @Override
    public String getHeader(String name){
        if (customHeaders.containsKey(name)){
            return customHeaders.get(name);
        }
        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaderNames(){
        java.util.List<String> names = Collections.list(super.getHeaderNames());
        names.addAll(customHeaders.keySet());
        return Collections.enumeration(names);
    }
}
