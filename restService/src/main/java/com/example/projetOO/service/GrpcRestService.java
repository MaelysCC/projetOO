package com.example.projetOO.service;

import com.example.projetOO.entities.User;
import com.example.projetOO.entities.Work;
import com.example.projetOO.rpc.Empty;
import com.example.projetOO.rpc.IdRequest;
import com.example.projetOO.rpc.SeriesTrackerServiceGrpc;
import com.example.projetOO.rpc.UserEntriesRequest;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GrpcRestService {
    private static final Logger logger = LoggerFactory.getLogger(GrpcRestService.class);
    private final SeriesTrackerServiceGrpc.SeriesTrackerServiceBlockingStub rpc;

    public GrpcRestService(SeriesTrackerServiceGrpc.SeriesTrackerServiceBlockingStub rpc) {
        this.rpc = rpc;
    }

    public List<WorkResponse> getWorks() {
        logger.info("Retrieving works through gRPC");
        return rpc.listWorks(Empty.getDefaultInstance()).getItemsList().stream().map(this::workResponse).toList();
    }
    public WorkResponse getWork(long id) {
        logger.info("Retrieving work {} through gRPC", id);
        return workResponse(rpc.getWork(IdRequest.newBuilder().setId(id).build()));
    }
    public WorkResponse createWork(Work work) {
        return workResponse(rpc.createWork(workMessage(work)));
    }
    public WorkResponse updateWork(long id, Work work) {
        return workResponse(rpc.updateWork(workMessage(work).toBuilder().setId(id).build()));
    }
    public void deleteWork(long id) {
        rpc.deleteWork(IdRequest.newBuilder().setId(id).build());
    }

    public List<UserResponse> getUsers() {
        logger.info("Retrieving users through gRPC");
        return rpc.listUsers(Empty.getDefaultInstance()).getItemsList().stream().map(this::userResponse).toList();
    }
    public UserResponse getUser(long id) {
        return userResponse(rpc.getUser(IdRequest.newBuilder().setId(id).build()));
    }
    public UserResponse createUser(User user) {
        return userResponse(rpc.createUser(com.example.projetOO.rpc.User.newBuilder()
                .setUsername(value(user.getUsername())).setEmail(value(user.getEmail())).build()));
    }
    public UserResponse updateUser(long id, User user) {
        return userResponse(rpc.updateUser(com.example.projetOO.rpc.User.newBuilder().setId(id)
                .setUsername(value(user.getUsername())).setEmail(value(user.getEmail())).build()));
    }
    public void deleteUser(long id) {
        rpc.deleteUser(IdRequest.newBuilder().setId(id).build());
    }

    public List<EntryResponse> getEntries(long userId) {
        return rpc.listEntries(UserEntriesRequest.newBuilder().setUserId(userId).build())
                .getItemsList().stream().map(this::entryResponse).toList();
    }
    public EntryResponse getEntry(long id) {
        return entryResponse(rpc.getEntry(IdRequest.newBuilder().setId(id).build()));
    }
    public EntryResponse createEntry(long userId, EntryRequest request) {
        if (request.workId() == null) {
            throw Status.INVALID_ARGUMENT.withDescription("workId is required").asRuntimeException();
        }
        return entryResponse(rpc.saveEntry(entryMessage(userId, request).build()));
    }
    public EntryResponse updateEntry(long id, EntryRequest request) {
        var old = rpc.getEntry(IdRequest.newBuilder().setId(id).build());
        var update = old.toBuilder();
        if (request.status() != null) update.setStatus(request.status());
        if (request.currentChapter() != null) update.setCurrentChapter(request.currentChapter());
        if (request.rating() != null) update.setRating(request.rating());
        return entryResponse(rpc.saveEntry(update.build()));
    }
    public EntryResponse updateProgress(long id, int chapter) {
        var old = rpc.getEntry(IdRequest.newBuilder().setId(id).build());
        return entryResponse(rpc.saveEntry(old.toBuilder().setCurrentChapter(chapter).build()));
    }
    public EntryResponse updateRating(long id, int rating) {
        var old = rpc.getEntry(IdRequest.newBuilder().setId(id).build());
        return entryResponse(rpc.saveEntry(old.toBuilder().setRating(rating).build()));
    }
    public EntryResponse updateStatus(long id, String status) {
        var old = rpc.getEntry(IdRequest.newBuilder().setId(id).build());
        return entryResponse(rpc.saveEntry(old.toBuilder().setStatus(status).build()));
    }
    public void deleteEntry(long id) {
        rpc.deleteEntry(IdRequest.newBuilder().setId(id).build());
    }

    private com.example.projetOO.rpc.Work workMessage(Work work) {
        return com.example.projetOO.rpc.Work.newBuilder().setTitle(value(work.getTitle()))
                .setType(work.getType() == null ? "" : work.getType().name())
                .setAuthor(value(work.getAuthor())).setDescription(value(work.getDescription()))
                .setStatus(work.getStatus() == null ? "" : work.getStatus().name()).build();
    }
    private WorkResponse workResponse(com.example.projetOO.rpc.Work work) {
        return new WorkResponse(work.getId(), work.getTitle(), work.getType(), work.getAuthor(), work.getDescription(), work.getStatus());
    }
    private UserResponse userResponse(com.example.projetOO.rpc.User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail());
    }
    private EntryResponse entryResponse(com.example.projetOO.rpc.Entry entry) {
        return new EntryResponse(entry.getId(), entry.getUserId(), entry.getWorkId(), entry.getStatus(), entry.getCurrentChapter(), entry.getRating());
    }
    private com.example.projetOO.rpc.Entry.Builder entryMessage(long userId, EntryRequest entry) {
        return com.example.projetOO.rpc.Entry.newBuilder().setUserId(userId).setWorkId(entry.workId())
                .setStatus(entry.status() == null ? "PLANNED" : entry.status())
                .setCurrentChapter(entry.currentChapter() == null ? 0 : entry.currentChapter())
                .setRating(entry.rating() == null ? 0 : entry.rating());
    }
    private String value(String value) { return value == null ? "" : value; }

    public record WorkResponse(long id, String title, String type, String author, String description, String status) {}
    public record UserResponse(long id, String username, String email) {}
    public record EntryResponse(long id, long userId, long workId, String status, int currentChapter, int rating) {}
    public record EntryRequest(Long workId, String status, Integer currentChapter, Integer rating) {}
}
