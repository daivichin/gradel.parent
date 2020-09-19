package com.gradel.parent.common.util.threadlocal;

import com.gradel.parent.common.util.util.StringUtil;
import com.gradel.parent.common.util.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016/11/10
 * @Description:
 */
@Slf4j
public final class LocalHostUtil {

    private static String localHostAddress = "";
    private static String ipCode = "";

    /**
     * 获取Ip地址
     *
     * @return
     */
    public static String getIpCode() {
        if (ipCode != null && !ipCode.equals("")) {
            return ipCode;
        }

        synchronized (LocalHostUtil.class) {
            if (ipCode != null && !ipCode.equals("")) {
                return ipCode;
            }
            String ip = getLocalHostAddress();
            String[] ipArray = ip.split("\\.");
            for (int j = 0; j < 4; j++) {
                for (int i = ipArray[j].length(); i < 3; i++) {
                    ipCode = ipCode + "0";
                }
                ipCode = ipCode + ipArray[j];
            }
        }
        return ipCode;
    }

    /**
     * <p>
     * 取本机IP地址。
     * </p>
     *
     * @return 本机IP地址 -Djava.net.preferIPv4Stack=TRUE
     */
    public static String getLocalHostAddress() {
        if (!localHostAddress.equals("")) {
            return localHostAddress;
        }
        String ip = "";
        String ipBak = "";
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
            ip = inetAddress.getHostAddress();
        } catch (Throwable e1) {
            //e1.printStackTrace();
        }
        //log.info(ip + ":" + toStringFromInetAddress(inet));
        if (StringUtil.isNotBlank(ip) && !"127.0.0.1".equals(ip) && ip.indexOf(':') < 0) {
            localHostAddress = ip;
            //log.info("java.net.InetAddress.getLocalHost().getHostAddress()="+ ip);
            return ip;
        }
        Enumeration netInterfaces = null;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (Throwable e) {
            //e.printStackTrace();
            log.error("[{}] [{}] Finish handling .\nSome Exception Occur:[{}]", SerialNo.getSerialNo(), SerialNoUtil.class.getName(), ExceptionUtil.getAsString(e));
        }
        InetAddress iAddress = null;
        try {
            out:
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) netInterfaces
                        .nextElement();

                Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    iAddress = inetAddresses.nextElement();
                    if (!iAddress.isSiteLocalAddress()
                            && !iAddress.isLoopbackAddress()
                            && iAddress.getHostAddress().indexOf(':') == -1) {
                        ip = iAddress.getHostAddress();
                        //log.info(ip + ":" + toStringFromInetAddress(iAddress));
                        //try {
                        //log.info("iAddress.getHostAddress()=" + ip+ ",getLocalHost()=" + iAddress.getLocalHost());
                        //} catch (UnknownHostException e) {
                        //log.info("iAddress.getHostAddress()=" + ip+ ",getLocalHost()=" + e.toString());
                        //e.printStackTrace();
                        //}
                        break out;
                    } else {
                        ip = iAddress.getHostAddress();
                        //log.info(ip + ":" + toStringFromInetAddress(iAddress));
                        //try {
                        //log.info("NetworkInterface.getNetworkInterfaces().nextElement().getInetAddresses().nextElement().getHostAddress()="+ ip+ ",getLocalHost()="+ iAddress.getLocalHost());
                        //} catch (UnknownHostException e) {
                        //log.info("NetworkInterface.getNetworkInterfaces().nextElement().getInetAddresses().nextElement().getHostAddress()="+ ip + ",getLocalHost()=" + e.toString());
                        //e.printStackTrace();
                        //}
                        if (!ip.equals("127.0.0.1") && ip.split("\\.").length == 4
                                && ip.indexOf(':') < 0) {
                            ipBak = ip;
                        }
                        ip = "";
                        iAddress = null;
                    }
                }
            }
        } catch (Throwable e3) {
            //e3.printStackTrace();
            log.error("[{}] [{}] Finish handling .\nSome Exception Occur:[{}]", SerialNo.getSerialNo(), SerialNoUtil.class.getName(), ExceptionUtil.getAsString(e3));
        }
        if (!ip.equals("127.0.0.1") && ip.split("\\.").length == 4
                && ip.indexOf(':') < 0) {
            localHostAddress = ip;
            return ip;
        }
        try {
            Enumeration<?> e1 = (Enumeration<?>) NetworkInterface
                    .getNetworkInterfaces();
            while (e1.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) e1.nextElement();
                if (!ni.getName().equals("eth0")
                        && !ni.getName().equals("eth1")
                        && !ni.getName().equals("bond0")) {
                    //log.info("NetworkInterface.getNetworkInterfaces().nextElement().getName()="+ ni.getName());
                    continue;
                } else {
                    Enumeration<?> e2 = ni.getInetAddresses();
                    while (e2.hasMoreElements()) {
                        InetAddress ia = (InetAddress) e2.nextElement();
                        if (ia instanceof Inet6Address) {
                            continue;
                        }
                        ip = ia.getHostAddress();
                        //log.info(ip + ":" + toStringFromInetAddress(ia));
                        //try {
                        //log.info("NetworkInterface.getNetworkInterfaces():"+ ni.getName()+ ".getInetAddresses().nextElement().getHostAddress()="+ ip + ",getLocalHost()="+ ia.getLocalHost());
                        //} catch (UnknownHostException e) {
                        //log.info("NetworkInterface.getNetworkInterfaces():"+ ni.getName()+ ".getInetAddresses().nextElement().getHostAddress()="+ ip + ",getLocalHost()=" + e.toString());
                        //e.printStackTrace();
                        //}
                        if (!ia.isSiteLocalAddress() && !ip.equals("127.0.0.1")
                                && ip.split("\\.").length == 4
                                && ip.indexOf(':') < 0) {
                            localHostAddress = ip;
                            return ip;
                        }

                        if (ni.getName().equals("eth1")
                                && !ia.isSiteLocalAddress()
                                && !ip.equals("127.0.0.1")
                                && ip.split("\\.").length == 4
                                && ip.indexOf(':') < 0) {
                            ipBak = ip;
                            ip = "";
                        }

                    }
                    break;
                }
            }
        } catch (Throwable e) {
            //e.printStackTrace();
        }
        if (!ip.equals("127.0.0.1") && ip.split("\\.").length == 4
                && ip.indexOf(':') < 0) {
            localHostAddress = ip;
            return ip;
        }
        if (!ipBak.equals("127.0.0.1") && ipBak.split("\\.").length == 4
                && ipBak.indexOf(':') < 0) {
            localHostAddress = ipBak;
            return ipBak;
        }
        localHostAddress = ip;
        return ip;
    }
}
