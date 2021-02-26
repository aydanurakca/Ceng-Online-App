package com.example.pltermproject;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class Dialog extends AppCompatDialogFragment {

    private String postId;

    public Dialog(String postId) {
        this.postId = postId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    private DialogListener listener;
    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Attention!").setMessage("Do you want to delete the post?")
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onYesClicked(postId);
            }
        });
        return builder.create();
    }
    public interface  DialogListener{
        void onYesClicked(String postId);
    }
    @Override
    public void onAttach(Context context){

        super.onAttach(context);
        try {
            listener = (DialogListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString());
        }

    }
}
