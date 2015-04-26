package com.jerry.crop;

import java.io.File;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * <pre>
 * @author junxu.wang update
 * @d2015��4��26��
 * </pre>
 *
 */
public class CropImgActivity extends Activity {

	private static final int PHOTO_REQUEST_CAREMA = 1;// ����
	private static final int PHOTO_REQUEST_GALLERY = 2;// �������ѡ��
	private static final int PHOTO_REQUEST_CUT = 3;// ���

	private ImageView iv_image;

	/* ͷ������ */
	private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
	private File tempFile;
	private Uri uri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crop_img_layout);
		this.iv_image = (ImageView) this.findViewById(R.id.iv_image);
	}

	/*
	 * ������ȡ
	 */
	public void gallery(View view) {
		// ����ϵͳͼ�⣬ѡ��һ��ͼƬ
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		// ����һ�����з���ֵ��Activity��������ΪPHOTO_REQUEST_GALLERY
		startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
	}

	/*
	 * �������ȡ
	 */
	public void camera(View view) {
		// �������
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		// �жϴ洢���Ƿ�����ã����ý��д洢
		if (hasSdcard()) {
			tempFile = new File(Environment.getExternalStorageDirectory(),
					PHOTO_FILE_NAME);
			// ���ļ��д���uri
			Uri uri = Uri.fromFile(tempFile);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		}
		// ����һ�����з���ֵ��Activity��������ΪPHOTO_REQUEST_CAREMA
		startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
	}

	/*
	 * ����ͼƬ
	 */
	private void crop(Uri uri) {
		cropImageUri(uri, 200, 200, PHOTO_REQUEST_CUT);
	}

	/**
	 * <pre>
	 * ʹ��Bitmap�п��ܻᵼ��ͼƬ���󣬶����ܷ���ʵ�ʴ�С��ͼƬ���ҽ����ô�ͼUri��СͼBitmap�����ݴ洢��ʽ��
	 * @param uri
	 * @param outputX
	 * @param outputY
	 * @param requestCode
	 * </pre>
	 */
	private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode) {
		this.uri = uri;
		// �ü�ͼƬ��ͼ
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// �ü���ı�����1��1
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// �ü������ͼƬ�ĳߴ��С
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//
		intent.putExtra("return-data", false);// �Ƿ񷵻�����ͼ
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());// ͼƬ��ʽ
		intent.putExtra("noFaceDetection", true); // no face detection ȡ������ʶ��
		// ����һ�����з���ֵ��Activity��
		startActivityForResult(intent, requestCode);
	}

	private Bitmap decodeUriAsBitmap(Uri uri) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(getContentResolver()
					.openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

	/*
	 * �ж�sdcard�Ƿ񱻹���
	 */
	private boolean hasSdcard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PHOTO_REQUEST_GALLERY) {
			// ����᷵�ص�����
			if (data != null) {
				// �õ�ͼƬ��ȫ·��
				Uri uri = data.getData();
				crop(uri);
			}

		} else if (requestCode == PHOTO_REQUEST_CAREMA) {
			// ��������ص�����
			if (hasSdcard()) {
				crop(Uri.fromFile(tempFile));
			} else {
				Toast.makeText(CropImgActivity.this, "δ�ҵ��洢�����޷��洢��Ƭ��", 0)
						.show();
			}

		} else if (requestCode == PHOTO_REQUEST_CUT) {
			// �Ӽ���ͼƬ���ص�����

			if (uri != null) {
				Bitmap bitmap = decodeUriAsBitmap(uri);
				this.iv_image.setImageBitmap(bitmap);
				// Log.d("ddd","bitmap="+bitmap.getWidth()+","+bitmap.getHeight());
			} else if (data != null) {
				Bitmap bitmap = data.getParcelableExtra("data");
				this.iv_image.setImageBitmap(bitmap);
				// Log.d("ddd","bitmap="+bitmap.getWidth()+","+bitmap.getHeight());
			}

			try {
				// ����ʱ�ļ�ɾ��
				tempFile.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		super.onActivityResult(requestCode, resultCode, data);
	}
}
