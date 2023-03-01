package pt.tecnico.ttt.server;

import io.grpc.*;
import org.junit.jupiter.api.*;
import pt.tecnico.ttt.PlayRequest;
import pt.tecnico.ttt.TTTGrpc;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TTTServerIT {
    final static int PORT = 12391;

    Server server;
    ManagedChannel channel;
    TTTGrpc.TTTBlockingStub stub;

    @BeforeEach
    public void setupServer() throws IOException {
        BindableService impl = new TTTServiceImpl();
        this.server = ServerBuilder.forPort(PORT).addService(impl).build();
        this.server.start();
        System.out.println("Server started on port " + PORT);
    }

    @BeforeEach
    public void setupClient() {
        this.channel = ManagedChannelBuilder.forTarget("localhost:" + PORT).usePlaintext().build();
        this.stub = TTTGrpc.newBlockingStub(channel);
    }

    @AfterEach
    public void teardownClient() {
        this.channel.shutdownNow();
    }

    @Test
    public void testPlayOutOfBounds() {
        PlayRequest playReq = PlayRequest.newBuilder().setRow(1).setColumn(9).setPlayer(0).build();
        Exception exception = assertThrows(StatusRuntimeException.class, () -> {
            stub.play(playReq).getResult();
        });
        assertEquals("INVALID_ARGUMENT: Input has to be a valid position", exception.getMessage());
    }
}
