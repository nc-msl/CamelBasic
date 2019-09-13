package com.skills421.examples.camel.basics;

import org.apache.commons.lang3.StringUtils;
import org.apache.sshd.SshServer;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.Session;
import org.apache.sshd.common.file.FileSystemView;
import org.apache.sshd.common.file.nativefs.NativeFileSystemFactory;
import org.apache.sshd.common.file.nativefs.NativeFileSystemView;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.sftp.SftpSubsystem;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.fail;

public class FTPCopierTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    private static final String USERNAME = "demo";
    private static final String PASSWORD = "password";
    private SshServer sshd;

    @Before
    public void prepare() throws Exception {
        setupSSHServer();
    }

    @Test
    public void downloadSFTPDir()
    {
        FTPCopier copier = new FTPCopier();

        String url = "test.rebex.net";
        String remotePath = "pub/example";
        String localPath = "/Users/msl/Desktop";
        String username = "demo";
        String password = "password";

        try
        {
            copier.sftpDownload(url, remotePath, localPath, username, password);
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void downloadSFTPDirFromMock() throws Exception {
        FTPCopier copier = new FTPCopier();

        String host = "localhost";
        String port = "8001";
        String remotePath = "";
        String localPath = "/Users/msl/Desktop";
        String username = "demo";
        String password = "password";

        copier.sftpDownload(host, port, remotePath, localPath, username, password);
    }

    @Test
    public void uploadAndDownloadSFTPDirFromMock() throws Exception {
        FTPCopier copier = new FTPCopier();

        String host = "localhost";
        String port = "21000";
        String remotePath = "";
        String remotePathDownload = "";
        String localPathUpload = "/Users/msl/Desktop";
        String localPathDownload = "/Users/msl/sftpFolder";

        System.out.println("Starting upload");
        copier.sftpUpload(host, port, remotePath, localPathUpload, USERNAME, PASSWORD);
        System.out.println("Upload finished, Starting download");
        copier.sftpDownload(host, port, remotePathDownload, localPathDownload, USERNAME, PASSWORD);
        System.out.println("Download finished");
    }

    private void setupSSHServer() throws IOException {
        sshd = SshServer.setUpDefaultServer();
        sshd.setFileSystemFactory(new NativeFileSystemFactory() {
            @Override
            public FileSystemView createFileSystemView(final Session session) {
                return new NativeFileSystemView(session.getUsername(), false) {
                    @Override
                    public String getVirtualUserDir() {
                        return testFolder.getRoot().getAbsolutePath();
                    }
                };
            };
        });
        sshd.setHost("localhost");
        sshd.setPort(21000);
        sshd.setSubsystemFactories(Arrays.<NamedFactory<Command>>asList(new SftpSubsystem.Factory()));
        sshd.setCommandFactory(new ScpCommandFactory());
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(testFolder.newFile("hostkey.ser").getAbsolutePath()));
        sshd.setPasswordAuthenticator(new PasswordAuthenticator() {
            public boolean authenticate(final String username, final String password, final ServerSession session) {
                return StringUtils.equals(username, USERNAME) && StringUtils.equals(password, PASSWORD);
            }
        });
        sshd.start();
    }
}