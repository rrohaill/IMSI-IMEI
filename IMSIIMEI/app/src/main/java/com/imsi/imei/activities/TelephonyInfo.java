//package com.imsi.imei.activities;
//
//import android.content.Context;
//import android.telephony.TelephonyManager;
//
//import java.lang.reflect.Method;
//
///**
// * Created by rohail on 12/22/2016.
// */
//public final class TelephonyInfo {
//
//    private static TelephonyInfo telephonyInfo;
//    private String imsiSIM1;
//    private String imsiSIM2;
//    private boolean isSIM1Ready;
//    private boolean isSIM2Ready;
//
//    public String getImsiSIM1() {
//        return imsiSIM1;
//    }
//
//    /*public static void setImsiSIM1(String imsiSIM1) {
//        TelephonyInfo.imsiSIM1 = imsiSIM1;
//    }*/
//
//    public String getImsiSIM2() {
//        return imsiSIM2;
//    }
//
//    /*public static void setImsiSIM2(String imsiSIM2) {
//        TelephonyInfo.imsiSIM2 = imsiSIM2;
//    }*/
//
//    public boolean isSIM1Ready() {
//        return isSIM1Ready;
//    }
//
//    /*public static void setSIM1Ready(boolean isSIM1Ready) {
//        TelephonyInfo.isSIM1Ready = isSIM1Ready;
//    }*/
//
//    public boolean isSIM2Ready() {
//        return isSIM2Ready;
//    }
//
//    /*public static void setSIM2Ready(boolean isSIM2Ready) {
//        TelephonyInfo.isSIM2Ready = isSIM2Ready;
//    }*/
//
//    public boolean isDualSIM() {
//        return imsiSIM2 != null;
//    }
//
//    private TelephonyInfo() {
//    }
//
//    public static TelephonyInfo getInstance(Context context){
//
//        if(telephonyInfo == null) {
//
//            telephonyInfo = new TelephonyInfo();
//
//            TelephonyManager telephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
//
//            telephonyInfo.imsiSIM1 = telephonyManager.getDeviceId();;
//            telephonyInfo.imsiSIM2 = null;
//
//            try {
//                telephonyInfo.imsiSIM1 = getDeviceIdBySlot(context, "getDeviceIdGemini", 0);
//                telephonyInfo.imsiSIM2 = getDeviceIdBySlot(context, "getDeviceIdGemini", 1);
//            } catch (GeminiMethodNotFoundException e) {
//                e.printStackTrace();
//
//                try {
//                    telephonyInfo.imsiSIM1 = getDeviceIdBySlot(context, "getDeviceId", 0);
//                    telephonyInfo.imsiSIM2 = getDeviceIdBySlot(context, "getDeviceId", 1);
//                } catch (GeminiMethodNotFoundException e1) {
//                    //Call here for next manufacturer's predicted method name if you wish
//                    e1.printStackTrace();
//                }
//            }
//
//            telephonyInfo.isSIM1Ready = telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY;
//            telephonyInfo.isSIM2Ready = false;
//
//            try {
//                telephonyInfo.isSIM1Ready = getSIMStateBySlot(context, "getSimStateGemini", 0);
//                telephonyInfo.isSIM2Ready = getSIMStateBySlot(context, "getSimStateGemini", 1);
//            } catch (GeminiMethodNotFoundException e) {
//
//                e.printStackTrace();
//
//                try {
//                    telephonyInfo.isSIM1Ready = getSIMStateBySlot(context, "getSimState", 0);
//                    telephonyInfo.isSIM2Ready = getSIMStateBySlot(context, "getSimState", 1);
//                } catch (GeminiMethodNotFoundException e1) {
//                    //Call here for next manufacturer's predicted method name if you wish
//                    e1.printStackTrace();
//                }
//            }
//        }
//
//        return telephonyInfo;
//    }
//
//    private static String getDeviceIdBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {
//
//        String imsi = null;
//
//        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//
//        try{
//
//            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());
//
//            Class<?>[] parameter = new Class[1];
//            parameter[0] = int.class;
//            Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);
//
//            Object[] obParameter = new Object[1];
//            obParameter[0] = slotID;
//            Object ob_phone = getSimID.invoke(telephony, obParameter);
//
//            if(ob_phone != null){
//                imsi = ob_phone.toString();
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new GeminiMethodNotFoundException(predictedMethodName);
//        }
//
//        return imsi;
//    }
//
//    private static  boolean getSIMStateBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {
//
//        boolean isReady = false;
//
//        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//
//        try{
//
//            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());
//
//            Class<?>[] parameter = new Class[1];
//            parameter[0] = int.class;
//            Method getSimStateGemini = telephonyClass.getMethod(predictedMethodName, parameter);
//
//            Object[] obParameter = new Object[1];
//            obParameter[0] = slotID;
//            Object ob_phone = getSimStateGemini.invoke(telephony, obParameter);
//
//            if(ob_phone != null){
//                int simState = Integer.parseInt(ob_phone.toString());
//                if(simState == TelephonyManager.SIM_STATE_READY){
//                    isReady = true;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new GeminiMethodNotFoundException(predictedMethodName);
//        }
//
//        return isReady;
//    }
//
//
//    private static class GeminiMethodNotFoundException extends Exception {
//
//        private static final long serialVersionUID = -996812356902545308L;
//
//        public GeminiMethodNotFoundException(String info) {
//            super(info);
//        }
//    }
//}
