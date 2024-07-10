package com.ciot.deliverywear.utils;

import com.ciot.deliverywear.bean.HeartBeatBeanR;
import com.ciot.deliverywear.bean.RegisterBeanR;
import com.ciot.deliverywear.bean.ResultBean;
import com.ciot.deliverywear.constant.NetConstant;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

public class GsonUtils {

    /**
     * 请求码(无论平台请求还是主控请求都是0x00)
     */
    private static final byte REQUEST_QA = 0x00;
    private static final String TAG = GsonUtils.class.getSimpleName();
    private static HashMap<String,Type> typeHashMap = new HashMap<>();
    private static Gson gson;

    /**
     * JsonType
     */
    private static Type sRegisterControlJsonType, sHeartBeatRJsonType, sResultJsonType;

    /**
     * 获得请求的data实体类型
     *
     * @param cmd 命令字
     * @return 对应的类型
     */
    public static Type getType(short cmd, byte qa) {
        switch (cmd) {
            case NetConstant.CONTROL_DEVICE_MANAGEMENT_REGISTER:
                if (qa == REQUEST_QA) {
                    if (sRegisterControlJsonType == null) {
                        sRegisterControlJsonType = new TypeToken<RegisterBeanR>() {
                        }.getType();
                    }
                    return sRegisterControlJsonType;
                } else {
                    if (sResultJsonType == null) {
                        sResultJsonType = new TypeToken<ResultBean>() {
                        }.getType();
                    }
                    return sResultJsonType;
                }
            default:
                if (qa == REQUEST_QA) {
                    if (sHeartBeatRJsonType == null) {
                        sHeartBeatRJsonType = new TypeToken<HeartBeatBeanR>() {
                        }.getType();
                    }
                    return sHeartBeatRJsonType;
                } else {
                    if (sResultJsonType == null) {
                        sResultJsonType = new TypeToken<ResultBean>() {
                        }.getType();
                    }
                    return sResultJsonType;
                }
        }
    }

    /**
     * 获得gson对象
     *
     * @return Gson对象
     */
    public static Gson getGson() {
        if (null == gson) {
            ////不导出实体中没有用@Expose注解的属性
            //约定协议里的时间格式 记得补零
            gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        }

        return gson;
    }

    /**
     * 用于生成返回的json对象
     *
     * @param actionNumber 协议命令码
     * @param isSuccess    请求是否成功
     * @return
     */
    public static Gson getGsonR(short actionNumber, boolean isSuccess) {
        Gson gson = new GsonBuilder()
                .setExclusionStrategies(ExclusionStrategyHelper.getExclusionStrategy(actionNumber, isSuccess))
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                //约定协议里的时间格式 记得补零
                .create();
        return gson;
    }

    static class ExclusionStrategyHelper {
        static ExclusionStrategy getExclusionStrategy(final short actionNumber, final boolean isSuccess) {
            ExclusionStrategy myExclusionStrategy = new ExclusionStrategy() {
                /**
                 * 是否跳过属性 不序列化
                 * 返回 false 代表 属性要进行序列化
                 */
                @Override
                public boolean shouldSkipField(FieldAttributes fa) {
                    return isInclude(fa, actionNumber, isSuccess);
                }

                /**
                 * 是否排除对应的类
                 *  同时这里也可以 排除 int 类型的属性 不进行序列化
                 *  若不排除任何 类  直接 返回false 即可
                 * @param clazz
                 * @return
                 */
                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }
            };
            return myExclusionStrategy;
        }

        /**
         * 根据协议命令码 和是否成功生成相应的返回对象
         *
         * @param actionNumber 协议命令码
         * @param isSuccess    是否请求成功
         */
        static boolean isInclude(FieldAttributes fa, short actionNumber, boolean isSuccess) {
            if (isSuccess) {
                //请求成功
                if (actionNumber == NetConstant.CONTROL_DEVICE_MANAGEMENT_REGISTER) {
                    return false;
                }
                return "reason".equals(fa.getName());
            } else {
                //请求失败
                if (actionNumber == NetConstant.CONTROL_DEVICE_MANAGEMENT_REGISTER) {
                    return false;
                }
                return !("reason".equals(fa.getName()) || "result".equals(fa.getName()));
            }
        }
    }

}
