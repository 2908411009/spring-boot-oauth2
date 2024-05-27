package pers.hzf.auth2.demos.common.tools;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author houzhifang
 * @date 2024/4/25 15:36
 */
@Slf4j
public class MyRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] body;

    public MyRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        String b = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        body = b.getBytes();
    }


    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }


    @Override
    public ServletInputStream getInputStream() throws IOException {
        ByteArrayInputStream basis = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }
            @Override
            public boolean isReady() {
                return false;
            }
            @Override
            public void setReadListener(ReadListener readListener) {
            }
            @Override
            public int read() throws IOException {
                return basis.read();
            }
        };
    }
}
