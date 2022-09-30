package com.example.oncesen2.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oncesen2.Activity.ChatActivity;
import com.example.oncesen2.Models.MessageModel;
import com.example.oncesen2.R;
import com.example.oncesen2.Utils.GetDate;
import com.example.oncesen2.Utils.RandomName;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> implements AdapterView.OnItemSelectedListener {
    Activity activity;
    Context context;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    FirebaseUser user;
    String userId,otherId,text;
    Boolean multipleSelectMode = false,messageState, durum;
    List<MessageModel> messageModelList;
    List<LinearLayout> allMessagesLayouts;
    List<String> selectedMessages,anonimIdList, messageKeyList,selectedMessagesText;
    int viewTypeSent = 1;
    int viewTypeReceived = 2;
    Menu reportMenu;
    MenuItem reportMenuItem;
    Spinner spinner1;
    ArrayAdapter<CharSequence> adapter;

    public MessageAdapter(Activity activity, Context context,String otherId, List<MessageModel> messageModelList,List<String> anonimIdList,List<String> messageKeyList) {
        this.activity = activity;
        this.context = context;
        this.messageModelList = messageModelList;
        selectedMessages=new ArrayList<>();
        allMessagesLayouts=new ArrayList<>();
        selectedMessagesText=new ArrayList<>();
        this.otherId=otherId;
        this.anonimIdList = anonimIdList;
        this.messageKeyList = messageKeyList;
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();
        messageState = false;
    }

    //Layout tanımlaması yapılacak
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == viewTypeSent) {
            view = LayoutInflater.from(context).inflate(R.layout.message_sent_layout, parent, false);
            return new ViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.message_received_layout, parent, false);
            return new ViewHolder(view);
        }
    }

    //view'lara setlemeler yapılacak
    @Override
    public void onBindViewHolder(final @NonNull ViewHolder holder, final int position) {
        if (messageModelList.get(position) != null ) {
            holder.messageEditText.setText(messageModelList.get(position).getText());
        }
    }
    //Adapteri oluşturulacak listenin size'si
    @Override
    public int getItemCount() {
        return messageModelList.size();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        text = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //View'lerin tanımlanma işlemleri
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView messageEditText;
        LinearLayout messageBackground;
        ActionMode.Callback callback;

        public ViewHolder(View itemView) {
            super(itemView);
            if (messageState == true) {
                messageEditText = (TextView) itemView.findViewById(R.id.message_sent_text);
                messageBackground=(LinearLayout) itemView.findViewById(R.id.sentLinearLayout);
                allMessagesLayouts.add(messageBackground);

            } else {
                messageEditText = (TextView) itemView.findViewById(R.id.message_received_text);
                messageBackground=(LinearLayout)itemView.findViewById(R.id.receivedLinearLayout);
                allMessagesLayouts.add(messageBackground);

            }

            messageBackground.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //messagesFragment.setToolbar();
                    multipleSelectMode = true;
                    ((AppCompatActivity) context).startSupportActionMode(callback);
                    //messageFragmentToolbar.getMenu().clear();
                    //messageFragmentToolbar.inflateMenu(R.menu.multiple_delete_menu);
                    //((AppCompatActivity) activity).getSupportActionBar().hide();

                    if (selectedMessages.contains(messageKeyList.get(getAdapterPosition()))) {
                        messageBackground.setBackgroundColor(Color.TRANSPARENT);
                        selectedMessages.remove(messageKeyList.get(getAdapterPosition()));
                        if(selectedMessages.size()>1){
                            if(reportMenu !=null)
                                reportMenuItem.setVisible(false);
                        }
                        else {
                            if(reportMenu !=null)
                                reportMenuItem.setVisible(true);
                        }
                    }
                    else {
                        messageBackground.setBackgroundResource(R.color.blue);
                        selectedMessages.add(messageKeyList.get(getAdapterPosition()));
                        if(selectedMessages.size()>1){
                            if(reportMenu !=null)
                                reportMenuItem.setVisible(false);
                        }
                        else {
                            if(reportMenu !=null)
                                reportMenuItem.setVisible(true);
                        }
                    }
                    /*if (selectedItems.size() == 0) {
                        multipleSelectMode = false;
                    }*/
                    return true;
                }
            });

            messageBackground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (multipleSelectMode == true) {
                        if (selectedMessages.contains(messageKeyList.get(getAdapterPosition()))) {
                            messageBackground.setBackgroundColor(Color.TRANSPARENT);
                            selectedMessages.remove(messageKeyList.get(getAdapterPosition()));

                            if(selectedMessages.size()>1){
                                if(reportMenu !=null)
                                    reportMenuItem.setVisible(false);
                            }
                            else {
                                if(reportMenu !=null)
                                    reportMenuItem.setVisible(true);
                            }
                        } else {
                            messageBackground.setBackgroundResource(R.color.blue);
                            selectedMessages.add(messageKeyList.get(getAdapterPosition()));
                            if(selectedMessages.size()>1){
                                if(reportMenu !=null)
                                    reportMenuItem.setVisible(false);
                            }
                            else {
                                if(reportMenu !=null)
                                    reportMenuItem.setVisible(true);
                            }
                        }

                    }
                }
            });
            callback = new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    reportMenu=menu;
                    mode.getMenuInflater().inflate(R.menu.multiple_delete_menu, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    //((AppCompatActivity) context).getSupportActionBar().hide();
                    if (reportMenu != null) {
                        reportMenuItem=menu.findItem(R.id.reportIcon);

                    }
                    return true;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.multipleDelete:
                            if (selectedMessages.size() > 0) {
                                for (int i = 0; i < selectedMessages.size(); i++) {
                                    String s1 = selectedMessages.get(i);
                                    int sira = messageKeyList.indexOf(selectedMessages.get(i));
                                    databaseReference.child("Mesajlar").child(userId).child(otherId).child(anonimIdList.get(sira)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //Log.i("selectedItems1",s1);
                                                //Log.i("selectedItems2",anonimIstekList.get(sira));
                                                messageModelList.remove(sira);
                                                notifyDataSetChanged();
                                                //Log.i("istekList",model.get(model.indexOf(s1)));

                                            }
                                        }
                                    });

                                }
                                /*for (int i = 0; i < selectedItems.size(); i++) {
                                    model.remove(selectedItems.get(i));
                                    notifyDataSetChanged();
                                }*/
                                //notifyDataSetChanged();
                                Toast.makeText(activity, "Seçilen  " + selectedMessages.size() + " Mesaj Silindi", Toast.LENGTH_SHORT).show();
                            }
                            selectedMessages.clear();
                            mode.finish();
                            return true;
                        case R.id.reportIcon:
                            sikayetEt();
                            mode.finish();
                            return true;
                        default:
                            return false;
                    }

                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    //((AppCompatActivity) context).getSupportActionBar().show();
                    multipleSelectMode = false;
                    if (allMessagesLayouts.size() > 0) {
                        for (int i = 0; i < allMessagesLayouts.size(); i++) {

                            allMessagesLayouts.get(i).setBackgroundColor(Color.TRANSPARENT);
                        }
                    }
                    //((AppCompatActivity) context).getSupportActionBar().show();
                    selectedMessages.clear();

                }

            };

        }

    }
    public void sikayetEt(){
        LayoutInflater inflater=activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.report_alert_dialog,null);
        spinner1=view.findViewById(R.id.spinnerSikayet);
        EditText sikayetMesajEditText=view.findViewById(R.id.sikayetMesajEditText);
        Button gonderButton=view.findViewById(R.id.gonderButton);

        adapter = ArrayAdapter.createFromResource(context, R.array.sikayetKonu, android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        spinner1.setOnItemSelectedListener(this);

        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setCancelable(false);
        AlertDialog dialog= builder.create();

        gonderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reportSubject=spinner1.getSelectedItem().toString();
                String reportInfo=sikayetMesajEditText.getText().toString();
                String reportId=databaseReference.child("Sikayet").push().getKey();
                Map map=new HashMap();
                map.put("from",userId);
                map.put("to",otherId);
                map.put("subject",reportSubject);
                map.put("Info",reportInfo);
                map.put("time",GetDate.getDate());
                databaseReference.child("Sikayetler").child(reportId).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
                //cinsiyetEditText.setText(erkekButton.getText().toString());
                dialog.cancel();
            }
        });

        dialog.show();
    }
    @Override
    public int getItemViewType(int position) {
        //Log.i("messagemodeldeneme",messageModelList.get(position).getFrom());
        if (messageModelList.get(position) != null && messageModelList.get(position).getFrom().equals(userId)) {
            messageState = true;
            return viewTypeSent;
        } else if (messageModelList.get(position) != null && !messageModelList.get(position).getFrom().equals(userId)) {
            messageState = false;
            return viewTypeReceived;
        } else {
            return position;
        }
    }
}
