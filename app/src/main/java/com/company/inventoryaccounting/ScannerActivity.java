package com.company.inventoryaccounting;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ScannerActivity extends AppCompatActivity{

    Map<String, String> instrumentBarcode = new HashMap<String, String>();
    String instrumentID, equipBarcode;
    Toast myToast;
    SurfaceView surfaceView;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;

    private String onChangeBarcodeBtn = "";
    private Boolean flashlightMode;
    private Camera camera = null;
    //private CameraManager camManager;
    //private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        Toolbar toolbar = findViewById(R.id.scanner_tool_bar);
        setSupportActionBar(toolbar);
        surfaceView = findViewById(R.id.surfaceView);
        if (getIntent().getExtras() != null) {
            onChangeBarcodeBtn = getIntent().getExtras().getString("onChangeBarcodeBtn");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        flashlightMode = false;
        fillBarCodeDictionary();
        final Activity thisActivity = this;
        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build();
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() { }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                SparseArray<Barcode> barcodes =  detections.getDetectedItems();
                if(barcodes.size()!=0){
                    Intent intent = new Intent(".ChangeInventoryActivity");
                    if(onChangeBarcodeBtn.equals("true")){/*Поведение активити если была нажата кнопка изменения штрих кода (в окне изменения инвентаря)*/
                        if(instrumentBarcode.containsValue(barcodes.valueAt(0).rawValue)){// если существует инструмент с такми штрихкодом то пишем
                            thisActivity.runOnUiThread(new Runnable() {// запуск в Ui потоке основной активити
                                @Override
                                public void run() {
                                    if (myToast != null) {
                                        myToast.cancel();
                                    }
                                    myToast = Toast.makeText(thisActivity, "Инвентарь с таким штрих-кодом уже существует", Toast.LENGTH_SHORT);
                                    TextView v = (TextView) myToast.getView().findViewById(android.R.id.message);
                                    if( v != null) v.setGravity(Gravity.CENTER);
                                    myToast.show();
                                }
                            });
                        }
                        else if(((GlobalInventoryaccounting) thisActivity.getApplication()).isAdmin()){// если не существует то открываем админу активити для создания нового инструмента
                            ((GlobalInventoryaccounting) thisActivity.getApplication()).setNewEquipBarcode(barcodes.valueAt(0).rawValue); // передаем новый Barcode
                            //intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
                            startActivity(intent);
                        }
                    }
                    else {/*Поведение активити если была нажата кнопка СКАНИРОВАТЬ (в главном меню)*/
                        if(instrumentBarcode.containsValue(barcodes.valueAt(0).rawValue)){// если существует инструмент с такми штрихкодом открываем его
                            intent.putExtra("idInstrument", getKeyByValue(instrumentBarcode, barcodes.valueAt(0).rawValue));
                            //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(intent);
                        }
                        else if(((GlobalInventoryaccounting) thisActivity.getApplication()).isAdmin()){// если не существует то открываем админу активити для создания нового инструмента
                            intent.putExtra("idInstrument", "newEquip");
                            intent.putExtra("equipBarcode",  barcodes.valueAt(0).rawValue);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(intent);
                        }
                        else {
                            thisActivity.runOnUiThread(new Runnable() {// запуск в Ui потоке основной активити
                                @Override
                                public void run() {
                                    if (myToast != null) {
                                        myToast.cancel();
                                    }
                                    myToast = Toast.makeText(thisActivity, "Инвентарь не найден", Toast.LENGTH_SHORT);// если не существует то пишем пользователю:"Инвентарь с таким штрих кодом не найден"
                                    myToast.show();
                                }
                            });
                        }
                    }
                }
            }
        });
        //barcodeDetector.setProcessor(this);
        cameraSource = new CameraSource.Builder(getApplicationContext(), barcodeDetector).setRequestedPreviewSize(1024,768).setRequestedFps(20.0f).setAutoFocusEnabled(true).build();
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try{
                    if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(thisActivity, new String[]{Manifest.permission.CAMERA}, 1024);
                        return;
                    }
                    cameraSource.start(surfaceView.getHolder());
                }
                catch (IOException ie)
                {
                    Log.e("Camera start problem", ie.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) { // выполняем при создании меню
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {// если существует фонарик добавляем меню
            MenuInflater inflater = getMenuInflater(); // подключаем меню
            inflater.inflate(R.menu.scanner_menu, menu);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {// выполняем когда выбран определенный пункт меню
        if (item.getItemId() == R.id.flashlightOnOffBtn) {
            flashButton();
            return true;
        }
        return true; //default: return super.onOptionsItemSelected(item); //super - текущий экземпляр родительского класса.
    }

    protected void onStop() {
        super.onStop();
        if(flashlightMode){
            flashButton();
            flashlightMode = false;
        }
    }

    /*private void flashButton(boolean state) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                camManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
                String cameraId = null; // Usually front camera is at 0 position.
                if (camManager != null) {
                    cameraId = camManager.getCameraIdList()[0];
                    camManager.setTorchMode(cameraId, state);
                    flashlightMode = !flashlightMode;
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }*/

    private void flashButton() {
        camera=getCamera(cameraSource);
        if (camera != null) {
            try {
                Camera.Parameters param = camera.getParameters();
                param.setFlashMode(!flashlightMode ?Camera.Parameters.FLASH_MODE_TORCH :Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(param);
                flashlightMode = !flashlightMode;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static Camera getCamera(@NonNull CameraSource cameraSource) {
        Field[] declaredFields = CameraSource.class.getDeclaredFields();

        for (Field field : declaredFields) {
            if (field.getType() == Camera.class) {
                field.setAccessible(true);
                try {
                    Camera camera = (Camera) field.get(cameraSource);
                    if (camera != null) {
                        return camera;
                    }
                    return null;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return null;
    }


    private void fillBarCodeDictionary(){ // получаем категории
        instrumentBarcode.clear();
        try {
            JSONObject jsonObject = new JSONObject(((GlobalInventoryaccounting) this.getApplication()).getEquipmentData());
            JSONArray jsonArray = jsonObject.getJSONArray("server_response");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                instrumentID = jsonObj.getString("id");
                equipBarcode = jsonObj.getString("barcode");
                instrumentBarcode.put(instrumentID, equipBarcode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}


            /*final StringBuilder sb = new StringBuilder();
            for (int i=0; i<barcodes.size(); i++){
                sb.append(barcodes.valueAt(i).rawValue).append("\n");
            }
            /*textView.post(new Runnable() {
                @Override
                public void run() {
                    textView.setText(sb.toString());
                }
            });*/

        /*ImageView myImageView = (ImageView) findViewById(R.id.imgview);
        Bitmap myBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.puppy);
        myImageView.setImageBitmap(myBitmap);
        BarcodeDetector detector = new BarcodeDetector.Builder(getApplicationContext()).setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE).build();
        if(!detector.isOperational()){//we need to check if our detector is operational before we use it. If it isn't, we may have to wait for a download to complete, or let our users know that they need to find an internet connection or clear some space on their device.
            textView.setText("Could not set up the detector!");
            return;
        }
        Frame frame = new Frame.Builder().setBitmap(myBitmap).build();//it creates a frame from the bitmap, and passes it to the detector. This returns a SparseArray of barcodes.
        SparseArray<Barcode> barcodes = detector.detect(frame);
        Barcode thisCode = barcodes.valueAt(0);
        TextView txtView = (TextView) findViewById(R.id.txtContent);
        txtView.setText(thisCode.rawValue);*/