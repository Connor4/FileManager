package com.example.fileman1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

public class MainActivity extends Activity {

	private ViewPager mPager;// 页卡内容
	private List<View> listViews; // Tab页面列表
	private ImageView cursor;// 动画图片
	private TextView t1, t2;// 页卡头标
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度

	private ListView listView1;
	private ListView listView2;
	// 起始目录“/”
	private String mRootPath = java.io.File.separator;
	// SD卡根目录
	private String mSDCard = Environment.getExternalStorageDirectory()
			.toString();
	// 用静态变量存储 当前目录路径信息
	public static String mCurrentFilePath = "";
	// 存放显示的文件列表的相对应的路径
	private List<String> RootPath = null;
	private List<String> SDCardPath = null;
	// 代表手机或SD卡，1代表手机，2代表SD卡
	private static int menuPosition = 1;
	private boolean isback = true;
	private String mOldFilePath = "";
	private String mNewFilePath = "";
	private boolean hasSDCard = false;
	private final String[][] MIME_MapTable = {
			// {后缀名， MIME类型}
			{ ".3gp", "video/3gpp" },
			{ ".apk", "application/vnd.android.package-archive" },
			{ ".asf", "video/x-ms-asf" }, { ".avi", "video/x-msvideo" },
			{ ".bin", "application/octet-stream" }, { ".bmp", "image/bmp" },
			{ ".c", "text/plain" }, { ".class", "application/octet-stream" },
			{ ".conf", "text/plain" }, { ".cpp", "text/plain" },
			{ ".doc", "application/msword" },
			{ ".exe", "application/octet-stream" }, { ".gif", "image/gif" },
			{ ".gtar", "application/x-gtar" }, { ".gz", "application/x-gzip" },
			{ ".h", "text/plain" }, { ".htm", "text/html" },
			{ ".html", "text/html" }, { ".jar", "application/java-archive" },
			{ ".java", "text/plain" }, { ".jpeg", "image/jpeg" },
			{ ".jpg", "image/jpeg" }, { ".js", "application/x-javascript" },
			{ ".log", "text/plain" }, { ".m3u", "audio/x-mpegurl" },
			{ ".m4a", "audio/mp4a-latm" }, { ".m4b", "audio/mp4a-latm" },
			{ ".m4p", "audio/mp4a-latm" }, { ".m4u", "video/vnd.mpegurl" },
			{ ".m4v", "video/x-m4v" }, { ".mov", "video/quicktime" },
			{ ".mp2", "audio/x-mpeg" }, { ".mp3", "audio/x-mpeg" },
			{ ".mp4", "video/mp4" },
			{ ".mpc", "application/vnd.mpohun.certificate" },
			{ ".mpe", "video/mpeg" }, { ".mpeg", "video/mpeg" },
			{ ".mpg", "video/mpeg" }, { ".mpg4", "video/mp4" },
			{ ".mpga", "audio/mpeg" },
			{ ".msg", "application/vnd.ms-outlook" }, { ".ogg", "audio/ogg" },
			{ ".pdf", "application/pdf" }, { ".png", "image/png" },
			{ ".pps", "application/vnd.ms-powerpoint" },
			{ ".ppt", "application/vnd.ms-powerpoint" },
			{ ".prop", "text/plain" },
			{ ".rar", "application/x-rar-compressed" },
			{ ".rc", "text/plain" }, { ".rmvb", "audio/x-pn-realaudio" },
			{ ".rtf", "application/rtf" }, { ".sh", "text/plain" },
			{ ".tar", "application/x-tar" },
			{ ".tgz", "application/x-compressed" }, { ".txt", "text/plain" },
			{ ".wav", "audio/x-wav" }, { ".wma", "audio/x-ms-wma" },
			{ ".wmv", "audio/x-ms-wmv" },
			{ ".wps", "application/vnd.ms-works" }, { ".xml", "text/xml" },
			{ ".xml", "text/plain" }, { ".z", "application/x-compress" },
			{ ".zip", "application/zip" }, { "", "*/*" } };

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		InitText();
		InitImage();
		InitViewPager();
	}

	private void InitText() {
		t1 = (TextView) findViewById(R.id.text1);
		t2 = (TextView) findViewById(R.id.text2);
		t1.setOnClickListener(new MyOnClickListener(0)); 
		t2.setOnClickListener(new MyOnClickListener(1));
	}

	public class MyOnClickListener implements View.OnClickListener {

		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		public void onClick(View arg0) {
			mPager.setCurrentItem(index);
		}

	}

	private void InitImage() {
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.a)
				.getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / 2 - bmpW) / 2;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);// 设置动画初始位置
	}

	public void InitViewPager() {
		listViews = new ArrayList<View>();
		mPager = (ViewPager) findViewById(R.id.vPager);
		LayoutInflater mInflater = getLayoutInflater();
		listView1 = (ListView) (mInflater.inflate(R.layout.list1, null))
				.findViewById(R.id.storelist1);
		listView2 = (ListView) (mInflater.inflate(R.layout.list2, null))
				.findViewById(R.id.storelist2);

		listViews.add(listView1);
		listViews.add(listView2);
		hasSDCard = hasSDCard();
		if (hasSDCard) {
			SimpleAdapter SDCardAdapter = new SimpleAdapter(this,
					getSDCardData(mSDCard), R.layout.store_info, new String[] {
							"img", "info" }, new int[] { R.id.img, R.id.info });
			listView2.setAdapter(SDCardAdapter);
		} else {
			Toast.makeText(getApplicationContext(), "内存卡出错", Toast.LENGTH_SHORT)
					.show();

		}

		isback = true;
		SimpleAdapter RootAdapter = new SimpleAdapter(this,
				getRootData(mRootPath), R.layout.store_info, new String[] {
						"img", "info" }, new int[] { R.id.img, R.id.info });

		listView1.setAdapter(RootAdapter);
		listView1.setOnItemClickListener(new ItemClickListener1());
		listView2.setOnItemClickListener(new ItemClickListener2());
		listView1.setOnItemLongClickListener(new longClickListener());
		listView2.setOnItemLongClickListener(new longClickListener());

		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	private List<Map<String, Object>> getRootData(String filePath) {
		List<Map<String, Object>> rootlist = new ArrayList<Map<String, Object>>();
		int image;
		mCurrentFilePath = filePath;
		RootPath = new ArrayList<String>();
		File mFile = new File(filePath);
		// 遍历出该文件夹路径下的所有文件/文件夹
		File[] mFiles = mFile.listFiles();
		/* 将所有文件信息添加到集合中 */
		for (File mCurrentFile : mFiles) {
			Map<String, Object> map = new HashMap<String, Object>();
			String fileName = mCurrentFile.getName();
			if (!mCurrentFilePath.equals(mRootPath) && isback) { // isback作为标记，使每次添加一次
				Map<String, Object> map1 = new HashMap<String, Object>();
				map1.put("img", R.drawable.back_to_root);
				map1.put("info", "返回根目录");
				rootlist.add(map1);
				RootPath.add(0 + "");
				Map<String, Object> map2 = new HashMap<String, Object>();
				map2.put("img", R.drawable.back_to_up);
				map2.put("info", "返回上一层");
				rootlist.add(map2);
				RootPath.add(1 + "");
				isback = false;
			}
			String fileEnds = fileName.substring(fileName.lastIndexOf(".") + 1,
					fileName.length()).toLowerCase();// 取出文件后缀名并转成小写
			if (fileEnds.equals("m4a") || fileEnds.equals("mp3")
					|| fileEnds.equals("mid") || fileEnds.equals("xmf")
					|| fileEnds.equals("ogg") || fileEnds.equals("wav")) {
				image = R.drawable.video;
			} else if (fileEnds.equals("3gp") || fileEnds.equals("mp4")) {
				image = R.drawable.audio;
			} else if (fileEnds.equals("jpg") || fileEnds.equals("gif")
					|| fileEnds.equals("png") || fileEnds.equals("jpeg")
					|| fileEnds.equals("bmp")) {
				image = R.drawable.pic;
			} else if (fileEnds.equals("apk")) {
				image = R.drawable.apk;
			} else if (fileEnds.equals("txt")) {
				image = R.drawable.txt;
			} else if (fileEnds.equals("zip") || fileEnds.equals("rar")) {
				image = R.drawable.zip;
			} else if (fileEnds.equals("html") || fileEnds.equals("htm")
					|| fileEnds.equals("mht")) {
				image = R.drawable.web;
			} else if (mCurrentFile.isDirectory()) {
				image = R.drawable.folder;
			} else {
				image = R.drawable.others;
			}
			map.put("img", image);
			map.put("info", mCurrentFile.getName());
			rootlist.add(map);
			RootPath.add(mCurrentFile.getPath());
		}
		return rootlist;
	}

	private List<Map<String, Object>> getSDCardData(String filePath) {
		List<Map<String, Object>> sdcardlist = new ArrayList<Map<String, Object>>();
		int image;
		mCurrentFilePath = filePath;
		SDCardPath = new ArrayList<String>();
		File mFile = new File(filePath);
		// 遍历出该文件夹路径下的所有文件/文件夹
		File[] mFiles = mFile.listFiles();
		/* 将所有文件信息添加到集合中 */
		for (File mCurrentFile : mFiles) {
			Map<String, Object> map = new HashMap<String, Object>();
			String fileName = mCurrentFile.getName();
			if (!mCurrentFilePath.equals(mSDCard) && isback) {
				Map<String, Object> map1 = new HashMap<String, Object>();
				map1.put("img", R.drawable.back_to_root);
				map1.put("info", "返回根目录");
				sdcardlist.add(map1);
				SDCardPath.add(0 + "");
				Map<String, Object> map2 = new HashMap<String, Object>();
				map2.put("img", R.drawable.back_to_up);
				map2.put("info", "返回上一层");
				sdcardlist.add(map2);
				SDCardPath.add(1 + "");
				isback = false;
			}
			String fileEnds = fileName.substring(fileName.lastIndexOf(".") + 1,
					fileName.length()).toLowerCase();// 取出文件后缀名并转成小写
			if (fileEnds.equals("m4a") || fileEnds.equals("mp3")
					|| fileEnds.equals("mid") || fileEnds.equals("xmf")
					|| fileEnds.equals("ogg") || fileEnds.equals("wav")) {
				image = R.drawable.video;
			} else if (fileEnds.equals("3gp") || fileEnds.equals("mp4")) {
				image = R.drawable.audio;
			} else if (fileEnds.equals("jpg") || fileEnds.equals("gif")
					|| fileEnds.equals("png") || fileEnds.equals("jpeg")
					|| fileEnds.equals("bmp")) {
				image = R.drawable.pic;
			} else if (fileEnds.equals("apk")) {
				image = R.drawable.apk;
			} else if (fileEnds.equals("txt")) {
				image = R.drawable.txt;
			} else if (fileEnds.equals("zip") || fileEnds.equals("rar")) {
				image = R.drawable.zip;
			} else if (fileEnds.equals("html") || fileEnds.equals("htm")
					|| fileEnds.equals("mht")) {
				image = R.drawable.web;
			} else if (mCurrentFile.isDirectory()) {
				image = R.drawable.folder;
			} else {
				image = R.drawable.others;
			}
			map.put("img", image);
			map.put("info", mCurrentFile.getName());
			sdcardlist.add(map);
			SDCardPath.add(mCurrentFile.getPath());
		}
		return sdcardlist;
	}

	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {
		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		int two = one;// 页卡1 -> 页卡2 偏移量

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
					InitNewSDCardViewPager(mSDCard);
					menuPosition = 1;
				}
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, one, 0, 0);
					InitNewRootViewPager(mRootPath);
					menuPosition = 2;
				}
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			cursor.startAnimation(animation);
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}

	public class ItemClickListener1 implements OnItemClickListener {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			final File mFile = new File(RootPath.get(arg2));
			isback = true;
			if (mCurrentFilePath.equals(mRootPath)) {
				if (mFile.canRead()) {
					if (mFile.isDirectory()) {
						InitNewRootViewPager(RootPath.get(arg2) + "");
					}
				} else {
					Toast.makeText(getApplicationContext(), "can not open!",
							Toast.LENGTH_SHORT).show();
				}
			} else if (!mCurrentFilePath.equals(mRootPath)) {
				if (RootPath.get(arg2) == "0") {
					InitNewRootViewPager(mRootPath);
				} else if (RootPath.get(arg2) == "1") {
					InitNewRootViewPager(new File(mCurrentFilePath).getParent());
				} else if (mFile.canRead()) {
					if (mFile.isDirectory()) {
						isback = true;
						InitNewRootViewPager(RootPath.get(arg2) + "");
					} else {
						openFile(mFile);
					}
				} else {
					Toast.makeText(MainActivity.this, "访问权限不足!",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	public class ItemClickListener2 implements OnItemClickListener {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			final File mFile = new File(SDCardPath.get(arg2));
			isback = true;
			if (mCurrentFilePath.equals(SDCardPath)) {
				if (mFile.canRead()) {
					if (mFile.isDirectory()) {
						InitNewSDCardViewPager(SDCardPath.get(arg2) + "");
					}
				} else {
					Toast.makeText(
							getApplicationContext(),
							mCurrentFilePath + menuPosition + "can not open!!!",
							Toast.LENGTH_SHORT).show();
				}
			} else if (!mCurrentFilePath.equals(SDCardPath)) {
				if (SDCardPath.get(arg2) == "0") {
					InitNewSDCardViewPager(mSDCard);
				} else if (SDCardPath.get(arg2) == "1") {
					InitNewSDCardViewPager(new File(mCurrentFilePath)
							.getParent());
				} else if (mFile.canRead()) {
					if (mFile.isDirectory()) {
						isback = true;
						InitNewSDCardViewPager(SDCardPath.get(arg2) + "");
					} else {
						openFile(mFile);
					}
				} else {
					Toast.makeText(MainActivity.this, "访问权限不足!",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	private void InitNewRootViewPager(String path) {
		SimpleAdapter RootAdapter = new SimpleAdapter(this, getRootData(path),
				R.layout.store_info, new String[] { "img", "info" }, new int[] {
						R.id.img, R.id.info });

		listView1.setAdapter(RootAdapter);
		listView1.setOnItemClickListener(new ItemClickListener1());
		listView1.setOnItemLongClickListener(new longClickListener());
	}

	private void InitNewSDCardViewPager(String path) {
		SimpleAdapter SDCardAdapter = new SimpleAdapter(this,
				getSDCardData(path), R.layout.store_info, new String[] { "img",
						"info" }, new int[] { R.id.img, R.id.info });

		listView2.setAdapter(SDCardAdapter);
		listView2.setOnItemClickListener(new ItemClickListener2());
		listView2.setOnItemLongClickListener(new longClickListener());
	}

	private String mNewFolderName = "";
	private File mCreateFile;
	private String CurrentPath = mCurrentFilePath;

	private void createFolder() {
		// 显示对话框
		LayoutInflater mLI = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 初始化对话框布局
		final LinearLayout mLL = (LinearLayout) mLI.inflate(
				R.layout.create_dialog, null);
		new AlertDialog.Builder(MainActivity.this)
		        .setTitle("新建文件夹")
				.setView(mLL)
				.setPositiveButton("创建", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int i) {
						mNewFolderName = ((EditText) mLL
								.findViewById(R.id.new_filename)).getText()
								.toString();
						if (menuPosition == 1) {
							mCreateFile = new File(mCurrentFilePath
									+ java.io.File.separator + mNewFolderName);
							if (!mCreateFile.exists()
									&& !mCreateFile.isDirectory()
									&& mNewFolderName.length() != 0) {
								if (mCreateFile.mkdirs()) {
									// 刷新当前目录文件列表
									isback = true;
									InitNewRootViewPager(mCurrentFilePath);
									// InitNewSDCardViewPager(mCurrentFilePath);
								} else {
									Toast.makeText(
											MainActivity.this,
											mCurrentFilePath + ""
													+ menuPosition + "123",
											Toast.LENGTH_SHORT).show();
								}
							} else {
								Toast.makeText(MainActivity.this, "文件名不能为空",
										Toast.LENGTH_SHORT).show();
							}
						} else if (menuPosition == 2) {
							mCreateFile = new File(mCurrentFilePath
									+ java.io.File.separator + mNewFolderName);
							if (!mCreateFile.exists()
									&& !mCreateFile.isDirectory()
									&& mNewFolderName.length() != 0) {
								if (mCreateFile.mkdirs()) {
									// 刷新当前目录文件列表
									isback = true;
									InitNewSDCardViewPager(mCurrentFilePath);
									// InitNewRootViewPager(mCurrentFilePath);
								} else {
									Toast.makeText(
											MainActivity.this,
											mCurrentFilePath + ""
													+ menuPosition + "456",
											Toast.LENGTH_SHORT).show();
								}
							} else {
								Toast.makeText(MainActivity.this, "文件名不能为空",
										Toast.LENGTH_SHORT).show();
							}
						}
					}
				}).setNeutralButton(R.string.cancel, null).show();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 1, R.string.search);
		menu.add(0, 2, 2, R.string.palse);
		menu.add(0, 3, 3, R.string.build);
		menu.add(0, 4, 4, R.string.exit);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == 1) {
			searchFolder();
		} else if (item.getItemId() == 2) {
			palse();
		} else if (item.getItemId() == 3) {
			createFolder();
		} else if (item.getItemId() == 4) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	public class longClickListener implements OnItemLongClickListener {

		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			if (menuPosition == 1) {
				initItemLongClickListener(new File(RootPath.get(arg2)));
			} else if (menuPosition == 2) {
				initItemLongClickListener(new File(SDCardPath.get(arg2)));
			}
			return false;
		}

	}

	private String mCopyFileName;
	private boolean isCopy = false;

	private void initItemLongClickListener(final File file) {
		OnClickListener listener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if (file.canRead()) {// 注意，所有对文件的操作必须是在该文件可读的情况下才可以，否则报错
					if (item == 0) {// 复制
						if (file.isFile()
								&& "txt".equals((file.getName().substring(file
										.getName().lastIndexOf(".") + 1, file
										.getName().length())).toLowerCase())) {
							Toast.makeText(MainActivity.this, "已复制!",
									Toast.LENGTH_SHORT).show();
							// 复制标志位，表明已复制文件
							isCopy = true;
							// 取得复制文件的名字
							mCopyFileName = file.getName();
							// 记录复制文件的路径
							mOldFilePath = mCurrentFilePath
									+ java.io.File.separator + mCopyFileName;
						} else {
							Toast.makeText(MainActivity.this, "目前只支持复制文本文件!",
									Toast.LENGTH_SHORT).show();
						}
					} else if (item == 1) {// 重命名
						initRenameDialog(file);
					} else if (item == 2) {// 删除
						initDeleteDialog(file);
					}
				} else {
					Toast.makeText(MainActivity.this, "对不起，您的访问权限不足!",
							Toast.LENGTH_SHORT).show();
				}
			}
		};
		// 列表项名称
		String[] mMenu = { "复制", "重命名", "删除" };
		// 显示操作选择对话框
		new AlertDialog.Builder(MainActivity.this).setTitle("请选择操作!")
				.setItems(mMenu, listener).setPositiveButton("取消", null).show();

	}

	EditText mET;

	private void initRenameDialog(final File file) {
		LayoutInflater mLI = LayoutInflater.from(MainActivity.this);
		// 初始化重命名对话框
		LinearLayout mLL = (LinearLayout) mLI.inflate(R.layout.rename_dialog,
				null);
		mET = (EditText) mLL.findViewById(R.id.new_filename);
		// 显示当前的文件名
		mET.setText(file.getName());
		new AlertDialog.Builder(MainActivity.this)
				.setTitle("请输入")
				.setView(mLL)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
								String EditPath = mET.getText().toString();
								final String ParentPath = file.getParentFile()
										.getPath() + java.io.File.separator;
								final String NewFilePath = ParentPath
										+ EditPath;
								if (new File(NewFilePath).exists()) {
									if (!EditPath.equals(file.getName())) {// 去掉“重命名”操作没操作的情况
										new AlertDialog.Builder(
												MainActivity.this)
												.setTitle("提示!")
												.setMessage("该文件名已存在，是否要覆盖?")
												.setPositiveButton(
														R.string.ok,
														new DialogInterface.OnClickListener() {
															public void onClick(
																	DialogInterface dialog,
																	int which) {
																file.renameTo(new File(
																		NewFilePath));
																Toast.makeText(
																		MainActivity.this,
																		"the file path is "
																				+ new File(
																						NewFilePath),
																		Toast.LENGTH_SHORT)
																		.show();
																// 更新当前目录信息
																if (menuPosition == 1) {
																	InitNewRootViewPager(file
																			.getParentFile()
																			.getPath());
																} else if (menuPosition == 2) {
																	InitNewSDCardViewPager(file
																			.getParentFile()
																			.getPath());
																}
															}
														})
												.setNegativeButton(
														R.string.cancel, null)
												.show();
									}
								} else {// 文件名不重复时直接修改文件名后再次刷新列表
									file.renameTo(new File(NewFilePath));
									if (menuPosition == 1) {
										isback = true;
										InitNewRootViewPager(file.getParent());
									} else if (menuPosition == 2) {
										isback = true;
										InitNewSDCardViewPager(file.getParent());
									}
								}
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
								// 什么都不用做
							}
						}).show();
	}

	// 弹出删除文件/文件夹的对话框
	private void initDeleteDialog(final File file) {
		new AlertDialog.Builder(MainActivity.this)
				.setTitle("提示!")
				.setMessage(
						"您确定要删除该" + (file.isDirectory() ? "文件夹" : "文件") + "吗?")
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								if (file.isFile()) {
									// 是文件则直接删除
									file.delete();
								} else {
									// 是文件夹则用这个方法删除
									deleteFolder(file);
								}
								// 重新遍历该文件的父目录并刷新listview
								if (menuPosition == 1) {
									isback = true;
									InitNewRootViewPager(file.getParent());
								} else if (menuPosition == 2) {
									isback = true;
									InitNewSDCardViewPager(file.getParent());
								}
							}
						}).setNegativeButton(R.string.cancel, null).show();
	}

	// 删除文件夹的方法（递归删除该文件夹下的所有文件）
	public void deleteFolder(File folder) {
		File[] fileArray = folder.listFiles();
		if (fileArray.length == 0) {
			// 空文件夹则直接删除
			folder.delete();
		} else {
			// 遍历该目录
			for (File currentFile : fileArray) {
				if (currentFile.exists() && currentFile.isFile()) {
					// 文件则直接删除
					currentFile.delete();
				} else {
					// 递归删除
					deleteFolder(currentFile);
				}
			}
			folder.delete();
		}
	}

	/**
	 * 根据文件后缀名获得对应的MIME类型。
	 * 
	 * @param file
	 */
	private String getMIMEType(File file) {
		String type = "*/*";
		String fName = file.getName();
		// 获取后缀名前的分隔符"."在fName中的位置。
		int dotIndex = fName.lastIndexOf(".");
		if (dotIndex < 0) {
			return type;
		}
		/* 获取文件的后缀名 */
		String end = fName.substring(dotIndex, fName.length()).toLowerCase();
		if (end == "")
			return type;
		// 在MIME和文件类型的匹配表中找到对应的MIME类型。
		for (int i = 0; i < MIME_MapTable.length; i++) {
			if (end.equals(MIME_MapTable[i][0]))
				type = MIME_MapTable[i][1];
		}
		return type;
	}

	/**
	 * 打开文件
	 * 
	 * @param file
	 */
	private void openFile(File file) {
		// Uri uri = Uri.parse("file://"+file.getAbsolutePath());
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// 设置intent的Action属性
		intent.setAction(Intent.ACTION_VIEW);
		// 获取文件file的MIME类型
		String type = getMIMEType(file);
		// 设置intent的data和Type属性。
		intent.setDataAndType(Uri.fromFile(file), type);
		// 跳转
		startActivity(intent);
	}

	private void palse() {
		mNewFilePath = mCurrentFilePath + java.io.File.separator
				+ mCopyFileName;// 得到新路径
		if (!mOldFilePath.equals(mNewFilePath) && isCopy == true) {// 在不同路径下复制才起效
			if (!new File(mNewFilePath).exists()) {
				copyFile(mOldFilePath, mNewFilePath);
				Toast.makeText(MainActivity.this, "执行了粘贴", Toast.LENGTH_SHORT)
						.show();
				if (menuPosition == 1) {
					isback = true;
					InitNewRootViewPager(mCurrentFilePath);
				} else {
					isback = true;
					InitNewSDCardViewPager(mCurrentFilePath);
				}
			} else {
				new AlertDialog.Builder(MainActivity.this)
						.setTitle("提示!")
						.setMessage("该文件已存在，是否要覆盖?")
						.setPositiveButton(R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										copyFile(mOldFilePath, mNewFilePath);
										if (menuPosition == 1) {
											isback = true;
											InitNewRootViewPager(mCurrentFilePath);
										} else {
											isback = true;
											InitNewSDCardViewPager(mCurrentFilePath);
										}
									}
								}).setNegativeButton(R.string.cancel, null)
						.show();
			}
		} else {
			Toast.makeText(MainActivity.this, "未复制文件！", Toast.LENGTH_LONG)
					.show();
		}
	}

	private int i;
	FileInputStream fis;
	FileOutputStream fos;

	// 复制文件
	private void copyFile(String oldFile, String newFile) {
		try {
			fis = new FileInputStream(oldFile);
			fos = new FileOutputStream(newFile);
			do {
				// 逐个byte读取文件，并写入另一个文件中
				if ((i = fis.read()) != -1) {
					fos.write(i);
				}
			} while (i != -1);
			// 关闭输入文件流
			if (fis != null) {
				fis.close();
			}
			// 关闭输出文件流
			if (fos != null) {
				fos.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	EditText sET;

	private void searchFolder() {
		Intent intent = new Intent(MainActivity.this, Search.class);
		intent.putExtra("menuPosition", menuPosition);
		startActivity(intent);
	}

	public static boolean hasSDCard() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}

	// 设置再按一次退出
	long waitTime = 2000;
	long touchTime = 0;

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& KeyEvent.KEYCODE_BACK == keyCode) {
			long currentTime = System.currentTimeMillis();
			if ((currentTime - touchTime) >= waitTime) {
				Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT)
						.show();
				touchTime = currentTime;
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
