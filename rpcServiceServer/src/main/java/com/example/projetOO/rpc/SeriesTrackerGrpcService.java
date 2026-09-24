package com.example.projetOO.rpc;

import com.example.projetOO.entities.Entry;
import com.example.projetOO.entities.Status;
import com.example.projetOO.entities.User;
import com.example.projetOO.entities.Work;
import com.example.projetOO.entities.WorkType;
import com.example.projetOO.exeptions.RessourceNotFoundExeption;
import com.example.projetOO.service.ListService;
import com.example.projetOO.service.UserService;
import com.example.projetOO.service.WorkService;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;
import java.util.function.Supplier;

@Service
public class SeriesTrackerGrpcService extends SeriesTrackerServiceGrpc.SeriesTrackerServiceImplBase {
    private final WorkService works;
    private final UserService users;
    private final ListService entries;

    public SeriesTrackerGrpcService(WorkService works, UserService users, ListService entries) {
        this.works = works; this.users = users; this.entries = entries;
    }

    @Override public void listWorks(Empty request, StreamObserver<WorkList> out) {
        call(out, () -> WorkList.newBuilder().addAllItems(works.getAllWork().stream().map(this::toRpc).toList()).build());
    }
    @Override public void getWork(IdRequest request, StreamObserver<com.example.projetOO.rpc.Work> out) {
        call(out, () -> toRpc(works.getWork(request.getId())));
    }
    @Override public void createWork(com.example.projetOO.rpc.Work request, StreamObserver<com.example.projetOO.rpc.Work> out) {
        call(out, () -> toRpc(works.createWork(fromRpc(request))));
    }
    @Override public void updateWork(com.example.projetOO.rpc.Work request, StreamObserver<com.example.projetOO.rpc.Work> out) {
        call(out, () -> toRpc(works.updateWork(request.getId(), fromRpc(request))));
    }
    @Override public void deleteWork(IdRequest request, StreamObserver<Empty> out) {
        call(out, () -> { works.deleteWork(request.getId()); return Empty.getDefaultInstance(); });
    }

    @Override public void listUsers(Empty request, StreamObserver<UserList> out) {
        call(out, () -> UserList.newBuilder().addAllItems(users.getAllUsers().stream().map(this::toRpc).toList()).build());
    }
    @Override public void getUser(IdRequest request, StreamObserver<com.example.projetOO.rpc.User> out) {
        call(out, () -> toRpc(users.getUser(request.getId())));
    }
    @Override public void createUser(com.example.projetOO.rpc.User request, StreamObserver<com.example.projetOO.rpc.User> out) {
        call(out, () -> toRpc(users.createUser(new User(request.getUsername(), request.getEmail()))));
    }
    @Override public void updateUser(com.example.projetOO.rpc.User request, StreamObserver<com.example.projetOO.rpc.User> out) {
        call(out, () -> toRpc(users.updateUser(request.getId(), new User(request.getUsername(), request.getEmail()))));
    }
    @Override public void deleteUser(IdRequest request, StreamObserver<Empty> out) {
        call(out, () -> { users.deleteUser(request.getId()); return Empty.getDefaultInstance(); });
    }

    @Override public void listEntries(UserEntriesRequest request, StreamObserver<EntryList> out) {
        call(out, () -> EntryList.newBuilder().addAllItems(entries.getEntriesForUser(request.getUserId()).stream().map(this::toRpc).toList()).build());
    }
    @Override public void getEntry(IdRequest request, StreamObserver<com.example.projetOO.rpc.Entry> out) {
        call(out, () -> toRpc(entries.getEntry(request.getId())));
    }
    @Override public void saveEntry(com.example.projetOO.rpc.Entry request, StreamObserver<com.example.projetOO.rpc.Entry> out) {
        call(out, () -> {
            Status status = parse(Status.class, request.getStatus(), "status");
            if (request.getCurrentChapter() < 0) invalid("currentChapter cannot be negative");
            if (request.getRating() < 0 || request.getRating() > 5) invalid("rating must be between 0 and 5");
            Entry entry = request.getId() == 0 ? entries.addWorkToList(request.getUserId(), request.getWorkId(), status)
                    : entries.updateStatus(request.getId(), status);
            entry = entries.updateProgress(entry.getId(), request.getCurrentChapter());
            entry = entries.updateRating(entry.getId(), request.getRating());
            return toRpc(entry);
        });
    }
    @Override public void deleteEntry(IdRequest request, StreamObserver<Empty> out) {
        call(out, () -> { entries.deleteEntry(request.getId()); return Empty.getDefaultInstance(); });
    }

    private com.example.projetOO.rpc.Work toRpc(Work w) {
        return com.example.projetOO.rpc.Work.newBuilder().setId(w.getId()).setTitle(value(w.getTitle()))
                .setType(w.getType().name()).setAuthor(value(w.getAuthor())).setDescription(value(w.getDescription()))
                .setStatus(w.getStatus().name()).build();
    }
    private Work fromRpc(com.example.projetOO.rpc.Work w) {
        return new Work(w.getTitle(), parse(WorkType.class, w.getType(), "type"), w.getAuthor(), w.getDescription(), parse(Status.class, w.getStatus(), "status"));
    }
    private com.example.projetOO.rpc.User toRpc(User u) {
        return com.example.projetOO.rpc.User.newBuilder().setId(u.getId()).setUsername(value(u.getUsername())).setEmail(value(u.getEmail())).build();
    }
    private com.example.projetOO.rpc.Entry toRpc(Entry e) {
        return com.example.projetOO.rpc.Entry.newBuilder().setId(e.getId()).setUserId(e.getUser().getId())
                .setWorkId(e.getWork().getId()).setStatus(e.getStatus().name())
                .setCurrentChapter(e.getCurrentChapter()).setRating(e.getRating()).build();
    }
    private String value(String text) { return text == null ? "" : text; }
    private <E extends Enum<E>> E parse(Class<E> type, String value, String field) {
        try { return Enum.valueOf(type, value); }
        catch (IllegalArgumentException exception) { throw io.grpc.Status.INVALID_ARGUMENT.withDescription("Invalid " + field + ": " + value).asRuntimeException(); }
    }
    private void invalid(String message) { throw io.grpc.Status.INVALID_ARGUMENT.withDescription(message).asRuntimeException(); }
    private <T> void call(StreamObserver<T> out, Supplier<T> action) {
        try { out.onNext(action.get()); out.onCompleted(); }
        catch (StatusRuntimeException exception) { out.onError(exception); }
        catch (RessourceNotFoundExeption exception) { out.onError(io.grpc.Status.NOT_FOUND.withDescription(exception.getMessage()).asRuntimeException()); }
        catch (IllegalArgumentException exception) { out.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription(exception.getMessage()).asRuntimeException()); }
        catch (Exception exception) { out.onError(io.grpc.Status.INTERNAL.withDescription("Request failed").withCause(exception).asRuntimeException()); }
    }
}
