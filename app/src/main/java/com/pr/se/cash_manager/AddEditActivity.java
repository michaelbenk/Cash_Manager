package com.pr.se.cash_manager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddEditActivity extends AppCompatActivity {
    private EditText sumView;
    private TextView dateView;
    private AutoCompleteTextView categoryView;
    private TextView descriptionView;

    //Camera and Gallery
    private static final int ACTION_TAKE_PHOTO_B = 1;
    private static final int SELECT_FILE = 1;

    private static final String BITMAP_STORAGE_KEY = "viewbitmap";
    private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
    private ImageView mImageView;
    private Bitmap mImageBitmap;

    private String mCurrentPhotoPath;

    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

    private String userChoosenTask;

    private boolean deleteimage = false;

    private FloatingActionButton fab_del;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_add_toolbar);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String id = getIntent().getStringExtra("id");
        final boolean update = getIntent().getBooleanExtra("update", false);

        this.sumView = (EditText) this.findViewById(R.id.activity_add_input_sum);
        this.dateView = (TextView) this.findViewById(R.id.activity_add_input_date);
        this.categoryView = (AutoCompleteTextView) this.findViewById(R.id.activity_add_input_category);
        this.descriptionView = (TextView) this.findViewById(R.id.activity_add_input_description);

        //Image
        mImageView = (ImageView) findViewById(R.id.activity_add_image);
        mImageBitmap = null;

        FloatingActionButton fab = (FloatingActionButton) this.findViewById(R.id.activity_add_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        fab_del = (FloatingActionButton) this.findViewById(R.id.activity_add_fab_image_delete);
        fab_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_camera));
                deleteimage = true;
                fab_del.setVisibility(View.INVISIBLE);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }

        this.dataInput();

        TextView save = (TextView) this.findViewById(R.id.activity_add_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (!checkInput(sumView, categoryView)) {
                    return;
                }

                if (update) {
                    ArrayList<Expense> list = RW.readExpenses(AddEditActivity.this, "expenses");
                    Expense element = null;

                    for (Expense e : list) {
                        if (e.getId().equals(id)) {
                            element = e;
                        }
                    }

                    list.remove(element);

                    if (element != null) {
                        element.setSum(Double.parseDouble(sumView.getText().toString()));
                        element.setDate(dateView.getText().toString());
                        element.setCategory(categoryView.getText().toString());
                        element.setDescription(descriptionView.getText().toString());

                        try{
                            if (deleteimage) //Wenn X Button bei Bild geklickt wurde
                                element.deleteImage(0);
                            else
                                element.addImage(((BitmapDrawable) mImageView.getDrawable()).getBitmap()); //Rechnungsfoto speichern
                        }catch (Exception e) {
                            //Wenn noch kein Rechnungsfoto gespeichert wurde soll nichts gemacht werden
                        }
                    }
                    list.add(element);

                    RW.writeExpenses(AddEditActivity.this, list, "expenses");
                } else {
                    Expense element = new Expense(Double.parseDouble(sumView.getText().toString()), dateView.getText().toString(), categoryView.getText().toString(), descriptionView.getText().toString());
                    element.addImage(((BitmapDrawable) mImageView.getDrawable()).getBitmap());
                    ArrayList<Expense> list = RW.readExpenses(AddEditActivity.this, "expenses");
                    list.add(element);
                    RW.writeExpenses(AddEditActivity.this, list, "expenses");
                }

                Intent intent = new Intent(AddEditActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void dataInput() {
        String sum = getIntent().getStringExtra("sum");
        String date = getIntent().getStringExtra("date");
        String category = getIntent().getStringExtra("category");
        String description = getIntent().getStringExtra("description");
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap image = null;
        if (byteArray != null)
            image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        sumView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    Double sum = Double.parseDouble(sumView.getText().toString());

                    if(sum < 0) {
                        sumView.setBackgroundColor(AddEditActivity.this.getResources().getColor(android.R.color.holo_green_light));
                        Toast.makeText(AddEditActivity.this, "The Value must not be negative!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    sumView.setBackgroundColor(0);
                }
            }
        });

        ArrayList<Category> cat = RW.readCategories(this, "categories");
        ArrayList<String> categories = new ArrayList<>();
        for (Category ca : cat) {
            categories.add(ca.toString());
            for (Category c : ca.getCategories()) {
                categories.add(c.toString());
            }
        }

        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, categories.toArray());
        categoryView.setAdapter(adapter);
        categoryView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String str = categoryView.getText().toString();

                    ListAdapter listAdapter = categoryView.getAdapter();
                    for (int i = 0; i < listAdapter.getCount(); i++) {
                        String temp = listAdapter.getItem(i).toString();
                        if (str.equals(temp)) {
                            categoryView.setBackgroundColor(0);
                            return;
                        }
                    }

                    categoryView.setBackgroundColor(AddEditActivity.this.getResources().getColor(android.R.color.holo_green_light));
                    Toast.makeText(AddEditActivity.this, "This Category does not exist!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (sum != null) {
            sumView.setText(sum);
        } else {
            sumView.setText("10.00");
        }
        if (date != null) {
            dateView.setText(date);
        } else {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            this.dateView.setText(sdf.format(calendar.getTime()));
        }
        if (category != null) {
            categoryView.setText(category);
        }
        if (description != null) {
            descriptionView.setText(description);
        }
        if (image != null){
            fab_del.setVisibility(View.VISIBLE);
            mImageView.setImageBitmap(image);
        }

        this.dateInput();
    }

    private void dateInput() {
        final Calendar calendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(calendar);
            }

        };

        this.dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(AddEditActivity.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });
    }

    private void updateLabel(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        this.dateView.setText(sdf.format(calendar.getTime()));
    }

    private boolean checkInput(EditText sumView, AutoCompleteTextView categoryView) {
        Double sum = Double.parseDouble(sumView.getText().toString());
        if(sum < 0) {
            sumView.setBackgroundColor(this.getResources().getColor(android.R.color.holo_green_light));
            Toast.makeText(AddEditActivity.this, "The Value must not be negative!", Toast.LENGTH_SHORT).show();
            return false;
        }

        sumView.setBackgroundColor(0);

        String cat = categoryView.getText().toString();
        ListAdapter listAdapter = categoryView.getAdapter();
        for (int i = 0; i < listAdapter.getCount(); i++) {
            String temp = listAdapter.getItem(i).toString();
            if (cat.equals(temp)) {
                categoryView.setBackgroundColor(0);
                return true;
            }
        }
        categoryView.setBackgroundColor(this.getResources().getColor(android.R.color.holo_green_light));
        Toast.makeText(AddEditActivity.this, "This Category does not exist!", Toast.LENGTH_SHORT).show();

        return false;
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddEditActivity.this);
        builder.setTitle("Foto hinzufügen");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(AddEditActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
    }

    /* Photo album for this application */
    private String getAlbumName() {
        return "CashManager";
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		/* Associate the Bitmap to the ImageView */
        mImageView.setImageBitmap(bitmap);
        mImageView.setVisibility(View.VISIBLE);
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void dispatchTakePictureIntent(int actionCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        switch(actionCode) {
            case ACTION_TAKE_PHOTO_B:
                File f = null;

                try {
                    f = setUpPhotoFile();
                    mCurrentPhotoPath = f.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                } catch (IOException e) {
                    e.printStackTrace();
                    f = null;
                    mCurrentPhotoPath = null;
                }
                break;

            default:
                break;
        } // switch

        startActivityForResult(takePictureIntent, actionCode);
    }

    private void handleBigCameraPhoto() {

        if (mCurrentPhotoPath != null) {
            setPic();
            galleryAddPic();
            mCurrentPhotoPath = null;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (userChoosenTask){
            case "Take Photo":
                switch (requestCode) {
                    case ACTION_TAKE_PHOTO_B: {
                        if (resultCode == RESULT_OK) {
                            handleBigCameraPhoto();
                            fab_del.setVisibility(View.VISIBLE);
                        }
                        break;
                    } // ACTION_TAKE_PHOTO_B
                } // switch
                break;
            case "Choose from Library":
                if (resultCode == RESULT_OK){
                    Uri targetUri = data.getData();
                    Bitmap bitmap;
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                        mImageView.setImageBitmap(bitmap);
                        fab_del.setVisibility(View.VISIBLE);
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                break;
        }

    }

    // Some lifecycle callbacks so that the image can survive orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
        outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null) );
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
        mImageView.setImageBitmap(mImageBitmap);
        mImageView.setVisibility(
                savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ?
                        ImageView.VISIBLE : ImageView.INVISIBLE
        );
    }
}
