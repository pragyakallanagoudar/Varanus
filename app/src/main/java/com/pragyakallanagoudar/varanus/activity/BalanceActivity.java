package com.pragyakallanagoudar.varanus.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pragyakallanagoudar.varanus.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class BalanceActivity extends AppCompatActivity implements View.OnClickListener {

    private double balance; // the current balance in the gift card
    private TextView balanceView; // the BalanceView shows the balance in text
    public static final String TAG = BalanceActivity.class.getSimpleName();
    private DocumentReference docRef; // the DocumentReference to the balance in the database

    public static final String CHANNEL_1_ID = "balance";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balance);

        balanceView = findViewById(R.id.balance_view);

        // NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // get the balance from the database
        docRef = FirebaseFirestore.getInstance().collection("Balance").document("balance");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        balance = document.getDouble("balance");
                        updateBalanceView(balance);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


        findViewById(R.id.giftcard).setOnClickListener(this);
        findViewById(R.id.purchase).setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Balance");
    }

    @Override
    public void onClick(final View view)
    {
        // Create a dialog box to enter this information, with two possible tracks:
        // add purchase and deduct from balance and add gift card to add to balance.

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final double[] amount = {0.00};

        // Set up the input
        final EditText input = new EditText(this);
        switch(view.getId())
        {
            case R.id.purchase:
                builder.setTitle("Enter the purchase amount.");
                break;
            case R.id.giftcard:
                builder.setTitle("Enter the new gift card balance.");
                break;
        }
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                double amount = 0.0;
                try {
                    amount = Double.parseDouble(input.getText().toString());
                } catch (NumberFormatException e) {}
                switch(view.getId())
                {
                    case R.id.purchase:
                        Log.e(TAG, "6/17/2020 purchase");
                        balance -= amount;
                        break;
                    case R.id.giftcard:
                        balance += amount;
                        Log.e(TAG, "6/17/2020 giftcard " + balance);
                        break;
                }
                updateBalanceView(balance);
                updateBalance(balance);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        Log.e(TAG, "bonjour 6/17/2020 #2 " + amount[0]);

        builder.show();
    }

    /**
     * Update the balance view when given the new balance.
     * @param newBalance        the new balance after an action (add purchase or gift card)
     */
    private void updateBalanceView (double newBalance)
    {
        NumberFormat formatter = new DecimalFormat("#0.00");
        balanceView.setText("$" + formatter.format(newBalance));

        /**
        if (newBalance < 5.0)
        {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentTitle("Low Balance Alert")
                    .setContentText("Your current gift card balance is " + newBalance)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        }
         */
        balance = newBalance;
    }

    /**
     * Update the balance in the database.
     * @param newBalance        the new balance after an action (add purchase or gift card)
     */
    private void updateBalance(double newBalance)
    {
        Map<String, Double> newMap = new HashMap<>();
        newMap.put("balance", newBalance);

        docRef
        .set(newMap)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully written!");
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error writing document", e);
            }
        });

        balance = newBalance;

        if (balance < 5.0)
            sendNotification();
    }

    /**
     * Send a notification when the balance is low.
     */
    private void sendNotification()
    {
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_warning)
                .setContentTitle("Low Balance Alert")
                .setContentText("The gift card balance is currently " + balance)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(1, builder.build());
    }

    /**
     * Create a notification channel to use for the low balance notification.
     */
    private void createNotificationChannel ()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Low Balance Alert",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("The gift card balance is " + balance);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
