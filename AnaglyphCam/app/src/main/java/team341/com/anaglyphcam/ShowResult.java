package team341.com.anaglyphcam;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.content.Context;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.database.Cursor;
import android.provider.MediaStore;


import java.io.File;
import java.util.ArrayList;


public class ShowResult extends ActionBarActivity {
    ImageView result;
    ImageButton left;
    ImageButton right;
    private Gallery gallery;
    String path;
    Uri contentUri;
    String dirPath;
    ArrayList<String> filesPathes;
    // variable for selection intent
    private final int PICKER = 1;
    // variable to store the currently selected image
    public static int currentPic = 0;
    // adapter for gallery view
    private ImageAdapter adapter;
    int n;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        filesPathes = new ArrayList<String>();
        gallery = (Gallery) findViewById(R.id.gallery);
        // create a new adapter
        adapter = new ImageAdapter(this);
        // set the gallery adapter
        gallery.setAdapter(adapter);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);

        result = (ImageView) findViewById(R.id.res);

        //get 3d picture file
        path = getIntent().getStringExtra("result");
        dirPath = path.substring(0, path.lastIndexOf(File.separator));
        addFilesFromDir(dirPath);
        Bitmap res_bi = BitmapFactory.decodeFile(path);
        result.setImageBitmap(res_bi);

        // set long click listener for each gallery thumbnail item
       /* gallery.setOnItemLongClickListener(new OnItemLongClickListener() {
            // handle long clicks
            public boolean onItemLongClick(AdapterView<?> parent, View v,
                                           int position, long id) {
                // take user to choose an image
                // update the currently selected position so that we assign the
                // imported bitmap to correct item
                currentPic = position;
                // take the user to their chosen image selection app (gallery or
                // file manager)
                Intent pickIntent = new Intent();
                pickIntent.setType("image/*");
                pickIntent.setAction(Intent.ACTION_GET_CONTENT);
                // we will handle the returned data in onActivityResult
                startActivityForResult(
                        Intent.createChooser(pickIntent, "Select Picture"),
                        PICKER);
                return true;
            }
        });*/

        gallery.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                result.setImageBitmap(adapter.getPic(position));
                // result.setImageResource(imgid[position]);

            }
        });

    }

    public void addFilesFromDir(String directoryName) {
        File directory = new File(directoryName);

// get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                Bitmap pic = null;
                pic = BitmapFactory.decodeFile(file.getAbsolutePath());
                filesPathes.add(file.getAbsolutePath());
                // pass bitmap to ImageAdapter to add to array
                adapter.addPic(pic);

                // redraw the gallery thumbnails to reflect the new addition
                gallery.setAdapter(adapter);
            }
        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            // check if we are returning from picture selection
            if (requestCode == PICKER) {

                // the returned picture URI
                Uri pickedUri = data.getData();

                // declare the bitmap
                Bitmap pic = null;
                // declare the path string
                String imgPath = "";

                // retrieve the string using media data
                String[] medData = { MediaStore.Images.Media.DATA };
                // query the data
                Cursor picCursor = managedQuery(pickedUri, medData, null, null,
                        null);
                if (picCursor != null) {
                    // get the path string
                    int index = picCursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    picCursor.moveToFirst();
                    imgPath = picCursor.getString(index);
                } else
                    imgPath = pickedUri.getPath();

                // if and else handle both choosing from gallery and from file
                // manager

                // if we have a new URI attempt to decode the image bitmap
                if (pickedUri != null) {

                    // set the width and height we want to use as maximum
                    // display
                    int targetWidth = 600;
                    int targetHeight = 400;

                    // sample the incoming image to save on memory resources

                    // create bitmap options to calculate and use sample size
                    BitmapFactory.Options bmpOptions = new BitmapFactory.Options();

                    // first decode image dimensions only - not the image bitmap
                    // itself
                    bmpOptions.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(imgPath, bmpOptions);

                    // work out what the sample size should be

                    // image width and height before sampling
                    int currHeight = bmpOptions.outHeight;
                    int currWidth = bmpOptions.outWidth;

                    // variable to store new sample size
                    int sampleSize = 1;

                    // calculate the sample size if the existing size is larger
                    // than target size
                    if (currHeight > targetHeight || currWidth > targetWidth) {
                        // use either width or height
                        if (currWidth > currHeight)
                            sampleSize = Math.round((float) currHeight
                                    / (float) targetHeight);
                        else
                            sampleSize = Math.round((float) currWidth
                                    / (float) targetWidth);
                    }
                    // use the new sample size
                    bmpOptions.inSampleSize = sampleSize;

                    // now decode the bitmap using sample options
                    bmpOptions.inJustDecodeBounds = false;

                    // get the file as a bitmap
                    pic = BitmapFactory.decodeFile(imgPath, bmpOptions);

                    // pass bitmap to ImageAdapter to add to array
                    adapter.addPic(pic);

                    // redraw the gallery thumbnails to reflect the new addition
                    gallery.setAdapter(adapter);

                    // display the newly selected image at larger size
                    result.setImageBitmap(pic);
                    // scale options
                    result.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }
            }
        }
        // superclass method
        super.onActivityResult(requestCode, resultCode, data);
    }*/

    public void Save(View v)
    {
        galleryAddPic();
        AlertDialog.Builder builder = new AlertDialog.Builder(ShowResult.this);
        builder.setMessage("File saved. Check the gallery");
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.show();
    }
    public void Share(View v)
    {
        galleryAddPic();
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, "Share 3D"));
    }
    public void galleryAddPic()
    {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

    }
    public void rotate_left(View v)
    {
        result.setRotation(result.getRotation()-90);
    }
    public void rotate_right(View v)
    {
        result.setRotation(result.getRotation()+90);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
