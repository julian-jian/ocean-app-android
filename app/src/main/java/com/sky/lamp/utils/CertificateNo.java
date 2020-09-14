package com.sky.lamp.utils;

import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * 根据身份证获取性别、出生日期、年龄，支持15、18位身份证
 */
public class CertificateNo {

    public ResultDTO parseCertificateNo(String certificateNo) {

        ResultDTO resultDTO = new ResultDTO();
        String myRegExpIDCardNo = "^\\d{6}(((19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])\\d{3}([0-9]|x|X))|(\\d{2}(0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])\\d{3}))$";
        boolean valid = Pattern.matches(myRegExpIDCardNo, certificateNo) || (certificateNo.length() == 17 && Pattern.matches(myRegExpIDCardNo, certificateNo.substring(0, 15)));
        if (!valid) {
            resultDTO.setStatueMessage("证件号码不规范!");
            return resultDTO;
        }
        int idxSexStart = 16;
        int birthYearSpan = 4;
        //如果是15位的证件号码
        if (certificateNo.length() == 15) {
            idxSexStart = 14;
            birthYearSpan = 2;
        }

        //性别
        String idxSexStr = certificateNo.substring(idxSexStart, idxSexStart + 1);
        int idxSex = Integer.parseInt(idxSexStr) % 2;
        String sex = (idxSex == 1) ? "M" : "F";
        resultDTO.setSex(sex);

        //出生日期
        String year = (birthYearSpan == 2 ? "19" : "") + certificateNo.substring(6, 6 + birthYearSpan);
        String month = certificateNo.substring(6 + birthYearSpan, 6 + birthYearSpan + 2);
        String day = certificateNo.substring(8 + birthYearSpan, 8 + birthYearSpan + 2);
        String birthday = year + '-' + month + '-' + day;
        resultDTO.setBirthday(birthday);

        //年龄
        Calendar certificateCal = Calendar.getInstance();
        Calendar currentTimeCal = Calendar.getInstance();
        certificateCal.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
        int yearAge = (currentTimeCal.get(Calendar.YEAR)) - (certificateCal.get(Calendar.YEAR));
        certificateCal.set(currentTimeCal.get(Calendar.YEAR), Integer.parseInt(month) - 1, Integer.parseInt(day));
        int monthFloor = (currentTimeCal.before(certificateCal) ? 1 : 0);
        resultDTO.setAge(yearAge - monthFloor);

        return resultDTO;
    }
}