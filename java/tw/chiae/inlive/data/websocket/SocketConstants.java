package tw.chiae.inlive.data.websocket;
  
  /**
   * @author Muyangmin
   * @since 1.0.0
   */
  @SuppressWarnings("unused")
  public final class SocketConstants {

      public static final String FIELD_TYPE = "type";

      /**
       * @see tw.chiae.inlive.data.bean.websocket.WsPongRequest
       */
      public static final String DEVICE_ANDROID = "android";

      /**
       * 点亮❤
       */
      public static final String EVENT_LIGHT_HEART = "LightHeart";
  
      /**
       * 公聊消息
       */
      public static final String EVENT_PUB_MSG = "SendPubMsg";
      /**
       * 私聊消息
       */
      public static final String EVENT_PRV_MSG="SendPrvMsg";

      /**
       * 系统信息
       */
      public static final String STSTEM_MSG="sysmsg";

      /**
       * 系统欢迎信息
       */
      public static final String STSTEM_WELCOME="logins";

      /**
       * 登录到Socket
       */
      public static final String EVENT_LOGIN = "login";
  
      /**
       * 登出Socket
       */
      public static final String EVENT_LOGOUT = "logout";
  
      /**
       * 送礼
       */
      public static final String EVENT_SEND_GIFT = "sendGift";
  
      /**
       * 错误消息
       */
      public static final String EVENT_ERROR = "error";

      /**
       * 错误类型：被踢出房间。
       */
      public static final String ERROR_KICKED = "error.kicked";

      /**
       * 房管操作。
       */
      public static final String EVENT_MANAGE = "Manage";

  //    /**
  //     *
  //     */
  //    public static final String EVENT_RIGHT = "right";
      /**
       * 系统消息
       */
      public static final String EVENT_SYS_MSG = "sysmsg.alert";

      /**
       * 观众列表。
       */
      public static final String EVENT_ONLINE_CLIENT = "onLineClient";
  
      /**
       * 客户端上行心跳包，固定时间间隔重传。
       */
      public static final String EVENT_PONG = "pong";

      /**
       * 服务器下行心跳包，固定时间间隔重传。
       */
      public static final String EVENT_PING = "ping";

      public static final String EVENT_ADD_ADMIN = "right.adminer";

      public static final String EVENT_REMOVE_ADMIN = "right.removeAdminer";
  }
