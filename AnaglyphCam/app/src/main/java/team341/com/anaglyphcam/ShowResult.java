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
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;


public class ShowResult extends ActionBarActivity {
    ImageView result;
    ImageButton left;
    ImageButton right;
    String path;
    Uri contentUri;
    int n;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);

        result = (ImageView) findViewById(R.id.res);
        path = getIntent().getStringExtra("result");
        Bitmap res_bi = BitmapFactory.decodeFile(path);
        result.setImageBitmap(res_bi);

    }
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
        startActivity(Intent.createChooser(shareIntent, "3D picture"));
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
