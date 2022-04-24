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
    String year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
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
                addSong();
            }
        });
    }
    private void addSong()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.add_song, null);
        dialogBuilder.setView(view);

        final AlertDialog dialogAlert = dialogBuilder.create();
        dialogAlert.setCancelable(false);

        EditText title = view.findViewById(R.id.etTitle);
        EditText artist = view.findViewById(R.id.etArtist);
        EditText year = view.findViewById(R.id.etYear);
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
                String mTitle= title.getText().toString().trim();
                String mArtist = artist.getText().toString().trim();
                String mYear = year.getText().toString().trim();
                String id = reference.push().getKey();

                if (TextUtils.isEmpty(mTitle)) {
                    title.setError("Title Required");
                    return;
                }

                if (TextUtils.isEmpty(mArtist)) {
                    artist.setError("Artist Required");
                    return;
                }
                if (TextUtils.isEmpty(mYear)) {
                    year.setError("Year Required");
                    return;
                }

                loader.setMessage("Adding Song Information");
                loader.setCanceledOnTouchOutside(false);
                loader.show();

                SongModel model = new SongModel(mTitle, mArtist, mYear, id);

                reference.child(id).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SongListActivity.this, "Song Insertion Successful",
                                    Toast.LENGTH_SHORT).show();
                            loader.dismiss();
                        } else {
                            String error = task.getException().toString();
                            Toast.makeText(SongListActivity.this, "Song Insertion Failed\n" +
                                    error, Toast.LENGTH_SHORT).show();
                            loader.dismiss();
                        }
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

        FirebaseRecyclerAdapter<SongModel, MyViewHolder> adapter =
                new FirebaseRecyclerAdapter<SongModel, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, final int position, @NonNull final SongModel model) {
                holder.setTitle(model.getTitle());
                holder.setArtist(model.getArtist());
                holder.setYear(model.getYear());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        key = getRef(holder.getAbsoluteAdapterPosition()).getKey();
                        title = model.getTitle();
                        artist = model.getArtist();
                        year = model.getYear();

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
            TextView tvTitle = mView.findViewById(R.id.tvRowTitle);
            tvTitle.setText(title);
        }

        public void setArtist(String artist) {
            TextView tvArtist = mView.findViewById(R.id.tvRowArtist);
            tvArtist.setText(artist);
        }

        public void setYear(String year) {
            TextView tvYear = mView.findViewById(R.id.tvRowYear);
            tvYear.setText(year);
        }
    }

    private void updateTask() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.update_song, null);
        myDialog.setView(view);

        final AlertDialog dialog = myDialog.create();

        final EditText mTitle= view.findViewById(R.id.etUpdateTitle);
        final EditText mArtist = view.findViewById(R.id.etUpdateArtist);
        final EditText mYear = view.findViewById(R.id.etUpdateYear);
        
        mTitle.setText(title);
        mTitle.setSelection(title.length());

        mArtist.setText(artist);
        mArtist.setSelection(artist.length());

        mYear.setText(year);
        mYear.setSelection(year.length());

        Button btnDelete = view.findViewById(R.id.btnDelete);
        Button btnUpdate = view.findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = mTitle.getText().toString().trim();
                artist = mArtist.getText().toString().trim();
                year = mYear.getText().toString().trim();

                SongModel model = new SongModel(title, artist, key, year);

                reference.child(key).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(SongListActivity.this, "Song Updated Successfully", Toast.LENGTH_SHORT).show();
                        }else {
                            String err = task.getException().toString();
                            Toast.makeText(SongListActivity.this, "Update Failed "+err, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(SongListActivity.this, "Song Successfully Deleted", Toast.LENGTH_SHORT).show();
                        }else {
                            String err = task.getException().toString();
                            Toast.makeText(SongListActivity.this, "Song Deletion Failed "+ err, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}