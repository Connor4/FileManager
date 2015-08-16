package com.example.fileman1;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class Search extends Activity
{

	private EditText ET;
	private Button BT;
	private TextView TV;
	private int MenuPosition;
	private String RootPath = java.io.File.separator;
	private String SDCard = Environment.getExternalStorageDirectory()
			.toString();
	private String For;
	private String Out;

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		ET = (EditText) findViewById(R.id.search_et);
		BT = (Button) findViewById(R.id.search_bt1);
		TV = (TextView) findViewById(R.id.search_tv);
		TV.setMovementMethod(ScrollingMovementMethod.getInstance());
		BT.setOnClickListener(new AllSearchListener());
		Intent intent = this.getIntent();
		MenuPosition = intent.getIntExtra("menuPosition", 1);// 如果没有取得menuPosition则返回1
	}

	class AllSearchListener implements OnClickListener
	{
		public void onClick(View arg0)
		{
			File file = null;
			Out = "";
			For = ET.getText().toString();
			if (MenuPosition == 1)
			{
				file = new File(RootPath);
			} else if (MenuPosition == 2)
			{
				file = new File(SDCard);
			}
			if (!For.equals(""))
			{
				getFileDir(file);
				if (!Out.equals(""))
				{
					TV.setText(Out);
				} else
				{
					TV.setText("哎呀，什么都没找到");
				}
			} else
			{
				Toast.makeText(getApplicationContext(), "你倒是输入啊",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void getFileDir(File file)
	{
		File[] files = file.listFiles();
		if (file.canRead())
		{
			if (file.isDirectory())
			{
				if (file.getName().indexOf(For) >= 0)
				{
					Out += file.getPath() + "\n";
				}
				for (File f : files)
				{
					getFileDir(f);
				}
			} else
			{
				if (file.getName().indexOf(For) >= 0)
				{
					Out += file.getPath() + "\n";
				}
			}
		}
	}
}
