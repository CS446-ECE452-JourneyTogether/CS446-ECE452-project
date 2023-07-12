package ca.uwaterloo.cs446.journeytogether.common;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirestoreCollection<T> {

    private final DocumentUpdater<T> documentUpdater;
    private final DocumentWriter<T> documentWriter;
    private final IdSetter<T> idSetter;
    private final NilValueCreator<T> nilValueCreator;
    private final String collectionPath;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private HashMap<String, T> localSnapshots;

    @FunctionalInterface
    public interface DocumentUpdater<T> {
        void update(T onto, DocumentSnapshot document);
    }

    @FunctionalInterface
    public interface DocumentWriter<T> {
        Map<String, Object> write(T value);
    }

    @FunctionalInterface
    public interface IdSetter<T> {
        void setId(T onto, String id);
    }

    @FunctionalInterface
    public interface NilValueCreator<T> {
        T create();
    }

    @FunctionalInterface
    public interface ValuesCallback<T> {
        void callback(ArrayList<T> values);
    }

    @FunctionalInterface
    public interface VoidCallback<T> {
        void callback();
    }

    @FunctionalInterface
    public interface QueryCallback<T> {
        Query build(CollectionReference cr);
    }

    public FirestoreCollection(
            String collectionPath,
            DocumentUpdater<T> documentUpdater,
            DocumentWriter<T> documentWriter,
            IdSetter<T> idSetter,
            NilValueCreator<T> nilValueCreator) {
        this.collectionPath = collectionPath;
        this.documentUpdater = documentUpdater;
        this.documentWriter = documentWriter;
        this.idSetter = idSetter;
        this.nilValueCreator = nilValueCreator;

        this.localSnapshots = new HashMap<>();
    }

    public void getValuesById(String id, ValuesCallback<T> onSuccess, VoidCallback<T> onFailure) {

        ArrayList<T> retval = new ArrayList<>();

        db.collection(collectionPath).document(id).get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        updateSnapshot(id, documentSnapshot);
                        retval.add(localSnapshots.get(id));
                        onSuccess.callback(retval);
                    } else {
                        onSuccess.callback(new ArrayList<>());
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    onFailure.callback();
                }
            });
    }

    public void syncById(String id, VoidCallback<T> onSuccess, VoidCallback<T> onFailure) {

        T value = localSnapshots.get(id);

        db.collection(collectionPath).document(id)
            .set(documentWriter.write(value), SetOptions.merge())
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    onSuccess.callback();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    onFailure.callback();
                }
            });
    }

    public void create(T value, VoidCallback<T> onSuccess, VoidCallback<T> onFailure) {
        db.collection(collectionPath)
            .add(documentWriter.write(value))
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    String id = documentReference.getId();
                    updateSnapshot(id, value);

                    onSuccess.callback();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    onFailure.callback();
                }
            });
    }

    public void makeQuery(QueryCallback<T> qc, ValuesCallback<T> onSuccess, VoidCallback<T> onFailure) {
        Query query = qc.build(db.collection(collectionPath));
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {

                ArrayList<T> retval = new ArrayList<>();

                // Process the query results
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    String id = document.getId();
                    updateSnapshot(id, document);
                    retval.add(localSnapshots.get(id));
                }

                onSuccess.callback(retval);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onFailure.callback();
            }
        });
    }

    private void updateSnapshot(String id, DocumentSnapshot document) {
        if (!localSnapshots.containsKey(id)) {
            localSnapshots.put(id, nilValueCreator.create());
        }

        // Document now exists no matter what, retrieve the data and do in-place write
        T val = localSnapshots.get(id);
        idSetter.setId(val, id);
        documentUpdater.update(val, document);
    }

    private void updateSnapshot(String id, T value) {
        localSnapshots.put(id, value);
    }

}
