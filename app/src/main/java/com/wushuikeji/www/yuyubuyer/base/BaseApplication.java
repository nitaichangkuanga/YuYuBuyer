package com.wushuikeji.www.yuyubuyer.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.wushuikeji.www.yuyubuyer.receiver.CallReceiver;
import com.wushuikeji.www.yuyubuyer.service.LocationService;
import com.wushuikeji.www.yuyubuyer.utils.PermissionsCheckerUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;
import com.zhy.http.okhttp.https.HttpsUtils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class BaseApplication extends Application {

	private LinkedList<Activity> activityList = new LinkedList<Activity>();

	private static Context	mContext;
	private static Thread	mMainThread;
	private static long		mMainTreadId;
	private static Looper	mMainLooper;
	private static Handler	mHandler;
	//百度定位
	public LocationService locationService;
	public Vibrator mVibrator;
	//权限
	public PermissionsCheckerUtils mPermissionsCheckerUtils;

	public static BaseApplication baseApplication;
	public static BaseApplication getInstance() {

		return baseApplication;
	}

	public static Handler getHandler() {
		return mHandler;
	}

	public static Context getContext() {
		return mContext;
	}

	public static Thread getMainThread() {
		return mMainThread;
	}

	public static long getMainTreadId() {
		return mMainTreadId;
	}

	public static Looper getMainThreadLooper() {
		return mMainLooper;
	}

	public static Context applicationContext;
	private CallReceiver callReceiver;
	// 记录是否已经初始化
	private boolean isInit = false;

	@Override
	public void onCreate() {// 程序的入口
		super.onCreate();

		//配置ImageLoader
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.threadPriority(Thread.NORM_PRIORITY - 2)// 设置线程的优先级
				.denyCacheImageMultipleSizesInMemory()// 当同一个Uri获取不同大小的图片，缓存到内存时，只缓存一个。默认会缓存多个不同的大小的相同图片
				.discCacheFileNameGenerator(new Md5FileNameGenerator())// 设置缓存文件的名字，使用MD5对UIL进行加密命名
				.discCacheFileCount(50)// 缓存文件的最大个数
				.tasksProcessingOrder(QueueProcessingType.LIFO)// 设置图片下载和显示的工作队列排序 后进先出
				.build();
		//缓存目录自定义discCacheFileNameGenerator(new UnlimitedDiscCache(cacheDir))

		// Initialize ImageLoader with configuration
		ImageLoader.getInstance().init(config);
		//默认
//		ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));

		//配置OkHttpUtils参数
		HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
		CookieJarImpl cookieJar = new CookieJarImpl(new PersistentCookieStore(getApplicationContext()));
		OkHttpClient okHttpClient = new OkHttpClient.Builder()
				.connectTimeout(8000L, TimeUnit.MILLISECONDS)
				.readTimeout(8000L, TimeUnit.MILLISECONDS)
				.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
				//其他配置
				.build();
		OkHttpUtils.initClient(okHttpClient);

		// 初始化化一些.常用属性.然后放到盒子里面来
		// 上下文
		mContext = getApplicationContext();

		// 主线程
		mMainThread = Thread.currentThread();

		// 主线程Id
		mMainTreadId = android.os.Process.myTid();

		// tid thread
		// uid user
		// pid process
		// 主线程Looper对象
		mMainLooper = getMainLooper();

		// 定义一个handler
		mHandler = new Handler();

		//捕获程序中未try-catch的异常
//		CrashHandler crashHandler = CrashHandler.getInstance();
//		crashHandler.init(getApplicationContext());

		//便于统一管理Activity
		this.baseApplication = this;
		//百度定位的初始化
		/***
		 * 初始化定位sdk，建议在Application中创建
		 */
		locationService = new LocationService(getApplicationContext());
		mPermissionsCheckerUtils = new PermissionsCheckerUtils(getApplicationContext());
		mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
		// 初始化环信SDK
		initEasemob();

		IntentFilter callFilter = new IntentFilter(EMClient.getInstance().callManager().getIncomingCallBroadcastAction());
		CallReceiver callReceiver = new CallReceiver();
		//注册通话广播接收者
		registerReceiver(callReceiver, callFilter);

	}

	// 添加Activity到容器中
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	// 遍历所有Activity并finish
	public void closeActivity() {
		try {
			for (Activity activity : activityList) {
				if (activity != null)
					activity.finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initEasemob() {
		// 获取当前进程 id 并取得进程名
		int pid = android.os.Process.myPid();
		String processAppName = getAppName(pid);
		/**
		 * 如果app启用了远程的service，此application:onCreate会被调用2次
		 * 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
		 * 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返回
		 */
		if (processAppName == null || !processAppName.equalsIgnoreCase(mContext.getPackageName())) {
			// 则此application的onCreate 是被service 调用的，直接返回
			return;
		}
		if (isInit) {
			return;
		}

		// 调用初始化方法初始化sdk
		EMClient.getInstance().init(mContext, initOptions());

		// 设置开启debug模式
		EMClient.getInstance().setDebugMode(true);

		// 设置初始化已经完成
		isInit = true;
	}
	/**
	 * SDK初始化的一些配置
	 * 关于 EMOptions 可以参考官方的 API 文档
	 * http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1chat_1_1_e_m_options.html
	 */
	private EMOptions initOptions() {

		EMOptions options = new EMOptions();
		// 设置Appkey，如果配置文件已经配置，这里可以不用设置
		// options.setAppKey("lzan13#hxsdkdemo");
		// 设置自动登录
		options.setAutoLogin(true);
		// 设置是否需要发送已读回执
		options.setRequireAck(true);
		// 设置是否需要发送回执，
		options.setRequireDeliveryAck(true);
		// 设置是否需要服务器收到消息确认
		options.setRequireServerAck(true);
		// 设置是否根据服务器时间排序，默认是true
		options.setSortMessageByServerTime(false);
		// 收到好友申请是否自动同意，如果是自动同意就不会收到好友请求的回调，因为sdk会自动处理，默认为true
		options.setAcceptInvitationAlways(false);
		// 设置是否自动接收加群邀请，如果设置了当收到群邀请会自动同意加入
		options.setAutoAcceptGroupInvitation(false);
		// 设置（主动或被动）退出群组时，是否删除群聊聊天记录
		options.setDeleteMessagesAsExitGroup(false);
		// 设置是否允许聊天室的Owner 离开并删除聊天室的会话
		options.allowChatroomOwnerLeave(true);
		// 设置google GCM推送id，国内可以不用设置
		// options.setGCMNumber(MLConstants.ML_GCM_NUMBER);
		// 设置集成小米推送的appid和appkey
		// options.setMipushConfig(MLConstants.ML_MI_APP_ID, MLConstants.ML_MI_APP_KEY);

		return options;
	}
	/**
	 * 根据Pid获取当前进程的名字，一般就是当前app的包名
	 *
	 * @param pid 进程的id
	 * @return 返回进程的名字
	 */
	private String getAppName(int pid) {
		String processName = null;
		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List list = activityManager.getRunningAppProcesses();
		Iterator i = list.iterator();
		while (i.hasNext()) {
			ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
			try {
				if (info.pid == pid) {
					// 根据进程的信息获取当前进程的名字
					processName = info.processName;
					// 返回当前进程名
					return processName;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 没有匹配的项，返回为null
		return null;
	}
}
