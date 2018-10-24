package com.example.memo;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Add_memo extends Activity {
    private Button button_save;
    private Button button_back;
    private Button button_insertimg;
    private TextView timeView;
    private EditText content;
    private EditText title;
    private Uri originalUri;
    private Bitmap bitmap;
    private DatabaseHelper databaseHelper;
    private boolean exi = false;
    private int po = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memo);
        databaseHelper = new DatabaseHelper(this, "Memo.db", null, 1);
        title = (EditText) findViewById(R.id.ed_title);
        content = (EditText) findViewById(R.id.ed_content);
        timeView = (TextView) findViewById(R.id.time_text);
        button_back = (Button) findViewById(R.id.back);
        //点击BACK按钮，返回上一层
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Add_memo.this, MainActivity.class);
                startActivity(intent);
            }
        });
        Intent intent = getIntent();
        //若有传递参数，则显示
        if (intent.getSerializableExtra("memo") != null) {
            Memory memo = (Memory) intent.getSerializableExtra("memo");
            exi = true;
            if (memo != null) {
                title.setText(memo.getTitle());
                Log.d("MYTAG", "memo.getUri is "+memo.getUri());
                if (memo.getUri() != null) {
                    try {
                        ContentResolver resolver = getContentResolver();
                        Uri u = Uri.parse(memo.getUri());
                        Bitmap originalBitmap = null;
                        originalBitmap = BitmapFactory.decodeStream(resolver.openInputStream(u));
                        bitmap = getResizedBitmap(originalBitmap, 400, 400);
                        String temp = memo.getContent();
                        insertPhotoToEditText(getBitmapMime(bitmap, u), temp);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else
                    content.setText(memo.getContent());
                timeView.setText(memo.getDate());
                po = memo.getId();

            }
        }
        button_save = (Button) findViewById(R.id.save);
        //点击Save按钮，保存内容
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int res = saveContent(po);
                if (res == 0) {
                    Intent intent = new Intent(Add_memo.this, MainActivity.class);
                    startActivity(intent);
                } else
                    Toast.makeText(Add_memo.this, "内容不得为空", Toast.LENGTH_SHORT).show();

            }
        });
        timeView = (TextView) findViewById(R.id.time_text);
        //点击TextView，选择时间
        timeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePick((TextView) v);
            }
        });
        button_insertimg = (Button) findViewById(R.id.insert_pic);
        //点击按钮，插入图片
        button_insertimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
                getImage.addCategory(Intent.CATEGORY_OPENABLE);
                getImage.setType("image/*");
                startActivityForResult(getImage, 1);
            }
        });
    }
    //选择时间
    private void showTimePick(final TextView timeView) {
        final StringBuffer time = new StringBuffer();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int hour = calendar.get(Calendar.HOUR);
        int min = calendar.get(Calendar.MINUTE);
        final TimePickerDialog timePickerDialog = new TimePickerDialog(Add_memo.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String ho = "";
                        String min = "";
                        if (hourOfDay < 10)
                            ho = "0" + hourOfDay;
                        else
                            ho = "" + hourOfDay;
                        if (minute < 10)
                            min = "0" + minute;
                        else
                            min = "" + minute;
                        time.append(" " + ho + ":" + min + ":00");
                        timeView.setText(time);
                    }
                }, hour, min, true);
        DatePickerDialog datePickerDialog = new DatePickerDialog(Add_memo.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfyear, int dayOfMonth) {
                        String mo = "";
                        String da = "";
                        if (monthOfyear + 1 < 10)
                            mo = "0" + (monthOfyear + 1);
                        else
                            mo = "" + (monthOfyear + 1);
                        if (dayOfMonth < 10)
                            da = "0" + dayOfMonth;
                        else
                            da = "" + dayOfMonth;
                        time.append(year + "-" + mo + "-" + da);
                        timePickerDialog.show();
                    }
                }, year, month, day);
        datePickerDialog.show();
    }
    //实现插入图片功能
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        ContentResolver resolver = getContentResolver();
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                originalUri = data.getData();
                try {
                    Bitmap originalBitmap = BitmapFactory.decodeStream(resolver.openInputStream(originalUri));
                    bitmap = getResizedBitmap(originalBitmap, 400, 400);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (bitmap != null) {
                    insertPhotoToEditText(getBitmapMime(bitmap, originalUri), "");
                } else {
                    Toast.makeText(Add_memo.this, "获取图片失败",
                            Toast.LENGTH_SHORT).show();
                }

            }
        }
    }
    //调整图片大小
    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);
        return resizedBitmap;
    }
    //插入图片到某一位置
    private int insertPhotoToEditText(SpannableString spannableString, String con) {
        Editable editable = content.getText();
        int start;
        if (con.equals("")) {
            start = content.getSelectionStart();
            editable.insert(start, spannableString);
        }
        else {
            int position[] = getPosition(con);
            start = position[0];
            editable.insert(0,con.substring(0,position[0]));
            editable.insert(start, spannableString);
            if(position[1] < con.length()-1)
                editable.insert(start + spannableString.length(),con.substring(position[1],con.length()-1));
            Log.d("MYTAG", "文字接入部分"+start + spannableString.length());
            Log.d("MYTAG", ""+start + spannableString.length());
        }
        content.setText(editable);
        content.setSelection(start + spannableString.length());
        content.setFocusableInTouchMode(true);
        content.setFocusable(true);
        return start + spannableString.length();
    }
    //将Bitmap转化为流
    private SpannableString getBitmapMime(Bitmap pic, Uri uri) {
        String path = uri.getPath();
        SpannableString spannableString = new SpannableString(path);
        ImageSpan span = new ImageSpan(this, pic);
        spannableString.setSpan(span, 0, path.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;

    }
    //保存内容到数据库
    private int saveContent(int id) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String co_title = "";
        String co_content = "";
        String date = "";
        String min = "";
        String ho = "";
        String sec = "";
        String mon = "";
        String da = "";

        co_title = title.getText().toString();
        co_content = content.getText().toString();
        if (co_content.equals("") && co_title.equals("")) {
            return -1;
        }
        date = timeView.getText().toString();
        if (date.equals("Choose a time")) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            if (month < 10)
                mon = "0" + month;
            else
                mon = "" + month;
            if (day < 10)
                da = "0" + day;
            else
                da = "" + day;
            if (hour < 10)
                ho = "0" + hour;
            else
                ho = "" + hour;
            if (minute < 10)
                min = "0" + minute;
            else
                min = "" + minute;
            if (second < 10)
                sec = "0" + second;
            else
                sec = "" + second;
            date = year + "-" + mon + "-" + da + " " + ho + ":" + min + ":" + sec;
        }
        ContentValues values = new ContentValues();
        values.put("title", co_title);
        values.put("content", co_content);
        Log.d("MYTAG", "content final is " + co_content);
        values.put("date", date);
        if (originalUri != null)
            values.put("uri", originalUri.toString());
        if (!exi) {
            db.insert("Memory", null, values);
            values.clear();
        } else {
            db.update("Memory", values, "id = ?", new String[]{"" + po});
            values.clear();
        }
        db.close();
        return 0;
    }
    //使用正则表达式匹配图片插入位置
    private int[] getPosition(String con) {
        int position[] = new int[2];
        String re1 = "(\\/)";    // Any Single Character 1
        String re2 = "((?:[a-z][a-z0-9_]*))";    // Variable Name 1
        String re3 = "(.)";    // Any Single Character 2
        String re4 = ".*?";    // Non-greedy match on filler
        String re5 = "(\\/)";    // Any Single Character 3
        String re6 = "((?:[a-z][a-z]+))";    // Word 1
        String re7 = "(:)";    // Any Single Character 4
        String re8 = "(\\d+)";    // Integer Number 1

        Pattern p = Pattern.compile(re1 + re2 + re3 + re4 + re5 + re6 + re7 + re8, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        System.out.println(p.toString());
        Matcher m = p.matcher(con);
        if (m.find()) {
            String c1 = m.group(1);
            String var1 = m.group(2);
            String c2 = m.group(3);
            String c3 = m.group(4);
            String word1 = m.group(5);
            String c4 = m.group(6);
            String int1 = m.group(7);
            String str = c1 + var1 + c2 + c3 + word1 + c4 + int1;
            Log.d("MYTAG", "con is " + con);
            position[0] = con.indexOf(c1);
            Log.d("MYTAG", "position0 is "+position[0]);
            Log.d("MYTAG","(" + c1.toString() + ")" + "(" + var1.toString() + ")" + "(" + c2.toString() + ")" + "(" + c3.toString() + ")" + "(" + word1.toString() + ")" + "(" + c4.toString() + ")" + "(" + int1.toString() + ")" + "\n");
            position[1] = position[0]+str.length();
            Log.d("MYTAG", "position1 is" + position[1]);
        }
        return position;
    }
}
