package com.grgbanking.ct;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.grgbanking.ct.cach.DataCach;
import com.grgbanking.ct.database.Person;
import com.grgbanking.ct.database.PersonTableHelper;
import com.grgbanking.ct.entity.PdaLoginMessage;
import com.grgbanking.ct.entity.PdaNetInfo;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends Activity {

    PopupMenu popupMenu;
    Menu menu;

    private SharedPreferences sp;
    ListView listView;
    ImageView saomiaoImageView;
    SimpleAdapter listItemAdapter;
    ArrayList<HashMap<String, Object>> listItem;
    Person person = null;
    private Context context;
    TextView mainTitle;
    Button mainBackButton = null;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this.getApplicationContext();
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        popupMenu = new PopupMenu(this, findViewById(R.id.popupmenu_btn));
        menu = popupMenu.getMenu();

        // 通过XML文件添加菜单项
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.popupmenu, menu);


        mainTitle = (TextView) findViewById(R.id.main_title_view);
        listView = (ListView) findViewById(R.id.ListView01);
//		saomiaoImageView=(ImageView) findViewById(R.id.saosao_button);
//		saomiaoImageView.setOnClickListener(saomiaoButtonclick);
        mainBackButton = (Button) findViewById(R.id.main_btn_back);

        // 生成动态数组，加入数据
        listItem = new ArrayList<HashMap<String, Object>>();
        // 生成适配器的Item和动态数组对应的元素
        listItemAdapter = new SimpleAdapter(this, listItem, R.layout.main_list_item, new String[]{"list_img", "list_title", "list_position", "list_worktime"}, new int[]{R.id.list_img, R.id.list_title, R.id.list_position, R.id.list_worktime});
        // 添加并且显示
        listView.setAdapter(listItemAdapter);

        person = PersonTableHelper.queryEntity(this);
//		String userId =person.getUser_id();
//		String name = person.getUser_name();

        List<NameValuePair> params = new ArrayList<NameValuePair>();
//		params.add(new BasicNameValuePair("userId", userId));
        showWaitDialog("正在加载中...");

        String netType = DataCach.netType;
        if (netType.equals(Constants.NET_COMMIT_TYPE_IN)) {
            mainTitle.setText("网点入库任务列表");
        } else {
            mainTitle.setText("网点出库任务列表");
        }

        loadLoginMessageCach();

        hideWaitDialog();


//		new HttpPostUtils(Constants.URL_GET_TASK_LIST, params,new UICallBackDao() {
//					@Override
//					public void callBack(ResultInfo resultInfo) {
////						if (resultInfo==null) {
////							hideWaitDialog();
////						}
////						JSONArray jsonArray = resultInfo.getJsonArray();
////						for (int i = 0; i < jsonArray.length(); i++) {
////							try {
////								JSONObject jsonObject = jsonArray.getJSONObject(i);
////								HashMap<String, Object> map = new HashMap<String, Object>();
////								map.put("list_img", R.drawable.task_icon_1);// 图像资源的ID
////								map.put("list_title",jsonObject.getString("branchName"));
////								map.put("list_position",jsonObject.getString("branchAddress"));
////								map.put("list_worktime",jsonObject.getString("worktime"));
////								listItem.add(map);
////								listItemAdapter.notifyDataSetChanged();
////							} catch (JSONException e) {
////								e.printStackTrace();
////							}
////							hideWaitDialog();
////						}
//						
//						if (DataCach.taskMap != null && DataCach.taskMap.size() > 0) {
//							Iterator it = DataCach.taskMap.keySet().iterator();
//							while (it.hasNext()) {
//								String key = (String)it.next();
//								HashMap<String, Object> map = DataCach.taskMap.get(key);
//								listItem.add(map);
//							}
//							
//						} else {
//							HashMap<String, Object> map1 = new HashMap<String, Object>();
//							map1.put("list_img", R.drawable.task_2);// 图像资源的ID
//							map1.put("list_title","幸福分社");
//							map1.put("list_position","款箱数量：10个");
//							map1.put("list_worktime","未完成");
//							
//							DataCach.taskMap.put("0", map1);
//							listItem.add(map1);
//							
//							HashMap<String, Object> map2 = new HashMap<String, Object>();
//							map2.put("list_img", R.drawable.task_2);// 图像资源的ID
//							map2.put("list_title","前进分社");
//							map2.put("list_position","款箱数量：5个");
//							map2.put("list_worktime","未完成");
//							
//							DataCach.taskMap.put("1", map2);
//							listItem.add(map2);
//							
//							HashMap<String, Object> map3 = new HashMap<String, Object>();
//							map3.put("list_img", R.drawable.task_2);// 图像资源的ID
//							map3.put("list_title","湾里联社");
//							map3.put("list_position","款箱数量：7个");
//							map3.put("list_worktime","未完成");
//							
//							DataCach.taskMap.put("2", map3);
//							listItem.add(map3);
//							
//							HashMap<String, Object> map4 = new HashMap<String, Object>();
//							map4.put("list_img", R.drawable.task_2);// 图像资源的ID
//							map4.put("list_title","翠岩信用社");
//							map4.put("list_position","款箱数量：8个");
//							map4.put("list_worktime","未完成");
//							
//							DataCach.taskMap.put("3", map4);
//							listItem.add(map4);
//							
//							HashMap<String, Object> map5 = new HashMap<String, Object>();
//							map5.put("list_img", R.drawable.task_2);// 图像资源的ID
//							map5.put("list_title","岭口信用社");
//							map5.put("list_position","款箱数量：8个");
//							map5.put("list_worktime","未完成");
//							
//							DataCach.taskMap.put("4", map5);
//							listItem.add(map5);
//							
//							HashMap<String, Object> map6 = new HashMap<String, Object>();
//							map6.put("list_img", R.drawable.task_2);// 图像资源的ID
//							map6.put("list_title","红谷滩信用社");
//							map6.put("list_position","款箱数量：11个");
//							map6.put("list_worktime","未完成");
//							
//							DataCach.taskMap.put("5", map6);
//							listItem.add(map6);
//							
//							HashMap<String, Object> map7 = new HashMap<String, Object>();
//							map7.put("list_img", R.drawable.task_2);// 图像资源的ID
//							map7.put("list_title","太平信用社");
//							map7.put("list_position","款箱数量：2个");
//							map7.put("list_worktime","未完成");
//							
//							DataCach.taskMap.put("6", map7);
//							listItem.add(map7);
//						}
//						listItemAdapter.notifyDataSetChanged();
//						hideWaitDialog();
//					}
//				}).execute();

        // 添加点击
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {

                if (DataCach.taskMap.get(arg2 + "") != null) {
                    HashMap<String, Object> map = DataCach.taskMap.get(arg2 + "");
                    if (map.get("list_worktime").equals("已完成")) {
                        Toast.makeText(context, "该任务已完成", Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, DetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("count", arg2);
                        intent.putExtra("bundle", bundle);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });

        // 点击返回按钮操作内容
        mainBackButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //清空缓存
                DataCach.clearAllDataCach();

                Intent intent = new Intent();
                intent.setClass(MainActivity.this, NetOutInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.item_back:
//						Toast.makeText(MainActivity.this, "退出",
//								Toast.LENGTH_LONG).show();
//			startActivity(new Intent(getApplicationContext(), CaptureActivity.class));
                        //清空缓存
                        DataCach.clearAllDataCach();

                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                }

                return false;
            }
        });
    }

    public void popupmenu(View v) {
        popupMenu.show();
    }

    private void loadLoginMessageCach() {
        if (DataCach.taskMap != null && DataCach.taskMap.size() > 0) {
            Iterator it = DataCach.taskMap.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                HashMap<String, Object> map = DataCach.taskMap.get(key);
                listItem.add(map);
            }

        } else {
            PdaLoginMessage plm = DataCach.getPdaLoginMessage();
            if (plm != null) {

                List<PdaNetInfo> netInfoList = plm.getNetInfoList();
                if (netInfoList != null && netInfoList.size() > 0) {

                    for (int i = 0; i < netInfoList.size(); i++) {

                        PdaNetInfo pni = netInfoList.get(i);

                        int count = 0;
                        if (pni.getCashBoxInfoList() != null) {
                            count = pni.getCashBoxInfoList().size();
                        }

                        HashMap<String, Object> map1 = new HashMap<String, Object>();

                        //判断网点是否已完成
                        if (pni.getNetTaskStatus().equals(Constants.NET_TASK_STATUS_FINISH)) {

                            map1.put("list_img", R.drawable.task_1);// 图像资源的ID
                            map1.put("list_title", pni.getBankName());
                            map1.put("list_position", count);
                            map1.put("list_worktime", "已完成");
                            map1.put("data", pni);
                        } else {

                            map1.put("list_img", R.drawable.task_2);// 图像资源的ID
                            map1.put("list_title", pni.getBankName());
                            map1.put("list_position", count);
                            map1.put("list_worktime", "未完成");
                            map1.put("data", pni);
                        }

                        DataCach.taskMap.put("" + i, map1);
                        listItem.add(map1);
                    }
                }
            }
        }

        listItemAdapter.notifyDataSetChanged();
    }

    private ProgressDialog pd = null;

    private void showWaitDialog(String msg) {
        if (pd == null) {
            pd = new ProgressDialog(this);
        }
        pd.setCancelable(false);
        pd.setMessage(msg);
        pd.show();
    }

    private void hideWaitDialog() {
        if (pd != null) {
            pd.cancel();
        }
    }


    OnClickListener saomiaoButtonclick = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
//			startActivity(new Intent(getApplicationContext(), CaptureActivity.class));
            //清空缓存
            DataCach.clearAllDataCach();

            Intent intent = new Intent();
            intent.setClass(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    };
}
