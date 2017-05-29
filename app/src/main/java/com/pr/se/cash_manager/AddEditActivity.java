package com.pr.se.cash_manager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
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
    private TextView dateToView;
    private Spinner intervallView;
    private Switch recurringView;
    private TextView recurringTextView;
    private CardView recurringCardView;

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
        this.dateToView = (TextView) this.findViewById(R.id.activity_add_text_todate);
        this.intervallView = (Spinner) this.findViewById(R.id.activity_add_spinner_intervall);
        this.recurringView = (Switch) this.findViewById(R.id.activity_add_switch_recurring);
        this.recurringTextView = (TextView) this.findViewById(R.id.activity_add_text_recurring);
        this.recurringCardView = (CardView) this.findViewById(R.id.activity_add_recurring);


        //Image
        mImageView = (ImageView) findViewById(R.id.activity_add_image);
        mImageBitmap = null;

        recurringCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recurringView.isChecked()){
                    recurringView.setChecked(false);
                    AddEditActivity.this.findViewById(R.id.activity_add_todate).setVisibility(View.INVISIBLE);
                    AddEditActivity.this.findViewById(R.id.activity_add_intervall).setVisibility(View.INVISIBLE);
                    ((ScrollView)AddEditActivity.this.findViewById(R.id.action_add_scrollview)).fullScroll(View.FOCUS_UP);
                    recurringTextView.setText(R.string.not_recurring);
                }else{
                    recurringView.setChecked(true);
                    AddEditActivity.this.findViewById(R.id.activity_add_todate).setVisibility(View.VISIBLE);
                    AddEditActivity.this.findViewById(R.id.activity_add_intervall).setVisibility(View.VISIBLE);
                    recurringTextView.setText(R.string.recurring);
                    ((ScrollView)AddEditActivity.this.findViewById(R.id.action_add_scrollview)).fullScroll(View.FOCUS_DOWN);
                    dataInput();
                }

            }
        });

        recurringView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b){
                    AddEditActivity.this.findViewById(R.id.activity_add_todate).setVisibility(View.INVISIBLE);
                    AddEditActivity.this.findViewById(R.id.activity_add_intervall).setVisibility(View.INVISIBLE);
                    ((ScrollView)AddEditActivity.this.findViewById(R.id.action_add_scrollview)).fullScroll(View.FOCUS_UP);
                    recurringTextView.setText(R.string.not_recurring);

                }else{
                    AddEditActivity.this.findViewById(R.id.activity_add_todate).setVisibility(View.VISIBLE);
                    AddEditActivity.this.findViewById(R.id.activity_add_intervall).setVisibility(View.VISIBLE);
                    ((ScrollView)AddEditActivity.this.findViewById(R.id.action_add_scrollview)).fullScroll(View.FOCUS_DOWN);
                    recurringTextView.setText(R.string.recurring);
                    dataInput();
                }

            }
        });

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

        mAlbumStorageDirFactory = new FroyoAlbumDirFactory();

        this.dataInput();

        TextView save = (TextView) this.findViewById(R.id.activity_add_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (!checkInput(sumView, categoryView)) {
                    return;
               }

                if (update) {
                    List<Expense> list = RW.readExpenses(AddEditActivity.this, "expenses");
                    Expense element = null;

                    for (Expense e : list) {
                        if (e.getId().equals(id)) {
                            element = e;
                        }
                    }

                    list.remove(element);

                    if (element != null) {
                        if (recurringView.isChecked()){
                            element = new Recurring_Expense();
                            ((Recurring_Expense)element).setIntervall(intervallView.getSelectedItem().toString());
                            ((Recurring_Expense)element).setDate_to(dateToView.getText().toString());
                        }else if (!recurringView.isChecked() && element instanceof Recurring_Expense){
                            element = new Expense();
                        }
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
                    Expense element;
                    if (recurringView.isChecked()){
                        element = new Recurring_Expense(Double.parseDouble(sumView.getText().toString()), dateView.getText().toString(), categoryView.getText().toString(), descriptionView.getText().toString(), dateToView.getText().toString(), intervallView.getSelectedItem().toString());
                    }else {
                        element = new Expense(Double.parseDouble(sumView.getText().toString()), dateView.getText().toString(), categoryView.getText().toString(), descriptionView.getText().toString());
                    }
                    try{
                        element.addImage(((BitmapDrawable) mImageView.getDrawable()).getBitmap());
                    }catch (Exception e){
                        //Wenn noch kein Rechnungsfoto gespeichert wurde soll nichts gemacht werden
                    }

                    List<Expense> list = RW.readExpenses(AddEditActivity.this, "expenses");
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

        String dateto = getIntent().getStringExtra("dateto");
        String intervall = getIntent().getStringExtra("intervall");
        
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

        List<Category> cat = RW.readCategories(this, "categories");
        List<String> categories = new ArrayList<>();
        for (Category ca : cat) {
            categories.add(ca.toString());
            for (Category c : ca.getSubCategories()) {
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
            sumView.setText(R.string.default_sum);
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
        if (dateto != null){
            recurringView.setChecked(true);
            this.findViewById(R.id.activity_add_todate).setVisibility(View.VISIBLE);
            recurringTextView.setText(R.string.recurring);
            dateToView.setText(dateto);
        }else{
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            this.dateToView.setText(sdf.format(calendar.getTime()));
        }

        int pos = 0;
        List<String> intervallValues = new ArrayList<>();
        for (Intervall i: Intervall.values()) {
            intervallValues.add(i.name());
            if (i.name().equals(intervall))
                pos = intervallValues.size() - 1;
        }
        ArrayAdapter<String> adapterIntervall = new ArrayAdapter<>(this, R.layout.spinner_item, intervallValues);
        adapterIntervall.setDropDownViewResource(R.layout.spinner_item);
        intervallView.setAdapter(adapterIntervall);

        if (intervall != null){
            recurringView.setChecked(true);
            this.findViewById(R.id.activity_add_intervall).setVisibility(View.VISIBLE);
            intervallView.setSelection(pos);
            recurringTextView.setText(R.string.recurring);
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
                updateLabel(calendar, dateView);
            }

        };

        final DatePickerDialog.OnDateSetListener dateTo = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(calendar, dateToView);
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

        this.dateToView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(AddEditActivity.this, dateTo, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });
    }

    private void updateLabel(Calendar calendar, TextView date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        date.setText(sdf.format(calendar.getTime()));
    }

    private boolean checkInput(EditText sumView, AutoCompleteTextView categoryView) {
        Double sum = Double.parseDouble(sumView.getText().toString());
        if(sum < 0) {
            sumView.setBackgroundColor(this.getResources().getColor(android.R.color.holo_green_light));
            Toast.makeText(AddEditActivity.this, "The Value must not be negative!", Toast.LENGTH_SHORT).show();
            return false;
        }

        sumView.setBackgroundColor(0);

        if (recurringView.isChecked()){ //Bis-Datum muss größer als Von-Datum sein
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                Date dateFrom = sdf.parse(dateView.getText().toString());
                Date dateTo = sdf.parse(dateToView.getText().toString());

                if (dateFrom.compareTo(dateTo) > 0){
                    dateToView.setBackgroundColor(this.getResources().getColor(android.R.color.holo_green_light));
                    Toast.makeText(AddEditActivity.this, "Das 'Bis'-Datum muss größer als das 'Von'-Datum sein", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

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
        return File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
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
                File f;

                try {
                    f = setUpPhotoFile();
                    mCurrentPhotoPath = f.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                } catch (IOException e) {
                    e.printStackTrace();
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
