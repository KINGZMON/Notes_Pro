package com.kingzmon.notespro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EdgeEffect;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class AddNoteActivity extends AppCompatActivity {

    EditText titleEditText, contentEditText;
    String title, content, docId;
    boolean isEditMode = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        if(isEditMode){
            menuInflater.inflate(R.menu.edit_note_menu_options, menu);
        }
        else {
            menuInflater.inflate(R.menu.new_note_menu_options, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.save_note){
            saveNote();

            return true;
        }
        else if(item.getItemId() == R.id.delete_note){
            deleteNoteFromFirebase();
            return true;
        }

        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        titleEditText = findViewById(R.id.notes_title_text);
        contentEditText = findViewById(R.id.notes_content_text);

        //receive data
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        if(docId != null && !docId.isEmpty()){
            isEditMode = true;
        }

        titleEditText.setText(title);
        contentEditText.setText(content);
    }

    void saveNote(){
        String noteTitle = titleEditText.getText().toString();
        String noteContent = contentEditText.getText().toString();

        if(noteTitle == null || noteTitle.isEmpty()){
            Utility.showToast(this, "Make sure title is provided");
            return;
        }

        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());

        saveNoteToFirebase(note);
    }

    void saveNoteToFirebase(Note note){
        DocumentReference documentReference;
        if(isEditMode){
            //update note
            documentReference = Utility.getCollectionReference().document(docId);
            documentReference.set(note);
            Utility.showToast(AddNoteActivity.this, "Note updated");

        }
        else{
            //add note
            documentReference = Utility.getCollectionReference().document();
            documentReference.set(note);
            Utility.showToast(AddNoteActivity.this, "Note added successfully");
        }
        finish();
    }

    void deleteNoteFromFirebase(){
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReference().document(docId);

        documentReference.delete();
        Utility.showToast(AddNoteActivity.this, "Note deleted successfully");
        finish();
    }
}