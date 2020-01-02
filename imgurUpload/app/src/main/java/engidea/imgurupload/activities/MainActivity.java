package engidea.imgurupload.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;

import engidea.imgurupload.R;
import engidea.imgurupload.fragments.ImageItemListFragment;
import engidea.imgurupload.fragments.UrlListFragment;
import engidea.imgurupload.model.ImageItemContent;
import engidea.imgurupload.model.ImgurDB;
import engidea.imgurupload.model.NameToUrl;
import engidea.imgurupload.model.imgurmodel.ImageResponse;
import engidea.imgurupload.model.imgurmodel.Upload;
import engidea.imgurupload.services.UploadService;
import engidea.imgurupload.utils.IntentUtils;
import engidea.imgurupload.utils.aLog;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity implements
        ImageItemListFragment.OnImageListFragmentListener,
        UrlListFragment.OnImageUrlFragmentListener {

    private final static String TAG = "ei_i";
    private static final int PERM_READ_EXT = 1;
    private static final int COLUMNS_PORTRAIT = 3;
    private static final int COLUMNS_LANDSCAPE = 5;

    private Upload upload; // Upload object containing image and meta data
    private File chosenFile;
    private ImgurDB imgurDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.action_bar_text));
        setSupportActionBar(toolbar);

        imgurDB = new ImgurDB(this);

        showImagesFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_list_uploads:
                showUrlListFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragMngr = getSupportFragmentManager();
        if (fragMngr.getBackStackEntryCount() > 0) {
            fragMngr.popBackStack();
            setHomeAsEmpty();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            showImagesFragment();
        } else {
            showImagesFragment();
            showUrlListFragment();
        }
    }

    private void setHomeAsArrowBack(@SuppressWarnings("SameParameterValue") @StringRes int idTitle) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            actionBar.setTitle(idTitle);
        }
    }

    private void setHomeAsEmpty() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setTitle(R.string.app_name);
        }
    }

    private void showImagesFragment() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERM_READ_EXT);

        } else {
            FragmentManager fragMngr = getSupportFragmentManager();
            for (int i = 0; i < fragMngr.getBackStackEntryCount(); i++) {
                fragMngr.popBackStack();
            }
            setHomeAsEmpty();

            int columns = (getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_PORTRAIT) ? COLUMNS_PORTRAIT : COLUMNS_LANDSCAPE;
            ImageItemListFragment imageItemListFragment = ImageItemListFragment.newInstance(columns);
            fragMngr.beginTransaction()
                    .replace(R.id.main_content, imageItemListFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commitAllowingStateLoss();
        }
    }

    private void showUrlListFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, new UrlListFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commitAllowingStateLoss();
        setHomeAsArrowBack(R.string.title_uploaded_images);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == PERM_READ_EXT && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImagesFragment();
            } else {
                aLog.w(TAG, "Permissions not granted");
                finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onImageListInteraction(ImageItemContent.ImageItem item,
                                       ImageItemListFragment.OnInteractionDone callback) {

        chosenFile = new File(item.path);
        tryUploadImage(callback);
    }

    @Override
    public void onImageUrlFragmentInteraction(NameToUrl item) {
        IntentUtils.openInNativeBrowser(this, item.url);
    }

    public void tryUploadImage(ImageItemListFragment.OnInteractionDone interactionDone) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERM_READ_EXT);

        } else {
            uploadImage(interactionDone);
        }
    }


    public void uploadImage(ImageItemListFragment.OnInteractionDone interactionDone) {
        if (chosenFile == null) {
            return;
        }

        upload = new Upload();
        upload.image = chosenFile;
        upload.title = chosenFile.getName();
        upload.description = chosenFile.getName();

        ServiceConnection servConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                if (iBinder instanceof UploadService.LocalBinder) {
                    ((UploadService.LocalBinder) iBinder).getService().execute(upload,
                            new UiCallback(interactionDone, this));
                }
            }
            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Snackbar.make(findViewById(R.id.rootView), R.string.err_no_serv_conn,
                        Snackbar.LENGTH_LONG).show();
            }
        };

        bindService(new Intent(this, UploadService.class), servConn, BIND_AUTO_CREATE);
    }

    private class UiCallback implements Callback<ImageResponse> {
        private ImageItemListFragment.OnInteractionDone interactionDone;
        private ServiceConnection servConn;

        UiCallback(ImageItemListFragment.OnInteractionDone interactionDone, ServiceConnection servConn) {
            this.interactionDone = interactionDone;
            this.servConn = servConn;
        }

        @Override
        public void success(ImageResponse imageResponse, Response response) {
            aLog.w(TAG, "ImageResponse: " + imageResponse);
            interactionDone.onInteractionDone(true);
            unbindService(servConn);

            imgurDB.insertUrl(imageResponse.data.title, imageResponse.data.link);
        }

        @Override
        public void failure(RetrofitError error) {
            aLog.w(TAG, "RetrofitError: " + error);
            interactionDone.onInteractionDone(false);
            unbindService(servConn);
            if (error == null) {
                Snackbar.make(findViewById(R.id.rootView), R.string.err_no_inet,
                        Snackbar.LENGTH_LONG).show();
            }
        }
    }
}
