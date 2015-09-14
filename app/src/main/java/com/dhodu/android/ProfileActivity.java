package com.dhodu.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dhodu.android.ui.CircleImageView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by naman on 14/09/15.
 */
public class ProfileActivity extends AppCompatActivity {

    CircleImageView profileImage;
    TextView profileName, profileNumber;

    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");

        profileImage = (CircleImageView) findViewById(R.id.profile_pic);
        profileName = (TextView) findViewById(R.id.profie_name);
        profileNumber = (TextView) findViewById(R.id.profile_mobile);

        setUpProfileView();
    }

    private void setUpProfileView() {
        profileNumber = (TextView) findViewById(R.id.profile_mobile);
        profileName = (TextView) findViewById(R.id.profie_name);
        profileImage = (CircleImageView) findViewById(R.id.profile_pic);

        final ParseUser pUser = ParseUser.getCurrentUser();
        if (pUser != null) {
            profileNumber.setText(pUser.getUsername()
            );
            if (pUser.getString("photo") != null)
                Picasso.with(this).load(pUser.getString("photo")).placeholder(R.drawable.avatar_blank).into(profileImage);
            if (pUser.getString("name") != null)
                profileName.setText(pUser.getString("name"));
            else
                profileName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final AlertDialog.Builder alert = new AlertDialog.Builder(ProfileActivity.this);
                        final EditText name = new EditText(ProfileActivity.this);
                        name.setHint("James Bond");
                        alert.setView(name);
                        alert.setMessage("What do we call you?");
                        alert.setCancelable(true);
                        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String userName = name.getText().toString().trim();
                                profileName.setText(userName);
                                pUser.put("name", userName);
                                pUser.saveInBackground();
                            }
                        });
                        alert.setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                    }
                                });
                        alert.show();
                    }
                });

        }

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {

                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                }

                Intent chooser = new Intent(Intent.ACTION_CHOOSER);
                chooser.putExtra(Intent.EXTRA_INTENT, galleryIntent);
                chooser.putExtra(Intent.EXTRA_TITLE, "Choose Profile Picture");

                Intent[] intentArray = {cameraIntent};
                chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(chooser, 69);
            }
        });

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        photoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         if (requestCode == 69) {
            if (resultCode == RESULT_CANCELED) {
                File photoFile = new File(photoPath);
                if (photoFile.exists())
                    new File(photoPath).delete();
            } else if (resultCode == RESULT_OK) {
                if (data == null) {
                    Picasso.with(this).load(new File(photoPath)).into(profileImage);
                    saveImageToParse(photoPath);
                } else {
                    Uri selectedImage = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String picturePath = c.getString(columnIndex);
                    c.close();
                    Picasso.with(this).load(new File(picturePath)).into(profileImage);
                    saveImageToParse(picturePath);
                }
            }
        }
    }

    private void saveImageToParse(String path) {
        File file = new File(path);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final ParseUser parseUser = ParseUser.getCurrentUser();
        final ParseFile parseFile = new ParseFile("photo.png", bytes);
        parseFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    parseUser.put("photo", parseFile.getUrl());
                    parseUser.saveInBackground();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
