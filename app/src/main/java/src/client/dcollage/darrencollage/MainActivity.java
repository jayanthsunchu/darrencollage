package src.client.dcollage.darrencollage;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import petrov.kristiyan.colorpicker.ColorPicker;
import src.client.dcollage.darrencollage.fragments.OptionsFragment;
import src.client.dcollage.darrencollage.helpers.Constant;

import static android.R.attr.bitmap;
import static android.app.Activity.RESULT_OK;
import static java.lang.reflect.Array.getInt;
import static java.lang.reflect.Array.set;
import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {
    ImageView ImgTop;
    ImageView ImgBottom;
    LinearLayout LayoutWhole;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    RelativeLayout HoverTitle;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_save){
            Toast.makeText(MainActivity.this, "Saving Image. Please Wait...", Toast.LENGTH_LONG).show();
            SaveImage(viewToBitmap(LayoutFrame));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    FrameLayout LayoutFrame;
    LinearLayout LayoutOne;
    LinearLayout LayoutTwo;
    LinearLayout LayoutThree;
    int layout_id = 1;
    TextView Title;
    TextView Size;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings = getSharedPreferences(Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        editor = settings.edit();

        LayoutWhole = (LinearLayout)findViewById(R.id.layout_whole);
        LayoutFrame = (FrameLayout)findViewById(R.id.layout_frm);
        LayoutOne = (LinearLayout)findViewById(R.id.layout_one);
        LayoutTwo = (LinearLayout)findViewById(R.id.layout_two);
        LayoutThree = (LinearLayout)findViewById(R.id.layout_three);
        HoverTitle = (RelativeLayout)findViewById(R.id.hovering_title);
        LayoutOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout_id = Constant.LAYOUT_ONE;
                HoverTitle.setVisibility(View.VISIBLE);
                SetLinearLayout();
            }
        });
        LayoutTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HoverTitle.setVisibility(View.VISIBLE);
                layout_id = Constant.LAYOUT_TWO;
                SetLinearLayout();
            }
        });

        LayoutThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HoverTitle.setVisibility(View.GONE);
                layout_id = Constant.LAYOUT_THREE;
                SetLinearLayout();
            }
        });
        SetLinearLayout();

        Title = (TextView)findViewById(R.id.layout_item_text);
        Size = (TextView)findViewById(R.id.layout_item_subtext);
        PriceTag = (TextView)findViewById(R.id.txt_price_tag);

        PriceTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                alert.setTitle("Enter the amount");
                final EditText input = new EditText(view.getContext());
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setRawInputType(Configuration.KEYBOARD_12KEY);
                alert.setView(input);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        PriceTag.setText("$" + input.getText().toString());
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Put actions for CANCEL button here, or leave in blank
                    }
                });
                alert.show();
            }
        });

        Title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Pick an item");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Title.setText(items[which]);
                    }
                });
                builder.show();
            }
        });
        Size.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Pick a sub item");
                builder.setItems(subitems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Size.setText(subitems[which]);
                    }
                });
                builder.show();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    CharSequence items[] = new CharSequence[] {"Item 1", "Item 2", "Item 3", "Item 4"};
    CharSequence subitems[] = new CharSequence[] {"Sub Item 1", "Sub Item 2", "Sub Item 3", "Sub Item 4"};
    TextView PriceTag;
    TextView TitleT;
    TextView SizeT;
    RelativeLayout colorLayout;
    private void SetLinearLayout(){
        LayoutWhole.removeAllViews();
        if(layout_id == Constant.LAYOUT_ONE){
            LayoutInflater layoutInflater = (LayoutInflater)
                    this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewGroup view = (ViewGroup) findViewById(android.R.id.content);
            LayoutWhole.addView(layoutInflater.inflate(R.layout.lay_one, view, false));
        }
        if(layout_id == Constant.LAYOUT_TWO){
            LayoutInflater layoutInflater = (LayoutInflater)
                    this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewGroup view = (ViewGroup) findViewById(android.R.id.content);
            LayoutWhole.addView(layoutInflater.inflate(R.layout.lay_two, view, false));
        }
        if(layout_id == Constant.LAYOUT_THREE){
            LayoutInflater layoutInflater = (LayoutInflater)
                    this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewGroup view = (ViewGroup) findViewById(android.R.id.content);
            LayoutWhole.addView(layoutInflater.inflate(R.layout.lay_three, view, false));

            TitleT= (TextView)findViewById(R.id.layout_item_text_three);
            SizeT= (TextView)findViewById(R.id.layout_item_subtext_three);
            TitleT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Pick an item");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            TitleT.setText(items[which]);
                        }
                    });
                    builder.show();
                }
            });
            SizeT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Pick a sub item");
                    builder.setItems(subitems, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SizeT.setText(subitems[which]);
                        }
                    });
                    builder.show();
                }
            });

            colorLayout = (RelativeLayout)findViewById(R.id.color_layout);
            colorLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ColorPicker colorPicker = new ColorPicker(view.getContext());
                    colorPicker.setColors(R.array.collage);
                    colorPicker.show();
                    colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                        @Override
                        public void onChooseColor(int position,int color) {
                            colorLayout.setBackgroundColor(color);
                        }

                        @Override
                        public void onCancel(){
                            // put code
                        }
                    });
                }
            });
        }
        Log.e("Testing", "Check");
        ImgTop = (ImageView) findViewById(R.id.img_one);
        if(layout_id == Constant.LAYOUT_THREE || layout_id == Constant.LAYOUT_TWO){
            ImgBottom = (ImageView) findViewById(R.id.img_two);
            ImgBottom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LAYOUT_FLAG = 1;
                    SelectPicture();
                }
            });

        }
//        //Title = (TextView)findViewById(R.id.txt_title);
//        //Size = (TextView)findViewById(R.id.txt_size);
//        Title.setText(settings.getString("layouttitle", ""));
//        Size.setText(settings.getString("layoutsize", ""));
        ImgTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LAYOUT_FLAG = 0;
                SelectPicture();
            }
        });

    }

    private void SaveImage(Bitmap bitmap) {
        try {
            File yourFile = new File(Environment.getExternalStorageDirectory() + "/DarrenCollageFiles");
            yourFile.mkdirs();
            String fileName = new SimpleDateFormat("yyyyMMddhhmm'.png'").format(new Date());
            FileOutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory() + "/DarrenCollageFiles/DarrenFinalPic_"+fileName);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.close();
            Toast.makeText(MainActivity.this, "Saved Successfully.", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Error while saving.", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Error while saving.", Toast.LENGTH_LONG).show();
        }
    }

    private Bitmap RotateIf(Uri data){
        Bitmap myBitmap = null;
        try {
            myBitmap = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            File myFile = new File(data.toString());
            ExifInterface exif = new ExifInterface(myFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            Matrix matrix = new Matrix();
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                matrix.postRotate(90);
            }
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                matrix.postRotate(180);
            }
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                matrix.postRotate(270);
            }
            myBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true); // rotating bitmap
        }
        catch (Exception e) {
        }
        return myBitmap;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        String path ="";
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        }
        catch(Exception e){
            Log.e("Testing", "getImageUri" + e.getMessage());
        }
        return Uri.parse(path);
    }

    private int LAYOUT_FLAG = 0;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImgTop.setScaleType(ImageView.ScaleType.FIT_XY);
        if(LAYOUT_FLAG == 1)
            ImgBottom.setScaleType(ImageView.ScaleType.FIT_XY);
        if (requestCode == SELECT_PICTURE) {
            if (data != null && data.getData() != null) {
                Uri imageURI = data.getData();
                Bitmap bmp = RotateIf(imageURI);
                imageURI = getImageUri(MainActivity.this, bmp);

                if (LAYOUT_FLAG == 0)
                    ImgTop.setImageURI(imageURI);
                else
                    ImgBottom.setImageURI(imageURI);
            } else if (resultCode == RESULT_OK) {
                Bitmap bmp = RotateIf(uriSavedImage);
                Uri imageURI = getImageUri(MainActivity.this, bmp);

                if (LAYOUT_FLAG == 0)
                    ImgTop.setImageURI(imageURI);
                else
                    ImgBottom.setImageURI(imageURI);
            }
        }
    }

    public Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }


    Uri uriSavedImage;
    private static final int SELECT_PICTURE = 1;

    private void SelectPicture() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File image = new File(Environment.getExternalStorageDirectory(), "DarrenPic.png");
        uriSavedImage = Uri.fromFile(image);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        String pickTitle = "Upload Picture";
        Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
        chooserIntent.putExtra
                (
                        Intent.EXTRA_INITIAL_INTENTS,
                        new Intent[]{takePhotoIntent}
                );

        startActivityForResult(chooserIntent, SELECT_PICTURE);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void SetLayout() {
        layout_id = settings.getInt("layoutid", Constant.LAYOUT_ONE);
        SetLinearLayout();
    }
}
