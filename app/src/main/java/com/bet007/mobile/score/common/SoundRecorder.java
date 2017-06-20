package com.bet007.mobile.score.common;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

/**
 * 录音
 * 
 * @author 1
 *
 */
public class SoundRecorder {
	private static final String TAG = SoundRecorder.class.getSimpleName();
	private MediaRecorder mRecorder = null;
	public final static String VOICE_POSTFIX = ".amr"; // 语音文件后缀名
	public final static int RECORD_TIME_MIN = 1; // 录音最短时间，单位：s
	public final static int RECORD_TIME_MAX = 10; // 录音最长时间，单位：s
	public final static int RECORD_TIME_TIPS = 3; // 还能说多少秒
	// 录音文件大小最小值，如果ap给用户禁止了录音权限，有两种情况：
	// 1、录音会抛异常，这种情况录音失败，会有提示；
	// 2、录音不抛异常，这种情况录音文件大小是6个字节（目前试过的几个手机都是），所以这里限定小于30字节时，认为是没有录音权限导致的，不用发送语音了，同时把该文件删掉。
	public final static int RECORD_AUDIO_FILE_MIN_SIZE = 30;

	public SoundRecorder() {
	}

	public void start(String name) throws Exception {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			throw new Exception("没有存储器");
		}
		try {
			if (mRecorder == null) {
				mRecorder = new MediaRecorder();
			} else {
				mRecorder.reset();
			}

			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mRecorder.setOutputFile(getVoicePathWithName(name));
			mRecorder.prepare();
			mRecorder.start();
		} catch (Exception e) {
			mRecorder.reset();
			e.printStackTrace();
			throw new Exception(e);
		}
	}

	public void stop() {
		try {
			if (mRecorder != null) {
				mRecorder.stop();
			}
		} catch (Exception e) {
			if (mRecorder != null) {
				mRecorder.reset();
			}
			e.printStackTrace();
		}
		try {
			if (mRecorder != null) {
				mRecorder.release();
				mRecorder = null;
			}
		} catch (Exception e) {
		}

	}

	public void destroy() {
		try {
			if (mRecorder != null) {
				mRecorder.release();
			}
		} catch (Exception e) {
		}
		mRecorder = null;
	}

	public void pause() {
		if (mRecorder != null) {
			mRecorder.stop();
		}
	}

	public void start() {
		if (mRecorder != null) {
			mRecorder.start();
		}
	}

	//分等级
	public int getLevel() {

		double splValue = getRecorderDb();
		if(splValue <= 40){
			return 0;
		}else if (splValue <= 60) {
			return 1;
		}else if (splValue <= 70) {
			return 2;
		}else if (splValue <= 79) {
			return 3;
		}else if (splValue <= 82) {
			return 4;
		}else {
			return 5;
		}
	}


	/**
	 * 更新话筒状态
	 * 
	 */
	private int BASE = 1;
	//获取分贝值
	private double getRecorderDb() {
		if (mRecorder != null) {
			double ratio = (double) mRecorder.getMaxAmplitude() / BASE;
			double db = 0;// 分贝
			if (ratio > 1)
				db = 20 * Math.log10(ratio);
//			Log.d("SoundMeter", "-------分贝值：" + db);
			return db;
		} else {
			return 0;
		}
	}

	/**
	 * 录音时调用该方法暂停播放器的声音
	 * 
	 * @param context
	 *            上下文
	 * @param bMute
	 *            true：关闭背景音乐；false：恢复背景音乐。背景音乐是指播放器后台播放音乐
	 * @return
	 */
	public static boolean muteAudioFocus(Context context, boolean bMute) {
		if (context == null) {
			Log.d(TAG, "context is null.");
			return false;
		}
		// if(!VersionUtils.isrFroyo()){ //这里不用判断了，都是4.0以上的了
		// // 2.1以下的版本不支持下面的API：requestAudioFocus和abandonAudioFocus
		// Log.d("ANDROID_LAB", "Android 2.1 and below can not stop music");
		// return false;
		// }
		boolean bool = false;
		AudioManager am = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
		if (bMute) {
			int result = am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
			bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
		} else {
			int result = am.abandonAudioFocus(null);
			bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
		}
		Log.d(TAG, "pauseMusic bMute=" + bMute + " result=" + bool);
		return bool;
	}

	public final static String AP_FOLDERNAME = "liveScore"; // AP文件夹
	public final static String AP_FOLDER = File.separator + AP_FOLDERNAME + File.separator; // AP文件夹
	public final static String VOICE_FOLDER = "voice"; // 语音文件夹
	/**
	 * 获取语音文件本地路径
	 *
	 * @param voiceName
	 *            语音文件名字
	 * @return
	 */
	public static String getVoicePathWithName(String voiceName) {
		return getFolderWithName(VOICE_FOLDER) + voiceName;
	}
	/**
	 * 根据文件夹名字获取文件夹路径，如果文件夹不存在，会创建文件夹
	 *
	 * @param folderName
	 *            文件夹名字
	 * @return 返回文件夹路径
	 */
	public static String getFolderWithName(String folderName) {
		makeFolderWithName(folderName);
		return android.os.Environment.getExternalStorageDirectory() + AP_FOLDER + folderName + File.separator;
	}
	/**
	 * 判断文件夹名字对应的文件夹是否存在，不存在就创建
	 *
	 * @return 存在/创建成功返回true，创建失败返回false
	 */
	public static boolean makeFolderWithName(String folderName) {
		try {
			File file = new File(
					Environment.getExternalStorageDirectory() + AP_FOLDER + folderName + File.separator);
			if (!file.exists())
				file.mkdirs();
		} catch (Exception localException) {
			localException.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 计算SD卡的剩余空间
	 *
	 * @return 返回剩余空间，单位KB；返回-1，说明没有安装sd卡
	 */
	public static long getFreeDiskSpace() {
		String status = Environment.getExternalStorageState();
		long freeSpace = 0;
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			try {
				File path = Environment.getExternalStorageDirectory();
				StatFs stat = new StatFs(path.getPath());
				@SuppressWarnings("deprecation")
				long blockSize = stat.getBlockSize();
				@SuppressWarnings("deprecation")
				long availableBlocks = stat.getAvailableBlocks();
				freeSpace = availableBlocks * blockSize / 1024;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return -1;
		}
		return (freeSpace);
	}

	/**
	 * 创建语音文件名字
	 *
	 * @param uId
	 * @param moduleId
	 * @return
	 */
	public static String createVoiceName(String uId, String moduleId) {
		String mediano = getMeidiaNo();
		String voiceName = uId + "-" + moduleId + "-" + mediano + VOICE_POSTFIX;
		return voiceName;
	}

	/**
	 * 返回媒体文件序号
	 *
	 * @return
	 */
	public synchronized static String getMeidiaNo() {
		String result = System.currentTimeMillis() + "";
		return result;
	}

	/**
	 * 根据声音文件的url得到声音本地路径
	 *
	 * @param voiceUrl
	 *            声音文件url
	 * @return
	 */
	public static String createVoicePathWithUrl(String voiceUrl) {
		String filename = getLastStringWithSplit("/", voiceUrl);
		return getFolderWithName(VOICE_FOLDER) + filename;
	}

	/**
	 * 获取字符串中分隔符后面的一段字符串
	 *
	 * @param string
	 * @return
	 */
	public static String getLastStringWithSplit(String split, String string) {
		if (TextUtils.isEmpty(string)) {
			return "";
		}
		if (TextUtils.isEmpty(split)) {
			return string;
		}
		int k = string.lastIndexOf(split);
		try {
			String result = string.substring(k + 1);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return string;
	}
}
