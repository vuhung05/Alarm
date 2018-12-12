package com.example.vuhung.video10minutes;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vuhung.video10minutes.Model.Child;
import com.example.vuhung.video10minutes.Database.DBChild;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChildActivity extends AppCompatActivity {
    private static final int SELECT_IMAGE = 100;
    TextView tvTitle;
    DBChild dBChild;
    EditText edtChildName;
    ImageView imgAdd;
    CircleImageView imgChild;
    String pathPhoto="";
    Bitmap bmChoose;
    Child child;
    ArrayList<Child> allChild;
    Child childUpdate;

    boolean isUpdateChild =false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);

        imgChild = findViewById(R.id.imgChild);
        imgAdd = findViewById(R.id.img_add);
        edtChildName = findViewById(R.id.edt_child_name);
        tvTitle = findViewById(R.id.tv_title_child);
        dBChild =new DBChild(this);
       // dbManager.deleteAllChild();

        allChild = dBChild.getAllChild();
        childUpdate = new Child();

        Bundle extras = getIntent().getExtras();
        String name;
        int iD = -1;
        if(extras == null) {
            isUpdateChild = false;
            name= "";
            tvTitle.setText("New Child");
            imgAdd.setVisibility(View.VISIBLE);
        } else {
            isUpdateChild = true;
            tvTitle.setText("Update Child");
            iD= extras.getInt("child_id_update");
            childUpdate = dBChild.getChildById(iD);
            Log.d("abcde","id "+ childUpdate.getId());
            edtChildName.setText(childUpdate.getName());
            File file = new File(childUpdate.getPhoto());
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imgChild.setImageBitmap(bitmap);
                pathPhoto = childUpdate.getPhoto();
                imgAdd.setVisibility(View.INVISIBLE);
            }else {
                imgAdd.setVisibility(View.VISIBLE);
            }
        }

       bmChoose = BitmapFactory.decodeResource(getResources(),
                R.drawable.user);
    }

    public void backActivity(View view) {
        onBackPressed();
    }

    public void chooseImage(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_IMAGE);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        if (listChild.size() > 0) {
//            Log.d("abcd", "child 0 " + listChild.get(0).getName());
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Bitmap selectImage = BitmapFactory.decodeStream(imageStream);


                    bmChoose = selectImage;
                    imgAdd.setVisibility(View.INVISIBLE);
                    imgChild.setImageBitmap(selectImage);

                    String result;
                    Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
                    if (cursor == null) { // Source is Dropbox or other similar local file path
                        result = selectedImage.getPath();
                    } else {
                        cursor.moveToFirst();
                        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                        result = cursor.getString(idx);
                        cursor.close();
                    }
                    Log.d("abcd ","result " +result);
                    pathPhoto = result;
//                    Child child = databaseHandler.getChild("hung");
//                    byte[] mByte = child.getPhone_number();
//                    Bitmap myBitmap = BitmapFactory.decodeByteArray(mByte,0,mByte.length,null);

                }

//

        }
    }
    public void addOrUpdateChild(View view) {
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bmChoose.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//        byte[] byteArray = stream.toByteArray();
        if (!edtChildName.getText().toString().trim().isEmpty()){
            if (isUpdateChild){
                dBChild.update(childUpdate.getId(),new Child(edtChildName.getText().toString().trim(),pathPhoto));
                Toast.makeText(this,"Update completed!",Toast.LENGTH_SHORT).show();
                allChild = dBChild.getAllChild();
            }else if (dBChild.getChildByName(edtChildName.getText().toString().trim()).getName() == null){
                int ID;
                if (allChild.size()>0){
                    ID = allChild.get(allChild.size()-1).getId() +1;

                }else ID = 0;
                Log.d("abcd","ID  " +ID);
                dBChild.addChild(new Child(ID,edtChildName.getText().toString().trim(),pathPhoto));
                allChild = dBChild.getAllChild();
                Log.d("abcd", "path "+pathPhoto);

            }else  Toast.makeText(this, "The name has exist! ", Toast.LENGTH_SHORT).show();
//
        }else Toast.makeText(this,"Enter name child!",Toast.LENGTH_SHORT).show();

    }
}
