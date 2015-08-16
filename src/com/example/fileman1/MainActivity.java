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

	private ViewPager mPager;// ҳ������
	private List<View> listViews; // Tabҳ���б�
	private ImageView cursor;// ����ͼƬ
	private TextView t1, t2;// ҳ��ͷ��
	private int offset = 0;// ����ͼƬƫ����
	private int currIndex = 0;// ��ǰҳ�����
	private int bmpW;// ����ͼƬ���

	private ListView listView1;
	private ListView listView2;
	// ��ʼĿ¼��/��
	private String mRootPath = java.io.File.separator;
	// SD����Ŀ¼
	private String mSDCard = Environment.getExternalStorageDirectory()
			.toString();
	// �þ�̬�����洢 ��ǰĿ¼·����Ϣ
	public static String mCurrentFilePath = "";
	// �����ʾ���ļ��б�����Ӧ��·��
	private List<String> RootPath = null;
	private List<String> SDCardPath = null;
	// �����ֻ���SD����1�����ֻ���2����SD��
	private static int menuPosition = 1;
	private boolean isback = true;
	private String mOldFilePath = "";
	private String mNewFilePath = "";
	private boolean hasSDCard = false;
	private final String[][] MIME_MapTable = {
			// {��׺���� MIME����}
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
				.getWidth();// ��ȡͼƬ���
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// ��ȡ�ֱ��ʿ��
		offset = (screenW / 2 - bmpW) / 2;// ����ƫ����
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);// ���ö�����ʼλ��
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
			Toast.makeText(getApplicationContext(), "�ڴ濨����", Toast.LENGTH_SHORT)
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
		// ���������ļ���·���µ������ļ�/�ļ���
		File[] mFiles = mFile.listFiles();
		/* �������ļ���Ϣ��ӵ������� */
		for (File mCurrentFile : mFiles) {
			Map<String, Object> map = new HashMap<String, Object>();
			String fileName = mCurrentFile.getName();
			if (!mCurrentFilePath.equals(mRootPath) && isback) { // isback��Ϊ��ǣ�ʹÿ�����һ��
				Map<String, Object> map1 = new HashMap<String, Object>();
				map1.put("img", R.drawable.back_to_root);
				map1.put("info", "���ظ�Ŀ¼");
				rootlist.add(map1);
				RootPath.add(0 + "");
				Map<String, Object> map2 = new HashMap<String, Object>();
				map2.put("img", R.drawable.back_to_up);
				map2.put("info", "������һ��");
				rootlist.add(map2);
				RootPath.add(1 + "");
				isback = false;
			}
			String fileEnds = fileName.substring(fileName.lastIndexOf(".") + 1,
					fileName.length()).toLowerCase();// ȡ���ļ���׺����ת��Сд
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
		// ���������ļ���·���µ������ļ�/�ļ���
		File[] mFiles = mFile.listFiles();
		/* �������ļ���Ϣ��ӵ������� */
		for (File mCurrentFile : mFiles) {
			Map<String, Object> map = new HashMap<String, Object>();
			String fileName = mCurrentFile.getName();
			if (!mCurrentFilePath.equals(mSDCard) && isback) {
				Map<String, Object> map1 = new HashMap<String, Object>();
				map1.put("img", R.drawable.back_to_root);
				map1.put("info", "���ظ�Ŀ¼");
				sdcardlist.add(map1);
				SDCardPath.add(0 + "");
				Map<String, Object> map2 = new HashMap<String, Object>();
				map2.put("img", R.drawable.back_to_up);
				map2.put("info", "������һ��");
				sdcardlist.add(map2);
				SDCardPath.add(1 + "");
				isback = false;
			}
			String fileEnds = fileName.substring(fileName.lastIndexOf(".") + 1,
					fileName.length()).toLowerCase();// ȡ���ļ���׺����ת��Сд
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
		int one = offset * 2 + bmpW;// ҳ��1 -> ҳ��2 ƫ����
		int two = one;// ҳ��1 -> ҳ��2 ƫ����

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
			animation.setFillAfter(true);// True:ͼƬͣ�ڶ�������λ��
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
					Toast.makeText(MainActivity.this, "����Ȩ�޲���!",
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
					Toast.makeText(MainActivity.this, "����Ȩ�޲���!",
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
		// ��ʾ�Ի���
		LayoutInflater mLI = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// ��ʼ���Ի��򲼾�
		final LinearLayout mLL = (LinearLayout) mLI.inflate(
				R.layout.create_dialog, null);
		new AlertDialog.Builder(MainActivity.this)
		        .setTitle("�½��ļ���")
				.setView(mLL)
				.setPositiveButton("����", new DialogInterface.OnClickListener() {
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
									// ˢ�µ�ǰĿ¼�ļ��б�
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
								Toast.makeText(MainActivity.this, "�ļ�������Ϊ��",
										Toast.LENGTH_SHORT).show();
							}
						} else if (menuPosition == 2) {
							mCreateFile = new File(mCurrentFilePath
									+ java.io.File.separator + mNewFolderName);
							if (!mCreateFile.exists()
									&& !mCreateFile.isDirectory()
									&& mNewFolderName.length() != 0) {
								if (mCreateFile.mkdirs()) {
									// ˢ�µ�ǰĿ¼�ļ��б�
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
								Toast.makeText(MainActivity.this, "�ļ�������Ϊ��",
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
				if (file.canRead()) {// ע�⣬���ж��ļ��Ĳ����������ڸ��ļ��ɶ�������²ſ��ԣ����򱨴�
					if (item == 0) {// ����
						if (file.isFile()
								&& "txt".equals((file.getName().substring(file
										.getName().lastIndexOf(".") + 1, file
										.getName().length())).toLowerCase())) {
							Toast.makeText(MainActivity.this, "�Ѹ���!",
									Toast.LENGTH_SHORT).show();
							// ���Ʊ�־λ�������Ѹ����ļ�
							isCopy = true;
							// ȡ�ø����ļ�������
							mCopyFileName = file.getName();
							// ��¼�����ļ���·��
							mOldFilePath = mCurrentFilePath
									+ java.io.File.separator + mCopyFileName;
						} else {
							Toast.makeText(MainActivity.this, "Ŀǰֻ֧�ָ����ı��ļ�!",
									Toast.LENGTH_SHORT).show();
						}
					} else if (item == 1) {// ������
						initRenameDialog(file);
					} else if (item == 2) {// ɾ��
						initDeleteDialog(file);
					}
				} else {
					Toast.makeText(MainActivity.this, "�Բ������ķ���Ȩ�޲���!",
							Toast.LENGTH_SHORT).show();
				}
			}
		};
		// �б�������
		String[] mMenu = { "����", "������", "ɾ��" };
		// ��ʾ����ѡ��Ի���
		new AlertDialog.Builder(MainActivity.this).setTitle("��ѡ�����!")
				.setItems(mMenu, listener).setPositiveButton("ȡ��", null).show();

	}

	EditText mET;

	private void initRenameDialog(final File file) {
		LayoutInflater mLI = LayoutInflater.from(MainActivity.this);
		// ��ʼ���������Ի���
		LinearLayout mLL = (LinearLayout) mLI.inflate(R.layout.rename_dialog,
				null);
		mET = (EditText) mLL.findViewById(R.id.new_filename);
		// ��ʾ��ǰ���ļ���
		mET.setText(file.getName());
		new AlertDialog.Builder(MainActivity.this)
				.setTitle("������")
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
									if (!EditPath.equals(file.getName())) {// ȥ����������������û���������
										new AlertDialog.Builder(
												MainActivity.this)
												.setTitle("��ʾ!")
												.setMessage("���ļ����Ѵ��ڣ��Ƿ�Ҫ����?")
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
																// ���µ�ǰĿ¼��Ϣ
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
								} else {// �ļ������ظ�ʱֱ���޸��ļ������ٴ�ˢ���б�
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
								// ʲô��������
							}
						}).show();
	}

	// ����ɾ���ļ�/�ļ��еĶԻ���
	private void initDeleteDialog(final File file) {
		new AlertDialog.Builder(MainActivity.this)
				.setTitle("��ʾ!")
				.setMessage(
						"��ȷ��Ҫɾ����" + (file.isDirectory() ? "�ļ���" : "�ļ�") + "��?")
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								if (file.isFile()) {
									// ���ļ���ֱ��ɾ��
									file.delete();
								} else {
									// ���ļ��������������ɾ��
									deleteFolder(file);
								}
								// ���±������ļ��ĸ�Ŀ¼��ˢ��listview
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

	// ɾ���ļ��еķ������ݹ�ɾ�����ļ����µ������ļ���
	public void deleteFolder(File folder) {
		File[] fileArray = folder.listFiles();
		if (fileArray.length == 0) {
			// ���ļ�����ֱ��ɾ��
			folder.delete();
		} else {
			// ������Ŀ¼
			for (File currentFile : fileArray) {
				if (currentFile.exists() && currentFile.isFile()) {
					// �ļ���ֱ��ɾ��
					currentFile.delete();
				} else {
					// �ݹ�ɾ��
					deleteFolder(currentFile);
				}
			}
			folder.delete();
		}
	}

	/**
	 * �����ļ���׺����ö�Ӧ��MIME���͡�
	 * 
	 * @param file
	 */
	private String getMIMEType(File file) {
		String type = "*/*";
		String fName = file.getName();
		// ��ȡ��׺��ǰ�ķָ���"."��fName�е�λ�á�
		int dotIndex = fName.lastIndexOf(".");
		if (dotIndex < 0) {
			return type;
		}
		/* ��ȡ�ļ��ĺ�׺�� */
		String end = fName.substring(dotIndex, fName.length()).toLowerCase();
		if (end == "")
			return type;
		// ��MIME���ļ����͵�ƥ������ҵ���Ӧ��MIME���͡�
		for (int i = 0; i < MIME_MapTable.length; i++) {
			if (end.equals(MIME_MapTable[i][0]))
				type = MIME_MapTable[i][1];
		}
		return type;
	}

	/**
	 * ���ļ�
	 * 
	 * @param file
	 */
	private void openFile(File file) {
		// Uri uri = Uri.parse("file://"+file.getAbsolutePath());
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// ����intent��Action����
		intent.setAction(Intent.ACTION_VIEW);
		// ��ȡ�ļ�file��MIME����
		String type = getMIMEType(file);
		// ����intent��data��Type���ԡ�
		intent.setDataAndType(Uri.fromFile(file), type);
		// ��ת
		startActivity(intent);
	}

	private void palse() {
		mNewFilePath = mCurrentFilePath + java.io.File.separator
				+ mCopyFileName;// �õ���·��
		if (!mOldFilePath.equals(mNewFilePath) && isCopy == true) {// �ڲ�ͬ·���¸��Ʋ���Ч
			if (!new File(mNewFilePath).exists()) {
				copyFile(mOldFilePath, mNewFilePath);
				Toast.makeText(MainActivity.this, "ִ����ճ��", Toast.LENGTH_SHORT)
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
						.setTitle("��ʾ!")
						.setMessage("���ļ��Ѵ��ڣ��Ƿ�Ҫ����?")
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
			Toast.makeText(MainActivity.this, "δ�����ļ���", Toast.LENGTH_LONG)
					.show();
		}
	}

	private int i;
	FileInputStream fis;
	FileOutputStream fos;

	// �����ļ�
	private void copyFile(String oldFile, String newFile) {
		try {
			fis = new FileInputStream(oldFile);
			fos = new FileOutputStream(newFile);
			do {
				// ���byte��ȡ�ļ�����д����һ���ļ���
				if ((i = fis.read()) != -1) {
					fos.write(i);
				}
			} while (i != -1);
			// �ر������ļ���
			if (fis != null) {
				fis.close();
			}
			// �ر�����ļ���
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

	// �����ٰ�һ���˳�
	long waitTime = 2000;
	long touchTime = 0;

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& KeyEvent.KEYCODE_BACK == keyCode) {
			long currentTime = System.currentTimeMillis();
			if ((currentTime - touchTime) >= waitTime) {
				Toast.makeText(MainActivity.this, "�ٰ�һ���˳�", Toast.LENGTH_SHORT)
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
