package com.grgbanking.ct;/** * 保存URL常量的类， */public class Constants {    /*本地测试 ip地址   *///    public static final String BASE_SERVER_URL = "http://192.168.1.104:8080/android_jkweb/";    /*    银燕项目 ip 地址     */    public static final String BASE_SERVER_URL = "http://222.174.168.91:8123/android_jkweb/";    //  public static final String BASE_SERVER_URL = "http://192.168.1.13:8080/android_jkweb/"; //宜昌宜通正式服务器    //  public static final String BASE_SERVER_URL = "http://222.174.168.91:8080/android_jkweb/";    //  public static final String BASE_SERVER_URL = "http://172.168.1.1:8080/android_jkweb/";    //  public  static final  String  BASE_SERVER_URL="http://183.63.190.43:6600/hbct"; //河北晨通正式服务器    //	public  static final  String  BASE_SERVER_URL="http://183.63.190.43:6601/ycyt"; //宜昌宜通正式服务器    //	public  static final  String  BASE_SERVER_URL="http://192.168.1.107:8087/android_jkweb/"; //宜昌宜通正式服务器    //  public  static final  String  BASE_SERVER_URL="http://10.2.18.32/hbct";  //本机测试    //  public  static final  String  BASE_SERVER_URL="http://192.168.2.102/hbct";  //本机测试    //  public  static final  String  BASE_SERVER_URL="http://192.168.72.102/hbct";  //本机测试    //	public static final String URL_GET_USER=BASE_SERVER_URL+"/pda-user!pdaLogin.do";    /**     * 人行端交接提交数据     */    public static final String URL_Upload_Associate = BASE_SERVER_URL + "/pei-xiangz!uploadAssociate.do";    /**     * 交接页面查询人员的名字和所属银行     */    public static final String URL_FIND_BANK_EMPLOYEE = BASE_SERVER_URL + "/pei-xiangz!boxAssociate.do";    /**     * 清分场地配箱页面提交数据     */    public static final String URL_DATA_COMMIT = BASE_SERVER_URL + "/pei-xiangz!clearPlace.do";    /**     * 配箱页面查询人员的名字     */    public static final String URL_FIT_BACK_EMPLOYEE = BASE_SERVER_URL + "/pei-xiangz!employeeName.do";    /**     * PDA登录地址     */    public static final String URL_PDA_LOGIN = BASE_SERVER_URL + "/mobile-message!pdaLogin.do";    /**     *     */    public static final String URL_FIND_YINYAN_BANK_EMPLOYEE = BASE_SERVER_URL + "/pei-xiangz!boxAssociateYinYan.do";    /*----------------------------------------------------------------------------------------------------*/    /**     * 获取网点出、入库信息地址     */    public static final String URL_NET_OUTIN = BASE_SERVER_URL + "/mobile-message!pdaDownload.do";    /**     * PDA交接提交地址     */    public static final String URL_NET_IN_COMMIT = BASE_SERVER_URL + "/mobile-message!uploadData.do";    public static final String URL_GET_TASK_LIST = BASE_SERVER_URL + "/mobile!tasklist.do";    public static final String URL_GET_BRANCH = BASE_SERVER_URL + "/mobile!branch.do";    public static final String URL_SAVE_GPS = BASE_SERVER_URL + "/mobile!savegps.do";    public static final String URL_SAVE_TASK = BASE_SERVER_URL + "/mobile!savetask.do";    public static final String URL_FILE_UPLOAD = BASE_SERVER_URL + "/mobile!uploadimg.do";    public static final String URL_UPDATE = BASE_SERVER_URL + "/update/ct1.2.apk";    public static final String URL_UPDATE_TEXT = BASE_SERVER_URL + "/update/version.txt";    /**     * 登录系统后网点出库接口访问参数     */    public static final String LOGIN_NET_OUT = "0";    /**     * 登录系统后网点入库接口访问参数     */    public static final String LOGIN_NET_IN = "1";    /**     *     * 网点任务状态	1:已完成 ； 0：未完成     */    public static final String NET_TASK_STATUS_FINISH = "1";    public static final String NET_TASK_STATUS_UNFINISH = "0";    /**     * 押运人员RFID编码前缀     *///	public static final String PRE_RFID_GUARD = "001aa";//	public static final String PRE_RFID_GUARD = "BBB2015";    public static final String PRE_RFID_GUARD = "BBB2016";    /**     * 网点人员RFID编码前缀     *///	public static final String PRE_RFID_BANKEMPLOYEE = "001bb";//	public static final String PRE_RFID_BANKEMPLOYEE = "AAA2007";    public static final String PRE_RFID_BANKEMPLOYEE = "AAA2008";    /**     * 网点交接类型	1:网点入库 ； 0：网点出库     */    public static final String NET_COMMIT_TYPE_IN = "1";    public static final String NET_COMMIT_TYPE_OUT = "0";    /**     * 网点交接状态	0：扫描正确 ;1：扫描错误     */    public static final String NET_COMMIT_STATUS_RIGHT = "0";    public static final String NET_COMMIT_STATUS_ERROR = "1";    void test(){    }}