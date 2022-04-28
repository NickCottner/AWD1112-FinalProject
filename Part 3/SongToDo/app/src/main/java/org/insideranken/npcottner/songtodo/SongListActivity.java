package org.insideranken.npcottner.songtodo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

public class SongListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    FloatingActionButton fabAdd;

    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser user;
    String onlineUserId;
    ProgressDialog loader;

    String key = "";
    String title;
    String artist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        recyclerView = findViewById(R.id.songInfo);
        fabAdd = findViewById(R.id.fabAdd);

        recyclerView = findViewById(R.id.songInfo);
        fabAdd = findViewById(R.id.fabAdd);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        loader = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        onlineUserId = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("tasks").child(onlineUserId);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToDoTask();
            }
        });

    }

    private void addToDoTask()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.add_song, null);
        dialogBuilder.setView(view);

        final AlertDialog dialogAlert = dialogBuilder.create();
        dialogAlert.setCancelable(false);

        EditText title = view.findViewById(R.id.etTitle);
        EditText artist = view.findViewById(R.id.etArtist);
        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        dialogAlert.show();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAlert.dismiss();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mTitle = title.getText().toString().trim();
                String mArtist = artist.getText().toString().trim();
                String id = reference.push().getKey();
                String date = DateFormat.getDateInstance().format(new Date());

                if (TextUtils.isEmpty(mTitle)) {
                    title.setError("Title Required");
                    return;
                }

                if (TextUtils.isEmpty(mArtist)) {
                    artist.setError("Artist Required");
                    return;
                }

                loader.setMessage("Adding Song Data");
                loader.setCanceledOnTouchOutside(true);
                loader.show();

                SongModel model = new SongModel(mTitle, mArtist, id, date);

                reference.child(id).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SongListActivity.this, "Song Insertion Successful",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            String error = task.getException().toString();
                            Toast.makeText(SongListActivity.this, "Song Insertion Failed\n" +
                                    error, Toast.LENGTH_SHORT).show();
                        }
                        loader.dismiss();
                    }
                });

                dialogAlert.dismiss();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<SongModel> options = new FirebaseRecyclerOptions.Builder<SongModel>()
                .setQuery(reference, SongModel.class)
                .build();

        FirebaseRecyclerAdapter<SongModel, MyViewHolder> adapter = new FirebaseRecyclerAdapter<SongModel, MyViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull SongModel model) {
                holder.setDate(model.getDate());
                holder.setTitle(model.getTitle());
                holder.setArtist(model.getArtist());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        key = getRef(holder.getAbsoluteAdapterPosition()).getKey();
                        title = model.getTitle();
                        artist = model.getArtist();

                        updateTask();
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
                return new MyViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String title) {
            TextView taskTextView = mView.findViewById(R.id.tvRowTitle);
            taskTextView.setText(title);
        }

        public void setArtist(String desc) {
            TextView descTextView = mView.findViewById(R.id.tvRowArtist);
            descTextView.setText(desc);
        }

        public void setDate(String date) {
            TextView dateTextView = mView.findViewById(R.id.tvRowDate);
            dateTextView.setText(date);
        }
    }

    private void updateTask() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.update_song, null);
        myDialog.setView(view);

        final AlertDialog dialog = myDialog.create();

        final EditText mTitle = view.findViewById(R.id.etUpdateTitle);
        final EditText mArtist = view.findViewById(R.id.etUpdateArtist);

        mTitle.setText(title);
        mTitle.setSelection(title.length());

        mArtist.setText(artist);
        mArtist.setSelection(artist.length());

        Button btnDelete = view.findViewById(R.id.btnDelete);
        Button btnUpdate = view.findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = mTitle.getText().toString().trim();
                artist = mArtist.getText().toString().trim();

                String date = DateFormat.getDateInstance().format(new Date());

                SongModel model = new SongModel(title, artist, key, date);

                reference.child(key).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(SongListActivity.this, "Data has been updated successfully", Toast.LENGTH_SHORT).show();
                        }else {
                            String err = task.getException().toString();
                            Toast.makeText(SongListActivity.this, "Update failed "+err, Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                dialog.dismiss();

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(SongListActivity.this, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                        }else {
                            String err = task.getException().toString();
                            Toast.makeText(SongListActivity.this, "Failed to delete task "+ err, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}