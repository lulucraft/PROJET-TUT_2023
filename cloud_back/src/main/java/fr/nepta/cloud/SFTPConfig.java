package fr.nepta.cloud;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

import org.apache.sshd.sftp.client.SftpClient.DirEntry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.file.FileNameGenerator;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.Session;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.outbound.SftpMessageHandler;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;

@Configuration
public class SFTPConfig {

	@Value("${sftp.host}")
	private String sftpHost;

	@Value("${sftp.port:22}")
	private int sftpPort;

	@Value("${sftp.user}")
	private String sftpUser;

	@Value("${sftp.privateKey:#{null}}")
	private Resource sftpPrivateKey;

	@Value("${sftp.privateKeyPassphrase:}")
	private String sftpPrivateKeyPassphrase;

	@Value("${sftp.password:#{null}}")
	private String sftpPasword;

	@Value("${sftp.remote.directory:/}")
	private String sftpRemoteDirectory;

	@Bean
	SessionFactory<DirEntry> sftpSessionFactory() {
		DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);
		factory.setHost(sftpHost);
		factory.setPort(sftpPort);
		factory.setUser(sftpUser);
		if (sftpPrivateKey != null) {
			factory.setPrivateKey(sftpPrivateKey);
			factory.setPrivateKeyPassphrase(sftpPrivateKeyPassphrase);
		} else {
			factory.setPassword(sftpPasword);
		}
		factory.setAllowUnknownKeys(true);
		return new CachingSessionFactory<DirEntry>(factory);
	}

	public void upload() throws IOException {
		Session<DirEntry> session = sftpSessionFactory().getSession();
		InputStream resourceAsStream = SFTPConfig.class.getClassLoader().getResourceAsStream("mytextfile.txt");
		session.write(resourceAsStream, "upload/mynewfile" + LocalDateTime.now() + ".txt");
		session.close();
	}

	@Bean
	@ServiceActivator(inputChannel = "toSftpChannel")
	MessageHandler handler() {
		SftpMessageHandler handler = new SftpMessageHandler(sftpSessionFactory());
		handler.setRemoteDirectoryExpression(new LiteralExpression(sftpRemoteDirectory));
		handler.setFileNameGenerator(new FileNameGenerator() {
			@Override
			public String generateFileName(Message<?> message) {
				if (message.getPayload() instanceof File) {
					return ((File) message.getPayload()).getName();
				} else {
					throw new IllegalArgumentException("File expected as payload.");
				}
			}
		});
		return handler;
	}

	@MessagingGateway
	public interface UploadGateway {

		@Gateway(requestChannel = "toSftpChannel")
		void upload(File file);

	}
}