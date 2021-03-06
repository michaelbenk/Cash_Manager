package com.pr.se.cash_manager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
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
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class AddEditActivity extends AppCompatActivity {
    //Viewelemente
    private EditText sumView;
    private TextView dateView;
    private AutoCompleteTextView categoryView;
    private TextView descriptionView;
    private TextView dateToView;
    private Spinner intervallView;
    private Switch recurringSwitchView;
    private TextView recurringTextView;
    private CardView recurringCardView;

    //für Kamera und Gallerie benötigte Variablen
    private static final int ACTION_TAKE_PHOTO_B = 1;
    private static final int SELECT_FILE = 1;
    private static final String BITMAP_STORAGE_KEY = "viewbitmap";
    private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
    private ImageView mImageView;
    private Bitmap mImageBitmap = null;
    private String mCurrentPhotoPath;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private String userChoosenTask;
    private boolean deleteimage = false;
    private FloatingActionButton fab_del;

    //Ausgaben und Kategorien variablen und Listen
    private List<Expense> list;
    private List<Category> cat;
    private String sum;
    private String date;
    private String category;
    private String description;
    private byte[] byteArray;
    private String dateto;
    private String intervall;
    private Bitmap image = null;
    private Expense newExpense;
    private Date nextDate;
    private Date dateFrom;
    private Date dateTo;
    private SimpleDateFormat sdf;
    private Calendar calendar;
    private ListAdapter listAdapter;
    private Expense expense;
    private String id;
    private boolean update;

    private String TAG = "AddEditActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        //Toolbar setzen
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_add_toolbar);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Viewelemente setzen
        id = getIntent().getStringExtra("id");
        update = getIntent().getBooleanExtra("update", false);

        this.sumView = (EditText) this.findViewById(R.id.activity_add_input_sum);
        this.dateView = (TextView) this.findViewById(R.id.activity_add_input_date);
        this.categoryView = (AutoCompleteTextView) this.findViewById(R.id.activity_add_input_category);
        this.descriptionView = (TextView) this.findViewById(R.id.activity_add_input_description);
        this.dateToView = (TextView) this.findViewById(R.id.activity_add_text_todate);
        this.intervallView = (Spinner) this.findViewById(R.id.activity_add_spinner_intervall);
        this.recurringSwitchView = (Switch) this.findViewById(R.id.activity_add_switch_recurring);
        this.recurringTextView = (TextView) this.findViewById(R.id.activity_add_text_recurring);
        this.recurringCardView = (CardView) this.findViewById(R.id.activity_add_recurring);
        mImageView = (ImageView) findViewById(R.id.activity_add_image);

        // Click auf Cardview bewirkt die Anzeige bzw. Verschwinden von 'Bis'-Datum und Interval
        recurringCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recurringSwitchView.isChecked()){
                    recurringSwitchView.setChecked(false);
                    setUnrecurring();
                }else{
                    recurringSwitchView.setChecked(true);
                    setRecurring();
                }
            }
        });

        // Click auf Switch bewirkt die Anzeige bzw. Verschwinden von 'Bis'-Datum und Interval
        recurringSwitchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b){
                    setUnrecurring();
                }else{
                    setRecurring();
                }
            }
        });

        //Button zum Bild hinzufügen
        FloatingActionButton fab = (FloatingActionButton) this.findViewById(R.id.activity_add_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        // Button zum Bild löschen
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

        sdf = new SimpleDateFormat("dd.MM.yyyy");
        calendar = Calendar.getInstance();

        this.dataInput();

        //Save rechts oben - Speichern bzw. Update
        TextView save = (TextView) this.findViewById(R.id.activity_add_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkInput()) {
                    return;
                }

                if (update) {
                    //Update von Expense
                    list = RW.readExpenses(AddEditActivity.this, "expenses");
                    Expense element = null;


                    for (Expense e : list) {
                        if (e.getId().equals(id)) {
                            element = e;
                        }
                    }

                    final String recurringId = element.getId();
                    boolean wasUnRecurring = !(element instanceof RecurringExpense);

                    //zu änderndes element löschen
                    list.remove(element);

                    if (recurringSwitchView.isChecked()){ //wenn das Element wiederkehrend ist; Werte zuweisen
                        element = new RecurringExpense();
                        ((RecurringExpense)element).setIntervall(intervallView.getSelectedItem().toString());
                        ((RecurringExpense)element).setDate_to(dateToView.getText().toString());
                        ((RecurringExpense)element).setDate_next(sdf.format(nextDate));

                    }else if (!recurringSwitchView.isChecked() && element instanceof RecurringExpense){
                        //Wenn das Element von einem wiederkehrenden zu einem nicht wiederkehrenden Expense gemacht werden soll
                        element = new Expense();
                    }

                    //Werte zuweisen (nur Id bleibt die alte)
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
                    //Wenn die Ausgabe zuvor nicht wiederkehrend war, sollen nun Expenses bis zum aktuellen Tag erstellt werden
                    if (element instanceof RecurringExpense && recurringSwitchView.isChecked() && wasUnRecurring) {
                        nextDate = newRecurringExpensesBeforeToday((RecurringExpense) element);
                        if (nextDate != null)
                            ((RecurringExpense) element).setDate_next(sdf.format(nextDate));
                        list.add(element);
                        RW.writeExpenses(AddEditActivity.this, list, "expenses");
                        Intent intent = new Intent(AddEditActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else if (element instanceof RecurringExpense && recurringSwitchView.isChecked() && !wasUnRecurring) {
                        //Wenn die Ausgabe zuvor auch schon wiederkehrend war soll der Benutzer gefragt werden ob die Änderung
                        //auswirkungen auf alle vorherigen Expenses hat oder nur auf die zukünftigen

                        final Expense finalElement = element;

                        final CharSequence[] items = {getString(R.string.save_expense_Dialog_allExpenses), getString(R.string.save_expense_Dialog_futureExpeses)};
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddEditActivity.this);
                        builder.setTitle(R.string.save_expense_Dialog_heading);
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if (items[item].equals(getString(R.string.save_expense_Dialog_allExpenses))) {
                                    // Recurring Expense wird mit Änderung aller vergangenen Expenses eingefügt

                                    List<Expense> expToRemove = new ArrayList<>();
                                    for (Expense ex : list) {
                                        if (!(ex instanceof RecurringExpense) && ex.getRecurring_id() != null && ex.getRecurring_id().equals(recurringId)) {
                                            expToRemove.add(ex);
                                        }
                                    }
                                    list.removeAll(expToRemove);
                                    nextDate = newRecurringExpensesBeforeToday((RecurringExpense) finalElement);
                                    if (nextDate != null)
                                        ((RecurringExpense) finalElement).setDate_next(sdf.format(nextDate));
                                    list.add(finalElement);
                                    RW.writeExpenses(AddEditActivity.this, list, "expenses");
                                    Intent intent = new Intent(AddEditActivity.this, MainActivity.class);
                                    startActivity(intent);

                                } else if (items[item].equals(getString(R.string.save_expense_Dialog_futureExpeses))) {
                                    // Recurring Expense wird ohne Änderung der vergangenen Expenses eingefügt
                                    list.add(finalElement);
                                    RW.writeExpenses(AddEditActivity.this, list, "expenses");
                                    Intent intent = new Intent(AddEditActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });
                        builder.create();
                        builder.show();
                    }else{
                        list.add(element);
                        RW.writeExpenses(AddEditActivity.this, list, "expenses");
                        Intent intent = new Intent(AddEditActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                } else {
                    // neuer Expense
                    Expense element;
                    list = RW.readExpenses(AddEditActivity.this, "expenses");
                    if (recurringSwitchView.isChecked()){
                        // Wenn der neue Expense ein wiederkehrender ist sollen auch alle Expenses bis zum akt. Tag erstellt werden
                        element = new RecurringExpense(Double.parseDouble(sumView.getText().toString()), dateView.getText().toString(), categoryView.getText().toString(), descriptionView.getText().toString(), dateToView.getText().toString(), intervallView.getSelectedItem().toString());
                        nextDate = newRecurringExpensesBeforeToday((RecurringExpense) element);
                        if (nextDate != null)
                            ((RecurringExpense) element).setDate_next(sdf.format(nextDate));
                    }else {
                        //nicht wiederkehrendeer Expense
                        element = new Expense(Double.parseDouble(sumView.getText().toString()), dateView.getText().toString(), categoryView.getText().toString(), descriptionView.getText().toString());
                    }
                    try{
                        element.addImage(((BitmapDrawable) mImageView.getDrawable()).getBitmap());
                    }catch (Exception e){
                        //Wenn noch kein Rechnungsfoto gespeichert wurde soll nichts gemacht werden
                    }

                    list.add(element);
                    RW.writeExpenses(AddEditActivity.this, list, "expenses");

                    Intent intent = new Intent(AddEditActivity.this, MainActivity.class);
                    startActivity(intent);
                }

            }
        });
    }

    // Erstellt alle Ausgaben die laut wiederkehrender Ausgabe vor bzw. heute getätigt wurden
    @Nullable
    private Date newRecurringExpensesBeforeToday(RecurringExpense element) {
        if (dateFrom != null && dateTo != null) {
            Date today = new Date();
            if (dateFrom.compareTo(today) <= 0) { //Wenn die Ausgabe in der Vergangenheit liegt bzw. heute ist
                newExpense = new Expense(element.getSum(), element.getDate(), element.getCategory(), element.getDescription());
                newExpense.setRecurring_id(element.getId());
                try{
                    if (element.getImages().get(0) != null)
                        image = BitmapFactory.decodeByteArray(element.getImages().get(0), 0, element.getImages().get(0).length);
                    newExpense.addImage(image);
                }catch (Exception e){
                    //Wenn noch kein Rechnungsfoto gespeichert wurde soll nichts gemacht werden
                }
                list.add(newExpense);
            }
            //Restliche vergangene Ausgaben erzeugen
            while (nextDate.compareTo(today) <= 0) {
                newExpense = new Expense(element.getSum(), sdf.format(nextDate), element.getCategory(), element.getDescription());
                newExpense.setRecurring_id(element.getId());
                try{
                    if (element.getImages().get(0) != null)
                        image = BitmapFactory.decodeByteArray(element.getImages().get(0), 0, element.getImages().get(0).length);
                    newExpense.addImage(image);
                }catch (Exception e){
                    //Wenn noch kein Rechnungsfoto gespeichert wurde soll nichts gemacht werden
                }
                list.add(newExpense);
                nextDate = calculateNextDate(nextDate);
            }
            RW.writeExpenses(AddEditActivity.this, list, "expenses");
            return nextDate;
        }
        return null;
    }

    //Bis-Datum und Interval einblenden
    private void setRecurring() {
        AddEditActivity.this.findViewById(R.id.activity_add_todate).setVisibility(View.VISIBLE);
        AddEditActivity.this.findViewById(R.id.activity_add_intervall).setVisibility(View.VISIBLE);
        recurringTextView.setText(R.string.view_recurring);
        ((ScrollView)AddEditActivity.this.findViewById(R.id.action_add_scrollview)).fullScroll(View.FOCUS_DOWN);
        dataInput();
    }

    //Bis-Datum und Interval ausblenden
    private void setUnrecurring() {
        AddEditActivity.this.findViewById(R.id.activity_add_todate).setVisibility(View.INVISIBLE);
        AddEditActivity.this.findViewById(R.id.activity_add_intervall).setVisibility(View.INVISIBLE);
        ((ScrollView)AddEditActivity.this.findViewById(R.id.action_add_scrollview)).fullScroll(View.FOCUS_UP);
        recurringTextView.setText(R.string.view_non_recurring);
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
        if (update) {
            //Werte von Intent (ShowDetails) holen
            list = RW.readExpenses(this, "expenses");

            for (Expense e : list) {
                if (e.getId().equals(id)) {
                    expense = e;
                    break;
                }
            }

            //Byte Array in Bitmap konvertieren
            if (expense.getImages().size() != 0)
                image = BitmapFactory.decodeByteArray(expense.getImages().get(0), 0, expense.getImages().get(0).length);

            sum = String.valueOf(expense.getSum());
            date = expense.getDate();
            category = expense.getCategory();
            description = expense.getDescription();
            if (expense.getImages().size() != 0)
                byteArray = expense.getImages().get(0);
            if (expense instanceof RecurringExpense) {
                dateto = ((RecurringExpense) expense).getDate_to();
                intervall = ((RecurringExpense) expense).getIntervall();
            }
            //Byte Array in Bitmap konvertieren
            if (byteArray != null)
                image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        }

        //Wertcheck der Summe sobald kein Fokus mehr auf den Wert liegt
        sumView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    Double sum = Double.parseDouble(sumView.getText().toString());
                    if(sum < 0) {
                        sumView.setBackgroundColor(AddEditActivity.this.getResources().getColor(android.R.color.holo_green_light));
                        Toast.makeText(AddEditActivity.this, getString(R.string.error_negValue), Toast.LENGTH_SHORT).show();
                    }else {
                        sumView.setBackgroundColor(0);
                    }
                }
            }
        });

        //Autokomplete "Dictionary" setzten
        cat = RW.readCategories(this, "categories");
        List<String> categories = new ArrayList<>();
        for (Category ca : cat) {
            categories.add(ca.toString());
            for (Category c : ca.getSubCategories()) {
                categories.add(c.toString());
            }
        }
        final ArrayAdapter<?> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories.toArray());
        categoryView.setAdapter(adapter);

        //Wertcheck wenn Text nicht mehr editiert wird
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
                    Toast.makeText(AddEditActivity.this, getString(R.string.error_CategoryNotExists), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Viewelemente setzten

        if (sum != null ) {
            sumView.setText(sum);
        }
        if (date != null) {
            dateView.setText(date);
        } else {
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
            recurringSwitchView.setChecked(true);
            this.findViewById(R.id.activity_add_todate).setVisibility(View.VISIBLE);
            recurringTextView.setText(R.string.view_recurring);
            dateToView.setText(dateto);
        }else{
            this.dateToView.setText(sdf.format(calendar.getTime()));
        }

        int pos = 0;
        List<String> intervallValues = new ArrayList<>();
        for (Interval i: Interval.values()) {
            intervallValues.add(i.name());
            if (i.name().equals(intervall))
                pos = intervallValues.size() - 1;
        }
        ArrayAdapter<String> adapterIntervall = new ArrayAdapter<>(this, R.layout.spinner_item, intervallValues);
        adapterIntervall.setDropDownViewResource(R.layout.spinner_item);
        intervallView.setAdapter(adapterIntervall);

        if (intervall != null){
            recurringSwitchView.setChecked(true);
            this.findViewById(R.id.activity_add_intervall).setVisibility(View.VISIBLE);
            intervallView.setSelection(pos);
            recurringTextView.setText(R.string.view_recurring);
        }

        this.dateInput();
    }

    // DatePickerDiaglog setzten für beide Datumsfelder
    private void dateInput() {
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
        date.setText(sdf.format(calendar.getTime()));
        checkInput();
    }

    //Input der Werte überprüfen
    private boolean checkInput() {
        Double sum = Double.parseDouble(sumView.getText().toString());
        //TODO Wert darf nur 2 Nachkommerstellen haben
        if(sum < 0) {
            sumView.setBackgroundColor(this.getResources().getColor(android.R.color.holo_green_light));
            Toast.makeText(AddEditActivity.this, R.string.error_negValue, Toast.LENGTH_SHORT).show();
            return false;
        }else {
            sumView.setBackgroundColor(0);
        }

        if (recurringSwitchView.isChecked()){
            try {

                dateFrom = sdf.parse(dateView.getText().toString());
                dateTo = sdf.parse(dateToView.getText().toString());

                if (dateFrom.compareTo(dateTo) > 0){ //Bis-Datum muss größer als Von-Datum sein
                    dateToView.setBackgroundColor(this.getResources().getColor(android.R.color.holo_green_light));
                    Toast.makeText(AddEditActivity.this, R.string.error_TOgreaterFROM, Toast.LENGTH_SHORT).show();
                    return false;
                }

                nextDate = calculateNextDate(dateFrom);

                if (dateTo.compareTo(nextDate) < 0) { //Interval muss sich zwischen von und bis Datum ausgehen
                    dateToView.setBackgroundColor(this.getResources().getColor(android.R.color.holo_green_light));
                    Toast.makeText(AddEditActivity.this, R.string.error_TOdateIntervall, Toast.LENGTH_SHORT).show();
                    return false;
                }
                dateToView.setBackgroundColor(0);

            } catch (ParseException e) {
                Log.e(TAG, "Couldn't parse date when checking input", e);
            }
        }

        String cat = categoryView.getText().toString();
        listAdapter = categoryView.getAdapter();
        for (int i = 0; i < listAdapter.getCount(); i++) {
            String temp = listAdapter.getItem(i).toString();
            if (cat.equals(temp)) {
                categoryView.setBackgroundColor(0);
                return true;
            }
        }
        categoryView.setBackgroundColor(this.getResources().getColor(android.R.color.holo_green_light));
        Toast.makeText(AddEditActivity.this, R.string.error_CategoryNotExists, Toast.LENGTH_SHORT).show();
        return false;
    }

    private Date calculateNextDate(Date date) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);

        switch (intervallView.getSelectedItem().toString()) {
            case "yearly":
                gregorianCalendar.add(Calendar.YEAR, 1);
                break;
            case "monthly":
                gregorianCalendar.add(Calendar.MONTH, 1);
                break;
            case "quarterly":
                gregorianCalendar.add(Calendar.MONTH, 3);
                break;
            case "daily":
                gregorianCalendar.add(Calendar.DAY_OF_YEAR, 1);
                break;
            case "weekly":
                gregorianCalendar.add(Calendar.WEEK_OF_YEAR, 1);
                break;
        }

        return gregorianCalendar.getTime();
    }

    //Menü für Foto:   Foto mit Kamera schießen oder aus Gallerie auswählen
    private void selectImage() {
        final CharSequence[] items = {getString(R.string.save_picture_dialog_photo), getString(R.string.save_picture_dialog_Libarary)};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddEditActivity.this);
        builder.setTitle(R.string.save_picture_dialog_title);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(AddEditActivity.this);

                if (items[item].equals(getString(R.string.save_picture_dialog_photo))) {
                    userChoosenTask = getString(R.string.save_picture_dialog_photo);
                    if (result)
                        cameraIntent();
                } else if (items[item].equals(getString(R.string.save_picture_dialog_Libarary))) {
                    userChoosenTask = getString(R.string.save_picture_dialog_Libarary);
                    if (result)
                        galleryIntent();
                }
            }
        });
        builder.show();
    }

    //Foto aus Gallerie
    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.save_picture_dialog_SelFile)), SELECT_FILE);
    }

    //Foto mit Kamera schießen
    private void cameraIntent() {
        dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
    }

    /* Photo album for this application */
    private String getAlbumName() {
        return getString(R.string.app_name);
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
        String imageFileName = JPEG_FILE_PREFIX + sdf.format(new Date()) + "_";
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

                    Log.e(TAG, "Couldn't set path when taking photo", e);
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

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (userChoosenTask.equals(getString(R.string.save_picture_dialog_photo))){
            if (requestCode == ACTION_TAKE_PHOTO_B){
                if (resultCode == RESULT_OK) {
                    handleBigCameraPhoto();
                    fab_del.setVisibility(View.VISIBLE);
                }
            }
        }else if (userChoosenTask.equals(getString(R.string.save_picture_dialog_Libarary))){
            if (resultCode == RESULT_OK){
                Uri targetUri = data.getData();
                mCurrentPhotoPath = getRealPathFromURI(targetUri);
                setPic();
            }
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
