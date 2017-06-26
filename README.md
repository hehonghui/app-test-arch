# 初创团队的Android应用质量保障之道-稳定性与内存优化 应用示例部分

>
该项目为 [初创团队的Android应用质量保障之道](http://blog.csdn.net/bboyfeiyu/article/details/73716633) 的应用示例部分（应用架构、单元测试、Monkey、LeakCanary定制），由于时间有限，只能够写些简单的代码来讲述一个大致的过程。其中的代码有很多不合理之处，各位客官只需要了解其原理，然后将原理运用到自己的项目中即可。具体的代码结构、测试框架都可以自行替换.

核心要点: 

1. 单元测试覆盖，提高开发、测试效率，保证底层基础类型的正确性. 测试对象: 非UI的Class都可以进行单元测试.
2. Monkey 压力测试 配合 LeakCanary, 获取崩溃信息、内存泄露信息
3. 通过Jenkins平台自动执行测试任务, 将结果通过邮件发送给开发人员. (夜间执行测试，第二天早上得到邮件反馈)

## jenkins 流程

对于Android项目来说，你可以理解为它可以定期的拉取代码，然后打包你的应用，并且执行一些特定的任务，例如打包之后运行单元测试、压力测试、UI自动化测试、上传到fir.im 上等。Jenkins的执行流程大致如图 1-1 所示 :

![](http://img.blog.csdn.net/20170418131739385)     
图 1-1  

Jenkins测试任务的执行步骤: 

1. 获取最新代码(通过将github作为代码仓库)
3. 运行测试任务
4. 将测试报告通过邮件的形式发给相关人员

## Monkey 测试

1. 通过 gradle 执行 `./gradlew assembleMonkeyDebug` 命令生成 monkey flavor的apk包
2. 通过 shell 脚本安装上述apk
3. 执行monkey 命令运行monkey测试， 例如 `adb shell monkey -p com.simple.apptestarch --ignore-crashes --ignore-timeouts --ignore-native-crashes --pct-touch 40 --pct-motion 25 --pct-appswitch 10 --pct-rotation 5 -s 12358 -v -v -v --throttle 500 1000 2>~/monkey_error.txt 1>~/monkey_log.txt`, `com.simple.apptestarch` 为你的应用包名, 参数 1000代表事件的数量，测试时可以根据具体情况来设置，通常我们设置为 100000次 到 200000次。
4. 如果在测试过程中出现崩溃和内存泄露，相关信息会写入到sdcard对应的目录中
5. 测试完成，将相关日志通过邮件反馈给开发人员


崩溃日志的保存路径可以通过logcat来查看(可以自行修改): 

```
log file name : /storage/emulated/0/com.simple.apptestarch/crash/2017-06-26-crash.txt
```

### Crash日志 `2017-06-26-crash.txt` 详细信息 :

```
java.lang.IllegalStateException: Detail Leak !!! Please check !
	at com.simple.apptestarch.ui.detail.DetailActivity$1$override.run(DetailActivity.java:34)
	at com.simple.apptestarch.ui.detail.DetailActivity$1$override.access$dispatch(DetailActivity.java)
	at com.simple.apptestarch.ui.detail.DetailActivity$1.run(DetailActivity.java:0)
	at android.os.Handler.handleCallback(Handler.java:751)
	at android.os.Handler.dispatchMessage(Handler.java:95)
	at android.os.Looper.loop(Looper.java:154)
	at android.app.ActivityThread.main(ActivityThread.java:6119)
	at java.lang.reflect.Method.invoke(Native Method)
	at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:886)
	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:776)

```

崩溃日志很直观，直接暴露出来了崩溃的原因. 即 `com.simple.apptestarch.ui.detail.DetailActivity` 34行出的 `run`函数中抛出了一个 `java.lang.IllegalStateException `异常. 看到这个log之后到相应的类中处理掉即可.


### 内存泄露日志的保存路径可以通过logcat来查看(可以自行修改):

```
### *** onHeapAnalyzed in onHeapAnalyzed , dump dir :  /data/user/0/com.simple.apptestarch/files/leakcanary
### log file name : /storage/emulated/0/com.simple.apptestarch/leak/2017-06-26-leak.txt
```

内存泄露的详细信息:

```
In com.simple.apptestarch:1.0:1.
* com.simple.apptestarch.ui.detail.DetailActivity has leaked:
* GC ROOT static com.simple.apptestarch.ui.detail.DetailActivity.sRecords
* references java.util.LinkedList.first
* references java.util.LinkedList$Node.item
* leaks com.simple.apptestarch.ui.detail.DetailActivity instance

* Retaining: 48 KB.
* Reference Key: dce6c099-4abe-4be1-abd8-0bdb24eb6082
* Device: motorola google Nexus 6 shamu
* Android Version: 7.1.1 API: 25 LeakCanary: 1.5 00f37f5
* Durations: watch=5009ms, gc=139ms, heap dump=1817ms, analysis=85866ms

```

log指出 `com.simple.apptestarch.ui.detail.DetailActivity `发生了内存泄露, 只有它的GC ROOT是 `com.simple.apptestarch.ui.detail.DetailActivity.sRecords`, 我们根据信息到DetailActivity类中，发现问题代码如下: 

```
public class DetailActivity extends AppCompatActivity {

    private static List<Activity> sRecords = new LinkedList<>() ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        // 这里有问题 !!!!
        sRecords.add(this) ;
        // 其他代码
    }
}
```
此时只需要将DetailActivity对象在合适的时候从sRecords中移除即可. Jenkins执行Monkey测试、LeakCanary收集信息、邮件发送测试报告，整个过程都是通过自动执行，不需要我们人工干预，在快速开发时使得我们能够更快、更省心的发现问题。

## 单元测试

### Android 单元测试

测试代码目录为: `app/src/androidTest/java/`

![](http://img.blog.csdn.net/20170418131731443)

图 2-1 中将自动测试分为了三个层次，从下到上依次为单元测试、业务逻辑测试、UI测试，越往上测试成本越高、测试的效率越低，也就是说单元测试是整个测试金字塔中投入最少、收益最高、测试效率最高的测试类型。

* com.simple.apptestarch.services 包下为 Presenter的测试用例, 相当于业务逻辑测试;
* com.simple.apptestarch.services.unittest包下为单元测试

以 MainPresenterTestCase 中的testFetchNewsFromDb测试用例为例, 该测试用例的测试对象为 MainPresenter的fetchNews函数,代码如下:

```
public class MainPresenter extends Presenter<MainView> {
    // 本地新闻源, 从数据库获取新闻
    NewsDataSource mLocalSource  ;
    // 网络数据源, 从服务器获取新闻
    NewsDataSource mRemoteSource  ;
    // 是否应该自动刷新
    RefreshMonitor mRefreshMonitor;

    public MainPresenter(NewsDataSource local, NewsDataSource remote, RefreshMonitor refreshMonitor) {
        this.mLocalSource = local;
        this.mRemoteSource = remote;
        this.mRefreshMonitor = refreshMonitor;
    }
    
    private boolean isNotEmpty(List<News> newsList) {
        return newsList != null && newsList.size() > 0 ;
    }

    public void fetchNews() {
        // 1. 从数据库中读取缓存新闻
        mLocalSource.fetchNews(new NewsListener() {
            @Override
            public void onComplete(List<News> newsList) {
            		// 2. 从数据库中如果到获取新闻则回调给 MainView 
                if ( getView() != null && isNotEmpty(newsList) ) {
                    getView().onFetchNews(newsList);
                }

                // 3. 如果缓存中没有新闻 或者 mRefreshMonitor.shouldRefresh() 返回true, 那么要从网络上获取新闻
                if ( !isNotEmpty(newsList) || mRefreshMonitor.shouldRefresh()) {
                    mRemoteSource.fetchNews(mNewsListener);
                }
            }
        });
    }

    NewsListener mNewsListener = new NewsListener() {
        @Override
        public void onComplete(List<News> newsList) {
            if ( getView() != null ) {
                getView().onFetchNews(newsList);
            }
        }
    } ;
}
``` 
在 MainPresenter中我们将 mLocalSource、mRemoteSource、mRemoteSource作为外部依赖注入, 而不是在声明字段时直接使用new的形式创建, 例如`NewsDataSource mLocalSource = new NewsDbSource(); `,这是因为在对 MainPresenter 进行测试时我们需要解除这几个类型依赖, 在测试时我们可以使用几个Mock对象来替代,这样我们就能够不真正的依赖数据库、网络等条件进行测试,而只需要关注 MainPresenter 本身的业务逻辑. 在应用中不少开发人员会使用Dagger进行依赖注入，但是对于为什么要注入、而不在初始化时直接通过new的形式创建这个问题很多人并不了解。这种情况恰好是修改依赖注入的场景之一，在正式代码中使用真实的对象，而在测试时则通过Dagger注入另外一种实现的对象，达到解除依赖的效果。这种形式会使得MainPresenter变得更灵活、简单，也是的MainPresenter可测试。

我们再来看看 `testFetchNewsFromDb`测试用例的代码如下: 

```
    /**
     * 测试只从数据库中读取新闻. 这个测试用例模拟的情况为:
     *
     * 从数据库中读取了三条新闻缓存, 并且不应该从网络上获取新闻. 获取到数据库缓存之后会将缓存新闻通过 MainView 的 onFetchNews 回调给 MainActivity,
     * 然后后续不会调用 mRemoteSource 的fetchNews 方法, 因为我们预设了条件 mRefreshMonitor.shouldRefresh() 返回false, 且获取到了缓存新闻.
     *
     * @throws Exception
     */
    public void testFetchNewsFromDb() throws Exception {
        // ========= step 1. 条件准备部分
        // 当调用mRefreshMonitor.shouldRefresh() 返回 false. 表示不应该从网络上获取新闻
        when(mRefreshMonitor.shouldRefresh()).thenReturn(false) ;

        // ======== step 2. 执行mPresenter.fetchNews()函数
        mPresenter.fetchNews();

        // 当调用 mLocalSource.fetchNews 函数时捕获它的 NewsListener 参数, 然后调用 NewsListener 对象的 onComplete 函数, 参数通过 createNews 返回.
        ArgumentCaptor<NewsListener> captor = ArgumentCaptor.forClass(NewsListener.class) ;
        // 参数捕获 NewsListener 参数
        verify(mLocalSource).fetchNews(captor.capture());
        // 执行回调, 将 createNews() 返回的数据回调给 MainPresenter 【这里相当于是模拟从数据库中读取到数据】
        captor.getValue().onComplete(createNews());

        // ======= step 3. 验证部分
        // 调用了 mMainView的 onFetchNews 函数
        verify(mMainView,times(1)).onFetchNews(anyListOf(News.class));
        // 没有调用过 mRemoteSource的fetchNews函数
        verify(mRemoteSource, never()).fetchNews(any(NewsListener.class));
    }
```

在testFetchNewsFromDb测试用例中，模拟`mRefreshMonitor.shouldRefresh()`返回false. 当执行到 testFetchNewsFromDb函数的"step 2"时会执行 MainPresenter 类中的 fetchNews() 函数, 该函数首先会调用 `mLocalSource.fetchNews`函数，而在 `testFetchNewsFromDb `的 "step 2"之后会捕获 mLocalSource.fetchNews 函数的 NewsListener 参数，然后回调该NewsListener，将三条新闻(通过 createNews() 函数创建)回调给MainView.因此在 "step 3"处的 `verify(mMainView,times(1)).onFetchNews(anyListOf(News.class));` 成立，这句代码的意思是mMainView的onFetchNews函数被调用了一次. 也就是 MainPresenter 类中的 fetchNews() 函数中的注释2处条件成立了, `getView().onFetchNews(newsList);`被成功执行. 而我们预设了 `mRefreshMonitor.shouldRefresh()`返回false， 因此在MainPresenter 类中的 fetchNews() 函数中注释3处条件不成立，MainPresenter中的mRemoteSource.fetchNews函数不会被执行, 所以`verify(mRemoteSource, never()).fetchNews(any(NewsListener.class));` 验证条件正确. 这样我们这个加载新闻的业务逻辑测试用例就完成了。如果我们的MainPresenter代码、测试用例代码没有问题，那么我们的测试用例就应该通过。否则我们就需要修改代码，使它通过测试. (如果后期业务逻辑发生了改变，测试代码也需要改变，所以写单元测试也会有成本).

总结一下测试的过程就是我们通过预设一些条件，然后再执行相应的函数，最后验证函数中的逻辑是否按照我们的期望来执行。如果满足我们的期望，那么测试成功.

整个过程能够自动化之后，我们的后续维护工作就会很少了。只需要维护自己的单元测试用例，Jenkins、Monkey、LeakCanary这些都不再需要我们的维护。Jenkins会每天自动的执行测试、反馈结果，我们的应用也会变得越来越灵活、越来越稳定。但这些只能够在一定程度上提升应用的质量，它不能够发现类似设备兼容性等问题，因此我们需要通过多种手段、多重维度来保证应用的质量问题.

## 参考资料

* [《Android开发进阶 从小工到专家》单元测试章节](http://item.jd.com/11880368.html)
* [小创](http://chriszou.com/)
* [Mockito中文文档](https://github.com/hehonghui/mockito-doc-zh)