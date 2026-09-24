package com.example.projetOO.rpc;

import com.example.projetOO.RpcServiceServerApplication;
import com.example.projetOO.exeptions.GlobalExceptionHandler;
import com.example.projetOO.service.GrpcRestService;
import com.example.projetOO.web.ListController;
import com.example.projetOO.web.UserController;
import com.example.projetOO.web.WorkController;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = RpcServiceServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class SeriesTrackerGrpcServiceIntegrationTest {
    private ManagedChannel channel;
    private SeriesTrackerServiceGrpc.SeriesTrackerServiceBlockingStub client;

    @BeforeEach
    void connect() {
        channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();
        client = SeriesTrackerServiceGrpc.newBlockingStub(channel);
    }

    @AfterEach
    void disconnect() {
        if (channel != null) channel.shutdownNow();
    }

    @Test
    void restApiUsesGrpcForWorkUserAndEntryOperations() throws Exception {
        assertTrue(client.listWorks(Empty.getDefaultInstance()).getItemsList().stream()
                .anyMatch(item -> item.getTitle().equals("Solo Leveling")));

        User user = client.createUser(User.newBuilder().setUsername("rpc-test").setEmail("rpc-test@example.com").build());
        assertNotEquals(0, user.getId());
        assertEquals(user.getId(), client.getUser(IdRequest.newBuilder().setId(user.getId()).build()).getId());

        Work work = client.createWork(Work.newBuilder().setTitle("RPC Test Series")
                .setType("MANGA").setAuthor("Test Author").setDescription("gRPC integration test")
                .setStatus("ONGOING").build());
        assertNotEquals(0, work.getId());
        assertEquals(work.getId(), client.getWork(IdRequest.newBuilder().setId(work.getId()).build()).getId());
        assertTrue(client.listWorks(Empty.getDefaultInstance()).getItemsList().stream()
                .anyMatch(item -> item.getId() == work.getId()));

        GrpcRestService restService = new GrpcRestService(client);
        MockMvc restApi = MockMvcBuilders.standaloneSetup(new WorkController(restService),
                        new UserController(restService), new ListController(restService))
                .setControllerAdvice(new GlobalExceptionHandler()).build();
        restApi.perform(get("/works/{id}", work.getId())).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(work.getId()));
        restApi.perform(get("/users/" + user.getId())).andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("rpc-test@example.com"));

        restApi.perform(post("/users/{userId}/entries", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"workId\":%d,\"status\":\"ONGOING\",\"currentChapter\":12,\"rating\":4}".formatted(work.getId())))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.currentChapter").value(12))
                .andExpect(jsonPath("$.rating").value(4));
        long entryId = client.listEntries(UserEntriesRequest.newBuilder().setUserId(user.getId()).build())
                .getItems(0).getId();
        assertNotEquals(0, entryId);

        restApi.perform(put("/entries/{entryId}", entryId).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"COMPLETED\",\"currentChapter\":100,\"rating\":5}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.currentChapter").value(100)).andExpect(jsonPath("$.rating").value(5));
        restApi.perform(get("/users/{userId}/entries", user.getId())).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(entryId));

        restApi.perform(delete("/entries/{entryId}", entryId)).andExpect(status().isNoContent());
        assertFalse(client.listEntries(UserEntriesRequest.newBuilder().setUserId(user.getId()).build()).getItemsList().stream()
                .anyMatch(item -> item.getId() == entryId));
        client.deleteWork(IdRequest.newBuilder().setId(work.getId()).build());
        client.deleteUser(IdRequest.newBuilder().setId(user.getId()).build());
    }
}
