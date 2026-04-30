package edu.generalpuzzle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.grpc.client.ImportGrpcClients;
//import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(
//		properties = { "spring.grpc.server.port=0",
//				"spring.grpc.client.channel.default.target=0.0.0.0:${local.grpc.sever.port}" },
//		useMainMethod = SpringBootTest.UseMainMethod.ALWAYS
)
//@DirtiesContext
//		<!--dependency>
//			<groupId>org.springframework.boot</groupId>
//			<artifactId>spring-boot-starter-grpc-client-test</artifactId>
//			<scope>test</scope>
//		</dependency-->
//@AutoConfigureTestGrpcTransport // from spring boot 4.1 which is not released yet. Milestone 4
@ImportGrpcClients
class GeneralpuzzleApplicationTests {

	private static final Log log = LogFactory.getLog(GeneralpuzzleApplicationTests.class);

	public static void main(String[] args) {
		new SpringApplicationBuilder(GeneralpuzzleApplicationTests.class).run();
	}

	@Autowired
	private edu.generalpuzzle.proto.SolutionServiceGrpc.SolutionServiceBlockingStub stub;

	@Test
	void contextLoads() {
	}

	@Disabled
	void serverResponds() {
		log.info("Testing");
		edu.generalpuzzle.proto.Solution response = stub.solve(edu.generalpuzzle.proto.IdRequest.newBuilder().setId("12_5").build());
		assertEquals(1, response.getRes());
	}

}
