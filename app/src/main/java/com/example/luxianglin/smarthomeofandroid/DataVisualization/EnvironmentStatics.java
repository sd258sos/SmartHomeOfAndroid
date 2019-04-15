package com.example.luxianglin.smarthomeofandroid.DataVisualization;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.example.luxianglin.smarthomeofandroid.Layout.MainActivity;
import com.example.luxianglin.smarthomeofandroid.R;
import com.example.luxianglin.smarthomeofandroid.Socket.DataVisualService;
import com.example.luxianglin.smarthomeofandroid.Socket.NetService;
import com.example.luxianglin.smarthomeofandroid.Util.CustomDialog;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lecho.lib.hellocharts.formatter.ColumnChartValueFormatter;
import lecho.lib.hellocharts.formatter.SimpleColumnChartValueFormatter;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;

public class EnvironmentStatics extends AppCompatActivity {
    private int year;
    private int month;
    private int day;
    private int hour, minute, second;
    private TextView ctime;
    public static String DateSet;
    private Spinner spinner1, spinner2;
    private ColumnChartView mColumnCharView1, mColumnCharView2, mColumnCharView3, mColumnCharView4;                //柱形图控件
    private ColumnChartData mColumnChartData;               //柱状图数据
    private boolean isHasColumnLabels = false;              //是否显示列标签
    private boolean isColumnsHasSelected = false;           //设置列点击后效果(消失/显示标签)
    private String[] YearSet = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",};
    ArrayAdapter<String> adapter1, adapter2;
    private String SpinnerDatas[] = {"温度", "湿度", "光照度", "烟雾", "燃气", "PM2.5", "CO2", "气压"};
    private String Scapes[] = {"最小值", "平均值", "最大值"};
    DataVisualService service;
    String TabSelected = "day";
    String SpinnerSelected1 = "STemp";
    String SpinnerSelected2 = "min";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_environment_statics);

        service = new DataVisualService(MainActivity.ip);
        service.setRun(true);
        new Thread(service).start();

        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        TabHost th = (TabHost) findViewById(R.id.tabhost);
        th.setup();            //初始化TabHost容器
        TabWidget tabWidget = th.getTabWidget();
        //在TabHost创建标签，然后设置：标题／图标／标签页布局
        th.addTab(th.newTabSpec("day").setIndicator("日", null).setContent(R.id.tab1));
        th.addTab(th.newTabSpec("week").setIndicator("周", null).setContent(R.id.tab2));
        th.addTab(th.newTabSpec("month").setIndicator("月", null).setContent(R.id.tab3));
        th.addTab(th.newTabSpec("year").setIndicator("年", null).setContent(R.id.tab4));
        th.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
                                       @Override
                                       public void onTabChanged(String s) {
                                           TabSelected = s;
                                           service.sendMsg("SelectData:" + DateSet + "," + s + "," + SpinnerSelected1);
                                           setColumnDatas(TabSelected);
                                       }
                                   }
        );
        for (int i = 0; i < tabWidget.getChildCount(); i++) {
            //修改Tabhost高度和宽度
            tabWidget.getChildAt(i).getLayoutParams().height = 80;
            tabWidget.getChildAt(i).getLayoutParams().width = 65;
            //修改显示字体大小
            TextView tv = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);
            tv.setTextSize(8);
            tv.setTextColor(this.getResources().getColorStateList(android.R.color.black));
        }
        ctime = (TextView) findViewById(R.id.currenttime);
        //初始化Calendar日历对象
        Calendar mycalendar = Calendar.getInstance(Locale.CHINA);
        Date mydate = new Date(); //获取当前日期Date对象
        mycalendar.setTime(mydate);////为Calendar对象设置时间为当前日期

        year = mycalendar.get(Calendar.YEAR); //获取Calendar对象中的年
        month = mycalendar.get(Calendar.MONTH);//获取Calendar对象中的月
        day = mycalendar.get(Calendar.DAY_OF_MONTH);//获取这个月的第几天
        hour = mycalendar.get(Calendar.HOUR_OF_DAY);
        minute = mycalendar.get(Calendar.MINUTE);
        second = mycalendar.get(Calendar.SECOND);
        ctime.setText(year + "-" + (month + 1) + "-" + day); //显示当前的年月日
        DateSet = year + "-" + (month + 1) + "-" + day + " " + hour + ":" + minute + ":" + second;
        //添加单击事件--设置日期
        ctime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /**
                 * 构造函数原型：
                 * public DatePickerDialog (Context context, DatePickerDialog.OnDateSetListener callBack,
                 * int year, int monthOfYear, int dayOfMonth)
                 * content组件运行Activity，
                 * DatePickerDialog.OnDateSetListener：选择日期事件
                 * year：当前组件上显示的年，monthOfYear：当前组件上显示的月，dayOfMonth：当前组件上显示的第几天
                 *
                 */
                //创建DatePickerDialog对象
                DatePickerDialog dpd = new DatePickerDialog(EnvironmentStatics.this, Datelistener, year, month, day);
                dpd.show();//显示DatePickerDialog组件
            }
        });

        mColumnCharView1 = (ColumnChartView) findViewById(R.id.ccv_day);
        mColumnCharView2 = (ColumnChartView) findViewById(R.id.ccv_week);
        mColumnCharView3 = (ColumnChartView) findViewById(R.id.ccv_month);
        mColumnCharView4 = (ColumnChartView) findViewById(R.id.ccv_year);

        adapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, SpinnerDatas);
        adapter1.setDropDownViewResource(android.
                R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        spinner1.setSelection(0, false);
        spinner1.setOnItemSelectedListener(itemSelectedListener);
        adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Scapes);
        adapter2.setDropDownViewResource(android.
                R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        spinner2.setSelection(0, false);
        spinner2.setOnItemSelectedListener(itemSelectedListener2);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        service.sendMsg("SelectData:" + DateSet + "," + TabSelected + "," + SpinnerSelected1);
        setColumnDatas(TabSelected);
    }

    private DatePickerDialog.OnDateSetListener Datelistener = new DatePickerDialog.OnDateSetListener() {
        /**params：view：该事件关联的组件
         * params：myyear：当前选择的年
         * params：monthOfYear：当前选择的月
         * params：dayOfMonth：当前选择的日
         */
        @Override
        public void onDateSet(DatePicker view, int myyear, int monthOfYear, int dayOfMonth) {


            //修改year、month、day的变量值，以便以后单击按钮时，DatePickerDialog上显示上一次修改后的值
            year = myyear;
            month = monthOfYear;
            day = dayOfMonth;
            //更新日期
            updateDate();

        }

        //当DatePickerDialog关闭时，更新日期显示
        private void updateDate() {
            //在TextView上显示日期
            ctime.setText(year + "-" + (month + 1) + "-" + day);
            DateSet = year + "-" + (month + 1) + "-" + day + " " + hour + ":" + minute + ":" + second;
            service.sendMsg("SelectData:" + DateSet + "," + TabSelected + "," + SpinnerSelected1);
            setColumnDatas(TabSelected);
        }
    };


    private void setColumnDatas(String dataScope) {
        switch (dataScope) {
            case "day":
                //setColumnDatasByParams(mColumnCharView1, 1, 24);
                isAsyncWorking(new String[]{"mColumnCharView1", "1", "24"});
                break;
            case "week":
                //setColumnDatasByParams(mColumnCharView2, 1, 7);
                isAsyncWorking(new String[]{"mColumnCharView2", "1", "7"});

                break;
            case "month":
                //setColumnDatasByParams(mColumnCharView3, 1, 30);
                isAsyncWorking(new String[]{"mColumnCharView3", "1", "30"});

                break;
            case "year":
                //setColumnDatasByParams(mColumnCharView4, 1, 12);
                isAsyncWorking(new String[]{"mColumnCharView4", "1", "12"});

                break;
            default:
                //setColumnDatasByParams(mColumnCharView1, 1, 24);
                isAsyncWorking(new String[]{"mColumnCharView1", "1", "24"});

                break;
        }
    }

    private void setTargetColumn(String Date, String dataType, int dataScope) {//选取某一类型的数据生成柱状图

    }

    /**
     * 根据不同的参数 决定绘制什么样的柱状图
     *
     * @param numSubcolumns 子列数
     * @param numColumns    总列数
     */
    private void setColumnDatasByParams(final ColumnChartView currentcolumn, final int numSubcolumns, final int numColumns) {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if (!service.ReturnResult().equals("")) {
                    String result = service.ReturnResult();
                    String[] results = result.split("@");
                    int listsize = results.length;
                    List<AxisValue> axisValues = new ArrayList<>();
                    String[] labels = new String[listsize];
                    Float[] datas = new Float[listsize];
                    List<Column> columns = new ArrayList<>();
                    List<SubcolumnValue> values;

                    for (int z = 0; z < listsize; z++) {
                        String[] ss = results[z].split("#");
                        switch (TabSelected) {
                            case "day":
                                labels[z] = ss[0].substring(11, 16);
                                break;
                            case "week":
                                labels[z] = ss[0].substring(5, 10);
                                break;
                            case "month":
                                labels[z] = ss[0].substring(5, 10);
                                break;
                            case "year":
                                labels[z] = ss[0].substring(0, 7);
                                break;
                        }
                        String[] alldata = ss[2].split(",");
                        switch (SpinnerSelected2) {
                            case "min":
                                datas[z] = Float.parseFloat(alldata[0]);
                                break;
                            case "mean":
                                datas[z] = Float.parseFloat(alldata[1]);
                                break;
                            case "max":
                                datas[z] = Float.parseFloat(alldata[2]);
                                break;
                        }

                    }
                    //双重for循环给每个子列设置随机的值和随机的颜色
                    for (int i = 0; i < numColumns; i++) {
                        values = new ArrayList<>();
                        for (int j = 0; j < numSubcolumns; j++) {

                            //根据反向值 设置列的值
                            values.add(new SubcolumnValue(datas[i], ChartUtils.pickColor()));
                        }

                        axisValues.add(new AxisValue(i).setLabel(labels[i]));

            /*===== 柱状图相关设置 =====*/
                        Column column = new Column(values);
                        //让图表能够显示小数点
                        ColumnChartValueFormatter chartValueFormatter = new SimpleColumnChartValueFormatter(1);
                        column.setFormatter(chartValueFormatter);
                        column.setHasLabels(false);
                        column.setHasLabelsOnlyForSelected(true);  //点击只放大
                        columns.add(column);
                    }
                    mColumnChartData = new ColumnChartData(columns);               //设置数据
        /*===== 坐标轴相关设置 类似于Line Charts =====*/
                    Axis axisX = new Axis(axisValues);
                    mColumnChartData.setAxisXBottom(axisX);
                    currentcolumn.setZoomEnabled(false);
                    currentcolumn.setValueSelectionEnabled(true);
                    currentcolumn.setColumnChartData(mColumnChartData);
                }
            }
        });

        t.start();
    }

    private void selectData(String Date, String dataType, int dataScope) {//从数据库获取柱状图数据

    }

    /**
     * 柱形图改变时的动画(需要判断当前是否是反向)
     *
     * @param isNegative 反向标记位
     */
    private void changeColumnsAnimate(ColumnChartView mColumnCharView, boolean isNegative) {
        //增强for循环改变列数据
        for (Column column : mColumnChartData.getColumns()) {
            for (SubcolumnValue value : column.getValues()) {
                //根据当前反向标志位来重新绘制
                value.setTarget((float) Math.random() * 100);
            }
        }
        mColumnCharView.startDataAnimation();               //开始动画
    }


    /**
     * 子列触摸监听
     */
    private class ValueTouchListener implements ColumnChartOnValueSelectListener {

        @Override
        public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
            Toast.makeText(getApplicationContext(), "当前列的值约为 " + (int) value.getValue(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {

        }

    }

    private AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            switch (position) {
                case 0:
                    SpinnerSelected1 = "STemp";
                    service.sendMsg("SelectData:" + DateSet + "," + TabSelected + "," + SpinnerSelected1);
                    break;
                case 1:
                    SpinnerSelected1 = "SHumidity";
                    service.sendMsg("SelectData:" + DateSet + "," + TabSelected + "," + SpinnerSelected1);
                    break;
                case 2:
                    SpinnerSelected1 = "SIllumination";
                    service.sendMsg("SelectData:" + DateSet + "," + TabSelected + "," + SpinnerSelected1);
                    break;
                case 3:
                    SpinnerSelected1 = "SSmoke";
                    service.sendMsg("SelectData:" + DateSet + "," + TabSelected + "," + SpinnerSelected1);
                    break;
                case 4:
                    SpinnerSelected1 = "SGas";
                    service.sendMsg("SelectData:" + DateSet + "," + TabSelected + "," + SpinnerSelected1);
                    break;
                case 5:
                    SpinnerSelected1 = "SPM25";
                    service.sendMsg("SelectData:" + DateSet + "," + TabSelected + "," + SpinnerSelected1);
                    break;
                case 6:
                    SpinnerSelected1 = "SCO2";
                    service.sendMsg("SelectData:" + DateSet + "," + TabSelected + "," + SpinnerSelected1);
                    break;
                case 7:
                    SpinnerSelected1 = "SAirpressure";
                    service.sendMsg("SelectData:" + DateSet + "," + TabSelected + "," + SpinnerSelected1);
                    break;
                default:
                    SpinnerSelected1 = "STemp";
                    service.sendMsg("SelectData:" + DateSet + "," + TabSelected + "," + SpinnerSelected1);
                    break;
            }

            setColumnDatas(TabSelected);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            Toast.makeText(getApplicationContext(), "请选择查询数据类别", Toast.LENGTH_SHORT).show();
        }
    };
    private AdapterView.OnItemSelectedListener itemSelectedListener2 = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            switch (position) {
                case 0:
                    SpinnerSelected2 = "min";

                    break;
                case 1:
                    SpinnerSelected2 = "mean";

                    break;
                case 2:
                    SpinnerSelected2 = "max";

                    break;
                default:
                    SpinnerSelected2 = "min";

                    break;
            }
            DataVisualService.isReceive = true;
            setColumnDatas(TabSelected);

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            Toast.makeText(getApplicationContext(), "请选择查询数据类别", Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    protected void onStop() {
        super.onStop();
        service.sendMsg("exit");

        try {
            service.getReader().close();
            service.getsocket().close();
            service.setRun(false);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        service.sendMsg("exit");
        try {
            service.getReader().close();
            service.getsocket().close();
            service.setRun(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void isAsyncWorking(String[] param) {
        MyAsyncTask myAsyncTask = new MyAsyncTask(EnvironmentStatics.this);
        myAsyncTask.execute(param);

    }

    class MyAsyncTask extends AsyncTask<String, Integer, String> {
        CustomDialog dialog;

        Map<String, ColumnChartView> currentChartView;

        // 可变长的输入参数，与AsyncTask.exucute()对应
        public MyAsyncTask(Context context) {
            dialog = new CustomDialog(context);
            currentChartView = new HashMap<String, ColumnChartView>();
            currentChartView.put("mColumnCharView1", mColumnCharView1);
            currentChartView.put("mColumnCharView2", mColumnCharView2);
            currentChartView.put("mColumnCharView3", mColumnCharView3);
            currentChartView.put("mColumnCharView4", mColumnCharView4);
        }

        @Override
        protected String doInBackground(String... params) {
            while (true) {
                if (DataVisualService.isReceive) {
                    setColumnDatasByParams(currentChartView.get(params[0]), Integer.parseInt(params[1]), Integer.parseInt(params[2]));
                    DataVisualService.isReceive = false;
                    return null;
                }
            }
        }

        /**
         * 在doInBackground 执行完成后，onPostExecute方法将被UI thread调用，后台的计算结果将通过该方法传递到UI thread.
         */
        @Override
        protected void onPostExecute(String bitmap) {
            dialog.dismiss();
        }

        /**
         * 该方法将在执行实际的后台操作前被UI thread调用。这个方法只是做一些准备工作，如在界面上显示一个进度条。
         */
        @Override
        protected void onPreExecute() {
            // 任务启动
            dialog.show();
        }
    }
}
