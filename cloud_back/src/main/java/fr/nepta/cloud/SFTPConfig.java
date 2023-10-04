package fr.nepta.cloud;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.web.multipart.MultipartFile;

@EnableIntegration
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
	@Order(0)
//	SessionFactory<DirEntry> sftpSessionFactory() {
	DefaultFtpSessionFactory sftpSessionFactory() {
		DefaultFtpSessionFactory factory = new DefaultFtpSessionFactory();
//		DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);
		factory.setHost(sftpHost);
		factory.setPort(sftpPort);
		factory.setUsername(sftpUser);
//		factory.setUser(sftpUser);
		if (sftpPrivateKey != null) {
//			factory.setPrivateKey(sftpPrivateKey);
//			factory.setPrivateKeyPassphrase(sftpPrivateKeyPassphrase);
		} else {
			factory.setPassword(sftpPasword);
		}
//		factory.setAllowUnknownKeys(true);
//		return new CachingSessionFactory<DirEntry>(factory);
		return factory;
	}

	//	public void upload() throws IOException {
	//		Session<DirEntry> session = sftpSessionFactory().getSession();
	//		InputStream resourceAsStream = SFTPConfig.class.getClassLoader().getResourceAsStream("mytextfile.txt");
	//		session.write(resourceAsStream, "upload/mynewfile" + LocalDateTime.now() + ".txt");
	//		session.close();
	//	}

//	@Bean
//	@ServiceActivator(inputChannel = "toSftpChannel")
//	MessageHandler handler() {
//		SftpMessageHandler handler = new SftpMessageHandler(sftpSessionFactory());
//		handler.setRemoteDirectoryExpression(new LiteralExpression(sftpRemoteDirectory));
//		//		handler.setFileNameGenerator(new FileNameGenerator() {
//		//			@Override
//		//			public String generateFileName(Message<?> message) {
//		//				System.err.println(message);
//		//				if (message.getPayload() instanceof File) {
//		//					return ((File) message.getPayload()).getName();
//		//				} else {
//		//					throw new IllegalArgumentException("File expected as payload.");
//		//				}
//		//			}
//		//		});
//		return handler;
//	}

//	@Bean
//	@BridgeTo
//	MessageChannel toSftpChannel() {
//		System.err.println("testtttttttttttttttttttttttttttt");
//		return new PublishSubscribeChannel();
//	}

	@ServiceActivator(inputChannel = "toUploadChannel")
	public void uploadFile(UploadFileData uploadFileData) throws IOException {
		System.err.println("Upload " + uploadFileData.fileName);

//		StringBuilder textBuilder = new StringBuilder();
		sftpSessionFactory().getSession().write(uploadFileData.mpFile.getInputStream(), uploadFileData.fileName);
//		sftpSessionFactory().getSession().readRaw(sftpRemoteDirectory + "test.txt");
//		try (Reader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
//			int c = 0;
//			while ((c = reader.read()) != -1) {
//				textBuilder.append((char) c);
//			}
//		}
//		System.err.println(textBuilder.toString());
	}
	
	@ServiceActivator(inputChannel = "toDownloadChannel")
	public InputStream downloadFile(long fileId) throws IOException {
		System.err.println(fileId);

//		OutputStream outputStream = new FileOutputStream(String.valueOf(fileId));
		return sftpSessionFactory().getSession().readRaw(String.valueOf(fileId));//(String.valueOf(fileId), outputStream);
//		sftpSessionFactory().getSession().readRaw(sftpRemoteDirectory + "test.txt");
//		try (Reader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
	}
	
	@ServiceActivator(inputChannel = "toArchiveChannel")
	public void archiveFile(long fileId) throws IOException {
		System.err.println(fileId);

//		StringBuilder textBuilder = new StringBuilder();
		sftpSessionFactory().getSession().rename(String.valueOf(fileId), fileId + ".backup");
//		sftpSessionFactory().getSession().readRaw(sftpRemoteDirectory + "test.txt");
//		try (Reader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
//			int c = 0;
//			while ((c = reader.read()) != -1) {
//				textBuilder.append((char) c);
//			}
//		}
//		System.err.println(textBuilder.toString());
	}

//	@ServiceActivator(inputChannel = "toSftpChannel")
//	@Order(1)
//	public String transferComplete(File payload) {
//		return "The SFTP transfer complete for file: " + payload;
//	}

	public static class UploadFileData {
		public MultipartFile mpFile;
//		public File file;
		public String fileName;
	}

	@MessagingGateway
	public interface UploadGateway {

//		@Gateway(requestChannel = "toSftpChannel")
//		void upload(File file);
		@Gateway(requestChannel = "toUploadChannel")
		void upload(UploadFileData uploadFileData) throws IOException;

		@Gateway(requestChannel = "toArchiveChannel")
		void archiveFile(long fileId) throws IOException;

		@Gateway(requestChannel = "toDownloadChannel")
		InputStream downloadFile(long fileId) throws IOException;

//		@Gateway(requestChannel = "toReadChannel")
//		void read(String path);

	}
}