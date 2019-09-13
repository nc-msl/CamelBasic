package com.skills421.examples.camel.basics;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class FTPCopier {
    public String getFtpPath(String type, String host, String remotePath, String username, String password) {
        StringBuilder ftpBuilder = new StringBuilder(type);
        ftpBuilder.append("://")
                .append(username)
                .append(":")
                .append(password)
                .append("@")
                .append(host)
                .append("/")
                .append(remotePath);

        return ftpBuilder.toString();
    }

    public String getFtpPath(String type, String host, String port, String remotePath, String username, String password) {
        StringBuilder ftpBuilder = new StringBuilder(type);
        ftpBuilder.append("://")
                .append(username)
                .append(":")
                .append(password)
                .append("@")
                .append(host)
                .append(":")
                .append(port)
                .append("/")
                .append(remotePath);

        return ftpBuilder.toString();
    }

    public void sftpDownload(String url, String remotePath, final String localPath, String username, String password) throws Exception {
        CamelContext context = new DefaultCamelContext();

        String ftpPath = this.getFtpPath("sftp", url, remotePath, username, password);

        StringBuilder sb = new StringBuilder(ftpPath);
        sb.append("?recursive=true&fileName=ConsoleClient.png");
        ftpPath = sb.toString();

        final String finalFtpPath = ftpPath;
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                System.out.println("from("+ finalFtpPath + ").to(file:" + localPath + ")");
                from(finalFtpPath).to("file:" + localPath);
            }
        });

        context.start();
        Thread.sleep(10000);
        context.stop();
    }

    void sftpDownload(String host, String port, String remotePath, final String localPath, String username, String password) throws Exception {
        CamelContext context = new DefaultCamelContext();

        String baseFtpPath = this.getFtpPath("sftp", host, port, remotePath, username, password);
        StringBuilder sb = new StringBuilder(baseFtpPath);
        sb.append("?recursive=true&fileName=test.txt");
        baseFtpPath = sb.toString();
        final String finalFtpPath = baseFtpPath;

        context.addRoutes(new RouteBuilder() {
            public void configure() {
                System.out.println("from("+ finalFtpPath + ").to(file:" + localPath + ")");
                from(finalFtpPath).to("file:" + localPath);
            }
        });

        context.start();
        Thread.sleep(10000);
        context.stop();
    }

    void sftpUpload(String host, String port, final String remotePath, final String localPath, String username, String password) throws Exception {
        CamelContext context = new DefaultCamelContext();

        final String baseFtpPath = this.getFtpPath("sftp", host, port, remotePath, username, password);
        final String baseLocalPath = this.getLocalPath(localPath);


        context.addRoutes(new RouteBuilder() {
            public void configure() {
                System.out.println("from(" + baseLocalPath + ").to(" + baseFtpPath + ")");
                from(baseLocalPath).to(baseFtpPath);
            }
        });

        context.start();
        Thread.sleep(10000);
        context.stop();
    }

    private String getLocalPath(String localPath) {
        return "file:" + localPath + "?recursive=true&include=.*.txt&noop=true";
    }

}