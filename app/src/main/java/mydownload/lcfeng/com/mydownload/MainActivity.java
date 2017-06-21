package mydownload.lcfeng.com.mydownload;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    DownloadManager dm;
    String downloadFileUrl = "https://www.101apps.co.za/images/android/apps/Addiction_101/addiction_101_alcohol.jpg";
    //    String downloadFileUrl = "http://gdown.baidu.com/data/wisegame/55dc62995fe9ba82/jinritoutiao_448.apk";
    private long myDownloadReference;
    private BroadcastReceiver receiverComplete;
    private BroadcastReceiver receiverNotifiClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDate();
    }

    private void initDate() {
        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在下载过程中，点击通知栏的处理
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        receiverNotifiClicked = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String extraId = DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS;
                long[] references = intent.getLongArrayExtra(extraId);
                for (long reference : references) {
                    if (reference == myDownloadReference) {
                        Toast.makeText(MainActivity.this, "lalalalalala", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        registerReceiver(receiverNotifiClicked, filter);

        //下载完成过后，想要做的处理
        IntentFilter filter2 = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        receiverComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long extra = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (extra == myDownloadReference) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(myDownloadReference);
                    Cursor cursor = dm.query(query);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(dm.COLUMN_STATUS);
                    int status = cursor.getInt(columnIndex);

                    int fileNameIndex = cursor.getColumnIndex(dm.COLUMN_LOCAL_FILENAME);
                    String saveFilePath = cursor.getString(fileNameIndex);

                    int columnReason = cursor.getColumnIndex(dm.COLUMN_REASON);
                    int reason = cursor.getInt(columnReason);
                    cursor.close();

                    switch (status) {
                        case DownloadManager.STATUS_SUCCESSFUL:
                            Intent intent1 = new Intent(MainActivity.this, DisplayActivity.class);
                            intent1.putExtra("uri", saveFilePath);
                            startActivity(intent1);
                            //下载好apk进行安装
//                            Intent intentapk = new Intent(Intent.ACTION_VIEW);
//                            intentapk.setDataAndType(Uri.fromFile(new File(saveFilePath)),"application/vnd.android.package-archive");
//                            startActivity(intentapk);
                            break;
                        case DownloadManager.STATUS_FAILED:
                            Toast.makeText(MainActivity.this, "FAILED:" + reason, Toast.LENGTH_SHORT).show();
                            break;
                        case DownloadManager.STATUS_PAUSED:
                            Toast.makeText(MainActivity.this, "PAUSED:" + reason, Toast.LENGTH_SHORT).show();
                            break;
                        case DownloadManager.STATUS_PENDING:
                            Toast.makeText(MainActivity.this, "PENDING:" + reason, Toast.LENGTH_SHORT).show();
                            break;
                        case DownloadManager.STATUS_RUNNING:
                            Toast.makeText(MainActivity.this, "RUNNING:" + reason, Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        };
        registerReceiver(receiverComplete, filter2);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //释放广播
        unregisterReceiver(receiverComplete);
        unregisterReceiver(receiverNotifiClicked);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_download:
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadFileUrl));
                request.setDescription("我的图片");
                request.setTitle("mypage");
                request.allowScanningByMediaScanner();
//                request.setVisibleInDownloadsUi(true);// default
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);//default
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "lcfeng.jpg");
//                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "lcfeng.apk");

                myDownloadReference = dm.enqueue(request);

                break;
            case R.id.btn_check:
                ConnectivityManager conManager = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = conManager.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null
                        && activeNetwork.isConnectedOrConnecting();

                if (isConnected) {
                    boolean isWiFi = activeNetwork.getType() == conManager.TYPE_WIFI;
                    boolean isMobile = activeNetwork.getType() == conManager.TYPE_MOBILE;
                    if (isWiFi) {
                        Toast.makeText(MainActivity.this, "Connected via WiFi",
                                Toast.LENGTH_SHORT).show();
                    } else if (isMobile) {
                        Toast.makeText(MainActivity.this, "Connected via Mobile",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "No Connection",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
