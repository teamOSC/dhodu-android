package com.dhodu.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dhodu.android.addresses.MyAddressesActivity;
import com.dhodu.android.login.LoginActivity;
import com.dhodu.android.ui.CircleImageView;
import com.parse.LogOutCallback;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    DrawerLayout drawerLayout;
    LinearLayout topView;

    TextView tvProfileMobile;
    TextView profileName;
    CircleImageView profilePhoto;
    ImageView editProfile;
    Toolbar toolbar;


    View statusView;
    private String photoPath;
    private TextView orderStatus;
    private ImageView expandCreateOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ParseUser parseUser = ParseUser.getCurrentUser();
        if ((parseUser == null)) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        } else {
            // Instantiate the layouts
            drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            topView = (LinearLayout) findViewById(R.id.topView);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            orderStatus = (TextView) findViewById(R.id.orderStatus);
            expandCreateOrder = (ImageView) findViewById(R.id.expand);
            editProfile = (ImageView) findViewById(R.id.edit_profile);
            statusView = findViewById(R.id.statusView);

            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
            getSupportActionBar().setTitle("Dhodu");

            final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            sp.edit().putBoolean("app_first_run", false).apply();

            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
            if (viewPager != null) {
                setupViewPager(viewPager);
                viewPager.setOffscreenPageLimit(2);
            }

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            if (navigationView != null) {
                setUpProfileView();
                navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.nav_addresses:
                                Intent intent = new Intent(MainActivity.this, MyAddressesActivity.class);
                                intent.setAction("addAddress");
                                startActivity(intent);
                                break;
                            case R.id.nav_ratecard:
                                startActivity(new Intent(MainActivity.this, RateCardActivity.class));
                                break;
                            case R.id.nav_referral:
                                startActivity(new Intent(MainActivity.this, ReferActivity.class));
                                break;
                            case R.id.nav_call:
                                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "7827121121")));
                                break;
                            case R.id.nav_logout:
                                final ProgressDialog pDialog = new ProgressDialog(MainActivity.this);
                                pDialog.setMessage("Logging out...");
                                pDialog.setCancelable(false);
                                pDialog.show();
                                ParseUser.logOutInBackground(new LogOutCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        pDialog.dismiss();
                                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                    }
                                });
                                break;
                        }
                        return false;
                    }
                });
            }


            //Setup the order pulldown screen
//            orderTime = (EditText) findViewById(R.id.order_time);
//            orderTime.setInputType(EditorInfo.TYPE_NULL);
//            orderTime.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View view, MotionEvent motionEvent) {
//                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                        // TODO Auto-generated method stub
//                        Calendar mcurrentTime = Calendar.getInstance();
//                        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
//                        int minute = mcurrentTime.get(Calendar.MINUTE);
//                        TimePickerDialog mTimePicker;
//                        mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
//                            @Override
//                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//                                orderTime.setText(
//                                        ((selectedHour < 10) ? "0" + selectedHour : selectedHour)
//                                                + ":" +
//                                                ((selectedMinute < 10) ? "0" + selectedMinute : selectedMinute));
//                            }
//                        }, hour, minute, true);//Yes 24 hour time
//                        mTimePicker.setTitle("Select Time");
//                        mTimePicker.show();
//                        return true;
//                    } else {
//                        return false;
//                    }
//                }
//            });
//


            editProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                }
            });


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.dhodu_primary_dark));
                getWindow().setNavigationBarColor(getResources().getColor(R.color.dhodu_primary_dark));
            }

        }

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
                    Picasso.with(this).load(new File(photoPath)).into(profilePhoto);
                    saveImageToParse(photoPath);
                } else {
                    Uri selectedImage = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String picturePath = c.getString(columnIndex);
                    c.close();
                    Picasso.with(this).load(new File(picturePath)).into(profilePhoto);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.action_call:
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "7827121121"));
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpProfileView() {
        tvProfileMobile = (TextView) findViewById(R.id.profile_mobile);
        profileName = (TextView) findViewById(R.id.profie_name);
        profilePhoto = (CircleImageView) findViewById(R.id.profile_pic);

        final ParseUser pUser = ParseUser.getCurrentUser();
        if (pUser != null) {
            tvProfileMobile.setText(pUser.getUsername()
            );
            if (pUser.getString("photo") != null)
                Picasso.with(this).load(pUser.getString("photo")).placeholder(R.drawable.avatar_blank).into(profilePhoto);
            if (pUser.getString("name") != null)
                profileName.setText(pUser.getString("name"));
            else
                profileName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                        final EditText name = new EditText(MainActivity.this);
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

        profilePhoto.setOnClickListener(new View.OnClickListener() {
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


    public void setStatusToHeader(String status, int imageId) {
        statusView.setVisibility(View.VISIBLE);
        if (imageId != 0) {
            expandCreateOrder.setImageResource(imageId);
            expandCreateOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AlertDialog.Builder cancelDialog = new AlertDialog.Builder(MainActivity.this);
                    cancelDialog.setTitle("Cancel Booking");
                    cancelDialog.setCancelable(true);
                    cancelDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //TODO:
                        }
                    });
                    cancelDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    cancelDialog.setMessage("Are you sure you want to cancel the booking?");
                    cancelDialog.show();
                }
            });
        }
        orderStatus.setText(status);
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new CurrentOrderFragment(), "Order");
        adapter.addFragment(new OrderHistoryFragment(), "History");
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

}
